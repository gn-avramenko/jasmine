/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.easyui.components

import com.gridnine.jasmine.web.core.ui.components.*
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS

class EasyUiWebBooleanBox(configure: WebBooleanBoxConfiguration.()->Unit) :WebBooleanBox,EasyUiComponent{

    private var initialized = false

    private val uid = MiscUtilsJS.createUUID()
    private var jq:dynamic = null
    private var enabled = true
    private var storedValue = false
    private val config = WebBooleanBoxConfiguration()
    init {
        config.configure()
    }

    override fun getId(): String {
        return "booleanBox${uid}"
    }

    override fun getHtml(): String {
        return "<input id=\"booleanBox${uid}\" style=\"${if(config.width != null) "width:${config.width}" else ""};${if(config.height != null) "height:${config.height}" else ""}\"/>"
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


    override fun decorate() {
        jq = jQuery("#booleanBox$uid")
        jq.switchbutton(object{
            val onText = config.onText
            val offText = config.offText
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