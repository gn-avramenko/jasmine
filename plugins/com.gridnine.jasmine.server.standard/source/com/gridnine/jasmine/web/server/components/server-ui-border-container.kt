/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.components

interface ServerUiBorderContainer : ServerUiNode {
    fun setNorthRegion(region: ServerUiBorderContainerRegion)
    fun setWestRegion(region: ServerUiBorderContainerRegion)
    fun setEastRegion(region: ServerUiBorderContainerRegion)
    fun setSouthRegion(region: ServerUiBorderContainerRegion)
    fun setCenterRegion(region: ServerUiBorderContainerRegion)
}

class ServerUiBorderContainerConfiguration(){
    constructor(config:ServerUiBorderContainerConfiguration.() ->Unit):this(){
        config.invoke(this)
    }
    var width:String? = null
    var height:String? = null
}

class ServerUiBorderContainerRegion() {
    constructor(config:ServerUiBorderContainerRegion.()->Unit):this(){
        config.invoke(this)
    }
    var title:String? = null
    var showBorder:Boolean = false
    var showSplitLine:Boolean = false
    var collapsible:Boolean = false
    var collapsed:Boolean =false
    var width:String? = null
    var height:String? = null
    lateinit var content: ServerUiNode
}