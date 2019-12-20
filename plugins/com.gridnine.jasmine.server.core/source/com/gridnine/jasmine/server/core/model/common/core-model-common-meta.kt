/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
package com.gridnine.jasmine.server.core.model.common

import com.gridnine.jasmine.server.core.utils.LocalizationUtils
import java.util.*

abstract class BaseModelElementDescription{
    val displayNames = hashMapOf<Locale, String>()
    val parameters = linkedMapOf<String, String>()

    fun getDisplayName(): String? {
        return displayNames[LocalizationUtils.getCurrentLocale()]
                ?:displayNames[LocalizationUtils.EN_LOCALE]
    }
}

abstract class BaseIdentityDescription(val id:String):BaseModelElementDescription()

abstract class BaseOwnedIdentityDescription(owner:String?, id:String): BaseIdentityDescription(id){
    val fullId = if(owner != null) "$owner.$id" else id
}


