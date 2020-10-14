/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UnsafeCastFromDynamic", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.core.ui

import com.gridnine.jasmine.server.core.model.common.BaseIntrospectableObjectJS
import com.gridnine.jasmine.web.core.application.EnvironmentJS
import com.gridnine.jasmine.web.core.ui.components.*

interface UiLibraryAdapter{
    fun showWindow(component: WebComponent)
    fun createBorderLayout(parent: WebComponent?, configure:WebBorderLayoutConfiguration.()->Unit):WebBorderContainer
    fun createLabel(parent: WebComponent?):WebLabel
    fun createAccordionContainer(parent: WebComponent?, configure:WebAccordionPanelConfiguration.()->Unit):WebAccordionContainer
    fun createTabsContainer(parent: WebComponent?, configure:WebTabsContainerConfiguration.()->Unit):WebTabsContainer
    fun<E:Any> createDataList(parent: WebComponent?, configure:WebDataListConfiguration.()->Unit):WebDataList<E>
    fun createGridLayoutContainer(parent: WebComponent?, configure:WebGridLayoutContainerConfiguration.()->Unit):WebGridLayoutContainer
    fun<E:BaseIntrospectableObjectJS> createDataGrid(parent: WebComponent?, configure:WebDataGridConfiguration<E>.()->Unit):WebDataGrid<E>
    fun createSearchBox(parent: WebComponent?, configure:WebSearchBoxConfiguration.()->Unit):WebSearchBox
    fun createTextBox(parent: WebComponent?, configure:WebTextBoxConfiguration.()->Unit):WebTextBox
    fun createLinkButton(parent: WebComponent?, configure:WebLinkButtonConfiguration.()->Unit):WebLinkButton
    fun createCombobox(parent: WebComponent?, configure:WebComboBoxConfiguration.()->Unit):WebComboBox
    fun createDateBox(parent: WebComponent?, configure:WebDateBoxConfiguration.()->Unit):WebDateBox
    fun createDateTimeBox(parent: WebComponent?, configure:WebDateTimeBoxConfiguration.()->Unit):WebDateTimeBox
    fun createNumberBox(parent: WebComponent?, configure:WebNumberBoxConfiguration.()->Unit):WebNumberBox
    fun createTagBox(parent: WebComponent?, configure:WebTagBoxConfiguration.()->Unit):WebTagBox
    fun createSelect(parent: WebComponent, configure: WebSelectConfiguration.() -> Unit): WebSelect

    companion object{
        fun get() = EnvironmentJS.getPublished(UiLibraryAdapter::class)
    }

}

interface WebComponent{
    fun getParent():WebComponent?
    fun getChildren():List<WebComponent>
    fun getHtml():String
    fun decorate()
}

object DefaultUIParameters{
    var controlWidth = 200
}

open class RegistryItemType<T:Any>(val id:String)

interface RegistryItem<T:Any> {
    val type: RegistryItemType<T>
    val id: String
}

class ClientRegistry{
    private val registry = hashMapOf<String, MutableMap<String, RegistryItem<*>>>()

    fun register(item:RegistryItem<*>){
        registry.getOrPut(item.type.id, { hashMapOf()})[item.id] = item
    }

    fun<T:Any> allOf(type: RegistryItemType<T>):List<RegistryItem<T>> = (registry[type.id]?.values?.toList() as List<RegistryItem<T>>?)?: emptyList()

    fun <T:Any> get(type:RegistryItemType<T>, id:String)= registry[type.id]?.get(id) as T?

    companion object{
        fun get() = EnvironmentJS.getPublished(ClientRegistry::class)
    }
}

enum class FakeEnumJS



external var debugger: dynamic = definedExternally