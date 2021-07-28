/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jtasks
 *****************************************************************/
package com.gridnine.jasmine.server.standard.test.sn

import com.gridnine.jasmine.common.core.storage.Storage
import com.gridnine.jasmine.server.standard.model.SequenceNumberGenerator
import com.gridnine.jasmine.server.standard.test.StandardServerTestBase
import org.junit.Assert
import org.junit.Test

class SequenceNumberTest:StandardServerTestBase() {
    @Test
    fun testSN() {
        val key = "XTR"
        Storage.get().executeInTransaction {
            val firstNumber = SequenceNumberGenerator.get().incrementAndGet(key)
            Assert.assertEquals(1, firstNumber)
            val secondNumber = SequenceNumberGenerator.get().incrementAndGet(key)
            Assert.assertEquals(2, secondNumber)
        }
    }
}