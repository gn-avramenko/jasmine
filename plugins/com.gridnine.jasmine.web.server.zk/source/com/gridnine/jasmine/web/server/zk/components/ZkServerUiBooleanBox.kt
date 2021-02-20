/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.components

import com.gridnine.jasmine.web.server.components.ServerUiComponent
import com.gridnine.jasmine.web.server.components.ServerUiBooleanBox
import com.gridnine.jasmine.web.server.components.ServerUiBooleanBoxConfiguration
import org.zkoss.zk.ui.HtmlBasedComponent
import org.zkoss.zul.Checkbox

class ZkServerUiBooleanBox(private val config: ServerUiBooleanBoxConfiguration): ServerUiBooleanBox, ZkServerUiComponent(){

    private var value = false

    private var component:Checkbox? = null

    private var enabled = true

    override fun getValue(): Boolean {
        if(component == null){
            return value
        }
        return component!!.isChecked
    }

    override fun setValue(value: Boolean) {
        this.value = value
        if(component != null){
            component!!.isChecked = value
        }
    }

    override fun setEnabled(value: Boolean) {
        this.enabled = value
        if(component != null){
            component!!.isDisabled = !value
        }
    }

    override fun getComponent(): HtmlBasedComponent {
        if(component != null){
            return component!!
        }
        component = Checkbox()
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
        component!!.mold = "switch"
        component!!.isChecked =value
        component!!.isDisabled = !enabled
        return component!!
    }

    override fun getParent(): ServerUiComponent? {
        return parent
    }

}