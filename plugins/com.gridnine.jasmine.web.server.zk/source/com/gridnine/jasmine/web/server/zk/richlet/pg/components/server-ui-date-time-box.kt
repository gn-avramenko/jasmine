/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.richlet.pg.components

import com.gridnine.jasmine.web.server.zk.richlet.pg.ServerUiComponent
import java.time.LocalDate
import java.time.LocalDateTime

interface ServerUiDateTimeBox:ServerUiComponent{
    fun getValue():LocalDateTime?
    fun setValue(value:LocalDateTime?)
    fun setEnabled(value:Boolean)
    fun showValidation(value:String?)
}

class ServerUiDateTimeBoxConfiguration{
    var width:String? = null
    var height:String? = null
}