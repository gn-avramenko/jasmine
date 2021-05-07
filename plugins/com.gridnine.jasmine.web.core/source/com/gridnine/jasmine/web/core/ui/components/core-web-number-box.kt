/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.components

interface WebNumberBox:WebNode{
    fun getValue():Double?
    fun setValue(value:Double?)
    fun setEnabled(value:Boolean)
    fun showValidation(value:String?)
}

class WebNumberBoxConfiguration:BaseWebComponentConfiguration(){
    var showClearIcon = false
    var precision = 2
}