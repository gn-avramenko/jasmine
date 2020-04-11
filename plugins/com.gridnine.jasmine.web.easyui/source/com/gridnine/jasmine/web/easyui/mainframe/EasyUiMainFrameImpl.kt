/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.easyui.mainframe

import com.gridnine.jasmine.server.standard.model.rest.BaseWorkspaceItemDTJS
import com.gridnine.jasmine.server.standard.model.rest.GetWorkspaceRequestJS
import com.gridnine.jasmine.server.standard.model.rest.ListWorkspaceItemDTJS
import com.gridnine.jasmine.server.standard.model.rest.WorkspaceDTJS
import com.gridnine.jasmine.web.core.StandardRestClient
import com.gridnine.jasmine.web.core.model.ui.Editor
import com.gridnine.jasmine.web.core.ui.MainFrame
import com.gridnine.jasmine.web.core.ui.MainFrameConfiguration
import com.gridnine.jasmine.web.core.utils.TextUtilsJS
import com.gridnine.jasmine.web.core.utils.HtmlUtilsJS
import com.gridnine.jasmine.web.easyui.jQuery
import kotlin.browser.window
import kotlin.js.Promise


@Suppress("UnsafeCastFromDynamic")
class EasyUiMainFrameImpl : MainFrame {

    private val tabs = arrayListOf<EasyUiTabHandler<*, *>>()

    init {
        val config = MainFrameConfiguration.get()
        if (config.title != null) window.asDynamic().title = config.title
        val content = HtmlUtilsJS.html {
            div(id = "main-frame", `class` = "easyui-layout", data_options = "fit:true") {
                div(data_options = "region:'west',split:true,width:200") {
                    div(id = "main-frame-west-region", `class` = "easyui-layout", data_options = "fit:true") {
                        div(data_options = "region:'north',split:false") {
                            div(style = "width:100%;height:100%", `class` = "jasmine-title") {
                                config.logoIconUrl?.let { img(src = it) }
                                config.logoText?.let { it() }
                            }
                        }
                        div(data_options = "region:'center',split:false") {
                            div(id = "mf-navigation", style = "width:100%;height:100%") { }
                        }
                    }
                }
                div(data_options = "region:'center'") {
                    div(id = "mf-content-tabs", data_options = "fit:true") { }
                }
            }
            div(id = "mf-content-tabs-settings-menu", style = "display:none;width:200px"/*,data_options = "onClick:function(item){alert(item.name)}"*/) {
                MainFrameConfiguration.get().getTools().withIndex().forEach { (idx, item) ->
                    div(id = "mf-content-tabs-settings-menu-$idx") { item.displayName() }
                }
            }
        }.toString()
        jQuery("body").html(content)
        jQuery("#main-frame").layout()
        jQuery("#main-frame-west-region").layout()




        StandardRestClient.standard_standard_getWorkspace(GetWorkspaceRequestJS()).then {
            setWorkspace(it.workspace)
            jQuery("#mf-content-tabs").jtabs(object {
                val toolPosition = "left"
                val tools = arrayOf(object {
                    val iconCls = "icon-settings"
                    val menu = "#mf-content-tabs-settings-menu"
                    val showEvent = "click"
                }
                )
                val onClose = { _: String, index: Int ->
                    tabs.removeAt(index)
                }
            })
            jQuery("#mf-content-tabs-settings-menu").menu(object {
                val onClick = { item: dynamic ->
                    val id = item.id as String
                    val idx = id.substring(id.lastIndexOf("-") + 1).toInt()
                    MainFrameConfiguration.get().getTools()[idx].handle(MainFrame.get())
                }
            })
        }
    }

    fun setWorkspace(workspace: WorkspaceDTJS) {
        val navigationDiv = jQuery("#mf-navigation")
        navigationDiv.accordion(object {})
        val existingSize = navigationDiv.accordion("panels").asDynamic().length as Int
        for (n in existingSize - 1 downTo 0) {
            navigationDiv.accordion("remove", n)
        }
        workspace.groups.withIndex().forEach { (idx, group) ->
            val itemsMap = hashMapOf<Int, BaseWorkspaceItemDTJS>()
            val navbarContent = HtmlUtilsJS.html {
                ul(id = "navigation_group_${idx}", `class` = "easyui-datalist", lines = false, style = "width:100%") {
                    group.items.withIndex().forEach { (idx2, item) ->
                        li {
                            (item.displayName ?: "???")()
                            itemsMap[idx2] = item
                        }
                    }

                }
            }.toString()
            navigationDiv.accordion("add", object {
                val title = group.displayName
                val content = navbarContent
            })
            jQuery("#navigation_group_${idx}").datalist(object {
                val onClickRow = { idx: Int, _: dynamic ->
                    val item = itemsMap[idx]
                    if (item is ListWorkspaceItemDTJS) {
                        openTab(EasyUiListTabHandler(item))
                    }
                }
            })
        }
    }

    override fun openTab(objectId: String, uid: String?, navigationKey: String?): Promise<Editor<*, *, *, *>> {
        return openTab(EasyUiEditorTabHandler(objectId, uid, navigationKey))
    }



    fun <T, I> openTab(handler: EasyUiTabHandler<T, I>): Promise<I> {
        return Promise { resolve, reject ->
            val existingTab = tabs.find { handler.getId() == it.getId() }
            val tabsDiv = jQuery("#mf-content-tabs")
            if (existingTab != null) {
                tabsDiv.jtabs("select", tabs.indexOf(existingTab))
                resolve(existingTab.cachedEditor as I)
                return@Promise
            }
            val uid = TextUtilsJS.createUUID()
            handler.getData(uid).then {
                tabsDiv.jtabs("add", object {
                    val title = processTabTitle(handler.getTitle(it))
                    val content = handler.getContent(it, uid)
                    val closable = true
                })
                tabs.add(handler)
                val tab = tabsDiv.jtabs("getSelected")  // get selected panel
                val editor = handler.decorateData(it, uid, { newTitle ->
                    tabsDiv.jtabs("update", object {
                        val tab = tab
                        val type = "header"
                        val options = object {
                            val title = processTabTitle(newTitle)
                        }
                    })
                }, {
                    val idx = tabsDiv.jtabs("getTabIndex", tab)
                    tabsDiv.jtabs("close", idx)
                })
                handler.cachedEditor = editor
                resolve(editor)
            }.catch(reject)
        }
    }

    private fun processTabTitle(title: String): String {
        return if (title.length > 30) title.substring(0, 30) + "..." else title
    }

}

interface EasyUiTabHandler<T, I> {
    var cachedEditor: I?
    fun getId(): String
    fun getData(uid: String): Promise<T>
    fun getTitle(data: T): String
    fun getContent(data: T, uid: String): String
    fun decorateData(data: T, uid: String, setTitle: (String) -> Unit, close: () -> Unit): I
}