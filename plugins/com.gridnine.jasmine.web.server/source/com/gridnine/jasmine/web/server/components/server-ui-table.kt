/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.components

interface ServerUiTable : ServerUiNode {
    fun addRow(position:Int?, components:List<ServerUiTableCell>)
    fun removeRow(position: Int)
    fun moveRow(fromPosition:Int, toPosition:Int)
    fun getRows():List<List<ServerUiNode?>>
}

class ServerUiTableConfiguration(){
    constructor(config:ServerUiTableConfiguration.()->Unit):this(){
        config.invoke(this)
    }
    var width:String? = null
    var height:String? = null
    var noHeader  = false
    val columns = arrayListOf<ServerUiTableColumnDescription>()
}

class ServerUiTableColumnDescription(val label:String?, val minWidth:Int?,  val prefWidth:Int?, val maxWidth:Int?)


class ServerUiTableCell(val component: ServerUiNode?, val colspan:Int =1)

