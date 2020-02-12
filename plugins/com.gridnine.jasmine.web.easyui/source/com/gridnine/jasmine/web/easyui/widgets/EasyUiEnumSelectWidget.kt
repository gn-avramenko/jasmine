/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.easyui.widgets

import com.gridnine.jasmine.web.core.model.ui.EnumSelectConfigurationJS
import com.gridnine.jasmine.web.core.model.ui.EnumSelectDescriptionJS
import com.gridnine.jasmine.web.core.model.ui.EnumSelectWidget
import com.gridnine.jasmine.web.core.model.ui.SelectItemJS
import com.gridnine.jasmine.web.core.utils.ReflectionFactoryJS
import com.gridnine.jasmine.web.core.utils.UiUtilsJS
import com.gridnine.jasmine.web.easyui.JQuery
import com.gridnine.jasmine.web.easyui.jQuery

@Suppress("UnsafeCastFromDynamic")
class EasyUiEnumSelectWidget<E:Enum<E>>(uid:String, description:EnumSelectDescriptionJS):EnumSelectWidget<E>(){
    private val div: JQuery = jQuery("#${description.id}${uid}")
    private val selectItems = arrayListOf<SelectItemJS?>()
    private var initialized  = false
    var spanElm:dynamic = null
    init {

        configure = {_:EnumSelectConfigurationJS<E> ->
            if (!initialized) {
                selectItems.clear()
                selectItems.addAll(UiUtilsJS.getEnumValues(description.enumId+"JS"))
                selectItems.add(0, SelectItemJS(null, null))
                val options = object {
                    val valueField = "id"
                    val textField = "caption"
                    val editable = false
                    val limitToList = true
                    val hasDownArrow =  true
                    val data = selectItems.toTypedArray()
                    val onChange = { newValue: String, _: String? ->
                        div.combobox("getIcon",0).asDynamic().css("visibility",if(newValue.isEmpty()) "hidden" else "visible")
                        if (spanElm != null) {
                            spanElm.css("border-color", "")
                            spanElm.removeAttr("title")
                        }
                    }
                    val icons = arrayOf(object{
                        val iconCls = "icon-clear"
                        val handler = {_:dynamic ->
                            div.combobox("setValues", arrayOfNulls<String>(0))
                        }
                    })
                }
                div.combobox(options)
                div.combobox("getIcon",0).asDynamic().css("visibility", "hidden")
                spanElm = div.combobox("textbox").asDynamic().parent()
            }
        }
        setData = { value ->  
            if(value == null){
                div.combobox("setValue", null)
                div.combobox("setText", null)
            } else{
                div.combobox("setValues", arrayOf(value.name))
            }
        }
        showValidation = {
            val spanElm = div.combobox("textbox").asDynamic().parent()
            if (it == null) {
                spanElm.css("border-color", "")
                spanElm.removeAttr("title")
            } else if (!spanElm.hasClass("text-field-error")) {
                spanElm.css("border-color", "#d9534f")
                spanElm.attr("title", it)
            }
        }
        getData ={
            val values = div.combobox("getValues") as Array<String>
            if(values.isEmpty()) null else ReflectionFactoryJS.get().getEnum<E>(description.enumId, values[0]) 
        }
    }

    private fun findValue(newValue: String?): SelectItemJS? {
        return newValue?.let{selectItems.find { item -> item?.id == newValue }}
    }
}