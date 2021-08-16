/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jtasks
 *****************************************************************/
package com.gridnine.jasmine.web.antd.components

import com.gridnine.jasmine.common.core.model.XeptionJS
import com.gridnine.jasmine.web.core.remote.launch
import com.gridnine.jasmine.web.core.ui.components.WebLinkButton
import com.gridnine.jasmine.web.core.ui.components.WebLinkButtonConfiguration

class AntdWebLinkButton(configure:WebLinkButtonConfiguration.()->Unit) : WebLinkButton, BaseAntdWebUiComponent(){
    private val config =WebLinkButtonConfiguration()

    private var handler: (suspend () -> Unit)? = null

    private var visible = true

    private var enabled = true

    init {
        config.configure()
    }
    override fun createReactElementWrapper(parentIndex:Int?): ReactElementWrapper {

        return ReactFacade.createProxy(parentIndex){parentIndexValue:Int?, childIndex:Int ->
            if(!visible){
                ReactFacade.createElement(ReactFacade.Fragment, object{})
            } else {
                val props = js("{}")
                props.disabled = !this.enabled
                props.style = js("{}")
                props.style.display = if (visible) "inline-block" else "none"
                config.icon?.let{
                    props.icon = getElementForIcon(it)
                }
                if (config.height != null) {
                    props.style.height = config.height
                }
                if (config.width != null) {
                    props.style.width = config.width
                }
                ReactFacade.getCallbacks(parentIndexValue, childIndex).onClick = {
                    if (this.handler != null) {
                        launch {
                            this.handler!!.invoke()
                        }
                    }
                }
                props.onClick = {ReactFacade.getCallbacks(parentIndexValue, childIndex).onClick()}
                if(config.title != null){
                    ReactFacade.createElementWithChildren(ReactFacade.Button, props, config.title!!)
                } else {
                    ReactFacade.createElement(ReactFacade.Button, props)
                }

            }
        }

    }

    override fun setHandler(handler: suspend () -> Unit) {
        this.handler = handler
    }


    override fun setEnabled(value: Boolean) {
        if(enabled != value){
            enabled = value
            maybeRedraw()
        }
    }

    override fun setVisible(value: Boolean) {
        if(visible != value){
            visible = value
            maybeRedraw()
        }
    }

    companion object {
        fun getElementForIcon(icon:String):ReactElement{
            return when(icon){
                "core:link" -> ReactFacade.createElement(ReactFacade.IconLinkOutlined, js("{}"))
                "core:close" -> ReactFacade.createElement(ReactFacade.IconCloseOutlined, js("{}"))
                "core:minus" -> ReactFacade.createElement(ReactFacade.IconMinusOutlined, js("{}"))
                "core:plus" -> ReactFacade.createElement(ReactFacade.IconPlusOutlined, js("{}"))
                "core:down" -> ReactFacade.createElement(ReactFacade.IconDownOutlined, js("{}"))
                "core:up" -> ReactFacade.createElement(ReactFacade.IconUpOutlined, js("{}"))
                else ->throw XeptionJS.forDeveloper("unsupported icon $icon")
            }
        }
    }
}