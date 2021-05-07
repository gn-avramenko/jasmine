/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.components

import kotlin.js.Date


interface WebDateTimeBox:WebNode{
    fun getValue():Date?
    fun setValue(value:Date?)
    fun setEnabled(value:Boolean)
    fun showValidation(value:String?)
}

class WebDateTimeBoxConfiguration:BaseWebComponentConfiguration(){
    var showClearIcon = false
    var showSeconds = false
}