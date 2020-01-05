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
    init {
        setData = {
            div.textbox("setValue", it)
        }
        configure = {_:Unit ->
            if(!initialized){
                div.textbox(object{})
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
            div.textbox("setValue", it)
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