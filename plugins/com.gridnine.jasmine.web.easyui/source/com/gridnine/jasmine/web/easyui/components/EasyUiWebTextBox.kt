/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.easyui.components

import com.gridnine.jasmine.web.core.ui.components.*
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS

@Suppress("unused")
class EasyUiWebTextBox(configure: WebTextBoxConfiguration.()->Unit) :WebTextBox,EasyUiComponent{

    private var initialized = false

    private val uid = MiscUtilsJS.createUUID()

    private var jq:dynamic = null

    private var value:String? = null

    private var disabled = false

    private val config = WebTextBoxConfiguration()
    init {
        config.configure()
    }

    override fun getId(): String {
        return "textBox${uid}"
    }
    override fun getHtml(): String {
        return "<input id=\"textBox${uid}\" style=\"${getSizeAttributes(config)}\"/>"
    }

    override fun getValue(): String? {
        if(!initialized){
            return value
        }
        return jq.textbox("getText")
    }

    override fun setValue(value: String?){
        if(!initialized){
            this.value = value
            return
        }
        return jq.textbox("setValue", value)
    }

    override fun setDisabled(value: Boolean) {
        disabled = value
        if(initialized) {
            if (value) {
                jq.textbox("disable")
            } else {
                jq.textbox("enable")
            }
            updateShowClearIconVisibility()
        }
    }

    override fun showValidation(value: String?) {
        if(!initialized){
            return
        }
        val tb =jq.textbox("textbox")
        val spanElm = tb.parent()
        if(MiscUtilsJS.isBlank(value)){
            spanElm.css("border-color", "")
            spanElm.removeAttr("title")
        } else {
            spanElm.css("border-color", "#d9534f")
            spanElm.attr("title", value)
        }
    }

    private fun updateShowClearIconVisibility() {
        if(!initialized || !config.showClearIcon){
            return
        }
        if(!MiscUtilsJS.isBlank(value) && !disabled){
            jq.textbox("getIcon",0).css("visibility","visible")
        } else {
            jq.textbox("getIcon",0).css("visibility","hidden")
        }
    }


    override fun decorate() {
        jq = jQuery("#textBox$uid")
        val icons = arrayListOf<Any>()
        if(config.showClearIcon){
            icons.add(object{
                val iconCls = "icon-clear"
                val handler = {_:dynamic ->
                    jq.textbox("setValue", null)
                }
            })
        }
        jq.textbox(object{
            val value = this@EasyUiWebTextBox.value
            val icons = icons.toTypedArray()
            val disabled = this@EasyUiWebTextBox.disabled
            val onChange = {newValue:String?,_:String? ->
                this@EasyUiWebTextBox.value = newValue
                updateShowClearIconVisibility()
            }
        })
        val tb = jq.textbox("textbox")
        tb.on("input") {
            this@EasyUiWebTextBox.value = jq.textbox("getText") as String?
            val spanElm = tb.parent()
            spanElm.css("border-color", "")
            spanElm.removeAttr("title")
            updateShowClearIconVisibility()
        }
        initialized = true
        updateShowClearIconVisibility()
    }

    override fun destroy() {
        if(initialized){
            jq.textbox("destroy")
        }
    }

}