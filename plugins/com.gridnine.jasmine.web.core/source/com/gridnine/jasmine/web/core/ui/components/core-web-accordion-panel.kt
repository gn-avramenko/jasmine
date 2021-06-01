/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.components

import com.gridnine.jasmine.web.core.utils.MiscUtilsJS

interface WebAccordionContainer : WebNode{
    fun addPanel(configure:WebAccordionPanel.()->Unit)
    fun removePanel(id:String)
    fun select(id:String)
    fun getPanels():List<WebAccordionPanel>
}

class WebAccordionContainerConfiguration:BaseWebComponentConfiguration(){
    var fit:Boolean = true
}

class WebAccordionPanel {
    var id:String = MiscUtilsJS.createUUID()
    var title:String? = null
    lateinit var content:WebNode
}