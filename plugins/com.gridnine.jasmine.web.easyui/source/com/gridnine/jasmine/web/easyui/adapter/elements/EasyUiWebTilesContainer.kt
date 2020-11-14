/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.easyui.adapter.elements

import com.gridnine.jasmine.web.core.ui.WebComponent
import com.gridnine.jasmine.web.core.ui.components.WebPanel
import com.gridnine.jasmine.web.core.ui.components.WebPanelConfiguration
import com.gridnine.jasmine.web.core.ui.components.WebTilesContainer
import com.gridnine.jasmine.web.core.ui.components.WebTilesContainerConfiguration
import com.gridnine.jasmine.web.core.utils.HtmlUtilsJS
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import com.gridnine.jasmine.web.easyui.adapter.EasyUiUtils
import com.gridnine.jasmine.web.easyui.adapter.jQuery

class EasyUiWebTilesContainer(private val parent:WebComponent?, configure:WebTilesContainerConfiguration.()->Unit):WebTilesContainer{
    private var handler: ((String) -> Unit)? = null
    private val conf:WebTilesContainerConfiguration = WebTilesContainerConfiguration()
    private val uid = MiscUtilsJS.createUUID()
    private var initialized = false
    private var jq:dynamic = null

    init {
        conf.configure()
    }

    override fun setExpandHandler(handler: (String) -> Unit) {
        this.handler = handler
    }

    override fun getParent(): WebComponent? {
        return parent
    }

    override fun getChildren(): List<WebComponent> {
        return emptyList()
    }

    override fun getHtml(): String {
        return "<div id=\"tiles${uid}\" style=\"${if(conf.width != null) "width:${conf.width}" else ""};${if(conf.height != null) "height:${conf.height}" else ""}\"/>"
    }

    override fun decorate() {
        jq = jQuery("#${getId()}")
        conf.tiles.forEach {wtc ->
            jq.append("""
                <div id = "tile${wtc.id}${uid}" style="width:${conf.tileWidth}px" class ="jasmine-web-tile">
                     <div class="jasmine-tile-caption">${wtc.title}</div><div id="expand${wtc.id}${uid}" class="jasmine-tile-expand"></div>
                </div>
            """.trimIndent())
            jQuery("#expand${wtc.id}${uid}").click{
                handler?.invoke(wtc.id)
            }
        }
        initialized = true
    }

    override fun destroy() {
        //nooops
    }

    override fun getId(): String {
        return "tiles${uid}"
    }

}