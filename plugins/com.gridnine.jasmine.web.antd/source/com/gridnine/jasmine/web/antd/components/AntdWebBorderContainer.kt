/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jtasks
 *****************************************************************/
package com.gridnine.jasmine.web.antd.components

import com.gridnine.jasmine.web.core.ui.components.WebBorderContainer
import com.gridnine.jasmine.web.core.ui.components.WebBorderContainerConfiguration
import com.gridnine.jasmine.web.core.ui.components.WebBorderLayoutRegionConfiguration

class AntdWebBorderContainer(configure: WebBorderContainerConfiguration.()->Unit):WebBorderContainer,BaseAntdWebUiComponent() {

    private val config = WebBorderContainerConfiguration()

    private val regions = hashMapOf<WebBorderRegionType, WebBorderLayoutRegionData>()

    init {
        config.configure()
    }

    override fun createReactElementWrapper(): ReactElementWrapper {
        return ReactFacade.createProxy{
            val children = arrayListOf<ReactElement>()
            val eastRegion = regions[WebBorderRegionType.EAST]
            if(eastRegion != null){
                if(eastRegion.element == null){
                    val props = js("{}")
                    props.className = "jasmine-layout-common"
                    if(eastRegion.config.showBorder || eastRegion.config.showSplitLine){
                        props.className+= " jasmine-layout-left-border"
                    }
                    if(eastRegion.config.width != null){
                        props.width =eastRegion.config.width
                    }
                    eastRegion.element = ReactFacade.createElementWithChildren(ReactFacade.LayoutSider, props, arrayOf(
                        findAntdComponent(eastRegion.config.content).getReactElement()))
                }
                children.add(eastRegion.element!!)
            }
            val westRegion = regions[WebBorderRegionType.WEST]
            if(westRegion != null){
                if(westRegion.element == null){
                    val props = js("{}")
                    props.className = "jasmine-layout-common"
                    if(westRegion.config.showBorder || westRegion.config.showSplitLine){
                        props.className+= " jasmine-layout-right-border"
                    }
                    if(westRegion.config.width != null){
                        props.width =westRegion.config.width
                    }
                    westRegion.element = ReactFacade.createElementWithChildren(ReactFacade.LayoutSider, props, arrayOf(
                        findAntdComponent(westRegion.config.content).getReactElement()))
                }
                children.add(westRegion.element!!)
            }
             val northRegion = regions[WebBorderRegionType.NORTH]
             if(northRegion != null){
                if(northRegion.element == null){
                    val props = js("{}")
                    props.className = "jasmine-layout-common"
                    if(northRegion.config.showBorder || northRegion.config.showSplitLine){
                        props.className+= " jasmine-layout-bottom-border"
                    }
                    val style = js("{}")
                    props.style = style
                    if(northRegion.config.height != null){
                        style.height  = "${northRegion.config.height}px"
                        style["line-height"]  = "${northRegion.config.height}px"
                    }
                    northRegion.element = ReactFacade.createElementWithChildren(ReactFacade.LayoutHeader, props, arrayOf(
                        findAntdComponent(northRegion.config.content).getReactElement()))
                }
                 children.add(northRegion.element!!)
             }
            val southRegion = regions[WebBorderRegionType.SOUTH]
            if(southRegion != null){
                if(southRegion.element == null){
                    val props = js("{}")
                    props.className = "jasmine-layout-common"
                    if(southRegion.config.showBorder || southRegion.config.showSplitLine){
                        props.className+= " jasmine-layout-top-border"
                    }
                    val style = js("{}")
                    props.style = style
                    if(southRegion.config.height != null){
                        style.height  = "${southRegion.config.height}px"
                    }
                    southRegion.element = ReactFacade.createElementWithChildren(ReactFacade.LayoutFooter,props, arrayOf(
                        findAntdComponent(southRegion.config.content).getReactElement()))
                }
                children.add(southRegion.element!!)
            }
            val centerRegion = regions[WebBorderRegionType.CENTER]
            if(centerRegion != null){
                if(centerRegion.element == null){
                    val props = js("{}")
                    props.className = "jasmine-layout-common"
                    centerRegion.element = ReactFacade.createElementWithChildren(ReactFacade.LayoutContent, props, arrayOf(
                        findAntdComponent(centerRegion.config.content).getReactElement()))
                }
                children.add(centerRegion.element!!)
            }

            val props = js("{}")
            val style = js("{}")
            props.style = style
            if(config.fit){
                style.width = "100%"
                style.height = "100%"
            } else {
                if (config.width != null) {
                    style.width = config.width
                }
                if (config.height != null) {
                    style.height = config.height
                }
            }
            ReactFacade.createElementWithChildren(ReactFacade.Layout,props, children.toTypedArray())
        }
    }

    override fun setNorthRegion(configure: WebBorderLayoutRegionConfiguration.() -> Unit) {
        processRegion(WebBorderRegionType.NORTH, configure)
    }

    private fun processRegion(regionType: WebBorderRegionType, configure: WebBorderLayoutRegionConfiguration.() -> Unit) {
        val hc = WebBorderLayoutRegionConfiguration()
        hc.configure()
        regions[regionType] = WebBorderLayoutRegionData().also { it.config = hc }
        maybeRedraw()
    }

    override fun setWestRegion(configure: WebBorderLayoutRegionConfiguration.() -> Unit) {
        processRegion(WebBorderRegionType.WEST, configure)
    }

    override fun setEastRegion(configure: WebBorderLayoutRegionConfiguration.() -> Unit) {
        processRegion(WebBorderRegionType.EAST, configure)
    }

    override fun setSouthRegion(configure: WebBorderLayoutRegionConfiguration.() -> Unit) {
        processRegion(WebBorderRegionType.SOUTH, configure)
    }

    override fun setCenterRegion(configure: WebBorderLayoutRegionConfiguration.() -> Unit) {
        processRegion(WebBorderRegionType.CENTER, configure)
    }

    enum class WebBorderRegionType{
        NORTH,
        SOUTH,
        EAST,
        WEST,
        CENTER
    }

    class WebBorderLayoutRegionData{
        var element:ReactElement? = null
        var config = WebBorderLayoutRegionConfiguration()
    }
}