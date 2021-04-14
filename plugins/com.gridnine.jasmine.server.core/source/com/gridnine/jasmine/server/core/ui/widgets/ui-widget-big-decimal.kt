/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.ui.widgets

import com.gridnine.jasmine.common.core.model.BigDecimalBoxConfiguration
import com.gridnine.jasmine.server.core.ui.common.BaseNodeWrapper
import com.gridnine.jasmine.server.core.ui.common.BaseWidgetConfiguration
import com.gridnine.jasmine.server.core.ui.components.NumberBox
import com.gridnine.jasmine.server.core.ui.components.UiLibraryAdapter
import java.math.BigDecimal

class BigDecimalBoxWidget(configure: BigDecimalBoxWidgetConfiguration.()->Unit): BaseNodeWrapper<NumberBox>(){

    init{
        val config = BigDecimalBoxWidgetConfiguration()
        config.configure()
        _node = UiLibraryAdapter.get().createNumberBox{
            width = config.width
            height = config.height
            precision= config.precision
        }
    }

    fun setValue(value:BigDecimal?) = _node.setValue(value)

    fun getValue() = _node.getValue()

    fun showValidation(value: String?) {
        _node.showValidation(value)
    }

    fun setReadonly(value:Boolean){
        _node.setEnabled(!value)
    }
    fun configure(config: BigDecimalBoxConfiguration?) {
        _node.setEnabled(config?.notEditable != false)
    }
}

class BigDecimalBoxWidgetConfiguration:BaseWidgetConfiguration(){
    var precision = 2
}