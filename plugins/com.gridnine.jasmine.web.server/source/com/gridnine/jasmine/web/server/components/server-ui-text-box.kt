/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.components

interface ServerUiTextBox: ServerUiComponent {
    fun getValue():String?
    fun setValue(value:String?)
    fun showValidation(value:String?)
}

class ServerUiTextBoxConfiguration{
    var width:String? = null
    var height:String? = null
}