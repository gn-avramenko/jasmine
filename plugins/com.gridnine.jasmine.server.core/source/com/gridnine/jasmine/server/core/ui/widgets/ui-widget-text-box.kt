/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.ui.widgets

import com.gridnine.jasmine.common.core.model.TextBoxConfiguration
import com.gridnine.jasmine.server.core.ui.common.BaseNodeWrapper
import com.gridnine.jasmine.server.core.ui.common.BaseWidgetConfiguration
import com.gridnine.jasmine.server.core.ui.components.TextBox
import com.gridnine.jasmine.server.core.ui.components.UiLibraryAdapter


class TextBoxWidget(configure: TextBoxWidgetConfiguration.()->Unit): BaseNodeWrapper<TextBox>(){

    private var readonly = false

    private var tbConfig:TextBoxConfiguration? = null

    init {
        val config = TextBoxWidgetConfiguration()
        config.configure()
        _node = UiLibraryAdapter.get().createTextBox{
            width = config.width
            height = config.height
        }
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

class TextBoxWidgetConfiguration:BaseWidgetConfiguration()