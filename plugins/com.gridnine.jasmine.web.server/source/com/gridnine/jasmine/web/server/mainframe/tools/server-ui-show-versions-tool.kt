/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.mainframe.tools

import com.gridnine.jasmine.server.core.model.domain.ObjectReference
import com.gridnine.jasmine.server.core.model.ui.BaseVM
import com.gridnine.jasmine.server.core.model.ui.BaseVS
import com.gridnine.jasmine.server.core.model.ui.BaseVV
import com.gridnine.jasmine.server.standard.helpers.UiVersionMetaData
import com.gridnine.jasmine.server.standard.helpers.UiVersionsHelper
import com.gridnine.jasmine.web.server.common.ServerUiRegistry
import com.gridnine.jasmine.web.server.common.ServerUiCommonUtils
import com.gridnine.jasmine.web.server.common.ServerUiObjectModificationEvent
import com.gridnine.jasmine.web.server.components.*
import com.gridnine.jasmine.web.server.mainframe.*

class ServerUiShowVersionsMenuItem :ServerUiObjectEditorMenuItem<BaseVM, ServerUiViewEditor<BaseVM, *,*>>{
    override fun isApplicable(vm: BaseVM, editor: ServerUiObjectEditor<ServerUiViewEditor<BaseVM, *, *>>): Boolean {
        return true
    }

    override fun onClick(value: ServerUiObjectEditor<ServerUiViewEditor<BaseVM, *, *>>) {
        val data = UiVersionsHelper.getVersionsMetadata(value.reference.type.java.name, value.reference.uid)
        val dialogContent = ServerUiVersionsListDialogContent(data)
        val dialog = ServerUiLibraryAdapter.get().showDialog(ServerUiDialogConfiguration {
            title = "Версии"
            editor = dialogContent
            cancelButton()
        })
        dialogContent.closeCallback = dialog::close
        dialogContent.openCallback = {
            ServerUiMainFrame.get().openTab(ServerUiVersionsViewerHandler() as ServerUiMainFrameTabHandler<Any>, ServerUiObjectVersionsViewerHandlerData(value.reference, it))
        }

    }

    override fun getDisplayName(): String {
        return "Показать версии"
    }

    override fun getMenuButtonId(): String {
        return ServerUiAdditionalMenuButton.ID
    }

    override fun getId(): String {
        return this::javaClass.name
    }

    override fun getWeight(): Double {
        return 10.0
    }

}

class ServerUiVersionsListDialogContent(val versions:List<UiVersionMetaData>): BaseServerUiNodeWrapper<ServerUiDataGridComponent<UiVersionMetaData>>(){
    lateinit var openCallback:(Int) -> Unit

    lateinit var closeCallback:()->Unit

    init {
        _node = ServerUiLibraryAdapter.get().createDataGrid(ServerUiDataGridComponentConfiguration{
            width ="600px"
            height = "500px"
            span = true
            columns.add(ServerUiDataGridColumnConfiguration{
                fieldId = UiVersionMetaData.versionField
                title = "Версия"
                width = "100px"
                sortable = false
                horizontalAlignment = ServerUiComponentHorizontalAlignment.RIGHT
            })
            columns.add(ServerUiDataGridColumnConfiguration{
                fieldId = UiVersionMetaData.modifiedField
                title = "Дата изменения"
                width = "100px"
                sortable = false
                horizontalAlignment = ServerUiComponentHorizontalAlignment.LEFT
            })
            columns.add(ServerUiDataGridColumnConfiguration{
                fieldId = UiVersionMetaData.modifiedByField
                title = "Автор"
                width = "100px"
                sortable = false
                horizontalAlignment = ServerUiComponentHorizontalAlignment.LEFT
            })
            columns.add(ServerUiDataGridColumnConfiguration{
                fieldId = UiVersionMetaData.commentField
                title = "Комментарии"
                width = "200px"
                sortable = false
                horizontalAlignment = ServerUiComponentHorizontalAlignment.LEFT
            })
        })
        _node.setLoader {
            var lastIndex = it.offSet+it.limit
            if(lastIndex > versions.size){
                lastIndex = versions.size
            }
            ServerUiDataGridResponse(versions.size, versions.subList(it.offSet, lastIndex))
        }
        _node.setDoubleClickListener {
            closeCallback.invoke()
            openCallback.invoke(it.version-1)
        }
        _node.setFormatter{ item,fieldId ->
            ServerUiCommonUtils.toString(item.getValue(fieldId))
        }
    }
}

class ServerUiObjectVersionViewer(val reference: ObjectReference<*>, versionNumber:Int, closeCallback:()->Unit):BaseServerUiNodeWrapper<ServerUiBorderContainer>() {

    init {
        _node = ServerUiLibraryAdapter.get().createBorderLayout(ServerUiBorderContainerConfiguration {
            width = "100%"
            height = "100%"
        })
        val buttonsGrid = ServerUiLibraryAdapter.get().createGridLayoutContainer(ServerUiGridLayoutContainerConfiguration {
            columns.add(ServerUiGridLayoutColumnConfiguration("auto"))
        })
        buttonsGrid.addRow()
        val restoreButton = ServerUiLibraryAdapter.get().createLinkButton(ServerUiLinkButtonConfiguration{
            title = "Восстановить"
        })
        restoreButton.setHandler {
            closeCallback.invoke()
            UiVersionsHelper.restoreVersion(reference.type.java.name, reference.uid, versionNumber)
            ServerUiMainFrame.get().publishEvent(ServerUiObjectModificationEvent(reference.type.java.name, reference.uid ))
            ServerUiLibraryAdapter.get().showNotification("Версия восстановлена", ServerUiNotificationType.INFO, 2000)
        }
        buttonsGrid.addCell(ServerUiGridLayoutCell(restoreButton))
        _node.setNorthRegion(ServerUiBorderContainerRegion{
            content = buttonsGrid
        })
        val bundle = UiVersionsHelper.getVersionReadBundle(reference.type.java.name, reference.uid, versionNumber)
        val handler: ServerUiObjectHandler = ServerUiRegistry.get().get(ServerUiObjectHandler.TYPE, reference.type)!!
        val rootEditor = handler.createEditor()
        _node.setCenterRegion(ServerUiBorderContainerRegion {
            content = rootEditor
        })
        (rootEditor as ServerUiViewEditor<BaseVM, BaseVS, BaseVV>).setData(bundle.vm, bundle.vs)
        rootEditor.setReadonly(true)
    }
}

class ServerUiObjectVersionsViewerHandlerData(val obj: ObjectReference<*>, val version:Int)

class ServerUiVersionsViewerHandler : ServerUiMainFrameTabHandler<ServerUiObjectVersionsViewerHandlerData> {
    override fun getTabId(obj: ServerUiObjectVersionsViewerHandlerData): String {
        return "${obj.obj.type.qualifiedName}||${obj.obj.uid}||${obj.version}"
    }

    override fun createTabData(obj: ServerUiObjectVersionsViewerHandlerData, callback: ServerUiMainFrameTabCallback): ServerUiMainFrameTabData {
        val editor = ServerUiObjectVersionViewer(obj.obj, obj.version) { callback.close() }
        return ServerUiMainFrameTabData("Версия ${obj.version+1} ${obj.obj.caption}", editor)
    }

}