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

class EasyUiWebSelect(configure: WebSelectConfiguration.() -> Unit) : WebSelect,EasyUiComponent {

    private var initialized = false

    private val uid = MiscUtilsJS.createUUID()

    private var jq: dynamic = null
    private val selectedValues = arrayListOf<SelectItemJS>()
    private val localData = arrayListOf<SelectItemJS>()
    private var loader: (suspend (String) -> List<SelectItemJS>)? = null
    private var validationMessage: String? = null
    private var enabled = true
    private var changeListener:(suspend (List<SelectItemJS>) ->Unit)? = null
    private var ignoreChange = false

    private val config = WebSelectConfiguration()
    init {
        config.configure()
    }

    override fun getId(): String {
        return "select${uid}"
    }

    override fun getHtml(): String {
        return "<input  id=\"select${uid}\" style=\"${getSizeAttributes(config)}\"/>"
    }

    override fun setLoader(loader: suspend (String) -> List<SelectItemJS>) {
        this.loader = loader
    }

    override fun getValues(): List<SelectItemJS> {
        if (!initialized) {
            return selectedValues
        }
        val data = jq.select2("val")
        return if (config.multiple) {
            (data as Array<*>).mapNotNull {  toSelectItem(it as String) }
        } else {
            if (MiscUtilsJS.isBlank(data)) emptyList() else arrayListOf(toSelectItem(data))
        }
    }

    override fun setValues(values: List<SelectItemJS>) {
        selectedValues.clear()
        selectedValues.addAll(values.map{si ->
            localData.find { it.id == si.id }?:si
        })
        if (!initialized) {
            return
        }
        ignoreChange = true
        if (config.multiple) {
            jq.select2("val", selectedValues.map { "${it.id}||${it.text}" }.toTypedArray())
        } else {
            jq.select2("val", if (selectedValues.isEmpty()) null else "${selectedValues[0].id}||${selectedValues[0].text}")
        }
        jq.trigger("change")
        ignoreChange =false
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

    override fun setPossibleValues(values: List<SelectItemJS>) {
        if(localData.containsAll(values) && values.containsAll(localData)){
            return
        }
        localData.clear()
        localData.addAll(values)
    }



    override fun decorate() {
        jq = jQuery("#select$uid")
        val options = object {
            val language = "ru"
            val multiple = config.multiple
            val allowClear = config.showClearIcon
            val placeholder = if (!config.multiple && config.showClearIcon) "Выберите значение" else null
        }.asDynamic()
        options.ajax = object {
            val data = { term: String ->
                term
            }
            val results = { data: dynamic ->
                data
            }
            val transport = { params: dynamic ->
                if(config.mode == SelectDataType.REMOTE) {
                    launch {
                        val items = this@EasyUiWebSelect.loader!!.invoke(params.data)
                        params.success(object {
                            val results = items.map { obj -> toItem(obj) }.toTypedArray()
                        })
                    }
                } else {
                    params.success(object {
                        val results = localData.map { obj -> toItem(obj) }.toTypedArray()
                    })
                }
            }
        }
        options.initSelection = { element: dynamic, callback: dynamic ->
            val id = element.`val`()
            if (MiscUtilsJS.isNotBlank(id)) {
                val si = toSelectItem(id)
                callback(object {
                    val id = id
                    val text = si.text
                })
            }
        }
        jq.select2(options)
        jq.on("change"){ event:dynamic ->
            if(!ignoreChange){
                val values = event.`val`
                selectedValues.clear()
                if(values is Array<String>){
                    values.forEach { item ->
                        if(!MiscUtilsJS.isBlank(item)) {
                            selectedValues.add(toSelectItem(item))
                        }
                    }
                }
                if(values is String){
                    if(!MiscUtilsJS.isBlank(values)) {
                        selectedValues.add(toSelectItem(values))
                    }
                }
                changeListener?.let {
                    launch {
                        it.invoke(selectedValues)
                    }
                }
            }
        }
        enableInternal()
        showValidationInternal()
        initialized = true
        setValues(ArrayList(selectedValues))
    }


    override fun showValidation(value: String?) {
        if (value != validationMessage) {
            validationMessage = value
            if (initialized) {
                showValidationInternal()
            }
        }
    }

    private fun showValidationInternal() {
        val par = jq.parent().find(".select2-container")
        par.removeClass("select2-jasmine-regular")
        par.removeClass("select2-jasmine-error")
        if (validationMessage != null) {
            par.addClass("select2-jasmine-error")
            par.tooltip(object {
                val content = validationMessage
            })
            return
        }
        par.addClass("select2-jasmine-regular")
        par.tooltip("destroy")
    }

    override fun setEnabled(value: Boolean) {
        if (enabled != value) {
            enabled = value
            if (initialized) {
                enableInternal()
            }
        }
    }

    private fun enableInternal() {
        jq.prop("disabled", !enabled)
    }

    override fun destroy() {
        jq.select2("destroy")
    }

    override fun setChangeListener(value:suspend (List<SelectItemJS>) ->Unit) {
        changeListener = value
    }

}