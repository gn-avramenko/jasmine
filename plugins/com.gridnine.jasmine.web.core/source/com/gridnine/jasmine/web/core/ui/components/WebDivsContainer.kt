/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.components

import com.gridnine.jasmine.web.core.ui.HasDivId
import com.gridnine.jasmine.web.core.ui.WebComponent

interface WebDivsContainer : WebComponent,HasDivId{
    fun addDiv(id:String, content:WebComponent)
    fun show(id:String)
    fun removeDiv(id:String)
    fun getDiv(id:String):WebComponent?
}

class WebDivsContainerConfiguration{
    var width:String? = null
    var height:String? = null
}


