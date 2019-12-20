/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.test.serialization

import com.gridnine.jasmine.server.core.serialization.DomainSerializationUtils
import com.gridnine.jasmine.server.core.test.CoreTestBase
import com.gridnine.jasmine.server.core.test.domain.TestDomainDocument
import com.gridnine.jasmine.server.core.test.domain.TestDomainNestedDocumentImpl
import com.gridnine.jasmine.server.core.test.domain.TestGroup
import com.gridnine.jasmine.server.core.test.domain.TestItem
import org.junit.Assert
import org.junit.Test


class BasicDomainSerializationTest : CoreTestBase() {

    @Test
    fun testSerialization() {
        val document = TestDomainDocument()
        document.stringProperty = "123"
        document.stringCollection.add("321")

        val nestedEntity = TestDomainNestedDocumentImpl()
        nestedEntity.name = "test1"
        nestedEntity.value = "test1"
        document.entityProperty = nestedEntity
        val collEntity = TestDomainNestedDocumentImpl()
        collEntity.name = "coll"
        collEntity.value = "coll"
        document.entityCollection.add(collEntity)
        val group1 = TestGroup()
        group1.name = "group1"
        document.groups.add(group1)
        val group2 = TestGroup()
        group2.name = "group2"
        document.groups.add(group2)
        val item = TestItem()
        item.name = "item"
        group2.items.add(item)
        val data = DomainSerializationUtils.serializeToString(document)
        println(data)
        val doc2 = DomainSerializationUtils.deserialize(TestDomainDocument::class, data)
        Assert.assertEquals(document.stringProperty,
                doc2.stringProperty)
        Assert.assertEquals(1, doc2.stringCollection.size.toLong())
        Assert.assertEquals("321", doc2.stringCollection[0])
        Assert.assertEquals(document.uid, doc2.uid)
        Assert.assertNotNull(doc2.entityProperty)
        Assert.assertEquals(nestedEntity.name,
                doc2.entityProperty!!.name)
        Assert.assertEquals(nestedEntity.value,
                (doc2.entityProperty as TestDomainNestedDocumentImpl)
                        .value)
        Assert.assertEquals(1, doc2.entityCollection.size.toLong())
        Assert.assertEquals(collEntity.name,
                doc2.entityCollection[0].name)
        Assert.assertEquals(collEntity.value,
                (doc2.entityCollection[0] as TestDomainNestedDocumentImpl)
                        .value)
        Assert.assertEquals(2, doc2.groups.size.toLong())
        Assert.assertEquals(1, doc2.groups[1].items.size.toLong())

    }
}
