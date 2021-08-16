/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jtasks
 *****************************************************************/
package com.gridnine.jasmine.web.antd.components

import com.gridnine.jasmine.web.core.remote.launch
import com.gridnine.jasmine.web.core.ui.components.*
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.NodeList

class AntdWebTabsContainer(configure: WebTabsContainerConfiguration.() -> Unit) : WebTabsContainer,
    BaseAntdWebUiComponent() {

    private val config = WebTabsContainerConfiguration()

    private val tabs = arrayListOf<WebTabPanel>()

    private val parentIndexes = hashMapOf<WebTabPanel, Int>()

    private var activeTabId: String? = null

    private val uuid = MiscUtilsJS.createUUID()

    init {
        config.configure()
    }
    private var tabExtraContent: ReactElement? = null

    override fun createReactElementWrapper(parentIndex:Int?): ReactElementWrapper {
        if(config.tools.isNotEmpty() && tabExtraContent == null){
           tabExtraContent = ReactFacade.createProxy(parentIndex){ parentIndexValue:Int?, childIndex:Int ->
                val menuProps = js("{}")
               ReactFacade.getCallbacks(parentIndexValue, childIndex).onClick = { event: dynamic ->
                    val key = event.key as String
                    launch {
                        config.tools[key.toInt()].handler.invoke()
                    }
                }
                menuProps.onClick = { event: dynamic ->
                    ReactFacade.getCallbacks(parentIndexValue, childIndex).onClick(event)
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
            }.element
        }
        return ReactFacade.createProxyAdvanced(parentIndex, { parentIndexValue:Int?, childIndex:Int ->
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
            props.tabPosition = when(config.tabsPositions){
                WebTabsPosition.TOP -> "top"
                WebTabsPosition.BOTTOM -> "bottom"
            }
            ReactFacade.getCallbacks(parentIndexValue, childIndex).onChange = { key: String ->
                activeTabId = key
                maybeRedraw()
            }
            props.onChange = { key: String ->
                ReactFacade.getCallbacks(parentIndexValue, childIndex).onChange(key)
            }
            props.type = "editable-card"
            props.activeKey = activeTabId
            ReactFacade.getCallbacks(parentIndexValue, childIndex).onEdit = { targetKey: String, action: String ->
                if (action == "remove") {
                    removeTab(targetKey)
                }
            }
            props.onEdit = { targetKey: String, action: String ->
                ReactFacade.getCallbacks(parentIndexValue, childIndex).onEdit(targetKey, action)
            }
            props.style = style

            if (config.tools.isNotEmpty()) {
                val extraContentProps = js("{}")
                extraContentProps.left = tabExtraContent
                props.tabBarExtraContent = extraContentProps
            }
            ReactFacade.createElementWithChildren(ReactFacade.Tabs, props, tabs.map {
                val paneProps = js("{}")
                paneProps.tab = it.title
                paneProps.key = it.id
                paneProps.closable = it.closable
                val paneStyle = js("{}")
                paneStyle.minHeight = "0px"
                paneProps.style = paneStyle
                ReactFacade.createElementWithChildren(
                    ReactFacade.TabPane,
                    paneProps,
                    findAntdComponent(it.content).getReactElement(if(parentIndex == null) parentIndexes[it] else parentIndex)
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
        val tabPanelContainer = tabsComp.querySelector(".ant-tabs-content-top")?:tabsComp.querySelector(".ant-tabs-content-bottom")
        tabPanelContainer.style.widht = "100%"
        tabPanelContainer.style.height = "100%"
        tabPanelContainer.style.overflowX ="auto"
        tabPanelContainer.style.overflowY ="auto"
    }

    override fun addTab(configure: WebTabPanel.() -> Unit) {
        val panel = WebTabPanel()
        panel.configure()
        tabs.add(panel)
        activeTabId = panel.id
        parentIndexes[panel] = ReactFacade.incrementAndGetCallbackIndex()
        maybeRedraw()
    }

    override fun removeTab(id: String) {
        val tab = tabs.find { it.id == id }
        tabs.remove(tab)
        activeTabId = if (tabs.isNotEmpty()) tabs[0].id else null
        maybeRedraw()
        val index = parentIndexes[tab]
        parentIndexes.remove(tab)
        window.setTimeout({
            ReactFacade.callbackRegistry.delete(index)
        }, 1000)
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