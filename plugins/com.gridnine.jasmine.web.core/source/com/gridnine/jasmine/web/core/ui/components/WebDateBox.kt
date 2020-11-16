/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.components

import com.gridnine.jasmine.web.core.ui.WebComponent
import kotlin.js.Date


interface WebDateBox:WebComponent{
    fun getValue():Date?
    fun setValue(value:Date?)
    fun setEnabled(value:Boolean)
    fun showValidation(value:String?)
}

class WebDateBoxConfiguration{
    var width:String? = null
    var height:String? = null
    var showClearIcon = false
}