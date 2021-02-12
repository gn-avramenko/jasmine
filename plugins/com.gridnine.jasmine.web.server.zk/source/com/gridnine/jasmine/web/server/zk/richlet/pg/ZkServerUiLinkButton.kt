/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.richlet.pg

import org.zkoss.zk.ui.HtmlBasedComponent
import org.zkoss.zk.ui.event.Events
import org.zkoss.zul.Button

class ZkServerUiLinkButton(private val config:ServerUiLinkButtonConfiguration) :ServerUiLinkButton, ZkServerUiComponent(){

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

    override fun createComponent(): HtmlBasedComponent {
        val comp = Button()
        component = comp
        comp.label = config.title
        if(config.width == "100%"){
            comp.hflex = "1"
        } else if(config.width != null){
            comp.width = config.width
        }
        if(config.height == "100%"){
            comp.vflex = "1"
        } else if(config.height != null){
            comp.height = config.height
        }
        comp.addEventListener(Events.ON_CLICK){
            handler?.invoke()
        }
        setEnabledInternal()
        return comp
    }

    override fun getParent(): ServerUiComponent? {
        return parent
    }

}