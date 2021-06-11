/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.components



interface  WebPasswordBox:WebNode{
    fun getValue():String?
    fun setValue(value:String?)
    fun setDisabled(value:Boolean)
    fun showValidation(value: String?)
}

class WebPasswordBoxConfiguration:BaseWebComponentConfiguration()