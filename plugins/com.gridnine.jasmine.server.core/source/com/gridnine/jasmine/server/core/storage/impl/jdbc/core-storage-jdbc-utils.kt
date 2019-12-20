/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")
package com.gridnine.jasmine.server.core.storage.impl.jdbc

import com.gridnine.jasmine.server.core.app.Environment
import com.gridnine.jasmine.server.core.model.common.BaseEntity
import com.gridnine.jasmine.server.core.model.domain.*
import com.gridnine.jasmine.server.core.storage.TransactionContext
import com.gridnine.jasmine.server.core.storage.impl.DocumentData
import com.gridnine.jasmine.server.core.storage.search.*
import com.gridnine.jasmine.server.core.utils.TextUtils
import java.math.BigDecimal
import java.sql.*
import javax.sql.DataSource
import kotlin.UnsupportedOperationException

class ValueHolder<D:Any>(initValue: D) {
    var value:D = initValue

}


class JdbcContext(val connection: Connection, val context:TransactionContext)

object JdbcUtils {

        const val AGGREGATED_DATA_COLUMN_NAME = "aggregatedData"

        private val contexts = ThreadLocal<JdbcContext>()

        fun <T : Any> query(sql: String, pss: ((ps:PreparedStatement)->Unit)?, rm: (rs:ResultSet)->T): List<T> {
            val result = arrayListOf<T>()
            executeInTransaction(false) {
                withStatement(sql) {
                    pss?.invoke(it)
                    val rs = it.executeQuery()
                    while (rs.next()) {
                        val row = rm.invoke(rs)
                        result.add(row)
                    }
                }
            }
            return result
        }

        fun update(sql: String) {
            executeInTransaction {
                withStatement(sql) { it.executeUpdate() }
            }
        }
        fun execute(sql: String, commit:Boolean = true, closeConnection: Boolean = true) {
            executeInTransaction(commit, closeConnection) {
                withStatement(sql) { it.executeUpdate() }
            }
        }


        fun update(sql: String, pss: ((ps:PreparedStatement)->Unit)?) {
            executeInTransaction {
                withStatement(sql, pss) { it.executeUpdate() }
            }
        }

        private fun withStatement(sql: String, setter: ((ps:PreparedStatement)->Unit)? = null, callback: (PreparedStatement) -> Unit) {
            val statement = contexts.get().connection.prepareStatement(sql)
            setter?.invoke(statement)
            statement.use { statement2 ->
                callback.invoke(statement2)
            }
        }

        fun executeInTransaction(commit: Boolean = true, closeConnection:Boolean = true, callback: (TransactionContext) -> Unit) {
            val ctx = contexts.get()
            lateinit var connection: Connection
            val owner = ctx == null
            try {
                if (ctx == null) {
                    connection = Environment.getPublished(DataSource::class).connection
                    connection.autoCommit = false
                    contexts.set(JdbcContext(connection, TransactionContext {connection.commit()}))
                }
                callback.invoke(contexts.get()!!.context)
                if (owner && commit) {
                    connection.commit()
                }
            } catch (e: Exception) {
                if (owner) {
                    connection.rollback()
                }
                throw e
            } finally {
                if (owner) {
                    contexts.remove()
                    if (closeConnection) {
                        connection.close()
                    }
                }
            }

        }



    class  AggregatedDataClass:PropertyNameSupport(AGGREGATED_DATA_COLUMN_NAME),StringOperationsSupport

    private val aggregatedDataProp = AggregatedDataClass()

    fun getTableName(className: String): String {
        return className.substring(className.lastIndexOf(".") + 1)
                .toUpperCase()
    }

    internal fun getTableDescriptions(): Map<String, DatabaseTableDescription> {
        val descriptions = hashMapOf<String, DatabaseTableDescription>()
        for (item in DomainMetaRegistry.get().assets.values) {
            val properties = linkedMapOf<String, DatabasePropertyDescription>()
            val collections = linkedMapOf<String, DatabaseCollectionDescription>()
            fromDomain(item, properties, collections)
            properties[BaseEntity.uid] = DatabasePropertyDescription(BaseEntity.uid, JdbcPropertyType.STRING, false, null)
            properties[BaseAsset.modified] = DatabasePropertyDescription(BaseAsset.modified, JdbcPropertyType.LOCAL_DATE_TIME, true, null)
            properties[BaseAsset.modifiedBy] = DatabasePropertyDescription(BaseAsset.modifiedBy,
                    JdbcPropertyType.STRING, false, null)
            properties[AGGREGATED_DATA_COLUMN_NAME] = DatabasePropertyDescription(AGGREGATED_DATA_COLUMN_NAME,
                    JdbcPropertyType.TEXT, true, null)
            val tableName = getTableName(item.id)
            descriptions[tableName] = DatabaseTableDescription(tableName, properties, collections)
        }
        for (item in DomainMetaRegistry.get().indexes.values) {
            val properties = linkedMapOf<String, DatabasePropertyDescription>()
            val collections = linkedMapOf<String, DatabaseCollectionDescription>()
            fromDomain(item, properties, collections)
            properties[BaseEntity.uid] = DatabasePropertyDescription(BaseEntity.uid, JdbcPropertyType.STRING, false, null)
            properties[BaseIndex.document] = DatabasePropertyDescription(BaseIndex.document, JdbcPropertyType.ENTITY_REFERENCE, true, null)
            properties[AGGREGATED_DATA_COLUMN_NAME] = DatabasePropertyDescription(AGGREGATED_DATA_COLUMN_NAME,
                    JdbcPropertyType.TEXT, true, null)
            properties[BaseIndex.navigationKey] = DatabasePropertyDescription(BaseIndex.navigationKey,
                    JdbcPropertyType.STRING, false, null)

            val tableName = getTableName(item.id)
            descriptions[tableName] = DatabaseTableDescription(tableName, properties, collections)
        }
        for (item in DomainMetaRegistry.get().documents.values) {
            val properties = linkedMapOf<String, DatabasePropertyDescription>()
            val collections = linkedMapOf<String, DatabaseCollectionDescription>()
            properties[BaseEntity.uid] = DatabasePropertyDescription(BaseEntity.uid, JdbcPropertyType.STRING, true, null)
            properties[DocumentData.data] = DatabasePropertyDescription(DocumentData.data, JdbcPropertyType.BYTE_ARRAY, false, null)
            val tableName = getTableName(item.id)
            descriptions[tableName] = DatabaseTableDescription(tableName, properties, collections)
        }
        return descriptions
    }

    private fun fromDomain(item: BaseIndexDescription, properties: LinkedHashMap<String, DatabasePropertyDescription>, collections: LinkedHashMap<String, DatabaseCollectionDescription>) {
        item.properties.values.forEach {
            properties[it.id] = DatabasePropertyDescription(it.id, toJdbcPropertyType(it.type), true, it.className)
        }
        item.collections.values.forEach {
            collections[it.id] = DatabaseCollectionDescription(it.id, toJdbcPropertyType(it.elementType), true, it.elementClassName)
        }

    }

    private fun toJdbcPropertyType(type : DatabasePropertyType):JdbcPropertyType{
        return when(type){
            DatabasePropertyType.TEXT -> JdbcPropertyType.TEXT
            DatabasePropertyType.BIG_DECIMAL -> JdbcPropertyType.BIG_DECIMAL
            DatabasePropertyType.BOOLEAN -> JdbcPropertyType.BOOLEAN
            DatabasePropertyType.ENTITY_REFERENCE -> JdbcPropertyType.ENTITY_REFERENCE
            DatabasePropertyType.ENUM -> JdbcPropertyType.ENUM
            DatabasePropertyType.INT -> JdbcPropertyType.INT
            DatabasePropertyType.LOCAL_DATE -> JdbcPropertyType.LOCAL_DATE
            DatabasePropertyType.LOCAL_DATE_TIME -> JdbcPropertyType.LOCAL_DATE_TIME
            DatabasePropertyType.LONG -> JdbcPropertyType.LONG
            DatabasePropertyType.STRING -> JdbcPropertyType.STRING
        }
    }
    private fun toJdbcPropertyType(type : DatabaseCollectionType):JdbcPropertyType{
        return when(type){
            DatabaseCollectionType.STRING -> JdbcPropertyType.STRING
            DatabaseCollectionType.ENUM -> JdbcPropertyType.ENUM
            DatabaseCollectionType.ENTITY_REFERENCE -> JdbcPropertyType.ENTITY_REFERENCE
        }
    }



    fun getValue(rs: ResultSet, idx: Int, sqlType: SqlType): Any? {
        return when (sqlType) {
            SqlType.BLOB -> rs.getBytes(idx)
            SqlType.DATE -> rs.getDate(idx)
            SqlType.TIMESTAMP -> rs.getTimestamp(idx)
            SqlType.LONG -> rs.getLong(idx)
            SqlType.INT -> rs.getInt(idx)
            SqlType.BIG_DECIMAL -> rs.getBigDecimal(idx)
            SqlType.BOOLEAN -> rs.getBoolean(idx)
            SqlType.TEXT, SqlType.STRING -> rs.getString(idx)
        }
    }

    fun setValue(ps: PreparedStatement, idx: Int, value: Any?,
                 sqlType: SqlType) {
        when (sqlType) {
            SqlType.BLOB -> ps.setBytes(idx, value as ByteArray?)
            SqlType.DATE -> ps.setDate(idx, value as Date?)
            SqlType.TIMESTAMP -> ps.setTimestamp(idx, value as Timestamp?)
            SqlType.LONG -> ps.setLong(idx, (value as Long).toLong())
            SqlType.INT -> ps.setInt(idx, (value as Int).toInt())
            SqlType.BIG_DECIMAL -> ps.setBigDecimal(idx, value as BigDecimal?)
            SqlType.BOOLEAN -> ps.setBoolean(idx, value as Boolean)
            SqlType.TEXT, SqlType.STRING -> ps.setString(idx, value as String?)
        }

    }

    fun prepareOrderPart(orders: Map<String, SortOrder>): String {
        return if(orders.entries.isEmpty()) "" else " ORDER BY ${orders.entries.joinToString(", ") { "${it.key} ${it.value.name}" }}"
    }

    fun prepareLimitPart(query: SearchQuery): String {
        return (if(query.limit > 0) " LIMIT ${query.limit}" else "") + if(query.offset > 0) " OFFSET ${query.offset}" else ""
    }

    internal fun prepareWherePart(crits: List<SearchCriterion>, freeTextPattern: String?,
                                  descr: DatabaseTableDescription): WherePartData {
        val criterions = arrayListOf<SearchCriterion>()
        criterions.addAll(crits)
        if (!TextUtils.isBlank(freeTextPattern)) {
            for (ptt in freeTextPattern!!.split(" ")) {
                if (!TextUtils.isBlank(ptt)) {
                    criterions.add(like(aggregatedDataProp, "%${ptt.toLowerCase()}%"))
                }
            }
        }
        if (criterions.isEmpty()) {
            return WherePartData(emptyList(), "")
        }
        val values = ArrayList<FieldValue>()
        val sql = StringBuilder()
        val indexOfSQL = ValueHolder(0)
        prepareWherePartInternal(sql, values, indexOfSQL, criterions, descr)
        return WherePartData(values,"WHERE $sql")
    }

    private fun makeAndToken(currentSQLIndex: Int): String {
        return if (currentSQLIndex > 0) " AND " else ""
    }

    private fun prepareWherePartInternal(sql: StringBuilder,
                                         values: MutableList<FieldValue>, indexOfSQL: ValueHolder<Int>,
                                         criterions: List<SearchCriterion>,
                                         descr: DatabaseTableDescription) {

        for (criterion in criterions) {
            if (criterion is NotBetweenCriterion) {
                val currentSQLIndex = indexOfSQL.value
                val subQuery = "${makeAndToken(currentSQLIndex)}NOT(${criterion.property} BETWEEN ? AND ?)"
                sql.insert(currentSQLIndex, subQuery)
                indexOfSQL.value = currentSQLIndex + subQuery.length
                values.add(
                        getSQLFieldValue(criterion.lo, descr, criterion.property))
                values.add(
                        getSQLFieldValue(criterion.hi, descr, criterion.property))
                continue
            }
            if (criterion is BetweenCriterion) {
                val currentSQLIndex = indexOfSQL.value
                val subQuery = "${makeAndToken(currentSQLIndex)}${criterion.property} BETWEEN ? AND ?"
                sql.insert(currentSQLIndex, subQuery)
                indexOfSQL.value = currentSQLIndex + subQuery.length
                values.add(
                        getSQLFieldValue(criterion.lo, descr, criterion.property))
                values.add(
                        getSQLFieldValue(criterion.hi, descr, criterion.property))
                continue
            }
            if (criterion is CheckCriterion) {
                when (criterion.check) {
                    CheckCriterion.Check.IS_EMPTY -> {
                        addSimpleCriterion(sql, values, criterion.property, "=", "",
                                indexOfSQL, descr)
                    }
                    CheckCriterion.Check.NOT_EMPTY -> {
                        addSimpleCriterion(sql, values, criterion.property, "!=", "",
                                indexOfSQL, descr)
                    }
                    CheckCriterion.Check.IS_NULL -> {
                        val currentSQLIndex = indexOfSQL.value
                        val subQuery = "${makeAndToken(currentSQLIndex)}${criterion.property} IS NULL"
                        sql.insert(currentSQLIndex, subQuery)
                        indexOfSQL.value = currentSQLIndex + subQuery.length
                    }
                    CheckCriterion.Check.IS_NOT_NULL -> {
                        val currentSQLIndex = indexOfSQL.value
                        val subQuery = "${makeAndToken(currentSQLIndex)}${criterion.property} IS NOT NULL"
                        sql.insert(currentSQLIndex, subQuery)
                        indexOfSQL.value = currentSQLIndex + subQuery.length
                    }
                }
                continue
            }
            if (criterion is InCriterion) {
                val currentSQLIndex = indexOfSQL.value
                val subQuery = StringBuilder("${makeAndToken(currentSQLIndex)}${criterion.property} IN(")
                criterion.objects.withIndex().forEach { (idx, value) ->
                    if (idx > 0) {
                        subQuery.append(", ")
                    }
                    subQuery.append("?")
                    values.add(getSQLFieldValue(value, descr,
                            criterion.property))
                }
                subQuery.append(")")
                sql.insert(currentSQLIndex, subQuery.toString())
                indexOfSQL.value = currentSQLIndex + subQuery.length
                continue
            }
            if (criterion is NotCriterion) {
                val currentSQLIndex = indexOfSQL.value
                val subSQL = StringBuilder()
                val subIndexOfSql = ValueHolder(
                        Integer.valueOf(0))
                prepareWherePartInternal(subSQL, values, subIndexOfSql,
                        listOf(criterion.criterion), descr)
                sql.insert(currentSQLIndex, "${makeAndToken(currentSQLIndex)}$subSQL")
                indexOfSQL.value = currentSQLIndex + subSQL.length
                continue
            }
            if (criterion is SimpleCriterion) {
                when (criterion.operation) {
                    SimpleCriterion.Operation.EQ -> {
                        addSimpleCriterion(sql, values, criterion.property, "=",
                                criterion.value, indexOfSQL, descr)
                    }
                    SimpleCriterion.Operation.GE -> {
                        addSimpleCriterion(sql, values, criterion.property, ">=",
                                criterion.value, indexOfSQL, descr)
                    }
                    SimpleCriterion.Operation.GT -> {
                        addSimpleCriterion(sql, values, criterion.property, ">",
                                criterion.value, indexOfSQL, descr)
                    }
                    SimpleCriterion.Operation.LIKE -> {
                        addSimpleCriterion(sql, values, criterion.property, "LIKE",
                                criterion.value, indexOfSQL, descr)
                    }
                    SimpleCriterion.Operation.ILIKE -> {
                        addSimpleCriterion(sql, values, criterion.property, "ILIKE",
                                criterion.value, indexOfSQL, descr)
                    }
                    SimpleCriterion.Operation.LE -> {
                        addSimpleCriterion(sql, values, criterion.property, "<=",
                                criterion.value, indexOfSQL, descr)
                    }
                    SimpleCriterion.Operation.LT -> {
                        addSimpleCriterion(sql, values, criterion.property, "<",
                                criterion.value, indexOfSQL, descr)
                    }
                    SimpleCriterion.Operation.NE -> {
                        addSimpleCriterion(sql, values, criterion.property, "!=",
                                criterion.value, indexOfSQL, descr)
                    }
                    SimpleCriterion.Operation.CONTAINS -> {
                        addSimpleCriterion(sql, values, criterion.property, "LIKE",
                                "%|" + criterion.value + "|%", indexOfSQL, descr)
                    }
                    SimpleCriterion.Operation.ICONTAINS -> {
                        addSimpleCriterion(sql, values, criterion.property, "ILIKE",
                                "%|" + criterion.value + "|%", indexOfSQL, descr)
                    }
                }
                continue
            }
            if (criterion is JunctionCriterion) {
                val operation = if (criterion.disjunction) "OR" else "AND"
                val currentSQLIndex = indexOfSQL.value
                val subSQL = StringBuilder("${makeAndToken(currentSQLIndex)}(")
                criterion.criterions.withIndex().forEach { (idx, crit) ->
                    if (idx > 0) {
                        subSQL.append(" $operation ")
                    }
                    val subSubSQL = StringBuilder()
                    val subIndexOfSql = ValueHolder(0)
                    prepareWherePartInternal(subSubSQL, values, subIndexOfSql,
                            listOf(crit),
                            descr)
                    subSQL.append(subSubSQL.toString())
                }
                subSQL.append(")")
                sql.insert(currentSQLIndex, subSQL.toString())
                indexOfSQL.value = currentSQLIndex + subSQL.length
                continue
            }
        }
    }


    private fun addSimpleCriterion(sql: StringBuilder,
                                   values: MutableList<FieldValue>, property: String,
                                   operation: String, value: Any,
                                   indexOfSQL: ValueHolder<Int>,
                                   descr: DatabaseTableDescription) {
        val currentSQLIndex = indexOfSQL.value
        val subQuery = "${makeAndToken(currentSQLIndex)}${property} $operation ?"
        sql.insert(currentSQLIndex, subQuery)
        indexOfSQL.value = currentSQLIndex + subQuery.length
        values.add(getSQLFieldValue(value, descr, property))
    }

    private fun getSQLFieldValue(value: Any,
                                 descr: DatabaseTableDescription, propertyName: String): FieldValue {
        val type = if (AGGREGATED_DATA_COLUMN_NAME.equals(propertyName, true)) {
            JdbcPropertyType.TEXT
        } else {
            val coll = descr.collections[propertyName]
            coll?.elementType ?: (descr.properties[propertyName] ?: error("")).type
        }
        return JdbcHandlerUtils.getHandler(type).getSqlQueryValue(value)
    }


    internal class WherePartData(val values: List<FieldValue>, val sql: String)

    fun prepareProjectionSelectPart(query: ProjectionQuery): String {
        val selectProperties = ArrayList<String>()
        for (proj in query.projections) {
            if (proj is SimpleProjection) {
                when (proj.operation) {
                    ProjectionOperation.COUNT -> selectProperties.add("count(${proj.property})")
                    else -> throw UnsupportedOperationException("${proj.operation} is not supported")
                }
            } else {
                throw Exception("only simple projections supported")
            }
        }
        return selectProperties.joinToString (", ")
    }

    fun prepareProjectionGroupByPart(query: ProjectionQuery): String {
        val groupByProperties = ArrayList<String>()
        for (proj in query.projections) {
            if (proj is SimpleProjection) {
                when (proj.operation) {
                    ProjectionOperation.GROUP -> groupByProperties.add(proj.property)
                    else ->{}
                }
            }
        }
        return groupByProperties.joinToString (", ")
    }


}