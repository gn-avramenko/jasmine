/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNUSED_PARAMETER", "UNCHECKED_CAST")

package com.gridnine.jasmine.server.core.storage

import com.gridnine.jasmine.server.core.app.Environment
import com.gridnine.jasmine.server.core.model.domain.BaseAsset
import com.gridnine.jasmine.server.core.model.domain.BaseDocument
import com.gridnine.jasmine.server.core.model.domain.BaseIndex
import com.gridnine.jasmine.server.core.model.domain.EntityReference
import com.gridnine.jasmine.server.core.storage.search.*
import java.lang.IllegalStateException
import kotlin.reflect.KClass


class TransactionContext(val commit:()->Unit)

abstract class BaseOperationContext

class SaveDocumentOperationContext<D:BaseDocument>(val globalContext:BaseOperationContext?, oldDocument:D?)

class DeleteDocumentOperationContext<D:BaseDocument>(val globalContext:BaseOperationContext?, oldDocument:D?)

class SaveAssetOperationContext<D:BaseAsset>(val globalContext:BaseOperationContext?, oldAsset:D?)

class DeleteAssetOperationContext<D:BaseAsset>(val globalContext:BaseOperationContext?, oldAsset:D?)

class AssetWrapper<A : BaseAsset>(val aggregateData: String, val asset: A){
    companion object{
        const val aggregateData = "aggregateData"
    }
}

class IndexWrapper<D : BaseDocument, I : BaseIndex<D>>(val aggregateData: String, val index: I){
    companion object{
        const val aggregateData = "aggregateData"
    }
}

interface Storage {

    fun <D : BaseDocument> loadDocument(ref: EntityReference<D>?): D?

    fun <D : BaseDocument> loadDocument(cls: KClass<D>, uid:String): D?

    fun <D : BaseDocument, I : BaseIndex<D>,E> findUniqueDocumentReference(
            index: KClass<I>, property: E, propertyValue: Any?): EntityReference<D>? where E:PropertyNameSupport, E: EqualitySupport

    fun <D : BaseDocument, I : BaseIndex<D>,E> findUniqueDocument(
            index: KClass<I>, property: E, propertyValue: Any?): D? where E:PropertyNameSupport, E: EqualitySupport

    fun <D : BaseDocument> saveDocument(doc: D)

    fun <D : BaseDocument> deleteDocument(doc: D)

    fun <D : BaseDocument, I : BaseIndex<D>> searchDocuments(
            cls: KClass<I>, query: SearchQuery): List<I>

    fun <D : BaseDocument, I : BaseIndex<D>> searchDocuments(
            cls: KClass<I>, query: ProjectionQuery): List<Map<String, Any>>

    fun <D : BaseDocument, I : BaseIndex<D>, R:Any> searchDocuments(
            cls: KClass<I>, query: SimpleProjectionQuery): R

    fun <A : BaseAsset> loadAsset(ref: EntityReference<A>?): A?

    fun <A : BaseAsset> loadAsset(cls:KClass<A>, uid:String): A?

    fun <A : BaseAsset,E> findUniqueAsset(
            index: KClass<A>, property: E, propertyValue: Any): A?where E:PropertyNameSupport, E : EqualitySupport

    fun <A : BaseAsset> saveAsset(doc: A)

    fun <A : BaseAsset> deleteAsset(doc: A)

    fun <A : BaseAsset> searchAssets(cls: KClass<A>,
                                     query: SearchQuery): List<A>

    fun <A : BaseAsset> searchAssets(
            cls: KClass<A>, query: ProjectionQuery): List<Map<String, Any>>

    fun <A : BaseAsset, T:Any> searchAssets(
            cls: KClass<A>, query: SimpleProjectionQuery): T

    fun executeInTransaction(executable: (TransactionContext) ->Unit)

    companion object {
        fun get():Storage{
            return Environment.getPublished(Storage::class)
        }
    }

}

interface IndexHandler<D : BaseDocument, I : BaseIndex<D>> {

    val documentClass: KClass<D>

    val indexClass: KClass<I>

    fun createIndexes(doc: D): List<I>
}

interface StorageInterceptor : HasPriority {


    fun<D:BaseDocument>  onDelete(doc: D, context: DeleteDocumentOperationContext<D>) {
        //noops
    }

    fun<D: BaseDocument>  onSave(doc: D, context: SaveDocumentOperationContext<D>) {
        //noops
    }

    fun<A:BaseAsset>  onDelete(doc: A, context: DeleteAssetOperationContext<A>) {
        //noops
    }

    fun<A:BaseAsset>  onSave(doc: A, context: SaveAssetOperationContext<A>) {
        //noops
    }
}

abstract class BaseDocumentInterceptor<D : BaseDocument>(private val documentClass:KClass<D>): StorageInterceptor{
    abstract fun onDeleteDocument(doc:D, context:DeleteDocumentOperationContext<D>)
    abstract fun onSaveDocument(doc:D, context:SaveDocumentOperationContext<D>)

    override fun <P : BaseDocument> onDelete(doc: P, context: DeleteDocumentOperationContext< P>) {
        if(doc::class == documentClass){
            onDeleteDocument(doc as D, context as DeleteDocumentOperationContext<D>)
        }
    }

    override fun <P : BaseDocument> onSave(doc: P, context: SaveDocumentOperationContext<P>) {
        if(doc::class == documentClass){
            onSaveDocument(doc as D, context as SaveDocumentOperationContext<D>)
        }
    }

}

abstract class BaseAssetInterceptor<A : BaseAsset>(private val assetClass:KClass<A>): StorageInterceptor{
    abstract fun onDeleteAsset(doc:A, context:DeleteAssetOperationContext<A>)
    abstract fun onSaveAsset(doc:A, context:SaveAssetOperationContext<A>)

    override fun <P : BaseAsset> onDelete(doc: P, context: DeleteAssetOperationContext<P>) {
        if(doc::class == assetClass){
            onDeleteAsset(doc as A, context as DeleteAssetOperationContext<A>)
        }
    }

    override fun <P : BaseAsset> onSave(doc: P, context: SaveAssetOperationContext<P>) {
        if(doc::class == assetClass){
            onSaveAsset(doc as A, context as SaveAssetOperationContext<A>)
        }
    }

}


interface StorageAdvice:HasPriority{
    fun <D : BaseDocument> onLoadDocument(cls: KClass<D>, uid: String, callback: (cls: KClass<D>, uid: String) -> D?): D? {
        return callback.invoke(cls, uid)
    }

    fun <D : BaseDocument, I : BaseIndex<D>> onSearchDocuments(cls: KClass<I>, query: SearchQuery, callback: (cls: KClass<I>, query: SearchQuery) -> List<I>): List<I> {
        return callback.invoke(cls, query)
    }

    fun <D : BaseDocument, I : BaseIndex<D>> onSearchDocuments(cls: KClass<I>, query: ProjectionQuery, callback: (cls: KClass<I>, query: ProjectionQuery) -> List<Map<String, Any>>): List<Map<String, Any>> {
        return callback.invoke(cls, query)
    }

    fun <D : BaseDocument, I : BaseIndex<D>,E> onFindUniqueDocumentReference(index: KClass<I>, property: E, propertyValue: Any?, callback: (index: KClass<I>, property: E, propertyValue: Any?) -> EntityReference<D>?): EntityReference<D>? where E:EqualitySupport,E:PropertyNameSupport{
        return callback.invoke(index, property, propertyValue)
    }

    fun <D : BaseAsset> onLoadAsset(cls: KClass<D>, uid: String, callback: (cls: KClass<D>, uid: String) -> D?): D? {
        return callback.invoke(cls, uid)
    }

    fun <D : BaseAsset> onDeleteAsset(doc: D, callback:(D) -> Unit) {
        callback.invoke(doc)
    }

    fun <D : BaseAsset> onSaveAsset(doc: D, callback:(D) -> Unit) {
        callback.invoke(doc)
    }

    fun <D : BaseDocument> onDeleteDocument(doc: D, callback:(D) -> Unit) {
        callback.invoke(doc)
    }

    fun <D : BaseDocument> onSaveDocument(doc: D, callback:(D) -> Unit) {
        callback.invoke(doc)
    }

    fun <D : BaseAsset> onSearchAssets(cls: KClass<D>, query: SearchQuery, callback: (cls: KClass<D>, query: SearchQuery) -> List<D>): List<D> {
        return callback.invoke(cls, query)
    }

    fun <D : BaseAsset> onSearchAssets(cls: KClass<D>, query: ProjectionQuery, callback: (cls: KClass<D>, query: ProjectionQuery) -> List<Map<String, Any>>): List<Map<String, Any>> {
        return callback.invoke(cls, query)
    }

    fun <D : BaseAsset,E> onFindUniqueAsset(index: KClass<D>, propertyName: E, propertyValue: Any?, callback: (index: KClass<D>, propertyName: E, propertyValue: Any?) -> D?): D? where E:PropertyNameSupport,E:EqualitySupport {
        return callback.invoke(index, propertyName, propertyValue)
    }
}




interface Database {

    fun <D : BaseDocument> loadDocument(cls: KClass<D>,
                                        uid: String): D?

    fun <D : BaseDocument, I : BaseIndex<D>> searchIndex(
            cls: KClass<I>, query: SearchQuery): List<I>

    fun <D : BaseDocument, I : BaseIndex<D>> searchIndex(
            cls: KClass<I>, query: ProjectionQuery): List<Map<String, Any>>

    fun <D : BaseDocument> saveDocument(obj: D, update: Boolean)

    fun <D : BaseDocument> deleteDocument(document: D)

    fun <A : BaseAsset> loadAsset(cls: KClass<A>, uid: String): A?

    fun <A : BaseAsset> searchAsset(cls: KClass<A>,
                                    query: SearchQuery): List<A>

    fun <A : BaseAsset> searchAsset(
            cls: KClass<A>, query: ProjectionQuery): List<Map<String, Any>>

    fun <D : BaseAsset> saveAsset(asset: AssetWrapper<D>, update: Boolean)

    fun <D : BaseAsset> deleteAsset(asset: D)

    fun executeInTransaction(executable: (TransactionContext) ->Unit)

    fun<D : BaseDocument, I : BaseIndex<D>> deleteIndexes(indexClass: KClass<I>, documentUid: String)

    fun <D : BaseDocument, I : BaseIndex<D>> updateIndexes(
            indexes: List<IndexWrapper<D, I>>, documentUid: String)

    companion object {

        fun get(): Database {
            return Environment.getPublished(Database::class)
        }
    }
}
class StorageRegistry {

    private val storageInterceptors =  arrayListOf<StorageInterceptor>()

    private val storageAdvices = arrayListOf<StorageAdvice>()

    private val indexHandlers = hashMapOf<KClass<*>, MutableList<IndexHandler<*, *>>>()

    fun<D:BaseDocument> register(interceptor:StorageInterceptor) {
        storageInterceptors.add(interceptor)
        storageInterceptors.sortWith(HAS_PRIORITY_COMPARATOR)
    }
    fun register(advice: StorageAdvice) {
        storageAdvices.add(advice)
        storageAdvices.sortWith(HAS_PRIORITY_COMPARATOR)
    }

    fun getInterceptors() = storageInterceptors

    fun getAdvices() = storageAdvices

    fun <D : BaseDocument, I : BaseIndex<D>> register(
            handler: IndexHandler<D, I>) {
        indexHandlers.getOrPut(handler.documentClass){ arrayListOf()}.add(handler)
    }

    fun <D : BaseDocument> getIndexHandlers(
            cls: KClass<D>): List<IndexHandler<BaseDocument, BaseIndex<BaseDocument>>> {
        return indexHandlers[cls] as List<IndexHandler<BaseDocument, BaseIndex<BaseDocument>>>??:
        emptyList()
    }

    companion object{
        fun get()  = Environment.getPublished(StorageRegistry::class)
    }

}

interface  HasPriority{
    val priority:Double
}

internal val HAS_PRIORITY_COMPARATOR =  Comparator.comparing(HasPriority::priority)
