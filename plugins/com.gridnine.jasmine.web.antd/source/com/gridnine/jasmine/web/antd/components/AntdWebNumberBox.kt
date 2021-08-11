/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jtasks
 *****************************************************************/
package com.gridnine.jasmine.web.antd.components

import com.gridnine.jasmine.web.core.ui.components.*
import kotlin.js.Date

class AntdWebNumberBox(private val configure: WebNumberBoxConfiguration.()->Unit) : WebNumberBox, BaseAntdWebUiComponent() {

    private val config = WebNumberBoxConfiguration()

    private var value:Double? = null

    private var enabled = true

    private var validationMessage:String? = null
    init {
        config.configure()
    }

    override fun createReactElementWrapper(): ReactElementWrapper {
        return ReactFacade.createProxy{callbackIndex:Int ->
            val props = js("{}")
            props.disabled = !enabled
            props.value = value
            props.style =  js("{}")
            props.precision = config.precision
            config.width?.let { props.style.width = it }
            config.height?.let { props.style.height = it }
            if (validationMessage != null) {
                props.className = "jasmine-input-error"
            }
            ReactFacade.callbackRegistry.get(callbackIndex).onChange =  { number:Any? ->
                if(number is Number){
                    value = number.toDouble()
                } else if(number is String){
                    value = number.toDouble()
                } else {
                    value = null
                }
                maybeRedraw()
            }
            props.onChange = {e:dynamic -> ReactFacade.callbackRegistry.get(callbackIndex).onChange(e)}
            if (validationMessage != null) {
                ReactFacade.createElementWithChildren(ReactFacade.Tooltip, object {
                    val title = validationMessage
                }, ReactFacade.createElement(ReactFacade.InputNumber, props))
            } else {
                ReactFacade.createElement(ReactFacade.InputNumber, props)
            }
        }
    }

    override fun getValue(): Double? {
        return value
    }

    override fun setValue(value: Double?) {
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
    override fun showValidation(value: String?) {
        if(value != validationMessage){
            validationMessage = value
            maybeRedraw()
        }
    }

}