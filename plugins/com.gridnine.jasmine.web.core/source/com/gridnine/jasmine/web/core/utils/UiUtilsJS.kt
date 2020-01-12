/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UnsafeCastFromDynamic", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.core.utils

import com.gridnine.jasmine.web.core.model.domain.DomainMetaRegistryJS
import com.gridnine.jasmine.web.core.model.ui.SelectItemJS

object UiUtilsJS {
    fun getEnumValues(enumId: String):List<SelectItemJS>{
        val enumDescr = DomainMetaRegistryJS.get().enums[enumId]?:throw IllegalArgumentException("unable to find description of $enumId")
        return enumDescr.items.values.mapNotNull { SelectItemJS(it.id, it.displayName) }.sortedBy { it.caption }.toList()
    }
}
