/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("unused", "UnsafeCastFromDynamic")

package com.gridnine.jasmine.web.easyui.components

import com.gridnine.jasmine.common.core.model.SelectItemJS
import com.gridnine.jasmine.web.core.remote.launch
import com.gridnine.jasmine.web.core.ui.components.SelectDataType
import com.gridnine.jasmine.web.core.ui.components.WebSelect
import com.gridnine.jasmine.web.core.ui.components.WebSelectConfiguration
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS

class EasyUiWebCombobox(private val config: WebSelectConfiguration) : WebSelect,EasyUiComponent {

    private var jq:dynamic = null

    private var initialized = false

    private var loader:(suspend (String?) -> List<SelectItemJS>)? = null

    private val values = arrayListOf<SelectItemJS>()

    private val possibleValues = arrayListOf<SelectItemJS>()

    private val uuid = MiscUtilsJS.createUUID()

    private var error:String? = null

    private var enabled = true

    private var changeListener:(suspend (List<SelectItemJS>) -> Unit)? = null

    private var ignoreChangeEvent = false

    override fun setLoader(loader: suspend (String?) -> List<SelectItemJS>) {
        this.loader = loader
    }

    override fun getValues(): List<SelectItemJS> {
        if(!initialized){
            return values
        }
        return (jq.combobox("getValues") as Array<String>).map{toSelectItem(it)}
    }

    override fun setValues(values: List<SelectItemJS>) {
        this.values.clear()
        this.values.addAll(values)
        if(initialized){
            setValuesInternal()
        }
    }

    private fun setValuesInternal() {
        ignoreChangeEvent = true
        try {
            jq.combobox("setValues", values.map {toItem(it)}.toTypedArray())
        } finally {
            ignoreChangeEvent = false
        }
    }

    override fun setPossibleValues(values: List<SelectItemJS>) {
        possibleValues.clear()
        possibleValues.addAll(values)
    }

    override fun showValidation(value: String?) {
        if(error != value) {
            error = value
            if (initialized) {
                showValidationInternal()
            }
        }
    }

    private fun showValidationInternal() {
        val spanElm = jq.parent()
        if(MiscUtilsJS.isBlank(error)){
            spanElm.css("border-color", "")
            spanElm.removeAttr("title")
        } else {
            spanElm.css("border-color", "#d9534f")
            spanElm.attr("title", error)
        }
    }

    override fun setEnabled(value: Boolean) {
        if(enabled != value){
            enabled = value
            if(initialized){
                setEnabledInternal()
            }
        }
    }

    private fun setEnabledInternal() {
        if(enabled){
            jq.combobox("enable")
        } else {
            jq.combobox("disable")
        }
    }

    override fun setChangeListener(value: suspend (List<SelectItemJS>) -> Unit) {
        changeListener = value
    }

    override fun getId(): String {
        return "combobox${uuid}"
    }

    override fun getHtml(): String {
        return "<input id=\"${getId()}\">"
    }

    override fun decorate() {
        jq = jQuery("#${getId()}")
        jq.combobox(object{
            val valueField ="id"
            val textField = "text"
            val mode = "remote"
            val panelHeight ="auto"
            val limitToList = true
            val onShowPanel ={
                jq.combobox("reload")
            }
            val loader = {param:dynamic,success:dynamic,_:dynamic ->
                if(config.mode == SelectDataType.LOCAL){
                    success(possibleValues.filter { param.t == null || it.text.contains(param.t as String)}.map { toItem(it) }.toTypedArray())
                } else{
                    if(!initialized){
                        success(values.map { toItem(it) }.toTypedArray())
                    } else {
                        launch {
                            val values = this@EasyUiWebCombobox.loader!!.invoke(param.q as String?)
                            success(values.map { toItem(it) }.toTypedArray())
                        }
                    }
                }
            }
            val onChange = { newValue:String?, _:dynamic ->
                if(!ignoreChangeEvent) {
                    this@EasyUiWebCombobox.changeListener?.let {
                        launch {
                            it.invoke(if(newValue ==null) arrayListOf<SelectItemJS>() else arrayListOf(toSelectItem(newValue)))
                        }
                    }
                }
            }
        })
        initialized = true
        setValuesInternal()
        setEnabledInternal()
        showValidationInternal()
    }

    private fun toSelectItem(id: String): SelectItemJS {
        return SelectItemJS(id.substringBeforeLast("||"), id.substringAfterLast("||"))
    }

    private fun toItem(it: SelectItemJS): dynamic {
        return object {
            val id = "${it.id}||${it.text}"
            val text = it.text
        }
    }

    override fun destroy() {
        if(initialized){
            jq.combobox("destroy")
        }
    }

}