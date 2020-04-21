/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.storage.impl

import com.gridnine.jasmine.server.core.model.common.BaseIdentity
import com.gridnine.jasmine.server.core.model.common.Xeption
import com.gridnine.jasmine.server.core.model.domain.BaseAsset
import com.gridnine.jasmine.server.core.model.domain.BaseDocument
import com.gridnine.jasmine.server.core.model.domain.BaseIndex
import com.gridnine.jasmine.server.core.model.domain.ObjectReference
import com.gridnine.jasmine.server.core.model.l10n.CoreL10nMessagesFactory
import com.gridnine.jasmine.server.core.storage.*
import com.gridnine.jasmine.server.core.storage.search.*
import kotlin.reflect.KClass

class StorageImpl:Storage{
    override fun <D : BaseDocument> loadDocument(ref: ObjectReference<D>?, forModification: Boolean): D? {
        return ref?.uid?.let { loadDocument(ref.type, it, forModification) }
    }

    override fun <D : BaseDocument> loadDocument(cls: KClass<D>, uid: String, forModification: Boolean): D? {
        return loadDocument(cls, uid, forModification, StorageRegistry.get().getAdvices(), 0)
    }

    override fun <D : BaseDocument> loadDocumentVersion(cls: KClass<D>, uid: String, version: Int): D? {
        return loadDocumentVersion(cls, uid, version, StorageRegistry.get().getAdvices(), 0)
    }

    private fun<D : BaseDocument> loadDocumentVersion(cls: KClass<D>, uid: String, version: Int, advices: List<StorageAdvice>, idx:Int): D?{
        if(idx == advices.size){
            return Database.get().loadDocumentVersion(cls, uid,version)
        }
        return advices[idx].onLoadDocumentVersion(cls, uid, version) { cls2, uid2, version2 ->
            loadDocumentVersion(cls2, uid2,version2, advices,  idx+1)
        }
    }

    private fun<D : BaseDocument> loadDocument(cls: KClass<D>, uid: String, forModification: Boolean, advices: List<StorageAdvice>, idx:Int): D?{
        if(idx == advices.size){
            return Database.get().loadDocument(cls, uid)
        }
        return advices[idx].onLoadDocument(cls, uid, forModification) { cls2, uid2, forModificationInt2 ->
            loadDocument(cls2, uid2,forModificationInt2, advices,  idx+1)
        }
    }

    override fun <D : BaseIdentity> getVersionsMetadata(cls: KClass<D>, uid: String): List<VersionMetadata> {
        return getVersionsMetadata(cls, uid, StorageRegistry.get().getAdvices(), 0)
    }

    private fun<D : BaseIdentity> getVersionsMetadata(cls: KClass<D>, uid: String, advices: List<StorageAdvice>, idx:Int): List<VersionMetadata>{
        if(idx == advices.size){
            return Database.get().getVersionsMetadata(cls, uid)
        }
        return advices[idx].onGetVersionsMetadata(cls, uid) { cls2, uid2 ->
            getVersionsMetadata(cls2, uid2, advices,  idx+1)
        }
    }

    override fun <D : BaseDocument, I : BaseIndex<D>, E : PropertyNameSupport> findUniqueDocumentReference(index: KClass<I>, property: E, propertyValue: Any?): ObjectReference<D>? where E : EqualitySupport {
        return findUniqueDocumentReference(index,property,propertyValue, StorageRegistry.get().getAdvices(), 0)
    }

    private fun<D : BaseDocument,I : BaseIndex<D>, E> findUniqueDocumentReference(index: KClass<I>, property: E, propertyValue: Any?, advices: List<StorageAdvice>, idx:Int): ObjectReference<D>? where E:PropertyNameSupport, E: EqualitySupport {
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
                else -> throw Xeption.forAdmin(CoreL10nMessagesFactory.FOUND_SEVERAL_RECORDS(index.qualifiedName, property.name, propertyValue?.toString()))
            }
        }
        return advices[idx].onFindUniqueDocumentReference(index, property, propertyValue) { index2, property2, propertyValue2 ->
            findUniqueDocumentReference(index2, property2, propertyValue2, advices, idx+1)
        }
    }

    override fun <D : BaseDocument, I : BaseIndex<D>, E : PropertyNameSupport> findUniqueDocument(index: KClass<I>, property: E, propertyValue: Any?, forModification: Boolean): D? where E : EqualitySupport {
        return findUniqueDocumentReference(index, property, propertyValue)?.let { loadDocument(it, forModification) }
    }

    override fun <D : BaseDocument> saveDocument(doc: D) {
        TODO("Not yet implemented")
    }

    override fun <D : BaseDocument> deleteDocument(doc: D) {
        TODO("Not yet implemented")

    }
    override fun <D : BaseDocument, I : BaseIndex<D>> searchDocuments(cls: KClass<I>, query: SearchQuery): List<I> {
        TODO("Not yet implemented")
    }

    override fun <D : BaseDocument, I : BaseIndex<D>> searchDocuments(cls: KClass<I>, query: ProjectionQuery): List<Map<String, Any>> {
        TODO("Not yet implemented")
    }

    override fun <D : BaseDocument, I : BaseIndex<D>, R : Any> searchDocuments(cls: KClass<I>, query: SimpleProjectionQuery): R {
        TODO("Not yet implemented")
    }

    override fun <A : BaseAsset> loadAsset(ref: ObjectReference<A>?, forModification: Boolean): A? {
        TODO("Not yet implemented")
    }

    override fun <A : BaseAsset> loadAsset(cls: KClass<A>, uid: String, forModification: Boolean): A? {
        TODO("Not yet implemented")
    }

    override fun <A : BaseAsset> loadAssetVersion(cls: KClass<A>, uid: String, version: Int): A? {
        TODO("Not yet implemented")
    }

    override fun <A : BaseAsset, E : PropertyNameSupport> findUniqueAsset(index: KClass<A>, property: E, propertyValue: Any, forModification: Boolean): A? where E : EqualitySupport {
        TODO("Not yet implemented")
    }

    override fun <A : BaseAsset> saveAsset(doc: A) {
        TODO("Not yet implemented")
    }

    override fun <A : BaseAsset> deleteAsset(doc: A) {
        TODO("Not yet implemented")
    }

    override fun <A : BaseAsset> searchAssets(cls: KClass<A>, query: SearchQuery, forModification: Boolean): List<A> {
        TODO("Not yet implemented")
    }

    override fun <A : BaseAsset> searchAssets(cls: KClass<A>, query: ProjectionQuery): List<Map<String, Any>> {
        TODO("Not yet implemented")
    }

    override fun <A : BaseAsset, T : Any> searchAssets(cls: KClass<A>, query: SimpleProjectionQuery): T {
        TODO("Not yet implemented")
    }

    override fun executeInTransaction(executable: (TransactionContext) -> Unit) {
        TODO("Not yet implemented")
    }

}