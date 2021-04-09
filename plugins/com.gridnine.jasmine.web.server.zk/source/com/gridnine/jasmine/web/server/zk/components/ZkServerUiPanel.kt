/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.components

import com.gridnine.jasmine.web.server.components.ServerUiNode
import com.gridnine.jasmine.web.server.components.ServerUiPanel
import com.gridnine.jasmine.web.server.components.ServerUiPanelConfiguration
import org.zkoss.zk.ui.HtmlBasedComponent
import org.zkoss.zk.ui.event.Events
import org.zkoss.zk.ui.event.MinimizeEvent
import org.zkoss.zul.Panel
import org.zkoss.zul.Panelchildren

open class ZkServerUiPanel (private val config: ServerUiPanelConfiguration) : ServerUiPanel, ZkServerUiComponent(){

    private var component: Panel? = null

    private var title:String? = null

    private var maximizeHandler: (()->Unit)? = null

    private var minimizeHandler: (()->Unit)? = null

    private var content: ZkServerUiComponent? = null

    override fun setTitle(title: String) {
        this.title = title
        if(component != null){
            component!!.title = title
        }
    }

    override fun setMaximizeHandler(handler: () -> Unit) {
        this.maximizeHandler = handler
    }

    override fun setMinimizeHandler(handler: () -> Unit) {
        this.minimizeHandler = handler
    }

    override fun setContent(comp: ServerUiNode?) {
        this.content = comp?.let {findZkComponent(it)}
        if(component != null){
            val child = component!!.panelchildren.firstChild
            if(child != null){
                component!!.panelchildren.removeChild(child)
            }
            if(comp is ZkServerUiComponent) {
                component!!.panelchildren.appendChild(comp.getZkComponent())
            }
        }
    }

    override fun getZkComponent(): HtmlBasedComponent {
        if(component != null){
            return component!!
        }
        component = Panel()
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
        component!!.title = title
        component!!.setBorder(true)
        if(config.maximizable) {
            component!!.isMaximizable = true
            component!!.isMaximized = true
            component!!.addEventListener(Events.ON_MAXIMIZE) { event ->
                maximizeHandler?.let {
                    it.invoke()
                }
            }
        }
        component!!.isMinimizable = config.minimizable
        if(config.minimizable){
            component!!.addEventListener(Events.ON_MINIMIZE){event->
                event as MinimizeEvent
                if(event.isMinimized){
                    minimizeHandler?.let{
                        it.invoke()
                    }
                }
            }
        }
        val panelChildren = Panelchildren()
        panelChildren.style ="overflow: auto"
        panelChildren.parent = component
        if(content != null){
            content!!.getZkComponent().parent = panelChildren
        }
        return component!!
    }

    override fun getParent(): ServerUiNode? {
        return parent
    }


}