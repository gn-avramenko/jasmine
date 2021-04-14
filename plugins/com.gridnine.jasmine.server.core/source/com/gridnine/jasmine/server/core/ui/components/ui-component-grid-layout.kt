/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.ui.components

import com.gridnine.jasmine.server.core.ui.common.BaseComponentConfiguration
import com.gridnine.jasmine.server.core.ui.common.UiNode

interface GridLayoutContainer: UiNode {

    fun addRow(height:String?=null)

    fun addCell(cell: GridLayoutCell)
}


class GridLayoutCell(val comp: UiNode?, val columnSpan:Int =1, val sClass:String? = null)

class GridLayoutContainerConfiguration: BaseComponentConfiguration(){
    val columns = arrayListOf<GridLayoutColumnConfiguration>()
    var noPadding = false
}

class GridLayoutColumnConfiguration(val width:String?=null)

class GridLayoutRowConfiguration(val height:String?)

class GridLayoutRow(val config: GridLayoutRowConfiguration){
    val cells = arrayListOf<GridLayoutCell>()
}
