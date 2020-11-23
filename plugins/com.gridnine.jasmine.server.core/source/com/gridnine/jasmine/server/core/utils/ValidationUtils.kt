/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 *****************************************************************/
@file:Suppress("unused")

package com.gridnine.jasmine.server.core.utils

import com.gridnine.jasmine.server.core.model.ui.BaseVV
import com.gridnine.jasmine.server.core.model.ui.UiMetaRegistry
import com.gridnine.jasmine.server.core.model.ui.VVPropertyType

object ValidationUtils {
    fun hasValidationErrors(vv: BaseVV): Boolean {
        val description = UiMetaRegistry.get().viewValidations[vv::class.qualifiedName]!!
        for (property in description.properties.values) {
            val value = vv.getValue(property.id) ?: continue
            if (property.type == VVPropertyType.ENTITY && !hasValidationErrors(value as BaseVV)) {
                continue
            }
            return true
        }
        return false
    }
}