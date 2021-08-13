/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jtasks
 *****************************************************************/
package com.gridnine.jasmine.web.antd.components

import com.gridnine.jasmine.web.core.remote.launch
import com.gridnine.jasmine.web.core.ui.components.StandardMenuItem
import com.gridnine.jasmine.web.core.ui.components.WebMenuButton
import com.gridnine.jasmine.web.core.ui.components.WebMenuButtonConfiguration

class AntdWebMenuButton(configure: WebMenuButtonConfiguration.() -> Unit) : WebMenuButton, BaseAntdWebUiComponent() {

    private val config = WebMenuButtonConfiguration()

    private var visible = true

    private var handlers = hashMapOf<String, suspend () -> Unit>()

    private var enabledItemsMap = hashMapOf<String, Boolean>()

    private var menuEnabled = true

    init {
        config.configure()
    }

    override fun createReactElementWrapper(): ReactElementWrapper {
        return ReactFacade.createProxy { callbackIndex ->
            if (!visible) {
                ReactFacade.createElement(ReactFacade.Fragment, object {})
            } else {
                val menuProps = js("{}")
                menuProps.disabled = !menuEnabled

                ReactFacade.callbackRegistry.get(callbackIndex).onClick = { event: dynamic ->
                    val key = event.key as String
                    handlers[key]?.let {
                        launch {
                            it.invoke()
                        }
                    }
                }
                menuProps.onClick = { event: dynamic ->
                    ReactFacade.callbackRegistry.get(callbackIndex).onClick(event)
                }
                val menu = ReactFacade.createElementWithChildren(ReactFacade.Menu, menuProps,
                    config.elements.filter { it is StandardMenuItem }.map {
                        it as StandardMenuItem
                        val menuItemProps = js("{}")
                        menuItemProps.key = it.id
                        ReactFacade.createElementWithChildren(ReactFacade.MenuItem, menuItemProps, it.title?:"")
                    }.toTypedArray()
                )
                val dropdownProps = js("{}")
                dropdownProps.overlay = menu
                dropdownProps.placement = "bottomLeft"
                val buttonProps =  js("{}")
                if(config.icon != null){
                    buttonProps.icon = AntdWebLinkButton.getElementForIcon(config.icon!!)
                }
                val dropdown = ReactFacade.createElementWithChildren(
                    ReactFacade.Dropdown, dropdownProps,
                    if(config.title != null) {
                        ReactFacade.createElementWithChildren(ReactFacade.Button, buttonProps, config.title!!)
                    } else {
                        ReactFacade.createElement(ReactFacade.Button, buttonProps)
                    }
                )
                dropdown
            }
        }
    }

    override fun setVisible(value: Boolean) {
        if (visible != value) {
            visible = value
            maybeRedraw()
        }
    }

    override fun setHandler(id: String, handler: suspend () -> Unit) {
        handlers[id] = handler
    }

    override fun setEnabled(id: String, value: Boolean) {
        if (enabledItemsMap[id] != value) {
            enabledItemsMap[id] = value
            maybeRedraw()
        }
    }

    override fun setEnabled(value: Boolean) {
        if (menuEnabled != value) {
            menuEnabled = value
            maybeRedraw()
        }
    }
}