/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UnsafeCastFromDynamic", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.core.ui

import kotlin.math.round
import kotlin.random.Random

object TextUtilsJS {
    private val random = Random(100)
    fun createUUID(): String {
        // http://www.ietf.org/rfc/rfc4122.txt
        val s = ArrayList<String>(37)
        val hexDigits = "0123456789abcdef"
        for (i in 0..36) {
            val round = round(random.nextDouble() * 0x10).toInt()
            val substr = hexDigits.substring(round, round + 1)
            s.add(substr)
        }
        s[14] = "4"
        s[8] = "-"
        s[13] = "-"
        s[18] = "-"
        s[23] = "-"
        return s.joinToString(separator = "")
    }
}