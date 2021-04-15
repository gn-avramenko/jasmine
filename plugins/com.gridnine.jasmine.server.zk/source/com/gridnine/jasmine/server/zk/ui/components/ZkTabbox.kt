/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.zk.ui.components

import com.gridnine.jasmine.server.core.ui.components.ContextMenuStandardItem
import com.gridnine.jasmine.server.core.ui.components.TabPanel
import com.gridnine.jasmine.server.core.ui.components.TabboxConfiguration
import org.zkoss.zk.ui.Component
import org.zkoss.zk.ui.HtmlBasedComponent
import org.zkoss.zk.ui.event.Events
import org.zkoss.zk.ui.event.MouseEvent
import org.zkoss.zul.*

open class ZkTabbox(configure: TabboxConfiguration.() -> Unit) : com.gridnine.jasmine.server.core.ui.components.Tabbox, ZkUiComponent{

    private var component:Tabbox? = null

    private var tabs:Tabs? = null

    private  var tabpanels:Tabpanels? = null

    private val panels = arrayListOf<TabPanel>()

    private var selectedPanelId:String? = null

    private val config = TabboxConfiguration()

    init {
        config.configure()
    }

    override fun addTab(panel: TabPanel) {
        panels.add(panel)
        selectedPanelId = panel.id
        if(component != null){
            addPanelInternal(panel)
        }
    }

    private fun addPanelInternal(panel: TabPanel) {
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
        tabbPanel.appendChild(findZkComponent(panel.comp).getZkComponent())
        tabpanels!!.appendChild(tabbPanel)

        select(panel.id)

    }

    override fun removeTab(id: String) {
        panels.removeIf { it.id == id }
        if(component != null){
           val children = tabs!!.getChildren<Component>()
           val child = children.find { it.id == id }
            if(child != null){
                val idx = children.indexOf(child)
                tabs!!.removeChild(child)
                val panelChild = tabpanels!!.getChildren<Component>()[idx]
                tabpanels!!.removeChild(panelChild)
                if(panels.isNotEmpty()){
                    select(panels.last().id)
                }
            }
        }
    }

    override fun select(id: String): TabPanel? {
       selectedPanelId = id
        if(component != null){
            val child = tabs!!.getChildren<Tab>().find { it.id == id }
            if(child != null){
                component!!.selectedTab = child
            }
        }
        return panels.find { it.id == id }
    }

    override fun getTabs(): List<TabPanel> {
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

    override fun getZkComponent(): HtmlBasedComponent {
        if(component != null){
            return component!!
        }
        component = Tabbox()
        configureDimensions(component!!, config)
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
                    val item = ContextMenuStandardItem(tool.text, null, false, tool.handler)
                    item
                }, event.pageX, event.pageY)
            }
        }
        panels.forEach { addPanelInternal(it) }
        return component!!
    }

}