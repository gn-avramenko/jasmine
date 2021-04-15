/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.ui.components

import com.gridnine.jasmine.server.core.ui.common.BaseComponentConfiguration
import com.gridnine.jasmine.server.core.ui.common.UiNode

interface BorderContainer : UiNode {
    fun setNorthRegion(configure: BorderContainerRegion.()->Unit)
    fun setWestRegion(configure: BorderContainerRegion.()->Unit)
    fun setEastRegion(configure: BorderContainerRegion.()->Unit)
    fun setSouthRegion(configure: BorderContainerRegion.()->Unit)
    fun setCenterRegion(configure: BorderContainerRegion.()->Unit)
}

class BorderContainerConfiguration: BaseComponentConfiguration()

class BorderContainerRegion{
    var title:String? = null
    var showBorder:Boolean = false
    var showSplitLine:Boolean = false
    var collapsible:Boolean = false
    var collapsed:Boolean =false
    var width:String? = null
    var height:String? = null
    lateinit var content: UiNode
}