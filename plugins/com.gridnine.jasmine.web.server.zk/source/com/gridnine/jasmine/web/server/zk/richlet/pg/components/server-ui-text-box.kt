/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.richlet.pg.components

import com.gridnine.jasmine.web.server.zk.richlet.pg.ServerUiComponent

interface ServerUiTextBox:ServerUiComponent{
    fun getValue():String?
    fun setValue(value:String?)
    fun showValidation(value:String?)
}

class ServerUiTextBoxConfiguration{
    var width:String? = null
    var height:String? = null
}