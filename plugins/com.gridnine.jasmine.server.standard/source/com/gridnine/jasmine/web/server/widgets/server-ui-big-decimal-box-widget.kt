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

class ServerUiBigDecimalBoxWidget(configure:ServerUiBigDecimalBoxWidgetConfiguration.()->Unit): BaseServerUiNodeWrapper<ServerUiNumberBox>(){

    init{
        val config = ServerUiBigDecimalBoxWidgetConfiguration()
        config.configure()
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

    fun setReadonly(value:Boolean){
        _node.setEnabled(!value)
    }
    fun configure(config: FloatNumberBoxConfiguration?) {
        _node.setEnabled(config?.notEditable != false)
    }
}

class ServerUiBigDecimalBoxWidgetConfiguration(){
    var width:String? = null
    var height:String? = null
    var precision = 2
}