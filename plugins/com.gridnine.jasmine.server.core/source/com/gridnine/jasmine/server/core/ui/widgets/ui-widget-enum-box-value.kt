/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.ui.widgets

import com.gridnine.jasmine.common.core.meta.DomainMetaRegistry
import com.gridnine.jasmine.common.core.meta.UiMetaRegistry
import com.gridnine.jasmine.common.core.model.EnumSelectBoxConfiguration
import com.gridnine.jasmine.common.core.model.SelectItem
import com.gridnine.jasmine.common.core.reflection.ReflectionFactory
import com.gridnine.jasmine.server.core.ui.common.BaseNodeWrapper
import com.gridnine.jasmine.server.core.ui.common.BaseWidgetConfiguration
import com.gridnine.jasmine.server.core.ui.components.Select
import com.gridnine.jasmine.server.core.ui.components.SelectDataType
import com.gridnine.jasmine.server.core.ui.components.UiLibraryAdapter
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
class EnumBoxValueWidget<E:Enum<E>>(configure: EnumBoxValueWidgetConfiguration<E>.()->Unit): BaseNodeWrapper<Select>(){

    private val config = EnumBoxValueWidgetConfiguration<E>()

    init{
        config.configure()
        val comp = UiLibraryAdapter.get().createSelect{
            width = config.width
            height = config.height
            mode = SelectDataType.LOCAL
            editable = false
            showClearIcon = config.allowNull
            multiple = false
        }
        comp.setPossibleValues(getPossibleValues(config.enumClassName?:config.enumClass!!.qualifiedName!!))
        _node = comp
    }

    fun setValue(value:E?){
        _node.setValues(if(value != null) arrayListOf(toSelectItem(value)) else emptyList())
    }

    fun setUncastedValue(value:Any?){
        _node.setValues(if(value != null) arrayListOf(toSelectItem(value as E)) else emptyList())
    }

    fun getValue(): E?{
        val values = _node.getValues()
        return if(values.isNotEmpty()) toEnum(values[0], config.enumClassName?:config.enumClass!!.qualifiedName!!)  as E else null
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
        fun getPossibleValues(enumClassName:String): List<SelectItem> {
            val result = arrayListOf<SelectItem>()
            DomainMetaRegistry.get().enums[enumClassName]?.items?.values?.forEach {
                result.add(SelectItem(it.id, it.getDisplayName()!!))
            }
            UiMetaRegistry.get().enums[enumClassName]?.items?.values?.forEach {
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

        fun<E:Enum<E>> toEnum(value:SelectItem, className:String): E {
            return ReflectionFactory.get().safeGetEnum(className, value.id) as E
        }
    }
}

class EnumBoxValueWidgetConfiguration<E:Enum<E>>:BaseWidgetConfiguration(){
    var allowNull = true
    var enumClass: KClass<E>? = null
    var enumClassName:String? = null
}