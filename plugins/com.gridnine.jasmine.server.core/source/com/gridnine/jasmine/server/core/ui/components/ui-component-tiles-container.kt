/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.ui.components

import com.gridnine.jasmine.server.core.ui.common.BaseComponentConfiguration
import com.gridnine.jasmine.server.core.ui.common.UiNode

interface TilesContainer: UiNode {
    fun setExpandHandler(handler: (String) ->Unit)
    fun setTiles(tiles: List<TileConfiguration>)
}

class TilesContainerConfiguration: BaseComponentConfiguration(){
    var tileWidth:String? = null
}
data class TileConfiguration(val id:String, val title:String)