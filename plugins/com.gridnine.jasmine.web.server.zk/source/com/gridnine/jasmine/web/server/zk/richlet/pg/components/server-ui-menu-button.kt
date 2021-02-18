/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.richlet.pg.components

import com.gridnine.jasmine.web.server.zk.richlet.pg.ServerUiComponent


interface ServerUiMenuButton: ServerUiComponent {
    fun setEnabled(id:String, value:Boolean)
    fun setEnabled(value:Boolean)
}

interface ServerUiMenuButtonItem

class ServerUiMenuButtonStandardItem(val id:String, val text:String, val icon:String?, val disabled:Boolean, val handler:()->Unit):ServerUiMenuButtonItem

class ServerUiMenuButtonGroupItem(val text:String, val icon:String?, val disabled:Boolean):ServerUiMenuButtonItem{
    val children = arrayListOf<ServerUiMenuButtonStandardItem>()
}

class ServerUiMenuButtonSeparator:ServerUiMenuButtonItem

class ServerUiMenuButtonConfiguration{
    var title:String? = null
    var width:String?=null
    var height:String?=null
    val items = arrayListOf<ServerUiMenuButtonItem>()
}