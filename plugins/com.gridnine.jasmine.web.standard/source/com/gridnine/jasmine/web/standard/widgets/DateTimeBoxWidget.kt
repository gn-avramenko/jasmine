/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("unused")

package com.gridnine.jasmine.web.standard.widgets

import com.gridnine.jasmine.common.core.model.DateTimeBoxConfigurationJS
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.components.BaseWebNodeWrapper
import com.gridnine.jasmine.web.core.ui.components.WebDateTimeBox
import kotlin.js.Date

class DateTimeBoxWidget(configure:DateTimeBoxWidgetConfiguration.()->Unit):BaseWebNodeWrapper<WebDateTimeBox>(){

    private val config = DateTimeBoxWidgetConfiguration()
    init {
        config.configure()
        _node = WebUiLibraryAdapter.get().createDateTimeBox {
            width = config.width
            height = config.height
            showClearIcon = config.showClearIcon
        }
    }

    fun setValue(value:Date?) = _node.setValue(value)

    fun getValue() = _node.getValue()

    fun setReadonly(value:Boolean) {
        _node.setEnabled(!value)
    }

    fun configure(config: DateTimeBoxConfigurationJS?){
        config?.let {
            _node.setEnabled(!config.notEditable)
        }
    }

    fun showValidation(value:String?){
        _node.showValidation(value)
    }
}





class DateTimeBoxWidgetConfiguration:BaseWidgetConfiguration(){
    var showClearIcon = true
}