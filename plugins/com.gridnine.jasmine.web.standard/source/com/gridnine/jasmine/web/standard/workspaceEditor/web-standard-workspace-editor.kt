/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("unused")

package com.gridnine.jasmine.web.standard.workspaceEditor

import com.gridnine.jasmine.common.core.model.BaseIntrospectableObjectJS
import com.gridnine.jasmine.common.core.model.SelectItemJS
import com.gridnine.jasmine.common.standard.model.rest.*
import com.gridnine.jasmine.web.core.serialization.CloneHelperJS
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.components.*
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import com.gridnine.jasmine.web.standard.OptionsIds
import com.gridnine.jasmine.web.standard.StandardRestClient
import com.gridnine.jasmine.web.standard.WebMessages
import com.gridnine.jasmine.web.standard.mainframe.*
import com.gridnine.jasmine.web.standard.utils.StandardUiUtils
import com.gridnine.jasmine.web.standard.widgets.WebGridLayoutWidget
import com.gridnine.jasmine.web.standard.widgets.WebNodeProjectorWidget


class WorkspaceEditorActionHandler : SimpleActionHandler{
    override suspend fun invoke() {
        MainFrame.get().openTab(StandardRestClient.standard_standard_getWorkspace(GetWorkspaceRequestJS()).workspace)
    }
}

class WorkspaceEditorTabHandler: MainFrameTabHandler<WorkspaceDTJS> {
    override fun getTabId(obj: WorkspaceDTJS): String {
        return "workspaceEditor"
    }

    override suspend fun createTabData(obj: WorkspaceDTJS, callback: MainFrameTabCallback): MainFrameTabData {
        return MainFrameTabData(WebMessages.Workspace, WorkspaceEditor(obj))
    }

    override fun getId(): String {
        return WorkspaceDTJS::class.simpleName!!
    }

}

class WorkspaceEditor(workspace:WorkspaceDTJS): BaseWebNodeWrapper<WebBorderContainer>() {

    private val centerContent:WebNodeProjectorWidget
    private var lastNodeId:String? = null
    private val tree:WebTree
    private var lastEditor: WorkspaceElementEditorHandler<*,*>? = null
    private val groupHandler = WorkspaceGroupEditorHandler()
    private var itemHandler:WorkspaceItemEditorHandler? = null
    init {
        _node = WebUiLibraryAdapter.get().createBorderContainer {
            fit=true
        }
        centerContent = WebNodeProjectorWidget{
            width = "100%"
            height = "100%"
        }
        _node.setCenterRegion {
            content = centerContent
            showBorder = true
        }
        val saveButton = WebUiLibraryAdapter.get().createLinkButton{
            title = WebMessages.save
        }
        val toolBar = WebGridLayoutWidget{
            width = "100%"
        }.also {
            it.setColumnsWidths("auto", "100%")
            it.addRow(saveButton, null)
        }
        _node.setNorthRegion{
            content = toolBar
        }
        tree = WebUiLibraryAdapter.get().createTree{
            fit = true
            enableDnd = true
            width = "100%"
            height = "100%"
        }
        _node.setWestRegion{
            content =tree
            width = 200
            showBorder = true
            collapsible = false
        }
        tree.setSelectListener { showEditor(it)}
        val onBeforeDropListener = lst@{target:WebTreeNode, source:WebTreeNode, point:WebTreeInsertNodePoint ->
            if(source.userData is WorkspaceGroupDTJS){
                if(target.userData !is WorkspaceGroupDTJS){
                    return@lst false
                }
            } else {
                if(target.userData is WorkspaceGroupDTJS && point != WebTreeInsertNodePoint.APPEND){
                    return@lst false
                }
            }
            true
        }
        tree.setOnBeforeDropListener(onBeforeDropListener)
        tree.setOnDragEnterListener{target:WebTreeNode, source:WebTreeNode ->
            onBeforeDropListener.invoke(target,source,WebTreeInsertNodePoint.APPEND)
        }
        tree.setOnDropListener { target, source, point ->
            tree.remove(source.id)
            if(source.userData !is WorkspaceGroupDTJS && target.userData is WorkspaceGroupDTJS ){
                val node = WebTreeNode(MiscUtilsJS.createUUID(), source.text, source.userData)
                node.children.addAll(source.children)
                if(target.children.isEmpty()){
                    tree.append(node, target.id)
                } else {
                    tree.insertBefore(node, target.children[0].id)
                }
            } else if (point == WebTreeInsertNodePoint.APPEND || point == WebTreeInsertNodePoint.BOTTOM) {
                val node = WebTreeNode(MiscUtilsJS.createUUID(), source.text, source.userData)
                node.children.addAll(source.children)
                tree.insertAfter(node, target.id)
            } else {
                val node = WebTreeNode(MiscUtilsJS.createUUID(), source.text, source.userData)
                node.children.addAll(source.children)
                tree.insertBefore(node, target.id)
            }
        }
        tree.setOnContextMenuListener { node, event ->
            node.userData.let { userData ->
                if(userData is WorkspaceGroupDTJS){
                    val items = arrayListOf<WebContextMenuItem>()
                    items.add(WebContextMenuStandardItem("Добавить группу выше", null, false ){
                        val uuid = MiscUtilsJS.createUUID()
                        val ud = WorkspaceGroupDTJS()
                        ud.uid = uuid
                        ud.displayName = "Новая группа"
                        tree.insertBefore(WebTreeNode(uuid, ud.displayName!!, ud), node.id)
                        tree.select(uuid)
                    })
                    items.add(WebContextMenuStandardItem("Добавить группу ниже", null, false ){
                        val uuid = MiscUtilsJS.createUUID()
                        val ud = WorkspaceGroupDTJS()
                        ud.uid = uuid
                        ud.displayName = "Новая группа"
                        tree.insertAfter(WebTreeNode(uuid, ud.displayName!!, ud), node.id)
                        tree.select(uuid)
                    })
                    items.add(WebContextMenuStandardItem("Добавить элемент", null, false ){
                        val uid  = MiscUtilsJS.createUUID()
                        tree.append(WebTreeNode(uid, "Новый элемент", null), node.id)
                        tree.select(uid)
                    })
                    items.add(WebContextMenuStandardItem("Удалить группу", null, false ){
                        tree.remove(node.id)
                        tree.select(tree.getData()[0].id)
                    })
                    WebUiLibraryAdapter.get().showContextMenu(items, event.pageX, event.pageY)
                    return@let
                }
                    val items = arrayListOf<WebContextMenuItem>()
                    items.add(WebContextMenuStandardItem("Добавить элемент", null, false ){
                        val uid  = MiscUtilsJS.createUUID()
                        tree.insertAfter(WebTreeNode(uid, "Новый элемент", null), node.id)
                        tree.select(uid)
                    })
                    items.add(WebContextMenuStandardItem("Копировать список", null, false ){
                        val uid  = MiscUtilsJS.createUUID()
                        tree.insertAfter(WebTreeNode(uid, "Новый элемент", CloneHelperJS.clone((node.userData?:getElementData(node.id)) as BaseIntrospectableObjectJS, true)), node.id)
                        tree.select(uid)
                    })
                    items.add(WebContextMenuStandardItem("Удалить список", null, false ){
                        tree.remove(node.id)
                        tree.select(tree.getData()[0].id)
                    })
                    WebUiLibraryAdapter.get().showContextMenu(items, event.pageX, event.pageY)
            }
        }
        tree.setData(workspace.groups.map {wg ->
            val node = WebTreeNode(wg.uid!!, wg.displayName?:"???",wg)
            node.children.addAll(wg.items.map {
                val itemNode = WebTreeNode(it.id, it.text, null)
                itemNode
            })
            node
        })

        saveButton.setHandler {
            saveData()
            val result = WorkspaceDTJS()
            val request = SaveWorkspaceRequestJS()
            tree.getData().forEach { tn ->
                val group = tn.userData as WorkspaceGroupDTJS
                group.displayName = tn.text
                result.groups.add(group)
                group.items.clear()
                tn.children.forEach { child ->
                    group.items.add(SelectItemJS((child.userData as BaseWorkspaceItemDTJS?)?.uid?:child.id, child.text))
                    child.userData?.let {
                        request.updatedItems.add(it as BaseWorkspaceItemDTJS)
                    }
                }
            }
            request.workspace = result
            StandardRestClient.standard_standard_saveWorkspace(request)
            MainFrame.get().setWorkspace(result)
            StandardUiUtils.showMessage(WebMessages.Object_saved)
        }
    }

    private suspend fun getElementData(uuid:String):BaseWorkspaceItemDTJS{
        return StandardRestClient.standard_standard_getWorkspaceItem(GetWorkspaceItemRequestJS().apply {
                uid =uuid
            }).workspaceItem
    }
    @Suppress("USELESS_CAST")
    private suspend fun showEditor(node: WebTreeNode) {
        if(lastNodeId == node.id){
            return
        }
        if(lastEditor != null && !saveData()){
             return
        }
        lastNodeId = node.id
        val handler = when(node.userData){
            is WorkspaceGroupDTJS -> groupHandler
            else -> {
                if(itemHandler == null){
                    itemHandler = WorkspaceItemEditorHandler(WebOptionsHandler.get().getOptionsFor(OptionsIds.standard_workspace_elements_handlers).sortedBy { it.text })
                }
                itemHandler!!
            }
        }.unsafeCast<WorkspaceElementEditorHandler<WebNode,Any>>()
        val comp = centerContent.getNode(handler.getId())?:run{
            val editor = handler.createEditor()
            centerContent.addNode(handler.getId(), editor)
            editor
        }
        centerContent.showNode(handler.getId())
        val userData =  node.userData?:getElementData(node.id)
        handler.setData(comp,  userData)
        lastEditor = handler
    }

    private fun saveData(): Boolean {
        val ed = lastEditor.unsafeCast<WorkspaceElementEditorHandler<WebNode,Any>>()
        val comp = centerContent.getNode(ed.getId())!!
        if(!ed.validate(comp)){
            tree.select(lastNodeId!!)
            StandardUiUtils.showMessage("Есть ошибки валидации")
            return false
        }
        val data = ed.getData(comp)
        tree.updateUserData(lastNodeId!!, data)
        tree.updateText(lastNodeId!!, ed.getName(data))
        return true
    }

}

interface WorkspaceElementEditorHandler<E:WebNode,M:Any>{
    fun getId():String
    fun createEditor(): E
    fun setData(editor:E, data:M)
    fun getData(editor:E):M
    fun getName(data:M):String
    fun validate(editor:E):Boolean
}
