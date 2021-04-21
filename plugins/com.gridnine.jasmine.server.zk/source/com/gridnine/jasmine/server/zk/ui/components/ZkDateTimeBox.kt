/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.zk.ui.components

import com.gridnine.jasmine.server.core.ui.components.DateTimeBox
import com.gridnine.jasmine.server.core.ui.components.DateTimeBoxComponentConfiguration
import org.zkoss.zk.ui.HtmlBasedComponent
import org.zkoss.zul.Datebox
import java.time.LocalDateTime

class ZkDateTimeBox (configure: DateTimeBoxComponentConfiguration.() -> Unit) : DateTimeBox, ZkUiComponent{

    private var value : LocalDateTime? = null

    private var component:Datebox? = null

    private var enabled = true

    private var validation:String? = null

    private val config = DateTimeBoxComponentConfiguration()

    init {
        config.configure()
    }

    override fun getValue(): LocalDateTime? {
        if(component == null){
            return value
        }
        return component!!.valueInLocalDateTime
    }

    override fun setValue(value: LocalDateTime?) {
        this.value = value
        if(component!= null){
            component!!.valueInLocalDateTime = value
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
        configureBasicParameters(component!!, config)
        component!!.format = "yyyy-MM-dd HH:mm"
        component!!.valueInLocalDateTime = value
        component!!.isDisabled = !enabled
        component!!.setClass(if(validation != null) "jasmine-error" else "jasmine-normal")
        component!!.tooltip = validation
        return component!!
    }


}