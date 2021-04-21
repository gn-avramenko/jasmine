/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.standard.ui.mainframe

import com.gridnine.jasmine.common.core.app.RegistryItem
import com.gridnine.jasmine.common.core.app.RegistryItemType
import com.gridnine.jasmine.common.core.model.BaseIntrospectableObject
import com.gridnine.jasmine.common.core.model.ObjectReference
import com.gridnine.jasmine.server.core.ui.common.HasWeight
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


interface UiListItemHandler: RegistryItem<UiListItemHandler> {
    fun open(obj:ObjectReference<*>, navigationKey:String)
    override fun getType(): RegistryItemType<UiListItemHandler> {
        return TYPE
    }
    companion object{
        val TYPE = RegistryItemType<UiListItemHandler>("list-item-handlers")
    }
}