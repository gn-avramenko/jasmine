/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNUSED_PARAMETER", "UNCHECKED_CAST")

package com.gridnine.jasmine.common.core.storage

import com.gridnine.jasmine.common.core.model.*
import com.gridnine.jasmine.common.core.app.Disposable
import com.gridnine.jasmine.common.core.app.PublishableWrapper
import java.time.LocalDateTime
import kotlin.reflect.KClass


class TransactionContext(val commit:()->Unit, val postCommitCallbacks:MutableList<()->Unit> = arrayListOf())

class VersionMetadata(init: VersionMetadata.() -> Unit) {

    lateinit var modifiedBy:String
    lateinit var modified:LocalDateTime
    var comment:String? = null
    var version:Int =0

    init {
        init(this)
    }
}

interface Storage: Disposable {

    fun <D : BaseDocument> loadDocument(ref: ObjectReference<D>?, ignoreCache:Boolean=false): D?

    fun <D : BaseDocument> loadDocument(cls: KClass<D>, uid:String, ignoreCache:Boolean=false): D?

    fun <D : BaseDocument> loadDocumentVersion(cls: KClass<D>, uid:String, version:Int): D?

    fun <D : BaseIdentity> getVersionsMetadata(cls: KClass<D>, uid:String): List<VersionMetadata>

    fun <D : BaseDocument, I : BaseIndex<D>,E> findUniqueDocumentReference(
            index: KClass<I>, property: E, propertyValue: Any?): ObjectReference<D>? where E: PropertyNameSupport, E: EqualitySupport

    fun <D : BaseDocument, I : BaseIndex<D>,E> findUniqueDocument(
            index: KClass<I>, property: E, propertyValue: Any?, ignoreCache:Boolean=false): D? where E: PropertyNameSupport, E: EqualitySupport

    fun <D : BaseDocument> saveDocument(doc: D, createNewVersion:Boolean = true, comment:String?=null)

    fun <D : BaseDocument> deleteDocument(doc: D)

    fun <D : BaseDocument> deleteDocument(ref: ObjectReference<D>)

    fun <D : BaseDocument, I : BaseIndex<D>> searchDocuments(
            cls: KClass<I>, query: SearchQuery): List<I>

    fun <D : BaseDocument, I : BaseIndex<D>> searchDocuments(
            cls: KClass<I>, query: ProjectionQuery): List<Map<String, Any?>>

    fun <D : BaseDocument, I : BaseIndex<D>, R:Any> searchDocuments(
            cls: KClass<I>, query: SimpleProjectionQuery): R

    fun <A : BaseAsset> loadAsset(ref: ObjectReference<A>?, ignoreCache:Boolean=false): A?

    fun <A : BaseAsset> loadAsset(cls:KClass<A>, uid:String, ignoreCache:Boolean=false): A?

    fun <A : BaseAsset> loadAssetVersion(cls:KClass<A>, uid:String, version:Int): A?

    fun <A : BaseAsset,E> findUniqueAsset(
            index: KClass<A>, property: E, propertyValue: Any, ignoreCache:Boolean=false): A?where E: PropertyNameSupport, E : EqualitySupport

    fun <A : BaseAsset> saveAsset(asset: A, createNewVersion:Boolean = true, comment:String?=null)

    fun <A : BaseAsset> deleteAsset(asset: A)

    fun <A : BaseAsset> deleteAsset(ref:ObjectReference<A>)

    fun <A : BaseAsset> searchAssets(cls: KClass<A>,
                                     query: SearchQuery, ignoreCache:Boolean=false): List<A>

    fun <A : BaseAsset> searchAssets(
            cls: KClass<A>, query: ProjectionQuery): List<Map<String, Any>>

    fun <A : BaseAsset, T:Any> searchAssets(
            cls: KClass<A>, query: SimpleProjectionQuery): T

    fun executeInTransaction(executable: (TransactionContext) ->Unit)

    override fun dispose() {
        wrapper.dispose()
    }
    companion object {
        private val wrapper = PublishableWrapper(Storage::class)
        fun get() = wrapper.get()
    }

}