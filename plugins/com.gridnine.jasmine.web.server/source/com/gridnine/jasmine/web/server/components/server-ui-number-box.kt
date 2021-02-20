/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.components

import java.math.BigDecimal

interface ServerUiNumberBox: ServerUiComponent {
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