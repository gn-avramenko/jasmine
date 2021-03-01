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
import com.gridnine.jasmine.web.server.components.*
import com.gridnine.jasmine.web.server.registry.ServerUiClientRegistry
import com.gridnine.jasmine.web.server.registry.ServerUiRegistryItem
import com.gridnine.jasmine.web.server.registry.ServerUiRegistryItemType
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
        val buttonsGrid = ServerUiLibraryAdapter.get().createGridLayoutContainer(ServerUiGridLayoutContainerConfiguration {
            columns.add(ServerUiGridLayoutColumnConfiguration("100%"))
            columns.add(ServerUiGridLayoutColumnConfiguration("auto"))
        })
        buttonsGrid.addRow()
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

}