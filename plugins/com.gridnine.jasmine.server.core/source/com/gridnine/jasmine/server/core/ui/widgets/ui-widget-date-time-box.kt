/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.ui.widgets

import com.gridnine.jasmine.common.core.model.DateTimeBoxConfiguration
import com.gridnine.jasmine.server.core.ui.common.BaseNodeWrapper
import com.gridnine.jasmine.server.core.ui.common.BaseWidgetConfiguration
import com.gridnine.jasmine.server.core.ui.components.DateTimeBox
import com.gridnine.jasmine.server.core.ui.components.UiLibraryAdapter
import java.time.LocalDateTime

class DateTimeBoxWidget(configure: DateTimeBoxWidgetConfiguration.()->Unit): BaseNodeWrapper<DateTimeBox>(){

    init{
        val config = DateTimeBoxWidgetConfiguration()
        config.configure()
        _node = UiLibraryAdapter.get().createDateTimeBox{
            width = config.width
            height = config.height
        }
    }

    fun setValue(value:LocalDateTime?) = _node.setValue(value)

    fun getValue() = _node.getValue()

    fun showValidation(value: String?) {
        _node.showValidation(value)
    }

    fun setReadonly(value:Boolean){
        _node.setEnabled(!value)
    }

    fun configure(config: DateTimeBoxConfiguration?) {
        _node.setEnabled(config?.notEditable != false)
    }
}

class DateTimeBoxWidgetConfiguration:BaseWidgetConfiguration()