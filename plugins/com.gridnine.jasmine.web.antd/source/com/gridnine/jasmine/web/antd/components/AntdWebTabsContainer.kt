/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jtasks
 *****************************************************************/
package com.gridnine.jasmine.web.antd.components

import com.gridnine.jasmine.web.core.remote.launch
import com.gridnine.jasmine.web.core.ui.components.WebNode
import com.gridnine.jasmine.web.core.ui.components.WebTabPanel
import com.gridnine.jasmine.web.core.ui.components.WebTabsContainer
import com.gridnine.jasmine.web.core.ui.components.WebTabsContainerConfiguration
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.NodeList

class AntdWebTabsContainer(configure: WebTabsContainerConfiguration.() -> Unit) : WebTabsContainer,
    BaseAntdWebUiComponent() {

    private val config = WebTabsContainerConfiguration()

    private val tabs = arrayListOf<WebTabPanel>()

    private var activeTabId: String? = null

    private val uuid = MiscUtilsJS.createUUID()

    init {
        config.configure()
    }

    override fun createReactElementWrapper(): ReactElementWrapper {
        return ReactFacade.createProxyAdvanced({ callbackIndex ->
            val style = js("{}")
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
            val props = js("{}")
            props.id = uuid
            props.hideAdd = true
            ReactFacade.callbackRegistry.get(callbackIndex).onChange = { key: String ->
                activeTabId = key
                maybeRedraw()
            }
            props.onChange = { key: String ->
                ReactFacade.callbackRegistry.get(callbackIndex).onChange(key)
            }
            props.type = "editable-card"
            props.activeKey = activeTabId
            ReactFacade.callbackRegistry.get(callbackIndex).onEdit = { targetKey: String, action: String ->
                if (action == "remove") {
                    removeTab(targetKey)
                }
            }
            props.onEdit = { targetKey: String, action: String ->
                ReactFacade.callbackRegistry.get(callbackIndex).onEdit(targetKey, action)
            }
            props.style = style

            if (config.tools.isNotEmpty()) {
                val proxy = ReactFacade.createProxy { toolsCalbackIndex ->
                    val menuProps = js("{}")
                    ReactFacade.callbackRegistry.get(toolsCalbackIndex).onClick = { event: dynamic ->
                        val key = event.key as String
                        launch {
                            config.tools[key.toInt()].handler.invoke()
                        }
                    }
                    menuProps.onClick = { event: dynamic ->
                        ReactFacade.callbackRegistry.get(toolsCalbackIndex).onClick(event)
                    }
                    val menu = ReactFacade.createElementWithChildren(
                        ReactFacade.Menu,
                        menuProps,
                        config.tools.withIndex().map {
                            val menuItemProps = js("{}")
                            menuItemProps.key = it.index.toString()
                            ReactFacade.createElementWithChildren(
                                ReactFacade.MenuItem,
                                menuItemProps,
                                it.value.displayName
                            )
                        }.toTypedArray()
                    )
                    val dropDownProps = js("{}")
                    dropDownProps.overlay = menu
                    dropDownProps.placement = "bottomLeft"
                    val buttonProps = js("{}")
                    buttonProps.size = "large"
                    val dropdown = ReactFacade.createElementWithChildren(ReactFacade.Dropdown, dropDownProps,
                        ReactFacade.createElementWithChildren(ReactFacade.Button, buttonProps, "Настройки"))
                    dropdown
                }
                val extraContentProps = js("{}")
                extraContentProps.left = proxy.element
                props.tabBarExtraContent = extraContentProps
            }
            ReactFacade.createElementWithChildren(ReactFacade.Tabs, props, tabs.map {
                val paneProps = js("{}")
                paneProps.tab = it.title
                paneProps.key = it.id
                val paneStyle = js("{}")
                paneStyle.minHeight = "0px"
                paneProps.style = paneStyle
                ReactFacade.createElementWithChildren(
                    ReactFacade.TabPane,
                    paneProps,
                    findAntdComponent(it.content).getReactElement()
                )
            }.toTypedArray())

        }, object {
            val componentDidMount = {
                updateHeight()
            }
        })

    }

    private fun updateHeight() {
        val tabsComp = document.getElementById(uuid).asDynamic()
        val tabPanelContainer = tabsComp.querySelector(".ant-tabs-content-top")
        tabPanelContainer.style.widht = "100%"
        tabPanelContainer.style.height = "100%"
    }

    override fun addTab(configure: WebTabPanel.() -> Unit) {
        val panel = WebTabPanel()
        panel.configure()
        tabs.add(panel)
        activeTabId = panel.id
        maybeRedraw()
    }

    override fun removeTab(id: String) {
        tabs.removeAll { it.id == id }
        activeTabId = if (tabs.isNotEmpty()) tabs[0].id else null
        maybeRedraw()
    }

    override fun select(id: String): WebNode? {
        val node = tabs.find { it.id == id }
        if (activeTabId == id) {
            return node?.content
        }
        activeTabId = id
        maybeRedraw()
        return node?.content
    }

    override fun getTabs(): List<WebTabPanel> {
        return tabs
    }

    override fun setTitle(tabId: String, title: String) {
        val node = tabs.find { it.id == tabId } ?: return
        if (node.title != title) {
            node.title = title
            maybeRedraw()
        }
    }
}