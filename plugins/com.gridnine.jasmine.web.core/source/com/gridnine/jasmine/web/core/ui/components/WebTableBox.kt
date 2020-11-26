/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.components

import com.gridnine.jasmine.web.core.ui.WebComponent

interface WebTableBox : WebComponent{
    fun addRow(position:Int?, row:WebTableBoxRow)
    fun removeRow(position: Int)
    fun getRows():List<WebTableBoxRow>
}

class WebTableBoxRow(val components:List<WebComponent>, val tools:WebComponent?)

class WebTableBoxConfiguration{
    var width:String? = null
    var height:String? = null
    var showHeader = true
    val headerCellsTitles = arrayListOf<String>()
    val headerCellsWidths = arrayListOf<String?>()
    var showToolsColumn = true
    var toolsColumnMaxWidth:String? = null
}

