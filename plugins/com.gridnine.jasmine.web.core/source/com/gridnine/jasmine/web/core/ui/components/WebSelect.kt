/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.components

import com.gridnine.jasmine.web.core.ui.WebComponent
import kotlin.js.Promise


interface WebSelect:WebComponent{
    fun setLoader(loader: (String) ->Promise<List<SelectItemJS>>)
    fun getValues():List<SelectItemJS>
    fun setValues(map: List<SelectItemJS>)
    fun setPossibleValues(values: List<SelectItemJS>)
    fun showValidation(value:String?)
    fun setEnabled(value:Boolean)
    fun setChangeListener(value:((List<SelectItemJS>) ->Unit)?)
}

data class SelectItemJS(val id:String, val text:String){
    override fun equals(other: Any?): Boolean {
        return other is SelectItemJS && other.id == id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

enum class SelectDataType{
    LOCAL,
    REMOTE
}

class WebSelectConfiguration{
    var width:String? = null
    var height:String? = null
    var mode:SelectDataType = SelectDataType.LOCAL
    var editable = false
    var showClearIcon = true
    var hasDownArrow = true
    var multiple = false
}