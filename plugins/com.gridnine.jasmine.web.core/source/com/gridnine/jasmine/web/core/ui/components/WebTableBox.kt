/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.components

import com.gridnine.jasmine.web.core.ui.WebComponent

interface WebTableBox : WebComponent{
    fun addRow(position:Int?, components:List<WebComponent?>)
    fun removeRow(position: Int)
    fun moveRow(fromPosition:Int, toPosition:Int)
    fun getRows():List<List<WebComponent?>>
}

class WebTableBoxConfiguration{
    var width:String? = null
    var height:String? = null
    val headerComponents = arrayListOf<WebComponent?>()
    val columnWidths = arrayListOf<WebTableBoxColumnWidth>()
}

class WebTableBoxColumnWidth(val min:Int?, val pref:Int?, val max:Int?)