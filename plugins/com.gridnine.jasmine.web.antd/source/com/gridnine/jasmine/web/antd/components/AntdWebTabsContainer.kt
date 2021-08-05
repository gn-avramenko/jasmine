/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jtasks
 *****************************************************************/
package com.gridnine.jasmine.web.antd.components

import com.gridnine.jasmine.web.core.remote.launch
import com.gridnine.jasmine.web.core.ui.components.WebNode
import com.gridnine.jasmine.web.core.ui.components.WebTabPanel
import com.gridnine.jasmine.web.core.ui.components.WebTabsContainer
import com.gridnine.jasmine.web.core.ui.components.WebTabsContainerConfiguration

class AntdWebTabsContainer(configure:WebTabsContainerConfiguration.()->Unit):WebTabsContainer,BaseAntdWebUiComponent() {

    private val config = WebTabsContainerConfiguration()

    private val tabs = arrayListOf<WebTabPanel>()

    private  var activeTabId:String? = null

    init {
        config.configure()
    }
    override fun createReactElementWrapper(): ReactElementWrapper {
        return ReactFacade.createProxy{
            val style = js("{}")
            if(config.fit){
                style.width = "100%"
                style.height = "100%"
            } else {
                if(config.width != null){
                    style.width = config.width
                }
                if(config.height != null){
                    style.height = config.height
                }
            }
            val props = object{
                val hideAdd = true
                val onChange = { key:String ->
                    activeTabId = key
                    maybeRedraw()
                }
                val type="editable-card"
                val activeKey=activeTabId
                val onEdit = {targetKey:String, action:String ->
                    if(action == "remove"){
                        removeTab(targetKey)
                    }
                }
                val style = style
            }.asDynamic()
            if(config.tools.isNotEmpty()){
                val menu = ReactFacade.createElementWithChildren(ReactFacade.Menu,object {
                    val onClick = {event:dynamic ->
                        val key = event.key as String
                        launch {
                            config.tools[key.toInt()].handler.invoke()
                        }
                    }
                }, config.tools.withIndex().map {
                    ReactFacade.createElementWithChildren(ReactFacade.MenuItem, object{
                        val key = it.index.toString()
                    },it.value.displayName)}.toTypedArray())
                val dropdown = ReactFacade.createElementWithChildren(ReactFacade.Dropdown, object {
                   val overlay = menu
                   val placement = "bottomLeft"
                }, ReactFacade.createElementWithChildren(ReactFacade.Button,object{
                                                                                  val size ="large"
                                                                                  }, "Настройки"))
                props.tabBarExtraContent = object{
                    val left =dropdown
                }
            }
            ReactFacade.createElementWithChildren(ReactFacade.Tabs, props, tabs.map {
                ReactFacade.createElementWithChildren(ReactFacade.TabPane,object{
                    val tab= it.title
                    val key= it.id
                }, findAntdComponent(it.content).getReactElement())
            }.toTypedArray() )
        }

    }

    override fun addTab(configure: WebTabPanel.() -> Unit) {
        val panel = WebTabPanel()
        panel.configure()
        tabs.add(panel)
        activeTabId = panel.id
        maybeRedraw()
    }

    override fun removeTab(id: String) {
        tabs.removeAll { it.id == id }
        activeTabId == if(tabs.isNotEmpty()) tabs[0].id else null
        maybeRedraw()
    }

    override fun select(id: String): WebNode? {
        val node = tabs.find { it.id == id}
        if(activeTabId == id){
            return node?.content
        }
        activeTabId = id
        maybeRedraw()
        return node?.content
    }

    override fun getTabs(): List<WebTabPanel> {
        return tabs
    }

    override fun setTitle(tabId: String, title: String) {
        val node = tabs.find { it.id == tabId}?:return
        if(node.title != title){
            node.title = title
            maybeRedraw()
        }
    }
}