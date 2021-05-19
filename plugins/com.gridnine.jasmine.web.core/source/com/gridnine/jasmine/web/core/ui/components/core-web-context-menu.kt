/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.components

interface WebContextMenuItem

class WebContextMenuStandardItem(val text:String, val icon:String?, val disabled:Boolean, val handler:suspend ()->Unit):WebContextMenuItem{
    val children = arrayListOf<WebContextMenuItem>()
}

class WebContextMenuSeparator:WebContextMenuItem