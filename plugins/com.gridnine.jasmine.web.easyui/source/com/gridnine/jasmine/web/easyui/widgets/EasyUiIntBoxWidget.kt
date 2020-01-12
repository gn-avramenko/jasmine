/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.easyui.widgets

import com.gridnine.jasmine.web.core.model.ui.IntegerBoxDescriptionJS
import com.gridnine.jasmine.web.core.model.ui.IntegerBoxWidget
import com.gridnine.jasmine.web.easyui.JQuery
import com.gridnine.jasmine.web.easyui.jQuery

class EasyUiIntBoxWidget(uid:String, description:IntegerBoxDescriptionJS):IntegerBoxWidget(){
    val div: JQuery = jQuery("#${description.id}${uid}")
    private var initialized:Boolean = false
    init {

        configure = {_:Unit ->
            if(!initialized){
                div.numberbox(object{
                    val precision = 0
                })
                val tb = div.numberbox("textbox").asDynamic()
                tb.on("input") {
                    val spanElm = tb.parent()
                    spanElm.css("border-color", "")
                    spanElm.removeAttr("title")
                }
                initialized = true
            }
        }
        setData = {
            div.numberbox("setValue", it)
        }
        showValidation = {
            val tb =div.numberbox("textbox").asDynamic()
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
            val value = div.numberbox("getText") as String?
            if (value.isNullOrBlank()) null else value.toDouble().toInt()
        }
    }
}