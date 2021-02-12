/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.richlet.pg

import org.zkoss.zk.ui.HtmlBasedComponent
import org.zkoss.zul.Textbox

class ZkServerUiTextBox(private val config : ServerUiTextBoxConfiguration) : ServerUiTextBox, ZkServerUiComponent(){

    private var value:String? = null

    private var component:Textbox? = null

    override fun getValue(): String? {
        if(component != null){
            return component!!.value
        }
        return value
    }

    override fun setValue(value: String?) {
        this.value = value
        if(component != null){
            component!!.value = value
        }
    }

    override fun createComponent(): HtmlBasedComponent {
        val comp = Textbox()
        if(config.width == "100%"){
            comp.hflex = "1"
        } else if(config.width != null){
            comp.width = "width"
        }
        if(config.height == "100%"){
            comp.vflex = "1"
        }else if(config.height != null) {
            comp.height = config.height
        }
        component = comp
        return comp
    }

    override fun getParent(): ServerUiComponent? {
        return parent
    }

}