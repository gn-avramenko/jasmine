/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.components

import com.gridnine.jasmine.web.core.ui.WebComponent

interface WebTabsContainer : WebComponent{
    fun addTab(panel:WebTabPanel)
    fun removeTab(idx:Int)
    fun select(idx:Int)
    fun getPanels():List<WebTabPanel>
    companion object{
        fun tab(init: WebTabPanel.()->Unit):WebTabPanel{
            val result = WebTabPanel()
            result.init()
            return result
        }
    }
}

class WebTabsContainerConfiguration{
    var fit:Boolean = true
    var width:String? = null
    var height:String? = null
    val tools = arrayListOf<BaseButtonConfiguration>()
}

class WebTabPanel {
    var title:String? = null
    lateinit var content:WebComponent
}


