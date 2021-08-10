/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jtasks
 *****************************************************************/
package com.gridnine.jasmine.web.antd.components

import com.gridnine.jasmine.web.core.ui.components.*
import kotlin.js.Date

class AntdWebPasswordBox(private val configure: WebPasswordBoxConfiguration.()->Unit) : WebPasswordBox, BaseAntdWebUiComponent() {

    private val config = WebPasswordBoxConfiguration()

    private var value:String? = null

    private var enabled = true

    private var validationMessage:String? = null
    init {
        config.configure()
    }

    override fun createReactElementWrapper(): ReactElementWrapper {
        return ReactFacade.createProxy{callbackIndex:Int ->
            val props = object {}.asDynamic()
            props.allowClear = true
            props.disabled = !enabled
            props.value = value
            props.style = object {}.asDynamic()
            config.width?.let { props.style.width = it }
            config.height?.let { props.style.height = it }
            if (validationMessage != null) {
                props.className = "jasmine-input-error"
            }
            ReactFacade.callbackRegistry.get(callbackIndex).onChange =  { e:dynamic ->
                value = e.target.value
                maybeRedraw()
            }
            props.iconRender = {visible:Boolean ->
                if(visible){
                    ReactFacade.createElement(ReactFacade.IconEyeTwoTone, object{})
                } else {
                    ReactFacade.createElement(ReactFacade.IconEyeInvisibleOutlined, object{})
                }
            }
            props.onChange = {e:dynamic -> ReactFacade.callbackRegistry.get(callbackIndex).onChange(e)}
            if (validationMessage != null) {
                ReactFacade.createElementWithChildren(ReactFacade.Tooltip, object {
                    val title = validationMessage
                }, ReactFacade.createElement(ReactFacade.PasswordBox, props))
            } else {
                ReactFacade.createElement(ReactFacade.PasswordBox, props)
            }
        }
    }

    override fun getValue(): String? {
        return value
    }

    override fun setValue(value: String?) {
        if(value != this.value){
            this.value = value
            maybeRedraw()
        }
    }

    override fun setDisabled(value: Boolean) {
        if(enabled != !value){
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