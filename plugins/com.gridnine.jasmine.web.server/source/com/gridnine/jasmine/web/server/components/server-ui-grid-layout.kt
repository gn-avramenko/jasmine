/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.components

interface ServerUiGridLayoutContainer: ServerUiComponent {

    fun addRow(height:String?=null)

    fun addCell(cell: ServerUiGridLayoutCell)
}


class ServerUiGridLayoutCell(val comp: ServerUiComponent?, val columnSpan:Int =1)

class ServerUiGridLayoutContainerConfiguration{
    val columns = arrayListOf<ServerUiGridLayoutColumnConfiguration>()
    var width:String? = null
    var height:String? = null
    var noPadding = false
}

class ServerUiGridLayoutColumnConfiguration(val width:String?)

class ServerUiGridLayoutRowConfiguration(val height:String?)

class ServerUiGridLayoutRow(val config: ServerUiGridLayoutRowConfiguration){
    val cells = arrayListOf<ServerUiGridLayoutCell>()
}
