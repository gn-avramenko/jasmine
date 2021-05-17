/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.components


interface WebTilesContainer:WebNode,HasId{
    fun setExpandHandler(handler: (String) ->Unit)
}

class WebTilesContainerConfiguration:BaseWebComponentConfiguration(){
    val tiles = arrayListOf<WebTileConfiguration>()
    var tileWidth = DefaultUIParameters.controlWidth
    fun tile(id:String, title: String){
        tiles.add(WebTileConfiguration(id, title))
    }
}
class WebTileConfiguration(val id:String, val title:String)