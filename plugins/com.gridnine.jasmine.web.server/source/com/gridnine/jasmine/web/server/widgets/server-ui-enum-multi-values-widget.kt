/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.widgets

import com.gridnine.jasmine.server.core.model.ui.EnumSelectBoxConfiguration
import com.gridnine.jasmine.web.server.components.*
import kotlin.reflect.KClass

class ServerUiEnumMultiValuesWidget<E:Enum<E>>(private val config:ServerUiEnumMultiValuesWidgetConfiguration<E>): BaseServerUiNodeWrapper<ServerUiSelect>(){

    init{
        val comp = ServerUiLibraryAdapter.get().createSelect(ServerUiSelectConfiguration{
            width = config.width
            height = config.height
            mode = ServerUiSelectDataType.LOCAL
            editable = false
            multiple = true
        })
        comp.setPossibleValues(ServerUiEnumValueWidget.getPossibleValues(config.enumClass))
        _node = comp
    }

    fun setValues(values:List<E>){
        _node.setValues(values.map { ServerUiEnumValueWidget.toSelectItem(it)})
    }

    fun getValues(): List<E>{
        return _node.getValues().map { ServerUiEnumValueWidget.toEnum(it, config.enumClass) }
    }

    fun showValidation(value: String?) {
        _node.showValidation(value)
    }
    fun configure(config: EnumSelectBoxConfiguration) {
        _node.setEnabled(!config.notEditable)
    }

}

class ServerUiEnumMultiValuesWidgetConfiguration<E:Enum<E>>(){
    constructor(config:ServerUiEnumMultiValuesWidgetConfiguration<E>.()->Unit):this(){
        config.invoke(this)
    }
    var width:String? = null
    var height:String? = null
    lateinit var enumClass: KClass<E>
}