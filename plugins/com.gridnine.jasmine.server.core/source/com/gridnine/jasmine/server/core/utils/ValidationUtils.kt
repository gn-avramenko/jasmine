/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 *****************************************************************/
@file:Suppress("unused")

package com.gridnine.jasmine.server.core.utils

import com.gridnine.jasmine.server.core.model.ui.BaseVVEntity
import com.gridnine.jasmine.server.core.model.ui.UiMetaRegistry

object ValidationUtils{
    fun hasValidationErrors(vv: BaseVVEntity): Boolean {
        val description  = UiMetaRegistry.get().viewValidations[vv::class.qualifiedName]!!
        for (property in description.properties.values) {
            if (vv.getValue(property.id) != null) {
                return true
            }
        }
        return false
    }
}