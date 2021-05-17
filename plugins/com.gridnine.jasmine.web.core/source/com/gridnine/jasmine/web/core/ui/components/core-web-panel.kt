/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.components


interface WebPanel : WebNode,HasId{
    fun setTitle(title:String)
    fun setToolHandler(handler: suspend (String, WebPanel) ->Unit)
}

class WebPanelConfiguration:BaseWebComponentConfiguration(){
    var fit:Boolean = true
    val tools = arrayListOf<PanelToolConfiguration>()
    lateinit var content:WebNode
}

class PanelToolConfiguration(val id:String, val icon:String)
