/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.components

import com.gridnine.jasmine.web.core.ui.HasDivId
import com.gridnine.jasmine.web.core.ui.WebComponent
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS

interface WebGridLayoutContainer : WebComponent,HasDivId{
    fun defineColumn(width:String?=null);

    fun addRow(height:String?=null)

    fun addCell(cell:WebGridLayoutCell)
}


class WebGridLayoutCell(val comp:WebComponent?, val columnSpan:Int =1)

class WebGridLayoutContainerConfiguration{
    var uid = MiscUtilsJS.createUUID()
    var width:String? = null
    var height:String? = null
}

class WebGridLayoutColumnConfiguration(val width:String?)

class WebGridLayoutRowConfiguration(val height:String?)

class GridLayoutRow(val config:WebGridLayoutRowConfiguration){
    val cells = arrayListOf<WebGridLayoutCell>()
}
