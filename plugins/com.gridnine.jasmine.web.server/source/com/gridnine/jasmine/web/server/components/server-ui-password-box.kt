/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.components

interface ServerUiPasswordBox: ServerUiNode {
    fun getValue():String?
    fun setValue(value:String?)
    fun showValidation(value:String?)
}

class ServerUiPasswordBoxConfiguration{
    var width:String? = null
    var height:String? = null
}