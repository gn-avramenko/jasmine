/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.richlet.pg

import com.gridnine.jasmine.web.server.components.ServerUiTileConfiguration
import com.gridnine.jasmine.web.server.components.ServerUiTilesContainerConfiguration
import com.gridnine.jasmine.web.server.zk.components.ZkServerUiTilesContainer
import org.zkoss.zk.ui.util.Clients

class TilesPanel : ZkServerUiTilesContainer(createConfiguration()) {

    init{
        setTiles(arrayListOf(ServerUiTileConfiguration("tile1", "Tile 1"), ServerUiTileConfiguration("tile2", "Tile 2")))
        setExpandHandler {
            Clients.alert("expanded $it")
        }

    }
    companion object{
        private fun createConfiguration(): ServerUiTilesContainerConfiguration {
            val result = ServerUiTilesContainerConfiguration()
            result.width = "100%"
            result.height = "100%"
            result.tileWidth = "200px"
            return result
        }
    }
}