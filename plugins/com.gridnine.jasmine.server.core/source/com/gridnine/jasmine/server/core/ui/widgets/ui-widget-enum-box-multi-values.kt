/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.ui.widgets

import com.gridnine.jasmine.common.core.model.EnumSelectBoxConfiguration
import com.gridnine.jasmine.server.core.ui.common.BaseNodeWrapper
import com.gridnine.jasmine.server.core.ui.common.BaseWidgetConfiguration
import com.gridnine.jasmine.server.core.ui.components.Select
import com.gridnine.jasmine.server.core.ui.components.SelectDataType
import com.gridnine.jasmine.server.core.ui.components.UiLibraryAdapter
import kotlin.reflect.KClass

class EnumMultiValuesWidget<E:Enum<E>>(configure: EnumMultiValuesWidgetConfiguration<E>.()->Unit): BaseNodeWrapper<Select>(){

    private val config = EnumMultiValuesWidgetConfiguration<E>()

    init{
        config.configure()
        val comp = UiLibraryAdapter.get().createSelect{
            width = config.width
            height = config.height
            mode = SelectDataType.LOCAL
            editable = false
            multiple = true
        }
        comp.setPossibleValues(EnumBoxValueWidget.getPossibleValues(config.enumClassName
                ?: config.enumClass!!.qualifiedName!!))
        _node = comp
    }

    fun setValues(values:List<E>){
        _node.setValues(values.map { EnumBoxValueWidget.toSelectItem(it) })
    }

    fun getValues(): List<E>{
        return _node.getValues().map {
            EnumBoxValueWidget.toEnum(it, config.enumClassName ?: config.enumClass!!.qualifiedName!!)
        }
    }

    fun showValidation(value: String?) {
        _node.showValidation(value)
    }
    fun configure(config: EnumSelectBoxConfiguration) {
        _node.setEnabled(!config.notEditable)
    }

}

class EnumMultiValuesWidgetConfiguration<E:Enum<E>>:BaseWidgetConfiguration() {
    var enumClass: KClass<E>? = null
    var enumClassName:String? = null
}