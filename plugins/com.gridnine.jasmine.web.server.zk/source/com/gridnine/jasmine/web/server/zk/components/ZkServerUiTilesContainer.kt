/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.components

import com.gridnine.jasmine.web.server.components.ServerUiNode
import com.gridnine.jasmine.web.server.components.ServerUiTileConfiguration
import com.gridnine.jasmine.web.server.components.ServerUiTilesContainer
import com.gridnine.jasmine.web.server.components.ServerUiTilesContainerConfiguration
import org.zkoss.zk.ui.HtmlBasedComponent
import org.zkoss.zk.ui.event.Events
import org.zkoss.zul.Div
import org.zkoss.zul.Label

open class ZkServerUiTilesContainer(private val config : ServerUiTilesContainerConfiguration) : ServerUiTilesContainer, ZkServerUiComponent(){

    private val tiles = arrayListOf<ServerUiTileConfiguration>()

    private var expandHandler:((String) -> Unit)? = null

    private var component:Div? = null

    override fun setExpandHandler(handler: (String) -> Unit) {
        expandHandler = handler
    }

    override fun setTiles(tiles: List<ServerUiTileConfiguration>) {
        if(this.tiles.equals(tiles)){
            return
        }
        this.tiles.clear()
        this.tiles.addAll(tiles)
        if(component!= null){
            setTilesInternal()
        }
    }

    private fun setTilesInternal() {
        ArrayList(component!!.getChildren<Div>()).forEach {
            component!!.removeChild(it)
        }
        tiles.forEach { tile ->
            val wrapper = Div()
            wrapper.sclass = "jasmine-web-tile"
            if(config.tileWidth != null){
                wrapper.style = "width:${config.tileWidth}"
            }
            wrapper.parent = component
            val tileCaption = Div()
            tileCaption.sclass = "jasmine-tile-caption"
            val tileLabel = Label(tile.title)
            tileLabel.parent = tileCaption
            tileCaption.parent = wrapper
            val expand = Div()
            expand.addEventListener(Events.ON_CLICK){
                expandHandler?.invoke(tile.id)
            }
            expand.sclass = "jasmine-tile-expand"
            expand.parent = wrapper
        }
    }

    override fun getZkComponent(): HtmlBasedComponent {
        if(component != null){
            return component!!
        }
        component = Div()
        if(config.width == "100%"){
            component!!.hflex = "1"
        } else if(config.width != null){
            component!!.width = config.width
        }
        if(config.height == "100%"){
            component!!.vflex = "1"
        }else if(config.height != null) {
            component!!.height = config.height
        }
        setTilesInternal()
        return component!!
    }


    override fun getParent(): ServerUiNode? {
        return parent
    }

}