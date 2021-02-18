/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.richlet.pg.components.zk

import com.gridnine.jasmine.web.server.zk.richlet.pg.ServerUiComponent
import com.gridnine.jasmine.web.server.zk.richlet.pg.ZkServerUiComponent
import com.gridnine.jasmine.web.server.zk.richlet.pg.components.ServerUiPanel
import com.gridnine.jasmine.web.server.zk.richlet.pg.components.ServerUiPanelConfiguration
import org.zkoss.zk.ui.HtmlBasedComponent
import org.zkoss.zk.ui.event.Events
import org.zkoss.zul.Panel
import org.zkoss.zul.Panelchildren

open class ZkServerUiPanel (private val config:ServerUiPanelConfiguration) : ServerUiPanel, ZkServerUiComponent(){

    private var component: Panel? = null

    private var title:String? = null

    private var maximizeHandler: (()->Unit)? = null

    private var minimizeHandler: (()->Unit)? = null

    private var content:ZkServerUiComponent? = null

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

    override fun setContent(comp: ServerUiComponent?) {
        this.content = comp as ZkServerUiComponent?
        if(component != null){
            val child = component!!.panelchildren.firstChild
            if(child != null){
                component!!.panelchildren.removeChild(child)
            }
            if(comp is ZkServerUiComponent) {
                component!!.panelchildren.appendChild(comp.getComponent())
            }
        }
    }

    override fun getComponent(): HtmlBasedComponent {
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
        component!!.isMaximizable = config.maximizable
        component!!.isMaximized = true
        if(config.maximizable){
            component!!.addEventListener(Events.ON_MAXIMIZE){event->
                maximizeHandler?.let{
                    event.stopPropagation()
                    it.invoke()
                }
            }
        }
        component!!.isMinimizable = config.minimizable
        if(config.minimizable){
            component!!.addEventListener(Events.ON_MINIMIZE){event->
                minimizeHandler?.let{
                    event.stopPropagation()
                    component!!.isMaximized = true
                    it.invoke()
                }
            }
        }
        val panelChildren = Panelchildren()
        panelChildren.parent = component
        if(content != null){
            content!!.getComponent().parent = panelChildren
        }
        return component!!
    }

    override fun getParent(): ServerUiComponent? {
        return parent
    }


}