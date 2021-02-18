/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.richlet.pg.components.zk

import com.gridnine.jasmine.web.server.zk.richlet.pg.ServerUiComponent
import com.gridnine.jasmine.web.server.zk.richlet.pg.ZkServerUiComponent
import com.gridnine.jasmine.web.server.zk.richlet.pg.components.ServerUiDateBox
import com.gridnine.jasmine.web.server.zk.richlet.pg.components.ServerUiDateBoxConfiguration
import com.gridnine.jasmine.web.server.zk.richlet.pg.components.ServerUiNumberBox
import com.gridnine.jasmine.web.server.zk.richlet.pg.components.ServerUiNumberBoxConfiguration
import org.zkoss.zk.ui.HtmlBasedComponent
import org.zkoss.zul.Datebox
import org.zkoss.zul.Doublebox
import java.lang.StringBuilder
import java.math.BigDecimal
import java.time.LocalDate

class ZkServerUiNumberBox (private val config:ServerUiNumberBoxConfiguration) : ServerUiNumberBox, ZkServerUiComponent(){

    private var initValue : BigDecimal? = null

    private var component:Doublebox? = null

    private var enabled = true

    private var validation:String? = null

    override fun getValue(): BigDecimal? {
        if(component == null){
            return initValue
        }
        return component!!.value?.let { BigDecimal.valueOf(it) }
    }

    override fun setValue(value: BigDecimal?) {
        this.initValue = value
        if(component!= null){
            component!!.setValue(value?.toDouble())
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

    override fun getComponent(): HtmlBasedComponent {
        if(component != null){
            return  component!!
        }
        component = Doublebox()
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
        val sb = StringBuilder("#")
        for(n in 1..config.precision){
            if(n == 1){
                sb.append(".")
            }
            sb.append("0")
        }
        component!!.format = sb.toString()
        component!!.setValue(initValue?.toDouble())
        component!!.isDisabled = !enabled
        component!!.setClass(if(validation != null) "jasmine-error" else "jasmine-normal")
        component!!.tooltip = validation
        return component!!
    }

    override fun getParent(): ServerUiComponent? {
        return parent
    }

}