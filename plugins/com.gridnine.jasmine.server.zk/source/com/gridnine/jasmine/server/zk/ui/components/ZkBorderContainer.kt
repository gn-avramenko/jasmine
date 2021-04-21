/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.components

import com.gridnine.jasmine.server.core.ui.components.BorderContainer
import com.gridnine.jasmine.server.core.ui.components.BorderContainerConfiguration
import com.gridnine.jasmine.server.core.ui.components.BorderContainerRegion
import com.gridnine.jasmine.server.zk.ui.components.ZkUiComponent
import com.gridnine.jasmine.server.zk.ui.components.configureBasicParameters
import com.gridnine.jasmine.server.zk.ui.components.findZkComponent
import org.zkoss.zk.ui.Component
import org.zkoss.zk.ui.HtmlBasedComponent
import org.zkoss.zul.*
import kotlin.reflect.KClass

open class ZkBorderContainer(configure: BorderContainerConfiguration.() -> Unit) : BorderContainer, ZkUiComponent{

    private var component: Borderlayout? = null

    private var  northRegion: BorderContainerRegion? = null
    private var  southRegion: BorderContainerRegion? = null
    private var  centerRegion: BorderContainerRegion? = null
    private var  eastRegion: BorderContainerRegion? = null
    private var  westRegion: BorderContainerRegion? = null

    private val config = BorderContainerConfiguration()
    init {
        config.configure()
    }
    
    override fun setNorthRegion(configure: BorderContainerRegion.()->Unit) {
        val region = BorderContainerRegion()
        region.configure()
        northRegion  = region
        if(component != null){
            setNorthInternal()
        }
    }

    private fun setRegionInternal(northRegion: BorderContainerRegion?, regionCls:KClass<*> , factory: () -> LayoutRegion) {
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

    override fun setWestRegion(configure: BorderContainerRegion.()->Unit) {
        val region = BorderContainerRegion()
        region.configure()
        westRegion  = region
        if(component != null){
            setWestInternal()
        }
    }

    private fun setWestInternal() {
        setRegionInternal(westRegion, West::class){West()}
    }

    override fun setEastRegion(configure: BorderContainerRegion.()->Unit) {
        val region = BorderContainerRegion()
        region.configure()
        eastRegion  = region
        if(component != null){
            setEastInternal()
        }
    }

    private fun setEastInternal() {
        setRegionInternal(eastRegion, East::class){East()}
    }

    override fun setSouthRegion(configure: BorderContainerRegion.()->Unit) {
        val region = BorderContainerRegion()
        region.configure()
        southRegion  = region
        if(component != null){
            setSouthInternal()
        }
    }

    private fun setSouthInternal() {
        setRegionInternal(southRegion, South::class){South()}
    }

    override fun setCenterRegion(configure: BorderContainerRegion.()->Unit) {
        val region = BorderContainerRegion()
        region.configure()
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
        configureBasicParameters(component!!, config)
        setNorthInternal()
        setSouthInternal()
        setEastInternal()
        setWestInternal()
        setCenterInternal()
        return component!!
    }

}