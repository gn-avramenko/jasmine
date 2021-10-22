/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("unused")

package com.gridnine.jasmine.web.standard.widgets

import com.gridnine.jasmine.common.core.model.LongNumberBoxConfigurationJS
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.components.BaseWebNodeWrapper
import com.gridnine.jasmine.web.core.ui.components.WebNumberBox

class LongNumberBoxWidget(configure:LongNumberBoxWidgetConfiguration.()->Unit):BaseWebNodeWrapper<WebNumberBox>(){
    private val config = LongNumberBoxWidgetConfiguration()
    init {
        config.configure()
        _node = WebUiLibraryAdapter.get().createNumberBox{
            width = config.width
            height = config.height
            showClearIcon = config.nullable && config.showClearIcon
            precision = 0
        }
    }
    fun setValue(value:Long?){
        _node.setValue(value?.toDouble())
        _node.showValidation(null)
    }

    fun getValue() = _node.getValue()?.toLong()?:(if(config.nullable) null else 0) as Long?

    fun setReadonly(value:Boolean) {
        _node.setEnabled(!value)
    }

    fun configure(config: LongNumberBoxConfigurationJS?){
        config?.let {
            _node.setEnabled(!config.notEditable)
        }
    }

    fun showValidation(value:String?){
        _node.showValidation(value)
    }
}





class LongNumberBoxWidgetConfiguration:BaseWidgetConfiguration(){
    var nullable = true
    var showClearIcon = true
}