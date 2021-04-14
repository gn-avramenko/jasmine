/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.ui.components

import com.gridnine.jasmine.server.core.ui.common.BaseComponentConfiguration
import com.gridnine.jasmine.server.core.ui.common.UiNode

interface Panel : UiNode {
    fun setTitle(title:String)
    fun setMaximizeHandler(handler:() ->Unit)
    fun setMinimizeHandler(handler:() ->Unit)
    fun setContent(comp: UiNode?)
}

class PanelConfiguration: BaseComponentConfiguration(){
    var maximizable = false
    var minimizable = false
}
