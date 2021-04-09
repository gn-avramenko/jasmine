/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.components


interface ServerUiLinkButton: ServerUiNode {
    fun setHandler(handler:()-> Unit)
    fun setEnabled(value:Boolean)
}

class ServerUiLinkButtonConfiguration(){
    constructor(config:ServerUiLinkButtonConfiguration.()->Unit):this(){
        config.invoke(this)
    }
    var title:String? = null
    var width:String?=null
    var height:String?=null
    var iconClass:String? = null
}