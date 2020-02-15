/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.easyui.widgets

import com.gridnine.jasmine.web.core.model.ui.TextBoxWidget
import com.gridnine.jasmine.web.core.model.ui.TextboxDescriptionJS
import com.gridnine.jasmine.web.easyui.JQuery
import com.gridnine.jasmine.web.easyui.jQuery

class EasyUiTextBoxWidget(uid:String, description:TextboxDescriptionJS):TextBoxWidget(){
    val div: JQuery = jQuery("#${description.id}${uid}")
    private var initialized:Boolean = false
    private var ignoreOnChange = false
    init {

        configure = {_:Unit ->
            if(!initialized){
                div.textbox(object{
                    val editable = !description.notEditable
                    val onChange = {newValue:String?, oldValue:String? ->
                        if(!ignoreOnChange && valueChangeListener != null){
                            valueChangeListener!!.invoke(newValue, oldValue)
                        }
                    }
                })
                val tb = div.textbox("textbox").asDynamic()
                tb.on("input") {
                    val spanElm = tb.parent()
                    spanElm.css("border-color", "")
                    spanElm.removeAttr("title")
                }
                initialized = true
            }
        }
        setData = {
            ignoreOnChange = true
            div.textbox("setValue", it)
            ignoreOnChange = false
        }
        showValidation = {
            val tb =div.textbox("textbox").asDynamic()
            val spanElm = tb.parent()
            if (it != null) {
                spanElm.css("border-color", "#d9534f")
                spanElm.attr("title", it)
            } else if (!tb.hasClass("text-field-error") as Boolean) {
                spanElm.css("border-color", "")
                spanElm.removeAttr("title")
            }
            Unit
        }
        getData ={
            val value = div.textbox("getText") as String?
            if (value.isNullOrBlank()) null else value
        }
    }
}