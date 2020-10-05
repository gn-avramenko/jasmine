/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.components

import com.gridnine.jasmine.web.core.ui.WebComponent


interface  WebTextBox:WebComponent{
    fun getValue():String?
    fun setValue(value:String?)
}

class WebTextBoxConfiguration{
    var width:String? = null
    var height:String? = null
    var prompt:String? = null
    var showClearIcon = false
}