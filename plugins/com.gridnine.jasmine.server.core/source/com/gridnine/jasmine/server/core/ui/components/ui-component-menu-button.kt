/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.ui.components

import com.gridnine.jasmine.server.core.ui.common.BaseComponentConfiguration
import com.gridnine.jasmine.server.core.ui.common.UiNode

interface MenuButton: UiNode {
    fun setEnabled(id:String, value:Boolean)
    fun setEnabled(value:Boolean)
}

interface MenuButtonItem

class MenuButtonStandardItem(val text:String, val icon:String?, val disabled:Boolean, val handler:()->Unit): MenuButtonItem

class MenuButtonGroupItem(val text:String, val icon:String?, val disabled:Boolean): MenuButtonItem {
    val children = arrayListOf<MenuButtonStandardItem>()
}

class MenuButtonSeparator: MenuButtonItem

class MenuButtonConfiguration: BaseComponentConfiguration(){
    var iconClass:String? = null
    var title:String? = null
    val items = arrayListOf<MenuButtonItem>()
}