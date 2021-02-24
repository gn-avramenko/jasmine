/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.components

import com.gridnine.jasmine.web.server.components.*
import com.gridnine.jasmine.web.server.components.ServerUiNode
import org.zkoss.zk.ui.HtmlBasedComponent
import org.zkoss.zk.ui.event.Events
import org.zkoss.zul.*

class ZkServerUiMenuButton(private val config: ServerUiMenuButtonConfiguration) : ServerUiMenuButton, ZkServerUiComponent(){

    private var component:Combobutton? = null

    private var enabled = true

    private val enabledItems = hashMapOf<String, Boolean>()

    private val items = hashMapOf<String, Menuitem>()

    override fun setEnabled(id: String, value: Boolean) {
        enabledItems[id] = value
        if(component != null){
            setEnabledInternal()
        }
    }

    override fun setEnabled(value: Boolean) {
        enabled = value
        if(component != null){
            setEnabledInternal()
        }
    }

    private fun setEnabledInternal() {
        val comp = component!!
        comp.isDisabled = !enabled
        enabledItems.entries.forEach {
            items[it.key]?.isDisabled = !it.value
        }
    }

    override fun getZkComponent(): HtmlBasedComponent {
        if(component!= null){
            return component!!
        }
        val comp = Combobutton()
        component = comp
        comp.label = config.title
        if(config.width == "100%"){
            comp.hflex = "1"
        } else if(config.width != null){
            comp.width = config.width
        }
        if(config.height == "100%"){
            comp.vflex = "1"
        } else if(config.height != null){
            comp.height = config.height
        }
        val menuPopup = createMenuPopup(config.items)
        menuPopup.parent = comp
        setEnabledInternal()
        return comp
    }

    private fun createMenuPopup(items: List<ServerUiMenuButtonItem>): Menupopup {
        val menu = Menupopup()
        items.forEach {
            if(it is ServerUiMenuButtonSeparator){
                val separator = Menuseparator()
                separator.parent = menu
                return@forEach
            }
            if(it is ServerUiMenuButtonGroupItem){
                if(it.children.isNotEmpty()){
                    val menu2 = Menu()
                    menu2.label = it.text
                    menu2.parent = menu
                    menu2.isDisabled = it.disabled
                    menu2.appendChild(createMenuPopup(it.children))
                }
                return@forEach
            }
            it as ServerUiMenuButtonStandardItem
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

    override fun getParent(): ServerUiNode? {
        return parent
    }

}