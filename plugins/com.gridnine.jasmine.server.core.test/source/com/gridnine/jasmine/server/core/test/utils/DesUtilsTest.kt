/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")

package com.gridnine.jasmine.server.core.test.utils

import com.gridnine.jasmine.server.core.test.CoreTestBase
import com.gridnine.jasmine.server.core.utils.DesUtil
import org.junit.Assert
import org.junit.Test

class DesUtilsTest:CoreTestBase(){

    @Test
    fun testUtils(){
        val str = "login|password"
        val encoded = DesUtil.encode(str)
        println(encoded)
        Assert.assertEquals(str, DesUtil.decode(encoded))

    }
}