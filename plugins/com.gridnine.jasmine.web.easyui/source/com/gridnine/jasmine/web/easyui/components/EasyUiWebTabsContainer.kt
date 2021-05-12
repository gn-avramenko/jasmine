/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("unused")

package com.gridnine.jasmine.web.easyui.components

import com.gridnine.jasmine.web.core.remote.launch
import com.gridnine.jasmine.web.core.ui.components.*
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS

class EasyUiWebTabsContainer(configure: WebTabsContainerConfiguration.()->Unit) :WebTabsContainer, EasyUiComponent{
    private var initialized = false
    private val tabs = arrayListOf<WebTabPanel>()
    private var selected:Int = 0
    private var jq:dynamic = null
    private val uid = MiscUtilsJS.createUUID()
    private val config = WebTabsContainerConfiguration()
    init {
        config.configure()
    }

    override fun addTab(configure: WebTabPanel.() -> Unit) {
        val panel = WebTabPanel()
        panel.configure()
        tabs.add(panel)
        if(initialized) {
            addTabInternal(panel)
        }
    }

    override fun removeTab(id: String) {
        val idx = tabs.indexOfFirst { it.id == id }
        if(idx != -1) {
            if (initialized) {
                jq!!.tabs("close", idx)
            } else {
                tabs.removeAt(idx)
            }
        }
    }

    override fun select(id: String):WebNode? {
        val idx = tabs.indexOfFirst { it.id == id }
        if(idx != -1) {
            selected = idx
            if (initialized) {
                jq!!.tabs("select", idx)
            }
            return tabs[idx].content
        }
        return null
    }

    override fun getTabs(): List<WebTabPanel> {
        return tabs
    }

    override fun setTitle(tabId: String, title: String) {
        if(!initialized){
            return
        }
        val idx = tabs.indexOfFirst { it.id == tabId }
        if(idx != -1){
            val tab = jq.tabs("getTab", idx)
            jq.tabs("update", object {
                val tab = tab
                val type = "header"
                val options = object {
                    val title = title
                }
            })
        }
    }

    private fun addTabInternal(panel: WebTabPanel) {
        val uiComp = findEasyUiComponent(panel.content)
        jq!!.tabs("add", object{
            val id = panel.id
            val title = panel.title
            val closable = true
            val content = uiComp.getHtml()
        })
        uiComp.decorate()
    }

    override fun getId(): String {
        return "tabs${uid}"
    }

    override fun getHtml(): String {
        return "<div id=\"tabs${uid}\" style=\"${getSizeAttributes(config)}\"></div>"
    }

    override fun decorate() {
        jq = jQuery("#tabs${uid}")
        jq.tabs(object{
            val fit = config.fit
            val toolPosition = "left"
            val onBeforeClose ={ _:String?, idx:Int ->
                val element = tabs.removeAt(idx)
                findEasyUiComponent(element.content).destroy()
                true
            }
        })
        tabs.forEach {
            addTabInternal(it)
        }
        val header  = jq.children("div.tabs-header")
        header.children("div.tabs-tool").remove()
        if(config.tools.isNotEmpty()){
            header.children("div.tabs-tool").remove()
            val toolsElm = jQuery("<div class=\"tabs-tool\"><table cellspacing=\"0\" cellpadding=\"0\" style=\"height:100%\"><tr></tr></table></div>").appendTo(header)
            val tr = toolsElm.find("tr")
            val td = jQuery("<td></td>").appendTo(tr)
            val toolElm = jQuery("<a href=\"javascript:;\"></a>").appendTo(td)
            val content = """
                        <div id = "toolsMenu${uid}">
                            ${config.tools.withIndex().joinToString ("\n") { 
                """<div id = "toolsMenu${uid}-${it.index}">${it.value.displayName}</div>"""
            }}
                        </div>
                    """.trimIndent()
            jq.append(content)
            toolElm.menubutton(object{
                val plain = true
                val iconCls = getIconClass("core:settings")
                val menu = "#toolsMenu${uid}"
            })
            jQuery("#toolsMenu${uid}").menu(object {
                val onClick = { item: dynamic ->
                    val id = item.id as String
                    val idx = id.substring(id.lastIndexOf("-") + 1).toInt()
                    val elm =config.tools[idx]
                    launch {
                        elm.handler.invoke()
                    }
                }
            })
        }
        jq!!.tabs("select", selected)
        initialized = true
    }

    override fun destroy() {
        if(initialized) {
            tabs.forEach {
                findEasyUiComponent(it.content).destroy()
            }
        }
    }

}