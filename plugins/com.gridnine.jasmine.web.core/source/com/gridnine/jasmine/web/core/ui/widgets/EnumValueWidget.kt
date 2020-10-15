/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.widgets

import com.gridnine.jasmine.server.core.model.domain.DomainMetaRegistryJS
import com.gridnine.jasmine.server.core.model.ui.UiMetaRegistryJS
import com.gridnine.jasmine.web.core.reflection.ReflectionFactoryJS
import com.gridnine.jasmine.web.core.ui.UiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.WebComponent
import com.gridnine.jasmine.web.core.ui.components.*
import kotlin.reflect.KClass

class EnumValueWidget<E:Enum<E>>(parent:WebComponent, configure:EnumValueWidgetConfiguration<E>.()->Unit
                                 , private val delegate: WebSelect = UiLibraryAdapter.get().createSelect(parent, convertConfiguration(configure)) ):WebComponent by delegate{
    private val className:String
    fun getValue():E? {

        val values = delegate.getValues()
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
                SelectItemJS(value.name, domainDescription.items[value.name]!!.displayName)
            } else {
                val uiDescription = UiMetaRegistryJS.get().enums[className]
                SelectItemJS(value.name, uiDescription!!.items[value.name]!!.displayName)
            })
        }
        delegate.setValues(values)
    }

    init {
        val conf = EnumValueWidgetConfiguration<E>();
        conf.configure()
        className = ReflectionFactoryJS.get().getQualifiedClassName(conf.enumClass)
        val possibleValues = arrayListOf<SelectItemJS>()
        val domainDescription = DomainMetaRegistryJS.get().enums[className]
        domainDescription?.items?.values?.forEach {
            possibleValues.add(SelectItemJS(it.id, it.displayName))
        }
        val uiDescription = UiMetaRegistryJS.get().enums[className]
        uiDescription?.items?.values?.forEach {
            possibleValues.add(SelectItemJS(it.id, it.displayName))
        }
        delegate.setPossibleValues(possibleValues)
    }


    companion object{
        fun <E:Enum<E>> convertConfiguration(configure: EnumValueWidgetConfiguration<E>.() -> Unit): WebSelectConfiguration.() -> Unit {
            val conf = EnumValueWidgetConfiguration<E>();
            conf.configure()
            return {
                width = conf.width
                height = conf.height
                mode = SelectDataType.LOCAL
                editable = false
                multiple = false
                showClearIcon = conf.allowNull
            }
        }

    }
}

class EnumValueWidgetConfiguration<E:Enum<E>>{
    var width:String? = null
    var height:String? = null
    var allowNull = true
    lateinit var enumClass: KClass<E>
}