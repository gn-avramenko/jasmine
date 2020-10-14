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
import com.gridnine.jasmine.web.core.ui.components.ComboboxMode
import com.gridnine.jasmine.web.core.ui.components.SelectItemJS
import com.gridnine.jasmine.web.core.ui.components.WebComboBox
import com.gridnine.jasmine.web.core.ui.components.WebComboBoxConfiguration
import kotlin.reflect.KClass

class EnumComboBoxWidget<E:Enum<E>>(parent:WebComponent, configure:EnumComboBoxWidgetConfiguration<E>.()->Unit
                                    , private val delegate: WebComboBox = UiLibraryAdapter.get().createCombobox(parent, EnumComboBoxWidget.convertConfiguration(configure)) ):WebComponent by delegate{
    private val className:String
    fun getValue():E? {

        val values = delegate.getValues()
        if(values.isEmpty()){
            return null
        }
        val enumId = values[0]
        return ReflectionFactoryJS.get().getEnum<E>(className, enumId)
    }

    fun setValue(value: E?) {
        delegate.setValues(value?.let { arrayListOf(value.name) }?: emptyList())
    }

    init {
        val conf = EnumComboBoxWidgetConfiguration<E>();
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
        fun <E:Enum<E>> convertConfiguration(configure: EnumComboBoxWidgetConfiguration<E>.() -> Unit): WebComboBoxConfiguration.() -> Unit {
            val conf = EnumComboBoxWidgetConfiguration<E>();
            conf.configure()
            return {
                width = conf.width
                height = conf.height
                mode = ComboboxMode.LOCAL
                editable = false
            }
        }

    }
}

class EnumComboBoxWidgetConfiguration<E:Enum<E>>{
    var width:String? = null
    var height:String? = null
    lateinit var enumClass: KClass<E>
}