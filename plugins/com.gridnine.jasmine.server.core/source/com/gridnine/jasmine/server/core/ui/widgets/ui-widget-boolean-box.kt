/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.ui.widgets

import com.gridnine.jasmine.common.core.model.BooleanBoxConfiguration
import com.gridnine.jasmine.server.core.ui.common.BaseNodeWrapper
import com.gridnine.jasmine.server.core.ui.common.BaseWidgetConfiguration
import com.gridnine.jasmine.server.core.ui.components.BooleanBox
import com.gridnine.jasmine.server.core.ui.components.UiLibraryAdapter

class BooleanBoxWidget(configure: BooleanBoxWidgetConfiguration.()->Unit): BaseNodeWrapper<BooleanBox>(){

    init{
        val config = BooleanBoxWidgetConfiguration()
        config.configure()
        _node = UiLibraryAdapter.get().createBooleanBox{
            width = config.width
            height = config.height
        }
    }

    fun setValue(value:Boolean) = _node.setValue(value)

    fun getValue() = _node.getValue()

    fun showValidation(value: String?) {
        //noops
    }

    fun setReadonly(value:Boolean){
        _node.setEnabled(!value)
    }

    fun configure(config: BooleanBoxConfiguration?) {
        _node.setEnabled(config?.notEditable != false)
    }
}

class BooleanBoxWidgetConfiguration:BaseWidgetConfiguration()