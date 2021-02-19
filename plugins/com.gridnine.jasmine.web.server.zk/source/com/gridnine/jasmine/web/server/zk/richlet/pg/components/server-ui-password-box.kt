/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.richlet.pg.components

import com.gridnine.jasmine.web.server.zk.richlet.pg.ServerUiComponent

interface ServerUiPasswordBox:ServerUiComponent{
    fun getValue():String?
    fun setValue(value:String?)
    fun showValidation(value:String?)
}

class ServerUiPasswordBoxConfiguration{
    var width:String? = null
    var height:String? = null
}