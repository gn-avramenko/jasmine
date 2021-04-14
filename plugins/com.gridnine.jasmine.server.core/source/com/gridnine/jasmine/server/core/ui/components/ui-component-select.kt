/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.ui.components

import com.gridnine.jasmine.common.core.model.SelectItem
import com.gridnine.jasmine.server.core.ui.common.BaseComponentConfiguration
import com.gridnine.jasmine.server.core.ui.common.UiNode

interface Select: UiNode {
    fun setLoaderParams(url:String, limit:Int=10, parameters: List<Pair<String,String?>>)
    fun getValues():List<SelectItem>
    fun setValues(values: List<SelectItem>)
    fun setPossibleValues(values: List<SelectItem>)
    fun showValidation(value:String?)
    fun setEnabled(value:Boolean)
    fun setChangeListener(value:((List<SelectItem>) ->Unit)?)
}

enum class SelectDataType{
    LOCAL,
    REMOTE
}

class SelectConfiguration: BaseComponentConfiguration(){
    var mode: SelectDataType = SelectDataType.LOCAL
    var editable = false
    var showClearIcon = true
    var multiple = false
    var showAllPossibleValues = false
}