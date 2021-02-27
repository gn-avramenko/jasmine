/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.widgets

import com.gridnine.jasmine.server.core.model.ui.DateTimeBoxConfiguration
import com.gridnine.jasmine.web.server.components.BaseServerUiNodeWrapper
import com.gridnine.jasmine.web.server.components.ServerUiDateTimeBox
import com.gridnine.jasmine.web.server.components.ServerUiDateTimeBoxConfiguration
import com.gridnine.jasmine.web.server.components.ServerUiLibraryAdapter
import java.time.LocalDateTime

class ServerUiDateTimeBoxWidget(config:ServerUiDateTimeBoxWidgetConfiguration): BaseServerUiNodeWrapper<ServerUiDateTimeBox>(){

    init{
        _node = ServerUiLibraryAdapter.get().createDateTimeBox(ServerUiDateTimeBoxConfiguration{
            width = config.width
            height = config.height
        })
    }

    fun setValue(value:LocalDateTime?) = _node.setValue(value)

    fun getValue() = _node.getValue()

    fun showValidation(value: String?) {
        _node.showValidation(value)
    }

    fun configure(config: DateTimeBoxConfiguration) {
        _node.setEnabled(!config.notEditable)
    }
}

class ServerUiDateTimeBoxWidgetConfiguration(){
    constructor(config:ServerUiDateTimeBoxWidgetConfiguration.()->Unit):this(){
        config.invoke(this)
    }
    var width:String? = null
    var height:String? = null

}