/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 *****************************************************************/
@file:Suppress("unused")
package com.gridnine.jasmine.server.core.utils

import java.lang.reflect.InvocationTargetException
import java.sql.SQLException


fun String?.isBlankOrNull():Boolean{
    return this == null||this.isEmpty()
}

object TextUtils {
    fun isBlank(str: String?): Boolean {
        return str == null || str.isBlank()
    }
    fun isNotBlank(str:String?):Boolean{
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
         when{
             ste.isNativeMethod -> "Native Method"
             ste.fileName != null -> "${ste.fileName}${if(ste.lineNumber >= 0) ":${ste.lineNumber}" else ""}"
             else ->"Unknown Source"
         }
        })")
        return sb
    }
}