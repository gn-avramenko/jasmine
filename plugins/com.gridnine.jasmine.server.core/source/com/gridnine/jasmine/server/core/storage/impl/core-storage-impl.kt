/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")
package com.gridnine.jasmine.server.core.storage.impl

import com.gridnine.jasmine.server.core.model.common.BaseEntity
import com.gridnine.jasmine.server.core.model.domain.*
import com.gridnine.jasmine.server.core.storage.*
import com.gridnine.jasmine.server.core.storage.search.*
import com.gridnine.jasmine.server.core.utils.EntityUtils
import java.lang.IllegalStateException
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import kotlin.reflect.KClass

class DocumentData : BaseEntity() {

    lateinit var data: ByteArray

    override fun getValue(propertyName: String): Any? {
        return if ("data" == propertyName) {
            data
        } else super.getValue(propertyName)
    }

    override fun setValue(propertyName: String, value: Any?) {
        if ("data" == propertyName) {
            data = value as ByteArray
            return
        }
        super.setValue(propertyName, value)
    }

    companion object{
        const val data = "data"
    }
}

@Suppress("UNCHECKED_CAST")
class StandardStorageImpl: Storage {

    companion object {
        internal val contexts = ThreadLocal<BaseOperationContext>()
    }

    override fun <D : BaseDocument> loadDocument(ref: EntityReference<D>?): D? {
        return ref?.let{loadDocument(ref.type, ref.uid)}
    }

    override fun <D : BaseDocument> loadDocument(cls: KClass<D>, uid: String): D? {
        return loadDocument(cls, uid, StorageRegistry.get().getAdvices(), 0)
    }

    private fun<D : BaseDocument> loadDocument(cls: KClass<D>, uid: String, advices: List<StorageAdvice>, idx:Int): D?{
        if(idx == advices.size){
            return Database.get().loadDocument(cls, uid)
        }
        return advices[idx].onLoadDocument(cls, uid) { cls2, uid2 ->
            loadDocument(cls2, uid2, advices, idx+1)
        }
    }

    override fun <D : BaseDocument, I : BaseIndex<D>, E > findUniqueDocumentReference(index: KClass<I>, property: E, propertyValue: Any?): EntityReference<D>? where E:PropertyNameSupport, E: EqualitySupport {
        return findUniqueDocumentReference(index, property, propertyValue, StorageRegistry.get().getAdvices(), 0)
    }

    override  fun <D : BaseDocument, I : BaseIndex<D>, E >  findUniqueDocument(index: KClass<I>, property: E, propertyValue: Any?): D? where E: PropertyNameSupport, E: EqualitySupport {
        return findUniqueDocumentReference(index,property,propertyValue)?.let { loadDocument(it.type, it.uid) }
    }

    private fun<D : BaseDocument,I : BaseIndex<D>, E> findUniqueDocumentReference(index: KClass<I>, property: E, propertyValue: Any?, advices: List<StorageAdvice>, idx:Int): EntityReference<D>? where E:PropertyNameSupport, E: EqualitySupport {
        if(idx == advices.size){
            val query = searchQuery {
                select(property)
                where {
                    if(propertyValue != null) {
                        eq(property, propertyValue)
                    } else {
                        isNull(property)
                    }
                }
            }
            val lst = Database.get().searchIndex(index, query)
            return when (lst.size){
                0 ->null
                1 ->lst[0].document
                else -> throw Exception("найдено несколько записей ${index.qualifiedName} с ${property.name} = $propertyValue")
            }
        }
        return advices[idx].onFindUniqueDocumentReference(index, property, propertyValue) { index2, property2, propertyValue2 ->
            findUniqueDocumentReference(index2, property2, propertyValue2, advices, idx+1)
        }
    }

    override fun <D : BaseDocument> saveDocument(doc: D) {
        wrapWithContext{
          saveDocument(doc, StorageRegistry.get().getAdvices(), 0)
        }
    }

    private fun wrapWithContext(function: () -> Unit) {
        val owner = contexts.get() == null
        try {
            if(owner) {
                Database.get().executeInTransaction {
                    function.invoke()
                }
            } else {
                function.invoke()
            }
        } finally {
            if (owner) {
                contexts.remove()
            }
        }

    }

    private fun<D : BaseDocument> saveDocument(doc:D, advices: List<StorageAdvice>, idx:Int){
        if(idx == advices.size){
            val oldDoc:D? = Database.get().loadDocument(doc::class, doc.uid)
            val globalContext = contexts.get()
            val context = if(globalContext == null) SaveDocumentOperationContext(null, oldDoc)
            else SaveDocumentOperationContext(globalContext, oldDoc)
            StorageRegistry.get().getInterceptors().forEach {
                it.onSave(doc, context)
            }
            Database.get().saveDocument(doc, oldDoc != null)

                StorageRegistry.get().getIndexHandlers(doc::class).forEach { indexHandler ->
                    if(oldDoc != null) {
                        Database.get().deleteIndexes(indexHandler.indexClass, doc.uid)
                    }
                    val wrappers = arrayListOf<IndexWrapper<BaseDocument, BaseIndex<BaseDocument>>>()
                    indexHandler.createIndexes(doc).forEach { index ->
                        val sb = StringBuilder()
                        val indexDescription =DomainMetaRegistry.get().indexes[index::class.java.name]?:throw IllegalStateException("no index found for ${index::class.java.name}")
                        indexDescription.properties.values.forEach idx@{ prop ->
                            val value = index.getValue(prop.id)?:return@idx
                            if(value is LocalDate || value is LocalDateTime){
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
                        index.uid = UUID.randomUUID().toString()
                        wrappers.add(IndexWrapper(sb.toString(), index))
                    }
                    Database.get().updateIndexes(wrappers, doc.uid)
                }
            return
        }
        advices[idx].onSaveDocument(doc) { doc2 ->
            saveDocument(doc2, advices, idx+1)
        }
    }

    override fun <D : BaseDocument> deleteDocument(doc: D) {
        wrapWithContext{
            deleteDocument(doc, StorageRegistry.get().getAdvices(), 0)
        }
    }

    private fun<D : BaseDocument> deleteDocument(doc:D, advices: List<StorageAdvice>, idx:Int){
        if(idx == advices.size){
            val oldDoc:D? =  Database.get().loadDocument(doc::class, doc.uid)

            val globalContext = contexts.get()
            val context = if(globalContext == null) DeleteDocumentOperationContext(null, oldDoc) else DeleteDocumentOperationContext(globalContext, oldDoc)
            StorageRegistry.get().getInterceptors().forEach {
                it.onDelete(doc, context)
            }
            Database.get().deleteDocument(doc)
            StorageRegistry.get().getIndexHandlers(doc::class).forEach {
                Database.get().deleteIndexes(it.indexClass, doc.uid)
            }
            return
        }
        advices[idx].onDeleteDocument(doc) { doc2 ->
            deleteDocument(doc2, advices, idx+1)
        }
    }

    override fun <D : BaseDocument, I : BaseIndex<D>> searchDocuments(cls: KClass<I>, query: SearchQuery): List<I> {
        return searchDocumentsInternal(cls, query, StorageRegistry.get().getAdvices(), 0)
    }

    private fun<D : BaseDocument,I : BaseIndex<D>> searchDocumentsInternal(cls: KClass<I>, query: SearchQuery, interceptors: List<StorageAdvice>, idx:Int): List<I> {
        if(idx == interceptors.size){
           return Database.get().searchIndex(cls, query)
        }
        return interceptors[idx].onSearchDocuments(cls, query) { cls2, query2 ->
            searchDocumentsInternal(cls2, query2, interceptors, idx+1)
        }
    }

    override fun <D : BaseDocument, I : BaseIndex<D>> searchDocuments(cls: KClass<I>, query: ProjectionQuery): List<Map<String, Any>> {
        return searchDocumentsInternal(cls, query, StorageRegistry.get().getAdvices(), 0)
    }

    private fun<D : BaseDocument,I : BaseIndex<D>> searchDocumentsInternal(cls: KClass<I>, query: ProjectionQuery, interceptors: List<StorageAdvice>, idx:Int): List<Map<String, Any>> {
        if(idx == interceptors.size){
            return Database.get().searchIndex(cls, query)
        }
        return interceptors[idx].onSearchDocuments(cls, query) { cls2, query2 ->
            searchDocumentsInternal(cls2, query2, interceptors, idx+1)
        }
    }

    override fun <D : BaseDocument, I : BaseIndex<D>, R : Any> searchDocuments(cls: KClass<I>, query: SimpleProjectionQuery): R {
        val pq = ProjectionQuery()
        pq.projections.add(query.projection)
        pq.criterions.addAll(query.criterions)
        val res = searchDocuments(cls, pq)
        return when (res.size){
            1 -> res[0].values.iterator().next() as R
            else -> throw Exception("unsupported result size ${res.size}")
        }
    }

    override fun <A : BaseAsset> loadAsset(ref: EntityReference<A>?): A? {
        return if(ref == null) null else loadAsset(ref.type, ref.uid)
    }

    override fun <A : BaseAsset> loadAsset(cls: KClass<A>, uid: String): A? {
        return loadAsset(cls, uid, StorageRegistry.get().getAdvices(), 0)
    }

    private fun<D : BaseAsset> loadAsset(cls: KClass<D>, uid: String, advices: List<StorageAdvice>, idx:Int): D?{
        if(idx == advices.size){
            return Database.get().loadAsset(cls, uid)
        }
        return advices[idx].onLoadAsset(cls, uid) { cls2, uid2 ->
            loadAsset(cls2, uid2, advices, idx+1)
        }
    }

    override fun <A : BaseAsset,E> findUniqueAsset(index: KClass<A>, property: E, propertyValue: Any): A?  where E:PropertyNameSupport, E : EqualitySupport{
        return findUniqueAsset(index, property, propertyValue, StorageRegistry.get().getAdvices(), 0)
    }

    private fun<D : BaseAsset,E> findUniqueAsset(index: KClass<D>, property: E, propertyValue: Any?, interceptors: List<StorageAdvice>, idx:Int): D? where E:PropertyNameSupport, E: EqualitySupport {
        if(idx == interceptors.size){
            val query = searchQuery {
                select(property)
                where {
                    if(propertyValue != null) {
                        eq(property, propertyValue)
                    } else {
                        isNull(property)
                    }
                }
            }
            val lst = Database.get().searchAsset(index, query)
            return when (lst.size){
                0 ->null
                1 ->lst[0]
                else -> throw Exception("найдено несколько записей ${index.qualifiedName} с ${property.name} = $propertyValue")
            }
        }
        return interceptors[idx].onFindUniqueAsset(index, property, propertyValue) { index2, property2, propertyValue2 ->
            findUniqueAsset(index2, property2, propertyValue2, interceptors, idx+1)
        }
    }
    override fun <A : BaseAsset> saveAsset(doc: A) {
        wrapWithContext{
            saveAsset(doc, StorageRegistry.get().getAdvices(), 0)
        }
    }

    private fun<D : BaseAsset> saveAsset(doc:D, advices: List<StorageAdvice>, idx:Int){
        if(idx == advices.size){
            val oldDoc:D? = Database.get().loadAsset(doc::class, doc.uid)

            val globalContext = contexts.get()
            val context = if(globalContext == null) SaveAssetOperationContext(null, oldDoc) else SaveAssetOperationContext(globalContext, oldDoc)
            StorageRegistry.get().getInterceptors().forEach {
                it.onSave(doc, context)
            }
            val sb = StringBuilder()
            val assetDescrition =DomainMetaRegistry.get().assets[doc::class.java.name]?:throw IllegalStateException("no asset description found for ${doc::class.java.name}")
            assetDescrition.properties.values.forEach idx@{ prop ->
                val value = doc.getValue(prop.id)?:return@idx
                if(value is LocalDate || value is LocalDateTime){
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
            Database.get().saveAsset(AssetWrapper(sb.toString(), doc), oldDoc != null)
            return
        }
        advices[idx].onSaveAsset(doc) { doc2 ->
            saveAsset(doc2, advices, idx+1)
        }
    }

    override fun <A : BaseAsset> deleteAsset(doc: A) {
        wrapWithContext {
            deleteAsset(doc, StorageRegistry.get().getAdvices(), 0)
        }
    }

    private fun<D : BaseAsset> deleteAsset(doc:D, advices: List<StorageAdvice>, idx:Int){
        if(idx == advices.size){
            val oldDoc:D? = Database.get().loadAsset(doc::class, doc.uid)
            val globalContext = contexts.get()
            val context = if(globalContext == null) DeleteAssetOperationContext(null, oldDoc) else DeleteAssetOperationContext(globalContext, oldDoc)
            StorageRegistry.get().getInterceptors().forEach {
                it.onDelete(doc, context)
            }
            Database.get().deleteAsset(doc)
            return
        }
        advices[idx].onDeleteAsset(doc) { doc2 ->
            deleteAsset(doc2, advices, idx+1)
        }
    }

    override fun <A : BaseAsset> searchAssets(cls: KClass<A>, query: SearchQuery): List<A> {
        return searchAssets(cls, query, StorageRegistry.get().getAdvices(), 0)
    }

    private fun<D : BaseAsset> searchAssets(cls: KClass<D>, query: SearchQuery, interceptors: List<StorageAdvice>, idx:Int): List<D> {
        if(idx == interceptors.size){
            return Database.get().searchAsset(cls, query)
        }
        return interceptors[idx].onSearchAssets(cls, query) { cls2, query2 ->
            searchAssets(cls2, query2, interceptors, idx+1)
        }
    }

    override fun <A : BaseAsset> searchAssets(cls: KClass<A>, query: ProjectionQuery): List<Map<String, Any>> {
        return searchAssets(cls, query, StorageRegistry.get().getAdvices(), 0)
    }

    private fun<D : BaseAsset> searchAssets(cls: KClass<D>, query: ProjectionQuery, interceptors: List<StorageAdvice>, idx:Int): List<Map<String, Any>> {
        if(idx == interceptors.size){
            return Database.get().searchAsset(cls, query)
        }
        return interceptors[idx].onSearchAssets(cls, query) { cls2, query2 ->
            searchAssets(cls2, query2, interceptors, idx+1)
        }
    }

    override fun <A : BaseAsset, T : Any> searchAssets(cls: KClass<A>, query: SimpleProjectionQuery): T {
        val pq = ProjectionQuery()
        pq.projections.add(query.projection)
        pq.criterions.addAll(query.criterions)
        val res = searchAssets(cls, pq)
        return when (res.size){
            1 -> res[0].values.iterator().next() as T
            else -> throw Exception("unsupported result size ${res.size}")
        }
    }


    override fun executeInTransaction(executable: (TransactionContext) -> Unit) {
        Database.get().executeInTransaction(executable)
    }


}


