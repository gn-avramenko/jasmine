/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UnsafeCastFromDynamic", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.core.ui

import com.gridnine.jasmine.web.core.model.ui.BaseVVEntityJS
import com.gridnine.jasmine.web.core.model.ui.UiMetaRegistryJS
import com.gridnine.jasmine.web.core.utils.ReflectionFactoryJS

object ValidationUtilsJS {
    fun hasValidationErrors(vv: BaseVVEntityJS): Boolean {
        val description  = UiMetaRegistryJS.get().viewValidations[ReflectionFactoryJS.get().getQualifiedClassName(vv::class)]!!
        for (property in description.properties.values) {
            if (vv.getValue(property.id) != null) {
                return true
            }
        }
        return false
    }
}