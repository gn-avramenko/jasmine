/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.zk.ui.components

import com.gridnine.jasmine.server.core.ui.components.TileConfiguration
import com.gridnine.jasmine.server.core.ui.components.TilesContainer
import com.gridnine.jasmine.server.core.ui.components.TilesContainerConfiguration
import org.zkoss.zk.ui.HtmlBasedComponent
import org.zkoss.zk.ui.event.Events
import org.zkoss.zul.Div
import org.zkoss.zul.Label

open class ZkTilesContainer(configure: TilesContainerConfiguration.() -> Unit) : TilesContainer, ZkUiComponent{

    private val tiles = arrayListOf<TileConfiguration>()

    private var expandHandler:((String) -> Unit)? = null

    private var component:Div? = null

    private val config = TilesContainerConfiguration()

    init {
        config.configure()
    }

    override fun setExpandHandler(handler: (String) -> Unit) {
        expandHandler = handler
    }

    override fun setTiles(tiles: List<TileConfiguration>) {
        if(this.tiles == tiles){
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
        configureBasicParameters(component!!, config)
        setTilesInternal()
        return component!!
    }

}