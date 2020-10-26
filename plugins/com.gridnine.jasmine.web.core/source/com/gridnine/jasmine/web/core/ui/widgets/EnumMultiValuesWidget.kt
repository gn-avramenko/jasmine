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

class EnumMultiValuesWidget<E:Enum<E>>(aParent:WebComponent, configure:EnumMultiValuesWidgetConfiguration.()->Unit ):WebComponent{
    private val className:String
    private val delegate:WebSelect
    private val parent:WebComponent = aParent
    private val children = arrayListOf<WebComponent>()
    init{
        (parent.getChildren() as MutableList<WebComponent>).add(this)
        val conf = EnumMultiValuesWidgetConfiguration();
        conf.configure()
        className = conf.enumClassName
        delegate = UiLibraryAdapter.get().createSelect(this){
            width = conf.width
            height = conf.height
            mode = SelectDataType.LOCAL
            showClearIcon = conf.showClearIcon
            multiple = true
        }
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
    fun getValues():List<E> {
        return delegate.getValues().map { ReflectionFactoryJS.get().getEnum<E>(className, it.id) }
    }

    fun setValues(values: List<E>) {
        delegate.setValues(values.map {
            val domainDescription = DomainMetaRegistryJS.get().enums[className]
            if (domainDescription != null) {
                SelectItemJS(it.name, domainDescription.items[it.name]!!.displayName)
            } else {
                val uiDescription = UiMetaRegistryJS.get().enums[className]
                SelectItemJS(it.name, uiDescription!!.items[it.name]!!.displayName)
            }
        })
    }

    override fun getParent(): WebComponent? {
        return parent
    }

    override fun getChildren(): List<WebComponent> {
        return children
    }

    override fun getHtml(): String {
        return delegate.getHtml()
    }

    override fun decorate() {
        delegate.decorate()
    }

    override fun destroy() {
        delegate.destroy()
    }

}

class EnumMultiValuesWidgetConfiguration{
    var width:String? = null
    var height:String? = null
    lateinit var enumClassName: String
    var showClearIcon = false
}