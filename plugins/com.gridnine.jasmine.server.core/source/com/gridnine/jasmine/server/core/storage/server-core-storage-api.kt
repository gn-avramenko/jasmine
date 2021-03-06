/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNUSED_PARAMETER", "UNCHECKED_CAST")

package com.gridnine.jasmine.server.core.storage

import com.gridnine.jasmine.common.core.app.Disposable
import com.gridnine.jasmine.common.core.app.PublishableWrapper
import com.gridnine.jasmine.common.core.model.*
import com.gridnine.jasmine.common.core.storage.ProjectionQuery
import com.gridnine.jasmine.common.core.storage.SearchQuery
import com.gridnine.jasmine.common.core.storage.TransactionContext
import com.gridnine.jasmine.common.core.storage.VersionMetadata
import com.gridnine.jasmine.server.core.storage.jdbc.JdbcDialect
import com.mchange.v2.c3p0.ComboPooledDataSource
import java.io.InputStream
import java.time.LocalDateTime
import javax.sql.DataSource
import kotlin.reflect.KClass


class GlobalOperationContext(private val oldObjectFactory:()->BaseIdentity?, val newObject:BaseIdentity, val transactionContext: TransactionContext, val parameters:MutableMap<String,Any?> = hashMapOf() ){
    val oldObject by lazy {
        oldObjectFactory.invoke()
    }
}

class LocalOperationContext<I:BaseIdentity>(private val oldObjectFactory:()->I?){
    val oldObject by lazy {
        oldObjectFactory.invoke()
    }
}

class OperationContext<I:BaseIdentity>(val globalContext:GlobalOperationContext, val localContext:LocalOperationContext<I>)

data class AssetWrapper<A : BaseAsset>(var aggregatedData: String, var comment:String?, var modified:LocalDateTime?, var modifiedBy:String?, var version:Int, var asset: A):BaseIntrospectableObject(){
    constructor(asset:A):this("", null, null, null, 0, asset)

    override fun getValue(propertyName: String): Any? {
        if(AssetWrapper.aggregatedData == propertyName){
            return aggregatedData
        }
        if(AssetWrapper.comment == propertyName){
            return comment
        }
        if(AssetWrapper.modified == propertyName){
            return modified
        }
        if(AssetWrapper.modifiedBy == propertyName){
            return modifiedBy
        }
        if(AssetWrapper.version == propertyName){
            return version
        }
        return asset.getValue(propertyName)
    }

    override fun setValue(propertyName: String, value: Any?) {
        if(AssetWrapper.aggregatedData == propertyName){
            aggregatedData = value as String
            return
        }
        if(AssetWrapper.comment == propertyName){
            comment = value as String?
            return
        }
        if(AssetWrapper.modified == propertyName){
            modified = value as LocalDateTime?
            return
        }
        if(AssetWrapper.modifiedBy == propertyName){
            modifiedBy = value as String?
            return
        }
        if(AssetWrapper.version == propertyName){
            version = value as Int
            return
        }
        asset.setValue(propertyName, value)
    }

    override fun getCollection(collectionName: String): MutableList<Any> {

        return asset.getCollection(collectionName)

    }
    companion object{
        const val aggregatedData = "aggregatedData"
        const val comment = "comment"
        const val modified = "modified"
        const val modifiedBy = "modifiedBy"
        const val version = "version"
    }

}

data class IndexWrapper<D : BaseDocument, I : BaseIndex<D>>(var aggregatedData: String, val index: I):BaseIntrospectableObject(){

    override fun getValue(propertyName: String): Any? {
        if(IndexWrapper.aggregatedData == propertyName){
            return aggregatedData
        }
        return index.getValue(propertyName)
    }

    override fun setValue(propertyName: String, value: Any?) {
        if(IndexWrapper.aggregatedData == propertyName){
            aggregatedData = value as String
            return
        }
        index.setValue(propertyName, value)
    }

    override fun getCollection(collectionName: String): MutableList<Any> {
        return index.getCollection(collectionName)
    }

    companion object{
        const val aggregatedData = "aggregatedData"
    }
}


interface IndexHandler<D : BaseDocument, I : BaseIndex<D>> {

    val documentClass: KClass<D>

    val indexClass: KClass<I>

    fun createIndexes(doc: D): List<I>
}

interface StorageInterceptor : HasPriority {


    fun<D:BaseDocument>  onDelete(doc: D, context: OperationContext<D>) {
        //noops
    }

    fun<D: BaseDocument>  onSave(doc: D, context: OperationContext<D>) {
        //noops
    }

    fun<A:BaseAsset>  onDelete(asset: A, context: OperationContext<A>) {
        //noops
    }

    fun<A:BaseAsset>  onSave(asset: A, context: OperationContext<A>) {
        //noops
    }
}

abstract class BaseDocumentInterceptor<D : BaseDocument>(private val documentClass:KClass<D>): StorageInterceptor{
    abstract fun onDeleteDocument(doc:D, context:OperationContext<D>)
    abstract fun onSaveDocument(doc:D, context:OperationContext<D>)

    override fun <P : BaseDocument> onDelete(doc: P, context: OperationContext< P>) {
        if(doc::class == documentClass){
            onDeleteDocument(doc as D, context as OperationContext<D>)
        }
    }

    override fun <P : BaseDocument> onSave(doc: P, context: OperationContext<P>) {
        if(doc::class == documentClass){
            onSaveDocument(doc as D, context as OperationContext<D>)
        }
    }

}

abstract class BaseAssetInterceptor<A : BaseAsset>(private val assetClass:KClass<A>): StorageInterceptor{
    abstract fun onDeleteAsset(doc:A, context:OperationContext<A>)
    abstract fun onSaveAsset(doc:A, context:OperationContext<A>)

    override fun <P : BaseAsset> onDelete(asset: P, context: OperationContext<P>) {
        if(asset::class == assetClass){
            onDeleteAsset(asset as A, context as OperationContext<A>)
        }
    }

    override fun <P : BaseAsset> onSave(asset: P, context: OperationContext<P>) {
        if(asset::class == assetClass){
            onSaveAsset(asset as A, context as OperationContext<A>)
        }
    }

}


interface StorageAdvice:HasPriority{
    fun <D : BaseDocument> onLoadDocument(cls: KClass<D>, uid: String, ignoreCache:Boolean, callback: (cls: KClass<D>, uid: String, ignoreCache:Boolean) -> D?): D? {
        return callback.invoke(cls, uid,ignoreCache)
    }

    fun <D : BaseDocument> onLoadDocumentVersion(cls: KClass<D>, uid: String, version:Int, callback: (cls: KClass<D>, uid: String, version:Int) -> D?): D? {
        return callback.invoke(cls, uid,version)
    }

    fun <D : BaseDocument, I : BaseIndex<D>> onSearchDocuments(cls: KClass<I>, query: SearchQuery, callback: (cls: KClass<I>, query: SearchQuery) -> List<I>): List<I> {
        return callback.invoke(cls, query)
    }

    fun <D : BaseDocument, I : BaseIndex<D>> onSearchDocuments(cls: KClass<I>, query: ProjectionQuery, callback: (cls: KClass<I>, query: ProjectionQuery) -> List<Map<String, Any>>): List<Map<String, Any>> {
        return callback.invoke(cls, query)
    }

    fun <D : BaseDocument, I : BaseIndex<D>,E> onFindUniqueDocumentReference(index: KClass<I>, property: E, propertyValue: Any?, callback: (index: KClass<I>, property: E, propertyValue: Any?) -> ObjectReference<D>?): ObjectReference<D>? where E:EqualitySupport,E:PropertyNameSupport{
        return callback.invoke(index, property, propertyValue)
    }

    fun <D : BaseAsset> onLoadAsset(cls: KClass<D>, uid: String, ignoreCache:Boolean, callback: (cls: KClass<D>, uid: String, ignoreCache:Boolean) -> D?): D? {
        return callback.invoke(cls, uid, ignoreCache)
    }
    fun <D : BaseAsset> onLoadAssetVersion(cls: KClass<D>, uid: String, version:Int, callback: (cls: KClass<D>, uid: String, version:Int) -> D?): D? {
        return callback.invoke(cls, uid, version)
    }


    fun <D : BaseAsset> onDeleteAsset(asset: D, callback:(D) -> Unit) {
        callback.invoke(asset)
    }

    fun <D : BaseAsset> onSaveAsset(asset: D, callback:(D) -> Unit) {
        callback.invoke(asset)
    }

    fun <D : BaseDocument> onDeleteDocument(doc: D, callback:(D) -> Unit) {
        callback.invoke(doc)
    }

    fun <D : BaseDocument> onSaveDocument(doc: D, callback:(D) -> Unit) {
        callback.invoke(doc)
    }

    fun <A : BaseAsset> onSearchAssets(cls: KClass<A>, query: SearchQuery, ignoreCache:Boolean, callback: (cls: KClass<A>, query: SearchQuery, ignoreCache:Boolean) -> List<A>): List<A> {
        return callback.invoke(cls, query, ignoreCache)
    }

    fun <A : BaseAsset> onSearchAssets(cls: KClass<A>, query: ProjectionQuery, callback: (cls: KClass<A>, query: ProjectionQuery) -> List<Map<String, Any>>): List<Map<String, Any>> {
        return callback.invoke(cls, query)
    }

    fun <A : BaseAsset,E> onFindUniqueAsset(index: KClass<A>, propertyName: E, propertyValue: Any?, ignoreCache:Boolean, callback: (index: KClass<A>, propertyName: E, propertyValue: Any?, ignoreCache:Boolean) -> A?): A? where E:PropertyNameSupport,E:EqualitySupport {
        return callback.invoke(index, propertyName, propertyValue, ignoreCache)
    }

    fun <D:BaseIdentity> onGetVersionsMetadata(cls: KClass<D>, uid: String, callback: (cls2: KClass<D>, uid2: String) -> List<VersionMetadata>): List<VersionMetadata>{
        return callback.invoke(cls, uid)
    }
}

interface Database:Disposable {

    fun <D : BaseDocument> loadDocument(cls: KClass<D>, uid: String): DocumentReadData?

    fun <D : BaseDocument> loadDocumentWrapper(cls: KClass<D>, uid: String): DocumentWrapper<D>?

    fun <D : BaseDocument> loadDocumentVersion(cls: KClass<D>, uid: String, version:Int): VersionReadData

    fun <D : BaseDocument, I : BaseIndex<D>> searchIndex(
            cls: KClass<I>, query: SearchQuery): List<I>

    fun <D : BaseDocument, I : BaseIndex<D>> searchIndex(
            cls: KClass<I>, query: ProjectionQuery): List<Map<String, Any>>

    fun <D : BaseDocument> saveDocument(obj: DocumentWrapper<D>, existingObjectData: DocumentWrapper<D>?)

    fun <D : BaseDocument> deleteDocument(readData: DocumentWrapper<D>)

    fun <A : BaseAsset> loadAsset(cls: KClass<A>, uid: String): AssetWrapper<A>?

    fun <A : BaseAsset> loadAssetVersion(cls: KClass<A>, uid: String, version:Int): VersionReadData

    fun <A : BaseAsset> searchAsset(cls: KClass<A>,
                                    query: SearchQuery): List<A>

    fun <A : BaseAsset> searchAsset(
            cls: KClass<A>, query: ProjectionQuery): List<Map<String, Any>>

    fun <D : BaseAsset> saveAsset(asset: AssetWrapper<D>, readData: AssetWrapper<D>?)

    fun <D : BaseAsset> deleteAsset(asset: D)


    fun executeInTransaction(executable: (TransactionContext) ->Unit)

    fun<D : BaseDocument, I : BaseIndex<D>> deleteIndexes(indexClass: KClass<I>, documentUid: String)

    fun <D : BaseDocument, I : BaseIndex<D>> updateIndexes(cls:KClass<I>, documentUid: String,
            indexes: List<IndexWrapper<D, I>>,  update:Boolean )

    fun <D:BaseIdentity> getVersionsMetadata(cls: KClass<D>, uid: String): List<VersionMetadata>

    fun <D : BaseDocument> saveDocumentVersion(cls: KClass<D>, uid:String, content:ByteArray, metadata:VersionMetadata)

    fun<A : BaseAsset> saveAssetVersion(cls: KClass<A>, uid: String, content:ByteArray, metadata: VersionMetadata)

    override fun dispose() {
        wrapper.dispose()
    }

    fun <A:BaseAsset> loadAssetWrapper(kClass: KClass<A>, uid: String): AssetWrapper<A>?

    companion object {
        private val wrapper = PublishableWrapper(Database::class)
        fun get() = wrapper.get()
    }
}
class StorageRegistry:Disposable {

    private val storageInterceptors =  arrayListOf<StorageInterceptor>()

    private val storageAdvices = arrayListOf<StorageAdvice>()

    private val indexHandlers = hashMapOf<KClass<*>, MutableList<IndexHandler<*, *>>>()

    private val dataSourceProviders = hashMapOf<String, DataSourceProvider>()

    fun register(interceptor:StorageInterceptor) {
        storageInterceptors.add(interceptor)
        storageInterceptors.sortBy { it.priority }
    }
    fun register(advice: StorageAdvice) {
        storageAdvices.add(advice)
        storageAdvices.sortBy { it.priority }
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

    fun register(dataSourceProvider: DataSourceProvider) {
        dataSourceProviders[dataSourceProvider.getId()] = dataSourceProvider
    }

    fun getDataSourceProvider(id:String)= dataSourceProviders[id]!!

    override fun dispose() {
        wrapper.dispose()
    }
    companion object {
        private val wrapper = PublishableWrapper(StorageRegistry::class)
        fun get() = wrapper.get()
    }

}

class DocumentReadData(val streamProvider: () -> InputStream, private val closeCallback:()->Unit):AutoCloseable{
    override fun close() {
        closeCallback()
    }
}

class VersionReadData(val streamProvider: () -> InputStream, private val closeCallback:()->Unit):AutoCloseable{
    override fun close() {
        closeCallback()
    }
}

class DocumentWrapper<D:BaseIdentity>(val uid:String, val content:ByteArray,val metadata:VersionMetadata,val revision:Int,val oid:Long?,val cls:KClass<D>)


class C3PoDataSource(private val delegate:ComboPooledDataSource): DataSource by delegate,Disposable{
    override fun dispose() {
        delegate.close()
    }
}
interface DataSourceProvider{
    fun createDataSource(): C3PoDataSource
    fun createDialect():JdbcDialect
    fun getId():String
}