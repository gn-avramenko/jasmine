/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.components

import com.gridnine.jasmine.web.core.ui.WebComponent

interface WebDataList<E:Any>: WebComponent{
    fun setValueGetter(value: (E) -> String?)

    fun setData(data:List<E>)

    fun setFormatter(value:(E, Int) ->String?)

    fun setSelectionAllowed(value:Boolean)

    fun setClickListener(listener:((E) ->Unit)?)

}

class WebDataListConfiguration{
    var fit:Boolean = true
    var showLines = true
    var width:String? = null
    var height:String? = null
}