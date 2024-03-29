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

class AntdWebRemoteSelect(private val config: WebSelectConfiguration) : WebSelect, BaseAntdWebUiComponent() {

    private val values = arrayListOf<SelectItemJS>()

    private val currentOptions = arrayListOf<SelectItemJS>()

    private var validationMessage: String? = null

    private var enabled = true

    private var changeListener: (suspend (List<SelectItemJS>) -> Unit)? = null

    private  var loader: (suspend (String?) -> List<SelectItemJS>)? = null

    override fun createReactElementWrapper(parentIndex:Int?): ReactElementWrapper {
        return ReactFacade.createProxy(parentIndex) {parentIndexValue:Int?, childIndex:Int ->
            val props = js("{}")
            if(config.multiple){
                props.mode = "multiple"
            } else {
                props.showSearch = "true"
            }
            props.allowClear = config.showClearIcon
            props.disabled = !enabled
            props.style = js("{}")
            config.width?.let { props.style.width = it }
            config.height?.let { props.style.height = it }
            if (validationMessage != null) {
                props.className = "jasmine-input-error"
            }
            props.value =  values.map {
                val res = js("{}")
                res.key = it.id
                res.label = it.text
                res.value = it.id
                res
            }.toTypedArray()
            props.showArrow = config.hasDownArrow
            ReactFacade.getCallbacks(parentIndexValue, childIndex).fetchOptions = {value:String?, success:dynamic ->
                loader?.let {
                    launch {
                        val opts = it.invoke(value)
                        currentOptions.clear()
                        currentOptions.addAll(opts)
                        success(opts.map { object {
                            val value = it.id
                            val label = it.text
                        } }.toTypedArray())
                    }
                }
            }
            props.fetchOptions = {value:String?, success:dynamic ->
                ReactFacade.getCallbacks(parentIndexValue, childIndex).fetchOptions(value, success)
            }
            ReactFacade.getCallbacks(parentIndexValue, childIndex).onChange = { newValues:Any? ->
                values.clear()
                if(newValues is Array<*>){
                    newValues as Array<dynamic>
                    values.addAll(newValues.map {SelectItemJS(it.key, it.label)})
                } else if (newValues != null){
                    val dv = newValues.asDynamic()
                    values.add(SelectItemJS(dv.key, dv.label))
                }
                validationMessage = null
                maybeRedraw()
                changeListener?.let {
                    launch {
                        it.invoke(values)
                    }
                }
            }
            props.onChange = { newValues:Any? ->
                ReactFacade.getCallbacks(parentIndexValue, childIndex).onChange(newValues)
            }
            if (validationMessage != null) {
                ReactFacade.createElementWithChildren(ReactFacade.Tooltip, object {
                    val title = validationMessage
                }, ReactFacade.createElement(ReactFacade.DebounceSelect, props))
            } else {
                ReactFacade.createElement(ReactFacade.DebounceSelect, props)
            }
        }
    }

    override fun setLoader(loader: suspend (String?) -> List<SelectItemJS>) {
        this.loader = loader
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
        throw XeptionJS.forDeveloper("possible values are not supported in local mode")
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