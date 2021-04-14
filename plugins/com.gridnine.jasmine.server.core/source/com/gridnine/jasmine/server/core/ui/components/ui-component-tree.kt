/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.ui.components

import com.gridnine.jasmine.server.core.ui.common.BaseComponentConfiguration
import com.gridnine.jasmine.server.core.ui.common.UiNode


class TreeItem(val id:String, var text:String, var userData:Any?){
    val children:MutableList<TreeItem> = arrayListOf()
}

interface Tree: UiNode {
    fun setData(data: List<TreeItem>)
    fun setSelectListener(listener:((item: TreeItem) ->Unit)?)
    fun setOnContextMenuListener(listener:((node: TreeItem, event: TreeContextMenuEvent) ->Unit)?)
    fun setOnDropListener(listener:((target: TreeItem, source: TreeItem) ->Unit)?)
    fun insertAfter(data: TreeItem, targetId: String)
    fun insertBefore(data: TreeItem, targetId: String)
    fun append(data: TreeItem, parentId: String)
    fun findNode(id:String): TreeItem?
    fun select(id:String)
    fun updateText(id:String, text:String)
    fun updateUserData(id:String, data:Any?)
    fun getData():List<TreeItem>
    fun remove(id: String)
}

class TreeConfiguration: BaseComponentConfiguration(){
    var enableDnd = false
}

class TreeContextMenuEvent(val pageX:Int, val pageY:Int)