/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.widgets

import com.gridnine.jasmine.server.core.model.ui.FloatNumberBoxConfiguration
import com.gridnine.jasmine.server.core.model.ui.IntegerNumberBoxConfiguration
import com.gridnine.jasmine.web.server.components.BaseServerUiNodeWrapper
import com.gridnine.jasmine.web.server.components.ServerUiLibraryAdapter
import com.gridnine.jasmine.web.server.components.ServerUiNumberBox
import com.gridnine.jasmine.web.server.components.ServerUiNumberBoxConfiguration
import java.math.BigDecimal

class ServerUiIntBoxWidget(private val config:ServerUiBigIntBoxWidgetConfiguration): BaseServerUiNodeWrapper<ServerUiNumberBox>(){

    init{
        _node = ServerUiLibraryAdapter.get().createNumberBox(ServerUiNumberBoxConfiguration(){
            width = config.width
            height = config.height
            precision= 0
        })
    }

    fun setValue(value:Int?) = _node.setValue(value?.let{BigDecimal.valueOf(it.toLong())})

    fun getValue() = _node.getValue()?.toInt() ?:(if(config.nullable) null else 0)

    fun showValidation(value: String?) {
        _node.showValidation(value)
    }

    fun setReadonly(value:Boolean){
        _node.setEnabled(!value)
    }

    fun configure(config: IntegerNumberBoxConfiguration?) {
        _node.setEnabled(config?.notEditable != false)
    }
}

class ServerUiBigIntBoxWidgetConfiguration(){
    constructor(config:ServerUiBigIntBoxWidgetConfiguration.()->Unit):this(){
        config.invoke(this)
    }
    var width:String? = null
    var height:String? = null
    var nullable = true
}