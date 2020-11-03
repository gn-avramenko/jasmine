/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.components

import com.gridnine.jasmine.server.core.model.common.BaseIntrospectableObjectJS
import com.gridnine.jasmine.web.core.ui.HasDivId
import com.gridnine.jasmine.web.core.ui.WebComponent
import kotlin.js.Promise

interface WebDataGrid<E:BaseIntrospectableObjectJS>: WebComponent,HasDivId{
    fun setLoader(loader:(WebDataGridRequest)->Promise<WebDataGridResponse<E>>)
    fun setLocalData(data:List<E>)
    fun getSelected():List<E>
    fun setRowDblClickListener(listener:(E)->Unit)
    fun reload()
}

class WebDataGridConfiguration<E:BaseIntrospectableObjectJS>{
    var fit:Boolean = true
    var width:String? = null
    var height:String? = null
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