/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("UNCHECKED_CAST")

package com.gridnine.jasmine.server.standard.ui.mainframe.tools

import com.gridnine.jasmine.common.core.app.Registry
import com.gridnine.jasmine.common.core.model.BaseVM
import com.gridnine.jasmine.common.core.model.BaseVS
import com.gridnine.jasmine.common.core.model.BaseVV
import com.gridnine.jasmine.common.core.model.ObjectReference
import com.gridnine.jasmine.common.standard.model.l10n.StandardL10nMessagesFactory
import com.gridnine.jasmine.server.core.ui.common.*
import com.gridnine.jasmine.server.core.ui.components.*
import com.gridnine.jasmine.server.core.ui.utils.UiUtils
import com.gridnine.jasmine.server.standard.ui.mainframe.*

class ShowVersionsMenuItem :ObjectEditorMenuItem<BaseVM, ViewEditor<BaseVM, *,*>>{
    override fun isApplicable(vm: BaseVM, editor: ObjectEditor<ViewEditor<BaseVM, *, *>>): Boolean {
        return true
    }

    override fun onClick(value: ObjectEditor<ViewEditor<BaseVM, *, *>>) {
        val data = UiVersionsHelper.getVersionsMetadata(value.reference.type.java.name, value.reference.uid)
        val dialogContent = VersionsListDialogContent(data)
        val dialog = UiLibraryAdapter.get().showDialog(dialogContent){
            title = StandardL10nMessagesFactory.Versions()
            cancelButton()
        }
        dialogContent.closeCallback = dialog::close
        dialogContent.openCallback = {
            MainFrame.get().openTab(VersionsViewerHandler() as MainFrameTabHandler<Any>, ObjectVersionsViewerHandlerData(value.reference, it))
        }

    }

    override fun getDisplayName(): String {
        return "Показать версии"
    }

    override fun getMenuButtonId(): String {
        return AdditionalMenuButton.ID
    }

    override fun getId(): String {
        return this::javaClass.name
    }

    override fun getWeight(): Double {
        return 10.0
    }

}

class VersionsListDialogContent(val versions:List<UiVersionMetaData>): BaseNodeWrapper<DataGrid<UiVersionMetaData>>(){
    lateinit var openCallback:(Int) -> Unit

    lateinit var closeCallback:()->Unit

    init {
        _node = UiLibraryAdapter.get().createDataGrid{
            width ="600px"
            height = "500px"
            span = true
            column{
                fieldId = UiVersionMetaData.versionField
                title = StandardL10nMessagesFactory.version()
                width = "100px"
                sortable = false
                horizontalAlignment = ComponentHorizontalAlignment.RIGHT
            }
            column{
                fieldId = UiVersionMetaData.modifiedField
                title = StandardL10nMessagesFactory.modified()
                width = "100px"
                sortable = false
                horizontalAlignment = ComponentHorizontalAlignment.LEFT
            }
            column{
                fieldId = UiVersionMetaData.modifiedByField
                title = StandardL10nMessagesFactory.modifiedBy()
                width = "100px"
                sortable = false
                horizontalAlignment = ComponentHorizontalAlignment.LEFT
            }
            column{
                fieldId = UiVersionMetaData.commentField
                title = StandardL10nMessagesFactory.comment()
                width = "200px"
                sortable = false
                horizontalAlignment = ComponentHorizontalAlignment.LEFT
            }
        }
        _node.setLoader {
            var lastIndex = it.offSet+it.limit
            if(lastIndex > versions.size){
                lastIndex = versions.size
            }
            DataGridResponse(versions.size, versions.subList(it.offSet, lastIndex))
        }
        _node.setDoubleClickListener {
            closeCallback.invoke()
            openCallback.invoke(it.version-1)
        }
        _node.setFormatter{ item,fieldId ->
            UiUtils.toString(item.getValue(fieldId))
        }
    }
}

class ObjectVersionViewer(val reference: ObjectReference<*>, versionNumber:Int, closeCallback:()->Unit):BaseNodeWrapper<BorderContainer>() {

    init {
        _node = UiLibraryAdapter.get().createBorderLayout{
            width = "100%"
            height = "100%"
        }
        val buttonsGrid = UiLibraryAdapter.get().createGridLayoutContainer{
            columns.add(GridLayoutColumnConfiguration("auto"))
        }
        buttonsGrid.addRow()
        val restoreButton = UiLibraryAdapter.get().createLinkButton{
            title = StandardL10nMessagesFactory.restoreVersion()
        }
        restoreButton.setHandler {
            closeCallback.invoke()
            UiVersionsHelper.restoreVersion(reference.type.java.name, reference.uid, versionNumber)
            MainFrame.get().publishEvent(ObjectModificationEvent(reference.type.java.name, reference.uid ))
            UiUtils.showInfo(StandardL10nMessagesFactory.versionRestored())
        }
        buttonsGrid.addCell(GridLayoutCell(restoreButton))
        _node.setNorthRegion{
            content = buttonsGrid
        }
        val bundle = UiVersionsHelper.getVersionReadBundle(reference.type.java.name, reference.uid, versionNumber)
        val handler: UiObjectHandler = Registry.get().get(UiObjectHandler.TYPE, reference.type)!!
        val rootEditor = handler.createEditor()
        _node.setCenterRegion{
            content = rootEditor
        }
        (rootEditor as ViewEditor<BaseVM, BaseVS, BaseVV>).setData(bundle.vm, bundle.vs)
        rootEditor.setReadonly(true)
    }
}

class ObjectVersionsViewerHandlerData(val obj: ObjectReference<*>, val version:Int)

class VersionsViewerHandler : MainFrameTabHandler<ObjectVersionsViewerHandlerData> {
    override fun getTabId(obj: ObjectVersionsViewerHandlerData): String {
        return "${obj.obj.type.qualifiedName}||${obj.obj.uid}||${obj.version}"
    }

    override fun createTabData(obj: ObjectVersionsViewerHandlerData, callback: MainFrameTabCallback): MainFrameTabData {
        val editor = ObjectVersionViewer(obj.obj, obj.version) { callback.close() }
        return MainFrameTabData("Версия ${obj.version+1} ${obj.obj.caption}", editor)
    }

}