/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.ui.components

import com.gridnine.jasmine.server.core.ui.common.BaseComponentConfiguration
import com.gridnine.jasmine.server.core.ui.common.UiNode

interface Table : UiNode {
    fun addRow(position:Int?, components:List<TableCell>)
    fun removeRow(position: Int)
    fun moveRow(fromPosition:Int, toPosition:Int)
    fun getRows():List<List<UiNode?>>
}

class TableConfiguration: BaseComponentConfiguration(){
    var noHeader  = false
    val columns = arrayListOf<TableColumnDescription>()
}

class TableColumnDescription(val label:String?, val minWidth:Int?,  val prefWidth:Int?, val maxWidth:Int?)


class TableCell(val component: UiNode?, val colspan:Int =1)

