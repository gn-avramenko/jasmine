/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.zk.ui.components

import com.gridnine.jasmine.server.core.ui.components.NumberBox
import com.gridnine.jasmine.server.core.ui.components.NumberBoxComponentConfiguration
import org.zkoss.zk.ui.HtmlBasedComponent
import org.zkoss.zul.Doublebox
import java.math.BigDecimal

class ZkNumberBox (configure: NumberBoxComponentConfiguration.() -> Unit) : NumberBox, ZkUiComponent{

    private var initValue : BigDecimal? = null

    private var component:Doublebox? = null

    private var enabled = true

    private var validation:String? = null

    private val config = NumberBoxComponentConfiguration()

    init {
        config.configure()
    }
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

    override fun getZkComponent(): HtmlBasedComponent {
        if(component != null){
            return  component!!
        }
        component = Doublebox()
        configureDimensions(component!!, config)
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

}