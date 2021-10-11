/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 *****************************************************************/
@file:Suppress("unused")

package com.gridnine.jasmine.server.standard.helpers

import com.gridnine.jasmine.common.core.meta.UiMetaRegistry
import com.gridnine.jasmine.common.core.meta.VVPropertyType
import com.gridnine.jasmine.common.core.model.BaseVV

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
        for (property in description.collections.values) {
            val value = vv.getCollection(property.id)
            if(value.any { hasValidationErrors(it as BaseVV) }){
                return true
            }
        }
        return false
    }
}