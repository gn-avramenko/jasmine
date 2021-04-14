/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.ui.components

import com.gridnine.jasmine.server.core.ui.common.BaseComponentConfiguration
import com.gridnine.jasmine.server.core.ui.common.UiNode

interface DivsContainer : UiNode {
    fun addDiv(id:String, content: UiNode)
    fun show(id:String)
    fun removeDiv(id:String)
    fun getDiv(id:String): UiNode?
    fun clear()
}

class DivsContainerConfiguration: BaseComponentConfiguration()