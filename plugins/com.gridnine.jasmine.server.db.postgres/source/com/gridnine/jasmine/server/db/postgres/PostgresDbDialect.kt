/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")
package com.gridnine.jasmine.server.db.postgres

import com.gridnine.jasmine.server.core.storage.impl.jdbc.*
import com.gridnine.jasmine.server.core.storage.jdbc.*
import org.postgresql.PGConnection
import org.postgresql.largeobject.LargeObjectManager
import java.sql.PreparedStatement
import java.sql.ResultSet


class PostgresDbDialect : JdbcDialect {


    override val tableNames: Collection<String>
        get() = JdbcUtils.query("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES where TABLE_SCHEMA = ?", { ps, _ -> ps.setString(1, "public") }){ rs, _ ->
            rs.getString(1)
        }

    override val shutdownStatement: String? = null

    override fun getColumnTypes(tableName: String): Map<String, SqlType> {
        val result = LinkedHashMap<String, SqlType>()
        JdbcUtils.query("select COLUMN_NAME, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH from INFORMATION_SCHEMA.COLUMNS where TABLE_NAME = ?",
                { ps, _ -> ps.setString(1, tableName.toLowerCase()) }){ rs, _ ->
            arrayOf(rs.getString(1), rs.getString(2),
                    rs.getString(3))
        }.forEach {
            result[it[0].toLowerCase()] = getType(it[1])
        }
        return result
    }

    override fun getIndexNames(tableName: String): Set<String> {
        return JdbcUtils.query("select i.relname as index_name from pg_class t, pg_class i, pg_index ix, pg_attribute a where t.oid = ix.indrelid and i.oid = ix.indexrelid and a.attrelid = t.oid and a.attnum = ANY(ix.indkey)  and t.relkind = 'r' and t.relname = ?",
                { ps, _ ->
                    ps.setString(1, tableName.toLowerCase())
                }){ rs, _ ->
                rs.getString(1)
        }.toSet()
    }

    override fun getSqlType(value: SqlType): String {
        return when (value) {
            SqlType.DATE -> "DATE"
            SqlType.TIMESTAMP -> "timestamp without time zone"
            SqlType.STRING -> "VARCHAR(255)"
            SqlType.TEXT -> "TEXT"
            SqlType.LONG -> "bigint"
            SqlType.BOOLEAN -> "boolean"
            SqlType.BLOB -> "oid"
            SqlType.INT -> "integer"
            SqlType.BIG_DECIMAL -> "numeric(19,2)"
            SqlType.STRING_ARRAY -> "text[]"
        }
    }
    private fun getType(dataType: String): SqlType {
        if ("character varying".equals(dataType, ignoreCase = true)) {
            return SqlType.STRING
        }
        if ("text".equals(dataType, ignoreCase = true)) {
            return SqlType.STRING
        }
        if ("timestamp without time zone".equals(dataType, ignoreCase = true)) {
            return SqlType.TIMESTAMP
        }
        if ("date".equals(dataType, ignoreCase = true)) {
            return SqlType.DATE
        }
        if ("oid".equals(dataType, ignoreCase = true)) {
            return SqlType.BLOB
        }
        if ("bigint".equals(dataType, ignoreCase = true)) {
            return SqlType.LONG
        }
        if ("integer".equals(dataType, ignoreCase = true)) {
            return SqlType.INT
        }
        if ("numeric".equals(dataType, ignoreCase = true)) {
            return SqlType.BIG_DECIMAL
        }
        if ("boolean".equals(dataType, ignoreCase = true)) {
            return SqlType.BOOLEAN
        }
        throw IllegalStateException("unsupported type: $dataType")
    }

    override fun createDropIndexQuery(tableName: String, index: String): String {
        return String.format("DROP INDEX %s", index.toLowerCase())
    }

    override fun getCreateIndexStatement(tableName: String, indexName: String, description: JdbcIndexDescription): String {
        return when(description.type){
            JdbcIndexType.BTREE -> "CREATE INDEX ${indexName.toLowerCase()} ON ${tableName.toLowerCase()} USING btree(${description.field.toLowerCase()})"
            JdbcIndexType.GIN -> "CREATE INDEX ${indexName.toLowerCase()} ON ${tableName.toLowerCase()} USING gin(${description.field.toLowerCase()})"
            JdbcIndexType.BRIN -> "CREATE INDEX ${indexName.toLowerCase()} ON ${tableName.toLowerCase()} USING brin(${description.field.toLowerCase()})"
        }
    }

    override fun getBlobHandler(ctx: JdbcContext, rs: ResultSet, idx: Int): JdbcBlobWrapper {
        val lobj = ctx.connection.unwrap(PGConnection::class.java).largeObjectAPI
        val oidVal = rs.getLong(idx)
        val obj = lobj.open(oidVal, LargeObjectManager.READ)
        ctx.closeCallbacks.add{
            obj.close()
        }
        return JdbcBlobWrapper{
            oid = oidVal
            inputStreamCallback = {
                obj.inputStream
            }
        }
    }

    override fun setBlob(ctx: JdbcContext, ps: PreparedStatement, idx: Int, jdbcBlobHandler: JdbcBlobWrapper) {
        val lobj = ctx.connection.unwrap(PGConnection::class.java).largeObjectAPI
        jdbcBlobHandler.oid?.let {
            lobj.delete(it)
        }
        val oid = lobj.createLO(LargeObjectManager.WRITE or LargeObjectManager.READ)
        val obj = lobj.open(oid, LargeObjectManager.WRITE)
        obj.write(jdbcBlobHandler.data)
        obj.close()
        ps.setLong(idx, oid)
    }

    override fun deleteBlob(ctx: JdbcContext, oid: Long) {
        val lobj = ctx.connection.unwrap(PGConnection::class.java).largeObjectAPI
        lobj.delete(oid)

    }

}
