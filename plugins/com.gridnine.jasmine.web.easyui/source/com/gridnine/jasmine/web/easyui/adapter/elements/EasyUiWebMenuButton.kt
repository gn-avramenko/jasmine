/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.easyui.adapter.elements

import com.gridnine.jasmine.web.core.ui.WebComponent
import com.gridnine.jasmine.web.core.ui.WebPopupContainer
import com.gridnine.jasmine.web.core.ui.components.*
import com.gridnine.jasmine.web.core.utils.HtmlUtilsJS
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import com.gridnine.jasmine.web.core.utils.UiUtils
import com.gridnine.jasmine.web.easyui.adapter.EasyUiUtils
import com.gridnine.jasmine.web.easyui.adapter.jQuery

class EasyUiWebMenuButton(private val parent:WebComponent?, configure: WebMenuButtonConfiguration.()->Unit) :WebMenuButton{

    private var initialized = false

    private val uid = MiscUtilsJS.createUUID()

    private var title:String? = null

    private var icon:String? = null

    private var width:String? = null
    private var height:String? = null
    private var visible=true
    private var enabled=true
    private  var jq:dynamic = null
    private  var jq2:dynamic = null
    private val items = arrayListOf<WebMenuItemConfiguration>()
    private val itemsState = hashMapOf<String, MenuItemState>()

    init {
        (parent?.getChildren() as MutableList<WebComponent>?)?.add(this)
        val config = WebMenuButtonConfiguration()
        config.configure()
        width = config.width
        height = config.height
        title = config.title
        icon = config.icon
        items.addAll(config.items)
        items.forEach { itemsState[it.id] = MenuItemState() }
    }

    override fun getHtml(): String {
        return "<a id=\"menuButton${uid}\" style=\"${if(width != null) "width:$width" else ""};${if(height != null) "height:$height" else ""}\"/>"
    }


    override fun decorate() {
       jq = jQuery("#menuButton${uid}")
       val popupContainer = UiUtils.findParent(this, WebPopupContainer::class)!!
        val menuDiv = HtmlUtilsJS.div(id ="menuButtonMenu${uid}" ) {
            items.forEach {item ->
                itemsState[item.id]!!.elementId = "menuElement${item.id}${uid}"
                div(id = "menuElement${item.id}${uid}" ,data_options = "disabled:${if(itemsState[item.id]!!.disabled)"true" else "false"}"){
                    item.title?.invoke()
                }
            }
        }.toString()
        val popupId = popupContainer.getId()
        jQuery("#${popupId}").append(menuDiv)
        jq2 = jQuery("#menuButtonMenu${uid}")
        jq.menubutton(object{
            val text   = if(title?.contains(" ") == true) "<nobr>$title</nobr>" else title
            val iconCls = EasyUiUtils.getIconClass(icon)
            val menu = "#menuButtonMenu${uid}"
        })
        jq2.menu(object{
            val onClick = {item:dynamic ->
                itemsState.values.find { it.elementId  == item.id}!!.handler?.invoke()
            }
        })
        initialized = true
        updateVisibility()
    }

    override fun destroy() {
        if(initialized) {
            jq.menubutton("destroy")
            jq2.menu("destroy")
            jq2.remove()
        }
    }

    override fun setVisible(value: Boolean) {
        if(visible != value) {
            visible = value
            if (initialized) {
                updateVisibility()
            }
        }
    }

    private fun updateVisibility() {
        if(visible){
            jq.show()
            val function = if(enabled) "enable" else "disable"
            jq.menubutton(function)
        } else {
            jq.hide()
        }

    }

    override fun setHandler(id: String, handler: () -> Unit) {
        itemsState[id]!!.handler = handler
    }

    override fun setEnabled(id: String, value: Boolean) {
         itemsState[id]!!.disabled = !value
         if(initialized){
             val elm = jQuery("#${itemsState[id]!!.elementId}")
             jq2.menu(if(value) "enableItem" else "disableItem", elm)
         }
    }


    override fun setEnabled(value: Boolean) {
        enabled = value
        if(initialized){
            val function = if(value) "enable" else "disable"
            jq.menubutton(function)
        }
    }

    override fun getParent(): WebComponent? {
        return parent
    }

    override fun getChildren(): List<WebComponent> {
        return emptyList()
    }

}

class MenuItemState(var disabled:Boolean= false, var elementId:String? = null, var handler:(()-> Unit)? = null)