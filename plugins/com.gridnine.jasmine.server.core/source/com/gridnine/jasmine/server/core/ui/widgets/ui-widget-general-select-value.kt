/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.ui.widgets

import com.gridnine.jasmine.common.core.model.GeneralSelectBoxConfiguration
import com.gridnine.jasmine.common.core.model.SelectItem
import com.gridnine.jasmine.server.core.ui.common.BaseNodeWrapper
import com.gridnine.jasmine.server.core.ui.common.BaseWidgetConfiguration
import com.gridnine.jasmine.server.core.ui.components.Select
import com.gridnine.jasmine.server.core.ui.components.SelectDataType
import com.gridnine.jasmine.server.core.ui.components.UiLibraryAdapter

class GeneralSelectBoxValueWidget(configure: GeneralSelectBoxValueWidgetConfiguration.()->Unit):BaseNodeWrapper<Select>(){


    init {
        val config = GeneralSelectBoxValueWidgetConfiguration()
        config.configure()
        _node = UiLibraryAdapter.get().createSelect{
            width = config.width
            height = config.height
            mode = SelectDataType.LOCAL
            editable = false
            multiple = false
            showClearIcon = config.showClearIcon
            showAllPossibleValues = config.showAllPossibleValues
        }
    }
    fun setChangeListener(value: ((SelectItem?) ->Unit)?){
        _node.setChangeListener {values ->
            value?.invoke(if(values.isNotEmpty()) values[0] else null)
        }
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

class GeneralSelectBoxValueWidgetConfiguration:BaseWidgetConfiguration(){
    var showClearIcon = true
    var showAllPossibleValues = false
}