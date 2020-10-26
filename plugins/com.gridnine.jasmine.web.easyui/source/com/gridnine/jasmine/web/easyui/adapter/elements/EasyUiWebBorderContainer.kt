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
import com.gridnine.jasmine.web.core.ui.debugger
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import com.gridnine.jasmine.web.easyui.adapter.jQuery

class EasyUiWebBorderContainer(private val parent:WebComponent?, configure: WebBorderLayoutConfiguration.()->Unit) :WebBorderContainer{

    private var initialized = false

    private var fit = false

    private var defferedNorthRegion:WebBorderLayoutRegion? = null

    private var defferedSouthRegion:WebBorderLayoutRegion?  = null

    private var defferedEastRegion:WebBorderLayoutRegion?  = null
    private var defferedWestRegion:WebBorderLayoutRegion?  = null
    private var defferedCenterRegion:WebBorderLayoutRegion?  = null
    private val children = arrayListOf<WebComponent>()
    private val uid = MiscUtilsJS.createUUID()

    init {
        (parent?.getChildren() as MutableList<WebComponent>?)?.add(this)
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
        defferedWestRegion?.let {
            addRegion(it, "west")
            defferedWestRegion = null
        }
        defferedNorthRegion?.let {
            addRegion(it, "north")
            defferedNorthRegion = null
        }
        defferedSouthRegion?.let {
            addRegion(it, "south")
            defferedSouthRegion = null
        }
        defferedCenterRegion?.let {
            addRegion(it, "center")
            defferedCenterRegion = null
        }
        defferedEastRegion?.let {
            addRegion(it, "east")
            defferedEastRegion = null
        }
        initialized = true
    }

    private fun setRegion(region: WebBorderLayoutRegion?, regionCode:String, setter: (WebBorderLayoutRegion?) -> Unit){
        if(!initialized){
            setter.invoke(region)
            return
        }
        setter.invoke(null)
        if(region == null){
            if("center" == regionCode){
                throw XeptionJS.forDeveloper("unable to delete center region")
            }
            jQuery(getSelector()).layout("remove", regionCode)
            return
        }
        addRegion(region, regionCode)
    }

    override fun setCenterRegion(region: WebBorderLayoutRegion?) {
        setRegion(region, "center") {defferedCenterRegion = it}
    }

    override fun setEastRegion(region: WebBorderLayoutRegion?) {
        setRegion(region, "east") {defferedEastRegion = it}
    }

    override fun setWestRegion(region: WebBorderLayoutRegion?) {
        setRegion(region, "west") {defferedWestRegion = it}
    }

    override fun setNorthRegion(region: WebBorderLayoutRegion?) {
        setRegion(region, "north") {defferedNorthRegion = it}
    }

    override fun setSouthRegion(region: WebBorderLayoutRegion?) {
        setRegion(region, "south") {defferedSouthRegion = it}
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
        children.add(it.content)
//        if(it.collapsed){
//            defferedContent[region] = it.content
//        } else {
//        }
    }

    override fun getParent(): WebComponent? {
        return parent
    }

    override fun getChildren(): MutableList<WebComponent> {
        return children
    }

    override fun destroy() {
       children.forEach { it.destroy() }
    }

}