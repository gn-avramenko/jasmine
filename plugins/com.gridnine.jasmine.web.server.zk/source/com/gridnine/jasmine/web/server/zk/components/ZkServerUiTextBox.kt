/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.components

import com.gridnine.jasmine.web.server.components.ServerUiNode
import com.gridnine.jasmine.web.server.components.ServerUiTextBox
import com.gridnine.jasmine.web.server.components.ServerUiTextBoxConfiguration
import org.zkoss.zk.ui.HtmlBasedComponent
import org.zkoss.zk.ui.event.Events
import org.zkoss.zul.Textbox

class ZkServerUiTextBox(private val config : ServerUiTextBoxConfiguration) : ServerUiTextBox, ZkServerUiComponent(){

    private var value:String? = null

    private var component:Textbox? = null

    private var validation:String? = null

    private var actionListenerSet = false

    private var actionListener: ((String?)->Unit)? = null

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

    override fun setActionListener(listener: (String?) -> Unit) {
        this.actionListener = listener
        if(component != null){
            if(!actionListenerSet){
                component!!.addEventListener(Events.ON_OK){
                    this.actionListener!!.invoke(component!!.value)
                }
                actionListenerSet = true
            }
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
        if(actionListener != null){
            comp.addEventListener(Events.ON_OK){
                this.actionListener!!.invoke(comp.value)
            }
            actionListenerSet = true
        }
        comp.isDisabled = disabled
        component = comp

        return comp
    }

    override fun getParent(): ServerUiNode? {
        return parent
    }

}