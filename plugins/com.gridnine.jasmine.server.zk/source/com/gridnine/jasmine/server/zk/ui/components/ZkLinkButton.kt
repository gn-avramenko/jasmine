/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.zk.ui.components

import com.gridnine.jasmine.server.core.ui.components.LinkButton
import com.gridnine.jasmine.server.core.ui.components.LinkButtonConfiguration
import org.zkoss.zk.ui.HtmlBasedComponent
import org.zkoss.zk.ui.event.Events
import org.zkoss.zul.Button

class ZkLinkButton(configure: LinkButtonConfiguration.() -> Unit) : LinkButton, ZkUiComponent{

    private var component:Button? = null

    private var handler: (() -> Unit)? = null

    private var enabled = true

    private val config = LinkButtonConfiguration()

    init {
        config.configure()
    }

    override fun setHandler(handler: () -> Unit) {
        this.handler = handler
    }

    override fun setEnabled(value: Boolean) {
        enabled = value
        if(component != null){
            setEnabledInternal()
        }
    }

    private fun setEnabledInternal() {
        val comp = component!!
        comp.isDisabled = !enabled
    }

    override fun getZkComponent(): HtmlBasedComponent {
        if(component!= null){
            return component!!
        }
        val comp = Button()
        component = comp
        comp.label = config.title
        configureDimensions(comp, config)
        comp.iconSclass = config.iconClass
        comp.addEventListener(Events.ON_CLICK){
            handler?.invoke()
        }
        setEnabledInternal()
        return comp
    }


}