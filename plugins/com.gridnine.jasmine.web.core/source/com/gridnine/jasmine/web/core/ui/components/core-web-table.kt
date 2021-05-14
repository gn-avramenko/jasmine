/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.components


interface WebTableBox : WebNode{
    fun addRow(position:Int?, components:List<WebTableBoxCell>)
    fun removeRow(position: Int)
    fun moveRow(fromPosition:Int, toPosition:Int)
    fun getRows():List<List<WebNode?>>
}

class WebTableBoxConfiguration:BaseWebComponentConfiguration(){
    val headerComponents = arrayListOf<WebNode?>()
    val columnWidths = arrayListOf<WebTableBoxColumnWidth>()
}

class WebTableBoxColumnWidth(val min:Int?, val pref:Int?, val max:Int?)

class WebTableBoxCell(val component:WebNode? , val colspan:Int =1)