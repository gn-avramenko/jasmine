/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.components


class ServerUiTreeItem(val id:String, var text:String, var userData:Any?){
    val children:MutableList<ServerUiTreeItem> = arrayListOf()
}

interface ServerUiTree: ServerUiNode {
    fun setData(data: List<ServerUiTreeItem>)
    fun setSelectListener(listener:((item: ServerUiTreeItem) ->Unit)?)
    fun setOnContextMenuListener(listener:((node: ServerUiTreeItem, event: ServerUiTreeContextMenuEvent) ->Unit)?)
    fun setOnDropListener(listener:((target: ServerUiTreeItem, source: ServerUiTreeItem) ->Unit)?)
    fun insertAfter(data: ServerUiTreeItem, targetId: String)
    fun insertBefore(data: ServerUiTreeItem, targetId: String)
    fun append(data: ServerUiTreeItem, parentId: String)
    fun findNode(id:String): ServerUiTreeItem?
    fun select(id:String)
    fun updateText(id:String, text:String)
    fun updateUserData(id:String, data:Any?)
    fun getData():List<ServerUiTreeItem>
    fun remove(id: String)
}

class ServerUiTreeConfiguration(){
    constructor(config:ServerUiTreeConfiguration.()->Unit):this(){
        config.invoke(this)
    }
    var width:String? = null
    var height:String? = null
    var enableDnd = false
}

class ServerUiTreeContextMenuEvent(val pageX:Int, val pageY:Int)