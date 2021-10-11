/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.common.core.utils

import com.gridnine.jasmine.common.core.model.BaseIdentity
import com.gridnine.jasmine.common.core.model.ObjectReference
import com.gridnine.jasmine.common.core.model.SelectItem
import com.gridnine.jasmine.common.core.reflection.ReflectionFactory
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.lang.reflect.InvocationTargetException
import java.sql.SQLException
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

object TextUtils {
    fun generateUid() = UUID.randomUUID().toString()
    fun isBlank(str: String?): Boolean {
        return str == null || str.isBlank()
    }

    fun isNotBlank(str: String?): Boolean {
        return !isBlank(str)
    }

    fun getExceptionStackTrace(e: Throwable?): String? {
        if (e == null) {
            return null
        }
        val sb = StringBuilder(e.stackTrace.size * 100)
        printError(e, e.toString(), sb)
        return sb.toString()
    }

    private fun printError(t: Throwable?, header: String,
                           sb: StringBuilder) {
        if (t == null) {
            return
        }
        val nl = System.getProperty("line.separator") //$NON-NLS-1$
        if (!isBlank(header)) {
            sb.append(nl).append(header).append(nl).append(nl)
        }
        for (element in t.stackTrace) {
            printStackTraceElement(element, sb).append(nl)
        }
        var next = t.cause
        printError(next, "Caused by $next", sb) //$NON-NLS-1$
        if (t is SQLException) {
            // Handle SQLException specifically.
            next = t.nextException
            printError(next, "Next exception: $next", sb) //$NON-NLS-1$
        } else if (t is InvocationTargetException) {
            // Handle InvocationTargetException specifically.
            next = t.targetException
            printError(next, "Target exception: $next", sb) //$NON-NLS-1$
        }
    }

    private fun printStackTraceElement(ste: StackTraceElement, sb: StringBuilder): StringBuilder {
        sb.append("${ste.className}.${ste.methodName}(${
            when {
                ste.isNativeMethod -> "Native Method"
                ste.fileName != null -> "${ste.fileName}${if (ste.lineNumber >= 0) ":${ste.lineNumber}" else ""}"
                else -> "Unknown Source"
            }
        })")
        return sb
    }
}

object IoUtils {
    fun gunzip(input: InputStream): ByteArray {
        return GZIPInputStream(input).use {
            val baos = ByteArrayOutputStream()
            it.copyTo(baos, 256)
            baos.toByteArray()
        }
    }

    fun gzip(data: ByteArray): ByteArray {
        val baos3 = ByteArrayOutputStream()
        data.inputStream().use { ins ->
            GZIPOutputStream(baos3).use {
                ins.copyTo(it)
            }
        }
        return baos3.toByteArray()
    }


    fun gunzip(input: ByteArray): ByteArray {
        return GZIPInputStream(input.inputStream()).use {
            val baos = ByteArrayOutputStream()
            it.copyTo(baos, 256)
            baos.toByteArray()
        }
    }
}

object AuthUtils {
    private val users = ThreadLocal<String>()
    fun setCurrentUser(user: String) {
        users.set(user)
    }

    fun resetCurrentUser() {
        users.remove()
    }

    fun getCurrentUser():String = users.get()
}

object CommonUiUtils{
    fun toSelectItem(ref:ObjectReference<*>?) :SelectItem?{
        return ref?.let {
            SelectItem("${it.type.qualifiedName}|${it.uid}", it.caption!!)
        }
    }

    fun<D:BaseIdentity> toObjectReference(item:SelectItem?):ObjectReference<D>?{
        return item?.let {
            ObjectReference(ReflectionFactory.get().getClass<D>(it.id.substringBefore("|")), it.id.substringAfter("|"), it.text)
        }
    }
}