/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.richlet.pg

import com.gridnine.jasmine.zk.select2.Select2
import com.gridnine.jasmine.zk.select2.Select2DataSourceType
import com.gridnine.jasmine.zk.select2.Select2Option
import org.zkoss.zk.ui.event.Events
import org.zkoss.zul.Button
import org.zkoss.zul.Textbox
import org.zkoss.zul.Vbox

class GridLayoutPanel :ZkServerUiGridLayoutContainer(createConfiguration()){

    init {
        addRow("auto")
        val config1 = ServerUiTextBoxConfiguration()
        config1.width = "100%"
        config1.height = "20px"
        addCell(ServerUiGridLayoutCell(ZkServerUiTextBox(config1)))
        addRow("auto")
        val config2 = ServerUiTextBoxConfiguration()
        config2.width = "100%"
        config2.height = "20px"
        addCell(ServerUiGridLayoutCell(ZkServerUiTextBox(config2), 2))
        addRow("100%")
    }

    companion object{
        private fun createConfiguration():ServerUiGridLayoutContainerConfiguration{
            val result = ServerUiGridLayoutContainerConfiguration()
            result.height = "100%"
            result.width = "100%"
            result.noPadding = false
            result.columns.add(ServerUiGridLayoutColumnConfiguration("200px"))
            result.columns.add(ServerUiGridLayoutColumnConfiguration("100%"))
            return result
        }
    }
}