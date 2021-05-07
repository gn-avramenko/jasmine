/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("unused")

package com.gridnine.jasmine.web.core.ui.components

import com.gridnine.jasmine.common.core.model.SelectItemJS


interface WebSelect:WebNode{
    fun setLoader(loader: suspend (String) ->List<SelectItemJS>)
    fun getValues():List<SelectItemJS>
    fun setValues(values: List<SelectItemJS>)
    fun setPossibleValues(values: List<SelectItemJS>)
    fun showValidation(value:String?)
    fun setEnabled(value:Boolean)
    fun setChangeListener(value: suspend (List<SelectItemJS>) ->Unit)
}

enum class SelectDataType{
    LOCAL,
    REMOTE
}

class WebSelectConfiguration:BaseWebComponentConfiguration(){
    var mode:SelectDataType = SelectDataType.LOCAL
    var editable = false
    var showClearIcon = true
    var hasDownArrow = true
    var multiple = false
}