/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.richlet.pg

import com.gridnine.jasmine.web.server.zk.richlet.pg.components.ServerUiBorderContainerConfiguration
import com.gridnine.jasmine.web.server.zk.richlet.pg.components.ServerUiBorderContainerRegion
import com.gridnine.jasmine.web.server.zk.richlet.pg.components.ServerUiTreeConfiguration
import com.gridnine.jasmine.web.server.zk.richlet.pg.components.ServerUiTreeItem
import com.gridnine.jasmine.web.server.zk.richlet.pg.components.zk.ZkServerUiBorderContainer
import com.gridnine.jasmine.web.server.zk.richlet.pg.components.zk.ZkServerUiTree
import org.zkoss.zk.ui.HtmlBasedComponent
import org.zkoss.zul.*

class MainFrame:Div() {
    init{
        hflex = "1"
        vflex = "1"
        val border = ZkServerUiBorderContainer(createConfig())
        val westRegion = ServerUiBorderContainerRegion()
        westRegion.title = "Jasmine"
        westRegion.width = "200px"
        westRegion.showSplitLine = true
        westRegion.collapsible = true
        westRegion.content = createNavigation()
        border.setWestRegion(westRegion)

        val centerRegion = ServerUiBorderContainerRegion()
         centerRegion.content = TabbboxComp()
        border.setCenterRegion(centerRegion)
        appendChild(border.getComponent())
    }

    private fun createNavigation(): ZkServerUiComponent {
        val config = ServerUiTreeConfiguration()
        config.width = "100%"
        config.height = "100%"
        val comp = ZkServerUiTree(config)
        val items = arrayListOf<ServerUiTreeItem>()
        items.add(ServerUiTreeItem("settings", "Настройки", null))
        comp.setData(items)
        return comp
    }




    class TabbboxComp: ZkServerUiComponent(){
        override fun getComponent(): HtmlBasedComponent {
            val tabbox = Tabbox()
            tabbox.hflex = "1"
            tabbox.vflex = "1"
            val tabs = Tabs()
            tabbox.appendChild(tabs)
            val panels = Tabpanels()
            tabbox.appendChild(panels)
            run{
                val tab = Tab("")
                tab.isClosable = true
                tab.id = "select2"
                tab.label = "Select2"
                tabs.appendChild(tab)


                val tabbPanel = Tabpanel()
                tabbPanel.vflex = "1"
                tabbPanel.hflex = "1"
                val panel = Select2Panel()
                panel.hflex = "1"
                panel.vflex = "1"
                tabbPanel.appendChild(panel)
                panels.appendChild(tabbPanel)

            }
            run{
                val tab = Tab("")
                tab.isClosable = true
                tab.id = "Grid"
                tab.label = "Grid"
                tabs.appendChild(tab)


                val tabbPanel = Tabpanel()
                tabbPanel.vflex = "1"
                tabbPanel.hflex = "1"
                val panel = ListPanel()
                panel.hflex = "1"
                panel.vflex = "1"
                tabbPanel.appendChild(panel)
                panels.appendChild(tabbPanel)

            }
            run{
                val tab = Tab("")
                tab.isClosable = true
                tab.id = "GridLayout"
                tab.label = "Grid Layout"
                tabs.appendChild(tab)


                val tabbPanel = Tabpanel()
                tabbPanel.vflex = "1"
                tabbPanel.hflex = "1"
                val panel = GridLayoutPanel()
                tabbPanel.appendChild(panel.getComponent())
                panels.appendChild(tabbPanel)

            }
            run{
                val tab = Tab("")
                tab.isClosable = true
                tab.id = "Table"
                tab.label = "Table"
                tabs.appendChild(tab)


                val tabbPanel = Tabpanel()
                tabbPanel.vflex = "1"
                tabbPanel.hflex = "1"
                val panel = TablePanel()
                tabbPanel.appendChild(panel.getComponent())
                panels.appendChild(tabbPanel)

            }
            run{
                val tab = Tab("")
                tab.isClosable = true
                tab.id = "Tree"
                tab.label = "Tree"
                tabs.appendChild(tab)


                val tabbPanel = Tabpanel()
                tabbPanel.vflex = "1"
                tabbPanel.hflex = "1"
                val panel = TreePanel()
                tabbPanel.appendChild(panel.getComponent())
                panels.appendChild(tabbPanel)

            }
            run{
                val tab = Tab("")
                tab.isClosable = true
                tab.id = "Accordion"
                tab.label = "Accordion"
                tabs.appendChild(tab)


                val tabbPanel = Tabpanel()
                tabbPanel.vflex = "1"
                tabbPanel.hflex = "1"
                val panel = AccordionPanel()
                tabbPanel.appendChild(panel.getComponent())
                panels.appendChild(tabbPanel)

            }
            run{
                val tab = Tab("")
                tab.isClosable = true
                tab.id = "DivsContainer"
                tab.label = "DivsContainer"
                tabs.appendChild(tab)


                val tabbPanel = Tabpanel()
                tabbPanel.vflex = "1"
                tabbPanel.hflex = "1"
                val panel = DivsContainerPanel()
                tabbPanel.appendChild(panel.getComponent())
                panels.appendChild(tabbPanel)

            }
            run{
                val tab = Tab("")
                tab.isClosable = true
                tab.id = "Panel"
                tab.label = "Panel"
                tabs.appendChild(tab)


                val tabbPanel = Tabpanel()
                tabbPanel.vflex = "1"
                tabbPanel.hflex = "1"
                val panel = Panel()
                tabbPanel.appendChild(panel.getComponent())
                panels.appendChild(tabbPanel)

            }
            return tabbox
        }

        override fun getParent(): ServerUiComponent? {
            return parent
        }

    }
    companion object{
        fun createConfig():ServerUiBorderContainerConfiguration{
            val config = ServerUiBorderContainerConfiguration()
            config.height = "100%"
            config.width = "100%"
            return config
        }
    }


}