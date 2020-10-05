/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.components

import com.gridnine.jasmine.web.core.ui.WebComponent


interface WebComboBox:WebComponent{
    fun setLoader(value: (String?) ->List<SelectItemJS>)
    fun setPossibleValues(items:List<SelectItemJS>)
    fun getValues():List<String>
    fun setValues(items:List<String>)
}

class SelectItemJS(val id:String, val text:String)

enum class ComboboxMode{
    LOCAL,
    REMOTE
}
class WebComboBoxConfiguration{
    var width:String? = null
    var height:String? = null
    var mode:ComboboxMode = ComboboxMode.LOCAL
    var editable = false
}