/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jtasks
 *****************************************************************/
package com.gridnine.jasmine.web.antd.components

import com.gridnine.jasmine.web.core.ui.components.*
import kotlin.js.Date

class AntdWebBooleanBox(private val configure: WebBooleanBoxConfiguration.()->Unit) : WebBooleanBox, BaseAntdWebUiComponent() {

    private val config = WebBooleanBoxConfiguration()

    private var value = false

    private var enabled = true

    init {
        config.configure()
    }

    override fun createReactElementWrapper(): ReactElementWrapper {
        return ReactFacade.createProxy{callbackIndex:Int ->
            val props = js("{}")
            props.disabled = !enabled
            props.checked = value
            props.style =  js("{}")
            props.checkedChildren = config.onText
            props.unCheckedChildren = config.offText
            config.width?.let { props.style.width = it }
            config.height?.let { props.style.height = it }
            ReactFacade.callbackRegistry.get(callbackIndex).onChange =  { checked:Boolean ->
                value = checked
                maybeRedraw()
            }
            props.onChange = {checked:Boolean -> ReactFacade.callbackRegistry.get(callbackIndex).onChange(checked)}
            ReactFacade.createElement(ReactFacade.Switch, props)
        }
    }

    override fun getValue(): Boolean {
        return value
    }

    override fun setValue(value: Boolean) {
        if(value != this.value){
            this.value = value
            maybeRedraw()
        }
    }

    override fun setEnabled(value: Boolean) {
        if(enabled != value){
            enabled = value
            maybeRedraw()
        }
    }
}