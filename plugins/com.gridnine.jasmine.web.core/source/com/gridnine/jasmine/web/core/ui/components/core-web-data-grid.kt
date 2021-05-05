/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("unused")

package com.gridnine.jasmine.web.core.ui.components

import com.gridnine.jasmine.common.core.model.BaseIntrospectableObjectJS

interface WebDataGrid<E:BaseIntrospectableObjectJS>: WebNode{
    fun setLoader(loader: suspend (WebDataGridRequest)->WebDataGridResponse<E>)
    fun setLocalData(data:List<E>)
    fun getSelected():List<E>
    fun setSelectionChangeListener(value:()->Unit)
    fun setRowDblClickListener(listener: suspend (E)->Unit)
    fun reload()
}

class WebDataGridConfiguration<E:BaseIntrospectableObjectJS>:BaseWebComponentConfiguration(){
    var fit:Boolean = true
    val columns = arrayListOf<WebDataGridColumnConfiguration<E>>()
    var showPagination = false
    var dataType = DataGridDataType.REMOTE
    var fitColumns = false
    fun column(init:WebDataGridColumnConfiguration<E>.()->Unit){
        val col = WebDataGridColumnConfiguration<E>()
        col.init()
        columns.add(col)
    }
}

class WebDataGridColumnConfiguration<E:Any>{
    lateinit var fieldId:String
    lateinit var title:String
    var width:Int? = null
    var sortable = true
    var resizable = true
    var horizontalAlignment:WebDataHorizontalAlignment? = null
    var formatter:((value:Any?, row:E, index:Int)->String?)? = null
}

class WebDataGridRequest {
    var page: Int = 0
    var rows: Int = 0
    var desc = false
    var sortColumn: String? = null
}

class WebDataGridResponse<E:BaseIntrospectableObjectJS>(val count:Long, val data:List<E>)

enum class DataGridDataType{
    LOCAL,
    REMOTE
}