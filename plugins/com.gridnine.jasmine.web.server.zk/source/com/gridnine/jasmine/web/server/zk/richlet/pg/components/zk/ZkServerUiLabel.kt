/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.richlet.pg.components.zk

import com.gridnine.jasmine.web.server.zk.richlet.pg.ServerUiComponent
import com.gridnine.jasmine.web.server.zk.richlet.pg.ZkServerUiComponent
import com.gridnine.jasmine.web.server.zk.richlet.pg.components.ServerUiDateBox
import com.gridnine.jasmine.web.server.zk.richlet.pg.components.ServerUiDateBoxConfiguration
import com.gridnine.jasmine.web.server.zk.richlet.pg.components.SeverUiLabel
import org.zkoss.zk.ui.HtmlBasedComponent
import org.zkoss.zul.Datebox
import org.zkoss.zul.Label
import java.time.LocalDate

class ZkServerUiLabel : SeverUiLabel, ZkServerUiComponent(){

    private var text : String? = null

    private var width : String? = null

    private var height : String? = null

    private var component:Label? = null

    override fun setText(value: String?) {
        this.text = value
        if(component != null){
            component!!.value = text
        }
    }

    override fun setWidth(value: String) {
        this.width = value
        if(component != null){
            if(value == "100%"){
                component!!.hflex = "1"
            } else {
                component!!.width = value
            }
        }
    }

    override fun setHeight(value: String) {
        this.height = value
        if(component != null){
            if(value == "100%"){
                component!!.vflex = "1"
            } else {
                component!!.height = value
            }
        }
    }

    override fun getComponent(): HtmlBasedComponent {
        if(component != null){
            return component!!
        }
        component = Label()
        if(height == "100%"){
            component!!.vflex = "1"
        } else {
            component!!.height = height
        }
        if(width == "100%"){
            component!!.hflex = "1"
        } else {
            component!!.width = width
        }
        component!!.value  = text
        return component!!
    }

    override fun getParent(): ServerUiComponent? {
        return parent
    }


}