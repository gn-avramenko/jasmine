/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jtasks
 *****************************************************************/
package com.gridnine.jasmine.web.antd.components

import com.gridnine.jasmine.web.core.remote.launch
import com.gridnine.jasmine.web.core.ui.components.*
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import kotlin.js.Date

class AntdWebPanel(private val configure: WebPanelConfiguration.()->Unit) : WebPanel, BaseAntdWebUiComponent() {

    private val config = WebPanelConfiguration()

    private var value = false

    private var enabled = true

    private var title:String? = null

    private val id = "panel${MiscUtilsJS.createUUID()}"

    private var handler: (suspend (String, WebPanel) -> Unit)? = null

    init {
        config.configure()
    }

    override fun createReactElementWrapper(): ReactElementWrapper {
        return ReactFacade.createProxy{callbackIndex:Int ->
            val headerProps = js("{}")
            headerProps.className = "jasmine-panel-header"
            val toolsDivProps = js("{}")
            val toolsDivStyle = js("{}")
            toolsDivProps.style = toolsDivStyle
            toolsDivStyle.float = "right"
            val titleProps = js("{}")
            titleProps.className = "jasmine-panel-header-title"
            val headerDiv = ReactFacade.createElementWithChildren("div", headerProps,
                arrayOf(ReactFacade.createElementWithChildren("div", titleProps, title?:"" ),
                    ReactFacade.createElementWithChildren("span", toolsDivProps, config.tools.map { config->
                        val buttonProps = js("{}")
                        buttonProps.icon = AntdWebLinkButton.getElementForIcon(config.icon)
                        buttonProps.size = "small"
                        val method = "onTool${config.id}"
                        ReactFacade.callbackRegistry.get(callbackIndex)[method] = {
                            handler?.let {
                                launch {
                                    it.invoke(config.id, AntdWebPanel@this)
                                }
                            }
                        }
                        buttonProps.onClick = {
                            ReactFacade.callbackRegistry.get(callbackIndex)[method]()
                        }
                        ReactFacade.createElement(ReactFacade.Button, buttonProps)
                    }.toTypedArray() )))
            val containerProps = js("{}")
            val containerStyle = js("{}")
            containerProps.style = containerStyle
            if(config.fit){
                containerStyle.width = "100%"
                containerStyle.height = "100%"
            } else {
                config.width?.let { containerStyle.width = it }
                config.height?.let { containerStyle.height = it }
            }
            containerProps.className = "jasmine-panel-container"
            ReactFacade.createElementWithChildren("div", containerProps, arrayOf(headerDiv, findAntdComponent(config.content).getReactElement()))
        }
    }

    override fun setTitle(title: String) {
        if(this.title != title){
            this.title = title
            maybeRedraw()
        }
    }

    override fun setToolHandler(handler: suspend (String, WebPanel) -> Unit) {
        this.handler = handler
    }

    override fun getId(): String? {
        return id
    }


}