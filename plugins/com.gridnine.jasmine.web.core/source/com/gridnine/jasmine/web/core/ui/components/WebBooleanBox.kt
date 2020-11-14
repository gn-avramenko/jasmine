/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.components

import com.gridnine.jasmine.web.core.ui.WebComponent


interface WebBooleanBox:WebComponent{
    fun getValue():Boolean
    fun setValue(value:Boolean)
    fun setEnabled(value:Boolean)
}

class WebBooleanBoxConfiguration{
    var width:String? = null
    var height:String? = null
    lateinit var onText:String
    lateinit var offText:String
}