/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.richlet.pg.components.zk

import com.gridnine.jasmine.web.server.zk.richlet.pg.ServerUiComponent
import com.gridnine.jasmine.web.server.zk.richlet.pg.ZkServerUiComponent
import com.gridnine.jasmine.web.server.zk.richlet.pg.components.ServerUiAccordionContainer
import com.gridnine.jasmine.web.server.zk.richlet.pg.components.ServerUiAccordionContainerConfiguration
import com.gridnine.jasmine.web.server.zk.richlet.pg.components.ServerUiAccordionPanel
import org.zkoss.zk.ui.HtmlBasedComponent
import org.zkoss.zk.ui.event.Events
import org.zkoss.zul.*

open class ZkServerUiAccordionContainer(private val config: ServerUiAccordionContainerConfiguration) : ServerUiAccordionContainer, ZkServerUiComponent(){

    private var component: Tabbox? = null

    private val panels = arrayListOf<ServerUiAccordionPanel>()

    private var tabs:Tabs? = null

    private var tabPanels: Tabpanels? = null

    override fun addPanel(panel: ServerUiAccordionPanel) {
        panels.add(panel)
        if(component != null){
            addPanelInternal(panel)
        }
    }

    private fun addPanelInternal(panel: ServerUiAccordionPanel) {
        val tab = Tab("")
        tab.isClosable = false
        tab.id = panel.id
        tab.label = panel.title
        tabs!!.appendChild(tab)

        val tabbPanel = Tabpanel()
        tabbPanel.vflex = "1"
        tabbPanel.hflex = "1"
        tabbPanel.appendChild((panel.content as ZkServerUiComponent).getComponent())
        tabPanels!!.appendChild(tabbPanel)
    }

    override fun removePanel(id: String) {
        if(panels.removeIf { it.id == id }){
            if(component != null){
                val tab = tabs!!.getChildren<Tab>().find { it.id == id }
                val panel = tabPanels!!.getChildren<Tabpanel>().find { it.linkedTab == tab }
                tabs!!.removeChild(tab)
                tabPanels!!.removeChild(panel)
            }
        }
    }

    override fun select(id: String) {
        val tab = tabs!!.getChildren<Tab>().find { it.id == id }
        val panel = tabPanels!!.getChildren<Tabpanel>().find { it.linkedTab == tab }
        component!!.selectedTab = tab
        component!!.selectedPanel = panel
    }

    override fun getPanels(): List<ServerUiAccordionPanel> {
        return panels
    }

    override fun getComponent(): HtmlBasedComponent {
        if(component!= null){
            return component!!
        }
        val comp = Tabbox()
        comp.mold = "accordion"
        if(config.width == "100%"){
            comp.hflex = "1"
        } else if(config.width != null){
            comp.width = config.width
        }
        if(config.height == "100%"){
            comp.vflex = "1"
        } else if(config.height != null){
            comp.height = config.height
        }
        val tabs = Tabs()
        this.tabs = tabs
        comp.appendChild(tabs)
        val panels = Tabpanels()
        this.tabPanels = panels
        comp.appendChild(panels)
        component = comp
        this.panels.forEach {
            addPanelInternal(it)
        }
        return comp
    }

    override fun getParent(): ServerUiComponent? {
        return parent
    }

}