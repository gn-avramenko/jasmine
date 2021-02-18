/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.richlet.pg.components.zk

import com.gridnine.jasmine.web.server.zk.richlet.pg.components.ServerUiContextMenuGroupItem
import com.gridnine.jasmine.web.server.zk.richlet.pg.components.ServerUiContextMenuItem
import com.gridnine.jasmine.web.server.zk.richlet.pg.components.ServerUiContextMenuSeparator
import com.gridnine.jasmine.web.server.zk.richlet.pg.components.ServerUiContextMenuStandardItem
import org.zkoss.zk.ui.Component
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.event.Events
import org.zkoss.zul.Menu
import org.zkoss.zul.Menuitem
import org.zkoss.zul.Menupopup
import org.zkoss.zul.Menuseparator

fun showMenu(items: List<ServerUiContextMenuItem>, pageX:Int, pageY:Int){
    val comp = Executions.getCurrent().desktop.pages.iterator().next().firstRoot
    val existingMenu = comp.getChildren<Component>().find { it.id == "jasmine-context-menu" }
    existingMenu?.parent?.removeChild(existingMenu)
    val menu = createPopup(items)
    menu.id = "jasmine-context-menu"
    menu.parent = comp
    menu.open(pageX, pageY)
}

private fun createPopup(items: List<ServerUiContextMenuItem>): Menupopup {
    val menu = Menupopup()
    items.forEach {
        if(it is ServerUiContextMenuSeparator){
            val separator = Menuseparator()
            separator.parent = menu
            return@forEach
        }
        if(it is ServerUiContextMenuGroupItem){
            if(it.children.isNotEmpty()){
                val menu2 = Menu()
                menu2.label = it.text
                menu2.parent = menu
                menu2.isDisabled = it.disabled
                menu2.appendChild(createPopup(it.children))
            }
            return@forEach
        }
        it as ServerUiContextMenuStandardItem
        val item = Menuitem()
        item.label = it.text
        item.parent = menu
        item.isDisabled = it.disabled
        item.addEventListener(Events.ON_CLICK){_ ->
            it.handler.invoke()
        }
    }
    return menu
}
