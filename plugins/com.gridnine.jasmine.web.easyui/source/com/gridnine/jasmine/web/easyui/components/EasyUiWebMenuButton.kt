/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("unused")

package com.gridnine.jasmine.web.easyui.components

import com.gridnine.jasmine.web.core.remote.launch
import com.gridnine.jasmine.web.core.ui.components.*
import com.gridnine.jasmine.web.core.utils.HtmlUtilsJS
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS

class EasyUiWebMenuButton(configure: WebMenuButtonConfiguration.()->Unit) :WebMenuButton,EasyUiComponent{

    private var initialized = false

    private val uid = MiscUtilsJS.createUUID()


    private var visible=true
    private var enabled=true
    private  var jq:dynamic = null
    private  var jq2:dynamic = null
    private val items = arrayListOf<BaseMenuItem>()
    private val itemsState = hashMapOf<String, MenuItemState>()

    private val config = WebMenuButtonConfiguration()
    init {

        config.configure()
        items.addAll(config.elements.filterIsInstance<BaseMenuItem>())
        items.forEach { itemsState[it.id] = MenuItemState() }
    }

    override fun getId(): String {
        return "menuButton${uid}"
    }

    override fun getHtml(): String {

        val menuDiv =HtmlUtilsJS.div(id ="menuButtonMenu${uid}" ) {
                items.forEach {item ->
                    itemsState[item.id]!!.elementId = "menuElement${item.id}${uid}"
                    div(id = "menuElement${item.id}${uid}" ,data_options = "disabled:${if(itemsState[item.id]!!.disabled)"true" else "false"}"){
                        item.title?.invoke()
                    }
                }
            }.toString()
        val buttonA ="<a id=\"menuButton${uid}\" style=\"${if(config.width != null) "width:${config.width}" else ""};${if(config.width != null) "height:${config.width}" else ""}\"/>"
        return buttonA+menuDiv
    }


    override fun decorate() {
        jq = jQuery("#menuButton${uid}")
        jq2 = jQuery("#menuButtonMenu${uid}")
        jq.menubutton(object{
            val text   = if(config.title?.contains(" ") == true) "<nobr>${config.title}</nobr>" else config.title
            val iconCls = getIconClass(config.icon)
            val menu = "#menuButtonMenu${uid}"
        })
        jq2.menu(object{
            val onClick = {item:dynamic ->
                itemsState.values.find { it.elementId  == item.id}!!.handler?.let{ h->
                    launch(h)
                }
            }
        })
        initialized = true
        updateVisibility()
    }

    override fun destroy() {
        if(initialized) {
            jq2.menu("destroy")
            jq2.remove()
            jq.menubutton("destroy")
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

    override fun setHandler(id: String,  handler: suspend () -> Unit) {
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

}

class MenuItemState(var disabled:Boolean= false, var elementId:String? = null, var handler:(suspend ()-> Unit)? = null)