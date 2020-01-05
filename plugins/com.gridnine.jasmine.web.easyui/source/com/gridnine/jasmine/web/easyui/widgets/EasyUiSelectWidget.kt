/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.easyui.widgets

import com.gridnine.jasmine.web.core.model.ui.*
import com.gridnine.jasmine.web.easyui.JQuery
import com.gridnine.jasmine.web.easyui.jQuery

@Suppress("UnsafeCastFromDynamic")
class EasyUiSelectWidget(uid:String, description:SelectDescriptionJS):SelectWidget(){
    val div: JQuery = jQuery("#${description.id}${uid}")
    private var initialized:Boolean = false
    val selectItems = arrayListOf<SelectItemJS?>()
    var spanElm:dynamic = null
    init {

        configure = {settings:SelectConfigurationJS ->
            if(!initialized){
                selectItems.clear()
                selectItems.addAll(settings.possibleValues)
                selectItems.sortBy { it?.caption }
                if(settings.nullAllowed){
                    selectItems.add(0,null)
                }
                initialized = true
                val options = object {
                    val valueField = "id"
                    val textField = "caption"
                    val editable = false
                    val limitToList = true
                    val data = selectItems.toTypedArray()
                    val onChange = { _: String?, _: String? ->
                        if(spanElm != null) {
                            spanElm.css("border-color", "")
                            spanElm.removeAttr("title")
                        }
                    }
                }
                div.combobox(options)
                spanElm = div.combobox("textbox").asDynamic().parent()
            }
        }
        setData = {
            if(it == null){
                div.combobox("setValue", null)
                div.combobox("setText", null)
            } else{
                div.combobox("setValue", it.id)
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
            val value = div.combobox("getValue") as String?
            val text =  div.combobox("getText") as String?
            value?.let{SelectItemJS(value, text)}
        }
    }
}