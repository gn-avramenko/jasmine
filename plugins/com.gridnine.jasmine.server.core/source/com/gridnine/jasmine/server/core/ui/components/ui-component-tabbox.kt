/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.ui.components

import com.gridnine.jasmine.server.core.ui.common.BaseComponentConfiguration
import com.gridnine.jasmine.server.core.ui.common.UiNode

interface Tabbox : UiNode {
    fun addTab(panel: TabPanel)
    fun removeTab(id:String)
    fun select(id:String): TabPanel?
    fun getTabs():List<TabPanel>
    fun setTitle(tabId: String, title: String)
}

class TabboxConfiguration: BaseComponentConfiguration(){
    val tools = arrayListOf<TabTool>()
}

class TabTool(val text:String, val handler:()->Unit)

class TabPanel(val id:String, var title:String?, val comp: UiNode)


