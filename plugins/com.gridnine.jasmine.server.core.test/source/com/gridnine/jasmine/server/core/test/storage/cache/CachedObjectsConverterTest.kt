/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.test.storage.cache

import com.gridnine.jasmine.server.core.model.domain.EntityUtils
import com.gridnine.jasmine.server.core.storage.cache.CachedObjectsConverter
import com.gridnine.jasmine.server.core.test.CoreTestBase
import com.gridnine.jasmine.server.core.test.domain.*
import org.junit.Assert
import org.junit.Test
import java.time.LocalDateTime
import java.util.*

class CachedObjectsConverterTest :CoreTestBase(){
    @Test
    fun testDocumentConverter(){
        val document = TestDomainDocument()
        document.uid = UUID.randomUUID().toString()
        document.stringProperty = "123"
        document.stringCollection.add("321")
        document.entityReference =EntityUtils.toReference(document)
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
        val doc2 = CachedObjectsConverter.get().toCachedObject(document)
        compare(document, doc2)
        val doc3 = CachedObjectsConverter.get().toStandard(doc2)
        compare(document, doc3)
    }

    private fun compare(document: TestDomainDocument, doc2: TestDomainDocument) {
        Assert.assertEquals(document.stringProperty,
                doc2.stringProperty)
        Assert.assertEquals(1, doc2.stringCollection.size.toLong())
        Assert.assertEquals(document.entityReference, doc2.entityReference)
        Assert.assertEquals("321", doc2.stringCollection[0])
        Assert.assertEquals(document.uid, doc2.uid)
        Assert.assertNotNull(doc2.entityProperty)
        Assert.assertEquals(document.entityProperty?.name,
                doc2.entityProperty!!.name)
        Assert.assertEquals((document.entityProperty as TestDomainNestedDocumentImpl).value,
                (doc2.entityProperty as TestDomainNestedDocumentImpl)
                        .value)
        Assert.assertEquals(1, doc2.entityCollection.size.toLong())
        Assert.assertEquals(document.entityCollection[0].name,
                doc2.entityCollection[0].name)
        Assert.assertEquals((document.entityCollection[0] as TestDomainNestedDocumentImpl).value,
                (doc2.entityCollection[0] as TestDomainNestedDocumentImpl)
                        .value)
        Assert.assertEquals(2, doc2.groups.size.toLong())
        Assert.assertEquals(1, doc2.groups[1].items.size.toLong())
    }

    @Test
    fun testAssetConverter(){
        val asset = TestDomainAsset()
        asset.dateProperty = LocalDateTime.now()
        asset.stringProperty = "test"
        val asset2 = CachedObjectsConverter.get().toCachedObject(asset)
        Assert.assertEquals(asset.dateProperty, asset2.dateProperty)
        Assert.assertEquals(asset.stringProperty, asset2.stringProperty)
    }

}