/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.components

import com.gridnine.jasmine.web.server.components.ServerUiNode
import com.gridnine.jasmine.web.server.components.ServerUiLinkButton
import com.gridnine.jasmine.web.server.components.ServerUiLinkButtonConfiguration
import org.zkoss.zk.ui.HtmlBasedComponent
import org.zkoss.zk.ui.event.Events
import org.zkoss.zul.Button

class ZkServerUiLinkButton(private val config: ServerUiLinkButtonConfiguration) : ServerUiLinkButton, ZkServerUiComponent(){

    private var component:Button? = null

    private var handler: (() -> Unit)? = null

    private var enabled = true

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
        comp.width = config.width
        comp.height = config.height
        comp.iconSclass = config.iconClass
        comp.addEventListener(Events.ON_CLICK){
            handler?.invoke()
        }
        setEnabledInternal()
        return comp
    }

    override fun getParent(): ServerUiNode? {
        return parent
    }

}