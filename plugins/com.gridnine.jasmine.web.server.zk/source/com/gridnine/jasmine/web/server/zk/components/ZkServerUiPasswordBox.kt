/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.components

import com.gridnine.jasmine.web.server.components.ServerUiNode
import com.gridnine.jasmine.web.server.components.ServerUiPasswordBox
import com.gridnine.jasmine.web.server.components.ServerUiPasswordBoxConfiguration
import org.zkoss.zk.ui.HtmlBasedComponent
import org.zkoss.zul.Textbox

class ZkServerUiPasswordBox(private val config : ServerUiPasswordBoxConfiguration) : ServerUiPasswordBox, ZkServerUiComponent(){

    private var value:String? = null

    private var component:Textbox? = null

    private var validation:String? = null

    private var disabled = false

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

    override fun showValidation(value: String?) {
       this.validation = value
        if(component != null){
            component!!.setClass(if(validation != null) "jasmine-error" else "jasmine-normal")
        }
    }

    override fun setDisabled(value: Boolean) {
        disabled = value
        if(component != null){
            component!!.isDisabled = disabled
        }
    }

    override fun getZkComponent(): HtmlBasedComponent {
        if(component != null){
            return component!!
        }
        val comp = Textbox()
        comp.type = "password"
        if(config.width == "100%"){
            comp.hflex = "1"
        } else if(config.width != null){
            comp.width = config.width
        }
        if(config.height == "100%"){
            comp.vflex = "1"
        }else if(config.height != null) {
            comp.height = config.height
        }
        comp.text = value
        comp.setClass(if(validation != null) "jasmine-error" else "jasmine-normal")
        comp.isDisabled = disabled
        component = comp

        return comp
    }

    override fun getParent(): ServerUiNode? {
        return parent
    }

}