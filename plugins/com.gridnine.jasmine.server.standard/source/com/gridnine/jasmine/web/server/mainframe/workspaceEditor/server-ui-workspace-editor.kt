/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.mainframe.workspaceEditor

import com.gridnine.jasmine.server.core.model.domain.BaseDocument
import com.gridnine.jasmine.server.core.serialization.JsonSerializer
import com.gridnine.jasmine.server.standard.model.domain.BaseWorkspaceItem
import com.gridnine.jasmine.server.standard.model.domain.ListWorkspaceItem
import com.gridnine.jasmine.server.standard.model.domain.Workspace
import com.gridnine.jasmine.server.standard.model.domain.WorkspaceGroup
import com.gridnine.jasmine.server.standard.rest.WorkspaceProvider
import com.gridnine.jasmine.web.server.components.*
import com.gridnine.jasmine.web.server.mainframe.ServerUiMainFrame
import com.gridnine.jasmine.web.server.mainframe.ServerUiMainFrameTabCallback
import com.gridnine.jasmine.web.server.mainframe.ServerUiMainFrameTabData
import com.gridnine.jasmine.web.server.mainframe.ServerUiMainFrameTabHandler
import java.util.*


class ServerUiWorkspaceEditorTabHandler: ServerUiMainFrameTabHandler<Any> {
    override fun getTabId(obj: Any): String {
        return "workspaceEditor"
    }

    override fun createTabData(obj: Any, callback: ServerUiMainFrameTabCallback): ServerUiMainFrameTabData {
        return ServerUiMainFrameTabData("Рабочая облась", ServerUiWorkspaceEditor(WorkspaceProvider.get().getWorkspace()))
    }
}

class ServerUiWorkspaceEditor(private val workspace:Workspace): BaseServerUiNodeWrapper<ServerUiBorderContainer>() {
    private val saveButton: ServerUiLinkButton
    private val centerContent:ServerUiDivsContainer
    private var lastNodeId:String? = null
    private val tree:ServerUiTree
    private var lastEditor: ServerUiWorkspaceElementEditorHandler<*,*>? = null
    private var revision:Int
    private var dontSave = false
    init {
        revision = workspace.getValue(BaseDocument.revision) as Int
        _node = ServerUiLibraryAdapter.get().createBorderLayout(ServerUiBorderContainerConfiguration{
            height = "100%"
            width = "100%"
        })
        centerContent = ServerUiLibraryAdapter.get().createDivsContainer(ServerUiDivsContainerConfiguration{
            width = "100%"
            height = "100%"
        })
        _node.setCenterRegion(ServerUiBorderContainerRegion {
            content = centerContent
            showBorder = true
        })
        val toolBar = ServerUiLibraryAdapter.get().createGridLayoutContainer(ServerUiGridLayoutContainerConfiguration{
            width = "100%"
            columns.add(ServerUiGridLayoutColumnConfiguration("auto"))
            columns.add(ServerUiGridLayoutColumnConfiguration("100%"))
        })
        saveButton = ServerUiLibraryAdapter.get().createLinkButton(ServerUiLinkButtonConfiguration{
            title = "Сохранить"
        })
        toolBar.addRow()
        toolBar.addCell(ServerUiGridLayoutCell(saveButton))
        toolBar.addCell(ServerUiGridLayoutCell(null))
        _node.setNorthRegion(ServerUiBorderContainerRegion{
            content = toolBar
        })
        tree = ServerUiLibraryAdapter.get().createTree(ServerUiTreeConfiguration{
            enableDnd = true
            width = "100%"
            height = "100%"
        })
        _node.setWestRegion(ServerUiBorderContainerRegion {
            content =tree
            width = "200px"
            showBorder = true
            collapsible = false
        })
        tree.setSelectListener { showEditor(it)}

        tree.setOnDropListener { target, source ->
            tree.remove(source.id)
            if(source.userData is ListWorkspaceItem && target.userData is WorkspaceGroup ){
                val node = ServerUiTreeItem(source.id, source.text, source.userData)
                node.children.addAll(source.children)
                if(target.children.isEmpty()){
                    tree.append(node, target.id)
                } else {
                    tree.insertBefore(node, target.children[0].id)
                }
            }  else {
                val node = ServerUiTreeItem(source.id, source.text, source.userData)
                node.children.addAll(source.children)
                tree.insertBefore(node, target.id)
            }
        }
        tree.setOnContextMenuListener { node, event ->
            node.userData.let { userData ->
                if(userData is WorkspaceGroup){
                    val items = arrayListOf<ServerUiContextMenuItem>()
                    items.add(ServerUiContextMenuStandardItem("Добавить группу выше", null, false ){
                        val uuid = UUID.randomUUID().toString()
                        val userData = WorkspaceGroup()
                        userData.uid = uuid
                        userData.displayName = "Новая группа"
                        tree.insertBefore(ServerUiTreeItem(uuid, userData.displayName!!, userData), node.id)
                        tree.select(uuid)
                    })
                    items.add(ServerUiContextMenuStandardItem("Добавить группу ниже", null, false ){
                        val uuid = UUID.randomUUID().toString()
                        val userData = WorkspaceGroup()
                        userData.uid = uuid
                        userData.displayName = "Новая группа"
                        tree.insertAfter(ServerUiTreeItem(uuid, userData.displayName!!, userData), node.id)
                        tree.select(uuid)
                    })
                    items.add(ServerUiContextMenuStandardItem("Добавить список", null, false ){
                        val uuid = UUID.randomUUID().toString()
                        val userData = ListWorkspaceItem()
                        userData.uid = uuid
                        userData.displayName = "Новый список"
                        val item = ServerUiTreeItem(uuid, userData.displayName!!, userData)
                        tree.append(item, node.id)
                        tree.select(uuid)
                    })
                    items.add(ServerUiContextMenuStandardItem("Удалить группу", null, false ){
                        if(tree.getData().size == 1){
                            ServerUiLibraryAdapter.get().showNotification("Нельзя удалить все группы", ServerUiNotificationType.ERROR, 2000);
                        } else {
                            tree.remove(node.id)
                            dontSave = true
                            tree.select(tree.getData()[0].id)
                            dontSave = false
                        }
                    })
                    ServerUiLibraryAdapter.get().showContextMenu(items, event.pageX, event.pageY)
                }
                if(userData is ListWorkspaceItem){
                    val items = arrayListOf<ServerUiContextMenuItem>()
                    items.add(ServerUiContextMenuStandardItem("Добавить список", null, false ){
                        val uuid = UUID.randomUUID().toString()
                        val userData = ListWorkspaceItem()
                        userData.uid = uuid
                        userData.displayName = "Новый список"
                        tree.insertAfter(ServerUiTreeItem(uuid, userData.displayName!!, userData), node.id)
                        tree.select(uuid)
                    })
                    items.add(ServerUiContextMenuStandardItem("Копировать список", null, false ){
                        val uuid = UUID.randomUUID().toString()
                        val userData = JsonSerializer.get().clone(node.userData as ListWorkspaceItem, true)
                        userData.uid = uuid
                        userData.displayName = "${node.text}-Копия"
                        tree.insertAfter(ServerUiTreeItem(uuid, userData.displayName!!, userData), node.id)
                        tree.select(uuid)
                    })
                    items.add(ServerUiContextMenuStandardItem("Удалить список", null, false ){
                        tree.remove(node.id)
                        dontSave = true
                        tree.select(tree.getData()[0].id)
                        dontSave = false
                    })
                    ServerUiLibraryAdapter.get().showContextMenu(items, event.pageX, event.pageY)
                }
            }
        }
        tree.setData(workspace.groups.map {
            val node = ServerUiTreeItem(it.uid, it.displayName?:"???",it)
            node.children.addAll(it.items.map {
                val itemNode = ServerUiTreeItem(it.uid, it.displayName?:"???",it)
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
            ServerUiMainFrame.get().setWorkspace(workspace)
        }
    }

    private fun showEditor(node: ServerUiTreeItem) {
        if(lastNodeId == node.id){
            return
        }
        if(lastEditor != null && !saveData()){
             return
        }
        lastNodeId = node.id
        val handler = when(node.userData){
            is WorkspaceGroup -> ServerUiWorkspaceGroupEditorHandler()
            is ListWorkspaceItem -> ServerUiWorkspaceListEditorHandler()
            else -> null
        } as ServerUiWorkspaceElementEditorHandler<ServerUiNode,Any>
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
        val ed = lastEditor as  ServerUiWorkspaceElementEditorHandler<ServerUiNode,Any>
        val comp = centerContent.getDiv(ed.getId())!!
        if(!ed.validate(comp)){
            tree.select(lastNodeId!!)
            ServerUiLibraryAdapter.get().showNotification("Есть ошибки валидации",ServerUiNotificationType.INFO, 2000 )
            return false
        }
        val data = ed.getData(comp)
        tree.updateUserData(lastNodeId!!, data)
        tree.updateText(lastNodeId!!, ed.getName(data))
        return true
    }

//    companion object{
//        val groupEditorHandler = ServerUiWorkspaceGroupEditorHandler()
//        val listEditorHandler = ServerUiWorkspaceListEditorHandler()
//    }
}

interface ServerUiWorkspaceElementEditorHandler<E:ServerUiNode,M:Any>{
    fun getId():String
    fun createEditor(): E
    fun setData(editor:E, data:M)
    fun getData(editor:E):M
    fun getName(data:M):String
    fun validate(editor:E):Boolean
}
