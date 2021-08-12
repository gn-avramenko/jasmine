/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jtasks
 *****************************************************************/
package com.gridnine.jasmine.web.easyui.components

import com.gridnine.jasmine.web.core.remote.launch
import com.gridnine.jasmine.web.core.ui.components.WebContextMenuItem
import com.gridnine.jasmine.web.core.ui.components.WebContextMenuSeparator
import com.gridnine.jasmine.web.core.ui.components.WebContextMenuStandardItem
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import kotlinx.browser.window

fun showContextMenu(items: List<WebContextMenuItem>, pageX: Int, pageY: Int) {
    val compJq = jQuery("body")
    //val compJq = jQuery("body")
    val menuQ = jQuery("#contextMenu")
    val size = menuQ.length as Int
    if(size >0){
        menuQ.menu("destroy")
        menuQ.remove()
    }
    val itemsMap = hashMapOf<WebContextMenuItem, String>()
    val itemsReverseMap = hashMapOf<String, WebContextMenuItem>()
    fillItemsMap(itemsMap, items)
    itemsMap.entries.forEach {
        itemsReverseMap[it.value] = it.key
    }
    val divContent = """<div id = "contextMenu" style="display:none">
                ${items.joinToString ("\n"){
        buildContextMenuItem(it, itemsMap)
    }}
            </div>
        """.trimIndent()
    compJq.append(divContent)
    val menuJQ = jQuery("#contextMenu")
    menuJQ.menu(object{
        val onClick = { item:dynamic ->
            val webItem = itemsReverseMap[item.id]!!
            if(webItem is WebContextMenuStandardItem){
                launch {
                    webItem.handler.invoke()
                }
            }
        }
        val onHide = {
            window.setTimeout({
                menuJQ.menu("destroy")
                menuJQ.remove()
            }, 50)
        }

    })
    menuJQ.menu("show", object{
        val left = pageX
        val top = pageY
    })
}

private fun fillItemsMap(itemsMap: HashMap<WebContextMenuItem, String>, items: List<WebContextMenuItem>) {
    items.forEach {
        itemsMap[it] = MiscUtilsJS.createUUID()
        if(it is WebContextMenuStandardItem && it.children.isNotEmpty()){
            fillItemsMap(itemsMap, it.children)
        }
    }
}

private fun buildContextMenuItem(item: WebContextMenuItem, itemsMap: HashMap<WebContextMenuItem, String>):String{
    if(item is WebContextMenuSeparator){
        return """<div class="menu-sep"></div>"""
    }
    item as WebContextMenuStandardItem
    var result = """
            <div id = "${itemsMap[item]}" data-options="disabled:${item.disabled}">
            <span>${item.text}</span>
        """.trimIndent()
    if(item.children.isNotEmpty()){
        result = result + "\n"+ item.children.joinToString("\n") {
            buildContextMenuItem(it, itemsMap)
        }
    }
    return result+"\n</div>"
}