/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.components

import com.gridnine.jasmine.web.core.ui.WebComponent


interface  WebPasswordBox:WebComponent{
    fun getValue():String?
    fun setValue(value:String?)
    fun setDisabled(value:Boolean)
    fun resetValidation()
    fun showValidation(value: String)
}

class WebPasswordBoxConfiguration{
    var width:String? = null
    var height:String? = null
    var prompt:String? = null
    var showClearIcon = false
    var showEye = true
}