/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jtasks
 *****************************************************************/
package com.gridnine.jasmine.web.antd.components

import com.gridnine.jasmine.web.core.ui.components.WebBorderContainer
import com.gridnine.jasmine.web.core.ui.components.WebBorderContainerConfiguration
import com.gridnine.jasmine.web.core.ui.components.WebBorderLayoutRegionConfiguration

class AntdWebBorderContainer(configure: WebBorderContainerConfiguration.() -> Unit) : WebBorderContainer,
    BaseAntdWebUiComponent() {

    private val config = WebBorderContainerConfiguration()

    private val regions = hashMapOf<WebBorderRegionType, WebBorderLayoutRegionData>()

    private var siderData = hashMapOf<WebBorderRegionType, WebBorderSiderData>()

    init {
        config.configure()
    }

    override fun createReactElementWrapper(): ReactElementWrapper {
        return ReactFacade.createProxy {
            val children = arrayListOf<ReactElement>()

            val westRegion = regions[WebBorderRegionType.WEST]
            if (westRegion != null) {
                if (westRegion.element == null) {
                    westRegion.element = createSider(WebBorderRegionType.WEST, it)
                }
            }
            val northRegion = regions[WebBorderRegionType.NORTH]
            if (northRegion != null) {
                if (northRegion.element == null) {
                    val props = js("{}")
                    props.className = "jasmine-layout-common"
                    if (northRegion.config.showBorder || northRegion.config.showSplitLine) {
                        props.className += " jasmine-layout-bottom-border"
                    }
                    val style = js("{}")
                    props.style = style
                    if (northRegion.config.height != null) {
                        style.height = "${northRegion.config.height}px"
                        style["line-height"] = "${northRegion.config.height}px"
                    }
                    northRegion.element = ReactFacade.createElementWithChildren(
                        ReactFacade.LayoutHeader, props, arrayOf(
                            findAntdComponent(northRegion.config.content).getReactElement()
                        )
                    )
                }
            }

            val centerRegion = regions[WebBorderRegionType.CENTER]
            if (centerRegion != null) {
                if (centerRegion.element == null) {
                    val props = js("{}")
                    props.className = "jasmine-layout-common"
                    val style = js("{}")
                    props.style = style
                    style.overflowY = "auto"
                    style.overflowX = "auto"
                    style.minHeight = "0px"
                    centerRegion.element = ReactFacade.createElementWithChildren(
                        ReactFacade.LayoutContent, props, arrayOf(
                            findAntdComponent(centerRegion.config.content).getReactElement()
                        )
                    )
                }
            }
            val southRegion = regions[WebBorderRegionType.SOUTH]
            if (southRegion != null) {
                if (southRegion.element == null) {
                    val props = js("{}")
                    props.className = "jasmine-layout-common"
                    if (southRegion.config.showBorder || southRegion.config.showSplitLine) {
                        props.className += " jasmine-layout-top-border"
                    }
                    val style = js("{}")
                    props.style = style
                    if (southRegion.config.height != null) {
                        style.height = "${southRegion.config.height}px"
                    }
                    southRegion.element = ReactFacade.createElementWithChildren(
                        ReactFacade.LayoutFooter, props, arrayOf(
                            findAntdComponent(southRegion.config.content).getReactElement()
                        )
                    )
                }
            }
            val eastRegion = regions[WebBorderRegionType.EAST]
            if (eastRegion != null) {
                if (eastRegion.element == null) {
                    eastRegion.element = createSider(WebBorderRegionType.EAST,it)
                }
            }
            if (eastRegion == null && westRegion == null) {
                if (northRegion != null) {
                    children.add(northRegion.element!!)
                }
                if (centerRegion != null) {
                    children.add(centerRegion.element!!)
                }
                if (southRegion != null) {
                    children.add(southRegion.element!!)
                }
            } else if (eastRegion != null) {
                if (northRegion != null) {
                    children.add(northRegion.element!!)
                }
                children.add(
                    ReactFacade.createElementWithChildren(
                        ReactFacade.Layout, js("{}"), arrayOf(
                            centerRegion!!.element, eastRegion.element
                        )
                    )
                )
                if (southRegion != null) {
                    children.add(southRegion.element!!)
                }
            } else {
                if (northRegion != null) {
                    children.add(northRegion.element!!)
                }
                children.add(
                    ReactFacade.createElementWithChildren(
                        ReactFacade.Layout, js("{}"), arrayOf(
                            westRegion!!.element, centerRegion!!.element
                        )
                    )
                )
                if (southRegion != null) {
                    children.add(southRegion.element!!)
                }
            }
            val props = js("{}")
            val style = js("{}")
            props.style = style
            if (config.fit) {
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
            ReactFacade.createElementWithChildren(ReactFacade.Layout, props, children.toTypedArray())
        }
    }

    private fun createSider(type: WebBorderRegionType, callbackIndex:Int): ReactElement {
        val region = regions[type]!!
        if (!region.config.collapsible) {
            val props = createSiderProps(type)
            return ReactFacade.createElementWithChildren(
                ReactFacade.LayoutSider, props, arrayOf(
                    findAntdComponent(region.config.content).getReactElement()
                )
            )
        }
        var ref: dynamic = null
        val wrapper = ReactFacade.createProxy { _: Int ->
            val regionData = siderData.getOrPut(type) {
                WebBorderSiderData(region.config.collapsed)
            }
            var props = createSiderProps(type)
            val expandButtonProps = js("{}")
            expandButtonProps.style = js("{}")
            expandButtonProps.style.width = "100%"
            expandButtonProps.style.display = if (regionData.collapsed) "block" else "none"
            val expandFunctionName = "on${if (type == WebBorderRegionType.EAST) "East" else "West"}Expand"
            val expandButtonTitle = if (type == WebBorderRegionType.EAST) "<<" else ">>"

            ReactFacade.callbackRegistry.get(callbackIndex)[expandFunctionName] = {

                regionData.collapsed = false
                ref.current.forceRedraw()
            }
            expandButtonProps.onClick = {
                ReactFacade.callbackRegistry.get(callbackIndex)[expandFunctionName]()
            }

            val collapseButtonProps = js("{}")
            collapseButtonProps.style = js("{}")
            collapseButtonProps.style.width = "100%"
            val collapseFunctionName = "on${if (type == WebBorderRegionType.EAST) "East" else "West"}Collapse"
            val collapseButtonTitle = if (type == WebBorderRegionType.EAST) ">>" else "<<"
            ReactFacade.callbackRegistry.get(callbackIndex)[collapseFunctionName] = {
                regionData.collapsed = true
                ref.current.forceRedraw()
            }
            collapseButtonProps.onClick = {
                ReactFacade.callbackRegistry.get(callbackIndex)[collapseFunctionName]()
            }
            val fullPanelProps = js("{}")
            fullPanelProps.style = js("{}")
            fullPanelProps.style.width = config.width
            fullPanelProps.style.height = "100%"
            fullPanelProps.style.display = if (regionData.collapsed) "none" else "flex"
            val northPanelProps = js("{}")
            northPanelProps.className = "jasmine-layout-common"

            val centerPanelProps = js("{}")
            centerPanelProps.className = "jasmine-layout-common"
            val centerPanelStyle = js("{}")
            centerPanelProps.style = centerPanelStyle
            centerPanelStyle.overflowY = "auto"
            centerPanelStyle.overflowX = "auto"
            centerPanelStyle.minHeight = "0px"
            val headerContent = ReactFacade.createElementWithChildren(
                ReactFacade.LayoutHeader, northPanelProps,
                ReactFacade.createElementWithChildren(ReactFacade.Button, collapseButtonProps, collapseButtonTitle)
            )
            val centerContent = ReactFacade.createElementWithChildren(
                ReactFacade.LayoutContent,
                centerPanelProps,
                findAntdComponent(region.config.content).getReactElement()
            )
            ReactFacade.createElementWithChildren(
                ReactFacade.LayoutSider, props, arrayOf(
                    ReactFacade.createElementWithChildren(
                        ReactFacade.Layout, fullPanelProps,
                        arrayOf(headerContent, centerContent)
                    ), ReactFacade.createElementWithChildren(ReactFacade.Button, expandButtonProps, expandButtonTitle)
                )
            )
        }
        ref = wrapper.ref
        return wrapper.element
    }

    private fun createSiderProps(type: AntdWebBorderContainer.WebBorderRegionType): Any {
        val region = regions[type]!!
        val props = js("{}")
        props.className = "jasmine-layout-common"
        if (region.config.showBorder || region.config.showSplitLine) {
            props.className += if (type == WebBorderRegionType.EAST) " jasmine-layout-left-border" else " jasmine-layout-right-border"
        }
        if (region.config.width != null) {
            props.width = "${region.config.width}px"
        }
        val regionData = siderData.getOrPut(type) {
            WebBorderSiderData(region.config.collapsed)
        }
        props.collapsible = true
        props.reverseArrow = true
        props.collapsedWidth = 50
        props.collapsed = regionData.collapsed
        props.trigger = null
        return props
    }

    override fun setNorthRegion(configure: WebBorderLayoutRegionConfiguration.() -> Unit) {
        processRegion(WebBorderRegionType.NORTH, configure)
    }

    private fun processRegion(
        regionType: WebBorderRegionType,
        configure: WebBorderLayoutRegionConfiguration.() -> Unit
    ) {
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

    enum class WebBorderRegionType {
        NORTH,
        SOUTH,
        EAST,
        WEST,
        CENTER
    }

    class WebBorderLayoutRegionData {
        var element: ReactElement? = null
        var config = WebBorderLayoutRegionConfiguration()
    }

    data class WebBorderSiderData(
        var collapsed: Boolean,
    )
}