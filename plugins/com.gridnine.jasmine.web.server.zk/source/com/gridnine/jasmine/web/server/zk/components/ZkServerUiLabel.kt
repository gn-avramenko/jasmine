/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.components

import com.gridnine.jasmine.web.server.components.ServerUiNode
import com.gridnine.jasmine.web.server.components.ServerUiLabel
import com.gridnine.jasmine.web.server.components.ServerUiLabelConfiguration
import org.zkoss.zk.ui.HtmlBasedComponent
import org.zkoss.zul.Label

class ZkServerUiLabel(private val config: ServerUiLabelConfiguration) : ServerUiLabel, ZkServerUiComponent(){

    private var text : String? = null

    private var component:Label? = null

    override fun setText(value: String?) {
        this.text = value
        if(component != null){
            component!!.value = text
        }
    }


    override fun getZkComponent(): HtmlBasedComponent {
        if(component != null){
            return component!!
        }
        component = Label()
        if(config.height == "100%"){
            component!!.vflex = "1"
        } else {
            component!!.height = config.height
        }
        if(config.width == "100%"){
            component!!.hflex = "1"
        } else {
            component!!.width = config.width
        }
        component!!.value  = text
        component!!.isMultiline  = config.multiline
        return component!!
    }

    override fun getParent(): ServerUiNode? {
        return parent
    }


}