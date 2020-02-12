/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 *****************************************************************/
@file:Suppress("unused")

package com.gridnine.jasmine.server.core.utils

import com.gridnine.jasmine.server.core.model.ui.BaseVVEntity
import com.gridnine.jasmine.server.core.model.ui.TileData
import com.gridnine.jasmine.server.core.model.ui.UiMetaRegistry

object ValidationUtils{
    fun hasValidationErrors(vv: BaseVVEntity): Boolean {
        val description  = UiMetaRegistry.get().viewValidations[vv::class.qualifiedName]!!
        for (property in description.properties.values) {
            val value = vv.getValue(property.id)
            if(value is TileData<*,*>){
                if(hasValidationErrors(value.compactData as BaseVVEntity) || hasValidationErrors(value.fullData as BaseVVEntity)){
                    return true
                }
            } else if (value!= null){
                return true
            }
        }
        return false
    }
}