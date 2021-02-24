/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.richlet.pg

import com.gridnine.jasmine.web.server.components.*
import com.gridnine.jasmine.web.server.zk.components.ZkServerUiBorderContainer
import com.gridnine.jasmine.web.server.zk.components.ZkServerUiComponent
import com.gridnine.jasmine.web.server.zk.components.ZkServerUiTabbox
import com.gridnine.jasmine.web.server.zk.components.ZkServerUiTree
import org.zkoss.zk.ui.util.Clients
import org.zkoss.zul.Div
import java.util.*

class PgMainFrame : Div() {
    init {
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
        appendChild(border.getZkComponent())
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


    class TabbboxComp : ZkServerUiTabbox(createConfiguration()) {
        init {
            addTab(ServerUiTabPanel(UUID.randomUUID().toString(), "Select2", Select2Panel()))
            addTab(ServerUiTabPanel(UUID.randomUUID().toString(), "Grid", ListPanel()))
            addTab(ServerUiTabPanel(UUID.randomUUID().toString(), "GridLayout", GridLayoutPanel()))
            addTab(ServerUiTabPanel(UUID.randomUUID().toString(), "Table", TablePanel()))
            addTab(ServerUiTabPanel(UUID.randomUUID().toString(), "Tree", TreePanel()))
            addTab(ServerUiTabPanel(UUID.randomUUID().toString(), "DivsContainer", DivsContainerPanel()))
            addTab(ServerUiTabPanel(UUID.randomUUID().toString(), "Panel", Panel()))
            addTab(ServerUiTabPanel(UUID.randomUUID().toString(), "Tiles", TilesPanel()))
        }

        companion object {
            private fun createConfiguration(): ServerUiTabboxConfiguration {
                val result = ServerUiTabboxConfiguration()
                result.width = "100%"
                result.height = "100%"
                result.tools.add(ServerUiTabTool("Меню") {
                    Clients.alert("Меню")
                })
                return result
            }
        }

    }

    companion object {
        fun createConfig(): ServerUiBorderContainerConfiguration {
            val config = ServerUiBorderContainerConfiguration()
            config.height = "100%"
            config.width = "100%"
            return config
        }
    }


}