/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.mainframe

import com.gridnine.jasmine.server.core.model.domain.ObjectReference
import com.gridnine.jasmine.server.core.model.ui.BaseVM
import com.gridnine.jasmine.server.core.model.ui.BaseVS
import com.gridnine.jasmine.server.core.model.ui.BaseVV
import com.gridnine.jasmine.server.standard.helpers.UiEditorHelper
import com.gridnine.jasmine.web.server.common.*
import com.gridnine.jasmine.web.server.components.*
import kotlin.reflect.KClass

interface ServerUiObjectHandler:ServerUiRegistryItem<ServerUiObjectHandler>{

    fun createEditor():ServerUiViewEditor<*,*,*>

    override fun getType(): ServerUiRegistryItemType<ServerUiObjectHandler> {
        return TYPE
    }

    companion object{
        val TYPE = ServerUiRegistryItemType<ServerUiObjectHandler>("object-handlers")
    }
}

abstract class BaseServerUiObjectHandler(private val cls: KClass<*>):ServerUiObjectHandler{

    override fun getId(): String {
        return cls.java.name
    }
}

class ServerUiObjectEditorHandlerData(val obj: ObjectReference<*>, val navigationKey:String?)
class ServerUiObjectEditorHandler : ServerUiMainFrameTabHandler<ServerUiObjectEditorHandlerData>{
    override fun getTabId(obj: ServerUiObjectEditorHandlerData): String {
        return "${obj.obj.type.qualifiedName}||${obj.obj.uid}"
    }

    override fun createTabData(obj:ServerUiObjectEditorHandlerData, callback: ServerUiMainFrameTabCallback): ServerUiMainFrameTabData {
        val bundle = UiEditorHelper.getReadDataBundle(obj.obj.type.java.name, obj.obj.uid)
        val handler:ServerUiObjectHandler = ServerUiClientRegistry.get().get(ServerUiObjectHandler.TYPE, obj.obj.type)!!
        val editor = ServerUiObjectEditor(obj.obj, bundle.vm,bundle.vs, handler.createEditor(), true,obj.navigationKey, callback)
        return ServerUiMainFrameTabData(bundle.title, editor)
    }

}

interface ServerUiObjectEditorButton<VM:BaseVM, W:ServerUiViewEditor<VM,*,*>>:ServerUiRegistryItem<ServerUiObjectEditorButton<BaseVM, ServerUiViewEditor<BaseVM,*,*>>>,ServerUiHasWeight{
    fun isApplicable(vm:VM, editor: ServerUiObjectEditor<W>):Boolean
    fun onClick(value: ServerUiObjectEditor<W>)
    fun getDisplayName():String
    override fun getType(): ServerUiRegistryItemType<ServerUiObjectEditorButton<BaseVM, ServerUiViewEditor<BaseVM,*,*>>>{
        return TYPE
    }
    companion object{
        val TYPE = ServerUiRegistryItemType<ServerUiObjectEditorButton<BaseVM, ServerUiViewEditor<BaseVM,*,*>>>("editor-button-handlers")
    }
}


interface ServerUiObjectEditorMenuItem<VM:BaseVM, W:ServerUiViewEditor<VM,*,*>>:ServerUiRegistryItem<ServerUiObjectEditorMenuItem<BaseVM, ServerUiViewEditor<BaseVM,*,*>>>,ServerUiHasWeight{
    fun isApplicable(vm:VM, editor: ServerUiObjectEditor<W>):Boolean
    fun onClick(value: ServerUiObjectEditor<W>)
    fun getDisplayName():String
    fun getMenuButtonId():String
    override fun getType(): ServerUiRegistryItemType<ServerUiObjectEditorMenuItem<BaseVM, ServerUiViewEditor<BaseVM,*,*>>>{
        return TYPE
    }
    companion object{
        val TYPE = ServerUiRegistryItemType<ServerUiObjectEditorMenuItem<BaseVM, ServerUiViewEditor<BaseVM,*,*>>>("editor-menu-item-handlers")
    }
}

class ServerUiObjectEditor<V:ServerUiViewEditor<*,*,*>>(val reference: ObjectReference<*>, vm:BaseVM, vs:BaseVS, val rootEditor:V, var readOnly:Boolean, private val navigationKey:String?, private val callback: ServerUiMainFrameTabCallback):BaseServerUiNodeWrapper<ServerUiBorderContainer>(){

    init {
        _node = ServerUiLibraryAdapter.get().createBorderLayout(ServerUiBorderContainerConfiguration {
            width = "100%"
            height = "100%"
        })
        updateTools(vm)
        _node.setCenterRegion(ServerUiBorderContainerRegion{
            content = rootEditor
        })
        (rootEditor as ServerUiViewEditor<BaseVM,BaseVS,BaseVV>).setData(vm, vs)
        if(navigationKey != null) {
            rootEditor.navigate(navigationKey)
        }
        rootEditor.setReadonly(readOnly)
    }

    fun updateTools(vm: BaseVM) {
        val buttons = ServerUiClientRegistry.get().allOf(ServerUiObjectEditorButton.TYPE).filter { it.isApplicable(vm, ServerUiObjectEditor@this as ServerUiObjectEditor<ServerUiViewEditor<BaseVM, *, *>>) }.toMutableList()
        val menuButtons = ServerUiClientRegistry.get().allOf(ServerUiObjectEditorMenuItem.TYPE).filter {  it.isApplicable(vm, ServerUiObjectEditor@this as ServerUiObjectEditor<ServerUiViewEditor<BaseVM, *, *>>)  }
        val lst = arrayListOf<ServerUiHasWeight>()
        lst.addAll(buttons)
        lst.addAll(menuButtons.map { it.getMenuButtonId() }.distinct().map { ServerUiClientRegistry.get().get(ServerUiMainFrameMenuButton.TYPE, it) as ServerUiHasWeight})
        lst.sortBy { it.getWeight() }
        val buttonsGrid = ServerUiLibraryAdapter.get().createGridLayoutContainer(ServerUiGridLayoutContainerConfiguration {
            lst.forEach {
                columns.add(ServerUiGridLayoutColumnConfiguration("auto"))
            }
            columns.add(ServerUiGridLayoutColumnConfiguration("100%"))
            columns.add(ServerUiGridLayoutColumnConfiguration("auto"))
        })
        buttonsGrid.addRow()
        lst.forEach {
            if(it is ServerUiObjectEditorButton<*,*>){
                it as ServerUiObjectEditorButton<BaseVM, ServerUiViewEditor<BaseVM, *,*>>
                val button = ServerUiLibraryAdapter.get().createLinkButton(ServerUiLinkButtonConfiguration{
                    title = it.getDisplayName()
                })
                button.setHandler {
                    it.onClick(this@ServerUiObjectEditor as ServerUiObjectEditor<ServerUiViewEditor<BaseVM, *, *>>)
                }
                buttonsGrid.addCell(ServerUiGridLayoutCell(button))
            }
            if(it is ServerUiMainFrameMenuButton){
                val menuButton = ServerUiLibraryAdapter.get().createMenuButton(ServerUiMenuButtonConfiguration{
                    title = it.getDisplayName()
                    val buttons2 = menuButtons.filter { button -> button.getMenuButtonId() == it.getId() }.sortedBy { it.getWeight() }
                    buttons2.forEach { button2 ->
                        items.add(ServerUiMenuButtonStandardItem(button2.getId(), button2.getDisplayName(), null, false) {
                            button2.onClick(this@ServerUiObjectEditor as ServerUiObjectEditor<ServerUiViewEditor<BaseVM, *, *>>)
                        })
                    }
                })
                buttonsGrid.addCell(ServerUiGridLayoutCell(menuButton))
            }
        }
        buttonsGrid.addCell(ServerUiGridLayoutCell(null, 1))
        if(readOnly){
            val editButton = ServerUiLibraryAdapter.get().createLinkButton(ServerUiLinkButtonConfiguration{
                title = "Редактировать"
            })
            editButton.setHandler {
                readOnly = false
                rootEditor.setReadonly(false)
                updateTools(vm)
            }
            buttonsGrid.addCell(ServerUiGridLayoutCell(editButton))
        } else {
            val viewButton = ServerUiLibraryAdapter.get().createLinkButton(ServerUiLinkButtonConfiguration {
                title = "Просмотр"
            })
            viewButton.setHandler {
                readOnly = true
                rootEditor.setReadonly(true)
                updateTools(vm)
            }
            buttonsGrid.addCell(ServerUiGridLayoutCell(viewButton))
        }
        _node.setNorthRegion(ServerUiBorderContainerRegion{
            showSplitLine = false
            showBorder = false
            collapsible = false
            width = "100%"
            content = buttonsGrid
        })
    }

    fun updateTitle(title: String?) {
        callback.setTitle(title?:"???")
    }

}