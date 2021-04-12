/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.test.storage

import com.gridnine.jasmine.common.core.test.model.domain.TestDomainDocument
import com.gridnine.jasmine.common.core.test.model.domain.TestDomainDocumentIndex
import com.gridnine.jasmine.server.core.storage.IndexHandler
import kotlin.reflect.KClass


class TestDocumentIndexHandler : IndexHandler<TestDomainDocument, TestDomainDocumentIndex> {

    override val documentClass: KClass<TestDomainDocument>
        get() = TestDomainDocument::class

    override val indexClass: KClass<TestDomainDocumentIndex>
        get() = TestDomainDocumentIndex::class

    override fun createIndexes(doc: TestDomainDocument): List<TestDomainDocumentIndex> {
        val idx = TestDomainDocumentIndex()
        idx.stringProperty = doc.stringProperty
        idx.uid = doc.uid
        idx.entityRefCollection.addAll(doc.entityRefCollection)
        idx.entityReference = doc.entityReference
        idx.enumCollection.addAll(doc.enumCollection)
        idx.enumProperty = doc.enumProperty
        idx.stringCollection.addAll(doc.stringCollection)
        return listOf(idx)
    }

}