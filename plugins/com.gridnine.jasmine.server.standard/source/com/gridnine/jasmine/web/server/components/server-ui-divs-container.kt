/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.components

interface ServerUiDivsContainer : ServerUiNode {
    fun addDiv(id:String, content: ServerUiNode)
    fun show(id:String)
    fun removeDiv(id:String)
    fun getDiv(id:String): ServerUiNode?
    fun clear()
}

class ServerUiDivsContainerConfiguration(){
    constructor(config:ServerUiDivsContainerConfiguration.()->Unit):this(){
        config.invoke(this)
    }
    var width:String? = null
    var height:String? = null
}