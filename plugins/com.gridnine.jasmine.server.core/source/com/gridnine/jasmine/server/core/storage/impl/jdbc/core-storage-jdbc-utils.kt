/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")
package com.gridnine.jasmine.server.core.storage.impl.jdbc

import com.gridnine.jasmine.server.core.app.Environment
import com.gridnine.jasmine.server.core.model.common.BaseIdentity
import com.gridnine.jasmine.server.core.model.common.BaseIntrospectableObject
import com.gridnine.jasmine.server.core.model.domain.*
import com.gridnine.jasmine.server.core.storage.TransactionContext
import com.gridnine.jasmine.server.core.storage.search.*
import org.slf4j.LoggerFactory
import java.math.BigDecimal
import java.sql.*
import java.time.LocalDateTime
import javax.sql.DataSource
import kotlin.reflect.KClass

class ValueHolder<D:Any>(initValue: D) {
    var value:D = initValue

}


class JdbcContext(val connection: Connection, val context:TransactionContext){
    val closeCallbacks:MutableList<(Connection)->Unit> = arrayListOf()
    var forceCommit = false
}

class JdbcVersionMetadata :BaseIdentity(){
    lateinit var modifiedBy:String
    lateinit var modified:LocalDateTime
    var comment:String? = null
    var version:Int =0
    override fun setValue(propertyName: String, value: Any?) {

        if (JdbcVersionMetadata.modified == propertyName) {
            modified = value as LocalDateTime
            return
        }
        if (JdbcVersionMetadata.modifiedBy == propertyName) {
            modifiedBy = value as String
            return
        }
        if (JdbcVersionMetadata.comment == propertyName) {
            comment = value as String?
            return
        }
        if (JdbcVersionMetadata.version == propertyName) {
            version = value as Int
            return
        }
        super.setValue(propertyName, value)
    }

    override fun getValue(propertyName: String): Any? {

        if (JdbcVersionMetadata.modified == propertyName) {
            return modified
        }
        if (JdbcVersionMetadata.modifiedBy == propertyName) {
            return modifiedBy
        }
        if (JdbcVersionMetadata.comment == propertyName) {
            return comment
        }
        if (JdbcVersionMetadata.version == propertyName) {
           return  version
        }
        return super.getValue(propertyName)
    }
    companion object{
        const val modifiedBy = "modifiedBy"
        const val modified = "modified"
        const val comment = "comment"
        const val version = "version"
    }
}

class JdbcDocumentData:BaseIdentity(){
    var revision:Int =0

    var version:Int =0

    lateinit var modified:LocalDateTime

    lateinit var modifiedBy:String

    lateinit var data:JdbcBlobWrapper

    var comment:String? = null

    override fun setValue(propertyName: String, value: Any?) {

        if (JdbcDocumentData.revision == propertyName) {
            revision = value as Int
            return
        }
        if (JdbcDocumentData.data == propertyName) {
            data = value as JdbcBlobWrapper
            return
        }
        if (JdbcDocumentData.version == propertyName) {
            version = value as Int
            return
        }
        if (JdbcDocumentData.modified == propertyName) {
            modified = value as LocalDateTime
            return
        }
        if (JdbcDocumentData.modifiedBy == propertyName) {
            modifiedBy = value as String
            return
        }
        if (JdbcDocumentData.comment == propertyName) {
            comment = value as String?
            return
        }
        super.setValue(propertyName, value)
    }

    override fun getValue(propertyName: String): Any? {

        if (JdbcDocumentData.revision == propertyName) {
            return revision
        }
        if (JdbcDocumentData.data == propertyName) {
            return data
        }
        if (JdbcDocumentData.version == propertyName) {
            return version
        }
        if (JdbcDocumentData.modified == propertyName) {
            return modified
        }
        if (JdbcDocumentData.modifiedBy == propertyName) {
            return modifiedBy
        }
        if (JdbcDocumentData.comment == propertyName) {
            return comment
        }
        return super.getValue(propertyName)
    }

    companion object{
        const val revision = "revision"
        const val version = "version"
        const val data = "data"
        const val modified = "modified"
        const val modifiedBy = "modifiedBy"
        const val comment = "comment"
    }
}

class JdbcVersionData:BaseIntrospectableObject(){
    var version:Int =0
    lateinit var objectUid:String
    lateinit var data:JdbcBlobWrapper

    lateinit var modifiedBy:String
    lateinit var modified:LocalDateTime
    var comment:String? = null

    override fun setValue(propertyName: String, value: Any?) {

        if (JdbcVersionData.version == propertyName) {
            version = value as Int
            return
        }
        if (JdbcVersionData.data == propertyName) {
            data = value as JdbcBlobWrapper
            return
        }
        if (JdbcVersionData.objectUid == propertyName) {
            objectUid = value as String
            return
        }
        if (JdbcVersionData.modifiedBy == propertyName) {
            modifiedBy = value as String
            return
        }
        if (JdbcVersionData.modified == propertyName) {
            modified = value as LocalDateTime
            return
        }
        if (JdbcVersionData.comment == propertyName) {
            comment = value as String?
            return
        }
        super.setValue(propertyName, value)
    }

    override fun getValue(propertyName: String): Any? {

        if (JdbcVersionData.version == propertyName) {
            return version
        }
        if (JdbcVersionData.data == propertyName) {
            return data
        }
        if (JdbcVersionData.objectUid == propertyName) {
            return objectUid
        }
        if (JdbcVersionData.modifiedBy == propertyName) {
            return modifiedBy
        }
        if (JdbcVersionData.modified == propertyName) {
            return modified
        }
        if (JdbcVersionData.comment == propertyName) {
            return comment
        }
        return super.getValue(propertyName)
    }

    companion object{
        const val version = "version"
        const val objectUid = "objectUid"
        const val  data= "data"
        const val modifiedBy ="modifiedBy"
        const val modified ="modified"
        const val comment ="comment"
    }
}
object JdbcUtils {

        private val log = LoggerFactory.getLogger(javaClass)

        private val descriptions = getTableDescriptions()

        const val AGGREGATED_DATA_COLUMN_NAME = "aggregatedData"

        private const val DOCUMENT_CAPTION_COLUMN_NAME = "documentCaption"

        val contexts = ThreadLocal<JdbcContext>()


        fun <T : Any> loadEntityWithBlob(sql: String, ctx:JdbcContext, pss: ((ps: PreparedStatement, ctx: JdbcContext) -> Unit)?, rm: (rs: ResultSet, ctx: JdbcContext) -> T): T? {
            val statement = contexts.get().connection.prepareStatement(sql)
            pss?.invoke(statement, contexts.get())
            val rs = statement.executeQuery()
            ctx.closeCallbacks.add {
                rs.close()
            }
            ctx.closeCallbacks.add {
                statement.close()
            }
            return if(rs.next()) rm.invoke(rs, ctx) else null
        }

        fun <T : Any> query(sql: String, pss: ((ps:PreparedStatement, ctx:JdbcContext)->Unit)?, rm: (rs:ResultSet, ctx:JdbcContext)->T): List<T> {
            val result = arrayListOf<T>()
            executeInTransaction(false) { ctx->
                withStatement(sql) {
                    pss?.invoke(it, ctx)
                    it.executeQuery().use { rs ->
                        while (rs.next()) {
                            val row = rm.invoke(rs, ctx)
                            result.add(row)
                        }
                    }
                }
            }
            return result
        }

        fun update(sql: String) {
            executeInTransaction {_->
                withStatement(sql) { it.executeUpdate() }
            }
        }
        fun execute(sql: String, commit:Boolean = true, closeConnection: Boolean = true) {
            executeInTransaction(commit, closeConnection) {_->
                withStatement(sql) { it.executeUpdate() }
            }
        }


        fun update(sql: String, pss: ((ps:PreparedStatement, ctx:JdbcContext)->Unit)?) {
            executeInTransaction {_ ->
                withStatement(sql, pss) { it.executeUpdate() }
            }
        }

        private fun withStatement(sql: String, setter: ((ps:PreparedStatement, ctx:JdbcContext)->Unit)? = null, callback: (PreparedStatement) -> Unit) {
            val statement = contexts.get().connection.prepareStatement(sql)
            setter?.invoke(statement,contexts.get())
            statement.use { statement2 ->
                callback.invoke(statement2)
            }
        }

        fun<T> executeInTransaction(commit: Boolean = true, closeConnection:Boolean = true, callback: (JdbcContext) -> T) :T {
            var hasError = false
            val owner = contexts.get() == null
            val ctx =  contexts.get()?: kotlin.run {
                val connection = Environment.getPublished(DataSource::class).connection
                connection.autoCommit = false
                val ctx2 = JdbcContext(connection, TransactionContext({
                    connection.commit()
                }))
                contexts.set(ctx2)
                ctx2
            }
            try {
                val result = callback.invoke(ctx)
                if (owner && commit) {
                    ctx.connection.commit()
                    ctx.context.postCommitCallbacks.forEach {
                        try{
                            it.invoke()
                        } catch (e:Exception){
                            log.error("error executing callback ", e)
                        }
                    }
                }
                return result
            } catch (e: Exception) {
                log.error("error executing transaction ", e)
                hasError = true
                if (owner) {
                    ctx.connection.rollback()
                }
                throw e
            } finally {
                if (owner) {
                    contexts.remove()
                    if (hasError || closeConnection) {
                        ctx.connection.close()
                    }
                }
            }

        }



    class  AggregatedDataClass:PropertyNameSupport(AGGREGATED_DATA_COLUMN_NAME),StringOperationsSupport

    private val aggregatedDataProp = AggregatedDataClass()

    fun getTableName(cls: KClass<*>): String {
        return getTableName(cls.java.name)
    }
    fun getTableName(className: String): String {
        return className.substringAfterLast(".")
    }

    internal fun getTableDescriptions(): Map<String, DatabaseTableDescription> {
        val descriptions = hashMapOf<String, DatabaseTableDescription>()
        for (item in DomainMetaRegistry.get().assets.values) {
            val properties = linkedMapOf<String, DatabasePropertyDescription>()
            val collections = linkedMapOf<String, DatabaseCollectionDescription>()
            fromDomain(item, properties, collections)
            properties[BaseIdentity.uid] = DatabasePropertyDescription(BaseIdentity.uid, JdbcPropertyType.STRING, false, null)
            properties[BaseAsset.revision] = DatabasePropertyDescription(BaseAsset.revision,
                    JdbcPropertyType.INT, false, null)
            properties[JdbcVersionMetadata.modified] = DatabasePropertyDescription(JdbcVersionMetadata.modified, JdbcPropertyType.LOCAL_DATE_TIME, false, null)
            properties[JdbcVersionMetadata.modifiedBy] = DatabasePropertyDescription(JdbcVersionMetadata.modifiedBy, JdbcPropertyType.STRING, false, null)
            properties[JdbcVersionMetadata.comment] = DatabasePropertyDescription(JdbcVersionMetadata.comment, JdbcPropertyType.STRING, false, null)
            properties[JdbcVersionMetadata.version] = DatabasePropertyDescription(JdbcVersionMetadata.version, JdbcPropertyType.INT, false, null)
            properties[AGGREGATED_DATA_COLUMN_NAME] = DatabasePropertyDescription(AGGREGATED_DATA_COLUMN_NAME,
                    JdbcPropertyType.TEXT, true, null)
            val tableName = getTableName(item.id)
            descriptions[tableName] = DatabaseTableDescription(tableName, properties, collections)
            descriptions["${tableName}Versions"] = createVersionsTableDescription(tableName)
        }
        for (item in DomainMetaRegistry.get().indexes.values) {
            val properties = linkedMapOf<String, DatabasePropertyDescription>()
            val collections = linkedMapOf<String, DatabaseCollectionDescription>()
            fromDomain(item, properties, collections)
            properties[BaseIdentity.uid] = DatabasePropertyDescription(BaseIdentity.uid, JdbcPropertyType.STRING, false, null)
            properties[BaseIndex.documentField] = DatabasePropertyDescription(BaseIndex.documentField, JdbcPropertyType.ENTITY_REFERENCE, true, null)
            properties[AGGREGATED_DATA_COLUMN_NAME] = DatabasePropertyDescription(AGGREGATED_DATA_COLUMN_NAME,
                    JdbcPropertyType.TEXT, true, null)

            val tableName = getTableName(item.id)
            descriptions[tableName] = DatabaseTableDescription(tableName, properties, collections)
        }
        for (item in DomainMetaRegistry.get().documents.values) {
            val properties = linkedMapOf<String, DatabasePropertyDescription>()
            val collections = linkedMapOf<String, DatabaseCollectionDescription>()
            properties[BaseIdentity.uid] = DatabasePropertyDescription(BaseIdentity.uid, JdbcPropertyType.STRING, true, null)
            properties[BaseDocument.revision] = DatabasePropertyDescription(BaseDocument.revision, JdbcPropertyType.INT, false, null)
            properties[JdbcVersionMetadata.modified] = DatabasePropertyDescription(JdbcVersionMetadata.modified, JdbcPropertyType.LOCAL_DATE_TIME, false, null)
            properties[JdbcVersionMetadata.modifiedBy] = DatabasePropertyDescription(JdbcVersionMetadata.modifiedBy, JdbcPropertyType.STRING, false, null)
            properties[JdbcVersionMetadata.comment] = DatabasePropertyDescription(JdbcVersionMetadata.comment, JdbcPropertyType.STRING, false, null)
            properties[JdbcVersionMetadata.version] = DatabasePropertyDescription(JdbcVersionMetadata.version, JdbcPropertyType.INT, false, null)
            properties[JdbcDocumentData.data] = DatabasePropertyDescription(JdbcDocumentData.data, JdbcPropertyType.BLOB, false, null)
            val tableName = getTableName(item.id)
            descriptions[tableName] = DatabaseTableDescription(tableName, properties, collections)
            descriptions["${tableName}Versions"] = createVersionsTableDescription(tableName)
        }
        return descriptions
    }

    private fun createVersionsTableDescription(tableName: String): DatabaseTableDescription {
        val properties = linkedMapOf<String, DatabasePropertyDescription>()
        val collections = linkedMapOf<String, DatabaseCollectionDescription>()
        properties[JdbcVersionData.objectUid] = DatabasePropertyDescription(JdbcVersionData.objectUid, JdbcPropertyType.STRING, true, null)
        properties[JdbcVersionData.version] = DatabasePropertyDescription(JdbcVersionData.version, JdbcPropertyType.INT, false, null)
        properties[JdbcVersionData.modified] = DatabasePropertyDescription(JdbcVersionData.modified, JdbcPropertyType.LOCAL_DATE_TIME, false, null)
        properties[JdbcVersionData.modifiedBy] = DatabasePropertyDescription(JdbcVersionData.modifiedBy, JdbcPropertyType.STRING, false, null)
        properties[JdbcVersionData.comment] = DatabasePropertyDescription(JdbcVersionData.comment, JdbcPropertyType.STRING, false, null)
        properties[JdbcVersionData.data] = DatabasePropertyDescription(JdbcVersionData.data, JdbcPropertyType.BLOB, false, null)
        return DatabaseTableDescription("${tableName}Versions", properties, collections)
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
    private fun toJdbcPropertyType(type : DatabaseCollectionType):JdbcCollectionType{
        return when(type){
            DatabaseCollectionType.STRING -> JdbcCollectionType.STRING
            DatabaseCollectionType.ENUM -> JdbcCollectionType.ENUM
            DatabaseCollectionType.ENTITY_REFERENCE -> JdbcCollectionType.ENTITY_REFERENCE
        }
    }



    fun getValue(ctx:JdbcContext, rs: ResultSet, idx: Int, sqlType: SqlType): Any? {
        return when (sqlType) {
            SqlType.BLOB -> {
                JdbcDialect.get().getBlobHandler(ctx, rs, idx)
            }
            SqlType.DATE -> rs.getDate(idx)
            SqlType.TIMESTAMP -> rs.getTimestamp(idx)
            SqlType.LONG -> rs.getLong(idx)
            SqlType.INT -> rs.getInt(idx)
            SqlType.BIG_DECIMAL -> rs.getBigDecimal(idx)
            SqlType.BOOLEAN -> rs.getBoolean(idx)
            SqlType.TEXT, SqlType.STRING -> rs.getString(idx)
            SqlType.STRING_ARRAY -> rs.getArray(idx)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun setValue(ctx:JdbcContext, ps: PreparedStatement, idx: Int, value: Any?,
                 sqlType: SqlType) {
        when (sqlType) {
            SqlType.BLOB -> {
                JdbcDialect.get().setBlob(ctx, ps, idx, value as JdbcBlobWrapper)
            }
            SqlType.DATE -> ps.setDate(idx, value as Date?)
            SqlType.TIMESTAMP -> ps.setTimestamp(idx, value as Timestamp?)
            SqlType.LONG -> ps.setLong(idx, (value as Long).toLong())
            SqlType.INT -> ps.setInt(idx, (value as Int).toInt())
            SqlType.BIG_DECIMAL -> ps.setBigDecimal(idx, value as BigDecimal?)
            SqlType.BOOLEAN -> ps.setBoolean(idx, value as Boolean)
            SqlType.TEXT, SqlType.STRING -> ps.setString(idx, value as String?)
            SqlType.STRING_ARRAY -> ps.setArray(idx, ps.connection.createArrayOf("VARCHAR", value as Array<String>))
        }

    }

    internal fun prepareOrderPart(orders: Map<String, SortOrder>, descr: DatabaseTableDescription): String {
        return if(orders.entries.isEmpty()) "" else " ORDER BY ${orders.entries.joinToString(", ") { "${getOrderColumn(it.key, descr)} ${it.value.name}" }}"
    }

    private fun getOrderColumn(key: String, descr: DatabaseTableDescription): String {
        val prop = descr.properties[key]
        if(prop != null){
            if(prop.type == JdbcPropertyType.ENTITY_REFERENCE){
                return "${key}Caption"
            }
        }
        return key
    }

    fun prepareLimitPart(query: SearchQuery): String {
        return (if(query.limit > 0) " LIMIT ${query.limit}" else "") + if(query.offset > 0) " OFFSET ${query.offset}" else ""
    }

    internal fun prepareWherePart(crits: List<SearchCriterion>, freeTextPattern: String?,
                                  descr: DatabaseTableDescription): WherePartData {
        val criterions = arrayListOf<SearchCriterion>()
        criterions.addAll(crits)
        if (!freeTextPattern.isNullOrEmpty()) {
            for (ptt in freeTextPattern.split(" ")) {
                if (ptt.isNotEmpty()) {
                    criterions.add(like(aggregatedDataProp, "%${ptt.toLowerCase()}%"))
                }
            }
        }
        if (criterions.isEmpty()) {
            return WherePartData(emptyList(), "")
        }
        val values = ArrayList<JdbcFieldValue>()
        val sql = StringBuilder()
        val indexOfSQL = ValueHolder(0)
        prepareWherePartInternal(sql, values, indexOfSQL, criterions, descr)
        return WherePartData(values,"WHERE $sql")
    }

    private fun makeAndToken(currentSQLIndex: Int): String {
        return if (currentSQLIndex > 0) " AND " else ""
    }

    private fun prepareWherePartInternal(sql: StringBuilder,
                                         values: MutableList<JdbcFieldValue>, indexOfSQL: ValueHolder<Int>,
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
                                   values: MutableList<JdbcFieldValue>, property: String,
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
                                 descr: DatabaseTableDescription, propertyName: String): JdbcFieldValue {

        return when (propertyName) {
            AGGREGATED_DATA_COLUMN_NAME -> JdbcHandlerUtils.getPropertyHandler(JdbcPropertyType.TEXT).getSqlQueryValue(value)
            DOCUMENT_CAPTION_COLUMN_NAME -> JdbcHandlerUtils.getPropertyHandler(JdbcPropertyType.STRING).getSqlQueryValue(value)
            else -> {
                val coll = descr.collections[propertyName]
                if (coll != null) {
                    JdbcHandlerUtils.getCollectionHandler(coll.elementType).getSqlQueryValue(value)
                } else {
                    val prop = descr.properties[propertyName]
                            ?: throw IllegalArgumentException("no property with name $propertyName")
                    JdbcHandlerUtils.getPropertyHandler(prop.type).getSqlQueryValue(value)
                }
            }
        }
    }


    internal class WherePartData(val values: List<JdbcFieldValue>, val sql: String)

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

    internal fun getTableDescription(tableName: String): DatabaseTableDescription {
        return descriptions[tableName]
                ?: throw IllegalArgumentException("no description found for table $tableName")
    }

    internal fun getTableDescription(cls: KClass<*>): DatabaseTableDescription {
        return getTableDescription(getTableName(cls))
    }

}