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

class ServerUiTextBoxWidget(configure:ServerUiTextBoxWidgetConfiguration.()->Unit): BaseServerUiNodeWrapper<ServerUiTextBox>(){

    private var readonly = false

    private var tbConfig:TextBoxConfiguration? = null

    init {
        val config = ServerUiTextBoxWidgetConfiguration()
        config.configure()
        _node = ServerUiLibraryAdapter.get().createTextBox(ServerUiTextBoxConfiguration{
            width = config.width
            height = config.height
        })
    }

    fun setValue(value:String?) = _node.setValue(value)

    fun getValue() = _node.getValue()

    fun showValidation(value: String?) {
        _node.showValidation(value)
    }

    fun setReadonly(value:Boolean){
        readonly = value
        _node.setDisabled(tbConfig?.notEditable?:false || value)
    }
    fun configure(config: TextBoxConfiguration?) {
        _node.setDisabled(readonly || (config?.notEditable?:false))
    }
}

class ServerUiTextBoxWidgetConfiguration(){
    constructor(config:ServerUiTextBoxWidgetConfiguration.()->Unit):this(){
        config.invoke(this)
    }
    var width:String? = null
    var height:String? = null

}