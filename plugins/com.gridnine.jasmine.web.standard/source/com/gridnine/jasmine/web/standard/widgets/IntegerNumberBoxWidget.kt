/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("unused")

package com.gridnine.jasmine.web.standard.widgets

import com.gridnine.jasmine.common.core.model.IntegerNumberBoxConfigurationJS
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.components.BaseWebNodeWrapper
import com.gridnine.jasmine.web.core.ui.components.WebNumberBox

class IntegerNumberBoxWidget(configure:IntegerNumberBoxWidgetConfiguration.()->Unit):BaseWebNodeWrapper<WebNumberBox>(){
    private val config = IntegerNumberBoxWidgetConfiguration()
    init {
        config.configure()
        _node = WebUiLibraryAdapter.get().createNumberBox{
            width = config.width
            height = config.height
            showClearIcon = config.nullable && config.showClearIcon
            precision = 0
        }
    }
    fun setValue(value:Int?) {
        _node.setValue(value?.toDouble())
        _node.showValidation(null)
    }

    fun getValue() = _node.getValue()?.toInt()?:(if(config.nullable) null else 0)


    fun setReadonly(value:Boolean) {
        _node.setEnabled(!value)
    }

    fun configure(config: IntegerNumberBoxConfigurationJS?){
        config?.let {
            _node.setEnabled(!config.notEditable)
        }
    }

    fun showValidation(value:String?){
        _node.showValidation(value)
    }
}





class IntegerNumberBoxWidgetConfiguration:BaseWidgetConfiguration(){
    var nullable = true
    var showClearIcon = true
}