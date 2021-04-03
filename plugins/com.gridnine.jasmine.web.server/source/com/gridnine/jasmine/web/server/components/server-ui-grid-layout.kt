/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.components

interface ServerUiGridLayoutContainer: ServerUiNode {

    fun addRow(height:String?=null)

    fun addCell(cell: ServerUiGridLayoutCell)
}


class ServerUiGridLayoutCell(val comp: ServerUiNode?, val columnSpan:Int =1, val sClass:String? = null)

class ServerUiGridLayoutContainerConfiguration(){
    constructor(config:ServerUiGridLayoutContainerConfiguration.()->Unit):this(){
        config.invoke(this)
    }
    val columns = arrayListOf<ServerUiGridLayoutColumnConfiguration>()
    var width:String? = null
    var height:String? = null
    var noPadding = false
}

class ServerUiGridLayoutColumnConfiguration(val width:String?=null)

class ServerUiGridLayoutRowConfiguration(val height:String?)

class ServerUiGridLayoutRow(val config: ServerUiGridLayoutRowConfiguration){
    val cells = arrayListOf<ServerUiGridLayoutCell>()
}
