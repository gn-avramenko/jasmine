/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.richlet.pg

interface ServerUiComponent{
    fun getParent():ServerUiComponent?
}

interface ServerUiTextBox:ServerUiComponent{
    fun getValue():String?
    fun setValue(value:String?)
}

class ServerUiTextBoxConfiguration{
    var width:String? = null
    var height:String? = null
}


interface ServerUiTable : ServerUiComponent{
    fun addRow(position:Int?, components:List<ServerUiTableCell>)
    fun removeRow(position: Int)
    fun moveRow(fromPosition:Int, toPosition:Int)
    fun getRows():List<List<ServerUiComponent?>>
}

class ServerUiTableConfiguration{
    var width:String? = null
    var height:String? = null
    val columns = arrayListOf<ServerUiTableColumnDescription>()
}

class ServerUiTableColumnDescription(val label:String?, val fixedWidth:Int? = 0)

class ServerUiTableCell(val component:ServerUiComponent? , val colspan:Int =1)

abstract class BaseServerUiButtonConfiguration{
    var title:String? = null
}

class ServerUiLinkButtonConfiguration:BaseServerUiButtonConfiguration(){
    var width:String?=null
    var height:String?=null
}

interface ServerUiLinkButton: ServerUiComponent {
    fun setHandler(handler:()-> Unit)
    fun setEnabled(value:Boolean)
}