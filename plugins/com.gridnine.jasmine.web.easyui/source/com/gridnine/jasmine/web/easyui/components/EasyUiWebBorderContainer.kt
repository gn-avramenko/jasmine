/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("unused")

package com.gridnine.jasmine.web.easyui.components

import com.gridnine.jasmine.web.core.ui.components.WebBorderContainer
import com.gridnine.jasmine.web.core.ui.components.WebBorderContainerConfiguration
import com.gridnine.jasmine.web.core.ui.components.WebBorderLayoutRegionConfiguration
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS

class EasyUiWebBorderContainer(configure: WebBorderContainerConfiguration.()->Unit) :WebBorderContainer,EasyUiComponent{

    private var initialized = false

    private val configuration = WebBorderContainerConfiguration()

    private var northRegion:(WebBorderLayoutRegionConfiguration.()->Unit)? = null

    private var southRegion:(WebBorderLayoutRegionConfiguration.()->Unit)? = null

    private var eastRegion:(WebBorderLayoutRegionConfiguration.()->Unit)? = null
    private var westRegion:(WebBorderLayoutRegionConfiguration.()->Unit)? = null
    private var centerRegion:(WebBorderLayoutRegionConfiguration.()->Unit)?  = null
    private val contentsList = arrayListOf<EasyUiComponent>()

    private val uid = MiscUtilsJS.createUUID()

    init {
        configuration.configure()
    }


    override fun getHtml(): String {
        return "<div id = \"borderLayout$uid\"></div>"
    }

    override fun getId(): String {
        return "borderLayout$uid"
    }

    private fun getSelector() = "#borderLayout$uid"

    override fun decorate() {
        jQuery(getSelector()).layout(object{
            val fit = configuration.fit
        })
        westRegion?.let {
            addRegion(it, "west")
        }
        northRegion?.let {
            addRegion(it, "north")
        }
        southRegion?.let {
            addRegion(it, "south")
        }
        centerRegion?.let {
            addRegion(it, "center")
        }
        eastRegion?.let {
            addRegion(it, "east")
        }
        initialized = true
    }


    private fun setRegion(region: (WebBorderLayoutRegionConfiguration.()->Unit), regionCode:String, setter: (WebBorderLayoutRegionConfiguration.()->Unit) -> Unit){
        if(!initialized){
            setter.invoke(region)
            return
        }
        addRegion(region, regionCode)
    }

    override fun setCenterRegion(configure: (WebBorderLayoutRegionConfiguration.()->Unit)) {
        setRegion(configure, "center") {centerRegion = it}
    }

    override fun setEastRegion(configure: WebBorderLayoutRegionConfiguration.()->Unit) {
        setRegion(configure, "east") {eastRegion = it}
    }

    override fun setWestRegion(configure: WebBorderLayoutRegionConfiguration.()->Unit) {
        setRegion(configure, "west") {westRegion = it}
    }

    override fun setNorthRegion(configure: WebBorderLayoutRegionConfiguration.()->Unit) {
        setRegion(configure, "north") {northRegion = it}
    }

    override fun setSouthRegion(configure: WebBorderLayoutRegionConfiguration.()->Unit) {
        setRegion(configure, "south") {southRegion = it}
    }

    private fun addRegion(configure: WebBorderLayoutRegionConfiguration.()->Unit, region: String) {
        val config = WebBorderLayoutRegionConfiguration()
        config.configure()
        val elm = findEasyUiComponent(config.content)
        contentsList.add(elm)
        jQuery(getSelector()).layout("add", object{
            var id = "$region$uid"
            var title = config.title
            var border = config.showBorder
            var split = config.showSplitLine
            var collapsible = config.collapsible
            var collapsed = config.collapsed
            var content = elm.getHtml()
            var width:Int? = config.width
            var height:Int? = config.height
            var region = region
        })
        elm.decorate()
    }

    override fun destroy() {
        contentsList.forEach {
            findEasyUiComponent(it).destroy()
        }
    }

}