/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("UNCHECKED_CAST")

package com.gridnine.jasmine.web.core.serialization

import com.gridnine.jasmine.common.core.meta.*
import com.gridnine.jasmine.common.core.model.BaseIdentityJS
import com.gridnine.jasmine.common.core.model.XeptionJS
import com.gridnine.jasmine.web.core.common.EnvironmentJS
import com.gridnine.jasmine.web.core.reflection.ReflectionFactoryJS
import com.gridnine.jasmine.web.core.remote.WebPluginsHandler
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import kotlin.js.Date

@Suppress("unused")
class JsonSerializerJS {

    suspend fun <T : Any> serializeToString(obj: T): String {
        return JSON.stringify(serialize(obj, false, hashSetOf()))
    }

    suspend fun <T : Any> deserialize(quilifiedClassName: String, jsonObj: dynamic): T {
        return deserialize(quilifiedClassName, jsonObj, hashMapOf())
    }


    private val providersCache = hashMapOf<String, ObjectMetadataProviderJS<*>>()

    private val dateFormatter = { date: Date? ->
        date?.let { "${it.getFullYear()}-${MiscUtilsJS.fillWithZeros(it.getMonth() + 1)}-${MiscUtilsJS.fillWithZeros(it.getDate())}" }
    }
    private val dateParser = lambda@{ value: String? ->
        if (value.isNullOrBlank()) {
            return@lambda null
        }
        val components = value.split("-")
        Date(components[0].toInt(), components[1].toInt() - 1, components[2].toInt())
    }

    private val dateTimeFormatter = { date: Date? ->
        date?.let { "${it.getFullYear()}-${MiscUtilsJS.fillWithZeros(it.getMonth() + 1)}-${MiscUtilsJS.fillWithZeros(it.getDate())}T${MiscUtilsJS.fillWithZeros(it.getHours())}-${MiscUtilsJS.fillWithZeros(it.getMinutes())}-${MiscUtilsJS.fillWithZeros(it.getSeconds())}-${MiscUtilsJS.fillWithZeros(it.getMilliseconds(), 3)}" }
    }

    private val dateTimeParser = lambda@{ value: String? ->
        if (value.isNullOrBlank()) {
            return@lambda null
        }
        val parts = value.split("T")
        val comps1 = parts[0].split("-")
        val comps2 = parts[1].split("-")
        Date(year = comps1[0].toInt(), month = comps1[1].toInt() - 1, day = comps1[2].toInt(), hour = comps2[0].toInt(), minute = comps2[1].toInt(), second = comps2[2].toInt(), millisecond = comps2[3].toInt())
    }


    private suspend fun <T : Any> serialize(obj: T, isAbstract: Boolean, uids: MutableSet<String>): dynamic {
        val quilifiedClassName = ReflectionFactoryJS.get().getQualifiedClassName(obj::class)
        val provider = getProvider(quilifiedClassName) as ObjectMetadataProviderJS<T>
        val result = js("{}")
        if (provider.hasUid()) {
            val uid = provider.getPropertyValue(obj, BaseIdentityJS.uid) as String?
            if (MiscUtilsJS.isNotBlank(uid )) {
                if (uids.contains(uid)) {
                    result[BaseIdentityJS.uid] = uid
                    return result
                }
                uids.add(uid!!)
            }
        }
        if (isAbstract) {
            result["_className"] = clearClassName(quilifiedClassName)
        }

        provider.getAllProperties().forEach { prop ->
            val value = provider.getPropertyValue(obj, prop.id)
            if (value != null) {
                result[prop.id] = toJsonValue(prop.type, prop.isAbstract, value, uids)
            }
        }
        provider.getAllCollections().forEach { coll ->
            val colls = provider.getCollection(obj, coll.id)
            if (colls.isNotEmpty()) {
                val array = arrayOfNulls<Any>(colls.size)
                result[coll.id] = array
                colls.withIndex().forEach { (idx, elm) ->
                    array[idx] = toJsonValue(coll.elementType, coll.isAbstract, elm, uids)
                }
            }
        }
        provider.getAllMaps().forEach { mapDescription ->
            val map = provider.getMap(obj, mapDescription.id)
            if (map.isNotEmpty()) {
                val array = arrayOfNulls<Any>(map.size)
                map.entries.withIndex().forEach { (idx, value) ->
                    val item = js("{}")
                    if(value.key != null){
                        item["key"] = toJsonValue(mapDescription.keyType, mapDescription.isKeyAbstract, value.key!!, uids )
                    }
                    if(value.value != null){
                        item["value"] = toJsonValue(mapDescription.valueType, mapDescription.isValueAbstract, value.value!!, uids )
                    }
                    array[idx] = item
                }
                result[mapDescription.id] = array
            }
        }
        return result
    }

    private suspend fun toJsonValue(type: SerializablePropertyTypeJS, abstract: Boolean, value: Any, uids: MutableSet<String>) : dynamic {
        return when (type) {
            SerializablePropertyTypeJS.STRING ->  value as String
            SerializablePropertyTypeJS.ENUM ->  (value as Enum<*>).name
            SerializablePropertyTypeJS.ENTITY -> serialize(value, abstract, uids)
            SerializablePropertyTypeJS.BIG_DECIMAL, SerializablePropertyTypeJS.INT, SerializablePropertyTypeJS.LONG -> value as Number
            SerializablePropertyTypeJS.BOOLEAN ->  value as Boolean
            SerializablePropertyTypeJS.BYTE_ARRAY ->  value as String
            SerializablePropertyTypeJS.LOCAL_DATE_TIME ->  dateTimeFormatter(value as Date)
            SerializablePropertyTypeJS.LOCAL_DATE ->  dateFormatter(value as Date)
            SerializablePropertyTypeJS.CLASS ->  clearClassName(value as String)
        }
    }


    private fun clearClassName(className: String): String {
        if (className.endsWith("JS")) {
            return className.substringBeforeLast("JS")
        }
        return className
    }
    internal suspend fun getProvider(qualifiedName: String): ObjectMetadataProviderJS<*> {
        val provider = getProviderInternal(qualifiedName)
        if(provider == null){
            WebPluginsHandler.get().loadPluginForId(qualifiedName)
        }
        return getProviderInternal(qualifiedName)?:throw XeptionJS.forDeveloper("no provider registered for class $qualifiedName")
    }

    private fun getProviderInternal(qualifiedName: String): ObjectMetadataProviderJS<*>? {
        if (providersCache.containsKey(qualifiedName)) {
            return providersCache[qualifiedName]!!
        }
        lateinit var provider:ObjectMetadataProviderJS<*>
        val ced = CustomMetaRegistryJS.get().entities[qualifiedName]
        if(ced != null){
            provider = CustomEntityMetadataProviderJS(ced)
            providersCache[qualifiedName] = provider
            return provider
        }
        val did = DomainMetaRegistryJS.get().indexes[qualifiedName]
        if(did != null){
            provider = DomainIndexMetadataProvider(did)
            providersCache[qualifiedName] = provider
            return provider
        }
        val dad = DomainMetaRegistryJS.get().assets[qualifiedName]
        if(dad != null){
            provider = DomainAssetMetadataProvider(dad)
            providersCache[qualifiedName] = provider
            return provider
        }
        val ddd = DomainMetaRegistryJS.get().documents[qualifiedName]
        if(ddd != null){
            provider = DomainDocumentMetadataProvider(ddd)
            providersCache[qualifiedName] = provider
            return provider
        }
        val red = RestMetaRegistryJS.get().entities[qualifiedName]
        if(red != null){
            provider = RestEntityMetadataProviderJS(red)
            providersCache[qualifiedName] = provider
            return provider
        }
        val vmd = UiMetaRegistryJS.get().viewModels[qualifiedName]
        if(vmd != null){
            provider = VMEntityMetadataProviderJS(vmd)
            providersCache[qualifiedName] = provider
            return provider
        }
        val vsd = UiMetaRegistryJS.get().viewSettings[qualifiedName]
        if(vsd != null){
            provider = VSEntityMetadataProviderJS(vsd)
            providersCache[qualifiedName] = provider
            return provider
        }
        val vvd = UiMetaRegistryJS.get().viewValidations[qualifiedName]
        if(vvd != null){
            provider = VVEntityMetadataProviderJS(vvd)
            providersCache[qualifiedName] = provider
            return provider
        }
        val med = MiscMetaRegistryJS.get().entities[qualifiedName]
        if(med != null){
            provider = MiscEntityMetadataProviderJS(med)
            providersCache[qualifiedName] = provider
            return provider
        }
        return null
    }


    private suspend fun <T : Any> deserialize(quilifiedClassName: String, jsonObj: dynamic, context: MutableMap<String, Any>): T {
        lateinit var result: T
        lateinit var provider: ObjectMetadataProviderJS<T>
        val realClassName = jsonObj["_className"]
        if (realClassName != null) {
            provider = getProvider("${realClassName}JS") as ObjectMetadataProviderJS<T>
            result = ReflectionFactoryJS.get().getFactory("${realClassName}JS").invoke() as T
        } else {
            provider = getProvider(quilifiedClassName) as ObjectMetadataProviderJS<T>
            val instance = provider.createInstance()
            result = instance ?: ReflectionFactoryJS.get().getFactory(quilifiedClassName).invoke() as T
        }

        if (provider.hasUid()) {
            val uid = jsonObj[BaseIdentityJS.uid] as String?
            if (MiscUtilsJS.isNotBlank(uid)) {
                val existing = context[uid]
                if (existing != null) {
                    return existing as T
                }
                context[uid!!] = result
            }
        }
        provider.getAllProperties().forEach { prop ->
            val propValue = jsonObj[prop.id]
            if (propValue != null) {
                val value = fromJsonValue(prop.type, prop.className, propValue, context)
                provider.setPropertyValue(result, prop.id, value)
            }
        }
        provider.getAllCollections().forEach { coll ->
            if (jsonObj[coll.id] != null) {
                val array = jsonObj[coll.id]
                for(elm in array){
                    val value = fromJsonValue(coll.elementType, coll.elementClassName, elm, context)
                    provider.getCollection(result, coll.id).add(value)
                }
            }
        }
        provider.getAllMaps().forEach { mapDescription ->
            if (jsonObj[mapDescription.id] != null) {
                val array = jsonObj[mapDescription.id]
                val map = provider.getMap(result, mapDescription.id)
                for(elm in array){
                    val keyValue = (elm["key"] as Any?)?.let { fromJsonValue(mapDescription.keyType, mapDescription.keyClassName, it, context)}
                    val valueValue = (elm["value"] as Any?)?.let { fromJsonValue(mapDescription.valueType, mapDescription.valueCassName, it, context)}
                    map[keyValue] = valueValue
                }
            }
        }
        return result
    }

    private suspend fun fromJsonValue(type: SerializablePropertyTypeJS, className: String?, propValue:dynamic, context: MutableMap<String, Any>): dynamic {
        return when (type) {
            SerializablePropertyTypeJS.STRING -> propValue as String
            SerializablePropertyTypeJS.CLASS -> propValue as String
            SerializablePropertyTypeJS.ENUM -> ReflectionFactoryJS.get().getEnum(className!!, propValue as String)
            SerializablePropertyTypeJS.ENTITY -> deserialize(className!!, propValue, context)
            SerializablePropertyTypeJS.BIG_DECIMAL -> (propValue as Number).toDouble()
            SerializablePropertyTypeJS.INT -> (propValue as Number).toInt()
            SerializablePropertyTypeJS.LONG -> (propValue as Number).toLong()
            SerializablePropertyTypeJS.BOOLEAN -> propValue as Boolean
            SerializablePropertyTypeJS.BYTE_ARRAY -> propValue
            SerializablePropertyTypeJS.LOCAL_DATE_TIME -> dateTimeParser(propValue as String)
            SerializablePropertyTypeJS.LOCAL_DATE -> dateParser(propValue as String)
        }
    }

    companion object {
        fun get() = EnvironmentJS.getPublished(JsonSerializerJS::class)
    }
}