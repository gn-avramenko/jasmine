/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.standard.ui.mainframe.workspace

import com.gridnine.jasmine.common.core.model.BaseDocument
import com.gridnine.jasmine.common.core.serialization.SerializationProvider
import com.gridnine.jasmine.common.standard.model.domain.BaseWorkspaceItem
import com.gridnine.jasmine.common.standard.model.domain.ListWorkspaceItem
import com.gridnine.jasmine.common.standard.model.domain.Workspace
import com.gridnine.jasmine.common.standard.model.domain.WorkspaceGroup
import com.gridnine.jasmine.common.standard.model.l10n.StandardL10nMessagesFactory
import com.gridnine.jasmine.server.core.ui.common.BaseNodeWrapper
import com.gridnine.jasmine.server.core.ui.common.UiNode
import com.gridnine.jasmine.server.core.ui.components.*
import com.gridnine.jasmine.server.core.ui.utils.UiUtils
import com.gridnine.jasmine.server.standard.model.WorkspaceProvider
import com.gridnine.jasmine.server.standard.ui.mainframe.MainFrame
import com.gridnine.jasmine.server.standard.ui.mainframe.MainFrameTabCallback
import com.gridnine.jasmine.server.standard.ui.mainframe.MainFrameTabData
import com.gridnine.jasmine.server.standard.ui.mainframe.MainFrameTabHandler
import java.util.*


class WorkspaceEditorTabHandler: MainFrameTabHandler<Any> {
    override fun getTabId(obj: Any): String {
        return "workspaceEditor"
    }

    override fun createTabData(obj: Any, callback: MainFrameTabCallback): MainFrameTabData {
        return MainFrameTabData(StandardL10nMessagesFactory.Workspace(), WorkspaceEditor(WorkspaceProvider.get().getWorkspace()))
    }
}

@Suppress("ImplicitThis", "UNCHECKED_CAST")
class WorkspaceEditor(private val workspace:Workspace): BaseNodeWrapper<BorderContainer>() {
    private val saveButton: LinkButton
    private val centerContent:DivsContainer
    private var lastNodeId:String? = null
    private val tree:Tree
    private var lastEditor: WorkspaceElementEditorHandler<*,*>? = null
    private var revision:Int
    private var dontSave = false
    init {
        revision = workspace.getValue(BaseDocument.revision) as Int
        _node = UiLibraryAdapter.get().createBorderLayout{
            height = "100%"
            width = "100%"
        }
        centerContent = UiLibraryAdapter.get().createDivsContainer{
            width = "100%"
            height = "100%"
        }
        _node.setCenterRegion{
            content = centerContent
            showBorder = true
        }
        val toolBar = UiLibraryAdapter.get().createGridLayoutContainer{
            width = "100%"
            columns.add(GridLayoutColumnConfiguration("auto"))
            columns.add(GridLayoutColumnConfiguration("100%"))
        }
        saveButton = UiLibraryAdapter.get().createLinkButton{
            title = "Сохранить"
        }
        toolBar.addRow()
        toolBar.addCell(GridLayoutCell(saveButton))
        toolBar.addCell(GridLayoutCell(null))
        _node.setNorthRegion{
            content = toolBar
        }
        tree = UiLibraryAdapter.get().createTree{
            enableDnd = true
            width = "100%"
            height = "100%"
        }
        _node.setWestRegion{
            content =tree
            width = "200px"
            showBorder = true
            collapsible = false
        }
        tree.setSelectListener { showEditor(it)}

        tree.setOnDropListener { target, source ->
            tree.remove(source.id)
            if(source.userData is ListWorkspaceItem && target.userData is WorkspaceGroup ){
                val node = TreeItem(source.id, source.text, source.userData)
                node.children.addAll(source.children)
                if(target.children.isEmpty()){
                    tree.append(node, target.id)
                } else {
                    tree.insertBefore(node, target.children[0].id)
                }
            }  else {
                val node = TreeItem(source.id, source.text, source.userData)
                node.children.addAll(source.children)
                tree.insertBefore(node, target.id)
            }
        }
        tree.setOnContextMenuListener { node, event ->
            node.userData.let { userData ->
                if(userData is WorkspaceGroup){
                    val items = arrayListOf<ContextMenuItem>()
                    items.add(ContextMenuStandardItem(StandardL10nMessagesFactory.Add_group_above(), null, false ){
                        val uuid = UUID.randomUUID().toString()
                        val wsGroup = WorkspaceGroup()
                        wsGroup.uid = uuid
                        wsGroup.displayName = StandardL10nMessagesFactory.New_group()
                        tree.insertBefore(TreeItem(uuid, wsGroup.displayName!!, wsGroup), node.id)
                        tree.select(uuid)
                    })
                    items.add(ContextMenuStandardItem(StandardL10nMessagesFactory.Add_group_below(), null, false ){
                        val uuid = UUID.randomUUID().toString()
                        val wsGroup = WorkspaceGroup()
                        wsGroup.uid = uuid
                        wsGroup.displayName = StandardL10nMessagesFactory.New_group()
                        tree.insertAfter(TreeItem(uuid, wsGroup.displayName!!, wsGroup), node.id)
                        tree.select(uuid)
                    })
                    items.add(ContextMenuStandardItem(StandardL10nMessagesFactory.Add_list(), null, false ){
                        val uuid = UUID.randomUUID().toString()
                        val wsGroup = ListWorkspaceItem()
                        wsGroup.uid = uuid
                        wsGroup.displayName = StandardL10nMessagesFactory.New_list()
                        val item = TreeItem(uuid, wsGroup.displayName!!, wsGroup)
                        tree.append(item, node.id)
                        tree.select(uuid)
                    })
                    items.add(ContextMenuStandardItem(StandardL10nMessagesFactory.Delete_group(), null, false ){
                        if(tree.getData().size == 1){
                            UiUtils.showError(StandardL10nMessagesFactory.Unable_to_delete_all_groups())
                        } else {
                            tree.remove(node.id)
                            dontSave = true
                            tree.select(tree.getData()[0].id)
                            dontSave = false
                        }
                    })
                    UiLibraryAdapter.get().showContextMenu(items, event.pageX, event.pageY)
                }
                if(userData is ListWorkspaceItem){
                    val items = arrayListOf<ContextMenuItem>()
                    items.add(ContextMenuStandardItem(StandardL10nMessagesFactory.Add_list(), null, false ){
                        val uuid = UUID.randomUUID().toString()
                        val wsList = ListWorkspaceItem()
                        wsList.uid = uuid
                        wsList.displayName = StandardL10nMessagesFactory.New_group()
                        tree.insertAfter(TreeItem(uuid, wsList.displayName!!, wsList), node.id)
                        tree.select(uuid)
                    })
                    items.add(ContextMenuStandardItem(StandardL10nMessagesFactory.Copy_list(), null, false ){
                        val uuid = UUID.randomUUID().toString()
                        val wsList = SerializationProvider.get().clone(node.userData as ListWorkspaceItem, true)
                        wsList.uid = uuid
                        wsList.displayName = "${node.text}-${StandardL10nMessagesFactory.Copy_suffix()}"
                        tree.insertAfter(TreeItem(uuid, wsList.displayName!!, wsList), node.id)
                        tree.select(uuid)
                    })
                    items.add(ContextMenuStandardItem(StandardL10nMessagesFactory.Delete_list(), null, false ){
                        tree.remove(node.id)
                        dontSave = true
                        tree.select(tree.getData()[0].id)
                        dontSave = false
                    })
                    UiLibraryAdapter.get().showContextMenu(items, event.pageX, event.pageY)
                }
            }
        }
        tree.setData(workspace.groups.map {
            val node = TreeItem(it.uid, it.displayName?:"???",it)
            node.children.addAll(it.items.map { wsItem ->
                val itemNode = TreeItem(wsItem.uid, wsItem.displayName?:"???",wsItem)
                itemNode
            })
            node
        })

        saveButton.setHandler {
            saveData()
            val result = Workspace()
            result.uid = workspace.uid
            result.setValue("revision", revision)
            tree.getData().forEach {
                val group = it.userData as WorkspaceGroup
                group.displayName = it.text
                result.groups.add(group)
                group.items.clear()
                it.children.forEach { child ->
                    val item = child.userData as BaseWorkspaceItem
                    item.displayName = child.text
                    group.items.add(item)
                }
            }
            WorkspaceProvider.get().saveWorkspace(workspace)
            revision = workspace.getValue(BaseDocument.revision) as Int
            MainFrame.get().setWorkspace(workspace)
        }
    }

    private fun showEditor(node: TreeItem) {
        if(lastNodeId == node.id){
            return
        }
        if(lastEditor != null && !saveData()){
             return
        }
        lastNodeId = node.id
        val handler = when(node.userData){
            is WorkspaceGroup -> WorkspaceGroupEditorHandler()
            is ListWorkspaceItem -> WorkspaceListEditorHandler()
            else -> null
        } as WorkspaceElementEditorHandler<UiNode,Any>
        centerContent.clear()
        val editor = handler.createEditor()
        centerContent.addDiv(handler.getId(), editor)
        centerContent.show(handler.getId())
        handler.setData(editor, node.userData!!)
        lastEditor = handler
    }

    private fun saveData(): Boolean {
        if(dontSave){
            return true
        }
        val ed = lastEditor as  WorkspaceElementEditorHandler<UiNode,Any>
        val comp = centerContent.getDiv(ed.getId())!!
        if(!ed.validate(comp)){
            tree.select(lastNodeId!!)
            UiUtils.showError(StandardL10nMessagesFactory.Validation_errors_exist())
            return false
        }
        val data = ed.getData(comp)
        tree.updateUserData(lastNodeId!!, data)
        tree.updateText(lastNodeId!!, ed.getName(data))
        return true
    }

//    companion object{
//        val groupEditorHandler = WorkspaceGroupEditorHandler()
//        val listEditorHandler = WorkspaceListEditorHandler()
//    }
}


