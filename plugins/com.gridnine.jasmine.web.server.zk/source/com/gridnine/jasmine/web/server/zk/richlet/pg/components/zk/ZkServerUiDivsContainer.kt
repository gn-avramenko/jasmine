/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.richlet.pg.components.zk

import com.gridnine.jasmine.web.server.zk.richlet.pg.ServerUiComponent
import com.gridnine.jasmine.web.server.zk.richlet.pg.ZkServerUiComponent
import com.gridnine.jasmine.web.server.zk.richlet.pg.components.ServerUiDivsContainer
import com.gridnine.jasmine.web.server.zk.richlet.pg.components.ServerUiDivsContainerConfiguration
import org.zkoss.zk.ui.HtmlBasedComponent
import org.zkoss.zul.Div

class ZkServerUiDivsContainer (private val config:ServerUiDivsContainerConfiguration) : ServerUiDivsContainer, ZkServerUiComponent(){

    private val divsMap = hashMapOf<String, ZkServerUiComponent>()

    private var component:Div? = null

    private var activeComponentId: String? = null

    override fun addDiv(id: String, content: ServerUiComponent) {
        divsMap[id] = content as ZkServerUiComponent
    }

    override fun show(id: String) {
        if(activeComponentId != id){
            if(component != null){
                showInternal(id)
            }
            activeComponentId = id
        }
    }

    private fun showInternal(id:String) {
        if(activeComponentId != null){
            divsMap[activeComponentId]!!.getComponent().isVisible = false
        }
        val comp = divsMap[id]!!.getComponent()
        comp.parent = component!!
        comp.isVisible = true
    }

    override fun removeDiv(id: String) {
        val comp = divsMap.remove(id)!!
        if(component != null){
            val zkComp = comp.getComponent()
            if(zkComp.parent != null){
                component!!.removeChild(zkComp)
            }
        }
    }

    override fun getDiv(id: String): ServerUiComponent? {
        return divsMap[id]
    }

    override fun getComponent(): HtmlBasedComponent {
        if(component != null){
            return component!!
        }
        component = Div()
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
        var previousId = activeComponentId
        if(previousId != null){
            activeComponentId == null
            showInternal(previousId)
            activeComponentId = previousId
        }
        return component!!
    }

    override fun getParent(): ServerUiComponent? {
        return parent
    }

}