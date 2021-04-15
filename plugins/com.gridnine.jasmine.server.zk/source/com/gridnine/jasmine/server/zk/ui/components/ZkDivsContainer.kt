/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.zk.ui.components

import com.gridnine.jasmine.server.core.ui.common.UiNode
import com.gridnine.jasmine.server.core.ui.components.DivsContainer
import com.gridnine.jasmine.server.core.ui.components.DivsContainerConfiguration
import org.zkoss.zk.ui.AbstractComponent
import org.zkoss.zk.ui.HtmlBasedComponent
import org.zkoss.zul.Div

class ZkDivsContainer(configure: DivsContainerConfiguration.() -> Unit) : DivsContainer, ZkUiComponent{

    private val divsMap = hashMapOf<String, UiNode>()

    private var component:Div? = null

    private var activeComponentId: String? = null

    private val config = DivsContainerConfiguration()
    init {
        config.configure()
    }

    override fun addDiv(id: String, content: UiNode) {
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
        if(activeComponentId == id){
            activeComponentId = null
        }
        if(component != null){
            val zkComp = findZkComponent(comp).getZkComponent()
            if(zkComp.parent != null){
                component!!.removeChild(zkComp)
            }
        }
    }

    override fun getDiv(id: String): UiNode? {
        return divsMap[id]
    }

    override fun clear() {
        divsMap.clear()
        if(component != null){
            component!!.getChildren<AbstractComponent>().clear()
        }
    }

    override fun getZkComponent(): HtmlBasedComponent {
        if(component != null){
            return component!!
        }
        component = Div()
        configureDimensions(component!!, config)
        var previousId = activeComponentId
        if(previousId != null){
            activeComponentId == null
            showInternal(previousId)
            activeComponentId = previousId
        }
        return component!!
    }
}