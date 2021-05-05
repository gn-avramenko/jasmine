/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("unused")

package com.gridnine.jasmine.web.core.ui.components


interface WebTree: WebNode{
    fun setData(data:List<WebTreeNode>)
    fun setSelectListener(listener:(suspend (item:WebTreeNode) ->Unit)?)
    fun setOnBeforeDropListener(listener:((target:WebTreeNode, source:WebTreeNode, point:WebTreeInsertNodePoint) ->Boolean)?)
    fun setOnDragEnterListener(listener:((target:WebTreeNode, source:WebTreeNode) ->Boolean)?)
    fun setOnDropListener(listener:((target:WebTreeNode, source:WebTreeNode, point:WebTreeInsertNodePoint) ->Unit)?)
    fun setOnContextMenuListener(listener:((node:WebTreeNode, event:WebTreeContextMenuEvent) ->Unit)?)
    fun findNode(id:String):WebTreeNode?
    fun select(id:String)
    fun updateText(id:String, text:String)
    fun updateUserData(id:String, data:Any?)
    fun getData():List<WebTreeNode>
    fun append(webTreeNode: WebTreeNode, parentId: String)
    fun remove(id: String)
    fun insertAfter(node: WebTreeNode, targetId: String)
    fun insertBefore(node: WebTreeNode, targetId: String)
}

class WebTreeNode(val id:String, var text:String, var userData:Any?, val children:MutableList<WebTreeNode> = arrayListOf())


class WebTreeConfiguration:BaseWebComponentConfiguration(){
    var fit = true
    var enableDnd = false
}

enum class WebTreeInsertNodePoint{
    APPEND,
    TOP,
    BOTTOM
}

class WebTreeContextMenuEvent(val pageX:Int, val pageY:Int)