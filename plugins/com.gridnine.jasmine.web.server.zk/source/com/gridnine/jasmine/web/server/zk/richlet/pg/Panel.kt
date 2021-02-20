/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.richlet.pg

import com.gridnine.jasmine.web.server.components.ServerUiPanelConfiguration
import com.gridnine.jasmine.web.server.components.ServerUiTextBoxConfiguration
import com.gridnine.jasmine.web.server.zk.components.ZkServerUiPanel
import com.gridnine.jasmine.web.server.zk.components.ZkServerUiTextBox
import org.zkoss.zk.ui.util.Clients

class Panel : ZkServerUiPanel(createConfiguration()){

    init {
        setTitle("Panel")
        setMinimizeHandler {
            Clients.alert("minimized")
        }
        setMaximizeHandler {
            Clients.alert("maximized")
        }
        val config = ServerUiTextBoxConfiguration()
        config.width = "100%"
        setContent(ZkServerUiTextBox(config))
    }

    companion object{
        private fun createConfiguration(): ServerUiPanelConfiguration {
            val result = ServerUiPanelConfiguration()
            result.height = "100%"
            result.width = "100%"
            result.maximizable = true
            result.minimizable = true
            return result
        }
    }
}