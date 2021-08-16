/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jtasks
 *****************************************************************/
package com.gridnine.jasmine.web.antd.components

import com.gridnine.jasmine.web.core.ui.components.WebDateBox
import com.gridnine.jasmine.web.core.ui.components.WebDateBoxConfiguration
import kotlin.js.Date

class AntdWebDateBox(private val configure: WebDateBoxConfiguration.()->Unit) : WebDateBox, BaseAntdWebUiComponent() {

    private val config = WebDateBoxConfiguration()

    private var value:Date? = null

    private var enabled = true

    private var validationMessage:String? = null
    init {
        config.configure()
    }

    override fun createReactElementWrapper(parentIndex:Int?): ReactElementWrapper {
        return ReactFacade.createProxy(parentIndex){parentIndexValue:Int?, childIndex:Int ->
            val props = js("{}")
            props.allowClear = true
            props.disabled = !enabled
            props.value = ReactFacade.dateToMoment(value)
            props.style = js("{}")
            config.width?.let { props.style.width = it }
            config.height?.let { props.style.height = it }
            if (validationMessage != null) {
                props.className = "jasmine-input-error"
            }
            ReactFacade.getCallbacks(parentIndexValue, childIndex).onChange = { newValue:dynamic ->
                value = ReactFacade.momentToDate(newValue)
                maybeRedraw()
            }
            props.onChange = {e:dynamic -> ReactFacade.getCallbacks(parentIndexValue, childIndex).onChange(e)}

            if (validationMessage != null) {
                ReactFacade.createElementWithChildren(ReactFacade.Tooltip, object {
                    val title = validationMessage
                }, ReactFacade.createElement(ReactFacade.DatePicker, props))
            } else {
                ReactFacade.createElement(ReactFacade.DatePicker, props)
            }
        }
    }

    override fun getValue(): Date? {
        return value
    }

    override fun setValue(value: Date?) {
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