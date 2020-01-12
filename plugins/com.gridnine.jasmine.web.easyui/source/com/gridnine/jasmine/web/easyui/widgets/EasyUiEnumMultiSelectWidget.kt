/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.easyui.widgets

import com.gridnine.jasmine.web.core.model.common.FakeEnumJS
import com.gridnine.jasmine.web.core.model.ui.*
import com.gridnine.jasmine.web.core.utils.ReflectionFactoryJS
import com.gridnine.jasmine.web.core.utils.UiUtilsJS
import com.gridnine.jasmine.web.easyui.JQuery
import com.gridnine.jasmine.web.easyui.jQuery

@Suppress("UnsafeCastFromDynamic")
class EasyUiEnumMultiSelectWidget<E:Enum<E>>(uid:String, description:EnumSelectDescriptionJS):EnumMultiSelectWidget<E>(){
    private val div: JQuery = jQuery("#${description.id}${uid}")
    private val selectItems = arrayListOf<SelectItemJS?>()
    private var initialized  = false
    var spanElm:dynamic = null
    init {

        configure = {settings:EnumSelectConfigurationJS<E> ->
            if (!initialized) {
                selectItems.clear()
                selectItems.addAll(UiUtilsJS.getEnumValues(description.enumId))
                if (settings.nullAllowed) {
                    selectItems.add(0, null)
                }
                val options = object {
                    val valueField = "id"
                    val textField = "caption"
                    val editable = false
                    val limitToList = true
                    val hasDownArrow =  true
                    val data = selectItems.toTypedArray()
                    val onChange = { _: String?, _: String? ->
                        if (spanElm != null) {
                            spanElm.css("border-color", "")
                            spanElm.removeAttr("title")
                        }
                    }
                }
                div.tagbox(options)
                spanElm = div.tagbox("textbox").asDynamic().parent()
            }
        }
        readData = { values ->
            if(values.isEmpty()){
                div.tagbox("setValue", null)
                div.tagbox("setText", null)
            } else{
                div.tagbox("setValues", values.map { it.name}.toTypedArray())
            }
        }
        showValidation = {
            val spanElm = div.tagbox("textbox").asDynamic().parent()
            if (it == null) {
                spanElm.css("border-color", "")
                spanElm.removeAttr("title")
            } else if (!spanElm.hasClass("text-field-error")) {
                spanElm.css("border-color", "#d9534f")
                spanElm.attr("title", it)
            }
        }
        writeData ={data ->
            val values = div.tagbox("getValues") as Array<String>
            data.clear()
            values.map { ReflectionFactoryJS.get().getEnum<FakeEnumJS>(description.enumId, it)  }.forEach { data.add(it as E) }

        }
    }

    private fun findValue(newValue: String?): SelectItemJS? {
        return newValue?.let{selectItems.find { item -> item?.id == newValue }}
    }
}