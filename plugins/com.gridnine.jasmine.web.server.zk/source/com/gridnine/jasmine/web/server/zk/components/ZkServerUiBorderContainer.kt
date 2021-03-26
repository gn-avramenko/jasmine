/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.components

import com.gridnine.jasmine.web.server.components.ServerUiNode
import com.gridnine.jasmine.web.server.components.ServerUiBorderContainer
import com.gridnine.jasmine.web.server.components.ServerUiBorderContainerConfiguration
import com.gridnine.jasmine.web.server.components.ServerUiBorderContainerRegion
import org.zkoss.zk.ui.Component
import org.zkoss.zk.ui.HtmlBasedComponent
import org.zkoss.zul.*
import kotlin.reflect.KClass

open class ZkServerUiBorderContainer(private val config: ServerUiBorderContainerConfiguration) : ServerUiBorderContainer, ZkServerUiComponent(){

    private var component: Borderlayout? = null

    private var  northRegion: ServerUiBorderContainerRegion? = null
    private var  southRegion: ServerUiBorderContainerRegion? = null
    private var  centerRegion: ServerUiBorderContainerRegion? = null
    private var  eastRegion: ServerUiBorderContainerRegion? = null
    private var  westRegion: ServerUiBorderContainerRegion? = null

    override fun setNorthRegion(region: ServerUiBorderContainerRegion) {
        northRegion  = region
        if(component != null){
            setNorthInternal()
        }
    }

    private fun setRegionInternal(northRegion: ServerUiBorderContainerRegion?, regionCls:KClass<*> , factory: () -> LayoutRegion) {
        ArrayList(component!!.getChildren<Component>()).forEach {
            if(it::class == regionCls){
                component!!.removeChild(it)
            }
        }

        if(northRegion == null){
            return
        }
        val north = factory.invoke()


        north.title = northRegion.title
        if(north !is North) {
            northRegion.width?.let { north.width = it }
        }
        northRegion.height?.let { north.height = it}
        if(north !is Center) {
            north.isSplittable = northRegion.showSplitLine
            north.isCollapsible = northRegion.collapsible
            north.isOpen = !northRegion.collapsed
        } else {
            north.isAutoscroll = true
        }
        north.border = if(northRegion.showBorder) "normal" else "0"
        north.appendChild(findZkComponent(northRegion.content).getZkComponent())
        component!!.appendChild(north)
    }

    private fun setNorthInternal() {
        setRegionInternal(northRegion, North::class) { North() }
    }

    override fun setWestRegion(region: ServerUiBorderContainerRegion) {
        westRegion  = region
        if(component != null){
            setWestInternal()
        }
    }

    private fun setWestInternal() {
        setRegionInternal(westRegion, West::class){West()}
    }

    override fun setEastRegion(region: ServerUiBorderContainerRegion) {
        eastRegion  = region
        if(component != null){
            setEastInternal()
        }
    }

    private fun setEastInternal() {
        setRegionInternal(eastRegion, East::class){East()}
    }

    override fun setSouthRegion(region: ServerUiBorderContainerRegion) {
        southRegion  = region
        if(component != null){
            setSouthInternal()
        }
    }

    private fun setSouthInternal() {
        setRegionInternal(southRegion, South::class){South()}
    }

    override fun setCenterRegion(region: ServerUiBorderContainerRegion) {
        centerRegion  = region
        if(component != null){
            setCenterInternal()
        }
    }

    private fun setCenterInternal() {
        setRegionInternal(centerRegion, Center::class){Center()}
    }

    override fun getZkComponent(): HtmlBasedComponent {
        if(component != null){
            return component!!
        }
        component = Borderlayout()
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
        setNorthInternal()
        setSouthInternal()
        setEastInternal()
        setWestInternal()
        setCenterInternal()
        return component!!
    }

    override fun getParent(): ServerUiNode? {
        return parent
    }


}