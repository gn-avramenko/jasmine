/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.easyui.adapter.elements

import com.gridnine.jasmine.web.core.ui.WebComponent
import com.gridnine.jasmine.web.core.ui.components.WebLinkButtonConfiguration
import com.gridnine.jasmine.web.core.ui.components.WebLinkButton
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import com.gridnine.jasmine.web.easyui.adapter.EasyUiUtils
import com.gridnine.jasmine.web.easyui.adapter.jQuery

class EasyUiWebLinkButton(private val parent:WebComponent?, configure: WebLinkButtonConfiguration.()->Unit) :WebLinkButton{

    private var initialized = false

    private val uid = MiscUtilsJS.createUUID()

    private var title:String? = null

    private var icon:String? = null

    private var width:String? = null
    private var height:String? = null
    private var visible=true
    private var enabled=true
    private  var jq:dynamic = null
    private lateinit var handler:()->Unit
    init {
        (parent?.getChildren() as MutableList<WebComponent>?)?.add(this)
        val config = WebLinkButtonConfiguration()
        config.configure()
        width = config.width
        height = config.height
        title = config.title
        icon = config.icon
    }

    override fun getHtml(): String {
        return "<a id=\"linkButton${uid}\" style=\"${if(width != null) "width:$width" else ""};${if(height != null) "height:$height" else ""}\"/>"
    }


    override fun decorate() {
       jq = jQuery("#linkButton${uid}")
        jq.linkbutton(object{
            val text   = title
            val iconCls = EasyUiUtils.getIconClass(icon)
            val onClick = {
                handler.invoke()
            }
        })
        initialized = true
        updateVisibility()
    }

    override fun destroy() {
        //noops
    }

    override fun setVisible(value: Boolean) {
        visible = value
        if(initialized) {
            updateVisibility()
        }
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

    override fun setHandler(handler: () -> Unit) {
        this.handler = handler
    }

    override fun setEnabled(value: Boolean) {
        enabled = value
        if(initialized){
            val function = if(value) "enable" else "disable"
            jq.linkbutton(function)
        }
    }

    override fun getParent(): WebComponent? {
        return parent
    }

    override fun getChildren(): List<WebComponent> {
        return emptyList()
    }

}