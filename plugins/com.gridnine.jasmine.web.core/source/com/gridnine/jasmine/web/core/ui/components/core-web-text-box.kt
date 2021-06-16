/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.components



interface  WebTextBox:WebNode{
    fun getValue():String?
    fun setValue(value:String?)
    fun setDisabled(value:Boolean)
    fun showValidation(value: String?)
}

class WebTextBoxConfiguration:BaseWebComponentConfiguration(){
    var showClearIcon = false
    var multiline = false
}