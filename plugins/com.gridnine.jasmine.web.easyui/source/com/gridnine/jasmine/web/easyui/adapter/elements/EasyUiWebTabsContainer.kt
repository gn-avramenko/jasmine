/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.easyui.adapter.elements

import com.gridnine.jasmine.web.core.ui.WebComponent
import com.gridnine.jasmine.web.core.ui.components.*
import com.gridnine.jasmine.web.core.utils.HtmlUtilsJS
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import com.gridnine.jasmine.web.easyui.adapter.EasyUiUtils
import com.gridnine.jasmine.web.easyui.adapter.jQuery
import kotlin.browser.window

class EasyUiWebTabsContainer(private val parent:WebComponent?, configure: WebTabsContainerConfiguration.()->Unit) :WebTabsContainer{
    private var initialized = false
    private val fit:Boolean
    private val tabs = arrayListOf<WebTabPanel>()
    private var selected:Int = 0
    private var jq:dynamic = null
    private val width:String?
    private val height:String?
    private val uid = MiscUtilsJS.createUUID()
    private val tools = arrayListOf<BaseButtonConfiguration>()
    init {
        (parent?.getChildren() as MutableList<WebComponent>?)?.add(this)
        val configuration = WebTabsContainerConfiguration()
        configuration.configure()
        fit = configuration.fit
        width = configuration.width
        height = configuration.height
        tools.addAll(configuration.tools)
    }

    override fun addTestTab() {
        //noops
    }

    override fun addTab(panel: WebTabPanel) {
        tabs.add(panel)
        if(initialized) {
            addTabInternal(panel)
        }
    }

    override fun removeTab(id: String) {
        val idx = tabs.indexOfFirst { it.id == id }
        if(idx != -1) {
            tabs.removeAt(idx)
            if (initialized) {
                jq!!.tabs("close", idx)
            }
        }
    }

    override fun select(id: String) {
        val idx = tabs.indexOfFirst { it.id == id }
        if(idx != -1) {
            selected = idx
            if (initialized) {
                jq!!.tabs("select", idx)
            }
        }
    }

    override fun getTabs(): List<WebTabPanel> {
        return tabs
    }

    private fun addTabInternal(panel: WebTabPanel) {
        jq!!.tabs("add", object{
            val id = panel.id
            val title = panel.title
            val closable = true
            val content = panel.content.getHtml()
        })
        panel.content.decorate()
    }


    override fun getParent(): WebComponent? {
        return parent
    }

    override fun getChildren(): List<WebComponent> {
        return tabs.map { it.content }
    }

    override fun getHtml(): String {
        return "<div id=\"tabs${uid}\" style=\"${if(width != null) "width:$width" else ""};${if(height != null) "height:$height" else ""}\"></div>"

    }

    override fun decorate() {
        jq = jQuery("#tabs${uid}")
        jq.tabs(object{
            val fit = this@EasyUiWebTabsContainer.fit
            val toolPosition = "left"
            val onClose = {_:String, idx:Int ->

            }
            val onBeforeClose ={ _:String?, idx:Int ->
                val element = tabs.removeAt(idx)
                element.content.destroy()
                true
            }
        })
        tabs.forEach {
            addTabInternal(it)
        }
        val header  = jq.children("div.tabs-header")
        header.children("div.tabs-tool").remove()
        if(tools.isNotEmpty()){
            header.children("div.tabs-tool").remove()
            var toolsElm = jQuery("<div class=\"tabs-tool\"><table cellspacing=\"0\" cellpadding=\"0\" style=\"height:100%\"><tr></tr></table></div>").appendTo(header)
            var tr = toolsElm.find("tr");
            tools.withIndex().forEach {(index, tool) ->
                var td = jQuery("<td></td>").appendTo(tr);
                var toolElm = jQuery("<a href=\"javascript:;\"></a>").appendTo(td);
                if(tool is MenuButtonConfiguration){
                    val content = HtmlUtilsJS.div {
                        id = "toolsMenu${index}${uid}"
                        style ="width:150px"
                        tool.elements.withIndex().forEach {(index2, elm) ->
                            if(elm is StandardMenuItem){
                                div(id = "toolsMenu${index}${uid}-$index2") {
                                    text(elm.title!!)
                                }
                            }
                        }
                    }.toString()
                    jq.append(content)

                    toolElm.menubutton(object{
                        val plain = true
                        val text = tool.title
                        val iconCls = EasyUiUtils.getIconClass(tool.icon)
                        val menu = "#toolsMenu${index}${uid}"
                    })
                    jQuery("#toolsMenu${index}${uid}").menu(object {
                        val onClick = { item: dynamic ->
                            val id = item.id as String
                            val idx = id.substring(id.lastIndexOf("-") + 1).toInt()
                            val elm =tool.elements[idx]
                            if(elm is StandardMenuItem){
                                elm.handler.invoke()
                            }
                        }
                    })
                }

            }
        }
        jq!!.tabs("select", selected)
        initialized = true
    }

    override fun destroy() {
        //noops
    }

}