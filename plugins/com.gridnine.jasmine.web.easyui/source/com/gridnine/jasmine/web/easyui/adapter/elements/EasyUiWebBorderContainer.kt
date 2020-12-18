/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.easyui.adapter.elements

import com.gridnine.jasmine.server.core.model.common.XeptionJS
import com.gridnine.jasmine.web.core.ui.WebComponent
import com.gridnine.jasmine.web.core.ui.components.WebBorderContainer
import com.gridnine.jasmine.web.core.ui.components.WebBorderLayoutConfiguration
import com.gridnine.jasmine.web.core.ui.components.WebBorderLayoutRegion
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import com.gridnine.jasmine.web.easyui.adapter.jQuery

class EasyUiWebBorderContainer(private val parent:WebComponent?, configure: WebBorderLayoutConfiguration.()->Unit) :WebBorderContainer{

    private var initialized = false

    private var fit = false

    private var northRegion:WebBorderLayoutRegion? = null

    private var southRegion:WebBorderLayoutRegion?  = null

    private var eastRegion:WebBorderLayoutRegion?  = null
    private var westRegion:WebBorderLayoutRegion?  = null
    private var centerRegion:WebBorderLayoutRegion?  = null


    private val uid = MiscUtilsJS.createUUID()

    init {
        val configuration = WebBorderLayoutConfiguration()
        configuration.configure()
        fit = configuration.fit
    }


    override fun getHtml(): String {
        return "<div id = \"borderLayout$uid\"></div>"
    }

    private fun getSelector() = "#borderLayout$uid"

    override fun decorate() {
        jQuery(getSelector()).layout(object{
            val fit = this@EasyUiWebBorderContainer.fit
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


    private fun setRegion(region: WebBorderLayoutRegion?, regionCode:String, setter: (WebBorderLayoutRegion?) -> Unit){
        if(!initialized){
            setter.invoke(region)
            return
        }
        val existingRegion = when(regionCode){
            "center" -> centerRegion
            "east" -> eastRegion
            "west" -> westRegion
            "north" -> northRegion
            "south" -> southRegion
            else -> error("")
        }
        existingRegion?.let{it.content.destroy()}
        setter.invoke(null)
        if(region == null){
            if("center" == regionCode){
                throw XeptionJS.forDeveloper("unable to delete center region")
            }
            jQuery(getSelector()).layout("remove", regionCode)
            return
        }
        jQuery(getSelector()).layout("remove", regionCode)
        addRegion(region, regionCode)
    }

    override fun setCenterRegion(region: WebBorderLayoutRegion?) {
        setRegion(region, "center") {centerRegion = it}
    }

    override fun setEastRegion(region: WebBorderLayoutRegion?) {
        setRegion(region, "east") {eastRegion = it}
    }

    override fun setWestRegion(region: WebBorderLayoutRegion?) {
        setRegion(region, "west") {westRegion = it}
    }

    override fun setNorthRegion(region: WebBorderLayoutRegion?) {
        setRegion(region, "north") {northRegion = it}
    }

    override fun setSouthRegion(region: WebBorderLayoutRegion?) {
        setRegion(region, "south") {southRegion = it}
    }

    private fun addRegion(it: WebBorderLayoutRegion, region: String) {
        jQuery(getSelector()).layout("add", object{
            var id = "$region$uid"
            var title = it.title
            var border = it.showBorder
            var split = it.showSplitLine
            var collapsible = it.collapsible
            var collapsed = it.collapsed
            var content = it.content.getHtml()
            var width:Int? = it.width
            var height:Int? = it.height
            var region = region
        })
        it.content.decorate()
//        if(it.collapsed){
//            defferedContent[region] = it.content
//        } else {
//        }
    }

    override fun getParent(): WebComponent? {
        return parent
    }

    override fun getChildren(): MutableList<WebComponent> {
        return  arrayListOf(northRegion, southRegion, eastRegion, westRegion, centerRegion).mapNotNull { it?.content }.toMutableList()
    }

    override fun destroy() {
       getChildren().forEach {
           it.destroy()
       }
    }

    override fun getId(): String {
        return "borderLayout$uid"
    }

}