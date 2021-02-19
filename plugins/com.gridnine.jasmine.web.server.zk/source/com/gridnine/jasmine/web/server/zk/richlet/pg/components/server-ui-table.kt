/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.richlet.pg.components

import com.gridnine.jasmine.web.server.zk.richlet.pg.ServerUiComponent

interface ServerUiTable : ServerUiComponent{
    fun addRow(position:Int?, components:List<ServerUiTableCell>)
    fun removeRow(position: Int)
    fun moveRow(fromPosition:Int, toPosition:Int)
    fun getRows():List<List<ServerUiComponent?>>
}

class ServerUiTableConfiguration{
    var width:String? = null
    var height:String? = null
    var noHeader  = false
    val columns = arrayListOf<ServerUiTableColumnDescription>()
}

class ServerUiTableColumnDescription(val label:String?, val minWidth:Int?,  val prefWidth:Int?, val maxWidth:Int?)


class ServerUiTableCell(val component:ServerUiComponent? , val colspan:Int =1)

