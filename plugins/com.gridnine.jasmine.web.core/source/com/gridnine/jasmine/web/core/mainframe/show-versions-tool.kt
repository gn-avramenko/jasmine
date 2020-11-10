/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.mainframe

import com.gridnine.jasmine.server.core.model.ui.BaseVMJS
import com.gridnine.jasmine.server.core.model.ui.BaseVSJS
import com.gridnine.jasmine.server.core.model.ui.BaseVVJS
import com.gridnine.jasmine.server.standard.model.rest.*
import com.gridnine.jasmine.server.standard.rest.ObjectVersionMetaDataJS
import com.gridnine.jasmine.web.core.CoreWebMessagesJS
import com.gridnine.jasmine.web.core.StandardRestClient
import com.gridnine.jasmine.web.core.remote.RpcManager
import com.gridnine.jasmine.web.core.ui.*
import com.gridnine.jasmine.web.core.ui.components.*
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import com.gridnine.jasmine.web.core.utils.UiUtils
import kotlin.browser.window
import kotlin.js.Date
import kotlin.js.Promise

class ShowVersionsMenuItem :ObjectEditorMenuItem<WebEditor<BaseVMJS,BaseVSJS,BaseVVJS>>{
    override fun getId(): String {
        return "ShowVersionsMenuItem"
    }

    override fun isApplicable(objectId: String): Boolean {
        return  true
    }

    override fun isEnabled(value: ObjectEditor<WebEditor<BaseVMJS, BaseVSJS, BaseVVJS>>): Boolean {
        return true
    }

    override fun onClick(value: ObjectEditor<WebEditor<BaseVMJS, BaseVSJS, BaseVVJS>>) {
        val request = GetVersionsMetadataRequestJS()
        request.objectId = value.obj.objectType.substringBeforeLast("JS")
        request.objectUid = value.obj.objectUid!!
        StandardRestClient.standard_standard_getVersionsMetadata(request).then {
            val ed = ShowVersionsDialogContent(value.obj.objectType, value.obj.objectUid!!, value.title, it.versions)
            val dialog = UiLibraryAdapter.get().showDialog<ShowVersionsDialogContent>(value.rootWebEditor){
                title = CoreWebMessagesJS.showVersions
                editor =ed
                button {
                    displayName =CoreWebMessagesJS.openVersion
                    handler = {
                        val version = it.getContent().getSelectedVersion()
                        if(version != null) {
                            openVersion(value.obj.objectType, value.obj.objectUid!!, value.title, version)
                            it.close()
                        }
                    }
                }
                cancelButton()
            }
            ed.closeCallbalck = dialog::close
        }
    }

    override fun getIcon(): String? {
        return null
    }

    override fun getDisplayName(): String {
        return CoreWebMessagesJS.showVersions
    }

    override fun getWeight(): Double {
        return 100.0
    }

    override fun getMenuButtonId(): String {
        return AdditionalMenuButton.id
    }

}

private fun openVersion(objectId: String, objectUid:String, title:String, version:Int){
    MainFrame.get().openTab(ObjectVersionEditorTabHandler(), ObjectVersionEditorTabData(objectId, objectUid, title, version))
}
class ShowVersionsDialogContent(objectId: String, objectUid: String, aTitle:String, versions:List<ObjectVersionMetaDataJS>):WebComponent,HasDivId{

    private val delegate:WebGridLayoutContainer
    private val dataGrid:WebDataGrid<ObjectVersionMetaDataJS>
    lateinit var closeCallbalck:()->Unit
    init {
        delegate = UiLibraryAdapter.get().createGridLayoutContainer(this){
            height = "500px"
            width = "500px"
        }
        dataGrid= UiLibraryAdapter.get().createDataGrid(delegate){
            fit = true
            fitColumns = true
            dataType = DataGridDataType.LOCAL
            column {
                fieldId = "version"
                title = CoreWebMessagesJS.version
                width = 50
                sortable = false
            }
            column {
                fieldId = "modifiedBy"
                title = CoreWebMessagesJS.modifiedBy
                width = 50
                sortable = false
            }
            column {
                fieldId = "modified"
                title = CoreWebMessagesJS.modified
                formatter = {value, _, _ ->
                    MiscUtilsJS.formatDateTime(value as Date?)
                }
                width = 50
                sortable = false
            }
            column {
                fieldId = "comment"
                title = CoreWebMessagesJS.comment
                width = 50
                sortable = false
            }
        }
        dataGrid.setLocalData(versions.sortedBy { -it.version })
        dataGrid.setRowDblClickListener {
            openVersion(objectId, objectUid, aTitle, it.version)
            closeCallbalck.invoke()
        }
        delegate.addRow("100%")
        delegate.addCell(WebGridLayoutCell(dataGrid))
    }

    override fun getParent(): WebComponent? {
        return null
    }

    override fun getChildren(): List<WebComponent> {
        return arrayListOf()
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

    override fun getId(): String {
        return delegate.getId()
    }

    fun getSelectedVersion():Int?{
        val selected = dataGrid.getSelected()
        return if(selected.isNotEmpty()) selected[0].version else null
    }

}

class ObjectVersionEditorTabHandler:MainFrameTabHandler<ObjectVersionEditorTabData, GetVersionEditorDataResponseJS>{
    override fun getTabId(obj: ObjectVersionEditorTabData): String {
        return "${obj.objectType}||${obj.objectUid}||${obj.version}"
    }

    override fun loadData(obj: ObjectVersionEditorTabData): Promise<GetVersionEditorDataResponseJS> {
        val request = GetVersionEditorDataRequestJS()
        request.objectId = obj.objectType
        request.objectUid = obj.objectUid
        request.version = obj.version
        return StandardRestClient.standard_standard_getVersionEditorData(request)
    }

    override fun createTabData(obj: ObjectVersionEditorTabData, data: GetVersionEditorDataResponseJS, parent: WebComponent, callback: MainFrameTabCallback): MainFrameTabData {
        return MainFrameTabData("${obj.title} (${CoreWebMessagesJS.version} ${obj.version})", ObjectVersionEditor<WebEditor<BaseVMJS,BaseVSJS,BaseVVJS>>(parent, obj, data,callback))
    }
}

class ObjectVersionEditorTabData(val objectType:String, var objectUid:String, val title:String, val version:Int)

class ObjectVersionEditor<W:WebEditor<*,*,*>>(aParent: WebComponent, val obj: ObjectVersionEditorTabData, data: GetVersionEditorDataResponseJS, private val callback: MainFrameTabCallback):WebComponent,WebPopupContainer{
    private val delegate:WebBorderContainer
    private val restoreButton:WebLinkButton
    private val rootWebEditor:W
    private val parent = aParent

    init {
        delegate = UiLibraryAdapter.get().createBorderLayout(this){
            fit=true
        }
        val handler = ClientRegistry.get().get(ObjectHandler.TYPE, obj.objectType)!!
        rootWebEditor = handler.createWebEditor(delegate) as W
        delegate.setCenterRegion(WebBorderContainer.region {
            content = rootWebEditor
        })
        val toolBar = UiLibraryAdapter.get().createGridLayoutContainer(delegate){
            width = "100%"
        }
        toolBar.defineColumn("100%")
        toolBar.defineColumn("auto")
        toolBar.addRow()
        restoreButton = UiLibraryAdapter.get().createLinkButton(toolBar){
            title = CoreWebMessagesJS.restoreVersion
        }
        restoreButton.setHandler {
            callback.close()
            val request = RestoreVersionRequestJS()
            request.objectId = obj.objectType.substringBeforeLast("JS")
            request.objectUid = obj.objectUid
            request.version = obj.version-1
            StandardRestClient.standard_standard_restoreVersion(request).then {
                callback.close()
                MainFrame.get().publishEvent(ObjectModificationEvent(obj.objectType, obj.objectUid))
                UiUtils.showMessage(CoreWebMessagesJS.versionRestored.replace("{0}", "${request.version+1}"))
            }
        }
        toolBar.addCell(WebGridLayoutCell(restoreButton))
        delegate.setNorthRegion(WebBorderContainer.region {
            content = toolBar
        })
        (rootWebEditor as WebEditor<BaseVMJS, BaseVSJS,BaseVVJS>).readData(data.viewModel, data.viewSettings)
        rootWebEditor.setReadonly(true)
    }


    override fun getParent(): WebComponent? {
        return parent
    }

    override fun getChildren(): List<WebComponent> {
        return arrayListOf(delegate)
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

    override fun getId(): String {
        return delegate.getId()
    }
}