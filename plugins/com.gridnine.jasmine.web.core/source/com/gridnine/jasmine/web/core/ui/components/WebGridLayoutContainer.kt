/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.components

import com.gridnine.jasmine.web.core.ui.WebComponent

interface WebGridLayoutContainer : WebComponent{

    fun defineColumn(width:String?=null);

    fun addRow(height:String?=null)

    fun addCell(cell:WebGridLayoutCell)
}


class WebGridLayoutCell(val comp:WebComponent?, val columnSpan:Int =1)

class WebGridLayoutContainerConfiguration{
    var width:String? = null
    var height:String? = null
}