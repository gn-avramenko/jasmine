/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.components

import com.gridnine.jasmine.web.core.ui.HasDivId
import com.gridnine.jasmine.web.core.ui.WebComponent

interface WebTree: WebComponent,HasDivId{
    fun setData(data:List<WebTreeNode>)
}

class WebTreeNode(val id:String, val text:String, val userData:Any?, val children:MutableList<WebTreeNode> = arrayListOf<WebTreeNode>())

class WebTreeConfiguration{
    var fit = true
    var width:String? = null
    var height:String? = null
    var enableDnd = false
}