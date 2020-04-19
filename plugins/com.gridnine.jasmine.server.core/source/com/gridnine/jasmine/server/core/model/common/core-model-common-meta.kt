/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")

package com.gridnine.jasmine.server.core.model.common

import com.gridnine.jasmine.server.core.utils.LocalizationUtils
import java.util.*

@Suppress("UNCHECKED_CAST")
abstract class BaseModelElementDescription(val id:String){
    val displayNames = hashMapOf<Locale, String>()
    val parameters = linkedMapOf<String, String>()

    fun getDisplayName(): String? {
        return displayNames[LocalizationUtils.getCurrentLocale()]
                ?:displayNames[LocalizationUtils.EN_LOCALE]
    }
}


