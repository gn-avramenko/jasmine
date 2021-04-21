/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.zk.ui.components

import com.gridnine.jasmine.server.core.ui.components.PasswordBox
import com.gridnine.jasmine.server.core.ui.components.PasswordBoxComponentConfiguration
import org.zkoss.zk.ui.HtmlBasedComponent
import org.zkoss.zul.Textbox

class ZkPasswordBox(configure: PasswordBoxComponentConfiguration.() -> Unit) : PasswordBox, ZkUiComponent{

    private var value:String? = null

    private var component:Textbox? = null

    private var validation:String? = null

    private var disabled = false

    private val config  = PasswordBoxComponentConfiguration()
    init {
        config.configure()
    }

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
        comp.type = "password"
        configureBasicParameters(comp, config)
        comp.text = value
        comp.setClass(if(validation != null) "jasmine-error" else "jasmine-normal")
        comp.isDisabled = disabled
        component = comp
        return comp
    }

}