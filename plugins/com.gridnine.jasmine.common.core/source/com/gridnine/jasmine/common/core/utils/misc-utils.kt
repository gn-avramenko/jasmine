/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.common.core.utils

import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.*
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

object LocaleUtils {
    val RU_LOCALE = Locale("ru", "")
    val EN_LOCALE = Locale("en")

    fun getCurrentLocale(): Locale {
        return RU_LOCALE
    }
}

object TextUtils{
    fun generateUid() = UUID.randomUUID().toString()
}

object IoUtils {
    fun gunzip(input: InputStream): ByteArray {
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

object AuthUtils {
    private val users = ThreadLocal<String>()
    fun setCurrentUser(user:String){
        users.set(user)
    }

    fun resetCurrentUser(){
        users.remove()
    }

    fun getCurrentUser() = users.get()
}