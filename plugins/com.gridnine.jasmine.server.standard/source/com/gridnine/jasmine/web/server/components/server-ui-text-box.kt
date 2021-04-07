/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.components

interface ServerUiTextBox: ServerUiNode {
    fun getValue():String?
    fun setValue(value:String?)
    fun showValidation(value:String?)
    fun setActionListener(listener: (String?) -> Unit)
    fun setDisabled(value:Boolean)
}

class ServerUiTextBoxConfiguration(){
    constructor(config:ServerUiTextBoxConfiguration.()->Unit):this(){
        config.invoke(this)
    }
    var width:String? = null
    var height:String? = null
}