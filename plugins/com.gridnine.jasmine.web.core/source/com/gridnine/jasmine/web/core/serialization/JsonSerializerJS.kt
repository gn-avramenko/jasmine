/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("UNCHECKED_CAST")

package com.gridnine.jasmine.web.core.serialization

import com.gridnine.jasmine.server.core.model.common.BaseIdentityJS
import com.gridnine.jasmine.server.core.model.common.XeptionJS
import com.gridnine.jasmine.server.core.model.custom.CustomMetaRegistryJS
import com.gridnine.jasmine.server.core.model.domain.DomainMetaRegistryJS
import com.gridnine.jasmine.server.core.model.rest.RestMetaRegistryJS
import com.gridnine.jasmine.web.core.application.EnvironmentJS
import com.gridnine.jasmine.web.core.reflection.ReflectionFactoryJS
import com.gridnine.jasmine.web.core.ui.debugger
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import kotlin.js.Date

class JsonSerializerJS {

    fun <T : Any> serializeToString(obj: T): String {
        return JSON.stringify(serialize(obj, false, hashSetOf()))
    }

    fun <T : Any> deserialize(quilifiedClassName: String, jsonObj: dynamic): T {
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


    private fun <T : Any> serialize(obj: T, isAbstract: Boolean, uids: MutableSet<String>): dynamic {
        val quilifiedClassName = ReflectionFactoryJS.get().getQualifiedClassName(obj::class)
        val provider = getProvider(quilifiedClassName) as ObjectMetadataProviderJS<T>
        val result = js("{}")
        if (provider.hasUid()) {
            val uid = provider.getPropertyValue(obj, BaseIdentityJS.uid) as String?
            if (uid != null) {
                if (uids.contains(uid)) {
                    result[BaseIdentityJS.uid] = uid
                    return result
                }
                uids.add(uid)
            }
        }
        if (isAbstract) {
            result["_className"] = clearClassName(quilifiedClassName)
        }

        provider.getAllProperties().forEach { prop ->
            val value = provider.getPropertyValue(obj, prop.id)
            if (value != null) {
                when (prop.type) {
                    SerializablePropertyTypeJS.STRING -> result[prop.id] = value as String
                    SerializablePropertyTypeJS.ENUM -> result[prop.id] = (value as Enum<*>).name
                    SerializablePropertyTypeJS.ENTITY -> {
                        val ett = serialize(value, prop.isAbstract, uids)
                        result[prop.id] = ett
                    }
                    SerializablePropertyTypeJS.BIG_DECIMAL, SerializablePropertyTypeJS.INT, SerializablePropertyTypeJS.LONG -> result[prop.id] = value as Number
                    SerializablePropertyTypeJS.BOOLEAN -> result[prop.id] = value as Boolean
                    SerializablePropertyTypeJS.BYTE_ARRAY -> result[prop.id] = value as String
                    SerializablePropertyTypeJS.LOCAL_DATE_TIME -> result[prop.id] = dateTimeFormatter(value as Date)
                    SerializablePropertyTypeJS.LOCAL_DATE -> result[prop.id] = dateFormatter(value as Date)
                    SerializablePropertyTypeJS.CLASS -> result[prop.id] = value as String
                }
            }
        }
        provider.getAllCollections().forEach { coll ->
            val colls = provider.getCollection(obj, coll.id)
            if (colls.isNotEmpty()) {
                val array = arrayOfNulls<Any>(colls.size)
                result[coll.id] = array
                colls.withIndex().forEach { (idx, elm) ->
                    when (coll.elementType) {
                        SerializablePropertyTypeJS.STRING -> array[idx] = elm as String
                        SerializablePropertyTypeJS.CLASS -> array[idx] = elm as String
                        SerializablePropertyTypeJS.ENUM -> array[idx] = (elm as Enum<*>).name
                        SerializablePropertyTypeJS.ENTITY -> {
                            val ett = serialize(elm, coll.isAbstract, uids)
                            array[idx] = ett
                        }
                        SerializablePropertyTypeJS.BIG_DECIMAL, SerializablePropertyTypeJS.INT, SerializablePropertyTypeJS.LONG ->
                            array[idx] = elm as Number
                        SerializablePropertyTypeJS.BOOLEAN -> array[idx] = elm as Boolean
                        SerializablePropertyTypeJS.BYTE_ARRAY -> array[idx] = elm as String
                        SerializablePropertyTypeJS.LOCAL_DATE_TIME -> array[idx] = dateTimeFormatter(elm as Date)
                        SerializablePropertyTypeJS.LOCAL_DATE -> array[idx] = dateFormatter(elm as Date)
                    }

                }
            }
        }
        return result
    }


    private fun clearClassName(className: String): String {
        if (className.endsWith("JS")) {
            return className.substringBeforeLast("JS")
        }
        return className
    }

    private fun getProvider(qualifiedName: String): ObjectMetadataProviderJS<*> {
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
        val ddd = DomainMetaRegistryJS.get().documents[qualifiedName]
        if(ddd != null){
            provider = DomainDocumentMetadataProvider(ddd)
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
        val red = RestMetaRegistryJS.get().entities[qualifiedName]
        if(red != null){
            provider = RestEntityMetadataProviderJS(red)
            providersCache[qualifiedName] = provider
            return provider
        }
        throw XeptionJS.forDeveloper("no provider registered for class $qualifiedName")
    }


    private fun <T : Any> deserialize(quilifiedClassName: String, jsonObj: dynamic, context: MutableMap<String, Any>): T {
        lateinit var result: T
        lateinit var provider: ObjectMetadataProviderJS<T>
        val realClassName = jsonObj["_className"]
        if (realClassName != null) {
            result = ReflectionFactoryJS.get().getFactory("${realClassName}JS").invoke() as T
            provider = getProvider("${realClassName}JS") as ObjectMetadataProviderJS<T>
        } else {
            provider = getProvider(quilifiedClassName) as ObjectMetadataProviderJS<T>
            val instance = provider.createInstance()
            result = instance ?: ReflectionFactoryJS.get().getFactory(quilifiedClassName).invoke() as T
        }

        if (provider.hasUid()) {
            val uid = jsonObj[BaseIdentityJS.uid] as String?
            if (uid != null) {
                val existing = context[uid]
                if (existing != null) {
                    return existing as T
                }
                context[uid] = result
            }
        }
        provider.getAllProperties().forEach { prop ->
            val propValue = jsonObj[prop.id]
            if (propValue != null) {
                val value =
                        when (prop.type) {
                            SerializablePropertyTypeJS.STRING -> propValue as String
                            SerializablePropertyTypeJS.CLASS -> propValue as String
                            SerializablePropertyTypeJS.ENUM -> ReflectionFactoryJS.get().getEnum(prop.className!!, propValue as String)
                            SerializablePropertyTypeJS.ENTITY -> deserialize(prop.className!!, propValue, context)
                            SerializablePropertyTypeJS.BIG_DECIMAL -> (propValue as Number).toDouble()
                            SerializablePropertyTypeJS.INT -> (propValue as Number).toInt()
                            SerializablePropertyTypeJS.LONG -> (propValue as Number).toLong()
                            SerializablePropertyTypeJS.BOOLEAN -> propValue as Boolean
                            SerializablePropertyTypeJS.BYTE_ARRAY -> propValue
                            SerializablePropertyTypeJS.LOCAL_DATE_TIME -> dateTimeParser(propValue as String)
                            SerializablePropertyTypeJS.LOCAL_DATE -> dateParser(propValue as String)
                        }
                provider.setPropertyValue(result, prop.id, value)
            }
        }
        provider.getAllCollections().forEach { coll ->
            if (jsonObj[coll.id] != null) {
                val array = jsonObj[coll.id]
                array.forEach { elm ->
                    val value = when (coll.elementType) {
                        SerializablePropertyTypeJS.STRING -> elm as String
                        SerializablePropertyTypeJS.CLASS -> elm as String
                        SerializablePropertyTypeJS.ENUM -> ReflectionFactoryJS.get().getEnum(coll.elementClassName!!, elm)
                        SerializablePropertyTypeJS.ENTITY -> deserialize(coll.elementClassName!!, elm, context)
                        SerializablePropertyTypeJS.BIG_DECIMAL -> elm
                        SerializablePropertyTypeJS.INT -> elm
                        SerializablePropertyTypeJS.LONG -> elm
                        SerializablePropertyTypeJS.BOOLEAN -> elm
                        SerializablePropertyTypeJS.BYTE_ARRAY -> elm
                        SerializablePropertyTypeJS.LOCAL_DATE_TIME -> dateTimeParser(elm as String)
                        SerializablePropertyTypeJS.LOCAL_DATE -> dateParser(elm as String)
                    }
                    if (value != null) {
                        provider.getCollection(result, coll.id).add(value)
                    }
                }
            }
        }
        return result
    }

    companion object {
        fun get() = EnvironmentJS.getPublished(JsonSerializerJS::class)
    }
}