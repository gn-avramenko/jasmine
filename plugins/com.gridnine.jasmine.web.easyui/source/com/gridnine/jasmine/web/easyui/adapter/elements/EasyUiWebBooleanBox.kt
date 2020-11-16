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

class EasyUiWebBooleanBox(private val parent:WebComponent?, configure: WebBooleanBoxConfiguration.()->Unit) :WebBooleanBox{

    private var initialized = false

    private val uid = MiscUtilsJS.createUUID()
    private var jq:dynamic = null
    private var enabled = true
    private var storedValue = false
    private val configuration = WebBooleanBoxConfiguration()
    init {
        configuration.configure()
    }

    override fun getHtml(): String {
        return "<input id=\"booleanBox${uid}\" style=\"${if(configuration.width != null) "width:${configuration.width}" else ""};${if(configuration.height != null) "height:${configuration.height}" else ""}\"/>"
    }

    override fun getValue() = storedValue

    override fun setValue(value: Boolean){
        if(storedValue != value){
            storedValue =  value
            if(initialized){
                if(storedValue){
                    jq.switchbutton("check")
                } else {
                    jq.switchbutton("uncheck")
                }
                return
            }
        }
    }

    override fun setEnabled(value: Boolean) {
        if(value != enabled){
            enabled = value
            if(initialized){
                if(enabled){
                    jq.switchbutton("enable")
                } else{
                    jq.switchbutton("disable")
                }
            }
        }
    }

    override fun getParent(): WebComponent? {
        return parent
    }

    override fun getChildren(): List<WebComponent> {
        return emptyList()
    }


    override fun decorate() {
        jq = jQuery("#booleanBox$uid")
        jq.switchbutton(object{
            val onText = configuration.onText
            val offText = configuration.offText
            val onChange = {checked:Boolean ->
                storedValue = checked
            }
            val disabled = !enabled
            val checked = storedValue
        })
        initialized = true
    }

    override fun destroy() {
        //noops
    }

}