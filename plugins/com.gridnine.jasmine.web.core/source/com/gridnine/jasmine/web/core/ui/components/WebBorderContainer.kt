/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.components

import com.gridnine.jasmine.web.core.ui.WebComponent

interface WebBorderContainer : WebComponent{
    fun setNorthRegion(region:WebBorderLayoutRegion?)
    fun setWestRegion(region:WebBorderLayoutRegion?)
    fun setEastRegion(region:WebBorderLayoutRegion?)
    fun setSouthRegion(region:WebBorderLayoutRegion?)
    fun setCenterRegion(region:WebBorderLayoutRegion?)
    companion object{
        fun region(init: WebBorderLayoutRegion.()->Unit):WebBorderLayoutRegion{
            val result = WebBorderLayoutRegion()
            result.init()
            return result
        }
    }
}

class WebBorderLayoutConfiguration{
    var fit:Boolean = true
    var width:String? = null
    var height:String? = null
}

class WebBorderLayoutRegion() {
    var title:String? = null
    var showBorder:Boolean = false
    var showSplitLine:Boolean = false
    var collapsible:Boolean = false
    var width:Int? = null
    var height:Int? = null
    lateinit var content:WebComponent
}
