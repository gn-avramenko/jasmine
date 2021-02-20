/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.components

import java.time.LocalDate

interface ServerUiDateBox: ServerUiComponent {
    fun getValue():LocalDate?
    fun setValue(value:LocalDate?)
    fun setEnabled(value:Boolean)
    fun showValidation(value:String?)
}

class ServerUiDateBoxConfiguration{
    var width:String? = null
    var height:String? = null
}