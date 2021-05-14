/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.test.storage

import com.gridnine.jasmine.common.core.model.EntityUtils
import com.gridnine.jasmine.common.core.storage.Storage
import com.gridnine.jasmine.common.core.storage.count
import com.gridnine.jasmine.common.core.storage.searchQuery
import com.gridnine.jasmine.common.core.storage.simpleProjectionQuery
import com.gridnine.jasmine.common.core.test.model.domain.TestDomainAsset
import com.gridnine.jasmine.common.core.test.model.domain.TestDomainDocument
import com.gridnine.jasmine.common.core.test.model.domain.TestDomainDocumentIndex
import com.gridnine.jasmine.common.core.test.model.domain.TestEnum
import com.gridnine.jasmine.common.core.utils.AuthUtils
import com.gridnine.jasmine.common.core.utils.TextUtils
import org.junit.Assert
import org.junit.Test
import java.time.LocalDateTime
import java.util.*

class StorageBasicTest : StorageTestBase() {
    @Test
    fun testDocumentCRUD() {
        AuthUtils.setCurrentUser("system")
        var doc = TestDomainDocument()
        doc.uid = TextUtils.generateUid()
        doc.stringProperty = "test"
        doc.entityReference = EntityUtils.toReference(doc)
        doc.stringCollection.addAll(arrayListOf("test1", "test2"))
        doc.enumCollection.addAll(arrayListOf(TestEnum.ITEM1, TestEnum.ITEM2))
        doc.entityRefCollection.addAll(arrayListOf(EntityUtils.toReference(doc), EntityUtils.toReference(doc)))
        doc.enumProperty = TestEnum.ITEM1
        Storage.get().saveDocument(doc, comment = "version1")
        Storage.get().searchDocuments(TestDomainDocumentIndex::class,
                searchQuery {
                    select(TestDomainDocumentIndex.entityReferenceProperty)
                    where {
                        eq(TestDomainDocumentIndex.entityReferenceProperty, doc.entityReference!!)
                    }
                }
        )
        doc = Storage.get().loadDocument(TestDomainDocument::class, doc.uid, true)!!
        Assert.assertEquals("test", doc.stringProperty)
        doc.stringProperty = "test2"
        Storage.get().saveDocument(doc, true, "version2")
        doc = Storage.get().loadDocument(TestDomainDocument::class, doc.uid)!!
        Assert.assertEquals("test2", doc.stringProperty)
        doc = Storage.get().loadDocumentVersion(TestDomainDocument::class, doc.uid, 0)!!
        Assert.assertEquals("test", doc.stringProperty)
        var query = searchQuery {
            where {
                eq(TestDomainDocumentIndex.stringPropertyProperty, "test2")
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
                searchQuery { })
        Assert.assertEquals(1, indexes.size)
        Assert.assertEquals(2, indexes[0].stringCollection.size)
        Assert.assertEquals(2, indexes[0].enumCollection.size)
        Assert.assertEquals(2, indexes[0].entityRefCollection.size)
        indexes = Storage.get().searchDocuments(TestDomainDocumentIndex::class,
                searchQuery {
                    select(TestDomainDocumentIndex.entityReferenceProperty)
                    where {
                        eq(TestDomainDocumentIndex.entityReferenceProperty, doc.entityReference!!)
                    }
                }
        )
        Assert.assertEquals(1, indexes.size)

        val projectionQuery = simpleProjectionQuery(count()) {
            where {
                eq(TestDomainDocumentIndex.stringPropertyProperty, "test2")
            }
        }
        val result: Int = Storage.get()
                .searchDocuments(TestDomainDocumentIndex::class, projectionQuery)
        Assert.assertEquals(1, result)

        val metadataItems = Storage.get().getVersionsMetadata(TestDomainDocument::class, doc.uid)
        Assert.assertEquals(2, metadataItems.size)
    }

    @Test
    fun testAssetCRUD() {
        AuthUtils.setCurrentUser("system")
        var asset = TestDomainAsset()
        asset.uid = TextUtils.generateUid()
        asset.stringProperty = "test"
        asset.dateProperty = LocalDateTime.now()
        Storage.get().saveAsset(asset)
        asset = Storage.get().loadAsset(TestDomainAsset::class, asset.uid, true)!!
        Assert.assertEquals("test", asset.stringProperty)
        asset.stringProperty = "test2"
        Storage.get().saveAsset(asset, true, "version2")
        asset = Storage.get().loadAsset(TestDomainAsset::class, asset.uid)!!
        Assert.assertEquals("test2", asset.stringProperty)
        asset = Storage.get().loadAssetVersion(TestDomainAsset::class, asset.uid, 0)!!
        Assert.assertEquals("test", asset.stringProperty)
        var query = searchQuery {
            where {
                eq(TestDomainAsset.stringPropertyProperty, "test2")
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
                searchQuery { })
        Assert.assertEquals(1, assets.size)

        val projectionQuery = simpleProjectionQuery(count()) {
            where {
                eq(TestDomainAsset.stringPropertyProperty, "test2")
            }
        }
        val result: Int = Storage.get()
                .searchAssets(TestDomainAsset::class, projectionQuery)
        Assert.assertEquals(1, result)

        val metadataItems = Storage.get().getVersionsMetadata(TestDomainAsset::class, asset.uid)
        Assert.assertEquals(2, metadataItems.size)
    }

    @Test
    fun testDocumentSameVersion(){
        AuthUtils.setCurrentUser("system")
        var doc = TestDomainDocument()
        doc.uid = TextUtils.generateUid()
        doc.stringProperty = "test"
        Storage.get().saveDocument(doc)
        doc.stringProperty = "test2"
        Storage.get().saveDocument(doc)
        doc = Storage.get().loadDocumentVersion(doc::class, doc.uid, 0)!!
        Assert.assertEquals("test", doc.stringProperty)
        doc = Storage.get().loadDocument(doc::class, doc.uid, true)!!
        doc.stringProperty = "test3"
        Storage.get().saveDocument(doc, false, "update")
        doc = Storage.get().loadDocument(doc::class, doc.uid, true)!!
        Assert.assertEquals("test3", doc.stringProperty)
        doc = Storage.get().loadDocumentVersion(doc::class, doc.uid, 0)!!
        Assert.assertEquals("test", doc.stringProperty)
    }

    @Test
    fun testDocumentSameVersionMultipleTimes(){
        AuthUtils.setCurrentUser("system")
        val doc = TestDomainDocument()
        doc.uid = TextUtils.generateUid()
        doc.stringProperty = "test"
        Storage.get().saveDocument(doc, false)
        doc.stringProperty = "test2"
        Storage.get().saveDocument(doc, false)
        doc.stringProperty = "test3"
        Storage.get().saveDocument(doc, false)
        doc.stringProperty = "test5"
        Storage.get().saveDocument(doc, false)
    }
}