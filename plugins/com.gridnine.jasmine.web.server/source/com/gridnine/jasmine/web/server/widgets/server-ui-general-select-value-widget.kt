/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.widgets

import com.gridnine.jasmine.server.core.model.common.SelectItem
import com.gridnine.jasmine.server.core.model.ui.GeneralSelectBoxConfiguration
import com.gridnine.jasmine.web.server.components.*

class ServerUiGeneralSelectValueWidget(private val config:ServerUiGeneralSelectValueWidgetConfiguration):BaseServerUiNodeWrapper<ServerUiSelect>(){
    var changeListener:((SelectItem?) ->Unit)? = null
    set(value) = _node.setChangeListener {values ->
        value?.let {
        it.invoke(if(values.isNotEmpty()) values[0] else null)
    } }
    init {
        _node = ServerUiLibraryAdapter.get().createSelect(ServerUiSelectConfiguration{
            width = config.width
            height = config.height
            mode = ServerUiSelectDataType.LOCAL
            editable = false
            multiple = false
            showClearIcon = config.showClearIcon
        })
    }

    fun setPossibleValues(values:List<SelectItem>){
        _node.setPossibleValues(values)
    }

    fun getValue(): SelectItem? {
        val values = _node.getValues()
        return if(values.isEmpty()) null else values[0]
    }

    fun setValue(value: SelectItem?) {
        val values = arrayListOf<SelectItem>()
        if(value != null){
            values.add(value)
        }
        _node.setValues(values)
    }


    fun setReadonly(value:Boolean) {
        _node.setEnabled(!value)
    }

    fun configure(config: GeneralSelectBoxConfiguration?){
        config?.let {
            _node.setEnabled(!config.notEditable)
            _node.setPossibleValues(config.possibleValues)
        }
    }

    fun showValidation(value:String?){
        _node.showValidation(value)
    }

}

class ServerUiGeneralSelectValueWidgetConfiguration(){
    constructor(config:ServerUiGeneralSelectValueWidgetConfiguration.()->Unit):this(){
        config.invoke(this)
    }
    var width:String? = null
    var height:String? = null
    var showClearIcon = true
}