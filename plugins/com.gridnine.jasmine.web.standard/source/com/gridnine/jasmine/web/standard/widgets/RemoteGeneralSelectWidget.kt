/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("unused")

package com.gridnine.jasmine.web.standard.widgets

import com.gridnine.jasmine.common.core.model.GeneralSelectBoxConfigurationJS
import com.gridnine.jasmine.common.core.model.SelectItemJS
import com.gridnine.jasmine.web.core.ui.components.SelectDataType
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.components.BaseWebNodeWrapper
import com.gridnine.jasmine.web.core.ui.components.WebSelect

class RemoteGeneralSelectWidget(configure:RemoteGeneralSelectWidgetConfiguration.()->Unit):BaseWebNodeWrapper<WebSelect>(){
    private var changeListener:(suspend (SelectItemJS?) ->Unit)? = null
    private lateinit var provider: suspend (String)->List<SelectItemJS>
    private val config = RemoteGeneralSelectWidgetConfiguration()
    init {
        config.configure()
        _node = WebUiLibraryAdapter.get().createSelect{
            width = config.width
            height = config.height
            mode = SelectDataType.REMOTE
            editable = false
            multiple = false
            showClearIcon = config.showClearIcon
        }
    }

    fun setChangeListener(listener: suspend (SelectItemJS?) ->Unit){
        _node.setChangeListener {values ->
            listener.invoke(if(values.isNotEmpty()) values[0] else null)
        }
    }

    fun setProvider(provider:suspend (String)->List<SelectItemJS> ){
        this.provider = provider
        _node.setLoader {
            provider.invoke(it)
        }
    }

    fun getValue(): SelectItemJS? {
        val values = _node.getValues()
        return if(values.isEmpty()) null else values[0]
    }

    fun setValue(value: SelectItemJS?) {
        val values = arrayListOf<SelectItemJS>()
        if(value != null){
            values.add(value)
        }
        _node.setValues(values)
    }


    fun configure(config: GeneralSelectBoxConfigurationJS?){
        config?.let {
            _node.setEnabled(!config.notEditable)
        }
    }

    fun setReadonly(value:Boolean){
        _node.setEnabled(!value)
    }
    fun showValidation(value:String?){
        _node.showValidation(value)
    }
}

class RemoteGeneralSelectWidgetConfiguration:BaseWidgetConfiguration(){
    var showClearIcon = true
}