/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.widgets

import com.gridnine.jasmine.server.core.model.ui.FloatNumberBoxConfiguration
import com.gridnine.jasmine.web.server.components.BaseServerUiNodeWrapper
import com.gridnine.jasmine.web.server.components.ServerUiLibraryAdapter
import com.gridnine.jasmine.web.server.components.ServerUiNumberBox
import com.gridnine.jasmine.web.server.components.ServerUiNumberBoxConfiguration
import java.math.BigDecimal

class ServerUiBigDecimalBoxWidget(config:ServerUiBigDecimalBoxWidgetConfiguration): BaseServerUiNodeWrapper<ServerUiNumberBox>(){

    init{
        _node = ServerUiLibraryAdapter.get().createNumberBox(ServerUiNumberBoxConfiguration(){
            width = config.width
            height = config.height
            precision= config.precision
        })
    }

    fun setValue(value:BigDecimal?) = _node.setValue(value)

    fun getValue() = _node.getValue()

    fun showValidation(value: String?) {
        _node.showValidation(value)
    }

    fun configure(config: FloatNumberBoxConfiguration) {
        _node.setEnabled(!config.notEditable)
    }
}

class ServerUiBigDecimalBoxWidgetConfiguration(){
    constructor(config:ServerUiBigDecimalBoxWidgetConfiguration.()->Unit):this(){
        config.invoke(this)
    }
    var width:String? = null
    var height:String? = null
    var precision = 2
}