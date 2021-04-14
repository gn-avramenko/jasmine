/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.ui.components

import com.gridnine.jasmine.server.core.ui.common.BaseComponentConfiguration
import com.gridnine.jasmine.server.core.ui.common.UiNode


interface LinkButton: UiNode {
    fun setHandler(handler:()-> Unit)
    fun setEnabled(value:Boolean)
}

class LinkButtonConfiguration: BaseComponentConfiguration(){
    var title:String? = null
    var iconClass:String? = null
}