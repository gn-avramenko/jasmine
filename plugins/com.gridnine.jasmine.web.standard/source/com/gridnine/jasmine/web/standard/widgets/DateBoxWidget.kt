/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("unused")

package com.gridnine.jasmine.web.standard.widgets

import com.gridnine.jasmine.common.core.model.DateBoxConfigurationJS
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.components.BaseWebNodeWrapper
import com.gridnine.jasmine.web.core.ui.components.WebDateBox
import kotlin.js.Date

class DateBoxWidget(configure:DateBoxWidgetConfiguration.()->Unit):BaseWebNodeWrapper<WebDateBox>(){

    private val config = DateBoxWidgetConfiguration()

    private var conf:DateBoxConfigurationJS? = null

    private var readonly =false
    init {
        config.configure()
        _node = WebUiLibraryAdapter.get().createDateBox {
            width = config.width
            height = config.height
            showClearIcon = config.showClearIcon
        }
    }

    fun setValue(value:Date?) = _node.setValue(value)

    fun getValue() = _node.getValue()

    fun setReadonly(value:Boolean) {
        readonly = value
        updateEnabledMode()
    }

    fun configure(config: DateBoxConfigurationJS?){
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





class DateBoxWidgetConfiguration:BaseWidgetConfiguration(){
    var showClearIcon = true
}