/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.components

import com.gridnine.jasmine.web.core.ui.WebComponent
import kotlin.js.Promise


interface WebTagBox:WebComponent{
    fun setLoader(value: (String?) -> Promise<List<SelectItemJS>>)
    fun setPossibleValues(items:List<SelectItemJS>)
    fun getValues():List<String>
    fun setValues(items:List<String>)
}


class WebTagBoxConfiguration{
    var width:String? = null
    var height:String? = null
    var mode:ComboboxMode = ComboboxMode.LOCAL
    var editable = false
    var showClearIcon = true
    var limitToList = true
    var hasDownArrow = true
}