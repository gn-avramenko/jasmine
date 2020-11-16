/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.easyui.adapter.elements

import com.gridnine.jasmine.web.core.ui.WebComponent
import com.gridnine.jasmine.web.core.ui.components.*
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import com.gridnine.jasmine.web.easyui.adapter.jQuery

class EasyUiWebNumberBox(private val parent:WebComponent?, configure: WebNumberBoxConfiguration.()->Unit) :WebNumberBox{

    private var initialized = false

    private val uid = MiscUtilsJS.createUUID()

    private var width:String? = null
    private var height:String? = null
    private var jq:dynamic = null
    private var showClearIcon = false
    private var precision = 2
    private var enabled = true
    private var validationMessage:String? = null

    private var storedValue:Double? = null
    init {
        (parent?.getChildren() as MutableList<WebComponent>?)?.add(this)
        val configuration = WebNumberBoxConfiguration()
        configuration.configure()
        width = configuration.width
        height = configuration.height
        showClearIcon = configuration.showClearIcon
        precision = configuration.precision
    }



    override fun getHtml(): String {
        return "<input id=\"numberBox${uid}\" style=\"${if(width != null) "width:$width" else ""};${if(height != null) "height:$height" else ""}\"/>"
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
                if(showClearIcon ){
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

    override fun getParent(): WebComponent? {
        return parent
    }

    override fun getChildren(): List<WebComponent> {
        return emptyList()
    }


    override fun decorate() {
        jq = jQuery("#numberBox$uid")
        var icons = arrayListOf<Any>()
        if(showClearIcon){
            icons.add(object{
                val iconCls = "icon-clear"
                val handler = {_:dynamic ->
                    jq.numberbox("setValue", null)
                    jq.numberbox("getIcon",0).css("visibility","hidden")
                }
            })
        }
        jq.numberbox(object{
            val precision = this@EasyUiWebNumberBox.precision
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
            if(showClearIcon){
                val text = jq.numberbox("getText") as String?
                jq.numberbox("getIcon",0).css("visibility",if(text?.isNotBlank() == false) "hidden" else "visible")
            }
        }
        if(showClearIcon && (storedValue == null  || !enabled)){
            jq.numberbox("getIcon",0).css("visibility","hidden")
        }
        showValidationInternal(validationMessage)
        initialized = true
    }

    private fun showValidationInternal(validationMessage: String?) {
        if(validationMessage != null){
            val tb =jq.textbox("textbox")
            val spanElm = tb.parent()
            spanElm.css("border-color", "#d9534f")
            spanElm.attr("title", validationMessage)
            return
        }
        val tb =jq.textbox("textbox")
        val spanElm = tb.parent()
        spanElm.css("border-color", "")
        spanElm.removeAttr("title")
    }

    override fun destroy() {
        //noops
    }

}