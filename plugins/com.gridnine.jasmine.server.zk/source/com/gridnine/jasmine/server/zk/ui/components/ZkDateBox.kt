/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.zk.ui.components

import com.gridnine.jasmine.server.core.ui.components.DateBox
import com.gridnine.jasmine.server.core.ui.components.DateBoxComponentConfiguration
import org.zkoss.zk.ui.HtmlBasedComponent
import org.zkoss.zul.Datebox
import java.time.LocalDate

class ZkDateBox (configure: DateBoxComponentConfiguration.() -> Unit) : DateBox, ZkUiComponent{

    private var value : LocalDate? = null

    private var component:Datebox? = null

    private var enabled = true

    private var validation:String? = null

    private val config = DateBoxComponentConfiguration()
    init {
        config.configure()
    }

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
        configureDimensions(component!!, config)
        component!!.format = "yyyy-MM-dd"
        component!!.valueInLocalDate = value
        component!!.isDisabled = !enabled
        component!!.setClass(if(validation != null) "jasmine-error" else "jasmine-normal")
        component!!.tooltip = validation
        return component!!
    }

}