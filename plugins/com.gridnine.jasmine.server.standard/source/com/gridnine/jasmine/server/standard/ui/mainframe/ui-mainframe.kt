/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.standard.ui.mainframe

import com.gridnine.jasmine.common.core.model.ObjectReference
import com.gridnine.jasmine.common.standard.model.domain.ListWorkspaceItem
import com.gridnine.jasmine.common.standard.model.domain.Workspace
import com.gridnine.jasmine.common.standard.model.domain.WorkspaceGroup
import com.gridnine.jasmine.server.core.ui.common.BaseNodeWrapper
import com.gridnine.jasmine.server.core.ui.common.EventsSubscriber
import com.gridnine.jasmine.server.core.ui.components.*
import com.gridnine.jasmine.server.standard.model.WorkspaceProvider
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
class MainFrame(configure:MainFrameConfiguration.()->Unit) : BaseNodeWrapper<BorderContainer>(){

    private val navigationItemsHandlers = hashMapOf<KClass<*>, MainFrameTabHandler<Any>>()

    private val _navigationTree:Tree

    private val _tabs:Tabbox

    private val editorHandler = ObjectEditorTabHandler() as MainFrameTabHandler<Any>

    init {
        val config = MainFrameConfiguration()
        config.configure()
        val border = UiLibraryAdapter.get().createBorderLayout{
            width = "100%"
            height = "100%"
        }
        _navigationTree = UiLibraryAdapter.get().createTree{
            width = "100%"
            height = "100%"
        }
        _navigationTree.setSelectListener {
            val userData = it.userData
            if(userData !is WorkspaceGroup){
                val handler = navigationItemsHandlers[userData!!::class]!!
                openTab(handler, userData)
            }
        }
        border.setWestRegion{
            title = config.title
            width  = "200px"
            showSplitLine = true
            collapsible = false
            content = _navigationTree
        }
        setWorkspace(WorkspaceProvider.get().getWorkspace())
        _tabs = UiLibraryAdapter.get().createTabboxContainer{
            width = "100%"
            height = "100%"
            tools.addAll(config.tools)
        }
        border.setCenterRegion{
            content = _tabs
        }
        navigationItemsHandlers[ListWorkspaceItem::class] = ListTabHandler() as MainFrameTabHandler<Any>
        _node = border
     }

    fun openTab(handler: MainFrameTabHandler<Any>, userData: Any) {
        val tabId = handler.getTabId(userData)
        val tab = _tabs.getTabs().find { it.id == tabId }
        if(tab != null){
            _tabs.select(tabId)
            return
        }
        val tabData = handler.createTabData(userData, object:MainFrameTabCallback{
            override fun setTitle(title: String) {
                _tabs.setTitle(tabId, title)
            }

            override fun close() {
                _tabs.removeTab(tabId)
            }
        })
        _tabs.addTab(TabPanel(tabId, tabData.title, tabData.content))
    }

    fun publishEvent(event:Any){
        _tabs.getTabs().map { it.comp }.filter { it is EventsSubscriber }.forEach { (it as EventsSubscriber).receiveEvent(event) }
    }

    fun setWorkspace(workspace: Workspace) {
        val result = arrayListOf<TreeItem>()
        workspace.groups.forEach { group ->
            val item = TreeItem(group.uid, group.displayName?:"", group)
            result.add(item)
            group.items.forEach { wi ->
                when(wi){
                    is ListWorkspaceItem ->{
                        item.children.add(TreeItem(wi.uid, wi.displayName?:"", wi))
                    }
                }
            }
        }
        _navigationTree.setData(result)
    }

    fun openTab(ref:ObjectReference<*>, navigationKey:String?){
        openTab(editorHandler, ObjectEditorTabHandlerData(ref, navigationKey))
    }

    companion object{
        fun get():MainFrame{
            return UiLibraryAdapter.get().findRootComponent() as MainFrame
        }
    }

}
