/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")
package com.gridnine.jasmine.server.core.storage.impl.jdbc

import com.gridnine.jasmine.server.core.app.Disposable
import com.gridnine.jasmine.server.core.app.Environment
import com.gridnine.jasmine.server.core.model.common.BaseEntity
import com.gridnine.jasmine.server.core.model.domain.BaseAsset
import com.gridnine.jasmine.server.core.model.domain.BaseDocument
import com.gridnine.jasmine.server.core.model.domain.BaseIndex
import com.gridnine.jasmine.server.core.serialization.DomainSerializationUtils
import com.gridnine.jasmine.server.core.storage.AssetWrapper
import com.gridnine.jasmine.server.core.storage.Database
import com.gridnine.jasmine.server.core.storage.IndexWrapper
import com.gridnine.jasmine.server.core.storage.TransactionContext
import com.gridnine.jasmine.server.core.storage.impl.DocumentData
import com.gridnine.jasmine.server.core.storage.search.ProjectionOperation
import com.gridnine.jasmine.server.core.storage.search.ProjectionQuery
import com.gridnine.jasmine.server.core.storage.search.SearchQuery
import com.gridnine.jasmine.server.core.storage.search.SimpleProjection
import com.gridnine.jasmine.server.core.utils.TextUtils
import java.sql.PreparedStatement
import java.sql.ResultSet
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor


class JdbcAdapter : Database, Disposable {

    private val descriptions = JdbcUtils.getTableDescriptions()

    init{
        DatabaseStructureUpdater.updateDbStructure()
    }

    override fun <D : BaseDocument> loadDocument(cls: KClass<D>,
                                        uid: String): D? {
        val data = loadObject(cls, DocumentData::class, uid) ?: return null
        return DomainSerializationUtils.deserialize(cls, data.data)
    }

    override fun <D : BaseDocument, I : BaseIndex<D>> searchIndex(
            cls: KClass<I>, query: SearchQuery): List<I> {
        return searchObjects(cls, query)

    }

    override fun <D : BaseDocument, I : BaseIndex<D>> searchIndex(
            cls: KClass<I>, query: ProjectionQuery): List<Map<String, Any>> {
        return projectionSearchObjects(cls, query)
    }

    private fun <E : BaseEntity> projectionSearchObjects(
            cls: KClass<E>, query: ProjectionQuery): List<Map<String, Any>> {
        val className = cls.qualifiedName!!
        val descr = descriptions[JdbcUtils.getTableName(className)]?:throw IllegalArgumentException("no table with description for id ${JdbcUtils.getTableName(className)}")
        val wherePart = JdbcUtils.prepareWherePart(query.criterions, null,  descr)

        val selectSql = "select ${JdbcUtils.prepareProjectionSelectPart(query)} from ${JdbcUtils.getTableName(className)} ${wherePart.sql}${JdbcUtils.prepareProjectionGroupByPart(query)}"
        val searchStatement = createPreparedStatementSetter(wherePart)
        return JdbcUtils.query(selectSql, searchStatement, {
            val item = hashMapOf<String, Any>()
            var idx = 0
            for (proj in query.projections) {
                idx++
                if (proj is SimpleProjection) {
                    when (proj.operation) {
                        ProjectionOperation.COUNT -> item["key"] = it.getLong(idx)
                        else -> throw UnsupportedOperationException("${proj.operation} not implemented")
                    }
                    continue
                }
                throw UnsupportedOperationException("$proj not implemented")
            }
            item
        })
    }

    override fun <D : BaseDocument> saveDocument(obj: D, update:Boolean) {
        val data = DocumentData()
        data.uid = obj.uid
        data.data = DomainSerializationUtils.serializeToString(obj).toByteArray(charset("utf-8"))
        if (update) {
            update(data, JdbcUtils.getTableName(obj::class.qualifiedName!!), null)
            return
        }
        insert(data, JdbcUtils.getTableName(obj::class.qualifiedName!!), null)
    }


    override fun <D : BaseDocument> deleteDocument(document: D) {
        deleteObject(document::class.java.name, document.uid)
    }

    private fun deleteObject(className: String, uid: String) {
        val deleteSql = "delete from ${JdbcUtils.getTableName(className)} where uid = ?"
        JdbcUtils.update(deleteSql) {it.setString(1, uid)}
    }

    override fun executeInTransaction(executable: (TransactionContext) ->Unit) {

        JdbcUtils.executeInTransaction {
            executable.invoke(it)
        }

    }

    override fun<D : BaseDocument, I : BaseIndex<D>> deleteIndexes(indexClass: KClass<I>, documentUid: String){
        val className = indexClass.qualifiedName!!
        val deleteSql = "delete from ${JdbcUtils.getTableName(className)} where document = ?"
        JdbcUtils.update(deleteSql) {it.setString(1, documentUid)}

    }

    private fun createPreparedStatementSetter(
            wherePart: JdbcUtils.WherePartData): (ps:PreparedStatement)->Unit {
        return {
            wherePart.values.withIndex().forEach{(n , property) ->JdbcUtils.setValue(it, n+1, property.value,property.sqlType)}
        }
    }

    private fun getColumnNames(descr: DatabaseTableDescription, prefferedProperties: Set<String>, includeAggregatedData: Boolean): List<String> {
        val result = arrayListOf<String>()
        val data = getTableColumnsData(descr, prefferedProperties,
                includeAggregatedData)
        val map = LinkedHashMap(
                data.properties)
        map.putAll(data.collections)
        for (item in map.values) {
            result.addAll(item.keys)
        }
        return result
    }

    private fun <T : BaseEntity,P:BaseEntity> createItem(rs: ResultSet, cls: KClass<T>, resultCls:KClass<P>, prefferedProperties: Set<String>): P {
        val item: P = resultCls.primaryConstructor?.call()?:throw IllegalArgumentException("$cls has no primary constructior")
        val className = cls.qualifiedName!!
        val description = descriptions[JdbcUtils.getTableName(className)]?:throw IllegalArgumentException("no description found fo id ${JdbcUtils.getTableName(className)}")
        val data = getTableColumnsData(description, prefferedProperties, false)
        var idx = 1
        for ((propertyName, value) in data.properties) {
            val values = ArrayList<Any?>()
            for (sqlType in value.values) {
                values.add(JdbcUtils.getValue(rs, idx, sqlType))
                idx++
            }
            val property = description.properties[propertyName]?:throw IllegalArgumentException("no description found for property $propertyName")
            val propertyType = property.type
            val modelValue = JdbcHandlerUtils.getHandler(propertyType)
                    .getModelPropertyValue(property, values)
            item.setValue(propertyName, modelValue)
        }
        for ((collectionName, value) in data.collections) {
            if (!queriedProperty(collectionName,
                            prefferedProperties)) {
                continue
            }
            val values = ArrayList<Any?>()
            for (sqlType in value.values) {
                values.add(JdbcUtils.getValue(rs, idx, sqlType)?:"")
                idx++
            }
            val collection = description.collections[collectionName]?:throw IllegalArgumentException("no description found for collection $collectionName")
            @Suppress("UNCHECKED_CAST")
            item.getCollection(collectionName)
                    .addAll(JdbcHandlerUtils.getHandler(collection.elementType).getModelCollectionValues(
                                    collection, values) as Collection<Any>)
        }
        return item

    }

    private fun <T : BaseEntity> fillPrepareStatement(ps: PreparedStatement,
                                                       obj: T, aggregatedData: String?, descr: DatabaseTableDescription,
                                                       fillUid: Boolean) {
        val data = getTableColumnsData(descr,
                emptySet(), aggregatedData != null)
        var idx = 1
        for ((propertyName, value1) in data.properties) {

            val modelValue = if(JdbcUtils.AGGREGATED_DATA_COLUMN_NAME.equals(propertyName, true)) aggregatedData else obj.getValue(propertyName)
            val propertyDescr = descr.properties[propertyName]?:throw IllegalArgumentException("no description found for property $propertyName")
            val propertyType = propertyDescr.type
            val columns = ArrayList(value1.keys)
            val values = JdbcHandlerUtils.getHandler(propertyType).getJdbcPropertyValue(propertyDescr, modelValue)
            for (n in columns.indices) {
                val column = columns[n]
                val value = values[n]
                JdbcUtils.setValue(ps, idx, value,
                        value1[column]?:throw IllegalArgumentException("no value for column $column"))
                idx++
            }

        }
        for ((collectionName, value1) in data.collections) {
            val descr2 = descr.collections[collectionName]?:throw IllegalArgumentException("no description found for collection $collectionName")
            val propertyType = descr2.elementType
            val columns = ArrayList(
                    value1.keys)
            val values = JdbcHandlerUtils.getHandler(propertyType)
                    .getJdbcCollectionValues(descr2, ArrayList(obj.getCollection(collectionName)))
            for (n in columns.indices) {
                val column = columns[n]
                val value = values[n]
                JdbcUtils.setValue(ps, idx, value, value1[column]?:throw IllegalArgumentException("no value found for column $column"))
                idx++
            }
        }
        if (fillUid) {
            ps.setString(idx, obj.uid)
        }

    }

    private fun getTableColumnsData(description: DatabaseTableDescription,
                                     prefferedProperties: Set<String>, includeAggregatedData: Boolean): TableColumnsData {
        val result = TableColumnsData()
        for (property in description.properties
                .values) {
            if (!queriedProperty(property.name,
                            prefferedProperties) && !(property.name == JdbcUtils.AGGREGATED_DATA_COLUMN_NAME && includeAggregatedData)) {
                continue
            }
            result.properties[property.name] = JdbcHandlerUtils.getHandler(property.type)
                    .getPropertyColumns(property)
        }
        for (coll in description.collections
                .values) {
            if (!queriedProperty(coll.name,
                            prefferedProperties)) {
                continue
            }
            result.collections[coll.name ] = JdbcHandlerUtils.getHandler(coll.elementType)
                    .getCollectionColumns(coll)
        }
        return result
    }

    private fun queriedProperty(id: String,
                                prefferedProperties: Set<String>): Boolean {
        if (JdbcUtils.AGGREGATED_DATA_COLUMN_NAME == id) {
            return false
        }
        if (prefferedProperties.isEmpty()) {
            return true
        }
        when(id){
            BaseEntity.uid,BaseAsset.modified,BaseAsset.modifiedBy,BaseAsset.created,BaseAsset.createdBy,BaseIndex.document, BaseIndex.navigationKey -> return true
                else ->{}
        }
        for (item in prefferedProperties) {
            if (item.equals(id, ignoreCase = true)) {
                return true
            }
        }
        return false
    }

    internal class TableColumnsData {
        val properties: MutableMap<String, Map<String, SqlType>> = LinkedHashMap()

        val collections: MutableMap<String, Map<String, SqlType>> = LinkedHashMap()
    }


    private fun <T : BaseEntity> insert(obj: T, tableName: String,
                                        aggregatedData: String?) {
        val descr = descriptions[tableName]?:throw IllegalArgumentException("no description found for table $tableName")
        val questions = ArrayList<String>()
        val columnNames = getColumnNames(descr,
                emptySet(), aggregatedData != null)
        val size = columnNames.size
        for (n in 0 until size) {
            questions.add("?")
        }
        val query = "insert into $tableName (${columnNames.joinToString(", ")}) values (${questions.joinToString(", ")})"
        JdbcUtils.update(query) {fillPrepareStatement(it, obj, aggregatedData, descr, false)}
    }

    private fun <T : BaseEntity> update(obj: T, tableName: String,
                                        aggregatedData: String?) {
        val descr = descriptions[tableName]?:throw IllegalArgumentException("no description found for table $tableName")
        val query ="update $tableName set ${getColumnNames(descr,emptySet(), aggregatedData != null).joinToString (", "){"$it = ?" }} " +
                "where ${BaseEntity.uid} = ?"
        JdbcUtils.update(query) {fillPrepareStatement(it, obj, aggregatedData, descr, true)}
        return
    }


    override fun <D : BaseDocument, I : BaseIndex<D>> updateIndexes(
            indexes: List<IndexWrapper<D, I>>, documentUid: String) {
        for (idx in indexes) {
            insert(idx.index,
                    JdbcUtils.getTableName(idx.index::class.qualifiedName!!),
                    idx.aggregateData)
        }
    }

    override fun dispose() {
        val dialect = Environment.getPublished(JdbcDialect::class)
        if (!TextUtils.isBlank(dialect.shutdownStatement)) {
            JdbcUtils.execute(dialect.shutdownStatement, commit=false, closeConnection = false)
        }
    }

    override  fun <A : BaseAsset> loadAsset(cls: KClass<A>, uid: String): A? {
        return loadObject(cls, cls, uid)
    }

    override fun <A : BaseAsset> searchAsset(cls: KClass<A>, query: SearchQuery): List<A> {
        return searchObjects(cls, query)
    }

    override fun <A : BaseAsset> searchAsset(
            cls: KClass<A>, query: ProjectionQuery): List<Map<String, Any>> {
        return projectionSearchObjects(cls, query)
    }

    override fun <D : BaseAsset> saveAsset(asset: AssetWrapper<D>, update: Boolean) {
        if(update){
            update(asset.asset, JdbcUtils.getTableName(asset.asset::class.qualifiedName!!), asset.aggregateData)
            return
        }
        insert(asset.asset, JdbcUtils.getTableName(asset.asset::class.qualifiedName!!), asset.aggregateData)
    }

    override fun <D : BaseAsset> deleteAsset(asset: D) {
        deleteObject(asset::class.java.name, asset.uid)
    }

    private fun <E : BaseEntity, P : BaseEntity> loadObject(cls: KClass<E>, resultClass:KClass<P>, uid:String): P? {
        val tableName = JdbcUtils.getTableName(cls.java.name)
        val descr = descriptions[tableName]?:throw IllegalArgumentException("no description found for table $tableName")
        val query = "select ${getColumnNames(descr, emptySet(), false).joinToString(", ") } " +
                "from $tableName where ${BaseEntity.uid} = ?"
        val result = JdbcUtils.query(query, {it.setString(1, uid)} , {createItem(it, cls, resultClass, emptySet())})
        return if (result.isEmpty()) null else result[0]
    }

    private fun <E : BaseEntity> searchObjects(cls: KClass<E>,
                                               searchQuery: SearchQuery): List<E> {
        val descr = descriptions[JdbcUtils.getTableName(cls.java.name)]?:throw IllegalArgumentException("no description found for table ${JdbcUtils.getTableName(cls.java.name)}")
        val wherePart = JdbcUtils.prepareWherePart(searchQuery.criterions, searchQuery.freeText, descr)

        val selectSql = "select ${getColumnNames(descr,searchQuery.preferredProperties, false).joinToString(", ") } " +
                "from ${JdbcUtils.getTableName(cls.java.name)} " +
                wherePart.sql+
                JdbcUtils.prepareOrderPart(searchQuery.orders) +
                JdbcUtils.prepareLimitPart(searchQuery)
        return JdbcUtils.query(selectSql, createPreparedStatementSetter(wherePart), {createItem(it, cls, cls, searchQuery.preferredProperties)})
    }
}
