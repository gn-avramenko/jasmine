/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jtasks
 *****************************************************************/
package com.gridnine.jasmine.web.antd.components

import com.gridnine.jasmine.common.core.model.SelectItemJS
import com.gridnine.jasmine.common.core.model.XeptionJS
import com.gridnine.jasmine.web.core.remote.launch
import com.gridnine.jasmine.web.core.ui.components.WebSelect
import com.gridnine.jasmine.web.core.ui.components.WebSelectConfiguration

class AntdWebLocalSelect(private val config: WebSelectConfiguration) : WebSelect, BaseAntdWebUiComponent() {

    private val values = arrayListOf<SelectItemJS>()

    private val options = arrayListOf<SelectItemJS>()

    private var validationMessage: String? = null

    private var enabled = true

    private var changeListener: (suspend (List<SelectItemJS>) -> Unit)? = null

    override fun createReactElementWrapper(): ReactElementWrapper {
        return ReactFacade.createProxy {callbackIndex:Int ->
            val props = object {}.asDynamic()
            if(config.multiple){
                props.mode = "multiple"
            }
            props.allowClear = config.showClearIcon
            props.style = object {}.asDynamic()
            config.width?.let { props.style.width = it }
            config.height?.let { props.style.height = it }
            if (validationMessage != null) {
                props.className = "jasmine-input-error"
            }
            props.options = options.map {
                object {
                    val value = it.id
                    val label = it.text
                }
            }.toTypedArray()
            props.value =  values.map { it.id}.toTypedArray()
            props.showArrow = config.hasDownArrow
            ReactFacade.callbackRegistry.get(callbackIndex).onChange = { newValues:Any? ->
                values.clear()
                if(newValues is String){
                    options.find { it.id == newValues }?.let { values.add(it) }
                } else if(newValues is Array<*>){
                    values.addAll(newValues.mapNotNull { key ->
                        options.find { it.id == key }
                    })
                }
                validationMessage = null
                maybeRedraw()
                changeListener?.let {
                    launch {
                        it.invoke(values)
                    }
                }
            }
            props.onChange = {newValues:Any? -> ReactFacade.callbackRegistry.get(callbackIndex).onChange(newValues)}
            if (validationMessage != null) {
                ReactFacade.createElementWithChildren(ReactFacade.Tooltip, object {
                    val title = validationMessage
                }, ReactFacade.createElement(ReactFacade.Select, props))
            } else {
                ReactFacade.createElement(ReactFacade.Select, props)
            }
        }
    }

    override fun setLoader(loader: suspend (String?) -> List<SelectItemJS>) {
        throw XeptionJS.forDeveloper("loader is not supported in local mode")
    }

    override fun getValues(): List<SelectItemJS> {
        return values;
    }

    override fun setValues(values: List<SelectItemJS>) {
        if(this.values != values) {
            this.values.clear()
            this.values.addAll(values)
            maybeRedraw()
        }
    }

    override fun setPossibleValues(values: List<SelectItemJS>) {
        if (options != values) {
            options.clear()
            options.addAll(values)
            maybeRedraw()
        }
    }

    override fun showValidation(value: String?) {
        if (validationMessage != value) {
            this.validationMessage = value
            maybeRedraw()
        }
    }

    override fun setEnabled(value: Boolean) {
        if (enabled != value) {
            enabled = value
            maybeRedraw()
        }
    }

    override fun setChangeListener(value: suspend (List<SelectItemJS>) -> Unit) {
        changeListener = value
    }
}