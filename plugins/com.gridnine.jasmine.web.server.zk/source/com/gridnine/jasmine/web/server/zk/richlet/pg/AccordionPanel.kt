/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.richlet.pg

import com.gridnine.jasmine.web.server.zk.richlet.pg.components.ServerUiAccordionContainerConfiguration
import com.gridnine.jasmine.web.server.zk.richlet.pg.components.ServerUiAccordionPanel
import com.gridnine.jasmine.web.server.zk.richlet.pg.components.ServerUiTextBoxConfiguration
import com.gridnine.jasmine.web.server.zk.richlet.pg.components.zk.ZkServerUiAccordionContainer
import com.gridnine.jasmine.web.server.zk.richlet.pg.components.zk.ZkServerUiTextBox

class AccordionPanel : ZkServerUiAccordionContainer(createConfiguration()){
    init {
        val panel1Config = ServerUiAccordionPanel("textbox1", "Text box 1", ZkServerUiTextBox(createTextboxConfig()))
        addPanel(panel1Config)
        val panel2Config = ServerUiAccordionPanel("textbox2", "Text box 2", ZkServerUiTextBox(createTextboxConfig()))
        addPanel(panel2Config)
    }


    companion object{
        private fun createConfiguration(): ServerUiAccordionContainerConfiguration {
            return ServerUiAccordionContainerConfiguration("300px", "100%")
        }
        private fun createTextboxConfig(): ServerUiTextBoxConfiguration {
                val result =ServerUiTextBoxConfiguration()
            result.width = "100%"
            return result
        }
    }
}


