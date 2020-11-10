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
import com.gridnine.jasmine.web.core.CoreWebMessagesJS
import com.gridnine.jasmine.web.core.application.EnvironmentJS
import com.gridnine.jasmine.web.core.mainframe.ObjectEditor
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
    fun createPasswordBox(parent: WebComponent?, configure:WebPasswordBoxConfiguration.()->Unit):WebPasswordBox
    fun createLinkButton(parent: WebComponent?, configure:WebLinkButtonConfiguration.()->Unit):WebLinkButton
    fun createDateBox(parent: WebComponent?, configure:WebDateBoxConfiguration.()->Unit):WebDateBox
    fun createDateTimeBox(parent: WebComponent?, configure:WebDateTimeBoxConfiguration.()->Unit):WebDateTimeBox
    fun createNumberBox(parent: WebComponent?, configure:WebNumberBoxConfiguration.()->Unit):WebNumberBox
    fun createSelect(parent: WebComponent, configure: WebSelectConfiguration.() -> Unit): WebSelect
    fun<W> showDialog(popupChild:WebComponent?, configure:DialogConfiguration<W>.()->Unit):WebDialog<W>  where W:WebComponent, W:HasDivId
    fun createMenuButton(parent: WebComponent?, configure:WebMenuButtonConfiguration.()->Unit):WebMenuButton
    fun showLoader()
    fun hideLoader()
    fun showNotification(message:String, timeout:Int)


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
interface HasDivId{
    fun getId():String
}

interface WebPopupContainer:HasDivId,WebComponent

interface HasVisibility{
    fun setVisible(value:Boolean)
}

interface WebEditor<VM:BaseVMJS, VS:BaseVSJS, VV:BaseVVJS>:WebComponent{
    fun getData():VM
    fun readData(vm:VM, vs:VS)
    fun setReadonly(value:Boolean)
    fun showValidation(validation: VV)
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

    fun<T:Any> allOf(type: RegistryItemType<T>):List<T> = (registry[type.id]?.values?.toList() as List<T>?)?: emptyList()

    fun <T:Any> get(type:RegistryItemType<T>, id:String)= registry[type.id]?.get(id) as T?

    companion object{
        fun get() = EnvironmentJS.getPublished(ClientRegistry::class)
    }
}

enum class FakeEnumJS

interface HasWeight{
    fun getWeight():Double
}

interface ObjectEditorButton<W:WebEditor<*,*,*>>:RegistryItem<ObjectEditorButton<WebEditor<*,*,*>>>,HasWeight{
    fun isApplicable(objectId:String):Boolean
    fun isEnabled(value: ObjectEditor<W>):Boolean
    fun onClick(value: ObjectEditor<W>)
    fun getIcon():String?
    fun getDisplayName():String
    override fun getType(): RegistryItemType<ObjectEditorButton<WebEditor<*,*,*>>>{
        return TYPE
    }
    companion object{
        val TYPE = RegistryItemType<ObjectEditorButton<WebEditor<*,*,*>>>("editor-button-handlers")
    }
}

interface MenuButton:RegistryItem<MenuButton>,HasWeight{
    fun getIcon():String?
    fun getDisplayName():String
    override fun getType(): RegistryItemType<MenuButton>{
        return TYPE
    }
    companion object{
        val TYPE = RegistryItemType<MenuButton>("menu-buttons-handlers")
    }
}


interface ObjectEditorMenuItem<W:WebEditor<*,*,*>>:RegistryItem<ObjectEditorMenuItem<WebEditor<*,*,*>>>,HasWeight{
    fun isApplicable(objectId:String):Boolean
    fun isEnabled(value: ObjectEditor<W>):Boolean
    fun onClick(value: ObjectEditor<W>)
    fun getIcon():String?
    fun getDisplayName():String
    fun getMenuButtonId():String
    override fun getType(): RegistryItemType<ObjectEditorMenuItem<WebEditor<*,*,*>>>{
        return TYPE
    }
    companion object{
        val TYPE = RegistryItemType<ObjectEditorMenuItem<WebEditor<*,*,*>>>("editor-menu-item-handlers")
    }
}

interface ObjectsList<E:BaseIntrospectableObjectJS>{
    fun getDataGrid():WebDataGrid<E>
}
interface ListButtonHandler<E:BaseIntrospectableObjectJS>:RegistryItem<ListButtonHandler<*>>,HasWeight{
    fun isApplicable(objectId:String):Boolean
    fun isEnabled(value: ObjectsList<E>):Boolean
    fun onClick(value: ObjectsList<E>)
    fun getIcon():String?
    fun getDisplayName():String
    override fun getType(): RegistryItemType<ListButtonHandler<*>>{
        return TYPE
    }
    companion object{
        val TYPE = RegistryItemType<ListButtonHandler<*>>("list-button-handlers")
    }
}


interface WebDialog<W> where W:WebComponent, W:HasDivId{
    fun close()
    fun getContent():W
}

class DialogButtonConfiguration<W> where W:WebComponent, W:HasDivId{
    lateinit var displayName:String
    lateinit var handler:(WebDialog<W>)  ->Unit
}

class DialogConfiguration<W>  where W:WebComponent, W:HasDivId{
    constructor(conf:DialogConfiguration<W>.()->Unit){
        this.conf()
    }
    var expandToMainFrame = false
    lateinit var title:String
    lateinit var editor: W
    val buttons = arrayListOf<DialogButtonConfiguration<W>>()
    fun button(conf:DialogButtonConfiguration<W>.()->Unit){
        val button = DialogButtonConfiguration<W>()
        button.conf()
        buttons.add(button)
    }
    fun cancelButton(){
        val button = DialogButtonConfiguration<W>()
        button.displayName = CoreWebMessagesJS.cancel
        button.handler = {
            it.close()
        }
        buttons.add(button)
    }
}



external var debugger: dynamic = definedExternally