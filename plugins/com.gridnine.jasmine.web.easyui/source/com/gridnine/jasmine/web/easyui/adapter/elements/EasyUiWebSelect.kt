/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.easyui.adapter.elements

import com.gridnine.jasmine.web.core.ui.WebComponent
import com.gridnine.jasmine.web.core.ui.components.ComboboxMode
import com.gridnine.jasmine.web.core.ui.components.SelectItemJS
import com.gridnine.jasmine.web.core.ui.components.WebSelect
import com.gridnine.jasmine.web.core.ui.components.WebSelectConfiguration
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import com.gridnine.jasmine.web.easyui.adapter.createTestSelect2
import com.gridnine.jasmine.web.easyui.adapter.jQuery
import kotlin.js.Promise

class EasyUiWebSelect(private val parent:WebComponent?, configure: WebSelectConfiguration.()->Unit) :WebSelect{

    private var initialized = false

    private val uid = MiscUtilsJS.createUUID()

    private var width:String? = null
    private var height:String? = null
    private val mode:ComboboxMode
    private var jq:dynamic = null
    private var editable = false
    private var showClearIcon = false
    private var hasDownArrow = true
    private var multiple = false
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
        return "<input id=\"select${uid}\" style=\"${if(width != null) "width:$width" else ""};${if(height != null) "height:$height" else ""}\"/>"
    }

    override fun setLoader(loader: (String) -> Promise<List<SelectItemJS>>) {
        this.loader = loader
    }


    override fun getParent(): WebComponent? {
        return parent
    }

    override fun getChildren(): List<WebComponent> {
        return emptyList()
    }


    override fun decorate() {
        jq = jQuery("#select$uid")
        val options = object{
            val language= "ru"
            val multiple=this@EasyUiWebSelect.multiple&&false
            val allowClear = true
            val placeholder = ""
        }.asDynamic()
        if(mode == ComboboxMode.REMOTE){
            options.ajax = object{
                val data = { term:String ->
                    term
                }
                val results = {data:dynamic ->
                    data
                }
                val transport = { params:dynamic ->
                    this@EasyUiWebSelect.loader!!.invoke(params.data).then {
                        params.success(object {
                            val results = it.map { obj-> object{
                               val id = obj.id
                               val text = obj.text
                            } }.toTypedArray()
                        })
                    }
                }
            }
        }
        jq.select2(options)
        jq.parent().find(".select2-container").addClass("select2-jasmine-regular")
        jq.parent().find(".select2-container").tooltip(object{
            val content = "This is the tooltip message"
        })
        initialized = true
    }

}