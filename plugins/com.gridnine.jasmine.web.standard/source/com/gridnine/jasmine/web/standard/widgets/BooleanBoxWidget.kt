/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("UNUSED_PARAMETER")

package com.gridnine.jasmine.web.standard.widgets

import com.gridnine.jasmine.common.core.model.BooleanBoxConfigurationJS
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.components.BaseWebNodeWrapper
import com.gridnine.jasmine.web.core.ui.components.WebBooleanBox
import com.gridnine.jasmine.web.standard.WebMessages

class BooleanBoxWidget(configure:BooleanBoxWidgetConfiguration.()->Unit):BaseWebNodeWrapper<WebBooleanBox>(){
    private val conf = BooleanBoxWidgetConfiguration()
    init {
        conf.configure()
        _node = WebUiLibraryAdapter.get().createBooleanBox {
            width = conf.width
            height = conf.height
            offText = WebMessages.NO
            onText = WebMessages.YES
        }
    }
    fun setValue(value:Boolean) = _node.setValue(value)

    fun getValue() = _node.getValue()



    fun setReadonly(value:Boolean) {
        _node.setEnabled(!value)
    }

    fun configure(config:BooleanBoxConfigurationJS?){
        config?.let {
            _node.setEnabled(!config.notEditable)
        }
    }

    fun showValidation(value:String?){
        //noops
    }
}


class BooleanBoxWidgetConfiguration{
    var width:String? = null
    var height:String? = null
    var notEditable = false
}