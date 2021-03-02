/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.mainframe.tools

import com.gridnine.jasmine.server.core.model.ui.BaseVM
import com.gridnine.jasmine.server.standard.helpers.UiVersionMetaData
import com.gridnine.jasmine.server.standard.helpers.UiVersionsHelper
import com.gridnine.jasmine.web.server.common.ServerUiCommonUtils
import com.gridnine.jasmine.web.server.components.*
import com.gridnine.jasmine.web.server.mainframe.ServerUiObjectEditor
import com.gridnine.jasmine.web.server.mainframe.ServerUiObjectEditorMenuItem
import java.time.LocalDateTime

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
            ServerUiLibraryAdapter.get().showNotification("Выбрана версия ${it}", ServerUiNotificationType.INFO, 2000)
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
            openCallback.invoke(it.version)
        }
        _node.setFormatter{ item,fieldId ->
            ServerUiCommonUtils.toString(item.getValue(fieldId))
        }
    }
}