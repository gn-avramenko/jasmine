/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.richlet.pg.components

interface ServerUiContextMenuItem

class ServerUiContextMenuStandardItem(val text:String, val icon:String?, val disabled:Boolean, val handler:()->Unit):ServerUiContextMenuItem

class ServerUiContextMenuGroupItem(val text:String, val icon:String?, val disabled:Boolean):ServerUiContextMenuItem{
    val children = arrayListOf<ServerUiContextMenuItem>()
}

class ServerUiContextMenuSeparator:ServerUiContextMenuItem