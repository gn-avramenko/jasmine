/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")
package com.gridnine.jasmine.server.db.h2

import com.gridnine.jasmine.server.core.storage.impl.jdbc.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.sql.PreparedStatement
import java.sql.ResultSet


class H2dbDialect : JdbcDialect {


    override val tableNames: Collection<String>
        get() =JdbcUtils.query("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES where TABLE_SCHEMA = 'PUBLIC'", null, {it, _ ->it.getString(1)})

    override val shutdownStatement: String
        get() = "SHUTDOWN"

    override fun getColumnTypes(tableName: String): Map<String, SqlType> {
        val columns = JdbcUtils.query(String.format(
                "select COLUMN_NAME, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH from INFORMATION_SCHEMA.COLUMNS where TABLE_NAME = '%s'",
                tableName.toUpperCase()), null,  {it, _ -> arrayOf(it.getString(1), it.getString(2), it.getString(3))})
        val result = LinkedHashMap<String, SqlType>()
        for (item in columns) {
            result[item[0]] = getType(item[1], item[2])
        }
        return result
    }

    override fun getIndexNames(tableName: String): Set<String> {
                return JdbcUtils.query(
                "SELECT index_name from INFORMATION_SCHEMA.INDEXES where TABLE_NAME = ?", {it, _ ->
            it.setString(1, tableName.toUpperCase())
        },{it,_ -> it.getString(1)}).toSet()
    }

    private fun getType(dataType: String, lenght: String): SqlType {
        if ("12".equals(dataType, ignoreCase = true)) {
            return if ("255" == lenght) SqlType.STRING else SqlType.TEXT
        }
        if ("93".equals(dataType, ignoreCase = true)) {
            return SqlType.TIMESTAMP
        }
        if ("91".equals(dataType, ignoreCase = true)) {
            return SqlType.DATE
        }
        if ("2004".equals(dataType, ignoreCase = true)) {
            return SqlType.BLOB
        }
        if ("-5".equals(dataType, ignoreCase = true)) {
            return SqlType.LONG
        }
        if ("16".equals(dataType, ignoreCase = true)) {
            return SqlType.BOOLEAN
        }
        if ("8".equals(dataType, ignoreCase = true)) {
            return SqlType.BIG_DECIMAL
        }
        if ("4".equals(dataType, ignoreCase = true)) {
            return SqlType.INT
        }
        throw IllegalStateException("unsupported type: $dataType")
    }


    override fun getSqlType(value: SqlType): String {
        return when (value) {
            SqlType.DATE ->"DATE"
            SqlType.TIMESTAMP ->"TIMESTAMP"
            SqlType.STRING ->"VARCHAR(255)"
            SqlType.TEXT ->"LONGVARCHAR"
            SqlType.LONG ->"LONG"
            SqlType.INT -> "INT"
            SqlType.BIG_DECIMAL -> "DOUBLE"
            SqlType.BOOLEAN ->"BOOLEAN"
            SqlType.BLOB ->"OID"
            SqlType.STRING_ARRAY ->"ARRAY"
            else ->throw IllegalArgumentException("unsupported  type $value")
        }

    }

    override fun createDropIndexQuery(tableName: String, index: String): String {
        return String.format("DROP INDEX %s", index.toUpperCase())
    }

    override fun getCreateIndexStatement(tableName: String, indexName:String, description: JdbcIndexDescription): String {
        return "CREATE INDEX ${indexName.toUpperCase()} ON ${tableName.toUpperCase()} (${description.field.toUpperCase()})"
    }

    override fun getBlobHandler(ctx: JdbcContext, rs: ResultSet, idx: Int): JdbcBlobWrapper {
//        val bs = rs.getBinaryStream(idx)
//        val baos = ByteArrayOutputStream()
//        bs.copyTo(baos)
        return JdbcBlobWrapper{
            inputStreamCallback = {
                rs.getBinaryStream(idx)
            }
        }
    }

    override fun setBlob(ctx: JdbcContext, ps: PreparedStatement, idx: Int, jdbcBlobHandler: JdbcBlobWrapper) {
       ps.setBinaryStream(idx, ByteArrayInputStream(jdbcBlobHandler.data))
    }

    override fun deleteBlob(ctx: JdbcContext, oid: Long) {
        //noops
    }

}
