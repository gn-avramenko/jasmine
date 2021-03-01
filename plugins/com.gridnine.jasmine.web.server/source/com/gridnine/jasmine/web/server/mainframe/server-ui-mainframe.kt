/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.mainframe

import com.gridnine.jasmine.server.core.model.domain.ObjectReference
import com.gridnine.jasmine.server.standard.model.domain.ListWorkspaceItem
import com.gridnine.jasmine.server.standard.model.domain.Workspace
import com.gridnine.jasmine.server.standard.model.domain.WorkspaceGroup
import com.gridnine.jasmine.server.standard.rest.WorkspaceProvider
import com.gridnine.jasmine.web.server.components.*
import kotlin.reflect.KClass

class ServerUiMainFrame(config:ServerUiMainFrameConfiguration) : BaseServerUiNodeWrapper<ServerUiBorderContainer>(){

    private val navigationItemsHandlers = hashMapOf<KClass<*>, ServerUiMainFrameTabHandler<Any>>()

    private val _navigationTree:ServerUiTree

    private val _tabs:ServerUiTabbox

    private val editorHandler = ServerUiObjectEditorHandler() as ServerUiMainFrameTabHandler<Any>

    init {
        val border = ServerUiLibraryAdapter.get().createBorderLayout(ServerUiBorderContainerConfiguration{
            width = "100%"
            height = "100%"
        })
        _navigationTree = ServerUiLibraryAdapter.get().createTree(ServerUiTreeConfiguration{
            width = "100%"
            height = "100%"
        })
        _navigationTree.setSelectListener {
            val userData = it.userData
            if(userData !is WorkspaceGroup){
                val handler = navigationItemsHandlers[userData!!::class]!!
                openTab(handler, userData)
            }
        }
        border.setWestRegion(ServerUiBorderContainerRegion{
            title = config.title
            width  = "200px"
            showSplitLine = true
            collapsible = false
            content = _navigationTree
        })
        setWorkspace(WorkspaceProvider.get().getWorkspace())
        _tabs = ServerUiLibraryAdapter.get().createTabboxContainer(ServerUiTabboxConfiguration{
            width = "100%"
            height = "100%"
            tools.addAll(config.tools)
        })
        border.setCenterRegion(ServerUiBorderContainerRegion{
            content = _tabs
        })
        navigationItemsHandlers[ListWorkspaceItem::class] = ServerUiListHandler() as ServerUiMainFrameTabHandler<Any>
        _node = border
     }

    private fun openTab(handler: ServerUiMainFrameTabHandler<Any>, userData: Any) {
        val tabId = handler.getTabId(userData)
        val tab = _tabs.getTabs().find { it.id == tabId }
        if(tab != null){
            _tabs.select(tabId)
            return
        }
        val tabData = handler.createTabData(userData, object:ServerUiMainFrameTabCallback{
            override fun setTitle(title: String) {
                _tabs.setTitle(tabId, title)
            }

            override fun close() {
                _tabs.removeTab(tabId)
            }
        })
        _tabs.addTab(ServerUiTabPanel(tabId, tabData.title, tabData.content))
    }

    private fun setWorkspace(workspace: Workspace) {
        val result = arrayListOf<ServerUiTreeItem>()
        workspace.groups.forEach { group ->
            val item = ServerUiTreeItem(group.uid, group.displayName?:"", group)
            result.add(item)
            group.items.forEach { wi ->
                when(wi){
                    is ListWorkspaceItem ->{
                        item.children.add(ServerUiTreeItem(wi.uid, wi.displayName?:"", wi))
                    }
                }
            }
        }
        _navigationTree.setData(result)
    }

    fun openTab(ref:ObjectReference<*>, navigationKey:String?){
        openTab(editorHandler, ServerUiObjectEditorHandlerData(ref, navigationKey))
    }

    companion object{
        fun get():ServerUiMainFrame{
            return ServerUiLibraryAdapter.get().findRootComponent() as ServerUiMainFrame
        }
    }

}

interface ServerUiMainFrameTabCallback{
    fun setTitle(title:String)
    fun close()
}

data class ServerUiMainFrameTabData(var title:String, var content:ServerUiNode)

interface ServerUiMainFrameTabHandler<T:Any>{
    fun getTabId(obj:T):String
    fun createTabData(obj:T, callback: ServerUiMainFrameTabCallback):ServerUiMainFrameTabData
}


class ServerUiMainFrameConfiguration(){

    lateinit var title:String

    val tools = arrayListOf<ServerUiTabTool>()



    constructor(config:ServerUiMainFrameConfiguration.()->Unit):this(){
        config.invoke(this)
    }

}