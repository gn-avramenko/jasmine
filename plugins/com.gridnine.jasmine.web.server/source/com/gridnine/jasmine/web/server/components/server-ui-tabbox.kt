/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.components

interface ServerUiTabbox : ServerUiComponent {
    fun addTab(panel: ServerUiTabPanel)
    fun removeTab(id:String)
    fun select(id:String): ServerUiTabPanel?
    fun getTabs():List<ServerUiTabPanel>
    fun setTitle(tabId: String, title: String)
}

class ServerUiTabboxConfiguration{
    var width:String? = null
    var height:String? = null
    val tools = arrayListOf<ServerUiTabTool>()
}

class ServerUiTabTool(val text:String, val handler:()->Unit)

class ServerUiTabPanel(val id:String, var title:String?, val comp: ServerUiComponent)


