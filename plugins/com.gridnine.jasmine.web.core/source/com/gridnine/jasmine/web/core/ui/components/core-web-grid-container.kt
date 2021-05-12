/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.components

import com.gridnine.jasmine.web.core.utils.MiscUtilsJS

interface WebGridLayoutContainer : WebNode,HasId

class WebGridLayoutCell(val comp:WebNode?, val columnSpan:Int =1)

class WebGridContainerConfiguration:BaseWebComponentConfiguration(){
    var uid = MiscUtilsJS.createUUID()
    val columns = arrayListOf<WebGridColumnConfiguration>()
    val rows = arrayListOf<GridRow>()
    var noPadding = false
    fun column(width:String?){
        columns.add(WebGridColumnConfiguration(width))
    }
    fun row(height:String? = null, configure:GridRow.()->Unit){
        val hRow = GridRow(WebGridRowConfiguration(height))
        rows.add(hRow)
        hRow.configure()
    }
}

class WebGridColumnConfiguration(val width:String?)

class WebGridRowConfiguration(val height:String?)

class GridRow(val config:WebGridRowConfiguration){
    val cells = arrayListOf<WebGridLayoutCell>()
    fun cell(comp:WebNode? = null, columnSpan:Int =1){
        cells.add(WebGridLayoutCell(comp, columnSpan))
    }
}
