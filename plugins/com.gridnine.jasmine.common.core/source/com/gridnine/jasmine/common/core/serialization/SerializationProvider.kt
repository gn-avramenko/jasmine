/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.common.core.serialization


import com.fasterxml.jackson.core.*
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.gridnine.jasmine.common.core.meta.CustomMetaRegistry
import com.gridnine.jasmine.common.core.meta.DomainMetaRegistry
import com.gridnine.jasmine.common.core.meta.RestMetaRegistry
import com.gridnine.jasmine.common.core.meta.UiMetaRegistry
import com.gridnine.jasmine.common.core.model.BaseIdentity
import com.gridnine.jasmine.common.core.model.BaseIntrospectableObject
import com.gridnine.jasmine.common.core.model.ObjectReference
import com.gridnine.jasmine.common.core.model.Xeption
import com.gridnine.jasmine.common.core.reflection.ReflectionFactory
import com.gridnine.jasmine.common.core.app.Disposable
import com.gridnine.jasmine.common.core.app.PublishableWrapper
import com.gridnine.jasmine.common.core.utils.TextUtils
import java.io.InputStream
import java.io.OutputStream
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
class SerializationProvider : Disposable {

    val providersCache = ConcurrentHashMap<String, ObjectMetadataProvider<*>>()

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm-ss-SSS")

    private val jsonFactory = JsonFactory()


    fun <T : Any> serialize(obj: T, stream: OutputStream, isAbstract: Boolean = false, prettyPrint: Boolean = false) {
        val generator = jsonFactory.createGenerator(stream, JsonEncoding.UTF8)
        if (prettyPrint) {
            generator.prettyPrinter = DefaultPrettyPrinter()
        }
        serialize(generator, obj, isAbstract, hashSetOf())
        generator.close()
    }
    fun <T : Any> deserialize(cls: KClass<T>, stream: InputStream):T {
        return deserialize(cls.java.name, stream)
    }

    fun <T : Any> deserialize(className: String, stream: InputStream):T {
        val parser = jsonFactory.createParser(stream)
        val result = deserialize<T>(parser, className, hashMapOf())
        parser.close()
        return result
    }

    private fun <T : Any> deserialize(parser: JsonParser, className: String, ctx: MutableMap<String, Any>) :T {
        var result:T? = null
        var uid:String? = null
        var realClassName = className
        var provider = providersCache.getOrPut(className, { createProvider(className) }) as ObjectMetadataProvider<T>
        outer@ while (parser.nextToken() != JsonToken.END_OBJECT){
            if(parser.currentToken == JsonToken.START_OBJECT){
                val nextToken = parser.nextToken()
                if(nextToken == JsonToken.END_OBJECT){
                    result = ReflectionFactory.get().newInstance<T>(realClassName)
                    break@outer
                }
            }
            when(val tagName = parser.currentName){
                BaseIdentity.uid ->{
                    parser.nextToken()
                    uid = parser.text
                    if(provider.hasUid() && ObjectReference::class.qualifiedName != realClassName){
                        val existingObject = ctx[uid]
                        if(existingObject != null && existingObject !is ObjectReference<*>){
                            return existingObject as T
                        }
                    }
                }
                CLASS_NAME_PROPERTY ->{
                    parser.nextToken()
                    realClassName = parser.text
                    provider = providersCache.getOrPut(realClassName, { createProvider(realClassName) }) as ObjectMetadataProvider<T>
                }
                else ->{
                    if(result == null) {
                        result = ReflectionFactory.get().newInstance<T>(realClassName)
                        if(provider.hasUid()) {
                            provider.setPropertyValue(result, BaseIdentity.uid, uid)
                            if(uid != null && result !is ObjectReference<*>) {
                                ctx.putIfAbsent(uid, result)
                            }
                        } else if (uid != null){
                            provider.setPropertyValue(result, BaseIdentity.uid, uid)
                        }
                    }
                    val propertyDescription = provider.getProperty(tagName)
                    if(propertyDescription != null){
                        val value = when(propertyDescription.type){
                            SerializablePropertyType.STRING -> {
                                parser.nextToken()
                                parser.text
                            }
                            SerializablePropertyType.ENUM ->{
                                parser.nextToken()
                                ReflectionFactory.get().safeGetEnum(propertyDescription.className?:
                                    throw IllegalStateException("no classname attribute in ${propertyDescription.id}"), parser.text)
                            }
                            SerializablePropertyType.ENTITY -> {
                                parser.nextToken()
                                deserialize(parser, propertyDescription.className?:throw Xeption.forDeveloper("no classname defined in property ${propertyDescription.id}"), ctx)
                            }
                            SerializablePropertyType.BIG_DECIMAL ->{
                                parser.nextToken()
                                parser.valueAsDouble.toBigDecimal()
                            }
                            SerializablePropertyType.INT ->{
                                parser.nextToken()
                                parser.valueAsInt
                            }
                            SerializablePropertyType.LONG ->{
                                parser.nextToken()
                                parser.valueAsLong
                            }
                            SerializablePropertyType.BYTE_ARRAY ->{
                                parser.nextToken()
                                parser.binaryValue
                            }
                            SerializablePropertyType.LOCAL_DATE_TIME ->{
                                parser.nextToken()
                                LocalDateTime.parse(parser.text, dateTimeFormatter)
                            }
                            SerializablePropertyType.LOCAL_DATE ->{
                                parser.nextToken()
                                LocalDate.parse(parser.text, dateFormatter)
                            }
                            SerializablePropertyType.CLASS ->{
                                parser.nextToken()
                                ReflectionFactory.get().getClass<Any>(parser.text)
                            }
                            SerializablePropertyType.BOOLEAN ->{
                                parser.nextToken()
                                parser.valueAsBoolean
                            }
                        }
                        provider.setPropertyValue(result, propertyDescription.id, value)
                        continue@outer
                    }
                    val collectionDescription = provider.getCollection(tagName)?:throw Xeption.forDeveloper("object $realClassName has neither property nor collection with id $tagName")
                    val collection = provider.getCollection(result, tagName)
                    while(parser.nextToken() != JsonToken.END_ARRAY){
                        if(parser.currentToken == JsonToken.START_ARRAY){
                            parser.nextToken()
                        }
                        val value = when(collectionDescription.elementType){
                            SerializablePropertyType.STRING -> {
                                parser.text
                            }
                            SerializablePropertyType.ENUM ->{
                                ReflectionFactory.get().safeGetEnum(collectionDescription.elementClassName?:
                                throw IllegalStateException("no classname attribute in ${collectionDescription.id}"), parser.text)
                            }
                            SerializablePropertyType.ENTITY -> {
                                deserialize(parser, collectionDescription.elementClassName?:throw Xeption.forDeveloper("no classname defined in collection ${collectionDescription.id}"), ctx)
                            }
                            SerializablePropertyType.BIG_DECIMAL ->{
                                parser.valueAsDouble.toBigDecimal()
                            }
                            SerializablePropertyType.INT ->{
                                parser.valueAsInt
                            }
                            SerializablePropertyType.LONG ->{
                                parser.valueAsLong
                            }
                            SerializablePropertyType.BYTE_ARRAY ->{
                                parser.binaryValue
                            }
                            SerializablePropertyType.LOCAL_DATE_TIME ->{
                                dateTimeFormatter.parse(parser.text)
                            }
                            SerializablePropertyType.LOCAL_DATE ->{
                                dateFormatter.parse(parser.text)
                            }
                            SerializablePropertyType.CLASS ->{
                                ReflectionFactory.get().getClass<Any>(parser.text)
                            }
                            SerializablePropertyType.BOOLEAN ->{
                                parser.valueAsBoolean
                            }
                        }
                        if(value != null) {
                            collection.add(value)
                        }

                    }
                }
            }
        }
        return result as T
    }

    private fun <T : Any> serialize(generator: JsonGenerator, obj: T, isAbstract: Boolean, uids: MutableSet<String>) {
        var key = obj::class.java.name
        if(key.contains("_Cached")){
            key = key.substring(0, key.lastIndexOf("."))+"."+key.substringAfterLast("_Cached")
        }
        val provider = providersCache.getOrPut(key, { createProvider(key) }) as ObjectMetadataProvider<T>
        if (provider.hasUid()) {
            val uid = provider.getPropertyValue(obj, "uid") as String?
            if (uid != null && obj !is ObjectReference<*>) {
                if (uids.contains(uid)) {
                    generator.writeStringField("uid", uid)
                    return
                }
                uids.add(uid)
            }
        }
        generator.writeStartObject()
        if (isAbstract) {
            generator.writeStringField(CLASS_NAME_PROPERTY, key)
        }
        provider.getAllProperties().forEach { prop ->
            val value = provider.getPropertyValue(obj, prop.id)
            if (value != null) {
                when (prop.type) {
                    SerializablePropertyType.STRING -> generator.writeStringField(prop.id, value as String)
                    SerializablePropertyType.CLASS -> generator.writeStringField(prop.id, (value as KClass<*>).qualifiedName)
                    SerializablePropertyType.ENUM -> generator.writeStringField(prop.id, (value as Enum<*>).name)
                    SerializablePropertyType.ENTITY -> {
                        generator.writeFieldName(prop.id)
                        serialize(generator, value, prop.isAbstract, uids)
                    }
                    SerializablePropertyType.BIG_DECIMAL -> generator.writeNumberField(prop.id, value as BigDecimal)
                    SerializablePropertyType.INT -> generator.writeNumberField(prop.id, value as Int)
                    SerializablePropertyType.LONG -> generator.writeNumberField(prop.id, value as Long)
                    SerializablePropertyType.BOOLEAN -> generator.writeBooleanField(prop.id, value as Boolean)
                    SerializablePropertyType.BYTE_ARRAY -> generator.writeBinaryField(prop.id, value as ByteArray)
                    SerializablePropertyType.LOCAL_DATE_TIME -> generator.writeStringField(prop.id, (value as LocalDateTime).format(dateTimeFormatter))
                    SerializablePropertyType.LOCAL_DATE -> generator.writeStringField(prop.id, (value as LocalDate).format(dateFormatter))
                }
            }
        }
        provider.getAllCollections().forEach { coll ->
            val colls = provider.getCollection(obj, coll.id)
            if (colls.isNotEmpty()) {
                generator.writeFieldName(coll.id)
                generator.writeStartArray()
                colls.forEach { elm ->
                    when (coll.elementType) {
                        SerializablePropertyType.STRING -> generator.writeString(elm as String)
                        SerializablePropertyType.CLASS -> generator.writeString((elm as KClass<*>).qualifiedName)
                        SerializablePropertyType.ENUM -> generator.writeString((elm as Enum<*>).name)
                        SerializablePropertyType.ENTITY -> {
                            serialize(generator, elm, coll.isAbstract, uids)
                        }
                        SerializablePropertyType.BIG_DECIMAL -> generator.writeNumber(elm as BigDecimal)
                        SerializablePropertyType.INT -> generator.writeNumber(elm as Int)
                        SerializablePropertyType.LONG -> generator.writeNumber(elm as Long)
                        SerializablePropertyType.BOOLEAN -> generator.writeBoolean(elm as Boolean)
                        SerializablePropertyType.BYTE_ARRAY -> generator.writeBinary(elm as ByteArray)
                        SerializablePropertyType.LOCAL_DATE_TIME -> generator.writeString((elm as LocalDateTime).format(dateTimeFormatter))
                        SerializablePropertyType.LOCAL_DATE -> generator.writeString((elm as LocalDate).format(dateFormatter))
                    }
                }
                generator.writeEndArray()
            }
        }
        generator.writeEndObject()
    }


    fun <T : BaseIntrospectableObject> clone(source: T, newUids: Boolean): T {
        val qualifiedName = source.javaClass.name
        val result = ReflectionFactory.get().newInstance<T>(qualifiedName)
        val provider = providersCache.getOrPut(qualifiedName, { createProvider(qualifiedName) }) as ObjectMetadataProvider<T>
        copy(source, result, newUids, provider)
        return result
    }

    private fun <T : BaseIntrospectableObject> copy(source: T, result: T, newUids: Boolean, provider: ObjectMetadataProvider<*>) {
        if (provider.hasUid()) {
            result.setValue(BaseIdentity.uid, if (newUids) TextUtils.generateUid() else source.getValue(BaseIdentity.uid))
        }
        provider.getAllProperties().forEach {
            val value = source.getValue(it.id) ?: return@forEach
            if (it.id == BaseIdentity.uid) {
                return@forEach
            }
            when (it.type) {
                SerializablePropertyType.ENTITY -> {
                    val qualifiedName = value.javaClass.name
                    val newEntity = ReflectionFactory.get().newInstance<BaseIntrospectableObject>(qualifiedName)
                    val elmProvider = providersCache.getOrPut(qualifiedName, { createProvider(qualifiedName) }) as ObjectMetadataProvider<T>
                    copy(value as BaseIntrospectableObject, newEntity, newUids, elmProvider)
                    result.setValue(it.id, newEntity)
                }
                SerializablePropertyType.STRING,
                SerializablePropertyType.ENUM,
                SerializablePropertyType.BIG_DECIMAL,
                SerializablePropertyType.BOOLEAN,
                SerializablePropertyType.LONG,
                SerializablePropertyType.LOCAL_DATE,
                SerializablePropertyType.LOCAL_DATE_TIME,
                SerializablePropertyType.INT,
                SerializablePropertyType.CLASS
                -> result.setValue(it.id, value)
                SerializablePropertyType.BYTE_ARRAY ->
                    result.setValue(it.id, (value as ByteArray).copyOf())
            }

        }
        provider.getAllCollections().forEach {
            val sourceColl = source.getCollection(it.id)
            val destColl = result.getCollection(it.id)
            sourceColl.forEach {elmValue ->
                when (it.elementType) {
                    SerializablePropertyType.ENTITY -> {
                        val qualifiedName = elmValue.javaClass.name
                        val newEntity = ReflectionFactory.get().newInstance<BaseIntrospectableObject>(qualifiedName)
                        val elmProvider = providersCache.getOrPut(qualifiedName, { createProvider(qualifiedName) }) as ObjectMetadataProvider<T>
                        copy(elmValue as BaseIntrospectableObject, newEntity, newUids, elmProvider)
                        destColl.add(newEntity)
                    }
                    SerializablePropertyType.STRING,
                    SerializablePropertyType.ENUM,
                    SerializablePropertyType.BIG_DECIMAL,
                    SerializablePropertyType.BOOLEAN,
                    SerializablePropertyType.LONG,
                    SerializablePropertyType.LOCAL_DATE,
                    SerializablePropertyType.LOCAL_DATE_TIME,
                    SerializablePropertyType.INT,
                    SerializablePropertyType.CLASS -> destColl.add(elmValue)
                    SerializablePropertyType.BYTE_ARRAY ->destColl.add((elmValue as ByteArray).copyOf())
                }
            }
        }
    }

    override fun dispose() {
        wrapper.dispose()
    }

    companion object {
        fun createProvider(key: String): ObjectMetadataProvider<*> {
            val docDescription = DomainMetaRegistry.get().documents[key]
            if (docDescription != null) {
                return DomainDocumentMetadataProvider(docDescription)
            }
            val nestedDocumentDescription = DomainMetaRegistry.get().nestedDocuments[key]
            if (nestedDocumentDescription != null) {
                return NestedDocumentMetadataProvider(nestedDocumentDescription)
            }
            val assetDescription = DomainMetaRegistry.get().assets[key]
            if(assetDescription != null){
                return AssetMetadataProvider(assetDescription)
            }
            val indexDescription = DomainMetaRegistry.get().indexes[key]
            if(indexDescription != null){
                return DomainIndexMetadataProvider(indexDescription)
            }
            val restEntityDescription = RestMetaRegistry.get().entities[key]
            if (restEntityDescription != null) {
                return RestEntityMetadataProvider(restEntityDescription)
            }
            val customEntityDescription = CustomMetaRegistry.get().entities[key]
            if (customEntityDescription != null) {
                return CustomEntityMetadataProvider(customEntityDescription)
            }
            val viewModelEntityDescription = UiMetaRegistry.get().viewModels[key]
            if (viewModelEntityDescription != null) {
                return VMEntityMetadataProvider(viewModelEntityDescription)
            }
            val viewSettingsEntityDescription = UiMetaRegistry.get().viewSettings[key]
            if (viewSettingsEntityDescription != null) {
                return VSEntityMetadataProvider(viewSettingsEntityDescription)
            }
            val viewValidationEntityDescription = UiMetaRegistry.get().viewValidations[key]
            if (viewValidationEntityDescription != null) {
                return VVEntityMetadataProvider(viewValidationEntityDescription)
            }
            TODO()
        }
        private const val CLASS_NAME_PROPERTY = "_className"
        private val wrapper = PublishableWrapper(SerializationProvider::class)
        fun get() = wrapper.get()
    }
}