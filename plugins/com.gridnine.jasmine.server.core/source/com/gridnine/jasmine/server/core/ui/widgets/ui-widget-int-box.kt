/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.ui.widgets

import com.gridnine.jasmine.common.core.model.IntegerNumberBoxConfiguration
import com.gridnine.jasmine.server.core.ui.common.BaseNodeWrapper
import com.gridnine.jasmine.server.core.ui.common.BaseWidgetConfiguration
import com.gridnine.jasmine.server.core.ui.components.NumberBox
import com.gridnine.jasmine.server.core.ui.components.UiLibraryAdapter
import java.math.BigDecimal

class IntBoxWidget(configure: IntBoxWidgetConfiguration.()->Unit): BaseNodeWrapper<NumberBox>(){
    private val config = IntBoxWidgetConfiguration()

    init{
        config.configure()
        _node = UiLibraryAdapter.get().createNumberBox{
            width = config.width
            height = config.height
            precision= 0
        }
    }

    fun setValue(value:Int?) = _node.setValue(value?.let{BigDecimal.valueOf(it.toLong())})

    fun getValue() = _node.getValue()?.toInt() ?:(if(config.nullable) null else 0)

    fun showValidation(value: String?) {
        _node.showValidation(value)
    }

    fun setReadonly(value:Boolean){
        _node.setEnabled(!value)
    }

    fun configure(config: IntegerNumberBoxConfiguration?) {
        _node.setEnabled(config?.notEditable != false)
    }
}

class IntBoxWidgetConfiguration:BaseWidgetConfiguration(){
    var nullable = true
}