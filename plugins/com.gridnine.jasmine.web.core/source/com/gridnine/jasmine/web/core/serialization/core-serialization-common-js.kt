/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UnsafeCastFromDynamic", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.core.serialization

import com.gridnine.jasmine.web.core.model.common.BaseEntityJS
import com.gridnine.jasmine.web.core.utils.TextUtilsJS
import com.gridnine.jasmine.web.core.utils.ReflectionFactoryJS
import kotlin.js.Date


internal abstract class ObjectMetadataProviderJS<T:Any> {
    val properties = arrayListOf<SerializablePropertyDescriptionJS>()
    val collections = arrayListOf<SerializableCollectionDescriptionJS>()
    abstract fun getPropertyValue(obj:T, id: String): Any?
    abstract fun getCollection(obj:T, id: String): MutableCollection<Any>
    abstract fun setPropertyValue(obj:T, id: String, value: Any?)
    abstract fun hasUid():Boolean
    open fun createInstance():T?{
        return null
    }
}

internal interface ProviderFactoryJS {
    fun create(className: String): ObjectMetadataProviderJS<out Any>
}

internal enum class SerializablePropertyTypeJS {
    STRING,
    ENUM,
    ENTITY,
    LONG,
    INT,
    BIG_DECIMAL,
    LOCAL_DATE_TIME,
    LOCAL_DATE,
    BOOLEAN,
    BYTE_ARRAY
}

internal class SerializablePropertyDescriptionJS(val id: String, val type: SerializablePropertyTypeJS, val className: String?, val isAbstract:Boolean)

internal class SerializableCollectionDescriptionJS(val id: String, val elementType: SerializablePropertyTypeJS, val elementClassName: String?, val isAbstract: Boolean)



internal object CommonSerializationUtilsJS {
    private val providersCache = hashMapOf<String, ObjectMetadataProviderJS<*>>()

    private val dateFormatter = { date: Date? ->
        date?.let { "${it.getFullYear()}-${TextUtilsJS.fillWithZeros(it.getMonth()+1)}-${TextUtilsJS.fillWithZeros(it.getDate())}" }
    }
    private val dateParser = lambda@{ value: String? ->
        if (value.isNullOrBlank()) {
            return@lambda null
        }
        val components = value.split("-")
        Date(components[0].toInt(), components[1].toInt() - 1, components[2].toInt())
    }

    private val dateTimeFormatter = { date: Date? ->
        date?.let { "${it.getFullYear()}-${TextUtilsJS.fillWithZeros(it.getMonth()+1)}-${TextUtilsJS.fillWithZeros(it.getDate())}_${TextUtilsJS.fillWithZeros(it.getHours())}-${TextUtilsJS.fillWithZeros(it.getMinutes())}-${TextUtilsJS.fillWithZeros(it.getSeconds())}-${TextUtilsJS.fillWithZeros(it.getMilliseconds(), 3)}" }
    }

    private val dateTimeParser = lambda@{ value: String? ->
        if (value.isNullOrBlank()) {
            return@lambda null
        }
        val parts = value.split("_")
        val comps1 = parts[0].split("-")
        val comps2 = parts[1].split("-")
        Date(year = comps1[0].toInt(), month = comps1[1].toInt() - 1, day = comps1[2].toInt(), hour = comps2[0].toInt(), minute = comps2[1].toInt(), second = comps2[2].toInt(), millisecond = comps2[3].toInt())
    }



    fun <T : Any> serialize(obj: T, factory: ProviderFactoryJS): String {
        return JSON.stringify(serialize(obj, factory, false, hashSetOf()))
    }

    private fun <T : Any> serialize(obj: T, factory: ProviderFactoryJS, isAbstract: Boolean, uids: MutableSet<String>): dynamic {
        val quilifiedClassName = ReflectionFactoryJS.get().getQualifiedClassName(obj::class)
        val provider = getProvider(quilifiedClassName, factory) as ObjectMetadataProviderJS<T>
        val result = js("{}")
        if (provider.hasUid()) {
            val uid = provider.getPropertyValue(obj, BaseEntityJS.uid) as String?
            if (uid != null) {
                if (uids.contains(uid)) {
                    result[BaseEntityJS.uid] = uid
                    return result
                }
                uids.add(uid)
            }
        }
        if (isAbstract) {
            result["_className"] = clearClassName(quilifiedClassName)
        }

        provider.properties.forEach { prop ->
            val value = provider.getPropertyValue(obj, prop.id)
            if (value != null) {
                when (prop.type) {
                    SerializablePropertyTypeJS.STRING -> result[prop.id] =  value as String
                    SerializablePropertyTypeJS.ENUM -> result[prop.id] = (value as Enum<*>).name
                    SerializablePropertyTypeJS.ENTITY -> {
                        val ett = serialize(value,  factory, prop.isAbstract, uids)
                        result[prop.id] = ett
                    }
                    SerializablePropertyTypeJS.BIG_DECIMAL,SerializablePropertyTypeJS.INT, SerializablePropertyTypeJS.LONG -> result[prop.id] = value as Number
                    SerializablePropertyTypeJS.BOOLEAN -> result[prop.id] = value as Boolean
                    SerializablePropertyTypeJS.BYTE_ARRAY -> result[prop.id] = value as String
                    SerializablePropertyTypeJS.LOCAL_DATE_TIME -> result[prop.id] = dateTimeFormatter(value as Date)
                    SerializablePropertyTypeJS.LOCAL_DATE -> result[prop.id] = dateFormatter(value as Date)
                }
            }
        }
        provider.collections.forEach { coll ->
            val colls = provider.getCollection(obj, coll.id)
            if (colls.isNotEmpty()) {
                val array = arrayOfNulls<Any>(colls.size)
                result[coll.id] = array
                colls.withIndex().forEach { (idx, elm) ->
                        when (coll.elementType) {
                            SerializablePropertyTypeJS.STRING -> array[idx] = elm as String
                            SerializablePropertyTypeJS.ENUM ->  array[idx] = (elm as Enum<*>).name
                            SerializablePropertyTypeJS.ENTITY -> {
                                val ett = serialize(elm,  factory, coll.isAbstract, uids)
                                array[idx]= ett
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
        if(className.endsWith("JS")){
            return className.substring(0, className.lastIndexOf("JS"))
        }
        return className
    }

    internal fun getProvider(qualifiedName: String, factory: ProviderFactoryJS): ObjectMetadataProviderJS<*> {
        if (providersCache.containsKey(qualifiedName)) {
            return providersCache[qualifiedName]!!
        }

        val provider = factory.create(qualifiedName)
        providersCache[qualifiedName] = provider
        return provider
    }


    fun <T : Any> deserialize(quilifiedClassName: String, jsonObj: dynamic, factory: ProviderFactoryJS): T {
        return deserialize(quilifiedClassName, jsonObj, factory, hashMapOf())
    }

    private fun <T : Any> deserialize(quilifiedClassName: String, jsonObj: dynamic, factory: ProviderFactoryJS, context: MutableMap<String, Any>): T {
        lateinit var result: T
        lateinit var provider :ObjectMetadataProviderJS<T>
        val realClassName = jsonObj["_className"]
        if (realClassName != null) {
            result = ReflectionFactoryJS.get().getFactory("${realClassName}JS").invoke() as T
            provider = getProvider("${realClassName}JS", factory) as ObjectMetadataProviderJS<T>
        } else {
            provider  = getProvider(quilifiedClassName, factory) as ObjectMetadataProviderJS<T>
            val instance = provider.createInstance()
            result = instance?:ReflectionFactoryJS.get().getFactory(quilifiedClassName).invoke() as T
        }

        if (provider.hasUid()) {
            val uid = jsonObj[BaseEntityJS.uid] as String?
            if(uid != null) {
                val existing = context[uid]
                if (existing != null) {
                    return existing as T
                }
                context[uid] = result
            }
        }
        provider.properties.forEach { prop ->
            val propValue = jsonObj[prop.id]
            if (propValue != null) {
                val value =
                        when (prop.type) {
                            SerializablePropertyTypeJS.STRING -> propValue  as String
                            SerializablePropertyTypeJS.ENUM -> ReflectionFactoryJS.get().getEnum(prop.className!!, propValue as String)
                            SerializablePropertyTypeJS.ENTITY -> deserialize(prop.className!!, propValue, factory, context)
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
        provider.collections.forEach { coll ->
            if (jsonObj[coll.id] != null) {
                val array = jsonObj[coll.id]
                array.forEach { elm ->
                    val value = when (coll.elementType) {
                        SerializablePropertyTypeJS.STRING -> elm as String
                        SerializablePropertyTypeJS.ENUM -> ReflectionFactoryJS.get().getEnum(coll.elementClassName!!, elm)
                        SerializablePropertyTypeJS.ENTITY -> deserialize(coll.elementClassName!!, elm, factory, context)
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
}