/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.components

import com.gridnine.jasmine.server.core.model.common.SelectItem

interface ServerUiSelect: ServerUiNode {
    fun setLoaderParams(url:String, limit:Int=10, parameters: List<Pair<String,String?>>)
    fun getValues():List<SelectItem>
    fun setValues(values: List<SelectItem>)
    fun setPossibleValues(values: List<SelectItem>)
    fun showValidation(value:String?)
    fun setEnabled(value:Boolean)
    fun setChangeListener(value:((List<SelectItem>) ->Unit)?)
}

enum class ServerUiSelectDataType{
    LOCAL,
    REMOTE
}

class ServerUiSelectConfiguration(){
    constructor(config:ServerUiSelectConfiguration.()->Unit):this(){
        config.invoke(this)
    }
    var width:String? = null
    var height:String? = null
    var mode: ServerUiSelectDataType = ServerUiSelectDataType.LOCAL
    var editable = false
    var showClearIcon = true
    var multiple = false
    var showAllPossibleValues = false
}