/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.components

import com.gridnine.jasmine.web.core.ui.WebComponent
import kotlin.js.Promise


interface WebSelect:WebComponent{
    fun setLoader(loader: (String) ->Promise<List<SelectItemJS>>)
}

class WebSelectConfiguration{
    var width:String? = null
    var height:String? = null
    var mode:ComboboxMode = ComboboxMode.LOCAL
    var editable = false
    var showClearIcon = true
    var hasDownArrow = true
    var multiple = false
}