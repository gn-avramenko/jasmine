/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.richlet.pg

import com.gridnine.jasmine.web.server.zk.richlet.pg.components.ServerUiLinkButtonConfiguration
import com.gridnine.jasmine.web.server.zk.richlet.pg.components.zk.ZkServerUiLinkButton

class TablePanel : ZkServerUiTable(createConfiguration()){


    init {
        addTestRow()
    }

    private fun addTestRow() {
        val firstRowComponents = arrayListOf<ServerUiTableCell>()
        val textBoxConfig = ServerUiTextBoxConfiguration()
        textBoxConfig.width = "100%"
        val textBox = ZkServerUiTextBox(textBoxConfig)
        firstRowComponents.add(ServerUiTableCell(textBox, 1))
        firstRowComponents.add(ServerUiTableCell(null, 1))
        val buttonConfig = ServerUiLinkButtonConfiguration()
        buttonConfig.title = "Add"
        val button = ZkServerUiLinkButton(buttonConfig)
        button.setHandler {
            addTestRow()
        }
        firstRowComponents.add(ServerUiTableCell(button))
        addRow(null, firstRowComponents)
    }

    companion object{
        private fun createConfiguration(): ServerUiTableConfiguration {
            val result = ServerUiTableConfiguration()
            result.width = "100%"
            result.columns.add(ServerUiTableColumnDescription("Поле 1", 100, 100, null))
            result.columns.add(ServerUiTableColumnDescription("Поле 2", 100, 200, null))
            result.columns.add(ServerUiTableColumnDescription(null, 50, 50, 50))
            return result
        }
    }
}


