/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.test.storage.cache

import com.gridnine.jasmine.common.core.model.*
import com.gridnine.jasmine.common.core.storage.Storage
import com.gridnine.jasmine.common.core.test.model.domain.TestDomainDocument
import com.gridnine.jasmine.common.core.test.model.domain.TestDomainDocumentIndex
import com.gridnine.jasmine.common.core.utils.AuthUtils
import com.gridnine.jasmine.server.core.storage.StorageAdvice
import com.gridnine.jasmine.server.core.storage.StorageRegistry
import com.gridnine.jasmine.server.core.test.storage.StorageTestBase
import org.junit.Assert
import org.junit.Test
import kotlin.reflect.KClass

@Suppress("MISPLACED_TYPE_PARAMETER_CONSTRAINTS")
class StorageCacheTest:StorageTestBase(){

    private var loadDocumentCount = 0

    private var loadAssetCount = 0

    private var findAssetCount = 0

    private var findDocumentCount = 0

    override fun configureStorageRegistry() {
        super.configureStorageRegistry()
        StorageRegistry.get().register(object:StorageAdvice{
            override val priority = 2.0

            override fun <D : BaseDocument> onLoadDocument(cls: KClass<D>, uid: String, ignoreCache: Boolean, callback: (cls: KClass<D>, uid: String, ignoreCache: Boolean) -> D?): D? {
                loadDocumentCount++
                return super.onLoadDocument(cls, uid, ignoreCache, callback)
            }

            override fun <D : BaseAsset> onLoadAsset(cls: KClass<D>, uid: String, ignoreCache: Boolean, callback: (cls: KClass<D>, uid: String, ignoreCache: Boolean) -> D?): D? {
                loadAssetCount++
                return super.onLoadAsset(cls, uid, ignoreCache, callback)
            }

            override fun <A : BaseAsset, E : PropertyNameSupport> onFindUniqueAsset(index: KClass<A>, propertyName: E, propertyValue: Any?, ignoreCache: Boolean, callback: (index: KClass<A>, propertyName: E, propertyValue: Any?, ignoreCache: Boolean) -> A?): A? where E : EqualitySupport {
                findAssetCount++
                return super.onFindUniqueAsset(index, propertyName, propertyValue, ignoreCache, callback)
            }

            override fun <D : BaseDocument, I : BaseIndex<D>, E : EqualitySupport> onFindUniqueDocumentReference(index: KClass<I>, property: E, propertyValue: Any?, callback: (index: KClass<I>, property: E, propertyValue: Any?) -> ObjectReference<D>?): ObjectReference<D>? where E : PropertyNameSupport {
                findDocumentCount++
                return super.onFindUniqueDocumentReference(index, property, propertyValue, callback)
            }

        })
    }


    @Test
    fun testCache(){
        AuthUtils.setCurrentUser("system")
        var doc = TestDomainDocument()
        doc.stringProperty = "test"
        Storage.get().saveDocument(doc)
        doc = Storage.get().loadDocument(TestDomainDocument::class, doc.uid)!!
        Assert.assertEquals(1, loadDocumentCount)
        doc = Storage.get().loadDocument(TestDomainDocument::class, doc.uid)!!
        Assert.assertTrue(doc is CachedObject)
        Assert.assertEquals(1, loadDocumentCount)
        doc = Storage.get().loadDocument(TestDomainDocument::class, doc.uid,true)!!
        Assert.assertEquals(2, loadDocumentCount)
        doc.stringProperty = "test2"
        Storage.get().saveDocument(doc)
        doc = Storage.get().loadDocument(TestDomainDocument::class, doc.uid)!!
        Assert.assertTrue(doc is CachedObject)
        Assert.assertEquals(3, loadDocumentCount)
        var ref = Storage.get().findUniqueDocumentReference(TestDomainDocumentIndex::class, TestDomainDocumentIndex.stringPropertyProperty, "test2")
        Assert.assertNotNull(ref)
        Assert.assertEquals(1, findDocumentCount)
        ref = Storage.get().findUniqueDocumentReference(TestDomainDocumentIndex::class, TestDomainDocumentIndex.stringPropertyProperty, "test2")
        Assert.assertNotNull(ref)
        Assert.assertEquals(1, findDocumentCount)
        doc = Storage.get().loadDocument(TestDomainDocument::class, doc.uid,true)!!
        doc.stringProperty = "test"
        Storage.get().saveDocument(doc)
        ref = Storage.get().findUniqueDocumentReference(TestDomainDocumentIndex::class, TestDomainDocumentIndex.stringPropertyProperty, "test")
        Assert.assertNotNull(ref)
        Assert.assertEquals(2, findDocumentCount)
        Storage.get().deleteDocument(doc)
        ref = Storage.get().findUniqueDocumentReference(TestDomainDocumentIndex::class, TestDomainDocumentIndex.stringPropertyProperty, "test")
        Assert.assertNull(ref)
    }

}