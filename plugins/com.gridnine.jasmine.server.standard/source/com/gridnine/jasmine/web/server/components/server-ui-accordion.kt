/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.components

interface ServerUiAccordionContainer : ServerUiNode {
    fun addPanel(panel: ServerUiAccordionPanel)
    fun removePanel(id:String)
    fun select(id:String)
    fun getPanels():List<ServerUiAccordionPanel>
}

data class ServerUiAccordionContainerConfiguration(val width:String? = null, val height:String? = null)

data class ServerUiAccordionPanel(val id:String, val title:String?, val content: ServerUiNode)