/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UnsafeCastFromDynamic", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.core.utils

import com.gridnine.jasmine.web.core.model.domain.DomainMetaRegistryJS
import com.gridnine.jasmine.web.core.model.ui.BaseView
import com.gridnine.jasmine.web.core.model.ui.Editor
import com.gridnine.jasmine.web.core.model.ui.SelectItemJS
import com.gridnine.jasmine.web.core.model.ui.WidgetWithParent

object UiUtilsJS {
    fun getEnumValues(enumId: String):List<SelectItemJS>{
        val enumDescr = DomainMetaRegistryJS.get().enums[enumId]?:throw IllegalArgumentException("unable to find description of $enumId")
        return enumDescr.items.values.mapNotNull { SelectItemJS(it.id, it.displayName) }.sortedBy { it.caption }.toList()
    }
    fun findEditor(obj:Any?):Editor<*,*,*,*>?{
        if(obj == null){
            return null
        }
        if(obj is BaseView<*,*,*>){
            if(obj.parent is Editor<*,*,*,*>){
                return obj.parent as Editor<*, *, *, *>
            }
            return findEditor(obj.parent)
        }
        if(obj is WidgetWithParent){
            return findEditor(obj.parent)
        }
        return null
    }
}
