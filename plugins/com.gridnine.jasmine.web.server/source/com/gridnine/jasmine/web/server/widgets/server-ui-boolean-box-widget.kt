/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.widgets

import com.gridnine.jasmine.server.core.model.ui.BooleanBoxConfiguration
import com.gridnine.jasmine.server.core.model.ui.FloatNumberBoxConfiguration
import com.gridnine.jasmine.server.core.model.ui.IntegerNumberBoxConfiguration
import com.gridnine.jasmine.web.server.components.*
import java.math.BigDecimal

class ServerUiBooleanBoxWidget(configure:ServerUiBooleanBoxWidgetConfiguration.()->Unit): BaseServerUiNodeWrapper<ServerUiBooleanBox>(){

    init{
        val config = ServerUiBooleanBoxWidgetConfiguration()
        config.configure()
        _node = ServerUiLibraryAdapter.get().createBooleanBox(ServerUiBooleanBoxConfiguration{
            width = config.width
            height = config.height
        })
    }

    fun setValue(value:Boolean) = _node.setValue(value)

    fun getValue() = _node.getValue()

    fun showValidation(value: String?) {
        //noops
    }

    fun setReadonly(value:Boolean){
        _node.setEnabled(!value)
    }

    fun configure(config: BooleanBoxConfiguration?) {
        _node.setEnabled(config?.notEditable != false)
    }
}

class ServerUiBooleanBoxWidgetConfiguration(){
    var width:String? = null
    var height:String? = null
}