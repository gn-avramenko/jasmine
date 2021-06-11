/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("unused")

package com.gridnine.jasmine.web.standard.mainframe

import com.gridnine.jasmine.common.standard.model.rest.BaseWorkspaceItemDTJS
import com.gridnine.jasmine.common.standard.model.rest.GetWorkspaceItemRequestJS
import com.gridnine.jasmine.common.standard.model.rest.WorkspaceDTJS
import com.gridnine.jasmine.common.standard.model.rest.WorkspaceGroupDTJS
import com.gridnine.jasmine.web.core.common.EnvironmentJS
import com.gridnine.jasmine.web.core.common.RegistryItemJS
import com.gridnine.jasmine.web.core.common.RegistryItemTypeJS
import com.gridnine.jasmine.web.core.common.RegistryJS
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.components.*
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import com.gridnine.jasmine.web.standard.StandardRestClient


open class MainFrame(configure: MainFrameConfiguration.()->Unit):BaseWebNodeWrapper<WebBorderContainer>(){

    private val navigationTree:WebTree

    val tabs:WebTabsContainer

    private val itemsCache = hashMapOf<String, BaseWorkspaceItemDTJS>()

    private val uid = MiscUtilsJS.createUUID()

    init {
        val config = MainFrameConfiguration()
        config.configure()
        _node = WebUiLibraryAdapter.get().createBorderContainer {
            fit = true
        }
        val westContent = WebUiLibraryAdapter.get().createBorderContainer {
            fit = true
        }
        westContent.setNorthRegion {
            content = WebUiLibraryAdapter.get().createLabel {
                width = "100%"
                className = "jasmine-logo"
            }.apply {
                setText(config.title)
            }
        }
        navigationTree = WebUiLibraryAdapter.get().createTree {
            width = "100%"
            height = "100%"
            fit = true
        }
        westContent.setCenterRegion {
            content = navigationTree
        }
        _node.setWestRegion {
            showSplitLine = true
            collapsible = false
            width = config.navigationWidth
            content = westContent
        }
        tabs = WebUiLibraryAdapter.get().createTabsContainer {
            fit = true
            tools.addAll(config.tools)
        }
        _node.setCenterRegion {
            content = tabs
        }
        navigationTree.setSelectListener {
             if(it.userData is WorkspaceGroupDTJS){
                 return@setSelectListener
             }
            val item = itemsCache.getOrPut(it.id){
                StandardRestClient.standard_standard_getWorkspaceItem(GetWorkspaceItemRequestJS().apply { uid =  it.id}).workspaceItem
            }
            openTab(item)
        }
    }

    fun publishEvent(event:Any){
        tabs.getTabs().filter { it.content is EventsSubscriber }.forEach { (it.content as EventsSubscriber).receiveEvent(event) }
    }

    suspend fun openTab(obj:Any):TabPanelContainer{
        val handler = RegistryJS.get().get(MainFrameTabHandler.TYPE, obj::class.simpleName!!)!!
        val tabId = "$uid|${handler.getTabId(obj)}"
        val existingTab = tabs.getTabs().find { it.id == tabId }
        if(existingTab != null){
            return TabPanelContainer(tabId, tabs.select(tabId)!!)
        }
        val callback = object:MainFrameTabCallback {
            private var currentTitle:String? = null
            override fun setTitle(title: String) {
                if(currentTitle == null){
                    currentTitle = title
                    return
                }
                if(currentTitle != title){
                    currentTitle = title
                    tabs.setTitle(tabId, title)
                    return
                }
            }

            override fun close() {
                tabs.removeTab(tabId)
            }

        }
        val tabData = handler.createTabData(obj, callback)
        tabs.addTab{
            id = tabId
            title = tabData.title
            content = tabData.content
        }
        callback.setTitle(tabData.title)
        return TabPanelContainer(tabId, tabData.content)
    }

    fun setWorkspace(value:WorkspaceDTJS){
        itemsCache.clear()
        val data = arrayListOf<WebTreeNode>()
        value.groups.forEach {wg ->
            val group = WebTreeNode(wg.uid!!, wg.displayName!!, wg)
            data.add(group)
            wg.items.forEach {wi ->
                group.children.add(WebTreeNode(wi.id, wi.text, wi))
            }
        }
        navigationTree.setData(data)
    }

    companion object{
        fun get() = EnvironmentJS.getPublished(MainFrame::class)
    }
}



class MainFrameConfiguration{
    lateinit var title:String
    var navigationWidth:Int =200
    val tools = arrayListOf<WebTabsContainerTool>()
}

interface MainFrameTabCallback{
    fun setTitle(title:String)
    fun close()
}

interface EventsSubscriber{
    fun receiveEvent(event:Any)
}

data class ObjectModificationEvent(val objectType: String, val objectUid:String)

data class ObjectDeleteEvent(val objectType: String, val objectUid:String)

data class TabPanelContainer(val tabId: String, val content:WebNode)

data class MainFrameTabData(var title:String, var content:WebNode)

interface MainFrameTabHandler<T:Any>:RegistryItemJS<MainFrameTabHandler<Any>>{
    fun getTabId(obj:T):String
    suspend fun createTabData(obj:T, callback: MainFrameTabCallback):MainFrameTabData

    override fun getType(): RegistryItemTypeJS<MainFrameTabHandler<Any>> = TYPE

    companion object{
        val TYPE = RegistryItemTypeJS<MainFrameTabHandler<Any>>("mainframe-tab-handlers")
    }
}



