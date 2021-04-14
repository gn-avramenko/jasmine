/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.ui.widgets

import com.gridnine.jasmine.common.core.model.PasswordBoxConfiguration
import com.gridnine.jasmine.server.core.ui.common.BaseNodeWrapper
import com.gridnine.jasmine.server.core.ui.common.BaseWidgetConfiguration
import com.gridnine.jasmine.server.core.ui.components.PasswordBox
import com.gridnine.jasmine.server.core.ui.components.UiLibraryAdapter

class PasswordBoxWidget(configure: PasswordBoxWidgetConfiguration.()->Unit): BaseNodeWrapper<PasswordBox>(){

    init{
        val config = PasswordBoxWidgetConfiguration()
        config.configure()
        val comp = UiLibraryAdapter.get().createPasswordBox{
            width = config.width
            height = config.height
        }
        _node = comp
    }

    fun setValue(value:String?) = _node.setValue(value)

    fun getValue() = _node.getValue()

    fun showValidation(value: String?) {
        _node.showValidation(value)
    }

    fun setReadonly(value:Boolean){
        _node.setDisabled(value)
    }
    fun configure(config: PasswordBoxConfiguration?) {
        _node.setDisabled(config?.notEditable?:false)
    }
}

class PasswordBoxWidgetConfiguration:BaseWidgetConfiguration()