/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("unused")

package com.gridnine.jasmine.web.easyui.components

import com.gridnine.jasmine.web.core.remote.launch
import com.gridnine.jasmine.web.core.ui.components.WebLinkButtonConfiguration
import com.gridnine.jasmine.web.core.ui.components.WebLinkButton
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS

class EasyUiWebLinkButton(configure: WebLinkButtonConfiguration.()->Unit) :WebLinkButton,EasyUiComponent{

    private var initialized = false

    private val uid = MiscUtilsJS.createUUID()

    private var enabled=true
    private var visible=true
    private  var jq:dynamic = null
    private lateinit var handler:suspend ()->Unit
    private val config = WebLinkButtonConfiguration()
    init {
        config.configure()
    }

    override fun getId(): String {
        return "linkButton${uid}"
    }
    override fun getHtml(): String {
        return "<a id=\"linkButton${uid}\" style=\"${if(config.width != null) "width:${config.width}" else ""};${if(config.height != null) "height:${config.height}" else ""}\"/>"
    }


    override fun decorate() {
       jq = jQuery("#linkButton${uid}")
        jq.linkbutton(object{
            val text   = if(config.title?.contains(" ") == true) "<nobr>${config.title}</nobr>" else config.title
            val iconCls = getIconClass(config.icon)
            val onClick = {
                launch(handler)
            }
        })
        initialized = true
        updateState()
        updateVisibility()
    }

    override fun destroy() {
        //noops
    }

    private fun updateState() {
        val function = if(enabled) "enable" else "disable"
        jq.linkbutton(function)

    }

    private fun updateVisibility() {
        if(visible){
            jq.show()
            val function = if(enabled) "enable" else "disable"
            jq.linkbutton(function)
        } else {
            jq.hide()
        }

    }

    override fun setHandler(handler: suspend () -> Unit) {
        this.handler = handler
    }

    override fun setEnabled(value: Boolean) {
        if(value == enabled){
            return
        }
        enabled = value
        if(initialized){
            updateState()
        }
    }

    override fun setVisible(value: Boolean) {
        if(visible == value){
            return
        }
        visible = value
        if(initialized){
            updateVisibility()
        }
    }

}