/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")

package com.gridnine.jasmine.server.core.serialization

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.internal.Streams
import com.google.gson.stream.JsonWriter
import com.gridnine.jasmine.server.core.utils.ReflectionUtils
import java.io.*
import java.lang.IllegalStateException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass


internal abstract class ObjectMetadataProvider<T:Any> {
    val properties = arrayListOf<SerializablePropertyDescription>()
    val collections = arrayListOf<SerializableCollectionDescription>()
    abstract fun getPropertyValue(obj:T, id: String): Any?
    abstract fun getCollection(obj:T, id: String): MutableCollection<Any>
    abstract fun setPropertyValue(obj:T, id: String, value: Any?)
    abstract fun hasUid():Boolean
}

internal interface ProviderFactory {
    fun create(className: String): ObjectMetadataProvider<out Any>
}

internal enum class SerializablePropertyType {
    STRING,
    ENUM,
    ENTITY,
    LONG,
    INT,
    BIG_DECIMAL,
    LOCAL_DATE_TIME,
    LOCAL_DATE,
    BOOLEAN,
    CLASS,
    BYTE_ARRAY
}

internal class SerializablePropertyDescription(val id: String, val type: SerializablePropertyType, val className: String?, val isAbstract:Boolean)



internal class SerializableCollectionDescription(val id: String, val elementType: SerializablePropertyType, val elementClassName: String?, val isAbstract: Boolean)


internal object SerializationUtils {
    private val providersCache = ConcurrentHashMap<String, ObjectMetadataProvider<*>>()

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss-SSS")

    fun <T : Any> serialize(obj: T, factory: ProviderFactory, isAbstract: Boolean): JsonObject {
        return serialize(obj, factory, isAbstract, hashSetOf())
    }

    private fun <T : Any> serialize(obj: T, factory: ProviderFactory, isAbstract: Boolean, uids: MutableSet<String>): JsonObject {
        val key = obj::class.java.name
        val provider = getProvider<T>(obj::class.java.name, factory)
        val result = JsonObject()
        if (provider.hasUid()) {
            val uid = provider.getPropertyValue(obj, "uid") as String?
            if (uid != null) {
                if (uids.contains(uid)) {
                    result.addProperty("uid", uid)
                    return result
                }
                uids.add(uid)
            }
        }
        if (isAbstract) {
            result.addProperty("_className", key)
        }

        provider.properties.forEach { prop ->
            val value = provider.getPropertyValue(obj, prop.id)
            if (value != null) {
                when (prop.type) {
                    SerializablePropertyType.STRING -> result.addProperty(prop.id, value as String)
                    SerializablePropertyType.CLASS -> result.addProperty(prop.id, (value as KClass<*>).qualifiedName)
                    SerializablePropertyType.ENUM -> result.addProperty(prop.id, (value as Enum<*>).name)
                    SerializablePropertyType.ENTITY -> result.add(prop.id, serialize(value, factory, prop.isAbstract))
                    SerializablePropertyType.BIG_DECIMAL, SerializablePropertyType.INT, SerializablePropertyType.LONG -> result.addProperty(prop.id, value as Number)
                    SerializablePropertyType.BOOLEAN -> result.addProperty(prop.id, value as Boolean)
                    SerializablePropertyType.BYTE_ARRAY -> result.addProperty(prop.id, Base64.getEncoder().encodeToString(value as ByteArray))
                    SerializablePropertyType.LOCAL_DATE_TIME -> result.addProperty(prop.id, (value as LocalDateTime).format(dateTimeFormatter))
                    SerializablePropertyType.LOCAL_DATE -> result.addProperty(prop.id, (value as LocalDate).format(dateFormatter))
                }
            }
        }
        provider.collections.forEach { coll ->
            val colls = provider.getCollection(obj, coll.id)
            if (colls.isNotEmpty()) {
                val array = JsonArray()
                result.add(coll.id, array)
                colls.forEach { elm ->
                        when (coll.elementType) {
                            SerializablePropertyType.STRING -> array.add(elm as String)
                            SerializablePropertyType.ENUM -> array.add((elm as Enum<*>).name)
                            SerializablePropertyType.ENTITY -> array.add(serialize(elm, factory, coll.isAbstract))
                            SerializablePropertyType.BIG_DECIMAL, SerializablePropertyType.INT, SerializablePropertyType.LONG -> array.add(elm as Number)
                            SerializablePropertyType.BOOLEAN -> array.add(elm as Boolean)
                            SerializablePropertyType.BYTE_ARRAY -> array.add(Base64.getEncoder().encodeToString(elm as ByteArray))
                            SerializablePropertyType.LOCAL_DATE_TIME -> array.add((elm as LocalDateTime).format(dateTimeFormatter))
                            SerializablePropertyType.LOCAL_DATE -> array.add((elm as LocalDate).format(dateFormatter))
                            SerializablePropertyType.CLASS -> array.add((elm as KClass<*>).qualifiedName)
                        }
                }
            }
        }
        return result
    }

    @Suppress("UNCHECKED_CAST")
    private fun<T:Any> getProvider(qualifiedName: String, factory: ProviderFactory): ObjectMetadataProvider<T> {
        return providersCache.getOrPut(qualifiedName, {factory.create(qualifiedName)}) as ObjectMetadataProvider<T>
    }


    internal fun <T : Any> serializeToString(obj: T, factory: ProviderFactory, isAbstract: Boolean): String {
        val json = serialize(obj, factory, isAbstract)
        val stringWriter = StringWriter()
        val jsonWriter = JsonWriter(stringWriter)
        jsonWriter.isLenient = true
        jsonWriter.setIndent("  ")
        Streams.write(json, jsonWriter)
        return stringWriter.toString()
    }

    internal fun <T : Any> serializeToByteArray(obj: T, factory: ProviderFactory, isAbstract: Boolean): ByteArray {
        val json = serialize(obj, factory, isAbstract)
        val baos = ByteArrayOutputStream()
        val writer = OutputStreamWriter(baos, "utf-8")
        val jsonWriter = JsonWriter(writer)
        jsonWriter.isLenient = true
        jsonWriter.setIndent("  ")
        Streams.write(json, jsonWriter)
        return baos.toByteArray()
    }

    internal fun <T : Any> deserialize(cls: KClass<T>, content:String, factory: ProviderFactory): T {
        return deserialize(cls, JsonParser.parseString(content) as JsonObject, factory, hashMapOf())
    }

    internal fun <T : Any> deserialize(cls: KClass<T>, content:ByteArray, factory: ProviderFactory): T {
        return deserialize(cls, JsonParser.parseReader(InputStreamReader(ByteArrayInputStream(content))) as JsonObject, factory, hashMapOf())
    }

    internal fun <T : Any> deserialize(cls: KClass<T>, obj:JsonObject, factory: ProviderFactory): T {
        return deserialize(cls, obj, factory, hashMapOf())
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> deserialize(cls: KClass<T>, jsonObj: JsonObject, factory: ProviderFactory, context: MutableMap<String, Any>): T {
        val className = jsonObj.get("_className")?.asString?:cls.java.name
        val provider = getProvider<T>(className, factory)
        if (provider.hasUid()) {
            val uid = jsonObj.get("uid").asString
            val existing = context[uid]
            if (existing != null) {
                return existing as T
            }
        }
        val result = ReflectionUtils.newInstance<T>(className)
        if (provider.hasUid()) {
            val uid = jsonObj.get("uid").asString
            context[uid] = result
        }
        provider.properties.forEach { prop ->
            if (jsonObj.has(prop.id)) {
                val value =
                        when (prop.type) {
                            SerializablePropertyType.STRING -> jsonObj.get(prop.id).asString
                            SerializablePropertyType.ENUM -> ReflectionUtils.safeGetEnum(prop.className?:
                            throw IllegalStateException("no classname attribute in ${prop.id}"), jsonObj.get(prop.id).asString)
                            SerializablePropertyType.ENTITY -> deserialize(ReflectionUtils.getClass(prop.className?:
                            throw IllegalStateException("no classname attribute in ${prop.id}")), jsonObj.getAsJsonObject(prop.id), factory)
                            SerializablePropertyType.BIG_DECIMAL -> jsonObj.get(prop.id).asBigDecimal
                            SerializablePropertyType.INT -> jsonObj.get(prop.id).asInt
                            SerializablePropertyType.LONG -> jsonObj.get(prop.id).asLong
                            SerializablePropertyType.BOOLEAN -> jsonObj.get(prop.id).asBoolean
                            SerializablePropertyType.BYTE_ARRAY -> Base64.getDecoder().decode(jsonObj.get(prop.id).asString)
                            SerializablePropertyType.LOCAL_DATE_TIME -> LocalDateTime.parse(jsonObj.get(prop.id).asString, dateTimeFormatter)
                            SerializablePropertyType.LOCAL_DATE -> LocalDate.parse(jsonObj.get(prop.id).asString, dateFormatter)
                            SerializablePropertyType.CLASS -> ReflectionUtils.getClass<Any>(jsonObj.get(prop.id).asString)
                        }
                provider.setPropertyValue(result, prop.id, value)
            }
        }
        provider.collections.forEach { coll ->
            if (jsonObj.has(coll.id)) {
                val array = jsonObj.getAsJsonArray(coll.id)
                array.forEach { elm ->
                    val value = when (coll.elementType) {
                        SerializablePropertyType.STRING -> elm.asString
                        SerializablePropertyType.ENUM -> ReflectionUtils.safeGetEnum(coll.elementClassName?:
                        throw IllegalStateException("no classname attibute in ${coll.id}"), elm.asString)
                        SerializablePropertyType.ENTITY -> deserialize(ReflectionUtils.getClass(coll.elementClassName?:
                        throw IllegalStateException("no classname attibute in ${coll.id}")), elm.asJsonObject, factory)
                        SerializablePropertyType.BIG_DECIMAL -> elm.asBigDecimal
                        SerializablePropertyType.INT -> elm.asInt
                        SerializablePropertyType.LONG -> elm.asLong
                        SerializablePropertyType.BOOLEAN -> elm.asBoolean
                        SerializablePropertyType.BYTE_ARRAY -> Base64.getDecoder().decode(elm.asString)
                        SerializablePropertyType.LOCAL_DATE_TIME -> LocalDateTime.parse(elm.asString, dateTimeFormatter)
                        SerializablePropertyType.LOCAL_DATE -> LocalDate.parse(elm.asString, dateFormatter)
                        SerializablePropertyType.CLASS -> ReflectionUtils.getClass<Any>(elm.asString)
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