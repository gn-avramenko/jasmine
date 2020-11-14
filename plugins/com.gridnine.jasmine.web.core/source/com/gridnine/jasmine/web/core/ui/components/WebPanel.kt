/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.components

import com.gridnine.jasmine.web.core.ui.HasDivId
import com.gridnine.jasmine.web.core.ui.WebComponent

interface WebPanel : WebComponent,HasDivId{
    fun setTitle(title:String)
    fun setToolHandler(handler:(String, WebPanel) ->Unit)
}

class WebPanelConfiguration{
    var fit:Boolean = true
    var width:String? = null
    var height:String? = null
    val tools = arrayListOf<PanelToolConfiguration>()
    lateinit var content:WebComponent
}

class PanelToolConfiguration(val id:String, val icon:String)
