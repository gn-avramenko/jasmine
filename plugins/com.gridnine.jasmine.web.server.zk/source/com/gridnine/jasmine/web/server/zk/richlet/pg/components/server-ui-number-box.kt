/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.richlet.pg.components

import com.gridnine.jasmine.web.server.zk.richlet.pg.ServerUiComponent
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

interface ServerUiNumberBox:ServerUiComponent{
    fun getValue():BigDecimal?
    fun setValue(value:BigDecimal?)
    fun setEnabled(value:Boolean)
    fun showValidation(value:String?)
}

class ServerUiNumberBoxConfiguration{
    var width:String? = null
    var height:String? = null
    var precision = 2
}