/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("unused")

package com.gridnine.jasmine.web.easyui.components

import com.gridnine.jasmine.web.core.remote.launch
import com.gridnine.jasmine.web.core.ui.components.WebPanel
import com.gridnine.jasmine.web.core.ui.components.WebPanelConfiguration
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS

class EasyUiWebPanel(configure:WebPanelConfiguration.()->Unit):WebPanel,EasyUiComponent{
    private lateinit var title:String
    private var handler: (suspend (String, WebPanel) -> Unit)? = null
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

    override fun setToolHandler(handler: suspend (String, WebPanel) -> Unit) {
        this.handler = handler
    }


    override fun getHtml(): String {
        return "<div id=\"panel${uid}\" style=\"${if(conf.width != null) "width:${conf.width}" else ""};${if(conf.height != null) "height:${conf.height}" else ""}\"/>"
    }

    override fun decorate() {
        jq = jQuery("#${getId()}")
        val toolsList = arrayListOf<dynamic>()
        conf.tools.forEach {
            toolsList.add(object{
                val iconCls = getIconClass(it.icon)
                val handler = {
                    this@EasyUiWebPanel.handler?.let {h ->
                        launch { h.invoke(it.id, this@EasyUiWebPanel) }
                    }
                }

            })
        }
        jq.panel(object{
            val title = this@EasyUiWebPanel.title
            val tools = toolsList.toTypedArray()
            val content = findEasyUiComponent(this@EasyUiWebPanel.conf.content).getHtml()
        })
        findEasyUiComponent(this@EasyUiWebPanel.conf.content).decorate()
        initialized = true
    }

    override fun destroy() {
        if(initialized){
            findEasyUiComponent(conf.content).destroy()
            jq.panel("destroy")
        }
    }

    override fun getId(): String {
        return "panel${uid}"
    }

}