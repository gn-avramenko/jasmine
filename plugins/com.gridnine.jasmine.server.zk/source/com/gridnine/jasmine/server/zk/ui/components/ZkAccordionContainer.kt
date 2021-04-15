/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.zk.ui.components

import com.gridnine.jasmine.server.core.ui.components.AccordionContainer
import com.gridnine.jasmine.server.core.ui.components.AccordionContainerConfiguration
import com.gridnine.jasmine.server.core.ui.components.AccordionPanel
import org.zkoss.zk.ui.HtmlBasedComponent
import org.zkoss.zul.*

open class ZkAccordionContainer(configure: AccordionContainerConfiguration.() -> Unit) : AccordionContainer, ZkUiComponent{

    private var component: Tabbox? = null

    private val panels = arrayListOf<AccordionPanel>()

    private var tabs:Tabs? = null

    private var tabPanels: Tabpanels? = null

    private var selectedTabId:String? = null

    private val config = AccordionContainerConfiguration()
    init {
        config.configure()
    }

    override fun addPanel(panel: AccordionPanel) {
        panels.add(panel)
        if(component != null){
            addPanelInternal(panel)
        }
    }

    private fun addPanelInternal(panel: AccordionPanel) {
        val tab = Tab("")
        tab.isClosable = false
        tab.id = panel.id
        tab.label = panel.title
        tabs!!.appendChild(tab)

        val tabbPanel = Tabpanel()
        tabbPanel.vflex = "1"
        tabbPanel.hflex = "1"
        tabbPanel.appendChild(findZkComponent(panel.content).getZkComponent())
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
        if(selectedTabId != id){
            selectedTabId = id
            if(component != null){
                selectTabInternal()
            }
        }

    }

    private fun selectTabInternal() {
        val tab = tabs!!.getChildren<Tab>().find { it.id == selectedTabId }
        val panel = tabPanels!!.getChildren<Tabpanel>().find { it.linkedTab == tab }
        component!!.selectedTab = tab
        component!!.selectedPanel = panel
    }

    override fun getPanels(): List<AccordionPanel> {
        return panels
    }

    override fun getZkComponent(): HtmlBasedComponent {
        if(component!= null){
            return component!!
        }
        val comp = Tabbox()
        comp.mold = "accordion"
        configureDimensions(comp, config)
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
        if(selectedTabId != null){
            selectTabInternal()
        }
        return comp
    }

}