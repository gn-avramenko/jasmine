/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.components

import com.gridnine.jasmine.web.core.ui.WebComponent


interface  WebSearchBox:WebComponent{
    fun setSearcher(value: (String?) ->Unit)
    fun getValue():String?
    fun setEnabled(value:Boolean)
}

class WebSearchBoxConfiguration{
    var width:String? = null
    var height:String? = null
    var prompt:String? = null
}