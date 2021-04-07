/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.components


interface ServerUiDataGridComponent<E:Any>: ServerUiNode {
    fun setLoader(loader: (ServerUiDataGridRequest) -> ServerUiDataGridResponse<E>)
    fun setFormatter(formatter: (item:E, fieldId:String)->String?)
    fun setDoubleClickListener(listener: ((item:E)->Unit)?)
    fun updateData()
    fun getSelected():List<E>
}

data class ServerUiDataGridRequest(var offSet: Int = 0, var limit: Int = 0, var desc:Boolean? = false, var sortColumn: String? = null)

data class ServerUiDataGridResponse<E:Any>(val count:Int, val data:List<E>)

class ServerUiDataGridComponentConfiguration(){
    constructor(config: ServerUiDataGridComponentConfiguration.()->Unit):this(){
        config.invoke(this)
    }
    var span = false
    var initSortingColumn:String? = null
    var initSortingOrderAsc = false
    var selectable = false
    var width:String? = null
    var height:String? = null
    val columns = arrayListOf<ServerUiDataGridColumnConfiguration>()
}

class ServerUiDataGridColumnConfiguration(){
    constructor(config: ServerUiDataGridColumnConfiguration.()->Unit):this(){
        config.invoke(this)
    }
    lateinit var fieldId:String
    lateinit var title:String
    var width:String? = null
    var sortable = true
    var horizontalAlignment: ServerUiComponentHorizontalAlignment? = null
}





