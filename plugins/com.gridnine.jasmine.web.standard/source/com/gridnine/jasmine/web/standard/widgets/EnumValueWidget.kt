/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("unused")

package com.gridnine.jasmine.web.standard.widgets

import com.gridnine.jasmine.common.core.meta.DomainMetaRegistryJS
import com.gridnine.jasmine.common.core.meta.UiMetaRegistryJS
import com.gridnine.jasmine.common.core.model.EnumSelectBoxConfigurationJS
import com.gridnine.jasmine.common.core.model.SelectItemJS
import com.gridnine.jasmine.web.core.reflection.ReflectionFactoryJS
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.components.*
import kotlin.reflect.KClass

class EnumValueWidget<E:Enum<E>>(configure:EnumValueWidgetConfiguration<E>.()->Unit):BaseWebNodeWrapper<WebSelect>(){
    private val conf =  EnumValueWidgetConfiguration<E>()
    private val className:String
    init {

        conf.configure()
        className = conf.enumClassName?:ReflectionFactoryJS.get().getQualifiedClassName(conf.enumClass)
        _node = WebUiLibraryAdapter.get().createSelect{
            width = conf.width
            height = conf.height
            mode = SelectDataType.LOCAL
            editable = false
            multiple = false
            showClearIcon = conf.allowNull
        }
        val possibleValues = arrayListOf<SelectItemJS>()
        val domainDescription = DomainMetaRegistryJS.get().enums[className]
        domainDescription?.items?.values?.forEach {
            possibleValues.add(SelectItemJS(it.id, it.displayName!!))
        }
        val uiDescription = UiMetaRegistryJS.get().enums[className]
        uiDescription?.items?.values?.forEach {
            possibleValues.add(SelectItemJS(it.id, it.displayName!!))
        }
        _node.setPossibleValues(possibleValues)
    }

    fun getValue():E? {

        val values = _node.getValues()
        if(values.isEmpty()){
            return null
        }
        val enumId = values[0]
        return ReflectionFactoryJS.get().getEnum<E>(className, enumId.id)
    }

    fun setValue(value: E?) {
        val values = arrayListOf<SelectItemJS>()
        if(value != null){
            val domainDescription = DomainMetaRegistryJS.get().enums[className]
            values.add(if (domainDescription != null) {
                SelectItemJS(value.name, domainDescription.items[value.name]!!.displayName!!)
            } else {
                val uiDescription = UiMetaRegistryJS.get().enums[className]
                SelectItemJS(value.name, uiDescription!!.items[value.name]!!.displayName!!)
            })
        }
        _node.setValues(values)
    }


    fun configure(config: EnumSelectBoxConfigurationJS?){
        config?.let {
            _node.setEnabled(!config.notEditable)
        }
    }

    fun showValidation(value:String?){
        _node.showValidation(value)
    }

}

class EnumValueWidgetConfiguration<E:Enum<E>>:BaseWidgetConfiguration(){
    var allowNull = true
    lateinit var enumClass: KClass<E>
    var enumClassName:String? = null
}