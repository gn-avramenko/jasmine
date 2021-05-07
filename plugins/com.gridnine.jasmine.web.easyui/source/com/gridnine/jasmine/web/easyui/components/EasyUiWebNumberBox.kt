/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("unused")

package com.gridnine.jasmine.web.easyui.components

import com.gridnine.jasmine.web.core.ui.components.*
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS

class EasyUiWebNumberBox(configure: WebNumberBoxConfiguration.()->Unit) :WebNumberBox,EasyUiComponent{

    private var initialized = false

    private val uid = MiscUtilsJS.createUUID()

    private var jq:dynamic = null
    private var enabled = true
    private var validationMessage:String? = null

    private var storedValue:Double? = null

    private val config = WebNumberBoxConfiguration()
    init {
        config.configure()
    }



    override fun getHtml(): String {
        return "<input id=\"numberBox${uid}\" style=\"${getSizeAttributes(config)}\"/>"
    }

    override fun getValue(): Double? {
        if(!initialized){
            return storedValue
        }
        val value = jq.numberbox("getText") as String?
        return if (value.isNullOrBlank()) null else value.toDouble()
    }

    override fun setValue(value: Double?){
        if(!initialized){
            this.storedValue = value
            return
        }
        jq.numberbox("setValue", value)
    }

    override fun setEnabled(value: Boolean) {
        if(value != enabled){
            enabled = value
            if(initialized){
                if(enabled){
                    jq.numberbox("enable")
                } else{
                    jq.numberbox("disable")
                }
                if(config.showClearIcon ){
                    jq.numberbox("getIcon",0).css("visibility", if(storedValue == null  || !enabled) "hidden" else "visible")
                }
            }
        }

    }

    override fun showValidation(value: String?) {
        validationMessage = value
        if(initialized){
            showValidationInternal(value)
        }
    }


    override fun decorate() {
        jq = jQuery("#numberBox$uid")
        val icons = arrayListOf<Any>()
        if(config.showClearIcon){
            icons.add(object{
                val iconCls = "icon-clear"
                val handler = {_:dynamic ->
                    jq.numberbox("setValue", null)
                    jq.numberbox("getIcon",0).css("visibility","hidden")
                }
            })
        }
        jq.numberbox(object{
            val precision = config.precision
            val icons = icons.toTypedArray()
            val onChange = {newValue:Double?,_:Double? ->
                jq.numberbox("getIcon",0).css("visibility",if(newValue == null) "hidden" else "visible")
                storedValue = newValue
            }
            val disabled = !enabled
            val value = storedValue
        })
        val tb = jq.numberbox("textbox")
        tb.on("input") {
            if(config.showClearIcon){
                val text = jq.numberbox("getText") as String?
                jq.numberbox("getIcon",0).css("visibility",if(text?.isNotBlank() == false) "hidden" else "visible")
            }
        }
        if(config.showClearIcon && (storedValue == null  || !enabled)){
            jq.numberbox("getIcon",0).css("visibility","hidden")
        }
        showValidationInternal(validationMessage)
        initialized = true
    }

    private fun showValidationInternal(validationMessage: String?) {
        if(validationMessage != null){
            val tb =jq.numberbox("textbox")
            val spanElm = tb.parent()
            spanElm.css("border-color", "#d9534f")
            spanElm.attr("title", validationMessage)
            return
        }
        val tb =jq.numberbox("textbox")
        val spanElm = tb.parent()
        spanElm.css("border-color", "")
        spanElm.removeAttr("title")
    }

    override fun destroy() {
       if(initialized){
           jq.numberbox("destroy")
       }
    }

}