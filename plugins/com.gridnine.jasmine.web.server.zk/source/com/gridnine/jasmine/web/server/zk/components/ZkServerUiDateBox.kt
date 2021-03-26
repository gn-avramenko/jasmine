/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.components

import com.gridnine.jasmine.web.server.components.ServerUiNode
import com.gridnine.jasmine.web.server.components.ServerUiDateBox
import com.gridnine.jasmine.web.server.components.ServerUiDateBoxConfiguration
import org.zkoss.zk.ui.HtmlBasedComponent
import org.zkoss.zul.Datebox
import java.time.LocalDate

class ZkServerUiDateBox (private val config: ServerUiDateBoxConfiguration) : ServerUiDateBox, ZkServerUiComponent(){

    private var value : LocalDate? = null

    private var component:Datebox? = null

    private var enabled = true

    private var validation:String? = null

    override fun getValue(): LocalDate? {
        if(component == null){
            return value
        }
        return component!!.valueInLocalDate
    }

    override fun setValue(value: LocalDate?) {
        this.value = value
        if(component!= null){
            component!!.valueInLocalDate = value
        }
    }

    override fun setEnabled(value: Boolean) {
        enabled = value
        if(component != null){
            component!!.isDisabled = !value
        }
    }

    override fun showValidation(value: String?) {
       validation = value
        if(component != null){
            component!!.setClass(if(validation != null) "jasmine-error" else "jasmine-normal")
            component!!.tooltiptext = validation
        }
    }

    override fun getZkComponent(): HtmlBasedComponent {
        if(component != null){
            return  component!!
        }
        component = Datebox()
        if(config.width == "100%"){
            component!!.hflex = "1"
        } else if(config.width != null){
            component!!.width = config.width
        }
        if(config.height == "100%"){
            component!!.vflex = "1"
        } else if(config.height != null){
            component!!.height = config.height
        }
        component!!.format = "yyyy-MM-dd"
        component!!.valueInLocalDate = value
        component!!.isDisabled = !enabled
        component!!.setClass(if(validation != null) "jasmine-error" else "jasmine-normal")
        component!!.tooltip = validation
        return component!!
    }

    override fun getParent(): ServerUiNode? {
        return parent
    }

}