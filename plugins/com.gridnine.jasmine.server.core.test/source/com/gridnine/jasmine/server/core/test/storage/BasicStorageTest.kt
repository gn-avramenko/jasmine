/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.test.storage

import com.gridnine.jasmine.server.core.storage.Storage
import com.gridnine.jasmine.server.core.storage.search.count
import com.gridnine.jasmine.server.core.storage.search.searchQuery
import com.gridnine.jasmine.server.core.storage.search.simpleProjectionQuery
import com.gridnine.jasmine.server.core.test.domain.TestDomainDocument
import com.gridnine.jasmine.server.core.test.domain.TestDomainDocumentIndex
import org.junit.Assert
import org.junit.Test


class BasicStorageTest : StorageTestBase() {

    @Test
    fun testCRUD() {
        var doc = TestDomainDocument()
        doc.stringProperty = "test"
        Storage.get().saveDocument(doc)
        doc = Storage.get().loadDocument(TestDomainDocument::class, doc.uid)!!
        Assert.assertEquals("test", doc.stringProperty)
        var query = searchQuery {
            where {
                eq(TestDomainDocumentIndex.stringProperty, "test")
            }
        }
        var indexes = Storage.get()
                .searchDocuments(TestDomainDocumentIndex::class, query)
        Assert.assertEquals(1, indexes.size)
        query = searchQuery {
            where {
                freeText("test")
            }
        }
        indexes = Storage.get().searchDocuments(TestDomainDocumentIndex::class,
                query)
        Assert.assertEquals(1, indexes.size)

        val projectionQuery =  simpleProjectionQuery(count()){
            where {
                eq(TestDomainDocumentIndex.stringProperty, "test")
            }
        }
        val result:Int = Storage.get()
                .searchDocuments(TestDomainDocumentIndex::class, projectionQuery)
        Assert.assertEquals(1, result)
    }
}