/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.ui.components

import com.gridnine.jasmine.server.core.ui.common.BaseComponentConfiguration
import com.gridnine.jasmine.server.core.ui.common.UiNode


interface AccordionContainer : UiNode {
    fun addPanel(panel: AccordionPanel)
    fun removePanel(id:String)
    fun select(id:String)
    fun getPanels():List<AccordionPanel>
}

class AccordionContainerConfiguration: BaseComponentConfiguration()

data class AccordionPanel(val id:String, val title:String?, val content: UiNode)