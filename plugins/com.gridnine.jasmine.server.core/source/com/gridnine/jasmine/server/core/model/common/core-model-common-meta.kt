/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")

package com.gridnine.jasmine.server.core.model.common

import com.gridnine.jasmine.server.core.utils.LocaleUtils
import java.util.*

@Suppress("UNCHECKED_CAST")
abstract class BaseModelElementDescription(val id:String){
    val displayNames = hashMapOf<Locale, String>()
    val parameters = linkedMapOf<String, String>()

    fun getDisplayName(): String? {
        return displayNames[LocaleUtils.getCurrentLocale()]
                ?:displayNames[LocaleUtils.EN_LOCALE]
    }
}


