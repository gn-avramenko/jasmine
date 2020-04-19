/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 *****************************************************************/
@file:Suppress("unused")
package com.gridnine.jasmine.server.core.utils

import java.util.*

object LocalizationUtils {
    val RU_LOCALE = Locale("ru", "")
    val EN_LOCALE = Locale("en")

    fun getCurrentLocale(): Locale {
        return RU_LOCALE
    }
}

