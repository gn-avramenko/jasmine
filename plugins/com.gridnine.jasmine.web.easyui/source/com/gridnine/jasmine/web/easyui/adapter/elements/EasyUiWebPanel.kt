/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.easyui.adapter.elements

import com.gridnine.jasmine.web.core.ui.WebComponent
import com.gridnine.jasmine.web.core.ui.components.WebPanel
import com.gridnine.jasmine.web.core.ui.components.WebPanelConfiguration
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import com.gridnine.jasmine.web.easyui.adapter.EasyUiUtils
import com.gridnine.jasmine.web.easyui.adapter.jQuery

class EasyUiWebPanel(private val parent:WebComponent?, configure:WebPanelConfiguration.()->Unit):WebPanel{
    private lateinit var title:String
    private var handler: ((String, WebPanel) -> Unit)? = null
    private val conf:WebPanelConfiguration = WebPanelConfiguration()
    private val uid = MiscUtilsJS.createUUID()
    private var initialized = false
    private var jq:dynamic = null
    init {
        conf.configure()
    }
    override fun setTitle(title: String) {
        this.title = title
        if(initialized){
            jq.panel("setTitle", title)
        }
    }

    override fun setToolHandler(handler: (String, WebPanel) -> Unit) {
        this.handler = handler
    }

    override fun getParent(): WebComponent? {
        return parent
    }

    override fun getChildren(): List<WebComponent> {
        return arrayListOf(conf.content)
    }

    override fun getHtml(): String {
        return "<div id=\"panel${uid}\" style=\"${if(conf.width != null) "width:${conf.width}" else ""};${if(conf.height != null) "height:${conf.height}" else ""}\"/>"
    }

    override fun decorate() {
        jq = jQuery("#${getId()}")
        val toolsList = arrayListOf<dynamic>()
        conf.tools.forEach {
            toolsList.add(object{
                val iconCls = EasyUiUtils.getIconClass(it.icon)
                val handler = {
                    this@EasyUiWebPanel.handler?.invoke(it.id, this@EasyUiWebPanel)
                }

            })
        }
        jq.panel(object{
            val title = this@EasyUiWebPanel.title
            val tools = toolsList.toTypedArray()
            val content = this@EasyUiWebPanel.conf.content.getHtml()
        })
        this@EasyUiWebPanel.conf.content.decorate()
        initialized = true
    }

    override fun destroy() {
        if(initialized){
            conf.content.destroy()
            jq.panel("destroy")
        }
    }

    override fun getId(): String {
        return "panel${uid}"
    }

}