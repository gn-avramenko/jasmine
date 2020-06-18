/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST")

package com.gridnine.jasmine.server.core.storage.impl.jdbc

import com.gridnine.jasmine.server.core.model.common.BaseIdentity
import com.gridnine.jasmine.server.core.model.domain.ObjectReference
import com.gridnine.jasmine.server.core.reflection.ReflectionFactory
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import kotlin.collections.ArrayList


internal class BlobTypeFieldHandler : BaseSimpleTypePropertyFieldHandler(SqlType.BLOB) {

    override fun getPropertyIndexes(property: DatabasePropertyDescription, table: DatabaseTableDescription): Map<String, JdbcIndexDescription> {
        return emptyMap()
    }
    override fun getSqlQueryValue(modelValue: Any?): JdbcFieldValue {
        throw IllegalArgumentException("unable to query against blob")
    }
}


internal class DateTypeFieldHandler : BaseSimpleTypePropertyFieldHandler(SqlType.DATE) {

    override fun getModelPropertyValue(property: DatabasePropertyDescription, jdbcValues: List<Any?>): Any? {
        return jdbcValues[0]?.let { LocalDateTime.ofInstant(Instant.ofEpochMilli((it as java.sql.Date).time), ZoneId.systemDefault()).toLocalDate() }
    }

    override fun getJdbcPropertyValue(property: DatabasePropertyDescription, modelValue: Any?): List<Any?> {
        return listOf(modelValue?.let{java.sql.Date((it as LocalDate).atTime(0, 0, 0, 0).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())})
    }

    override fun getSqlQueryValue(modelValue: Any?): JdbcFieldValue {
        return JdbcFieldValue(modelValue?.let{java.sql.Date((it as LocalDate).atTime(0, 0, 0, 0)
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())}, SqlType.DATE)
    }

}


internal class DateTimeTypeFieldHandler : BaseSimpleTypePropertyFieldHandler(SqlType.TIMESTAMP) {

    override fun getModelPropertyValue(property: DatabasePropertyDescription, jdbcValues: List<Any?>): Any? {
        return jdbcValues[0]?.let {   LocalDateTime.ofInstant(Instant.ofEpochMilli((it as Timestamp).time),ZoneId.systemDefault())}
    }

    override fun getJdbcPropertyValue(property: DatabasePropertyDescription, modelValue: Any?): List<Any?> {
        return listOf(modelValue?.let{Timestamp((it  as LocalDateTime).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())})
    }

    override fun getSqlQueryValue(modelValue: Any?): JdbcFieldValue {
        return JdbcFieldValue(modelValue?.let{Timestamp((it as LocalDateTime).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())}, SqlType.TIMESTAMP)
    }

}


internal class LongTypeFieldHandler : BaseSimpleTypePropertyFieldHandler(SqlType.LONG) {

    override fun getModelPropertyValue(property: DatabasePropertyDescription, jdbcValues: List<Any?>): Any? {
        return jdbcValues[0]
    }

    override fun getJdbcPropertyValue(property: DatabasePropertyDescription, modelValue: Any?): List<Any?> {
        return listOf(modelValue)
    }

    override fun getSqlQueryValue(modelValue: Any?): JdbcFieldValue {
        return JdbcFieldValue(modelValue, SqlType.LONG)
    }

}

internal class IntTypeFieldHandler : BaseSimpleTypePropertyFieldHandler(SqlType.INT) {

    override fun getModelPropertyValue(property: DatabasePropertyDescription, jdbcValues: List<Any?>): Any? {
        return jdbcValues[0]
    }

    override fun getJdbcPropertyValue(property: DatabasePropertyDescription, modelValue: Any?): List<Any?> {
        return listOf(modelValue)
    }

    override fun getSqlQueryValue(modelValue: Any?): JdbcFieldValue {
        return JdbcFieldValue(modelValue, SqlType.INT)
    }

}

internal class BigDecimalTypeFieldHandler : BaseSimpleTypePropertyFieldHandler(SqlType.BIG_DECIMAL) {

    override fun getModelPropertyValue(property: DatabasePropertyDescription, jdbcValues: List<Any?>): Any? {
        return jdbcValues[0]
    }

    override fun getJdbcPropertyValue(property: DatabasePropertyDescription, modelValue: Any?): List<Any?> {
        return listOf(modelValue)
    }

    override fun getSqlQueryValue(modelValue: Any?): JdbcFieldValue {
        return JdbcFieldValue(modelValue, SqlType.BIG_DECIMAL)
    }

}


internal class BooleanTypeFieldHandler : BaseSimpleTypePropertyFieldHandler(SqlType.BOOLEAN) {

    override fun getModelPropertyValue(property: DatabasePropertyDescription, jdbcValues: List<Any?>): Any? {
        return jdbcValues[0]
    }

    override fun getJdbcPropertyValue(property: DatabasePropertyDescription, modelValue: Any?): List<Any?> {
        return listOf(modelValue)
    }

    override fun getSqlQueryValue(modelValue: Any?): JdbcFieldValue {
        return JdbcFieldValue(modelValue, SqlType.BOOLEAN)
    }

}

internal class EnumCollectionTypeFieldHandler : GenericStringTypeCollectionFieldHandler(SqlType.STRING) {


    override fun getModelCollectionValues(coll: DatabaseCollectionDescription, jdbcValues: List<Any?>): List<Any?> {
        return super.getModelCollectionValues(coll, jdbcValues).map{it?.let{item -> ReflectionFactory.get().safeGetEnum(coll.elementClassName
                ?: throw IllegalArgumentException("no classname in ${coll.name}")
                , item.toString())}}.toList()
    }


    override fun getJdbcCollectionValues(coll: DatabaseCollectionDescription, modelValues: List<Any?>): List<Any?> {
        return super.getJdbcCollectionValues(coll, modelValues.map { item -> item?.let { (it as Enum<*>).name }}.toList())
    }


    override fun getSqlQueryValue(modelValue: Any?): JdbcFieldValue {
        return JdbcFieldValue(modelValue?.let { (it as Enum<*>).name }, SqlType.STRING)
    }

}

internal class EnumTypeFieldHandler : GenericStringTypeFieldHandler(SqlType.STRING) {

    override fun getModelPropertyValue(property: DatabasePropertyDescription, jdbcValues: List<Any?>): Any? {
        val str = super.getModelPropertyValue(property, jdbcValues) as String? ?: return null
        return ReflectionFactory.get().safeGetEnum(property.className
                ?: throw IllegalArgumentException("no classname in property ${property.name}"), str)
    }

    override fun getJdbcPropertyValue(property: DatabasePropertyDescription, modelValue: Any?): List<Any?> {
        return listOf(modelValue?.let{(it as Enum<*>).name})
    }


    override fun getSqlQueryValue(modelValue: Any?): JdbcFieldValue {
        return JdbcFieldValue(modelValue?.let { (it as Enum<*>).name }, SqlType.STRING)
    }

}
internal open class GenericStringTypeCollectionFieldHandler(private val sqlType: SqlType) : JdbcCollectionMappingHandler {

    override fun getCollectionIndexes(coll: DatabaseCollectionDescription, table: DatabaseTableDescription): Map<String, JdbcIndexDescription> {
        return mapOf("${table.name}_${coll.name}" to JdbcIndexDescription(coll.name, JdbcIndexType.GIN))
    }

    override fun getCollectionColumns(coll: DatabaseCollectionDescription): Map<String, SqlType> {
        return Collections.singletonMap(coll.name, SqlType.STRING_ARRAY)
    }


    override fun getModelCollectionValues(coll: DatabaseCollectionDescription, jdbcValues: List<Any?>): List<Any?> {
        return ((jdbcValues[0] as java.sql.Array).array as Array<String>).toList()
    }

    override fun getJdbcCollectionValues(coll: DatabaseCollectionDescription, modelValues: List<Any?>): List<Any?> {
        return arrayListOf((modelValues as ArrayList<String>).toTypedArray())
    }

    override fun getSqlQueryValue(modelValue: Any?): JdbcFieldValue {
        return JdbcFieldValue(modelValue, sqlType)
    }
}

internal open class GenericStringTypeFieldHandler(sqlType: SqlType) : BaseSimpleTypePropertyFieldHandler(sqlType) {

    override fun getSqlQueryValue(modelValue: Any?): JdbcFieldValue {
        return JdbcFieldValue(modelValue, type)
    }
}

internal abstract class BaseSimpleTypePropertyFieldHandler(protected val type: SqlType) : JdbcPropertyMappingHandler {

    override fun getPropertyColumns(property: DatabasePropertyDescription): Map<String, SqlType> {
        return Collections.singletonMap(property.name, type)
    }

    override fun getPropertyIndexes(property: DatabasePropertyDescription, table: DatabaseTableDescription): Map<String, JdbcIndexDescription> {
        return Collections.singletonMap(String.format("%s_%s", table.name, property.name), JdbcIndexDescription(property.name, JdbcIndexType.BTREE))
    }

    override fun getModelPropertyValue(property: DatabasePropertyDescription, jdbcValues: List<Any?>): Any? {
        return jdbcValues[0]
    }



    override fun getJdbcPropertyValue(property: DatabasePropertyDescription, modelValue: Any?): List<Any?> {
        return listOf(modelValue)
    }


}

internal class EntityReferenceFieldHandler : JdbcPropertyMappingHandler {

    override fun getPropertyColumns(property: DatabasePropertyDescription): Map<String, SqlType> {
        return linkedMapOf(property.name to SqlType.STRING,
                "${property.name}ClassName" to SqlType.STRING,
                "${property.name}Caption" to SqlType.STRING)
    }

    override fun getPropertyIndexes(property: DatabasePropertyDescription, table: DatabaseTableDescription): Map<String, JdbcIndexDescription> {
        return linkedMapOf("${table.name}_${property.name}" to JdbcIndexDescription(property.name, JdbcIndexType.BTREE),
                "${table.name}_${property.name}Caption" to JdbcIndexDescription("${property.name}Caption", JdbcIndexType.BTREE))
    }

    override fun getModelPropertyValue(property: DatabasePropertyDescription, jdbcValues: List<Any?>): Any? {
        val uid = jdbcValues[0] as String?
        val refClassName = jdbcValues[1] as String?
        val caption = jdbcValues[2] as String?
        if (uid == null || refClassName == null) {
            return null
        }
        val result = ObjectReference<BaseIdentity>()
        result.caption = caption
        result.uid = uid
        result.type = ReflectionFactory.get().getClass(refClassName)
        return result
    }

    override fun getJdbcPropertyValue(property: DatabasePropertyDescription, modelValue: Any?): List<Any?> {
        @Suppress("UNCHECKED_CAST")
        val ref = modelValue as ObjectReference<BaseIdentity>?
        val result = ArrayList<Any?>()
        result.add(ref?.uid)
        result.add(ref?.type?.qualifiedName)
        result.add(ref?.caption)
        return result
    }

    override fun getSqlQueryValue(modelValue: Any?): JdbcFieldValue {
        return  JdbcFieldValue(if(modelValue is ObjectReference<*>) modelValue.uid else null, SqlType.STRING)
    }


}

internal class EntityReferenceCollectionFieldHandler : JdbcCollectionMappingHandler {

    override fun getCollectionColumns(coll: DatabaseCollectionDescription): Map<String, SqlType> {
        return linkedMapOf(coll.name to SqlType.STRING_ARRAY,
                "${coll.name}ClassName" to SqlType.STRING_ARRAY,
                "${coll.name}Caption" to SqlType.STRING_ARRAY)
    }

    override fun getCollectionIndexes(coll: DatabaseCollectionDescription, table: DatabaseTableDescription): Map<String, JdbcIndexDescription> {
        return linkedMapOf("${table.name}_${coll.name}" to JdbcIndexDescription(coll.name, JdbcIndexType.GIN),
                "${table.name}_${coll.name}Caption" to  JdbcIndexDescription("${coll.name}Caption", JdbcIndexType.GIN))
    }

    @Suppress("UNCHECKED_CAST")
    override fun getModelCollectionValues(coll: DatabaseCollectionDescription, jdbcValues: List<Any?>): List<Any?> {
        val result = ArrayList<Any?>()
        if (jdbcValues[0] == null) {
            return result
        }
        val uids = getStringArray(jdbcValues[0])
        val classes = getStringArray(jdbcValues[1])
        val captions = getStringArray(jdbcValues[2])
        uids.withIndex().forEach { (idx, value) ->
            @Suppress("RemoveExplicitTypeArguments")
            result.add(ObjectReference(ReflectionFactory.get().getClass<BaseIdentity>(classes[idx]), value, if (captions[idx] == "null") null else captions[idx]))
            Unit
        }
        return result
    }

    private fun getStringArray(jdbcValue:Any?): Array<String> {
        if(jdbcValue == null){
            return emptyArray()
        }
        val jdbcArray = jdbcValue as java.sql.Array
        return (jdbcArray.array as Array<Any?>).map { it.toString() }.toTypedArray()

    }

    override fun getJdbcCollectionValues(coll: DatabaseCollectionDescription, modelValues: List<Any?>): List<Any?> {
        val uids = arrayOfNulls<String>(modelValues.size)
        val classes = arrayOfNulls<String>(modelValues.size)
        val captions = arrayOfNulls<String>(modelValues.size)
        modelValues.withIndex().forEach { (idx, value) ->
            if (value is ObjectReference<*>) {
                uids[idx] = value.uid
                classes[idx] = value.type.java.name
                captions[idx] = value.caption ?: "null"
            }
        }
        return arrayListOf(uids, classes, captions)
    }

    override fun getSqlQueryValue(modelValue: Any?): JdbcFieldValue {
        return JdbcFieldValue(
                if (modelValue == null)
                    null
                else
                    (modelValue as ObjectReference<*>).uid,
                SqlType.STRING)
    }

}



object JdbcHandlerUtils {
    private val propertyHandlers = hashMapOf<JdbcPropertyType, JdbcPropertyMappingHandler>()

    private val collectionHandlersMap = hashMapOf<JdbcCollectionType, JdbcCollectionMappingHandler>()

    init {
        propertyHandlers[JdbcPropertyType.LOCAL_DATE_TIME] = DateTimeTypeFieldHandler()
        propertyHandlers[JdbcPropertyType.LOCAL_DATE] = DateTypeFieldHandler()
        propertyHandlers[JdbcPropertyType.ENTITY_REFERENCE] = EntityReferenceFieldHandler()
        propertyHandlers[JdbcPropertyType.ENUM] = EnumTypeFieldHandler()
        propertyHandlers[JdbcPropertyType.STRING] = GenericStringTypeFieldHandler(
                SqlType.STRING)
        propertyHandlers[JdbcPropertyType.TEXT] = GenericStringTypeFieldHandler(
                SqlType.TEXT)
        propertyHandlers[JdbcPropertyType.LONG] = LongTypeFieldHandler()
        propertyHandlers[JdbcPropertyType.INT] = IntTypeFieldHandler()
        propertyHandlers[JdbcPropertyType.BIG_DECIMAL] = BigDecimalTypeFieldHandler()
        propertyHandlers[JdbcPropertyType.BOOLEAN] = BooleanTypeFieldHandler()
        propertyHandlers[JdbcPropertyType.BLOB] = BlobTypeFieldHandler()
        collectionHandlersMap[JdbcCollectionType.ENTITY_REFERENCE] = EntityReferenceCollectionFieldHandler()
        collectionHandlersMap[JdbcCollectionType.ENUM] = EnumCollectionTypeFieldHandler()
        collectionHandlersMap[JdbcCollectionType.STRING] = GenericStringTypeCollectionFieldHandler(SqlType.STRING)
    }

    internal fun getPropertyHandler(propertyType: JdbcPropertyType): JdbcPropertyMappingHandler {
        return propertyHandlers[propertyType]
                ?: throw IllegalArgumentException("no property handler registered for type $propertyType")
    }

    internal fun getCollectionHandler(collectionType: JdbcCollectionType): JdbcCollectionMappingHandler {
        return collectionHandlersMap[collectionType]
                ?: throw IllegalArgumentException("no collection handler registered for type $collectionType")
    }
}