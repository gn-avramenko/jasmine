/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("UNCHECKED_CAST")

package com.gridnine.jasmine.server.core.storage


import com.gridnine.jasmine.common.core.lock.LockUtils
import com.gridnine.jasmine.common.core.meta.DomainMetaRegistry
import com.gridnine.jasmine.common.core.model.*
import com.gridnine.jasmine.common.core.model.EqualitySupport
import com.gridnine.jasmine.common.core.serialization.SerializationProvider
import com.gridnine.jasmine.common.core.storage.*
import com.gridnine.jasmine.common.core.utils.IoUtils
import com.gridnine.jasmine.server.core.model.l10n.CoreServerL10nMessagesFactory
import com.gridnine.jasmine.common.core.storage.SearchQuery
import com.gridnine.jasmine.common.core.storage.searchQuery
import com.gridnine.jasmine.common.core.utils.AuthUtils
import com.nothome.delta.Delta
import com.nothome.delta.GDiffPatcher
import com.nothome.delta.GDiffWriter
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream
import kotlin.reflect.KClass

class StorageImpl : Storage {

    private val patcher = GDiffPatcher()
    private val delta = Delta()

    override fun <D : BaseDocument> loadDocument(ref: ObjectReference<D>?, ignoreCache: Boolean): D? {
        return ref?.uid?.let { loadDocument(ref.type, it, ignoreCache) }
    }

    override fun <D : BaseDocument> loadDocument(cls: KClass<D>, uid: String, ignoreCache: Boolean): D? {
        return loadDocument(cls, uid, ignoreCache, StorageRegistry.get().getAdvices(), 0)
    }

    override fun <D : BaseDocument> loadDocumentVersion(cls: KClass<D>, uid: String, version: Int): D? {
        return loadDocumentVersion(cls, uid, version, StorageRegistry.get().getAdvices(), 0)
    }

    private fun <D : BaseDocument> loadDocumentVersion(cls: KClass<D>, uid: String, version: Int, advices: List<StorageAdvice>, idx: Int): D? {
        if (idx == advices.size) {
            val documentData = Database.get().loadDocumentWrapper(cls, uid)
                    ?: throw Xeption.forDeveloper("unable to load document with uid $uid")
            var result = IoUtils.gunzip(documentData.content)
            for (vrs in documentData.metadata.version - 1 downTo version) {
                val versionData = Database.get().loadDocumentVersion(cls, uid, vrs)
                result = versionData.use {
                    val os = ByteArrayOutputStream()
                    patcher.patch(result, GZIPInputStream(it.streamProvider()), os)
                    os.toByteArray()
                }
            }
            return SerializationProvider.get().deserialize(cls, ByteArrayInputStream(result))
        }
        return advices[idx].onLoadDocumentVersion(cls, uid, version) { cls2, uid2, version2 ->
            loadDocumentVersion(cls2, uid2, version2, advices, idx + 1)
        }
    }

    private fun <D : BaseDocument> loadDocument(cls: KClass<D>, uid: String, ignoreCache: Boolean, advices: List<StorageAdvice>, idx: Int): D? {
        if (idx == advices.size) {
            return Database.get().loadDocument(cls, uid)?.use {
                SerializationProvider.get().deserialize(cls, GZIPInputStream(it.streamProvider()))
            }
        }
        return advices[idx].onLoadDocument(cls, uid, ignoreCache) { cls2, uid2, ignoreCacheInt2 ->
            loadDocument(cls2, uid2, ignoreCacheInt2, advices, idx + 1)
        }
    }

    override fun <D : BaseIdentity> getVersionsMetadata(cls: KClass<D>, uid: String): List<VersionMetadata> {
        return getVersionsMetadata(cls, uid, StorageRegistry.get().getAdvices(), 0)
    }

    private fun <D : BaseIdentity> getVersionsMetadata(cls: KClass<D>, uid: String, advices: List<StorageAdvice>, idx: Int): List<VersionMetadata> {
        if (idx == advices.size) {
            return Database.get().getVersionsMetadata(cls, uid)
        }
        return advices[idx].onGetVersionsMetadata(cls, uid) { cls2, uid2 ->
            getVersionsMetadata(cls2, uid2, advices, idx + 1)
        }
    }

    override fun <D : BaseDocument, I : BaseIndex<D>, E> findUniqueDocumentReference(index: KClass<I>, property: E, propertyValue: Any?): ObjectReference<D>? where E : PropertyNameSupport, E : EqualitySupport {
        return findUniqueDocumentReference(index, property, propertyValue, StorageRegistry.get().getAdvices(), 0)
    }

    private fun <D : BaseDocument, I : BaseIndex<D>, E> findUniqueDocumentReference(index: KClass<I>, property: E, propertyValue: Any?, advices: List<StorageAdvice>, idx: Int): ObjectReference<D>? where E : PropertyNameSupport, E : EqualitySupport {
        if (idx == advices.size) {
            val query = searchQuery {
                select(property)
                where {
                    if (propertyValue != null) {
                        eq(property, propertyValue)
                    } else {
                        isNull(property)
                    }
                }
            }
            val lst = Database.get().searchIndex(index, query)
            return when (lst.size) {
                0 -> null
                1 -> lst[0].document
                else -> throw Xeption.forAdmin(CoreServerL10nMessagesFactory.Found_several_recordsMessage(index.qualifiedName, property.name, propertyValue?.toString()))
            }
        }
        return advices[idx].onFindUniqueDocumentReference(index, property, propertyValue) { index2, property2, propertyValue2 ->
            findUniqueDocumentReference(index2, property2, propertyValue2, advices, idx + 1)
        }
    }

    override fun <D : BaseDocument, I : BaseIndex<D>, E> findUniqueDocument(index: KClass<I>, property: E, propertyValue: Any?, ignoreCache: Boolean): D? where E : PropertyNameSupport, E : EqualitySupport {
        return findUniqueDocumentReference(index, property, propertyValue)?.let { loadDocument(it, ignoreCache) }
    }

    override fun <D : BaseDocument> saveDocument(doc: D, createNewVersion: Boolean, comment: String?) {
        wrapWithLock(doc) {
            wrapWithTransaction { ctx ->
                saveDocument(doc, createNewVersion, comment, StorageRegistry.get().getAdvices(), ctx, 0)
            }
        }
    }

    private data class UpdateDocumentContext<D : BaseDocument>(val oldDocumentData: DocumentWrapper<D>?, val previousVersionContentFactory: () -> ByteArray?, val operationContext: OperationContext<D>)

    private fun <D : BaseDocument> getUpdateDocumentContext(doc: D, ctx: TransactionContext): UpdateDocumentContext<D> {
        val oldDocument = Database.get().loadDocumentWrapper(doc::class as KClass<D>, doc.uid)
        var docRevision = doc.getValue(BaseDocument.revision) as Int
        if (docRevision == -1) {
            docRevision = oldDocument!!.revision
        }
        if (oldDocument != null && docRevision != oldDocument.revision) {
            throw Xeption.forDeveloper("revision conflict with document ${doc::class} ${doc.uid}, " +
                    "db revision = ${oldDocument.revision}, operation revision ${doc.getValue(BaseDocument.revision)}")
        }
        var oldObject: D? = null
        val factory: () -> D? = {
            if (oldDocument != null) {
                if (oldObject == null) {
                    val oldObjectContent = IoUtils.gunzip(oldDocument.content)
                    oldObject = SerializationProvider.get().deserialize(doc::class as KClass<D>, ByteArrayInputStream(oldObjectContent))
                }
                oldObject
            } else {
                null
            }
        }
        val globalContext = if (contexts.get() != null) {
            contexts.get()
        } else {
            val gc = GlobalOperationContext(factory, doc, ctx)
            contexts.set(gc)
            gc
        }
        val previousVersionContentFactory: () -> ByteArray? = {
            if (oldDocument?.metadata?.version ?: 0 > 0) {
                Database.get().loadDocumentVersion(doc::class as KClass<D>, doc.uid, oldDocument!!.metadata.version - 1).use {
                    IoUtils.gunzip(it.streamProvider.invoke())
                }
            } else {
                null
            }
        }
        val localContext = LocalOperationContext<D>(factory)
        val operationContext = OperationContext(globalContext, localContext)
        return UpdateDocumentContext(oldDocument, previousVersionContentFactory, operationContext)
    }


    private fun <D : BaseDocument> saveDocument(doc: D, createNewVersion: Boolean, comment: String?, advices: List<StorageAdvice>, ctx: TransactionContext, idx: Int) {
        if (idx == advices.size) {
            val (oldDocument, factory, context) = getUpdateDocumentContext(doc, ctx)
            val updatePreviousVersion = !createNewVersion && oldDocument?.metadata?.version ?: 0 > 0
            val previousVersionContent: ByteArray? = if (updatePreviousVersion) {
                val os = ByteArrayOutputStream()
                ByteArrayInputStream(factory.invoke()!!).use {
                    patcher.patch(oldDocument!!.content, it, os)
                }
                IoUtils.gzip(os.toByteArray())
            } else {
                null
            }
            StorageRegistry.get().getInterceptors().forEach {
                it.onSave(doc, context)
            }
            doc.setValue(BaseDocument.revision, if (oldDocument == null) 0 else oldDocument.revision + 1)
            val baos = ByteArrayOutputStream()
            SerializationProvider.get().serialize(doc, baos)
            val data = baos.toByteArray()
            val now = LocalDateTime.now()
            val baos3 = ByteArrayOutputStream()
            data.inputStream().use { ist ->
                GZIPOutputStream(baos3).use { os ->
                    ist.copyTo(os)
                }
            }
            Database.get().saveDocument(DocumentWrapper(
                    uid = doc.uid,
                    oid = oldDocument?.oid,
                    revision = doc.getValue(BaseDocument.revision) as Int,
                    content = baos3.toByteArray(),
                    metadata = VersionMetadata {
                        modified = now
                        modifiedBy = AuthUtils.getCurrentUser()
                        this.comment = comment
                        version = if(updatePreviousVersion) oldDocument!!.metadata.version else (oldDocument?.let { it.metadata.version + 1 } ?: 0)
                    },
                    cls = doc::class as KClass<D>), oldDocument)
            if ((createNewVersion && oldDocument != null) || (oldDocument != null && updatePreviousVersion)) {
                val metadata = VersionMetadata {
                    modified = oldDocument.metadata.modified
                    modifiedBy = oldDocument.metadata.modifiedBy
                    this.comment = oldDocument.metadata.comment
                    version = if(updatePreviousVersion) oldDocument.metadata.version -1 else oldDocument.metadata.version
                }
                val baos2 = ByteArrayOutputStream()
                GDiffWriter(baos2).use { writer ->
                    delta.compute(data, GZIPInputStream(if (updatePreviousVersion) ByteArrayInputStream(previousVersionContent!!) else oldDocument.content.inputStream()), writer)
                    writer.flush()
                }
                Database.get().saveDocumentVersion(doc::class as KClass<D>, doc.uid, IoUtils.gzip(baos2.toByteArray()), metadata)
            }
            StorageRegistry.get().getIndexHandlers(doc::class).forEach { indexHandler ->

                val wrappers = arrayListOf<IndexWrapper<BaseDocument, BaseIndex<BaseDocument>>>()
                indexHandler.createIndexes(doc).forEach { index ->
                    val sb = StringBuilder()
                    val indexDescription = DomainMetaRegistry.get().indexes[index::class.java.name]
                            ?: throw IllegalStateException("no index found for ${index::class.java.name}")
                    indexDescription.properties.values.forEach idx@{ prop ->
                        val value = index.getValue(prop.id) ?: return@idx
                        if (value is LocalDate || value is LocalDateTime) {
                            return@idx
                        }
                        if (value is Enum<*>) {
                            DomainMetaRegistry.get().enums[value::class.qualifiedName]?.displayNames?.values?.forEach {
                                sb.append(" ${it.toLowerCase()}")
                            }
                            return@idx
                        }
                        sb.append(" " + value.toString().toLowerCase())
                    }
                    index.document = EntityUtils.toReference(doc)
                    wrappers.add(IndexWrapper(sb.toString(), index))
                }
                Database.get().updateIndexes(indexHandler.indexClass, doc.uid, wrappers, oldDocument != null)
            }
            return
        }
        advices[idx].onSaveDocument(doc) { doc2 ->
            saveDocument(doc2, createNewVersion, comment, advices, ctx, idx + 1)
        }
    }

    override fun <D : BaseDocument> deleteDocument(doc: D) {
        wrapWithLock(doc) {
            wrapWithTransaction {
                deleteDocument(doc, StorageRegistry.get().getAdvices(), it, 0)
            }
        }

    }

    override fun <D : BaseDocument> deleteDocument(ref: ObjectReference<D>) {
        loadDocument(ref, ignoreCache = true)?.let { deleteDocument(it) }
    }

    private fun <D : BaseDocument> deleteDocument(doc: D, advices: List<StorageAdvice>, ctx: TransactionContext, idx: Int) {
        if (idx == advices.size) {
            val (readData, _, context) = getUpdateDocumentContext(doc, ctx)
            if (readData == null) {
                throw Xeption.forDeveloper("document is absent in DB")
            }
            StorageRegistry.get().getInterceptors().forEach {
                it.onDelete(doc, context)
            }
            Database.get().deleteDocument(readData)
            StorageRegistry.get().getIndexHandlers(doc::class).forEach {
                Database.get().deleteIndexes(it.indexClass, doc.uid)
            }
            return
        }
        advices[idx].onDeleteDocument(doc) { doc2 ->
            deleteDocument(doc2, advices, ctx, idx + 1)
        }
    }

    private fun <D : BaseDocument, I : BaseIndex<D>> searchDocumentsInternal(cls: KClass<I>, query: SearchQuery, interceptors: List<StorageAdvice>, idx: Int): List<I> {
        if (idx == interceptors.size) {
            return Database.get().searchIndex(cls, query)
        }
        return interceptors[idx].onSearchDocuments(cls, query) { cls2, query2 ->
            searchDocumentsInternal(cls2, query2, interceptors, idx + 1)
        }
    }

    override fun <D : BaseDocument, I : BaseIndex<D>> searchDocuments(cls: KClass<I>, query: SearchQuery): List<I> {
        return searchDocumentsInternal(cls, query, StorageRegistry.get().getAdvices(), 0)
    }

    override fun <D : BaseDocument, I : BaseIndex<D>> searchDocuments(cls: KClass<I>, query: ProjectionQuery): List<Map<String, Any>> {
        return searchDocumentsInternal(cls, query, StorageRegistry.get().getAdvices(), 0)
    }

    private fun <D : BaseDocument, I : BaseIndex<D>> searchDocumentsInternal(cls: KClass<I>, query: ProjectionQuery, interceptors: List<StorageAdvice>, idx: Int): List<Map<String, Any>> {
        if (idx == interceptors.size) {
            return Database.get().searchIndex(cls, query)
        }
        return interceptors[idx].onSearchDocuments(cls, query) { cls2, query2 ->
            searchDocumentsInternal(cls2, query2, interceptors, idx + 1)
        }
    }

    override fun <D : BaseDocument, I : BaseIndex<D>, R : Any> searchDocuments(cls: KClass<I>, query: SimpleProjectionQuery): R {
        val pq = ProjectionQuery()
        pq.projections.add(query.projection)
        pq.criterions.addAll(query.criterions)
        pq.freeText = query.freeText
        val res = searchDocuments(cls, pq)
        @Suppress("UNCHECKED_CAST")
        return when (res.size) {
            1 -> res[0].values.iterator().next() as R
            else -> throw Exception("unsupported result size ${res.size}")
        }
    }

    override fun <A : BaseAsset> loadAsset(ref: ObjectReference<A>?, ignoreCache: Boolean): A? {
        return if (ref == null) null else loadAsset(ref.type, ref.uid, ignoreCache)
    }

    override fun <A : BaseAsset> loadAsset(cls: KClass<A>, uid: String, ignoreCache: Boolean): A? {
        return loadAsset(cls, uid, StorageRegistry.get().getAdvices(), ignoreCache, 0)
    }

    private fun <D : BaseAsset> loadAsset(cls: KClass<D>, uid: String, advices: List<StorageAdvice>, ignoreCache: Boolean, idx: Int): D? {
        if (idx == advices.size) {
            return Database.get().loadAsset(cls, uid)?.asset
        }
        return advices[idx].onLoadAsset(cls, uid, ignoreCache) { cls2, uid2, ignoreCache2 ->
            loadAsset(cls2, uid2, advices, ignoreCache2, idx + 1)
        }
    }


    override fun <A : BaseAsset> loadAssetVersion(cls: KClass<A>, uid: String, version: Int): A? {
        return loadAssetVersion(cls, uid, StorageRegistry.get().getAdvices(), version, 0)
    }

    private fun <D : BaseAsset> loadAssetVersion(cls: KClass<D>, uid: String, advices: List<StorageAdvice>, version: Int, idx: Int): D? {
        if (idx == advices.size) {
            return Database.get().loadAssetVersion(cls, uid, version).use {
                GZIPInputStream(it.streamProvider()).use { gz ->
                    SerializationProvider.get().deserialize(cls, gz)
                }
            }
        }
        return advices[idx].onLoadAssetVersion(cls, uid, version) { cls2, uid2, version2 ->
            loadAssetVersion(cls2, uid2, advices, version2, idx + 1)
        }
    }

    override fun <A : BaseAsset, E> findUniqueAsset(index: KClass<A>, property: E, propertyValue: Any, ignoreCache: Boolean): A? where E : PropertyNameSupport, E : EqualitySupport {
        return findUniqueAsset(index, property, propertyValue, ignoreCache, StorageRegistry.get().getAdvices(), 0)
    }

    private fun <D : BaseAsset, E> findUniqueAsset(index: KClass<D>, property: E, propertyValue: Any?, ignoreCache: Boolean, interceptors: List<StorageAdvice>, idx: Int): D? where E : PropertyNameSupport, E : EqualitySupport {
        if (idx == interceptors.size) {
            val query = searchQuery {
                select(property)
                where {
                    if (propertyValue != null) {
                        eq(property, propertyValue)
                    } else {
                        isNull(property)
                    }
                }
            }
            val lst = Database.get().searchAsset(index, query)
            return when (lst.size) {
                0 -> null
                1 -> lst[0]
                else -> throw Exception("найдено несколько записей ${index.qualifiedName} с ${property.name} = $propertyValue")
            }
        }
        return interceptors[idx].onFindUniqueAsset(index, property, propertyValue, ignoreCache) { index2, property2, propertyValue2, ignoreCache2 ->
            findUniqueAsset(index2, property2, propertyValue2, ignoreCache2, interceptors, idx + 1)
        }
    }

    private data class UpdateAssetContext<A : BaseAsset>(val oldAsset: AssetWrapper<A>?,  val operationContext: OperationContext<A>)

    private fun <A : BaseAsset> getUpdateAssetContext(asset: A, ctx: TransactionContext): UpdateAssetContext<A> {
        val oldAssetWrapperData = Database.get().loadAssetWrapper(asset::class as KClass<A>, asset.uid)
        val oldAsset = oldAssetWrapperData?.asset
        var revision = asset.getValue(BaseAsset.revision) as Int
        if (revision == -1) {
            revision = oldAsset!!.getValue(BaseAsset.revision) as Int
        }
        if (oldAsset != null && revision != oldAsset.getValue(BaseAsset.revision)) {
            throw Xeption.forDeveloper("revision conflict with document ${asset::class} ${asset.uid}, " +
                    "db revision = ${oldAsset.getValue(BaseAsset.revision)}, operation revision $revision")
        }
        val factory: () -> A? = {
            oldAsset
        }
        val globalContext = if (contexts.get() != null) {
            contexts.get()
        } else {
            val gc = GlobalOperationContext(factory, asset, ctx)
            contexts.set(gc)
            gc
        }
        val localContext = LocalOperationContext<A>(factory)
        val operationContext = OperationContext(globalContext, localContext)
        return UpdateAssetContext(oldAssetWrapperData, operationContext)
    }

    override fun <A : BaseAsset> saveAsset(asset: A, createNewVersion: Boolean, comment: String?) {
        wrapWithLock(asset) {
            wrapWithTransaction {
                saveAsset(asset, createNewVersion, comment, StorageRegistry.get().getAdvices(), it, 0)
            }
        }
    }

    private fun <A : BaseAsset> saveAsset(asset: A, createNewVersion: Boolean, comment: String?, advices: List<StorageAdvice>, ctx: TransactionContext, idx: Int) {
        if (idx == advices.size) {
            val (oldAssetReadData, context) = getUpdateAssetContext(asset, ctx)
            val oldAsset = oldAssetReadData?.asset
            StorageRegistry.get().getInterceptors().forEach {
                it.onSave(asset, context)
            }
            asset.setValue(BaseDocument.revision, if (oldAsset == null) 0 else oldAsset.getValue(BaseAsset.revision) as Int + 1)
            val sb = StringBuilder()
            val assetDescription = DomainMetaRegistry.get().assets[asset::class.java.name]
                    ?: throw Xeption.forDeveloper("no asset description found for ${asset::class.java.name}")
            assetDescription.properties.values.forEach idx@{ prop ->
                val value = asset.getValue(prop.id) ?: return@idx
                if (value is LocalDate || value is LocalDateTime) {
                    return@idx
                }
                if (value is Enum<*>) {
                    DomainMetaRegistry.get().enums[value::class.qualifiedName]?.displayNames?.values?.forEach {
                        sb.append(" ${it.toLowerCase()}")
                    }
                    return@idx
                }
                sb.append(" " + value.toString().toLowerCase())
            }
            val now = LocalDateTime.now()
            Database.get().saveAsset(AssetWrapper(sb.toString(), comment, now, AuthUtils.getCurrentUser(), if (oldAssetReadData == null) 0 else oldAssetReadData.version + 1, asset), oldAssetReadData)
            val metadata = VersionMetadata {
                modified = now
                modifiedBy = AuthUtils.getCurrentUser()
                this.comment = comment
                version = oldAssetReadData?.version ?: 0
            }
            if (createNewVersion && oldAssetReadData != null) {
                val baos2 = ByteArrayOutputStream()
                GZIPOutputStream(baos2).use {
                    SerializationProvider.get().serialize(oldAssetReadData.asset, it)
                    it.flush()
                }
                Database.get().saveAssetVersion(asset::class, asset.uid, baos2.toByteArray(), metadata)
            }
            return
        }
        advices[idx].onSaveAsset(asset) { asset2 ->
            saveAsset(asset2, createNewVersion, comment, advices, ctx, idx + 1)
        }
    }

    override fun <A : BaseAsset> deleteAsset(asset: A) {
        wrapWithLock(asset) {
            wrapWithTransaction {
                deleteAsset(asset, it, StorageRegistry.get().getAdvices(), 0)
            }
        }
    }

    override fun <A : BaseAsset> deleteAsset(ref: ObjectReference<A>) {
        loadAsset(ref)?.let { deleteAsset(it) }
    }

    private fun <A : BaseAsset> deleteAsset(asset: A, ctx: TransactionContext, advices: List<StorageAdvice>, idx: Int) {
        if (idx == advices.size) {
            val updateAssetContext = getUpdateAssetContext(asset, ctx)
            StorageRegistry.get().getInterceptors().forEach {
                it.onDelete(asset, updateAssetContext.operationContext)
            }
            Database.get().deleteAsset(asset)
            return
        }
        advices[idx].onDeleteAsset(asset) { asset2 ->
            deleteAsset(asset2, ctx, advices, idx + 1)
        }
    }

    override fun <A : BaseAsset> searchAssets(cls: KClass<A>, query: SearchQuery, ignoreCache: Boolean): List<A> {
        return searchAssets(cls, query, ignoreCache, StorageRegistry.get().getAdvices(), 0)
    }

    private fun <D : BaseAsset> searchAssets(cls: KClass<D>, query: SearchQuery, ignoreCache: Boolean, interceptors: List<StorageAdvice>, idx: Int): List<D> {
        if (idx == interceptors.size) {
            return Database.get().searchAsset(cls, query)
        }
        return interceptors[idx].onSearchAssets(cls, query, ignoreCache) { cls2, query2, ignoreCache2 ->
            searchAssets(cls2, query2, ignoreCache2, interceptors, idx + 1)
        }
    }

    override fun <A : BaseAsset> searchAssets(cls: KClass<A>, query: ProjectionQuery): List<Map<String, Any>> {
        return searchAssets(cls, query, StorageRegistry.get().getAdvices(), 0)
    }

    private fun <D : BaseAsset> searchAssets(cls: KClass<D>, query: ProjectionQuery, interceptors: List<StorageAdvice>, idx: Int): List<Map<String, Any>> {
        if (idx == interceptors.size) {
            return Database.get().searchAsset(cls, query)
        }
        return interceptors[idx].onSearchAssets(cls, query) { cls2, query2 ->
            searchAssets(cls2, query2, interceptors, idx + 1)
        }
    }

    override fun <A : BaseAsset, T : Any> searchAssets(cls: KClass<A>, query: SimpleProjectionQuery): T {
        val pq = ProjectionQuery()
        pq.projections.add(query.projection)
        pq.criterions.addAll(query.criterions)
        val res = searchAssets(cls, pq)
        @Suppress("UNCHECKED_CAST")
        return when (res.size) {
            1 -> res[0].values.iterator().next() as T
            else -> throw Xeption.forDeveloper("unsupported result size ${res.size}")
        }
    }


    override fun executeInTransaction(executable: (TransactionContext) -> Unit) {
        Database.get().executeInTransaction(executable)
    }

    private fun wrapWithLock(obj: Any, function: () -> Unit) {
        val owner = contexts.get() == null
        if (owner) {
            LockUtils.withLock(obj, function)
        } else {
            function.invoke()
        }
    }

    private fun wrapWithTransaction(function: (context: TransactionContext) -> Unit) {
        val owner = contexts.get() == null
        try {
            if (owner) {
                Database.get().executeInTransaction {
                    function.invoke(it)
                }
            } else {
                function.invoke(contexts.get().transactionContext)
            }
        } finally {
            if (owner) {
                contexts.remove()
            }
        }

    }

    companion object {
        internal val contexts = ThreadLocal<GlobalOperationContext>()
    }
}