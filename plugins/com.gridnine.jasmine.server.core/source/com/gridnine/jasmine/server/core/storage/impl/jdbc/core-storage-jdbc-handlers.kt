/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")

package com.gridnine.jasmine.server.core.storage.impl.jdbc

import com.gridnine.jasmine.server.core.model.common.BaseEntity
import com.gridnine.jasmine.server.core.model.domain.EntityReference
import com.gridnine.jasmine.server.core.utils.ReflectionUtils
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import kotlin.collections.ArrayList


internal class BlobTypeFieldHandler : BaseSimpleTypeFieldHandler(SqlType.BLOB) {

    override fun getPropertyIndexes(property: DatabasePropertyDescription, table: DatabaseTableDescription): Map<String, String> {
        return emptyMap()
    }

    override fun getSqlQueryValue(modelValue: Any?): FieldValue {
        throw IllegalArgumentException("unable to query against blob")
    }
}


internal class DateTypeFieldHandler : BaseSimpleTypeFieldHandler(SqlType.DATE) {

    override fun getModelPropertyValue(property: DatabasePropertyDescription, jdbcValues: List<Any?>): Any? {
        return jdbcValues[0]?.let { LocalDateTime.ofInstant(Instant.ofEpochMilli((it as java.sql.Date).time), ZoneId.systemDefault()).toLocalDate() }
    }

    override fun getJdbcPropertyValue(property: DatabasePropertyDescription, modelValue: Any?): List<Any?> {
        return listOf(modelValue?.let{java.sql.Date((it as LocalDate).atTime(0, 0, 0, 0).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())})
    }

    override fun getSqlQueryValue(modelValue: Any?): FieldValue {
        return FieldValue(modelValue?.let{java.sql.Date((it as LocalDate).atTime(0, 0, 0, 0)
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())}, SqlType.DATE)
    }

}


internal class DateTimeTypeFieldHandler : BaseSimpleTypeFieldHandler(SqlType.TIMESTAMP) {

    override fun getModelPropertyValue(property: DatabasePropertyDescription, jdbcValues: List<Any?>): Any? {
        return jdbcValues[0]?.let {   LocalDateTime.ofInstant(Instant.ofEpochMilli((it as Timestamp).time),ZoneId.systemDefault())}
    }

    override fun getJdbcPropertyValue(property: DatabasePropertyDescription, modelValue: Any?): List<Any?> {
        return listOf(modelValue?.let{Timestamp((it  as LocalDateTime).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())})
    }

    override fun getSqlQueryValue(modelValue: Any?): FieldValue {
        return FieldValue(modelValue?.let{Timestamp((it as LocalDateTime).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())}, SqlType.TIMESTAMP)
    }

}


internal class LongTypeFieldHandler : BaseSimpleTypeFieldHandler(SqlType.LONG) {

    override fun getModelPropertyValue(property: DatabasePropertyDescription, jdbcValues: List<Any?>): Any? {
        return jdbcValues[0]
    }

    override fun getJdbcPropertyValue(property: DatabasePropertyDescription, modelValue: Any?): List<Any?> {
        return listOf(modelValue)
    }

    override fun getSqlQueryValue(modelValue: Any?): FieldValue {
        return FieldValue(modelValue, SqlType.LONG)
    }

}

internal class IntTypeFieldHandler : BaseSimpleTypeFieldHandler(SqlType.INT) {

    override fun getModelPropertyValue(property: DatabasePropertyDescription, jdbcValues: List<Any?>): Any? {
        return jdbcValues[0]
    }

    override fun getJdbcPropertyValue(property: DatabasePropertyDescription, modelValue: Any?): List<Any?> {
        return listOf(modelValue)
    }

    override fun getSqlQueryValue(modelValue: Any?): FieldValue {
        return FieldValue(modelValue, SqlType.INT)
    }

}

internal class BigDecimalTypeFieldHandler : BaseSimpleTypeFieldHandler(SqlType.BIG_DECIMAL) {

    override fun getModelPropertyValue(property: DatabasePropertyDescription, jdbcValues: List<Any?>): Any? {
        return jdbcValues[0]
    }

    override fun getJdbcPropertyValue(property: DatabasePropertyDescription, modelValue: Any?): List<Any?> {
        return listOf(modelValue)
    }

    override fun getSqlQueryValue(modelValue: Any?): FieldValue {
        return FieldValue(modelValue, SqlType.BIG_DECIMAL)
    }

}


internal class BooleanTypeFieldHandler : BaseSimpleTypeFieldHandler(SqlType.BOOLEAN) {

    override fun getModelPropertyValue(property: DatabasePropertyDescription, jdbcValues: List<Any?>): Any? {
        return jdbcValues[0]
    }

    override fun getJdbcPropertyValue(property: DatabasePropertyDescription, modelValue: Any?): List<Any?> {
        return listOf(modelValue)
    }

    override fun getSqlQueryValue(modelValue: Any?): FieldValue {
        return FieldValue(modelValue, SqlType.BOOLEAN)
    }

}


internal class EnumTypeFieldHandler : GenericStringTypeFieldHandler(SqlType.STRING) {

    override fun getModelPropertyValue(property: DatabasePropertyDescription, jdbcValues: List<Any?>): Any? {
        val str = super.getModelPropertyValue(property, jdbcValues) as String? ?: return null
        return ReflectionUtils.safeGetEnum(property.className
                ?: throw IllegalArgumentException("no classname in property ${property.name}"), str)
    }

    override fun getModelCollectionValues(coll: DatabaseCollectionDescription, jdbcValues: List<Any?>): List<Any?> {
        return super.getModelCollectionValues(coll, jdbcValues).map{it?.let{item -> ReflectionUtils.safeGetEnum(coll.elementClassName
                ?: throw IllegalArgumentException("no classname in ${coll.name}")
                , item.toString())}}.toList()
    }

    override fun getJdbcPropertyValue(property: DatabasePropertyDescription, modelValue: Any?): List<Any?> {
        return listOf(modelValue?.let{(it as Enum<*>).name as Any})
    }

    override fun getJdbcCollectionValues(coll: DatabaseCollectionDescription, modelValues: List<Any?>): List<Any?> {
        return super.getJdbcCollectionValues(coll, modelValues.map { item -> item?.let { (it as Enum<*>).name }}.toList())
    }


    override fun getSqlQueryValue(modelValue: Any?): FieldValue {
        return FieldValue(modelValue?.let { (it as Enum<*>).name }, SqlType.STRING)
    }

}

internal open class GenericStringTypeFieldHandler(sqlType: SqlType) : BaseSimpleTypeFieldHandler(sqlType) {

    override fun getCollectionIndexes(coll: DatabaseCollectionDescription, table: DatabaseTableDescription): Map<String, String> {
        return mapOf("${table.name}_${coll.name}" to coll.name)
    }

    override fun getCollectionColumns(coll: DatabaseCollectionDescription): Map<String, SqlType> {
        return mapOf(coll.name to SqlType.TEXT)
    }


    override fun getModelCollectionValues(coll: DatabaseCollectionDescription, jdbcValues: List<Any?>): List<Any?> {
        return toModelStringValues(jdbcValues)
    }

    override fun getJdbcCollectionValues(coll: DatabaseCollectionDescription, modelValues: List<Any?>): List<Any?> {
        return toJdbcStringValues(modelValues)
    }

    override fun getSqlQueryValue(modelValue: Any?): FieldValue {
        return FieldValue(modelValue, type)
    }
}

internal abstract class BaseSimpleTypeFieldHandler(protected val type: SqlType) : JdbcMappingHandler {

    override fun getPropertyColumns(property: DatabasePropertyDescription): Map<String, SqlType> {
        return Collections.singletonMap(property.name, type)
    }

    override fun getPropertyIndexes(property: DatabasePropertyDescription, table: DatabaseTableDescription): Map<String, String> {
        return Collections.singletonMap(String.format("%s_%s", table.name, property.name), property.name)
    }

    override fun getModelPropertyValue(property: DatabasePropertyDescription, jdbcValues: List<Any?>): Any? {
        return jdbcValues[0]
    }

    override fun getCollectionColumns(coll: DatabaseCollectionDescription): Map<String, SqlType> {
        return Collections.singletonMap(coll.name, SqlType.BLOB)
    }

    override fun getCollectionIndexes(coll: DatabaseCollectionDescription, table: DatabaseTableDescription): Map<String, String> {
        return emptyMap()
    }

    override fun getModelCollectionValues(coll: DatabaseCollectionDescription, jdbcValues: List<Any?>): List<Any?> {
        val value = jdbcValues[0] ?: return emptyList<Any>()
        return arrayListOf(value as ByteArray)
    }

    override fun getJdbcPropertyValue(property: DatabasePropertyDescription, modelValue: Any?): List<Any?> {
        return listOf(modelValue)
    }

    override fun getJdbcCollectionValues(coll: DatabaseCollectionDescription, modelValues: List<Any?>): List<Any?> {
        return if (modelValues.isEmpty()) {
            arrayListOf()
        } else listOf(modelValues[0] as ByteArray)
    }
}

internal class EntityReferenceFieldHandler : JdbcMappingHandler {

    override fun getPropertyColumns(property: DatabasePropertyDescription): Map<String, SqlType> {
        return linkedMapOf(property.name to SqlType.STRING,
                "${property.name}ClassName" to SqlType.STRING,
                "${property.name}Caption" to SqlType.STRING)
    }

    override fun getPropertyIndexes(property: DatabasePropertyDescription, table: DatabaseTableDescription): Map<String, String> {
        return linkedMapOf("${table.name}_${property.name}" to property.name,
                "${table.name}_${property.name}ClassName" to "${property.name}ClassName",
                "${table.name}_${property.name}Caption" to "${property.name}Caption")
    }

    override fun getModelPropertyValue(property: DatabasePropertyDescription, jdbcValues: List<Any?>): Any? {
        val uid = jdbcValues[0] as String?
        val refClassName = jdbcValues[1] as String?
        val caption = jdbcValues[2] as String?
        if (uid == null || refClassName == null) {
            return null
        }
        val result = EntityReference<BaseEntity>()
        result.caption = caption
        result.uid = uid
        result.type = ReflectionUtils.getClass(refClassName)
        return result
    }

    override fun getJdbcPropertyValue(property: DatabasePropertyDescription, modelValue: Any?): List<Any?> {
        @Suppress("UNCHECKED_CAST")
        val ref = modelValue as EntityReference<BaseEntity>?
        val result = ArrayList<Any?>()
        result.add(ref?.uid)
        result.add(ref?.type?.qualifiedName)
        result.add(ref?.caption)
        return result
    }

    override fun getCollectionColumns(coll: DatabaseCollectionDescription): Map<String, SqlType> {
        return linkedMapOf(coll.name to SqlType.STRING,
                "${coll.name}ClassName" to SqlType.STRING,
                "${coll.name}Caption" to SqlType.STRING)
    }

    override fun getCollectionIndexes(coll: DatabaseCollectionDescription, table: DatabaseTableDescription): Map<String, String> {
        return linkedMapOf("${table.name}_${coll.name}" to coll.name,
        "${table.name}_${coll.name}ClassName" to "${coll.name}ClassName",
        "${table.name}_${coll.name}Caption" to "${coll.name}Caption")
    }

    override fun getModelCollectionValues(coll: DatabaseCollectionDescription, jdbcValues: List<Any?>): List<Any?> {
        val result = ArrayList<Any?>()
        if (jdbcValues[0] == null) {
            return result
        }
        val uids = (jdbcValues[0] as String).split("||")
        val classes = (jdbcValues[1] as String).split("||")
        val captions = (jdbcValues[2] as String).split("||")
        uids.withIndex().forEach { (idx, value) ->
            result.add(EntityReference(ReflectionUtils.getClass<BaseEntity>(classes[idx]), value, if (captions[idx] == "null") null else captions[idx]))
        }
        return result
    }

    override fun getJdbcCollectionValues(coll: DatabaseCollectionDescription, modelValues: List<Any?>): List<Any?> {
        val uids = arrayListOf<String>()
        val classes = arrayListOf<String>()
        val captions = arrayListOf<String>()
        modelValues.forEach { value ->
            if (value != null) {
                uids.add((value as EntityReference<*>).uid)
                classes.add(value.type.java.name)
                captions.add(value.caption ?: "null")
            }
        }
        return arrayListOf(toJdbcStringValues(uids), toJdbcStringValues(classes), toJdbcStringValues(captions))
    }

    override fun getSqlQueryValue(modelValue: Any?): FieldValue {
        return FieldValue(
                if (modelValue == null)
                    null
                else
                    (modelValue as EntityReference<*>).uid,
                SqlType.STRING)
    }

}


internal fun toJdbcStringValues(modelValues: List<Any?>): List<Any?> {
    if (modelValues.isEmpty()) {
        return listOf<Any?>(null)
    }
    return listOf(modelValues.joinToString("||"))
}


internal fun toModelStringValues(values: List<Any?>): List<String?> {
    return values[0]?.let{(it as String).split("\\|\\|").map { item -> if("null" == item) null else item }}?: emptyList()
}


object JdbcHandlerUtils {
    private val handlersMap = hashMapOf<JdbcPropertyType, JdbcMappingHandler>()

    init {
        handlersMap[JdbcPropertyType.BYTE_ARRAY] = BlobTypeFieldHandler()
        handlersMap[JdbcPropertyType.LOCAL_DATE_TIME] = DateTimeTypeFieldHandler()
        handlersMap[JdbcPropertyType.LOCAL_DATE] = DateTypeFieldHandler()
        handlersMap[JdbcPropertyType.ENTITY_REFERENCE] = EntityReferenceFieldHandler()
        handlersMap[JdbcPropertyType.ENUM] = EnumTypeFieldHandler()
        handlersMap[JdbcPropertyType.STRING] = GenericStringTypeFieldHandler(
                SqlType.STRING)
        handlersMap[JdbcPropertyType.TEXT] = GenericStringTypeFieldHandler(
                SqlType.TEXT)
        handlersMap[JdbcPropertyType.LONG] = LongTypeFieldHandler()
        handlersMap[JdbcPropertyType.INT] = IntTypeFieldHandler()
        handlersMap[JdbcPropertyType.BIG_DECIMAL] = BigDecimalTypeFieldHandler()
        handlersMap[JdbcPropertyType.BOOLEAN] = BooleanTypeFieldHandler()
    }

    internal fun getHandler(propertyType: JdbcPropertyType): JdbcMappingHandler {
        return handlersMap[propertyType]
                ?: throw IllegalArgumentException("no handler registered for type $propertyType")
    }
}