/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.richlet.pg

import com.gridnine.jasmine.zk.select2.Select2
import com.gridnine.jasmine.zk.select2.Select2ChangeEvent
import com.gridnine.jasmine.zk.select2.Select2DataSourceType
import com.gridnine.jasmine.zk.select2.Select2Option
import org.zkoss.zk.ui.event.Events
import org.zkoss.zul.*

class MainFrame:Borderlayout {
    constructor(){
        val wc = West()
        wc.title = "Jasmine"
        wc.size = "200px"
        wc.maxsize = 250
        wc.minsize = 200
        wc.isSplittable = true
        wc.isCollapsible = false
        wc.margins="0,0,0,0"
        val tree = createNavigation()
        wc.cmargins
        wc.appendChild(tree)
        appendChild(wc)
        val cc = Center()
//        cc.appendChild(Select2Panel())
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
        cc.appendChild(tabbox)
        appendChild(cc)

    }

    private fun createNavigation(): Tree {
        val tree = Tree()
        tree.vflex = "1"
        val model = DefaultTreeModel(DefaultTreeNode(null, arrayOf(DefaultTreeNode(createSettingsNode(), arrayOf(createListNode())))))
        tree.setModel(model)
        tree.setItemRenderer(object:TreeitemRenderer<DefaultTreeNode<TreeNodeData>>{
            override fun render(item: Treeitem, data: DefaultTreeNode<TreeNodeData>, index: Int) {
                item.label = data.data.label
            }
        })
        tree.style = "padding:0px"
        return tree
    }

    private fun  createSettingsNode(): TreeNodeData {
        val result = TreeNodeData()
        result.label = "Настройки"
        return  result
    }

    private fun createListNode(): DefaultTreeNode<TreeNodeData> {
        val result = TreeNodeData()
        result.label = "Список"
        return  DefaultTreeNode(result)
    }

    class TreeNodeData{
        lateinit var label:String
    }


}