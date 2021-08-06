/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jtasks
 *****************************************************************/
package com.gridnine.jasmine.web.antd.components

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
    override fun createReactElementWrapper(): ReactElementWrapper {

        return ReactFacade.createProxy{
            if(!visible){
                ReactFacade.createElement(ReactFacade.Fragment, object{})
            } else {
                val props = js("{}")
                props.disabled = !this.enabled
                props.style = object {
                    val display = if (visible) "inline-block" else "none"
                }.asDynamic()
                if (config.height != null) {
                    props.style.height = config.height
                }
                if (config.width != null) {
                    props.style.width = config.width
                }
                props.onClick = {
                    if (this.handler != null) {
                        launch {
                            this.handler!!.invoke()
                        }
                    }
                }
                ReactFacade.createElementWithChildren(ReactFacade.Button, props, config.title!!)
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
}