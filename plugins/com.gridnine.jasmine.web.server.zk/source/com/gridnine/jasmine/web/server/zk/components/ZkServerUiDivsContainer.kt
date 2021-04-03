/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.components

import com.gridnine.jasmine.web.server.components.ServerUiNode
import com.gridnine.jasmine.web.server.components.ServerUiDivsContainer
import com.gridnine.jasmine.web.server.components.ServerUiDivsContainerConfiguration
import org.zkoss.zk.ui.HtmlBasedComponent
import org.zkoss.zul.Div

class ZkServerUiDivsContainer (private val config: ServerUiDivsContainerConfiguration) : ServerUiDivsContainer, ZkServerUiComponent(){

    private val divsMap = hashMapOf<String, ServerUiNode>()

    private var component:Div? = null

    private var activeComponentId: String? = null

    override fun addDiv(id: String, content: ServerUiNode) {
        divsMap[id] = content
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
            divsMap[activeComponentId]?.let { findZkComponent(it).getZkComponent().isVisible = false}
        }
        val comp = findZkComponent(divsMap[id]!!).getZkComponent()
        comp.parent = component!!
        comp.isVisible = true
    }

    override fun removeDiv(id: String) {
        val comp = divsMap.remove(id)!!
        if(component != null){
            val zkComp = findZkComponent(comp).getZkComponent()
            if(zkComp.parent != null){
                component!!.removeChild(zkComp)
            }
        }
    }

    override fun getDiv(id: String): ServerUiNode? {
        return divsMap[id]
    }

    override fun getZkComponent(): HtmlBasedComponent {
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

    override fun getParent(): ServerUiNode? {
        return parent
    }

}