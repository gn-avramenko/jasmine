/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.easyui.adapter.elements

import com.gridnine.jasmine.server.core.model.common.BaseIntrospectableObjectJS
import com.gridnine.jasmine.web.core.StandardRestClient
import com.gridnine.jasmine.web.core.ui.WebComponent
import com.gridnine.jasmine.web.core.ui.components.*
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import com.gridnine.jasmine.web.easyui.adapter.jQuery
import kotlin.js.Promise

class EasyUiWebDataGrid<E:BaseIntrospectableObjectJS>(private val parent:WebComponent?, configure: WebDataGridConfiguration<E>.()->Unit) :WebDataGrid<E>{
    private var initialized = false
    private val fit:Boolean
    private val width:String?
    private val height:String?

    private var jq:dynamic = null
    private val uid = MiscUtilsJS.createUUID()
    private lateinit var loader:(WebDataGridRequest)-> Promise<WebDataGridResponse<E>>
    private val showPagination:Boolean

    private val columnsDescriptions:List<WebDataGridColumnConfiguration<E>>
    init {
        (parent?.getChildren() as MutableList<WebComponent>?)?.add(this)
        val configuration = WebDataGridConfiguration<E>()
        configuration.configure()
        fit = configuration.fit
        width = configuration.width
        height = configuration.height
        columnsDescriptions = configuration.columns
        showPagination = configuration.showPagination
    }
    override fun getParent(): WebComponent? {
        return parent
    }

    override fun getChildren(): List<WebComponent> {
        return emptyList()
    }

    override fun getHtml(): String {
        return "<div id=\"dataGrid${uid}\" style=\"${if(width != null) "width:$width" else ""};${if(height != null) "height:$height" else ""}\"></div>"
    }

    override fun decorate() {
        jq = jQuery("#dataGrid$uid")
        val colls = arrayListOf<Any>()
        columnsDescriptions.forEach {cd ->
            colls.add(object {
                val field = cd.fieldId
                val title = cd.title
                val sortable = cd.sortable
                val align = when(cd.horizontalAlignment){
                    WebDataHorizontalAlignment.LEFT -> "left"
                    WebDataHorizontalAlignment.RIGHT -> "right"
                    WebDataHorizontalAlignment.CENTER -> "center"
                    null -> "left"
                }
                val resizable = cd.resizable
                val formatter = cd.formatter
            })
        }
        jq.datagrid(object {
            val fit = this@EasyUiWebDataGrid.fit
            val columns = arrayOf(colls.toTypedArray())
            val pagination = showPagination
            val loader = { params: dynamic, success: dynamic, _: dynamic ->

                val request = WebDataGridRequest()
                request.sortColumn = params.sort
                request.desc = "desc" == params.order
                request.rows = params.rows
                request.page = params.page
                this@EasyUiWebDataGrid.loader.invoke(request).then {response ->
                    success(object{
                        val rows = response.data.toTypedArray()
                        val total = response.count
                    })
                }
            }
        })
        initialized = true
    }

    override fun setLoader(loader: (WebDataGridRequest) -> Promise<WebDataGridResponse<E>>) {
        this.loader = loader
    }

    override fun reload() {
        jq.datagrid("reload")
    }

}
