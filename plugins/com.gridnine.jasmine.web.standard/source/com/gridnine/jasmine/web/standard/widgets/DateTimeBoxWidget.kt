/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("unused")

package com.gridnine.jasmine.web.standard.widgets

import com.gridnine.jasmine.common.core.model.DateBoxConfigurationJS
import com.gridnine.jasmine.common.core.model.DateTimeBoxConfigurationJS
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.components.BaseWebNodeWrapper
import com.gridnine.jasmine.web.core.ui.components.WebDateTimeBox
import kotlin.js.Date

class DateTimeBoxWidget(configure:DateTimeBoxWidgetConfiguration.()->Unit):BaseWebNodeWrapper<WebDateTimeBox>(){

    private val config = DateTimeBoxWidgetConfiguration()

    private var conf: DateTimeBoxConfigurationJS? = null

    private var readonly =false

    init {
        config.configure()
        _node = WebUiLibraryAdapter.get().createDateTimeBox {
            width = config.width
            height = config.height
            showClearIcon = config.showClearIcon
        }
    }

    fun setValue(value:Date?) {
        _node.setValue(value)
        _node.showValidation(null)
    }

    fun getValue() = _node.getValue()

    fun setReadonly(value:Boolean) {
        readonly =value
        updateEnabledMode()
    }

    fun configure(config: DateTimeBoxConfigurationJS?){
        conf = config
        updateEnabledMode()
    }

    private fun updateEnabledMode() {
        _node.setEnabled(!((config.notEditable && conf?.notEditable != false) || conf?.notEditable == true || readonly))
    }

    fun showValidation(value:String?){
        _node.showValidation(value)
    }
}





class DateTimeBoxWidgetConfiguration:BaseWidgetConfiguration(){
    var showClearIcon = true
}