/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UnsafeCastFromDynamic", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.core.ui

import com.gridnine.jasmine.server.core.model.common.BaseIntrospectableObjectJS
import com.gridnine.jasmine.server.core.model.ui.BaseVMJS
import com.gridnine.jasmine.server.core.model.ui.BaseVSJS
import com.gridnine.jasmine.server.core.model.ui.BaseVVJS
import com.gridnine.jasmine.web.core.application.EnvironmentJS
import com.gridnine.jasmine.web.core.ui.components.*
import org.w3c.dom.DOMMatrixReadOnly

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
    fun createDateBox(parent: WebComponent?, configure:WebDateBoxConfiguration.()->Unit):WebDateBox
    fun createDateTimeBox(parent: WebComponent?, configure:WebDateTimeBoxConfiguration.()->Unit):WebDateTimeBox
    fun createNumberBox(parent: WebComponent?, configure:WebNumberBoxConfiguration.()->Unit):WebNumberBox
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
    fun destroy()
}

interface HasVisibility{
    fun setVisible(value:Boolean)
}
interface WebEditor<VM:BaseVMJS, VS:BaseVSJS, VV:BaseVVJS>:WebComponent{
    fun readData(vm:VM, vs:VS)
    fun setReadonly(value:Boolean)
}

object DefaultUIParameters{
    var controlWidth = 200
    var controlWidthAsString = "200px"
}

open class RegistryItemType<T:Any>(val id:String)

interface RegistryItem<T:Any> {
    fun getType(): RegistryItemType<T>
    fun getId(): String
}

interface ObjectHandler:RegistryItem<ObjectHandler>{

    fun getAutocompleteHandler():AutocompleteHandler

    fun createWebEditor(parent:WebComponent):WebEditor<*,*,*>

    override fun getType(): RegistryItemType<ObjectHandler> {
        return TYPE
    }
    companion object{
        val TYPE = RegistryItemType<ObjectHandler>("object-handlers")
    }
}

abstract class BaseObjectHandler(val objectId:String):ObjectHandler{

    private var cachedHandler:AutocompleteHandler? = null

    abstract fun createAutocompleteHandler():AutocompleteHandler

    override fun getAutocompleteHandler(): AutocompleteHandler {
        if(cachedHandler == null){
            cachedHandler = createAutocompleteHandler()
        }
        return cachedHandler!!
    }


}

class ClientRegistry{
    private val registry = hashMapOf<String, MutableMap<String, RegistryItem<*>>>()

    fun register(item:RegistryItem<*>){
        registry.getOrPut(item.getType().id, { hashMapOf()})[item.getId()] = item
    }

    fun<T:Any> allOf(type: RegistryItemType<T>):List<RegistryItem<T>> = (registry[type.id]?.values?.toList() as List<RegistryItem<T>>?)?: emptyList()

    fun <T:Any> get(type:RegistryItemType<T>, id:String)= registry[type.id]?.get(id) as T?

    companion object{
        fun get() = EnvironmentJS.getPublished(ClientRegistry::class)
    }
}

enum class FakeEnumJS


interface EditorButtonHandler<W:WebEditor<*,*,*>>{
    fun isApplicable(editor:W):Boolean
    fun isEnabled(editor: W, readOnly: Boolean):Boolean
    fun onClick(editor:W):Boolean
}

external var debugger: dynamic = definedExternally