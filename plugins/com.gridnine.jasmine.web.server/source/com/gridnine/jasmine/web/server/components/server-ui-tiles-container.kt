/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.components

interface ServerUiTilesContainer: ServerUiNode {
    fun setExpandHandler(handler: (String) ->Unit)
    fun setTiles(tiles: List<ServerUiTileConfiguration>)
}

class ServerUiTilesContainerConfiguration{
    var width:String? = null
    var height:String? = null
    var tileWidth:String? = null
}
data class ServerUiTileConfiguration(val id:String, val title:String)