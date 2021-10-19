/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("unused")

package com.gridnine.jasmine.web.standard.widgets

import com.gridnine.jasmine.common.core.model.TextBoxConfigurationJS
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.components.BaseWebNodeWrapper
import com.gridnine.jasmine.web.core.ui.components.WebTextBox

class TextBoxWidget(configure:TextBoxWidgetConfiguration.()->Unit):BaseWebNodeWrapper<WebTextBox>(){

    private val config = TextBoxWidgetConfiguration()

    private var conf:TextBoxConfigurationJS? = null
    private var readonly = false
    init {
        config.configure()
        _node = WebUiLibraryAdapter.get().createTextBox {
            width = config.width
            height = config.height
            showClearIcon = config.showClearIcon
            multiline = config.multiline
        }
        updateDisabledMode()
    }

    fun setValue(value:String?) = _node.setValue(value)

    fun getValue() = _node.getValue()

    fun setReadonly(value:Boolean) {
        readonly = value
        updateDisabledMode()
    }

    private fun updateDisabledMode() {
        _node.setDisabled((config.notEditable && conf?.notEditable != false) || conf?.notEditable == true || readonly)
    }


    fun showValidation(value: String?) {
        _node.showValidation(value)
    }

    fun configure(config: TextBoxConfigurationJS) {
        this.conf = config
        updateDisabledMode()
    }

}


class TextBoxWidgetConfiguration:BaseWidgetConfiguration(){
    var showClearIcon = true
    var multiline = false
}