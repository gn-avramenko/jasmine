/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.easyui.adapter.elements

import com.gridnine.jasmine.web.core.ui.WebComponent
import com.gridnine.jasmine.web.core.ui.components.*
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import com.gridnine.jasmine.web.easyui.adapter.jQuery

class EasyUiWebTextBox(private val parent:WebComponent?, configure: WebTextBoxConfiguration.()->Unit) :WebTextBox{

    private var initialized = false

    private val uid = MiscUtilsJS.createUUID()

    private var width:String? = null
    private var height:String? = null
    private var prompt:String? = null
    private var jq:dynamic = null
    private var showClearIcon = false

    private var value:String? = null
    init {
        (parent?.getChildren() as MutableList<WebComponent>?)?.add(this)
        val configuration = WebTextBoxConfiguration()
        configuration.configure()
        width = configuration.width
        height = configuration.height
        prompt = configuration.prompt
        showClearIcon = configuration.showClearIcon
    }

    override fun getHtml(): String {
        return "<input id=\"textBox${uid}\" style=\"${if(width != null) "width:$width" else ""};${if(height != null) "height:$height" else ""}\"/>"
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

    override fun getParent(): WebComponent? {
        return parent
    }

    override fun getChildren(): List<WebComponent> {
        return emptyList()
    }


    override fun decorate() {
        jq = jQuery("#textBox$uid")
        var icons = arrayListOf<Any>()
        if(showClearIcon){
            icons.add(object{
                val iconCls = "icon-clear"
                val handler = {_:dynamic ->
                    jq.textbox("setValue", null)
                    jq.textbox("getIcon",0).css("visibility","hidden")
                }
            })
        }
        jq.textbox(object{
            val prompt = this@EasyUiWebTextBox.prompt
            val value = this@EasyUiWebTextBox.value
            val icons = icons.toTypedArray()
            val onChange = {newValue:String?,_:String? ->
                jq.textbox("getIcon",0).css("visibility",if(newValue?.isNotBlank() == false) "hidden" else "visible")
            }
        })
        val tb = jq.textbox("textbox")
        tb.on("input") {
            if(showClearIcon){
                val text = jq.textbox("getText") as String?
                jq.textbox("getIcon",0).css("visibility",if(text?.isNotBlank() == false) "hidden" else "visible")
            }
        }
        if(showClearIcon && (value == null || value!!.isEmpty())){
            jq.textbox("getIcon",0).css("visibility","hidden")
        }
        initialized = true
    }

}