/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.common.core.test.serialization

import com.gridnine.jasmine.common.core.serialization.SerializationProvider
import com.gridnine.jasmine.common.core.test.CommonCoreTestBase
import com.gridnine.jasmine.common.core.test.model.rest.TestRestEntity
import com.gridnine.jasmine.common.core.test.model.rest.TestRestEnum
import org.junit.Assert
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class BasicRestSerializationTest : CommonCoreTestBase() {

    @Test
    fun testSerialization() {
        val document = TestRestEntity()
        document.stringField = "123"
        document.enumField = TestRestEnum.ELEMENT2
        val baos = ByteArrayOutputStream()
        SerializationProvider.get().serialize(document, baos,isAbstract = false, prettyPrint = true)
        println(baos.toString(Charsets.UTF_8))
        val doc2 = SerializationProvider.get().deserialize(TestRestEntity::class, ByteArrayInputStream(baos.toByteArray()))
        Assert.assertEquals(document.stringField,
                doc2.stringField)
        Assert.assertEquals(document.enumField,
                doc2.enumField)
    }
}
