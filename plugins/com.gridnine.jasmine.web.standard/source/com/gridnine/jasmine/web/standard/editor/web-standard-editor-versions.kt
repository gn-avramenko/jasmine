/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("UNCHECKED_CAST")

package com.gridnine.jasmine.web.standard.editor

import com.gridnine.jasmine.common.core.model.BaseVMJS
import com.gridnine.jasmine.common.core.model.BaseVSJS
import com.gridnine.jasmine.common.core.model.BaseVVJS
import com.gridnine.jasmine.common.standard.model.rest.GetVersionEditorDataRequestJS
import com.gridnine.jasmine.common.standard.model.rest.GetVersionEditorDataResponseJS
import com.gridnine.jasmine.common.standard.model.rest.GetVersionsMetadataRequestJS
import com.gridnine.jasmine.common.standard.model.rest.RestoreVersionRequestJS
import com.gridnine.jasmine.common.standard.rest.ObjectVersionMetaDataJS
import com.gridnine.jasmine.web.core.common.RegistryJS
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.components.*
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import com.gridnine.jasmine.web.standard.StandardRestClient
import com.gridnine.jasmine.web.standard.WebMessages
import com.gridnine.jasmine.web.standard.mainframe.*
import com.gridnine.jasmine.web.standard.utils.StandardUiUtils
import com.gridnine.jasmine.web.standard.widgets.WebGridLayoutWidget
import com.gridnine.jasmine.web.standard.widgets.WebGridLayoutWidgetCell
import kotlin.js.Date


class ShowVersionsEditorObjectButtonHandler: ObjectEditorTool<WebEditor<*,*,*>> {

    override suspend fun invoke(editor: ObjectEditor<WebEditor<*, *, *>>) {
        val response = StandardRestClient.standard_standard_getVersionsMetadata(GetVersionsMetadataRequestJS().apply {
            objectId = editor.objectType
            objectUid = editor.objectUid
        })
        val dialog = WebUiLibraryAdapter.get().showDialog(VersionsSelectDialogPanel(editor.objectType, editor.objectUid, editor.getTitle(), response.versions)){
            title = WebMessages.showVersions
            cancelButton()
        }
        dialog.getContent().closeCallbalck = dialog::close
    }
}

class VersionsSelectDialogPanel(objectType:String, objectUid:String,  aTitle:String, versions:List<ObjectVersionMetaDataJS>):BaseWebNodeWrapper<WebGridLayoutWidget>() {
    private val dataGrid: WebDataGrid<ObjectVersionMetaDataJS>
    lateinit var closeCallbalck:()->Unit
    init {

        dataGrid = WebUiLibraryAdapter.get().createDataGrid {
            fit = true
            fitColumns = true
            dataType = DataGridDataType.LOCAL
            column {
                fieldId = "version"
                title = WebMessages.version
                width = 50
                sortable = false
            }
            column {
                fieldId = "modifiedBy"
                title = WebMessages.modifiedBy
                width = 50
                sortable = false
            }
            column {
                fieldId = "modified"
                title = WebMessages.modified
                formatter = { value, _, _ ->
                    MiscUtilsJS.formatDateTime(value as Date?)
                }
                width = 50
                sortable = false
            }
            column {
                fieldId = "comment"
                title = WebMessages.comment
                width = 50
                sortable = false
            }
        }
        dataGrid.setLocalData(versions.sortedBy { -it.version })
        dataGrid.setRowDblClickListener {
            closeCallbalck.invoke()
            MainFrame.get().openTab(OpenObjectVersionData(objectType, objectUid, aTitle, it.version))
        }
        _node = WebGridLayoutWidget {
            height = "500px"
            width = "500px"
        }.also {
            it.setColumnsWidths("100%")
            it.addRow("100%", arrayListOf(WebGridLayoutWidgetCell(dataGrid)))
        }
    }

}

data class OpenObjectVersionData(val type: String, var uid: String, val title:String, val version:Int)

class ObjectVersionViewerMainFrameTabHandler: MainFrameTabHandler<OpenObjectVersionData> {
    override fun getTabId(obj: OpenObjectVersionData): String {
        return "${obj.type}||${obj.uid}||${obj.version}"
    }

    override suspend fun createTabData(obj: OpenObjectVersionData, callback: MainFrameTabCallback): MainFrameTabData {
        val request = GetVersionEditorDataRequestJS().apply {
            objectId = obj.type
            objectUid = obj.uid
            version = obj.version
        }
        val response = StandardRestClient.standard_standard_getVersionEditorData(request)
        return MainFrameTabData("${obj.title} ${WebMessages.version} ${obj.version}", ObjectVersionViewer(obj, response, callback))
    }


    override fun getId(): String {
        return OpenObjectVersionData::class.simpleName!!
    }

}

class ObjectVersionViewer(obj: OpenObjectVersionData, data: GetVersionEditorDataResponseJS, callback: MainFrameTabCallback) : BaseWebNodeWrapper<WebBorderContainer>() {

    init {
        _node = WebUiLibraryAdapter.get().createBorderContainer {
            fit = true
        }
        val handler = RegistryJS.get().get(ObjectEditorHandler.TYPE, obj.type)!!
        val rootWebEditor = handler.createEditor()

        _node.setCenterRegion {
            content = rootWebEditor
        }
        val restoreButton = WebUiLibraryAdapter.get().createLinkButton {
            title = WebMessages.restoreVersion
        }
        restoreButton.setHandler {
            callback.close()
            val request = RestoreVersionRequestJS().apply {
                objectId = obj.type.substringBeforeLast("JS")
                objectUid = obj.uid
                version = obj.version
            }
            StandardRestClient.standard_standard_restoreVersion(request)
            callback.close()
            MainFrame.get().publishEvent(ObjectModificationEvent(obj.type, obj.uid))
            StandardUiUtils.showMessage(WebMessages.versionRestored.replace("{0}", "${request.version+1}"))
        }
        val toolBar = WebGridLayoutWidget {
            width = "100%"
        }.also {
            it.setColumnsWidths("auto", "100%")
            it.addRow(restoreButton, null)
        }
        _node.setNorthRegion {
            content = toolBar
        }
        (rootWebEditor as WebEditor<BaseVMJS, BaseVSJS, BaseVVJS>).readData(data.viewModel, data.viewSettings)
        rootWebEditor.setReadonly(true)
    }
}