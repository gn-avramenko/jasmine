/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.components

import kotlin.js.Date


interface WebDateBox:WebNode{
    fun getValue():Date?
    fun setValue(value:Date?)
    fun setEnabled(value:Boolean)
    fun showValidation(value:String?)
}

class WebDateBoxConfiguration:BaseWebComponentConfiguration(){
    var showClearIcon = false
}