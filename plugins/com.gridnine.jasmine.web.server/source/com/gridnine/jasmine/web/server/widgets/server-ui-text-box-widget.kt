/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.widgets

import com.gridnine.jasmine.server.core.model.ui.TextBoxConfiguration
import com.gridnine.jasmine.web.server.components.BaseServerUiNodeWrapper
import com.gridnine.jasmine.web.server.components.ServerUiLibraryAdapter
import com.gridnine.jasmine.web.server.components.ServerUiTextBox
import com.gridnine.jasmine.web.server.components.ServerUiTextBoxConfiguration

class ServerUiTextBoxWidget(config:ServerUiTextBoxWidgetConfiguration): BaseServerUiNodeWrapper<ServerUiTextBox>(){

    init{
        val comp = ServerUiLibraryAdapter.get().createTextBox(ServerUiTextBoxConfiguration{
            width = config.width
            height = config.height
        })
        _node = comp
    }

    fun setValue(value:String?) = _node.setValue(value)

    fun getValue() = _node.getValue()

    fun showValidation(value: String?) {
        _node.showValidation(value)
    }
    fun configure(config: TextBoxConfiguration) {
        _node.setDisabled(config.notEditable)
    }
}

class ServerUiTextBoxWidgetConfiguration(){
    constructor(config:ServerUiTextBoxWidgetConfiguration.()->Unit):this(){
        config.invoke(this)
    }
    var width:String? = null
    var height:String? = null

}