/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.standard.ui.mainframe

import com.gridnine.jasmine.server.core.ui.common.UiNode
import com.gridnine.jasmine.server.core.ui.components.TabTool

interface MainFrameTabCallback{
    fun setTitle(title:String)
    fun close()
}

data class MainFrameTabData(var title:String, var content:UiNode)

interface MainFrameTabHandler<T:Any>{
    fun getTabId(obj:T):String
    fun createTabData(obj:T, callback: MainFrameTabCallback):MainFrameTabData
}


class MainFrameConfiguration {

    lateinit var title:String

    val tools = arrayListOf<TabTool>()

}