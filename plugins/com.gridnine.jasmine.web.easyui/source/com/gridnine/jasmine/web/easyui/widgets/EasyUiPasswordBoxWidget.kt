/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.easyui.widgets

import com.gridnine.jasmine.web.core.model.ui.PasswordBoxDescriptionJS
import com.gridnine.jasmine.web.core.model.ui.PasswordBoxWidget
import com.gridnine.jasmine.web.easyui.JQuery
import com.gridnine.jasmine.web.easyui.jQuery

class EasyUiPasswordBoxWidget(uid:String, description:PasswordBoxDescriptionJS):PasswordBoxWidget(){
    val div: JQuery = jQuery("#${description.id}${uid}")
    private var initialized:Boolean = false
    init {

        configure = {_:Unit ->
            if(!initialized){
                div.passwordbox(object{})
                val tb = div.passwordbox("textbox").asDynamic()
                tb.on("input") {
                    val spanElm = tb.parent()
                    spanElm.css("border-color", "")
                    spanElm.removeAttr("title")
                }
                initialized = true
            }
        }
        setData = {
            div.passwordbox("setValue", it)
        }
        showValidation = {
            val tb =div.passwordbox("textbox").asDynamic()
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
            val value = div.passwordbox("getValue") as String?
            if (value.isNullOrBlank()) null else value
        }
    }
}