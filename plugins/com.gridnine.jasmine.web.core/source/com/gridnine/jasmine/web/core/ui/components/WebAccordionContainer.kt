/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.components

import com.gridnine.jasmine.web.core.ui.WebComponent

interface WebAccordionContainer : WebComponent{
    fun addPanel(panel:WebAccordionPanel)
    fun removePanel(idx:Int)
    fun select(idx:Int)
    fun getPanels():List<WebAccordionPanel>
    companion object{
        fun panel(init: WebAccordionPanel.()->Unit):WebAccordionPanel{
            val result = WebAccordionPanel()
            result.init()
            return result
        }
    }
}

class WebAccordionPanelConfiguration{
    var fit:Boolean = true
    var width:String? = null
    var height:String? = null
}

class WebAccordionPanel() {
    var title:String? = null
    lateinit var content:WebComponent
}