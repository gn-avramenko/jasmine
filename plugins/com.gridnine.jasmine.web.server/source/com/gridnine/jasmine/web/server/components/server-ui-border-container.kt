/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.components

interface ServerUiBorderContainer : ServerUiComponent {
    fun setNorthRegion(region: ServerUiBorderContainerRegion)
    fun setWestRegion(region: ServerUiBorderContainerRegion)
    fun setEastRegion(region: ServerUiBorderContainerRegion)
    fun setSouthRegion(region: ServerUiBorderContainerRegion)
    fun setCenterRegion(region: ServerUiBorderContainerRegion)
}

class ServerUiBorderContainerConfiguration{
    var width:String? = null
    var height:String? = null
}

class ServerUiBorderContainerRegion {
    var title:String? = null
    var showBorder:Boolean = false
    var showSplitLine:Boolean = false
    var collapsible:Boolean = false
    var collapsed:Boolean =false
    var width:String? = null
    var height:String? = null
    lateinit var content: ServerUiComponent
}