/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("unused")

package com.gridnine.jasmine.web.core.ui.components


interface WebBorderContainer : WebNode{
    fun setNorthRegion(configure:WebBorderLayoutRegionConfiguration.()->Unit)
    fun setWestRegion(configure:WebBorderLayoutRegionConfiguration.()->Unit)
    fun setEastRegion(configure:WebBorderLayoutRegionConfiguration.()->Unit)
    fun setSouthRegion(configure:WebBorderLayoutRegionConfiguration.()->Unit)
    fun setCenterRegion(configure:WebBorderLayoutRegionConfiguration.()->Unit)
}

class WebBorderContainerConfiguration:BaseWebComponentConfiguration(){
    var fit:Boolean = true
}

class WebBorderLayoutRegionConfiguration {
    var width:Int? = null
    var height:Int? = null
    var title:String? = null
    var showBorder:Boolean = false
    var showSplitLine:Boolean = false
    var collapsible:Boolean = false
    var collapsed:Boolean =false
    lateinit var content:WebNode
}
