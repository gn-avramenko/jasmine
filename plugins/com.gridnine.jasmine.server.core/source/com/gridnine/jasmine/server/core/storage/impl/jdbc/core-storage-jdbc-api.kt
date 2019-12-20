/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")
package com.gridnine.jasmine.server.core.storage.impl.jdbc

enum class SqlType {

    STRING,
    BOOLEAN,
    TEXT,
    DATE,
    TIMESTAMP,
    LONG,
    INT,
    BIG_DECIMAL,
    BLOB
}

internal class FieldValue(val value: Any?, val sqlType: SqlType)

interface JdbcDialect {

    val tableNames: Collection<String>

    val shutdownStatement: String

    fun getColumnTypes(tableName: String): Map<String, SqlType>

    fun getIndexName(tableName: String, columnName: String): String?

    fun getSqlType(value: SqlType): String

    fun createDropIndexQuery(tableName: String, index: String): String

}

internal interface JdbcMappingHandler {

    fun getPropertyColumns(property:DatabasePropertyDescription): Map<String, SqlType>

    fun getPropertyIndexes(property:DatabasePropertyDescription, table:DatabaseTableDescription): Map<String, String>

    fun getModelPropertyValue(property:DatabasePropertyDescription, jdbcValues: List<Any?>): Any?

    fun getJdbcPropertyValue(property:DatabasePropertyDescription, modelValue: Any?): List<Any?>

    fun getCollectionColumns(coll:DatabaseCollectionDescription): Map<String, SqlType>

    fun getCollectionIndexes(coll:DatabaseCollectionDescription, table:DatabaseTableDescription): Map<String, String>

    fun getModelCollectionValues(coll:DatabaseCollectionDescription, jdbcValues: List<Any?>): List<Any?>

    fun getJdbcCollectionValues(coll:DatabaseCollectionDescription, modelValues: List<Any?>): List<Any?>

    fun getSqlQueryValue(modelValue: Any?): FieldValue

}

internal enum class JdbcPropertyType {
    STRING,
    TEXT,
    ENUM,
    LONG,
    INT,
    BIG_DECIMAL,
    LOCAL_DATE_TIME,
    LOCAL_DATE,
    BOOLEAN,
    CLASS,
    BYTE_ARRAY,
    ENTITY_REFERENCE
}

internal class DatabasePropertyDescription(val name: String, val type:JdbcPropertyType, val indexed:Boolean, val className:String?)
internal class DatabaseCollectionDescription(val name: String, val elementType:JdbcPropertyType, val indexed:Boolean, val elementClassName:String?)

internal class DatabaseTableDescription(val name:String, val properties:Map<String, DatabasePropertyDescription>, val collections:Map<String, DatabaseCollectionDescription>)

