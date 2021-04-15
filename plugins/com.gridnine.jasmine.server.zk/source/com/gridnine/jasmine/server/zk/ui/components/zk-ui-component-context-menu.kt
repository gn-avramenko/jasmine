/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.zk.ui.components

import com.gridnine.jasmine.server.core.ui.components.ContextMenuGroupItem
import com.gridnine.jasmine.server.core.ui.components.ContextMenuItem
import com.gridnine.jasmine.server.core.ui.components.ContextMenuSeparator
import com.gridnine.jasmine.server.core.ui.components.ContextMenuStandardItem
import org.zkoss.zk.ui.Component
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.event.Events
import org.zkoss.zul.Menu
import org.zkoss.zul.Menuitem
import org.zkoss.zul.Menupopup
import org.zkoss.zul.Menuseparator

fun zkShowContextMenu(items: List<ContextMenuItem>, pageX:Int, pageY:Int){
    val comp = Executions.getCurrent().desktop.pages.iterator().next().firstRoot
    val existingMenu = comp.getChildren<Component>().find { it.id == "jasmine-context-menu" }
    existingMenu?.parent?.removeChild(existingMenu)
    val menu = createPopup(items)
    menu.id = "jasmine-context-menu"
    menu.parent = comp
    menu.open(pageX, pageY)
}

private fun createPopup(items: List<ContextMenuItem>): Menupopup {
    val menu = Menupopup()
    items.forEach {
        if(it is ContextMenuSeparator){
            val separator = Menuseparator()
            separator.parent = menu
            return@forEach
        }
        if(it is ContextMenuGroupItem){
            if(it.children.isNotEmpty()){
                val menu2 = Menu()
                menu2.label = it.text
                menu2.parent = menu
                menu2.isDisabled = it.disabled
                menu2.appendChild(createPopup(it.children))
            }
            return@forEach
        }
        it as ContextMenuStandardItem
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
