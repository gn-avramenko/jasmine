/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.easyui.adapter

import com.gridnine.jasmine.server.core.model.common.BaseIntrospectableObjectJS
import com.gridnine.jasmine.server.core.model.common.XeptionJS
import com.gridnine.jasmine.web.core.ui.*
import com.gridnine.jasmine.web.core.ui.components.*
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import com.gridnine.jasmine.web.core.utils.UiUtils
import com.gridnine.jasmine.web.easyui.adapter.elements.*
import kotlin.browser.window

class EasyUiLibraryAdapter:UiLibraryAdapter {
    override fun showWindow(component: WebComponent) {
        jQuery("body").html(component.getHtml())
        component.decorate()
    }

    override fun createBorderLayout(parent: WebComponent?, configure: WebBorderLayoutConfiguration.() -> Unit): WebBorderContainer {
        return EasyUiWebBorderContainer(parent, configure)
    }

    override fun createLabel(parent: WebComponent?): WebLabel {
        return EasyUiWebLabel(parent)
    }

    override fun createAccordionContainer(parent: WebComponent?, configure: WebAccordionPanelConfiguration.() -> Unit): WebAccordionContainer {
        return EasyUiWebAccordionContainer(parent, configure)
    }

    override fun createTabsContainer(parent: WebComponent?, configure: WebTabsContainerConfiguration.() -> Unit): WebTabsContainer {
        return EasyUiWebTabsContainer(parent, configure)
    }

    override fun <E : Any> createDataList(parent: WebComponent?, configure: WebDataListConfiguration.() -> Unit): WebDataList<E> {
        return EasyUiWebDataList(parent, configure)
    }

    override fun createGridLayoutContainer(parent: WebComponent?, configure: WebGridLayoutContainerConfiguration.() -> Unit): WebGridLayoutContainer {
        return EasyUiWebGridLayoutContainer(parent, configure)
    }

    override fun <E : BaseIntrospectableObjectJS> createDataGrid(parent: WebComponent?, configure: WebDataGridConfiguration<E>.() -> Unit): WebDataGrid<E> {
        return EasyUiWebDataGrid(parent, configure)
    }

    override fun createSearchBox(parent: WebComponent?, configure: WebSearchBoxConfiguration.() -> Unit): WebSearchBox {
        return EasyUiWebSearchBox(parent, configure)
    }

    override fun createTextBox(parent: WebComponent?, configure: WebTextBoxConfiguration.() -> Unit): WebTextBox {
        return EasyUiWebTextBox(parent, configure)
    }

    override fun createPasswordBox(parent: WebComponent?, configure: WebPasswordBoxConfiguration.() -> Unit): WebPasswordBox {
        return EasyUiWebPasswordBox(parent,configure)
    }

    override fun createLinkButton(parent: WebComponent?, configure: WebLinkButtonConfiguration.() -> Unit): WebLinkButton {
        return EasyUiWebLinkButton(parent, configure)
    }


    override fun createDateBox(parent: WebComponent?, configure: WebDateBoxConfiguration.() -> Unit): WebDateBox {
        return EasyUiWebDateBox(parent, configure)
    }

    override fun createDateTimeBox(parent: WebComponent?, configure: WebDateTimeBoxConfiguration.() -> Unit): WebDateTimeBox {
        return EasyUiWebDateTimeBox(parent, configure)
    }

    override fun createNumberBox(parent: WebComponent?, configure: WebNumberBoxConfiguration.() -> Unit): WebNumberBox {
        return EasyUiWebNumberBox(parent, configure)
    }


    override fun createSelect(parent: WebComponent, configure: WebSelectConfiguration.() -> Unit): WebSelect {
        return EasyUiWebSelect(parent, configure)
    }

    override fun <W : WebComponent> showDialog(popupChild: WebComponent?, configure: DialogConfiguration<W>.() -> Unit): WebDialog<W> where W : HasDivId {
        val conf = DialogConfiguration<W>(configure)
        val compJq = if(popupChild == null) jQuery("body") else jQuery("#"+(UiUtils.findParent(popupChild,WebPopupContainer::class)?.getId()?:throw XeptionJS.forDeveloper("unable to find popup container")))
        compJq.append(conf.editor.getHtml())
        val jq = jQuery("#${conf.editor.getId()}")
        val result = object:WebDialog<W>{
            override fun close() {
                conf.editor.destroy()
                jq.dialog("close")
                jq.dialog("destroy")
                jq.remove()
            }

            override fun getContent(): W {
                return conf.editor
            }

        }
        val buttons = conf.buttons.map { db->
            object {
                val text = db.displayName
                val handler = {
                    db.handler.invoke(result)
                }
            }
        }.toTypedArray()
        val dialogConfig = object{
            val title = conf.title
            val modal = true
            val buttons = buttons
        }.asDynamic()
        if(conf.expandToMainFrame){
            val bd = jQuery("body")
            dialogConfig.width = bd.width() - 100
            dialogConfig.height = bd.height() - 100
        }
        jq.dialog(dialogConfig);
        conf.editor.decorate()
        if(!conf.expandToMainFrame){
            jq.dialog("resize",object{
                val width = jq.width()+20
                val height = "auto"
            });
        }

        return result
    }

    override fun createMenuButton(parent: WebComponent?, configure: WebMenuButtonConfiguration.() -> Unit): WebMenuButton {
        return EasyUiWebMenuButton(parent, configure)
    }

    override fun createPanel(parent: WebComponent?, configure: WebPanelConfiguration.() -> Unit): WebPanel {
        return EasyUiWebPanel(parent, configure)
    }

    override fun createTilesContainer(parent: WebComponent?, configure: WebTilesContainerConfiguration.() -> Unit): WebTilesContainer {
        return EasyUiWebTilesContainer(parent, configure)
    }

    override fun createDivsContainer(parent: WebComponent?, configure: WebDivsContainerConfiguration.() -> Unit): WebDivsContainer {
        return EasyUiWebDivsContainer(parent, configure)
    }

    override fun createBooleanBox(parent: WebComponent?, configure: WebBooleanBoxConfiguration.() -> Unit): WebBooleanBox {
        return EasyUiWebBooleanBox(parent, configure)
    }

    override fun createTableBox(parent: WebComponent?, configure: WebTableBoxConfiguration.() -> Unit): WebTableBox {
        return EasyUiWebTableBox(parent, configure)
    }

    override fun createTree(parent: WebComponent?, configure: WebTreeConfiguration.() -> Unit): WebTree {
        return EasyUiWebTree(parent, configure)
    }

    override fun showContextMenu(popupChild: WebComponent?, items: List<WebContextMenuItem>, pageX:Int, pageY:Int) {
        val compJq = if(popupChild == null) jQuery("body") else jQuery("#"+(UiUtils.findParent(popupChild,WebPopupContainer::class)?.getId()?:throw XeptionJS.forDeveloper("unable to find popup container")))
        //val compJq = jQuery("body")
        val menuQ = jQuery("#contextMenu")
        val size = menuQ.length as Int
        if(size >0){
            menuQ.menu("destroy")
            menuQ.remove()
        }
        val itemsMap = hashMapOf<WebContextMenuItem, String>()
        val itemsReverseMap = hashMapOf<String,WebContextMenuItem>()
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
                    webItem.handler.invoke()
                }
            }
            val onHide = {
                window.setTimeout({
                    menuJQ.menu("destroy")
                    menuJQ.remove()
                }, 50)
            }

        });
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
        if(item is WebContextMenuSeparatror){
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


    override fun showLoader() {
        jQuery.messager.progress()


    }

    override fun hideLoader() {
        jQuery.messager.progress("close")
    }

    override fun showNotification(message: String, timeout: Int) {
        jQuery.messager.show(object{
            val msg = message
            val timeout = timeout
            val showType = "show"
        });
    }


}
external fun createSelect2Option(id:String, text:String?, defaultSelected: Boolean, selected:Boolean)
external var jQuery: dynamic = definedExternally
