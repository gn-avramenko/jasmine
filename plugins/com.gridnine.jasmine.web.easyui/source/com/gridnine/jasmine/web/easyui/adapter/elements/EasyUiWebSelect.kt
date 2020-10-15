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
        return "<input id=\"select${uid}\" style=\"${if (width != null) "width:$width" else ""};${if (height != null) "height:$height" else ""}\"/>"
    }

    override fun setLoader(loader: (String) -> Promise<List<SelectItemJS>>) {
        this.loader = loader
    }

    override fun getValues(): List<SelectItemJS> {
        if (!initialized) {
            return selectedValues
        }
        val data = jq.select2("data")
        return if(multiple){
            (data as Array<*>).map { it.asDynamic() }.map { SelectItemJS(it.id, it.text) }
        } else{
            if(data == null) emptyList() else arrayListOf(SelectItemJS(data.id, data.text))
        }
    }

    override fun setValues(values: List<SelectItemJS>) {
        selectedValues.clear()
        selectedValues.addAll(values)
        if (!initialized) {
            return
        }
        if(mode == SelectDataType.LOCAL){
            if(multiple){
                jq.select2("val", values.map { it.id }.toTypedArray())
            } else {
                jq.select2("val", if(values.isEmpty()) null else values[0].id)
            }
            jq.trigger("change")
        }
        //jq.select2("val", null)
//        values.forEach {
//            val option = createSelect2Option(it.id, it.text, false, true)
//            jq.append(option)
//        }
//        jq.trigger("change")
    }

    private fun toItem(it: SelectItemJS):dynamic {
        return object{
            val id = it.id
            val text = it.text
        }
    }

    override fun setPossibleValues(values: List<SelectItemJS>) {
        localData.clear()
        localData.addAll(values)
        if (initialized) {
            jq.select2("data", localData.map { toItem(it)}.toTypedArray())
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
            val placeholder = if(!this@EasyUiWebSelect.multiple && this@EasyUiWebSelect.showClearIcon) CoreWebMessagesJS.selectItem else null
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
                            val results = it.map { obj -> toItem(obj)}.toTypedArray()
                        })
                    }
                }
            }
        } else {
            options.data = localData.map {toItem(it)}.toTypedArray()
        }
        jq.select2(options)
        jq.parent().find(".select2-container").addClass("select2-jasmine-regular")
//        jq.parent().find(".select2-container").tooltip(object{
//            val content = "This is the tooltip message"
//        })
        initialized = true
        setValues(ArrayList(selectedValues))
    }

}