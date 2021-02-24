/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.components

interface ServerUiPanel : ServerUiNode {
    fun setTitle(title:String)
    fun setMaximizeHandler(handler:() ->Unit)
    fun setMinimizeHandler(handler:() ->Unit)
    fun setContent(comp: ServerUiNode?)
}

class ServerUiPanelConfiguration{
    var width:String? = null
    var height:String? = null
    var maximizable = false
    var minimizable = false
}
