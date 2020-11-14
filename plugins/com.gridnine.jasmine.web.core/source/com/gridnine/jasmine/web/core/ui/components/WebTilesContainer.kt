/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.components

import com.gridnine.jasmine.web.core.ui.DefaultUIParameters
import com.gridnine.jasmine.web.core.ui.HasDivId
import com.gridnine.jasmine.web.core.ui.WebComponent
import com.gridnine.jasmine.web.core.utils.UiUtils

interface WebTilesContainer:WebComponent,HasDivId{
    fun setExpandHandler(handler: (String) ->Unit)
}

class WebTilesContainerConfiguration{
    var width:String? = null
    var height:String? = null
    val tiles = arrayListOf<WebTileConfiguration>()
    var tileWidth = DefaultUIParameters.controlWidth
    fun tile(id:String, title: String){
        tiles.add(WebTileConfiguration(id, title))
    }
}
class WebTileConfiguration(val id:String, val title:String)