/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.mainframe.workspaceEditor

import com.gridnine.jasmine.server.standard.model.domain.ListWorkspaceItemJS
import com.gridnine.jasmine.server.standard.model.domain.WorkspaceGroupJS
import com.gridnine.jasmine.server.standard.model.domain.WorkspaceJS
import com.gridnine.jasmine.server.standard.model.rest.GetWorkspaceRequestJS
import com.gridnine.jasmine.server.standard.model.rest.GetWorkspaceResponseJS
import com.gridnine.jasmine.web.core.CoreWebMessagesJS
import com.gridnine.jasmine.web.core.StandardRestClient
import com.gridnine.jasmine.web.core.mainframe.MainFrameTabCallback
import com.gridnine.jasmine.web.core.mainframe.MainFrameTabData
import com.gridnine.jasmine.web.core.mainframe.MainFrameTabHandler
import com.gridnine.jasmine.web.core.serialization.JsonSerializerJS
import com.gridnine.jasmine.web.core.ui.UiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.WebComponent
import com.gridnine.jasmine.web.core.ui.WebPopupContainer
import com.gridnine.jasmine.web.core.ui.components.*
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import com.gridnine.jasmine.web.core.utils.UiUtils
import kotlin.browser.window
import kotlin.js.Promise


class WorkspaceEditorTabHandler: MainFrameTabHandler<Unit, GetWorkspaceResponseJS> {
    override fun getTabId(obj: Unit): String {
        return "workspaceEditor"
    }

    override fun loadData(obj: Unit): Promise<GetWorkspaceResponseJS> {
        val request = GetWorkspaceRequestJS()
        return StandardRestClient.standard_standard_getWorkspace(request)
    }

    override fun createTabData(obj: Unit, data: GetWorkspaceResponseJS, parent: WebComponent, callback: MainFrameTabCallback): MainFrameTabData {
        return MainFrameTabData(CoreWebMessagesJS.Workspace, WorkspaceEditor(parent, data.workspace))
    }
}

class WorkspaceEditor(private val parent: WebComponent, private val workspace:WorkspaceJS): WebComponent, WebPopupContainer {
    private val delegate: WebBorderContainer
    private val saveButton: WebLinkButton
    private val centerContent:WebDivsContainer
    private var lastNodeId:String? = null
    private val tree:WebTree
    private var lastEditor: WorkspaceElementEditorHandler<*,*>? = null
    init {
        delegate = UiLibraryAdapter.get().createBorderLayout(this){
            fit=true
        }
        centerContent = UiLibraryAdapter.get().createDivsContainer(delegate){
            width = "100%"
            height = "100%"
        }

        delegate.setCenterRegion(WebBorderContainer.region {
            content = centerContent
            showBorder = true
        })
        val toolBar = UiLibraryAdapter.get().createGridLayoutContainer(delegate){
            width = "100%"
        }
        saveButton = UiLibraryAdapter.get().createLinkButton(toolBar){
            title = CoreWebMessagesJS.save
        }
        toolBar.defineColumn("auto")
        toolBar.defineColumn("100%")
        toolBar.addRow()
        toolBar.addCell(WebGridLayoutCell(saveButton))
        toolBar.addCell(WebGridLayoutCell(null))
        delegate.setNorthRegion(WebBorderContainer.region {
            content = toolBar
        })
        tree = UiLibraryAdapter.get().createTree(delegate){
            fit = true
            enableDnd = true
            width = "100%"
            height = "100%"
        }
        delegate.setWestRegion(WebBorderContainer.region {
            content =tree
            width = 200
            showBorder = true
            collapsible = false
        })
        tree.setSelectListener { showEditor(it)}
        val onBeforeDropListener = lst@{target:WebTreeNode, source:WebTreeNode, point:WebTreeInsertNodePoint ->
            if(source.userData is WorkspaceGroupJS){
                if(target.userData !is WorkspaceGroupJS){
                    return@lst false
                }
            } else {
                if(target.userData is WorkspaceGroupJS && point != WebTreeInsertNodePoint.APPEND){
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
            if(source.userData is ListWorkspaceItemJS && target.userData is WorkspaceGroupJS ){
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
                if(userData is WorkspaceGroupJS){
                    val items = arrayListOf<WebContextMenuItem>()
                    items.add(WebContextMenuStandardItem("Добавить группу выше", null, false ){
                        val uuid = MiscUtilsJS.createUUID()
                        val userData = WorkspaceGroupJS()
                        userData.uid = uuid
                        userData.displayName = "Новая группа"
                        tree.insertBefore(WebTreeNode(uuid, userData.displayName!!, userData), node.id)
                        tree.select(uuid)
                    })
                    items.add(WebContextMenuStandardItem("Добавить группу ниже", null, false ){
                        val uuid = MiscUtilsJS.createUUID()
                        val userData = WorkspaceGroupJS()
                        userData.uid = uuid
                        userData.displayName = "Новая группа"
                        tree.insertAfter(WebTreeNode(uuid, userData.displayName!!, userData), node.id)
                        tree.select(uuid)
                    })
                    items.add(WebContextMenuStandardItem("Добавить список", null, false ){
                        val uuid = MiscUtilsJS.createUUID()
                        val userData = ListWorkspaceItemJS()
                        userData.uid = uuid
                        userData.displayName = "Новый список"
                        tree.append(WebTreeNode(uuid, userData.displayName!!, userData), node.id)
                        tree.select(uuid)
                    })
                    items.add(WebContextMenuStandardItem("Удалить группу", null, false ){
                        tree.remove(node.id)
                        tree.select(tree.getData()[0].id)
                    })
                    UiLibraryAdapter.get().showContextMenu(WorkspaceEditor@this, items, event.pageX, event.pageY)
                }
                if(userData is ListWorkspaceItemJS){
                    val items = arrayListOf<WebContextMenuItem>()
                    items.add(WebContextMenuStandardItem("Добавить список", null, false ){
                        val uuid = MiscUtilsJS.createUUID()
                        val userData = ListWorkspaceItemJS()
                        userData.uid = uuid
                        userData.displayName = "Новый список"
                        tree.insertAfter(WebTreeNode(uuid, userData.displayName!!, userData), node.id)
                        tree.select(uuid)
                    })
                    items.add(WebContextMenuStandardItem("Копировать список", null, false ){
                        val uuid = MiscUtilsJS.createUUID()
                        val userData = JsonSerializerJS.get().clone(node.userData as ListWorkspaceItemJS, true)
                        userData.uid = uuid
                        userData.displayName = "${node.text}-Копия"
                        tree.insertAfter(WebTreeNode(uuid, userData.displayName!!, userData), node.id)
                        tree.select(uuid)
                    })
                    items.add(WebContextMenuStandardItem("Удалить список", null, false ){
                        tree.remove(node.id)
                        tree.select(tree.getData()[0].id)
                    })
                    UiLibraryAdapter.get().showContextMenu(WorkspaceEditor@this, items, event.pageX, event.pageY)
                }
            }
        }
        tree.setData(workspace.groups.map {
            val node = WebTreeNode(it.uid, it.displayName?:"???",it)
            node.children.addAll(it.items.map {
                val itemNode = WebTreeNode(it.uid, it.displayName?:"???",it)
                itemNode
            })
            node
        })
    }

    private fun showEditor(node: WebTreeNode) {
        if(lastNodeId == node.id){
            return
        }
        if(lastEditor != null && !saveData()){
             return
        }
        lastNodeId = node.id
        val handler = when(node.userData){
            is WorkspaceGroupJS -> WorkspaceGroupEditorHandler()
            else -> WorkspaceListEditorHandler()
        }.unsafeCast<WorkspaceElementEditorHandler<WebComponent,Any>>()
        val comp = centerContent.getDiv(handler.getId())?:run{
            val editor = handler.createEditor(centerContent)
            centerContent.addDiv(handler.getId(), editor)
            editor
        }
        centerContent.show(handler.getId())
        handler.setData(comp, node.userData!!)
        lastEditor = handler
    }

    private fun saveData(): Boolean {
        val ed = lastEditor.unsafeCast<WorkspaceElementEditorHandler<WebComponent,Any>>()
        val comp = centerContent.getDiv(ed.getId())!!
        if(!ed.validate(comp)){
            tree.select(lastNodeId!!)
            UiUtils.showMessage("Есть ошибки валидации")
            return false
        }
        val data = ed.getData(comp)
        tree.updateUserData(lastNodeId!!, data)
        tree.updateText(lastNodeId!!, ed.getName(data))
        return true
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

interface WorkspaceElementEditorHandler<E:WebComponent,M:Any>{
    fun getId():String
    fun createEditor(parent:WebComponent): E
    fun setData(editor:E, data:M)
    fun getData(editor:E):M
    fun getName(data:M):String
    fun validate(editor:E):Boolean
}
