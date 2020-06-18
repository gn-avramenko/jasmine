/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.utils

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

object IoUtils {
    fun gunzip(input:InputStream): ByteArray {
        return GZIPInputStream(input).use {
            val baos = ByteArrayOutputStream()
            it.copyTo(baos,256)
            baos.toByteArray()
        }
    }

    fun gzip(data:ByteArray): ByteArray {
        val baos3 = ByteArrayOutputStream()
        data.inputStream().use {ins ->
            GZIPOutputStream(baos3).use {
                ins.copyTo(it)
            }
        }
        return baos3.toByteArray()
    }


    fun gunzip(input:ByteArray): ByteArray {
        return GZIPInputStream(input.inputStream()).use {
            val baos = ByteArrayOutputStream()
            it.copyTo(baos,256)
            baos.toByteArray()
        }
    }
}