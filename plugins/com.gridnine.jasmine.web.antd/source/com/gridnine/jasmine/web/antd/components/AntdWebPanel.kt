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

    override fun createReactElementWrapper(parentIndex:Int?): ReactElementWrapper {
        return ReactFacade.createProxy(parentIndex){parentIndexValue:Int?, childIndex:Int ->
            val headerProps = js("{}")
            headerProps.className = "jasmine-panel-header"
            headerProps.style = js("{}")
            headerProps.style.gridColumn = "1"
            headerProps.style.gridRow = "1"
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
                        ReactFacade.getCallbacks(parentIndexValue, childIndex)[method] = {
                            handler?.let {
                                launch {
                                    it.invoke(config.id, AntdWebPanel@this)
                                }
                            }
                        }
                        buttonProps.onClick = {
                            ReactFacade.getCallbacks(parentIndexValue, childIndex)[method]()
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
            containerStyle.style = js("{}")
            containerStyle.style.display = "grid"
            containerStyle.style.gridTemplateColumns = "1fr"
            containerStyle.style.gridTemplateRows = "auto 1fr"

            val elementWrapperProps = js("{}")
            elementWrapperProps.style = js("{}")
            elementWrapperProps.style.gridColumn = "1"
            elementWrapperProps.style.gridRow = "2"
            ReactFacade.createElementWithChildren("div", containerProps, arrayOf(headerDiv, ReactFacade.createElementWithChildren("div", elementWrapperProps, findAntdComponent(config.content).getReactElement(parentIndex))))
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