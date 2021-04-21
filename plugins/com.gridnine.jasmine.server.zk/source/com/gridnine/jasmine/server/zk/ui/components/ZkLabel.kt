/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.components

import com.gridnine.jasmine.server.core.ui.components.LabelConfiguration
import com.gridnine.jasmine.server.zk.ui.components.ZkUiComponent
import com.gridnine.jasmine.server.zk.ui.components.configureBasicParameters
import org.zkoss.zk.ui.HtmlBasedComponent
import org.zkoss.zul.Label

class ZkLabel(configure: LabelConfiguration.() -> Unit ): com.gridnine.jasmine.server.core.ui.components.Label, ZkUiComponent{

    private var text : String? = null

    private var component:Label? = null

    private val config = LabelConfiguration()

    init {
        config.configure()
    }

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
        configureBasicParameters(component!!, config)
        component!!.value  = text
        component!!.isMultiline  = config.multiline
        return component!!
    }



}