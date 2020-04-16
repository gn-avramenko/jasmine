/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")

package com.gridnine.jasmine.server.core.model.common

import com.gridnine.jasmine.server.core.utils.LocalizationUtils
import java.util.*

@Suppress("UNCHECKED_CAST")
abstract class BaseModelElementDescription(val id:String):BaseIntrospectableObject(){
    val displayNames = hashMapOf<Locale, String>()
    val parameters = linkedMapOf<String, String>()

    fun getDisplayName(): String? {
        return displayNames[LocalizationUtils.getCurrentLocale()]
                ?:displayNames[LocalizationUtils.EN_LOCALE]
    }

    override fun getMap(mapName: String): MutableMap<Any?, Any?> {
        if(BaseModelElementDescription.displayNames == mapName){
            return displayNames as MutableMap<Any?, Any?>
        }
        if(BaseModelElementDescription.parameters == mapName){
            return parameters as MutableMap<Any?, Any?>
        }
        return super.getMap(mapName)
    }

    companion object {
        const val displayNames  = "displayNames"
        const val parameters  = "parameters"
    }
}


