/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.easyui.adapter.elements

import com.gridnine.jasmine.web.core.CoreWebMessagesJS
import com.gridnine.jasmine.web.core.ui.WebComponent
import com.gridnine.jasmine.web.core.ui.components.SelectDataType
import com.gridnine.jasmine.web.core.ui.components.SelectItemJS
import com.gridnine.jasmine.web.core.ui.components.WebSelect
import com.gridnine.jasmine.web.core.ui.components.WebSelectConfiguration
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import com.gridnine.jasmine.web.easyui.adapter.createSelect2Option
import com.gridnine.jasmine.web.easyui.adapter.jQuery
import kotlin.js.Promise

class EasyUiWebSelect(private val parent: WebComponent?, configure: WebSelectConfiguration.() -> Unit) : WebSelect {

    private var initialized = false

    private val uid = MiscUtilsJS.createUUID()

    private var width: String? = null
    private var height: String? = null
    private val mode: SelectDataType
    private var jq: dynamic = null
    private var editable = false
    private var showClearIcon = false
    private var hasDownArrow = true
    private var multiple = false
    private val selectedValues = arrayListOf<SelectItemJS>()
    private val localData = arrayListOf<SelectItemJS>()
    private var loader: ((String) -> Promise<List<SelectItemJS>>)? = null
    private var validationMessage: String? = null
    private var enabled = true
    private var changeListener:((List<SelectItemJS>) ->Unit)? = null
    private var ignoreChange = false

    init {
        (parent?.getChildren() as MutableList<WebComponent>?)?.add(this)
        val configuration = WebSelectConfiguration()
        configuration.configure()
        width = configuration.width
        height = configuration.height
        mode = configuration.mode
        editable = configuration.editable
        showClearIcon = configuration.showClearIcon
        hasDownArrow = configuration.hasDownArrow
        multiple = configuration.multiple
    }


    override fun getHtml(): String {
        return "<input  id=\"select${uid}\" style=\"${if (width != null) "width:$width" else ""};${if (height != null) "height:$height" else ""}\"/>"
    }

    override fun setLoader(loader: (String) -> Promise<List<SelectItemJS>>) {
        this.loader = loader
    }

    override fun getValues(): List<SelectItemJS> {
        if (!initialized) {
            return selectedValues
        }
        val data = jq.select2("data")
        return if (multiple) {
            (data as Array<*>).map { it.asDynamic() }.map { toSelectItem(it.id) }
        } else {
            if (data == null) emptyList() else arrayListOf(toSelectItem(data.id))
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
        if (multiple) {
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
        if (initialized) {
            destroy()
            jq.html(getHtml())
            decorate()
        }
    }


    override fun getParent(): WebComponent? {
        return parent
    }

    override fun getChildren(): List<WebComponent> {
        return emptyList()
    }


    override fun decorate() {
        jq = jQuery("#select$uid")
        val options = object {
            val language = "ru"
            val multiple = this@EasyUiWebSelect.multiple
            val allowClear = this@EasyUiWebSelect.showClearIcon
            val placeholder = if (!this@EasyUiWebSelect.multiple && this@EasyUiWebSelect.showClearIcon) CoreWebMessagesJS.selectItem else null
        }.asDynamic()
        if (mode == SelectDataType.REMOTE) {
            options.ajax = object {
                val data = { term: String ->
                    term
                }
                val results = { data: dynamic ->
                    data
                }
                val transport = { params: dynamic ->
                    this@EasyUiWebSelect.loader!!.invoke(params.data).then {
                        params.success(object {
                            val results = it.map { obj -> toItem(obj) }.toTypedArray()
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
        } else {
            options.data = localData.map { toItem(it) }.toTypedArray()
        }
        jq.select2(options)
        jq.on("change"){ event:dynamic ->
            if(!ignoreChange){
                val values = event.`val`
                selectedValues.clear()
                if(values is Array<String>){
                    values.forEach { item ->
                        selectedValues.add(toSelectItem(item))
                    }
                }
                if(values is String){
                    selectedValues.add(toSelectItem(values))
                }
               if(changeListener != null){
                   changeListener!!.invoke(selectedValues)
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

    override fun setChangeListener(value:((List<SelectItemJS>) ->Unit)?) {
        changeListener = value
    }

}