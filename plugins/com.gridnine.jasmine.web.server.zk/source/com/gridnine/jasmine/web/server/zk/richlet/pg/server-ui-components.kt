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
    var noHeader  = false
    val columns = arrayListOf<ServerUiTableColumnDescription>()
}

class ServerUiTableColumnDescription(val label:String?, val minWidth:Int?,  val prefWidth:Int?, val maxWidth:Int?)


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

class ServerUiTreeItem(val id:String, var text:String, var userData:Any?){
    val children:MutableList<ServerUiTreeItem> = arrayListOf()
}

interface ServerUiTree:ServerUiComponent{
    fun setData(data: List<ServerUiTreeItem>)
    fun setSelectListener(listener:((item:ServerUiTreeItem) ->Unit)?)
    fun setOnContextMenuListener(listener:((node:ServerUiTreeItem, event:ServerUiTreeContextMenuEvent) ->Unit)?)
    fun setOnDropListener(listener:((target:ServerUiTreeItem, source:ServerUiTreeItem) ->Unit)?)
    fun insertAfter(data: ServerUiTreeItem, targetId: String)
    fun insertBefore(data: ServerUiTreeItem, targetId: String)
    fun append(data: ServerUiTreeItem, parentId: String)
    fun findNode(id:String):ServerUiTreeItem?
    fun select(id:String)
    fun updateText(id:String, text:String)
    fun updateUserData(id:String, data:Any?)
    fun getData():List<ServerUiTreeItem>
    fun remove(id: String)
}

class ServerUiTreeConfiguration{
    var width:String? = null
    var height:String? = null
    var enableDnd = false
}

class ServerUiTreeContextMenuEvent(val pageX:Int, val pageY:Int)