/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")

package com.gridnine.jasmine.server.core.storage.jdbc

import com.gridnine.jasmine.common.core.app.Environment
import com.gridnine.jasmine.common.core.meta.DomainMetaRegistry
import com.gridnine.jasmine.common.core.model.*
import com.gridnine.jasmine.common.core.storage.*
import com.gridnine.jasmine.server.core.storage.*
import com.gridnine.jasmine.server.core.storage.impl.jdbc.*
import java.io.ByteArrayOutputStream
import java.sql.PreparedStatement
import java.sql.ResultSet
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor


class JdbcDatabase : Database {

    init {
        DatabaseStructureUpdater.updateDbStructure()
    }

    override fun <D : BaseDocument> loadDocument(cls: KClass<D>, uid: String): DocumentReadData? {
        val owner = JdbcUtils.contexts.get() == null
        return JdbcUtils.executeInTransaction(commit = false, closeConnection = false) { ctx ->
            val descr = JdbcUtils.getTableDescription(cls)
            val query = "select ${getColumnNames(descr, emptySet(), emptySet())} " +
                    "from ${descr.name} where ${BaseIdentity.uid} = ?"
            val result = JdbcUtils.loadEntityWithBlob(query, ctx, { ps, _ -> ps.setString(1, uid) }, { rs, ctx2 ->
                fillObject(JdbcDocumentData(), ctx2, rs, descr, emptySet(), emptySet())
            }
            )
            if (result == null) {
                ctx.closeCallbacks.forEach {
                    it.invoke(ctx.connection)
                }
                if (owner) {
                    ctx.connection.close()
                }
                null
            } else {
                DocumentReadData({
                    result.data.inputStreamCallback!!.invoke()
                }, {
                    ctx.closeCallbacks.forEach {
                        it.invoke(ctx.connection)
                    }
                    if (owner) {
                        if (ctx.forceCommit) {
                            ctx.connection.commit()
                        }
                        ctx.connection.close()
                    }
                })
            }
        }
    }

    override fun <D : BaseDocument> loadDocumentWrapper(cls: KClass<D>, uid: String): DocumentWrapper<D>? {
        return JdbcUtils.executeInTransaction(commit = false, closeConnection = true) { ctx ->
            val descr = JdbcUtils.getTableDescription(cls)
            val query = "select ${getColumnNames(descr, emptySet(), emptySet())} " +
                    "from ${descr.name} where ${BaseIdentity.uid} = ?"
            val result = JdbcUtils.loadEntityWithBlob(query, ctx, { ps, _ -> ps.setString(1, uid) }, { rs, ctx2 ->
                fillObject(JdbcDocumentData(), ctx2, rs, descr, emptySet(), emptySet())
            }
            )
            result?.let {
                try {
                    val baos = ByteArrayOutputStream()
                    result.data.inputStreamCallback!!.invoke().use { ins ->
                        ins.copyTo(baos)
                    }
                    DocumentWrapper(uid = uid, cls = cls, content = baos.toByteArray(), revision = it.revision, oid = it.data.oid,
                            metadata = VersionMetadata {
                                modifiedBy = it.modifiedBy
                                modified = it.modified
                                comment = it.comment
                                version = it.version
                            })
                } finally {
                    ctx.closeCallbacks.forEach { f ->
                        f.invoke(ctx.connection)
                    }
                }
            }
        }
    }

    override fun <D : BaseDocument> loadDocumentVersion(cls: KClass<D>, uid: String, version: Int): VersionReadData {
        return loadObjectVersion(cls, uid, version)
    }

    private fun <D:BaseIdentity> loadObjectVersion(cls: KClass<D>, uid: String, version: Int): VersionReadData {
        val owner = JdbcUtils.contexts.get() == null
        return JdbcUtils.executeInTransaction(commit = false, closeConnection = false) { ctx ->
            loadVersion(cls, uid, version, ctx).let { res ->
                VersionReadData({ res.data.inputStreamCallback!!.invoke() }, {
                    ctx.closeCallbacks.forEach {
                        it.invoke(ctx.connection)
                    }
                    if (owner) {
                        if (ctx.forceCommit) {
                            ctx.connection.commit()
                        }
                        ctx.connection.close()
                    }
                })
            }
        }
    }


    override fun <D : BaseDocument, I : BaseIndex<D>> searchIndex(
            cls: KClass<I>, query: SearchQuery): List<I> {
       return searchObjects(cls, query, hashSetOf(IndexWrapper.aggregatedData))
    }

    override fun <D : BaseDocument, I : BaseIndex<D>> searchIndex(
            cls: KClass<I>, query: ProjectionQuery): List<Map<String, Any>> {
        return projectionSearchObjects(cls, query)
    }

    private fun <E : BaseIdentity> projectionSearchObjects(
            cls: KClass<E>, query: ProjectionQuery): List<Map<String, Any>> {
        val descr = JdbcUtils.getTableDescription(cls)
        val wherePart = JdbcUtils.prepareWherePart(query.criterions, query.freeText, descr)

        val selectSql = "select ${JdbcUtils.prepareProjectionSelectPart(query)} from ${descr.name} ${wherePart.sql}${JdbcUtils.prepareProjectionGroupByPart(query)}"
        val searchStatement = createPreparedStatementSetter(wherePart)
        return JdbcUtils.query(selectSql, searchStatement, { rs, _ ->
            val item = hashMapOf<String, Any>()
            var idx = 0
            for (proj in query.projections) {
                idx++
                if (proj is SimpleProjection) {
                    when (proj.operation) {
                        ProjectionOperation.COUNT -> item["key"] = rs.getLong(idx)
                        else -> throw UnsupportedOperationException("${proj.operation} not implemented")
                    }
                    continue
                }
                throw UnsupportedOperationException("$proj not implemented")
            }
            item
        })
    }

    override fun <D : BaseDocument> saveDocument(obj: DocumentWrapper<D>, existingObjectData: DocumentWrapper<D>?) {
        val jdbcData = JdbcDocumentData()
        jdbcData.uid = obj.uid
        jdbcData.version = obj.metadata.version
        jdbcData.modified = obj.metadata.modified
        jdbcData.modifiedBy = obj.metadata.modifiedBy
        jdbcData.comment = obj.metadata.comment
        jdbcData.setValue(BaseDocument.revision, obj.revision)
        jdbcData.data = JdbcBlobWrapper {
            oid = existingObjectData?.oid
            this.data = obj.content
            inputStreamCallback = { throw IllegalArgumentException("unsupported operation") }
        }
        if (existingObjectData != null) {
            val descr = JdbcUtils.getTableDescription(JdbcUtils.getTableName(obj.cls.java.name))
            val columns = getColumns(descr, emptySet(), emptySet())
            val query = "update ${descr.name} set ${columns.joinToString{ "$it = ?" }} " +
                    "where ${BaseIdentity.uid} = ?"
            JdbcUtils.update(query) { ps, ctx ->
                fillPrepareStatement(ctx, ps, jdbcData, descr, emptySet(), emptySet())
                ps.setString(columns.size + 1, obj.uid)
            }
            return
        }
        insert(jdbcData, JdbcUtils.getTableName(obj.cls.java.name))
    }


    override fun <D : BaseDocument> deleteDocument(readData: DocumentWrapper<D>) {
        JdbcUtils.executeInTransaction(commit = true, closeConnection = true) { ctx ->
            readData.oid?.let { JdbcDialect.get().deleteBlob(ctx, it) }
            deleteObject(readData.cls.java.name, readData.uid)
        }
    }

    override fun <A : BaseAsset> loadAsset(cls: KClass<A>, uid: String): AssetWrapper<A>? {
        val tableDescription = JdbcUtils.getTableDescription(cls)
        val propertiesToExclude = hashSetOf(AssetWrapper.comment, AssetWrapper.modified, AssetWrapper.aggregatedData,AssetWrapper.modifiedBy)
        val query = "select ${getColumnNames(tableDescription, emptySet(), propertiesToExclude)} " +
                "from ${tableDescription.name} where ${BaseIdentity.uid} = ?"
        val result = JdbcUtils.query(query, { ps, _ -> ps.setString(1, uid) }, { rs, ctx ->
            fillObject(AssetWrapper(cls.primaryConstructor!!.call()), ctx, rs, tableDescription, emptySet(), propertiesToExclude)
        }
        )
        return if (result.isNotEmpty()) result[0] else null
    }

    private fun <A:BaseIntrospectableObject> fillObject(item: A, ctx: JdbcContext, rs: ResultSet, tableDescription: DatabaseTableDescription, onlyProperties: Set<String>, excludedProperties: Set<String>):A {
        val data = getTableColumnsData(tableDescription, onlyProperties, excludedProperties)
        var idx = 1
        for ((propertyName, value) in data.properties) {
            val values = ArrayList<Any?>()
            for (sqlType in value.values) {
                values.add(JdbcUtils.getValue(ctx, rs, idx, sqlType))
                idx++
            }
            val property = tableDescription.properties[propertyName]
                    ?: throw IllegalArgumentException("no description found for property $propertyName")
            val propertyType = property.type
            val modelValue = JdbcHandlerUtils.getPropertyHandler(propertyType)
                    .getModelPropertyValue(property, values)
            item.setValue(propertyName, modelValue)
        }
        for ((collectionName, value) in data.collections) {
            val values = ArrayList<Any?>()
            for (sqlType in value.values) {
                values.add(JdbcUtils.getValue(ctx, rs, idx, sqlType) ?: "")
                idx++
            }
            val collection = tableDescription.collections[collectionName]
                    ?: throw IllegalArgumentException("no description found for collection $collectionName")
            @Suppress("UNCHECKED_CAST")
            item.getCollection(collectionName)
                    .addAll(JdbcHandlerUtils.getCollectionHandler(collection.elementType).getModelCollectionValues(
                            collection, values) as Collection<Any>)
        }
        return item
    }

    override fun <A : BaseAsset> loadAssetVersion(cls: KClass<A>, uid: String, version: Int): VersionReadData {
       return loadObjectVersion(cls, uid, version)
    }

    private fun deleteObject(className: String, uid: String) {
        val deleteSql = "delete from ${JdbcUtils.getTableName(className)} where uid = ?"
        JdbcUtils.update(deleteSql) { ps, _ -> ps.setString(1, uid) }
    }

    override fun executeInTransaction(executable: (TransactionContext) -> Unit) {
        JdbcUtils.executeInTransaction { ctx ->
            executable.invoke(ctx.context)
        }
    }

    override fun <D : BaseDocument, I : BaseIndex<D>> deleteIndexes(indexClass: KClass<I>, documentUid: String) {
        val className = indexClass.qualifiedName!!
        val deleteSql = "delete from ${JdbcUtils.getTableName(className)} where document = ?"
        JdbcUtils.update(deleteSql) { ps, _ -> ps.setString(1, documentUid) }

    }

    override fun <D : BaseDocument, I : BaseIndex<D>> updateIndexes(cls: KClass<I>, documentUid: String, indexes: List<IndexWrapper<D, I>>, update: Boolean) {
        val descr = JdbcUtils.getTableDescription(cls)
        if (!update) {
            for (idx in indexes) {
                insert(idx,
                        JdbcUtils.getTableName(idx.index::class.java.name))
            }
            return
        }
        val selectSql = "select ${getColumnNames(descr, emptySet(), emptySet())} from ${descr.name} where document = ?"
        val existingIndexes = JdbcUtils.query(selectSql, { ps, _ ->
            ps.setString(1, documentUid)
        }, { rs, ctx ->
            fillObject(IndexWrapper("", cls.primaryConstructor!!.call()), ctx, rs, descr, emptySet(), emptySet())
        })
        val toDelete = existingIndexes.map { it.index.uid }.toMutableSet()
        indexes.forEach { iw ->
            toDelete.remove(iw.index.uid)
            val existing = existingIndexes.find { it.index.uid == iw.index.uid }
            if (existing == null) {
                insert(iw,
                        JdbcUtils.getTableName(iw.index::class.java.name))
                return@forEach
            }
            val changedProperties = linkedMapOf<String, Any?>()
            if (existing.aggregatedData != iw.aggregatedData) {
                changedProperties[JdbcUtils.AGGREGATED_DATA_COLUMN_NAME] = iw.aggregatedData
            }
            val id = DomainMetaRegistry.get().indexes[cls.java.name]!!
            id.properties.keys.forEach { pdi ->
                if (iw.index.getValue(pdi) != existing.index.getValue(pdi)) {
                    changedProperties[pdi] = iw.index.getValue(pdi)
                }
            }
            id.collections.keys.forEach { cdi ->
                val coll = iw.index.getCollection(cdi)
                val ex = existing.index.getCollection(cdi)
                if (!coll.containsAll(ex) || !ex.containsAll(coll)) {
                    changedProperties[cdi] = coll
                }
            }
            if (changedProperties.isNotEmpty()) {
                val columns = getColumns(descr, changedProperties.keys, emptySet())
                val query = "update ${descr.name} set ${columns.joinToString { "$it = ?" }} where uid = ?"
                JdbcUtils.update(query) { ps, ctx ->
                    fillPrepareStatement(ctx, ps, iw, descr, changedProperties.keys, emptySet())
                    ps.setString(columns.size + 1, iw.index.uid)
                }
            }
        }
        if (toDelete.isNotEmpty()) {
            JdbcUtils.update("delete from ${descr.name} where uid in (${toDelete.joinToString { "'${it}'" }})")
        }
    }

    override fun <D : BaseIdentity> getVersionsMetadata(cls: KClass<D>, uid: String): List<VersionMetadata> {
        val versionProps = hashSetOf(JdbcVersionMetadata.version, JdbcVersionMetadata.modified, JdbcVersionMetadata.modifiedBy, JdbcVersionMetadata.comment)
        val versionsTableDescr = JdbcUtils.getTableDescription("${JdbcUtils.getTableName(cls)}Versions")
        val versionsQuery = "select ${getColumnNames(versionsTableDescr, versionProps, emptySet())} from ${versionsTableDescr.name} where objectUid = ?"
        val result = JdbcUtils.query(versionsQuery, { ps, _ ->
            ps.setString(1, uid)
        }, { rs, ctx ->
            fillObject(JdbcVersionMetadata(), ctx, rs, versionsTableDescr, versionProps, emptySet())
        }) as MutableList
        val primaryTableDescr = JdbcUtils.getTableDescription(cls)
        val primaryQuery = "select ${getColumnNames(primaryTableDescr, versionProps, emptySet())} from ${primaryTableDescr.name} where uid = ?"
        result.addAll(JdbcUtils.query(primaryQuery, { ps, _ ->
            ps.setString(1, uid)
        }, { rs, ctx ->
            fillObject(JdbcVersionMetadata(), ctx, rs, primaryTableDescr, versionProps, emptySet())
        }))
        return result.sortedBy { it.version }.map {
            VersionMetadata {
                version = it.version
                comment = it.comment
                modified = it.modified
                modifiedBy = it.modifiedBy
            }
        }
    }


    override fun <D : BaseDocument> saveDocumentVersion(cls: KClass<D>, uid: String, content: ByteArray, metadata: VersionMetadata) {
        val data = JdbcVersionData()
        data.objectUid = uid
        data.version = metadata.version
        data.modifiedBy = metadata.modifiedBy
        data.modified = metadata.modified
        data.comment = metadata.comment

        data.data = JdbcBlobWrapper {
            this.data = content
        }
        insert(data, JdbcUtils.getTableName(cls.java.name) + "Versions")
    }

    override fun <A : BaseAsset> saveAssetVersion(cls: KClass<A>, uid: String, content: ByteArray, metadata: VersionMetadata) {
        val data = JdbcVersionData()
        data.objectUid = uid
        data.version = metadata.version
        data.modifiedBy = metadata.modifiedBy
        data.modified = metadata.modified
        data.comment = metadata.comment

        data.data = JdbcBlobWrapper {
            this.data = content
        }
        insert(data, JdbcUtils.getTableName(cls.java.name) +"Versions")
    }


    private fun createPreparedStatementSetter(
            wherePart: JdbcUtils.WherePartData): (ps: PreparedStatement, ctx: JdbcContext) -> Unit {
        return { ps, ctx ->
            wherePart.values.withIndex().forEach { (n, property) -> JdbcUtils.setValue(ctx, ps, n + 1, property.value, property.sqlType) }
        }
    }

    private fun getColumnNames(descr: DatabaseTableDescription, onlyProperties: Set<String>, excludedProperties: Set<String>): String {
        return getColumns(descr, onlyProperties, excludedProperties).joinToString()
    }
    private fun getColumns(descr: DatabaseTableDescription, onlyProperties: Set<String>, excludedProperties: Set<String>): List<String> {
        val result = arrayListOf<String>()
        val data = getTableColumnsData(descr, onlyProperties, excludedProperties)
        data.properties.forEach{result.addAll(it.value.keys)}
        data.collections.forEach{result.addAll(it.value.keys)}
        return result
    }




    private fun <T : BaseIntrospectableObject> fillPrepareStatement(ctx: JdbcContext, ps: PreparedStatement,
                                                                    obj: T, descr: DatabaseTableDescription, onlyProperties:Set<String>, excludedProperties: Set<String>) {
        val result = TableColumnsData()
        for (property in descr.properties
                .values) {
            if (excludedProperties.contains(property.name) || (onlyProperties.isNotEmpty() && !onlyProperties.contains(property.name))) {
                continue
            }
            result.properties[property.name] = JdbcHandlerUtils.getPropertyHandler(property.type)
                    .getPropertyColumns(property)
        }
        for (coll in descr.collections
                .values) {
            if (excludedProperties.contains(coll.name) || (onlyProperties.isNotEmpty() && !onlyProperties.contains(coll.name))) {
                continue
            }
            result.collections[coll.name] = JdbcHandlerUtils.getCollectionHandler(coll.elementType)
                    .getCollectionColumns(coll)
        }
        var idx = 1
        for ((propertyName, value1) in result.properties) {
            val modelValue = obj.getValue(propertyName)
            val propertyDescr = descr.properties[propertyName]
                    ?: throw IllegalArgumentException("no description found for property $propertyName")
            val propertyType = propertyDescr.type
            val columns = value1.keys.toList()
            val values = JdbcHandlerUtils.getPropertyHandler(propertyType).getJdbcPropertyValue(propertyDescr, modelValue)
            for (n in columns.indices) {
                val column = columns[n]
                val value = values[n]
                JdbcUtils.setValue(ctx, ps, idx, value,
                        value1[column] ?: throw IllegalArgumentException("no value for column $column"))
                idx++
            }

        }
        for ((collectionName, value1) in result.collections) {
            val descr2 = descr.collections[collectionName]
                    ?: throw IllegalArgumentException("no description found for collection $collectionName")
            val propertyType = descr2.elementType
            val columns = value1.keys.toList()
            val values = JdbcHandlerUtils.getCollectionHandler(propertyType)
                    .getJdbcCollectionValues(descr2, obj.getCollection(collectionName))
            for (n in columns.indices) {
                val column = columns[n]
                val value = values[n]
                JdbcUtils.setValue(ctx, ps, idx, value, value1[column]
                        ?: throw IllegalArgumentException("no value found for column $column"))
                idx++
            }
        }
    }

    private fun getTableColumnsData(description: DatabaseTableDescription,
                                    onlyProperties: Set<String>, excludedProperties: Set<String>): TableColumnsData {
        val result = TableColumnsData()
        for (property in description.properties.values) {
            if(excludedProperties.contains(property.name)){
                continue
            }
            if(onlyProperties.isNotEmpty() && !onlyProperties.contains(property.name)){
                continue
            }
            result.properties[property.name] = JdbcHandlerUtils.getPropertyHandler(property.type)
                    .getPropertyColumns(property)
        }
        for (coll in description.collections
                .values) {
            if(excludedProperties.contains(coll.name)){
                continue
            }
            if(onlyProperties.isNotEmpty() && !onlyProperties.contains(coll.name)){
                continue
            }
            result.collections[coll.name] = JdbcHandlerUtils.getCollectionHandler(coll.elementType)
                    .getCollectionColumns(coll)
        }
        return result
    }


    internal class TableColumnsData {
        val properties: MutableMap<String, Map<String, SqlType>> = LinkedHashMap()

        val collections: MutableMap<String, Map<String, SqlType>> = LinkedHashMap()
    }


    private fun <T : BaseIntrospectableObject> insert(obj: T, tableName: String) {
        val descr = JdbcUtils.getTableDescription(tableName)
        val columnsCount = getColumns(descr, emptySet(), emptySet()).size
        val query = "insert into $tableName (${getColumnNames(descr, emptySet(), emptySet())}) values (${(1.rangeTo(columnsCount).joinToString { "?" } )})"
        JdbcUtils.update(query) { ps, ctx -> fillPrepareStatement(ctx, ps, obj, descr, emptySet(), emptySet()) }
    }



    override fun dispose() {
        val dialect = Environment.getPublished(JdbcDialect::class)
        val statement = dialect.shutdownStatement
        if (!statement.isNullOrBlank()) {
            JdbcUtils.execute(statement, commit = false, closeConnection = false)
        }
        super.dispose()
    }

    override fun <A : BaseAsset> loadAssetWrapper(kClass: KClass<A>, uid: String): AssetWrapper<A>? {
        val descr = JdbcUtils.getTableDescription(kClass)

        val query = "select ${getColumnNames(descr, emptySet(), emptySet())} " +
                "from ${descr.name} where ${BaseIdentity.uid} = ?"
        val result = JdbcUtils.query(query, { ps, _ -> ps.setString(1, uid) }, { rs, ctx ->
            fillObject(AssetWrapper(kClass.primaryConstructor!!.call()), ctx, rs, descr, emptySet(), emptySet())
        }
        )
        return if (result.isNotEmpty()) result[0] else null
    }


    override fun <A : BaseAsset> searchAsset(cls: KClass<A>, query: SearchQuery): List<A> {
        return searchObjects(cls, query, hashSetOf(AssetWrapper.aggregatedData, AssetWrapper.modifiedBy, AssetWrapper.modified, AssetWrapper.comment,AssetWrapper.version ))
    }

    private fun <A:BaseIdentity> searchObjects(cls:KClass<A>, query:SearchQuery, excludedProperties:Set<String>):List<A>{
        val descr = JdbcUtils.getTableDescription(cls)
        val wherePart = JdbcUtils.prepareWherePart(query.criterions, query.freeText, descr)
        val selectSql = "select ${getColumnNames(descr, emptySet(), excludedProperties)} " +
                "from ${descr.name} " +
                wherePart.sql +
                JdbcUtils.prepareOrderPart(query.orders, descr) +
                JdbcUtils.prepareLimitPart(query)
        return JdbcUtils.query(selectSql, createPreparedStatementSetter(wherePart), { rs, ctx ->
            fillObject(cls.primaryConstructor!!.call(), ctx, rs, descr, emptySet(), excludedProperties)
        })
    }

    override fun <A : BaseAsset> searchAsset(
            cls: KClass<A>, query: ProjectionQuery): List<Map<String, Any>> {
        return projectionSearchObjects(cls, query)
    }

    override fun <D : BaseAsset> saveAsset(asset: AssetWrapper<D>, readData: AssetWrapper<D>?) {
        if (readData != null) {
            val descr = JdbcUtils.getTableDescription(asset.asset::class)
            val changedProperties = HashSet<String>()
            val addAggregatedData = readData.aggregatedData != asset.aggregatedData
            val id = DomainMetaRegistry.get().assets[asset.asset::class.java.name]!!
            id.properties.keys.forEach { pdi ->
                if (asset.asset.getValue(pdi) != readData.asset.getValue(pdi)) {
                    changedProperties.add(pdi)
                }
            }
            id.collections.keys.forEach { cdi ->
                val coll = asset.asset.getCollection(cdi)
                val ex = readData.asset.getCollection(cdi)
                if (!coll.containsAll(ex) || !ex.containsAll(coll)) {
                    changedProperties.add(cdi)
                }
            }
            changedProperties.add(BaseAsset.revision)
            if (asset.version != readData.version) {
                changedProperties.add(AssetWrapper.version)
            }
            if (asset.comment != readData.comment) {
                changedProperties.add(AssetWrapper.comment)
            }
            if (asset.modified != readData.modified) {
                changedProperties.add(AssetWrapper.modified)
            }
            if (asset.modifiedBy != readData.modifiedBy) {
                changedProperties.add(AssetWrapper.modifiedBy)
            }
            if (asset.asset.getValue(BaseAsset.revision) != asset.asset.getValue(BaseAsset.revision)) {
                changedProperties.add(BaseAsset.revision)
            }
            if(addAggregatedData){
                changedProperties.add(JdbcUtils.AGGREGATED_DATA_COLUMN_NAME)
            }
            val columns = getColumns(descr, changedProperties, emptySet())
            val query = "update ${descr.name} set ${columns.joinToString { "$it = ?" }} where uid = ?"
            JdbcUtils.update(query) { ps, ctx ->
                fillPrepareStatement(ctx, ps, asset, descr, changedProperties, emptySet())
                ps.setString(columns.size + 1, asset.asset.uid)
            }
            return
        }
        insert(asset, JdbcUtils.getTableName(asset.asset::class.qualifiedName!!))
    }

    override fun <D : BaseAsset> deleteAsset(asset: D) {
        deleteObject(asset::class.java.name, asset.uid)
    }



    private fun <E : BaseIdentity> loadVersion(cls: KClass<E>, uid: String, versionNumber: Int, ctx: JdbcContext): JdbcVersionData {
        val descr = JdbcUtils.getTableDescription("${JdbcUtils.getTableName(cls)}Versions")
        val query = "select ${getColumnNames(descr, emptySet(), emptySet())} " +
                "from ${descr.name} where ${JdbcVersionData.objectUid}=? and  ${JdbcVersionData.version}= ?"
        return JdbcUtils.loadEntityWithBlob(query, ctx, { ps, _ ->
            ps.setString(1, uid)
            ps.setInt(2, versionNumber)
        }, { rs, ctx2 ->
            fillObject(JdbcVersionData(), ctx2, rs, descr, emptySet(), emptySet())
        }
        ) ?: throw IllegalArgumentException("unable to find version $versionNumber of class $cls with uid $uid")
    }

}
