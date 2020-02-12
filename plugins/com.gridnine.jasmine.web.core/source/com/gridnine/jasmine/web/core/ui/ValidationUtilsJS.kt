/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UnsafeCastFromDynamic", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.core.ui

import com.gridnine.jasmine.web.core.model.ui.BaseVVEntityJS
import com.gridnine.jasmine.web.core.model.ui.TileDataJS
import com.gridnine.jasmine.web.core.model.ui.UiMetaRegistryJS
import com.gridnine.jasmine.web.core.utils.ReflectionFactoryJS

object ValidationUtilsJS {
    fun hasValidationErrors(vv: BaseVVEntityJS): Boolean {
        val description  = UiMetaRegistryJS.get().viewValidations[ReflectionFactoryJS.get().getQualifiedClassName(vv::class)]!!
        for (property in description.properties.values) {
            val value = vv.getValue(property.id)
            if(value is TileDataJS<*,*>){
                if(hasValidationErrors(value.compactData as BaseVVEntityJS) || hasValidationErrors(value.fullData as BaseVVEntityJS)){
                    return true
                }
            } else if (value!= null){
                return true
            }
        }
        return false
    }
}