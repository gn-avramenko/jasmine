/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")
package com.gridnine.jasmine.server.core.storage.impl.jdbc

import com.gridnine.jasmine.server.core.app.Disposable
import com.gridnine.jasmine.server.core.app.PublishableWrapper
import java.io.InputStream
import java.io.OutputStream
import java.sql.PreparedStatement
import java.sql.ResultSet

enum class SqlType {

    STRING,
    BOOLEAN,
    TEXT,
    DATE,
    TIMESTAMP,
    LONG,
    INT,
    BIG_DECIMAL,
    BLOB,
    STRING_ARRAY
}

enum class JdbcIndexType {
    BTREE,
    GIN,
    BRIN
}
class JdbcBlobWrapper(){
    constructor(init:JdbcBlobWrapper.()->Unit):this(){
        init(this)
    }
    var oid:Int? = null
    var data:ByteArray? = null
    var inputStreamCallback: (()->InputStream)? ={throw IllegalArgumentException("unsupported operation")}
}

internal class JdbcFieldValue(val value: Any?, val sqlType: SqlType)

data class JdbcIndexDescription(val field:String, val type:JdbcIndexType)

interface JdbcDialect:Disposable {

    val tableNames: Collection<String>

    val shutdownStatement: String?

    fun getColumnTypes(tableName: String): Map<String, SqlType>

    fun getIndexNames(tableName: String): Set<String>

    fun getSqlType(value: SqlType): String

    fun createDropIndexQuery(tableName: String, index: String): String

    fun getCreateIndexStatement(tableName: String, indexName:String, description:JdbcIndexDescription):String

    fun getBlobHandler(ctx:JdbcContext, rs:ResultSet, idx:Int):JdbcBlobWrapper

    fun setBlob(ctx: JdbcContext, ps: PreparedStatement, idx: Int, jdbcBlobHandler: JdbcBlobWrapper)

    override fun dispose() {
        wrapper.dispose()
    }

    fun deleteBlob(ctx: JdbcContext, oid: Int)

    companion object {
        private val wrapper = PublishableWrapper(JdbcDialect::class)
        fun get() = wrapper.get()
    }
}

internal interface JdbcPropertyMappingHandler {

    fun getPropertyColumns(property:DatabasePropertyDescription): Map<String, SqlType>

    fun getPropertyIndexes(property:DatabasePropertyDescription, table:DatabaseTableDescription): Map<String, JdbcIndexDescription>

    fun getModelPropertyValue(property:DatabasePropertyDescription, jdbcValues: List<Any?>): Any?

    fun getJdbcPropertyValue(property:DatabasePropertyDescription, modelValue: Any?): List<Any?>

    fun getSqlQueryValue(modelValue: Any?): JdbcFieldValue

}

internal interface JdbcCollectionMappingHandler {

    fun getCollectionColumns(coll:DatabaseCollectionDescription): Map<String, SqlType>

    fun getCollectionIndexes(coll:DatabaseCollectionDescription, table:DatabaseTableDescription): Map<String, JdbcIndexDescription>

    fun getModelCollectionValues(coll:DatabaseCollectionDescription, jdbcValues: List<Any?>): List<Any?>

    fun getJdbcCollectionValues(coll:DatabaseCollectionDescription, modelValues: List<Any?>): List<Any?>

    fun getSqlQueryValue(modelValue: Any?): JdbcFieldValue

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
    BLOB,
    ENTITY_REFERENCE
}

internal enum class JdbcCollectionType {
    STRING,
    ENUM,
    ENTITY_REFERENCE
}

internal class DatabasePropertyDescription(val name: String, val type:JdbcPropertyType, val indexed:Boolean, val className:String?)
internal class DatabaseCollectionDescription(val name: String, val elementType:JdbcCollectionType, val indexed:Boolean, val elementClassName:String?)

internal class DatabaseTableDescription(val name:String, val properties:Map<String, DatabasePropertyDescription>, val collections:Map<String, DatabaseCollectionDescription>)

