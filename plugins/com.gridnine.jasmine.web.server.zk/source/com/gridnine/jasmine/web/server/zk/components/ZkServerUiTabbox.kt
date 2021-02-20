/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.components

import com.gridnine.jasmine.web.server.components.ServerUiComponent
import com.gridnine.jasmine.web.server.components.ServerUiContextMenuStandardItem
import com.gridnine.jasmine.web.server.components.ServerUiTabPanel
import com.gridnine.jasmine.web.server.components.ServerUiTabbox
import com.gridnine.jasmine.web.server.components.ServerUiTabboxConfiguration
import org.zkoss.zk.ui.HtmlBasedComponent
import org.zkoss.zk.ui.event.Events
import org.zkoss.zk.ui.event.MouseEvent
import org.zkoss.zul.*

open class ZkServerUiTabbox(private val config : ServerUiTabboxConfiguration) : ServerUiTabbox, ZkServerUiComponent(){

    private var component:Tabbox? = null

    private var tabs:Tabs? = null

    private  var tabpanels:Tabpanels? = null

    private val panels = arrayListOf<ServerUiTabPanel>()

    private var selectedPanelId:String? = null

    override fun addTab(panel: ServerUiTabPanel) {
        panels.add(panel)
        selectedPanelId = panel.id
        if(component != null){
            addPanelInternal(panel)
        }
    }

    private fun addPanelInternal(panel: ServerUiTabPanel) {
        val tab = Tab()
        tab.isClosable = true
        tab.id = panel.id
        tab.label = panel.title
        tab.addEventListener(Events.ON_CLOSE){
            panels.removeIf { it.id == panel.id }
        }
        tabs!!.appendChild(tab)

        val tabbPanel = Tabpanel()
        tabbPanel.vflex = "1"
        tabbPanel.hflex = "1"
        tabbPanel.appendChild((panel.comp as ZkServerUiComponent).getComponent())
        tabpanels!!.appendChild(tabbPanel)

    }

    override fun removeTab(id: String) {
        panels.removeIf { it.id == id }
        if(component != null){
           val child = tabs!!.getChildren<Tab>().find { it.id == id }
            if(child != null){
                tabs!!.removeChild(child)
            }
        }
    }

    override fun select(id: String): ServerUiTabPanel? {
       selectedPanelId = id
        if(component != null){
            val child = tabs!!.getChildren<Tab>().find { it.id == id }
            if(child != null){
                component!!.selectedTab = child
            }
        }
        return panels.find { it.id == id }
    }

    override fun getTabs(): List<ServerUiTabPanel> {
        return panels
    }

    override fun setTitle(tabId: String, title: String) {
        panels.find { it.id == tabId }?.title = title
        if(component != null){
            val child = tabs!!.getChildren<Tab>().find { it.id == tabId }
            if(child != null){
                child.label = title
            }
        }
    }

    override fun getComponent(): HtmlBasedComponent {
        if(component != null){
            return component!!
        }
        component = Tabbox()
        if(config.width == "100%"){
            component!!.hflex = "1"
        } else if(config.width != null){
            component!!.width = config.width
        }
        if(config.height == "100%"){
            component!!.vflex = "1"
        }else if(config.height != null) {
            component!!.height = config.height
        }
        tabs = Tabs()
        component!!.appendChild(tabs)
        tabpanels = Tabpanels()
        component!!.appendChild(tabpanels)
        if(config.tools.isNotEmpty()){
            val toolbar = Toolbar()
            toolbar.width = "50px"
            toolbar.parent = component
            val button = Toolbarbutton()
            button.parent = toolbar
            button.iconSclass = "z-icon-home"
            button.addEventListener(Events.ON_CLICK){ event ->
                event as MouseEvent
                zkShowContextMenu(config.tools.map { tool ->
                    val item = ServerUiContextMenuStandardItem(tool.text, null, false, tool.handler)
                    item
                }, event.pageX, event.pageY)
            }
        }
        panels.forEach { addPanelInternal(it) }
        return component!!
    }


    override fun getParent(): ServerUiComponent? {
        return parent
    }

}