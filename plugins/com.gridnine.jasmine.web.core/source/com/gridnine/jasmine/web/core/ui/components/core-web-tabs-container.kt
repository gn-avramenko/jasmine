/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.components

import com.gridnine.jasmine.web.core.utils.MiscUtilsJS

interface WebTabsContainer : WebNode{
    fun addTab(configure:WebTabPanel.()->Unit)
    fun removeTab(id:String)
    fun select(id:String):WebNode?
    fun getTabs():List<WebTabPanel>
    fun setTitle(tabId: String, title: String)
}

class WebTabsContainerConfiguration:BaseWebComponentConfiguration(){
    var fit:Boolean = true
}

class WebTabPanel {
    var id = MiscUtilsJS.createUUID()
    var title:String? = null
    lateinit var content:WebNode
}


