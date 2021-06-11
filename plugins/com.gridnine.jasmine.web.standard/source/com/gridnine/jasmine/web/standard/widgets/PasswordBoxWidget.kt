/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("unused")

package com.gridnine.jasmine.web.standard.widgets

import com.gridnine.jasmine.common.core.model.PasswordBoxConfigurationJS
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.components.BaseWebNodeWrapper
import com.gridnine.jasmine.web.core.ui.components.WebPasswordBox

class PasswordBoxWidget(configure:PasswordBoxWidgetConfiguration.()->Unit):BaseWebNodeWrapper<WebPasswordBox>(){

    private val config = PasswordBoxWidgetConfiguration()

    private var conf: PasswordBoxConfigurationJS? = null

    private var readonly = false
    init {
        config.configure()
        _node = WebUiLibraryAdapter.get().createPasswordBox {
            width = config.width
            height = config.height
        }
    }

    fun setValue(value:String?) = _node.setValue(value)

    fun getValue() = _node.getValue()

    fun setReadonly(value:Boolean) {
        readonly = value
        updateDisabledMode()
    }

    private fun updateDisabledMode() {
        _node.setDisabled(conf?.notEditable == true || readonly)
    }


    fun showValidation(value: String?) {
        _node.showValidation(value)
    }

    fun configure(config: PasswordBoxConfigurationJS) {
        this.conf = config
        updateDisabledMode()
    }

}


class PasswordBoxWidgetConfiguration:BaseWidgetConfiguration()