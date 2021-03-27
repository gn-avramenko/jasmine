/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.widgets

import com.gridnine.jasmine.server.core.model.ui.PasswordBoxConfiguration
import com.gridnine.jasmine.server.core.model.ui.TextBoxConfiguration
import com.gridnine.jasmine.web.server.components.*

class ServerUiPasswordBoxWidget(config:ServerUiPasswordBoxWidgetConfiguration): BaseServerUiNodeWrapper<ServerUiPasswordBox>(){

    init{
        val comp = ServerUiLibraryAdapter.get().createPasswordBox(ServerUiPasswordBoxConfiguration{
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

    fun setReadonly(value:Boolean){
        _node.setDisabled(value)
    }
    fun configure(config: PasswordBoxConfiguration?) {
        _node.setDisabled(config?.notEditable?:false)
    }
}

class ServerUiPasswordBoxWidgetConfiguration(){
    constructor(config:ServerUiPasswordBoxWidgetConfiguration.()->Unit):this(){
        config.invoke(this)
    }
    var width:String? = null
    var height:String? = null

}