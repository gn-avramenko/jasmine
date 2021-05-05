/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("unused")

package com.gridnine.jasmine.web.easyui.components

import com.gridnine.jasmine.common.core.model.BaseIntrospectableObjectJS
import com.gridnine.jasmine.web.core.remote.launch
import com.gridnine.jasmine.web.core.ui.components.*
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS

class EasyUiWebDataGrid<E:BaseIntrospectableObjectJS>(configure: WebDataGridConfiguration<E>.()->Unit) :WebDataGrid<E>,EasyUiComponent{
    private var initialized = false

    private var jq:dynamic = null
    private val uid = MiscUtilsJS.createUUID()
    private  var loader: (suspend (WebDataGridRequest)-> WebDataGridResponse<E>)? = null
    private var dblClickListener:(suspend (E)-> Unit)? = null
    private var localData:List<E>? = null
    private var selectionChangeListener:(()->Unit)? = null
    private val config = WebDataGridConfiguration<E>()
    init {
        config.configure()
    }


    override fun getHtml(): String {
        return "<div id=\"dataGrid${uid}\" style=\"${if(config.width != null) "width:${config.width}" else ""};${if(config.height != null) "height:${config.height}" else ""}\"></div>"
    }

    override fun decorate() {
        jq = jQuery("#dataGrid$uid")
        val colls = arrayListOf<Any>()
        config.columns.forEach {cd ->
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
                val width = cd.width
                val resizable = cd.resizable
                val formatter = cd.formatter
            })
        }
        val options = object {
            val fit = config.fit
            val columns = arrayOf(colls.toTypedArray())
            val pagination = config.showPagination
            val fitColumns = config.fitColumns
            val singleSelect = true
            val onDblClickRow = {_:dynamic, row:dynamic ->
                this@EasyUiWebDataGrid.dblClickListener?.let {
                    launch {
                        it.invoke(row)
                    }
                }
            }
            val onSelect = {_:dynamic, _:dynamic ->
                selectionChangeListener?.invoke()
            }
            val onUnselect = {_:dynamic, _:dynamic ->
                selectionChangeListener?.invoke()
            }
        }.asDynamic()
        if(config.dataType == DataGridDataType.REMOTE) {
            options.loader = { params: dynamic, success: dynamic, _: dynamic ->

                val request = WebDataGridRequest()
                request.sortColumn = params.sort
                request.desc = "desc" == params.order
                request.rows = params.rows
                request.page = params.page
                launch {
                    val response = this@EasyUiWebDataGrid.loader!!.invoke(request)
                    success(object {
                        val rows = response.data.toTypedArray()
                        val total = response.count
                    })
                }
            }
        }
        jq.datagrid(options)
        initialized = true
        if(config.dataType == DataGridDataType.LOCAL){
            setLocalDataInternal()
        }
    }

    override fun destroy() {
        //noops
    }


    override fun setLoader(loader: suspend (WebDataGridRequest) -> WebDataGridResponse<E>) {
        this.loader = loader
    }

    override fun reload() {
        jq.datagrid("reload")
    }

    override fun setRowDblClickListener(listener: suspend (E) -> Unit) {
        this.dblClickListener = listener
    }

    override fun getSelected(): List<E> {
        if(!initialized){
            return emptyList()
        }
        val array = jq.datagrid("getSelections") as Array<E>
        return array.toList()
    }

    override fun setLocalData(data: List<E>) {
        localData = data
        if(initialized){
            setLocalDataInternal()
        }
    }

    private fun setLocalDataInternal(){
        if(localData != null){
            jq.datagrid("loadData", localData!!.toTypedArray())
        }
    }

    override fun setSelectionChangeListener(value: () -> Unit) {
        selectionChangeListener = value
    }

}
