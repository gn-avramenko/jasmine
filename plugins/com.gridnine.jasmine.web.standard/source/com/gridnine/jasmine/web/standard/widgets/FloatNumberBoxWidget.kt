/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("unused")

package com.gridnine.jasmine.web.standard.widgets

import com.gridnine.jasmine.common.core.model.BigDecimalBoxConfigurationJS
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.components.BaseWebNodeWrapper
import com.gridnine.jasmine.web.core.ui.components.WebNumberBox

class FloatNumberBoxWidget(configure:FloatNumberBoxWidgetConfiguration.()->Unit):BaseWebNodeWrapper<WebNumberBox>(){
    private val config = FloatNumberBoxWidgetConfiguration()
    init {
        config.configure()
        _node = WebUiLibraryAdapter.get().createNumberBox{
            width = config.width
            height = config.height
            showClearIcon = config.showClearIcon
            precision = config.precision
        }
    }
    fun setValue(value:Double?) = _node.setValue(value)

    fun getValue() = _node.getValue()


    fun setReadonly(value:Boolean) {
        _node.setEnabled(!value)
    }

    fun configure(config: BigDecimalBoxConfigurationJS?){
        config?.let {
            _node.setEnabled(!config.notEditable)
        }
    }

    fun showValidation(value:String?){
        _node.showValidation(value)
    }
}





class FloatNumberBoxWidgetConfiguration:BaseWidgetConfiguration(){
    var showClearIcon = true
    var precision = 2
}