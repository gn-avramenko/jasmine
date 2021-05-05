/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("unused")

package com.gridnine.jasmine.web.easyui.components

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


    override fun getHtml(): String {
        return "<div id=\"tabs${uid}\" style=\"${if(config.width != null) "width:${config.width}" else ""};${if(config.height != null) "height:${config.height}" else ""}\"></div>"
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
//        val header  = jq.children("div.tabs-header")
//        header.children("div.tabs-tool").remove()
//        if(.isNotEmpty()){
//            header.children("div.tabs-tool").remove()
//            var toolsElm = jQuery("<div class=\"tabs-tool\"><table cellspacing=\"0\" cellpadding=\"0\" style=\"height:100%\"><tr></tr></table></div>").appendTo(header)
//            var tr = toolsElm.find("tr");
//            tools.withIndex().forEach {(index, tool) ->
//                var td = jQuery("<td></td>").appendTo(tr);
//                var toolElm = jQuery("<a href=\"javascript:;\"></a>").appendTo(td);
//                if(tool is MenuButtonConfiguration){
//                    val content = HtmlUtilsJS.div {
//                        id = "toolsMenu${index}${uid}"
//                        //style ="width:200px"
//                        tool.elements.withIndex().forEach {(index2, elm) ->
//                            if(elm is StandardMenuItem){
//                                div(id = "toolsMenu${index}${uid}-$index2") {
//                                    text(elm.title!!)
//                                }
//                            }
//                        }
//                    }.toString()
//                    jq.append(content)
//
//                    toolElm.menubutton(object{
//                        val plain = true
//                        val text = tool.title
//                        val iconCls = EasyUiUtils.getIconClass(tool.icon)
//                        val menu = "#toolsMenu${index}${uid}"
//                    })
//                    jQuery("#toolsMenu${index}${uid}").menu(object {
//                        val onClick = { item: dynamic ->
//                            val id = item.id as String
//                            val idx = id.substring(id.lastIndexOf("-") + 1).toInt()
//                            val elm =tool.elements[idx]
//                            if(elm is StandardMenuItem){
//                                elm.handler.invoke()
//                            }
//                        }
//                    })
//                }
//
//            }
//        }
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