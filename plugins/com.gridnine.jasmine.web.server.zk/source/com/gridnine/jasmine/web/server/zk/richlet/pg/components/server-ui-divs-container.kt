/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.richlet.pg.components

import com.gridnine.jasmine.web.server.zk.richlet.pg.ServerUiComponent

interface ServerUiDivsContainer : ServerUiComponent{
    fun addDiv(id:String, content:ServerUiComponent)
    fun show(id:String)
    fun removeDiv(id:String)
    fun getDiv(id:String):ServerUiComponent?
}

class ServerUiDivsContainerConfiguration{
    var width:String? = null
    var height:String? = null
}