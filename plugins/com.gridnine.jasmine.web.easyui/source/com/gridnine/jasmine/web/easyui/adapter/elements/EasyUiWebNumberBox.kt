/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.easyui.adapter.elements

import com.gridnine.jasmine.web.core.ui.WebComponent
import com.gridnine.jasmine.web.core.ui.components.*
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import com.gridnine.jasmine.web.easyui.adapter.jQuery
import kotlin.js.Date

class EasyUiWebNumberBox(private val parent:WebComponent?, configure: WebNumberBoxConfiguration.()->Unit) :WebNumberBox{

    private var initialized = false

    private val uid = MiscUtilsJS.createUUID()

    private var width:String? = null
    private var height:String? = null
    private var jq:dynamic = null
    private var showClearIcon = false
    private var precision = 2

    private var value:Double? = null
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
            return value
        }
        val value = jq.numberbox("getText") as String?
        return if (value.isNullOrBlank()) null else value.toDouble()
    }

    override fun setValue(value: Double?){
        if(!initialized){
            this.value = value
            return
        }
        jq.numberbox("setValue", value)
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
            }
        })
        val tb = jq.numberbox("textbox")
        tb.on("input") {
            if(showClearIcon){
                val text = jq.numberbox("getText") as String?
                jq.numberbox("getIcon",0).css("visibility",if(text?.isNotBlank() == false) "hidden" else "visible")
            }
        }
        if(showClearIcon && value == null){
            jq.numberbox("getIcon",0).css("visibility","hidden")
        }
        initialized = true
    }

}