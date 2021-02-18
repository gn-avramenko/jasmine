/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.richlet.pg

import com.gridnine.jasmine.web.server.zk.richlet.pg.components.ServerUiPanelConfiguration
import com.gridnine.jasmine.web.server.zk.richlet.pg.components.zk.ZkServerUiPanel
import com.gridnine.jasmine.zk.select2.Select2
import com.gridnine.jasmine.zk.select2.Select2DataSourceType
import com.gridnine.jasmine.zk.select2.Select2Option
import org.zkoss.zk.ui.event.Events
import org.zkoss.zk.ui.util.Clients
import org.zkoss.zul.Button
import org.zkoss.zul.Textbox
import org.zkoss.zul.Vbox

class Panel :ZkServerUiPanel(createConfiguration()){

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
        private fun createConfiguration():ServerUiPanelConfiguration{
            val result = ServerUiPanelConfiguration()
            result.height = "100%"
            result.width = "100%"
            result.maximizable = true
            result.minimizable = true
            return result
        }
    }
}