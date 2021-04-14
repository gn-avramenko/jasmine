/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.ui.components

interface ContextMenuItem

class ContextMenuStandardItem(val text:String, val icon:String?, val disabled:Boolean, val handler:()->Unit): ContextMenuItem

class ContextMenuGroupItem(val text:String, val icon:String?, val disabled:Boolean): ContextMenuItem {
    val children = arrayListOf<ContextMenuItem>()
}

class ContextMenuSeparator: ContextMenuItem