/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.test.serialization

import com.gridnine.jasmine.server.core.serialization.JsonSerializer
import com.gridnine.jasmine.server.core.test.CoreTestBase
import com.gridnine.jasmine.server.core.test.rest.TestRestEntity
import com.gridnine.jasmine.server.core.test.rest.TestRestEnum
import org.junit.Assert
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class BasicRestSerializationTest : CoreTestBase() {

    @Test
    fun testSerialization() {
        val document = TestRestEntity()
        document.stringField = "123"
        document.enumField = TestRestEnum.ELEMENT2
        val baos = ByteArrayOutputStream()
        JsonSerializer.get().serialize(document, baos,isAbstract = false, prettyPrint = true)
        println(baos.toString(Charsets.UTF_8))
        val doc2 = JsonSerializer.get().deserialize(TestRestEntity::class, ByteArrayInputStream(baos.toByteArray()))
        Assert.assertEquals(document.stringField,
                doc2.stringField)
        Assert.assertEquals(document.enumField,
                doc2.enumField)
    }
}
