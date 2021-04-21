/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.zk.ui.components

import com.gridnine.jasmine.server.core.ui.common.UiNode
import com.gridnine.jasmine.server.core.ui.components.PanelConfiguration
import org.zkoss.zk.ui.HtmlBasedComponent
import org.zkoss.zk.ui.event.Events
import org.zkoss.zk.ui.event.MinimizeEvent
import org.zkoss.zul.Panel
import org.zkoss.zul.Panelchildren

open class ZkPanel (configure: PanelConfiguration.() -> Unit) : com.gridnine.jasmine.server.core.ui.components.Panel, ZkUiComponent{

    private var component: Panel? = null

    private var title:String? = null

    private var maximizeHandler: (()->Unit)? = null

    private var minimizeHandler: (()->Unit)? = null

    private var content: ZkUiComponent? = null

    private val config = PanelConfiguration()

    init {
        config.configure()
    }

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

    override fun setContent(comp: UiNode?) {
        this.content = comp?.let { findZkComponent(it) }
        if(component != null){
            val child = component!!.panelchildren.firstChild
            if(child != null){
                component!!.panelchildren.removeChild(child)
            }
            if(comp is ZkUiComponent) {
                component!!.panelchildren.appendChild(comp.getZkComponent())
            }
        }
    }

    override fun getZkComponent(): HtmlBasedComponent {
        if(component != null){
            return component!!
        }
        component = Panel()
        configureBasicParameters(component!!, config)
        component!!.title = title
        component!!.setBorder(true)
        if(config.maximizable) {
            component!!.isMaximizable = true
            component!!.isMaximized = true
            component!!.addEventListener(Events.ON_MAXIMIZE) {
                maximizeHandler?.invoke()
            }
        }
        component!!.isMinimizable = config.minimizable
        if(config.minimizable){
            component!!.addEventListener(Events.ON_MINIMIZE){event->
                event as MinimizeEvent
                if(event.isMinimized){
                    minimizeHandler?.invoke()
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

}