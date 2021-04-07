/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.components

interface ServerUiLabel: ServerUiNode {
    fun setText(value: String?)
}

class ServerUiLabelConfiguration(){
    constructor(config:ServerUiLabelConfiguration.()->Unit):this(){
        config.invoke(this)
    }
    var width:String?=null
    var height:String?=null
    var multiline = false
}