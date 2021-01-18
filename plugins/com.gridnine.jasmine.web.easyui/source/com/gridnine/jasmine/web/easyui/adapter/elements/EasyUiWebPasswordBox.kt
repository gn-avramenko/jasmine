/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.easyui.adapter.elements

import com.gridnine.jasmine.web.core.ui.WebComponent
import com.gridnine.jasmine.web.core.ui.components.*
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import com.gridnine.jasmine.web.easyui.adapter.jQuery

class EasyUiWebPasswordBox(private val parent:WebComponent?, configure: WebPasswordBoxConfiguration.()->Unit) :WebPasswordBox{

    private var initialized = false

    private val uid = MiscUtilsJS.createUUID()

    private var width:String? = null
    private var height:String? = null
    private var prompt:String? = null
    private var jq:dynamic = null
    private var showClearIcon = false
    private var showEye = false

    private var value:String? = null

    private var disabled = false
    init {
        (parent?.getChildren() as MutableList<WebComponent>?)?.add(this)
        val configuration = WebPasswordBoxConfiguration()
        configuration.configure()
        width = configuration.width
        height = configuration.height
        prompt = configuration.prompt
        showClearIcon = configuration.showClearIcon
        showEye = configuration.showEye
    }

    override fun getHtml(): String {
        return "<input id=\"passwordBox${uid}\" style=\"${if(width != null) "width:$width" else ""};${if(height != null) "height:$height" else ""}\"/>"
    }

    override fun getValue(): String? {
        if(!initialized){
            return value
        }
        return jq.passwordbox("getText")
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
            updateShowClearIconVisibility()
        }
    }

    override fun resetValidation() {
        val tb =jq.passwordbox("textbox")
        val spanElm = tb.parent()
        spanElm.css("border-color", "")
        spanElm.removeAttr("title")
    }

    override fun showValidation(value: String) {
        if(!initialized){
            return
        }
        val tb =jq.passwordbox("textbox")
        val spanElm = tb.parent()
        spanElm.css("border-color", "#d9534f")
        spanElm.attr("title", value)
    }

    private fun updateShowClearIconVisibility() {
        if(!initialized || !showClearIcon){
            return
        }
        if(!MiscUtilsJS.isBlank(value) && !disabled){
            jq.passwordbox("getIcon",0).css("visibility","visible")
        } else {
            jq.passwordbox("getIcon",0).css("visibility","hidden")
        }
    }

    override fun getParent(): WebComponent? {
        return parent
    }

    override fun getChildren(): List<WebComponent> {
        return emptyList()
    }


    override fun decorate() {
        jq = jQuery("#passwordBox$uid")
        var icons = arrayListOf<Any>()
        if(showClearIcon){
            icons.add(object{
                val iconCls = "icon-clear"
                val handler = {_:dynamic ->
                    jq.passwordbox("reset")
                }
            })
        }
        jq.passwordbox(object{
            val prompt = this@EasyUiWebPasswordBox.prompt
            val value = this@EasyUiWebPasswordBox.value
            val icons = icons.toTypedArray()
            val disabled = this@EasyUiWebPasswordBox.disabled
            val showEye = this@EasyUiWebPasswordBox.showEye
            val onChange = {newValue:String?,_:String? ->
                this@EasyUiWebPasswordBox.value = newValue
                updateShowClearIconVisibility()
            }
        })
        val tb = jq.passwordbox("textbox")
        tb.on("input") {
            this@EasyUiWebPasswordBox.value = jq.passwordbox("getText") as String?
            val spanElm = tb.parent()
            spanElm.css("border-color", "")
            spanElm.removeAttr("title")
            updateShowClearIconVisibility()
        }
        initialized = true
        updateShowClearIconVisibility()
    }

    override fun destroy() {
        jq.passwordbox("destroy")
    }

}