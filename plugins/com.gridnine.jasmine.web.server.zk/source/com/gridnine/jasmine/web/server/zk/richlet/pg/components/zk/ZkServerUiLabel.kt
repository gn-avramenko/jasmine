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
import com.gridnine.jasmine.web.server.zk.richlet.pg.components.SeverUiLabelConfiguration
import org.zkoss.zk.ui.HtmlBasedComponent
import org.zkoss.zul.Datebox
import org.zkoss.zul.Label
import java.time.LocalDate

class ZkServerUiLabel(private val config:SeverUiLabelConfiguration) : SeverUiLabel, ZkServerUiComponent(){

    private var text : String? = null

    private var component:Label? = null

    override fun setText(value: String?) {
        this.text = value
        if(component != null){
            component!!.value = text
        }
    }


    override fun getComponent(): HtmlBasedComponent {
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
        return component!!
    }

    override fun getParent(): ServerUiComponent? {
        return parent
    }


}