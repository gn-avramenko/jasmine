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
//                    if(westRegion.config.collapsible){
//                        props.collapsible = true
//                    }
//                    if(westRegion.config.collapsed){
//                        props.defaultCollapsed = true
//                    }
                    westRegion.element = ReactFacade.createElementWithChildren(ReactFacade.LayoutSider, props, arrayOf(
                        findAntdComponent(westRegion.config.content).getReactElement()))
                }
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
             }

            val centerRegion = regions[WebBorderRegionType.CENTER]
            if(centerRegion != null){
                if(centerRegion.element == null){
                    val props = js("{}")
                    props.className = "jasmine-layout-common"
                    val style = js("{}")
                    props.style = style
                    style.overflowY = "auto"
                    style.overflowX = "auto"
                    style.minHeight="0px"
                    centerRegion.element = ReactFacade.createElementWithChildren(ReactFacade.LayoutContent, props, arrayOf(
                        findAntdComponent(centerRegion.config.content).getReactElement()))
                }
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
            }
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
                    if(eastRegion.config.collapsible){
                        props.collapsible = true
                        props.reverseArrow = true
                        props.collapsedWidth = 10
                    }
                    if(eastRegion.config.collapsed){
                        props.defaultCollapsed = true
                    }
                    eastRegion.element = ReactFacade.createElementWithChildren(ReactFacade.LayoutSider, props, arrayOf(
                        findAntdComponent(eastRegion.config.content).getReactElement()))
                }
            }
            if(eastRegion == null && westRegion == null){
                if(northRegion != null) {
                    children.add(northRegion.element!!)
                }
                if(centerRegion != null) {
                    children.add(centerRegion.element!!)
                }
                if(southRegion != null) {
                    children.add(southRegion.element!!)
                }
            } else if (eastRegion != null){
                if(northRegion != null) {
                    children.add(northRegion.element!!)
                }
                children.add(ReactFacade.createElementWithChildren(ReactFacade.Layout, js("{}"), arrayOf(
                    centerRegion!!.element, eastRegion.element)))
                if(southRegion != null) {
                    children.add(southRegion.element!!)
                }
            }else{
                if(northRegion != null) {
                    children.add(northRegion.element!!)
                }
                children.add( ReactFacade.createElementWithChildren(ReactFacade.Layout, js("{}"), arrayOf(
                    westRegion!!.element, centerRegion!!.element)))
                if(southRegion != null) {
                    children.add(southRegion.element!!)
                }
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