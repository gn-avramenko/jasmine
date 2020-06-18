/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.test.storage

import com.gridnine.jasmine.server.core.model.domain.EntityUtils
import com.gridnine.jasmine.server.core.storage.Storage
import com.gridnine.jasmine.server.core.storage.search.count
import com.gridnine.jasmine.server.core.storage.search.searchQuery
import com.gridnine.jasmine.server.core.storage.search.simpleProjectionQuery
import com.gridnine.jasmine.server.core.test.domain.TestDomainAsset
import com.gridnine.jasmine.server.core.test.domain.TestDomainDocument
import com.gridnine.jasmine.server.core.test.domain.TestDomainDocumentIndex
import com.gridnine.jasmine.server.core.test.domain.TestEnum
import com.gridnine.jasmine.server.core.utils.AuthUtils
import org.junit.Assert
import org.junit.Test
import java.time.LocalDateTime
import java.util.*

class StorageBasicTest :StorageTestBase(){
    @Test
    fun testDocumentCRUD(){
        AuthUtils.setCurrentUser("system")
        var doc = TestDomainDocument()
        doc.uid = UUID.randomUUID().toString()
        doc.stringProperty = "test"
        doc.entityReference  = EntityUtils.toReference(doc)
        doc.stringCollection.addAll(arrayListOf("test1","test2"))
        doc.enumCollection.addAll(arrayListOf(TestEnum.ITEM1, TestEnum.ITEM2))
        doc.entityRefCollection.addAll(arrayListOf(EntityUtils.toReference(doc),EntityUtils.toReference(doc)) )
        doc.enumProperty = TestEnum.ITEM1
        Storage.get().saveDocument(doc)
        doc = Storage.get().loadDocument(TestDomainDocument::class, doc.uid)!!
        Assert.assertEquals("test", doc.stringProperty)
        doc.stringProperty = "test2"
        Storage.get().saveDocument(doc, "version2")
        doc = Storage.get().loadDocument(TestDomainDocument::class, doc.uid)!!
        Assert.assertEquals("test2", doc.stringProperty)
        doc = Storage.get().loadDocumentVersion(TestDomainDocument::class, doc.uid, 0)!!
        Assert.assertEquals("test", doc.stringProperty)
        var query = searchQuery {
            where {
                eq(TestDomainDocumentIndex.stringProperty, "test2")
            }
        }
        var indexes = Storage.get()
                .searchDocuments(TestDomainDocumentIndex::class, query)
        Assert.assertEquals(1, indexes.size)
        query = searchQuery {
            where {
                freeText("test2")
            }
        }
        indexes = Storage.get().searchDocuments(TestDomainDocumentIndex::class,
                query)
        Assert.assertEquals(1, indexes.size)
        indexes = Storage.get().searchDocuments(TestDomainDocumentIndex::class,
                searchQuery {  })
        Assert.assertEquals(1, indexes.size)
        Assert.assertEquals(2, indexes[0].stringCollection.size)
        Assert.assertEquals(2, indexes[0].enumCollection.size)
        Assert.assertEquals(2, indexes[0].entityRefCollection.size)

        val projectionQuery =  simpleProjectionQuery(count()){
            where {
                eq(TestDomainDocumentIndex.stringProperty, "test2")
            }
        }
        val result:Int = Storage.get()
                .searchDocuments(TestDomainDocumentIndex::class, projectionQuery)
        Assert.assertEquals(1, result)

        val metadataItems = Storage.get().getVersionsMetadata(TestDomainDocument::class, doc.uid)
        Assert.assertEquals(2, metadataItems.size)
    }

    @Test
    fun testAssetCRUD(){
        AuthUtils.setCurrentUser("system")
        var asset = TestDomainAsset()
        asset.uid = UUID.randomUUID().toString()
        asset.stringProperty = "test"
        asset.dateProperty  = LocalDateTime.now()
        Storage.get().saveAsset(asset)
        asset = Storage.get().loadAsset(TestDomainAsset::class, asset.uid)!!
        Assert.assertEquals("test", asset.stringProperty)
        asset.stringProperty = "test2"
        Storage.get().saveAsset(asset, "version2")
        asset = Storage.get().loadAsset(TestDomainAsset::class, asset.uid)!!
        Assert.assertEquals("test2", asset.stringProperty)
        asset = Storage.get().loadAssetVersion(TestDomainAsset::class, asset.uid, 0)!!
        Assert.assertEquals("test", asset.stringProperty)
        var query = searchQuery {
            where {
                eq(TestDomainAsset.stringProperty, "test2")
            }
        }
        var assets = Storage.get()
                .searchAssets(TestDomainAsset::class, query)
        Assert.assertEquals(1, assets.size)
        query = searchQuery {
            where {
                freeText("test2")
            }
        }
        assets = Storage.get().searchAssets(TestDomainAsset::class,
                query)
        Assert.assertEquals(1, assets.size)
        assets = Storage.get().searchAssets(TestDomainAsset::class,
                searchQuery {  })
        Assert.assertEquals(1, assets.size)

        val projectionQuery =  simpleProjectionQuery(count()){
            where {
                eq(TestDomainAsset.stringProperty, "test2")
            }
        }
        val result:Int = Storage.get()
                .searchAssets(TestDomainAsset::class, projectionQuery)
        Assert.assertEquals(1, result)

        val metadataItems = Storage.get().getVersionsMetadata(TestDomainAsset::class, asset.uid)
        Assert.assertEquals(2, metadataItems.size)
    }
}