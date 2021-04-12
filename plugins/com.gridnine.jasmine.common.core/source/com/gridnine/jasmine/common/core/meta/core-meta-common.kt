/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.common.core.meta

import com.gridnine.jasmine.common.core.utils.LocaleUtils
import java.util.*

abstract class BaseModelElementDescription(val id:String){
    val displayNames = hashMapOf<Locale, String>()
    val parameters = linkedMapOf<String, String>()

    fun getDisplayName(): String? {
        return displayNames[LocaleUtils.getCurrentLocale()]
                ?:displayNames[LocaleUtils.EN_LOCALE]
    }
}


