/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("unused")

package com.gridnine.jasmine.web.standard.widgets

import com.gridnine.jasmine.common.core.meta.DomainMetaRegistryJS
import com.gridnine.jasmine.common.core.meta.UiMetaRegistryJS
import com.gridnine.jasmine.common.core.model.SelectItemJS
import com.gridnine.jasmine.web.core.reflection.ReflectionFactoryJS
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.components.*

class EnumMultiValuesWidget<E:Enum<E>>(configure:EnumMultiValuesWidgetConfiguration.()->Unit ):BaseWebNodeWrapper<WebSelect>(){

    private val config = EnumMultiValuesWidgetConfiguration()

    init{
        config.configure()
        _node = WebUiLibraryAdapter.get().createSelect{
            width = config.width
            height = config.height
            mode = SelectDataType.LOCAL
            showClearIcon = config.showClearIcon
            multiple = true
        }
        val possibleValues = arrayListOf<SelectItemJS>()
        val domainDescription = DomainMetaRegistryJS.get().enums[config.enumClassName]
        domainDescription?.items?.values?.forEach {
            possibleValues.add(SelectItemJS(it.id, it.displayName!!))
        }
        val uiDescription = UiMetaRegistryJS.get().enums[config.enumClassName]
        uiDescription?.items?.values?.forEach {
            possibleValues.add(SelectItemJS(it.id, it.displayName!!))
        }
        _node.setPossibleValues(possibleValues)
    }
    fun getValues():List<E> {
        return _node.getValues().map { ReflectionFactoryJS.get().getEnum(config.enumClassName, it.id) }
    }

    fun setValues(values: List<E>) {
        _node.setValues(values.map {
            val domainDescription = DomainMetaRegistryJS.get().enums[config.enumClassName]
            if (domainDescription != null) {
                SelectItemJS(it.name, domainDescription.items[it.name]!!.displayName!!)
            } else {
                val uiDescription = UiMetaRegistryJS.get().enums[config.enumClassName]
                SelectItemJS(it.name, uiDescription!!.items[it.name]!!.displayName!!)
            }
        })
    }

}

class EnumMultiValuesWidgetConfiguration:BaseWidgetConfiguration(){
    lateinit var enumClassName: String
    var showClearIcon = false
}