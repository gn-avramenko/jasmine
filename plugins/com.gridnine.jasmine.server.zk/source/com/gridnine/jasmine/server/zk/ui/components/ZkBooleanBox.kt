/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.zk.ui.components

import com.gridnine.jasmine.server.core.ui.components.BooleanBox
import com.gridnine.jasmine.server.core.ui.components.BooleanBoxComponentConfiguration
import org.zkoss.zk.ui.HtmlBasedComponent
import org.zkoss.zul.Checkbox

class ZkBooleanBox(configure: BooleanBoxComponentConfiguration.() -> Unit): BooleanBox, ZkUiComponent{

    private var value = false

    private var component:Checkbox? = null

    private var enabled = true

    private val config =  BooleanBoxComponentConfiguration()

    init {
        config.configure()
    }

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

    override fun getZkComponent(): HtmlBasedComponent {
        if(component != null){
            return component!!
        }
        component = Checkbox()
        configureDimensions(component!!, config)
        component!!.mold = "switch"
        component!!.isChecked =value
        component!!.isDisabled = !enabled
        return component!!
    }

}