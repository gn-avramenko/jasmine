/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.widgets

import com.gridnine.jasmine.server.core.model.ui.DateBoxConfiguration
import com.gridnine.jasmine.server.core.model.ui.TextBoxConfiguration
import com.gridnine.jasmine.web.server.components.*
import java.time.LocalDate

class ServerUiDateBoxWidget(configure:ServerUiDateBoxWidgetConfiguration.()->Unit): BaseServerUiNodeWrapper<ServerUiDateBox>(){


    init{
        val config = ServerUiDateBoxWidgetConfiguration()
        config.configure()
        _node = ServerUiLibraryAdapter.get().createDateBox(ServerUiDateBoxConfiguration{
            width = config.width
            height = config.height
        })
    }

    fun setValue(value:LocalDate?) = _node.setValue(value)

    fun getValue() = _node.getValue()

    fun showValidation(value: String?) {
        _node.showValidation(value)
    }

    fun setReadonly(value:Boolean){
        _node.setEnabled(!value)
    }
    fun configure(config: DateBoxConfiguration?) {
        _node.setEnabled(config?.notEditable != false)
    }
}

class ServerUiDateBoxWidgetConfiguration(){
    var width:String? = null
    var height:String? = null
}