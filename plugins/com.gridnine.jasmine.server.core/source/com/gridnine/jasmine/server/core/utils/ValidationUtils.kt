/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 *****************************************************************/
@file:Suppress("unused")

package com.gridnine.jasmine.server.core.utils

import com.gridnine.jasmine.server.core.model.ui.BaseVV
import com.gridnine.jasmine.server.core.model.ui.UiMetaRegistry

object ValidationUtils{
    fun hasValidationErrors(vv: BaseVV): Boolean {
        val description  = UiMetaRegistry.get().viewValidations[vv::class.qualifiedName]!!
        for (property in description.properties.values) {
            val value = vv.getValue(property.id)
            if (value!= null){
                return true
            }
        }
        return false
    }
}