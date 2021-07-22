package com.gridnine.jasmine.common.core.test.cipher

import com.gridnine.jasmine.common.core.test.TestBase
import com.gridnine.jasmine.server.core.utils.DESUtils
import org.junit.Assert
import org.junit.Test

class DesUtilsTest: TestBase() {
    @Test
    fun testDesUtils() {
        val password = "пароль"
        val encryptedPassword = DESUtils.encrypt(password)
        println(encryptedPassword)
        val decryptedPassword = DESUtils.decrypt(encryptedPassword)
        Assert.assertEquals(password, decryptedPassword)
    }
}