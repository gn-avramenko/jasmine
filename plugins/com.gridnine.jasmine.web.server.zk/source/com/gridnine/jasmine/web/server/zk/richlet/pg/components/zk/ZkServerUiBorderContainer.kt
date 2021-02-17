/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.richlet.pg.components.zk

import com.gridnine.jasmine.web.server.zk.richlet.pg.ServerUiComponent
import com.gridnine.jasmine.web.server.zk.richlet.pg.ZkServerUiComponent
import com.gridnine.jasmine.web.server.zk.richlet.pg.components.ServerUiBorderContainer
import com.gridnine.jasmine.web.server.zk.richlet.pg.components.ServerUiBorderContainerConfiguration
import com.gridnine.jasmine.web.server.zk.richlet.pg.components.ServerUiBorderContainerRegion
import org.zkoss.zk.ui.HtmlBasedComponent
import org.zkoss.zul.*

open class ZkServerUiBorderContainer(private val config:ServerUiBorderContainerConfiguration) : ServerUiBorderContainer, ZkServerUiComponent(){

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

    private fun setRegionInternal(northRegion: ServerUiBorderContainerRegion?, factory: () -> LayoutRegion) {
        if(northRegion == null){
            return
        }
        val north = factory.invoke()
        north.title = northRegion.title
        northRegion.width?.let { north.width = it }
        northRegion.height?.let { north.height = it}
        if(north !is Center) {
            north.isSplittable = northRegion.showSplitLine
            north.isCollapsible = northRegion.collapsible
            north.isOpen = !northRegion.collapsed
        }
        north.border = if(northRegion.showBorder) "normal" else "0"
        north.appendChild((northRegion.content as ZkServerUiComponent).getComponent())
        component!!.appendChild(north)
    }

    private fun setNorthInternal() {
        setRegionInternal(northRegion) { North() }
    }

    override fun setWestRegion(region: ServerUiBorderContainerRegion) {
        westRegion  = region
        if(component != null){
            setWestInternal()
        }
    }

    private fun setWestInternal() {
        setRegionInternal(westRegion){West()}
    }

    override fun setEastRegion(region: ServerUiBorderContainerRegion) {
        eastRegion  = region
        if(component != null){
            setEastInternal()
        }
    }

    private fun setEastInternal() {
        setRegionInternal(eastRegion){East()}
    }

    override fun setSouthRegion(region: ServerUiBorderContainerRegion) {
        southRegion  = region
        if(component != null){
            setSouthInternal()
        }
    }

    private fun setSouthInternal() {
        setRegionInternal(southRegion){South()}
    }

    override fun setCenterRegion(region: ServerUiBorderContainerRegion) {
        centerRegion  = region
        if(component != null){
            setCenterInternal()
        }
    }

    private fun setCenterInternal() {
        setRegionInternal(centerRegion){Center()}
    }

    override fun getComponent(): HtmlBasedComponent {
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

    override fun getParent(): ServerUiComponent? {
        return parent
    }


}