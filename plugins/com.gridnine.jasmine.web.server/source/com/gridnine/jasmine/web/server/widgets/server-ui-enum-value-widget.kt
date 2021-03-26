/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.widgets

import com.gridnine.jasmine.server.core.model.common.SelectItem
import com.gridnine.jasmine.server.core.model.domain.DomainMetaRegistry
import com.gridnine.jasmine.server.core.model.ui.EnumSelectBoxConfiguration
import com.gridnine.jasmine.server.core.model.ui.TextBoxConfiguration
import com.gridnine.jasmine.server.core.model.ui.UiMetaRegistry
import com.gridnine.jasmine.server.core.reflection.ReflectionFactory
import com.gridnine.jasmine.web.server.components.*
import kotlin.reflect.KClass

class ServerUiEnumValueWidget<E:Enum<E>>(private val config:ServerUiEnumValueWidgetConfiguration<E>): BaseServerUiNodeWrapper<ServerUiSelect>(){

    init{
        val comp = ServerUiLibraryAdapter.get().createSelect(ServerUiSelectConfiguration{
            width = config.width
            height = config.height
            mode = ServerUiSelectDataType.LOCAL
            editable = false
            showClearIcon = config.allowNull
            multiple = false
        })
        comp.setPossibleValues(getPossibleValues(config.enumClass))
        _node = comp
    }

    fun setValue(value:E?){
        _node.setValues(if(value != null) arrayListOf(toSelectItem(value)) else emptyList())
    }

    fun getValue(): E?{
        val values = _node.getValues()
        return if(values.isNotEmpty()) toEnum(values[0], config.enumClass) else null
    }

    fun setReadonly(value:Boolean){
        _node.setEnabled(!value)
    }


    fun showValidation(value: String?) {
        _node.showValidation(value)
    }
    fun configure(config: EnumSelectBoxConfiguration?) {
        _node.setEnabled(config?.notEditable != false)
    }

    companion object{
        fun<E:Enum<E>> getPossibleValues(enumClass: KClass<E>): List<SelectItem> {
            val result = arrayListOf<SelectItem>()
            DomainMetaRegistry.get().enums[enumClass.java.name]?.items?.values?.forEach {
                result.add(SelectItem(it.id, it.getDisplayName()!!))
            }
            UiMetaRegistry.get().enums[enumClass.java.name]?.items?.values?.forEach {
                result.add(SelectItem(it.id, it.getDisplayName()!!))
            }
            result.sortBy { it.text }
            return result
        }
        fun<E:Enum<E>> getDisplayName(enumClass: KClass<E>, item:String): String {
            val displayName = DomainMetaRegistry.get().enums[enumClass.java.name]?.items?.values?.find { it.id == item }?.getDisplayName()
            if(displayName != null){
                return displayName
            }
            return UiMetaRegistry.get().enums[enumClass.java.name]?.items?.values?.find { it.id == item }!!.getDisplayName()!!
        }

        fun<E:Enum<E>> toSelectItem(value:E): SelectItem {
            return SelectItem(value.name, getDisplayName(value::class, value.name))
        }

        fun<E:Enum<E>> toEnum(value:SelectItem, cls:KClass<E>): E {
            return cls.java.enumConstants.find { it.name == value.id }!!
        }
    }
}

class ServerUiEnumValueWidgetConfiguration<E:Enum<E>>(){
    constructor(config:ServerUiEnumValueWidgetConfiguration<E>.()->Unit):this(){
        config.invoke(this)
    }
    var width:String? = null
    var height:String? = null
    var allowNull = true
    lateinit var enumClass: KClass<E>
}