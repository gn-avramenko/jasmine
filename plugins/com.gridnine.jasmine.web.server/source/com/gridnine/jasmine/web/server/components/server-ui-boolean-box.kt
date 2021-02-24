/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.components

interface ServerUiBooleanBox: ServerUiNode {
    fun getValue():Boolean
    fun setValue(value:Boolean)
    fun setEnabled(value:Boolean)
}

class ServerUiBooleanBoxConfiguration{
    var width:String? = null
    var height:String? = null
}