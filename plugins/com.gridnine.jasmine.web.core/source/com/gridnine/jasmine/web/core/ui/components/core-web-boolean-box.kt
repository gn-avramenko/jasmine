/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.components



interface WebBooleanBox:WebNode{
    fun getValue():Boolean
    fun setValue(value:Boolean)
    fun setEnabled(value:Boolean)
}

class WebBooleanBoxConfiguration:BaseWebComponentConfiguration(){
    lateinit var onText:String
    lateinit var offText:String
}