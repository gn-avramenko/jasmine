/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.easyui.components

import com.gridnine.jasmine.web.core.ui.components.*
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS

@Suppress("unused")
class EasyUiWebPasswordBox(configure: WebPasswordBoxConfiguration.()->Unit) :WebPasswordBox,EasyUiComponent{

    private var initialized = false

    private val uid = MiscUtilsJS.createUUID()

    private var jq:dynamic = null

    private var value:String? = null

    private var disabled = false

    private val config = WebPasswordBoxConfiguration()
    init {
        config.configure()
    }

    override fun getId(): String {
        return "passwordBox${uid}"
    }
    override fun getHtml(): String {
        return "<input id=\"passwordBox${uid}\" style=\"${getSizeAttributes(config)}\"/>"
    }

    override fun getValue(): String? {
        if(!initialized){
            return value
        }
        return jq.passwordbox("getValue")
    }

    override fun setValue(value: String?){
        if(!initialized){
            this.value = value
            return
        }
        return jq.passwordbox("setValue", value)
    }

    override fun setDisabled(value: Boolean) {
        disabled = value
        if(initialized) {
            if (value) {
                jq.passwordbox("disable")
            } else {
                jq.passwordbox("enable")
            }
        }
    }

    override fun showValidation(value: String?) {
        if(!initialized){
            return
        }
        val tb =jq.passwordbox("textbox")
        val spanElm = tb.parent()
        if(MiscUtilsJS.isBlank(value)){
            spanElm.css("border-color", "")
            spanElm.removeAttr("title")
        } else {
            spanElm.css("border-color", "#d9534f")
            spanElm.attr("title", value)
        }
    }


    override fun decorate() {
        jq = jQuery("#passwordBox$uid")
        val icons = arrayListOf<Any>()
        jq.passwordbox(object{
            val value = this@EasyUiWebPasswordBox.value
            val icons = icons.toTypedArray()
            val disabled = this@EasyUiWebPasswordBox.disabled
            val onChange = {newValue:String?,_:String? ->
                this@EasyUiWebPasswordBox.value = newValue
            }
        })
        val tb = jq.passwordbox("textbox")
        tb.on("input") {
            this@EasyUiWebPasswordBox.value = jq.passwordbox("getText") as String?
            val spanElm = tb.parent()
            spanElm.css("border-color", "")
            spanElm.removeAttr("title")
        }
        initialized = true
    }

    override fun destroy() {
        if(initialized){
            jq.passwordbox("destroy")
        }
    }

}