/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.components

import com.gridnine.jasmine.web.core.ui.WebComponent
import kotlin.js.Date


interface WebNumberBox:WebComponent{
    fun getValue():Double?
    fun setValue(value:Double?)
    fun setEnabled(value:Boolean)
    fun showValidation(value:String?)
}

class WebNumberBoxConfiguration{
    var width:String? = null
    var height:String? = null
    var showClearIcon = false
    var precision = 2
}