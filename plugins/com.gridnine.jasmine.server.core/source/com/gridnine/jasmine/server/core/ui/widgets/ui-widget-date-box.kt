/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.ui.widgets

import com.gridnine.jasmine.common.core.model.DateBoxConfiguration
import com.gridnine.jasmine.server.core.ui.common.BaseNodeWrapper
import com.gridnine.jasmine.server.core.ui.common.BaseWidgetConfiguration
import com.gridnine.jasmine.server.core.ui.components.DateBox
import com.gridnine.jasmine.server.core.ui.components.UiLibraryAdapter
import java.time.LocalDate

class DateBoxWidget(configure: DateBoxWidgetConfiguration.()->Unit): BaseNodeWrapper<DateBox>(){


    init{
        val config = DateBoxWidgetConfiguration()
        config.configure()
        _node = UiLibraryAdapter.get().createDateBox{
            width = config.width
            height = config.height
        }
    }

    fun setValue(value:LocalDate?) = _node.setValue(value)

    fun getValue() = _node.getValue()

    fun showValidation(value: String?) {
        _node.showValidation(value)
    }

    fun setReadonly(value:Boolean){
        _node.setEnabled(!value)
    }
    fun configure(config: DateBoxConfiguration?) {
        _node.setEnabled(config?.notEditable != false)
    }
}

class DateBoxWidgetConfiguration:BaseWidgetConfiguration()