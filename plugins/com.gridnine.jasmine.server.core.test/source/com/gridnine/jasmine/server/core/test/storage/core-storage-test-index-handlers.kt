/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.test.storage

import com.gridnine.jasmine.server.core.storage.IndexHandler
import com.gridnine.jasmine.server.core.test.domain.TestDomainDocument
import com.gridnine.jasmine.server.core.test.domain.TestDomainDocumentIndex
import kotlin.reflect.KClass


class TestDocumentIndexHandler : IndexHandler<TestDomainDocument, TestDomainDocumentIndex> {

    override val documentClass: KClass<TestDomainDocument>
        get() = TestDomainDocument::class

    override val indexClass: KClass<TestDomainDocumentIndex>
        get() = TestDomainDocumentIndex::class

    override fun createIndexes(doc: TestDomainDocument): List<TestDomainDocumentIndex> {
        val idx = TestDomainDocumentIndex()
        idx.stringProperty = doc.stringProperty
        idx.navigationKey = doc.uid
        return listOf(idx)
    }

}