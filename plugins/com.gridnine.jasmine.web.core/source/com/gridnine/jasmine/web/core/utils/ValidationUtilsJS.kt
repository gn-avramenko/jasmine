/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UnsafeCastFromDynamic", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.core.utils

import com.gridnine.jasmine.server.core.model.ui.BaseVVJS
import com.gridnine.jasmine.server.core.model.ui.UiMetaRegistryJS
import com.gridnine.jasmine.web.core.reflection.ReflectionFactoryJS

object ValidationUtilsJS {
    fun hasValidationErrors(vv: BaseVVJS): Boolean {
        val description  = UiMetaRegistryJS.get().viewValidations[ReflectionFactoryJS.get().getQualifiedClassName(vv::class)]!!
        for (property in description.properties.values) {
            val value = vv.getValue(property.id)
            if (value!= null){
                return true
            }
        }
        return false
    }
}