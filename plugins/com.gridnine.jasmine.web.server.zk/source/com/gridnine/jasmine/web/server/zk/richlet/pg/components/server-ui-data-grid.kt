/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.richlet.pg.components

import com.gridnine.jasmine.web.server.zk.richlet.pg.ServerUiComponent
import com.gridnine.jasmine.web.server.zk.richlet.pg.ServerUiComponentHorizontalAlignment


interface ServerUiDataGridComponent<E:Any>:ServerUiComponent {
    fun setLoader(loader: (ServerUiDataGridRequest) -> ServerUiDataGridResponse<E>)
    fun setFormatter(formatter: (item:E, fieldId:String)->String?)
    fun setDoubleClickListener(listener: ((item:E)->Unit)?)
    fun updateData()
    fun getSelected():List<E>
}

data class ServerUiDataGridRequest(var offSet: Int = 0, var limit: Int = 0, var desc:Boolean? = false, var sortColumn: String? = null)

data class ServerUiDataGridResponse<E:Any>(val count:Int, val data:List<E>)

class ServerUiDataGridComponentConfiguration{
    var span = false
    var initSortingColumn:String? = null
    var initSortingOrderAsc = false
    var selectable = false
    var width:String? = null
    var height:String? = null
    val columns = arrayListOf<ServerUiDataGridColumnConfiguration>()
}

class ServerUiDataGridColumnConfiguration{
    lateinit var fieldId:String
    lateinit var title:String
    var width:String? = null
    var sortable = true
    var horizontalAlignment: ServerUiComponentHorizontalAlignment? = null
}





