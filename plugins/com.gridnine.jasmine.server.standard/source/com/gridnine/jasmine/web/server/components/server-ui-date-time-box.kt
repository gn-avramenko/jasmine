/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.components

import java.time.LocalDateTime

interface ServerUiDateTimeBox: ServerUiNode {
    fun getValue():LocalDateTime?
    fun setValue(value:LocalDateTime?)
    fun setEnabled(value:Boolean)
    fun showValidation(value:String?)
}

class ServerUiDateTimeBoxConfiguration(){
    constructor(config: ServerUiDateTimeBoxConfiguration.()->Unit):this(){
        config.invoke(this)
    }
    var width:String? = null
    var height:String? = null
}