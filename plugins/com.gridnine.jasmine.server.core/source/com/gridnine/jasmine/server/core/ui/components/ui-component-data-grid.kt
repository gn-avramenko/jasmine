/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.ui.components

import com.gridnine.jasmine.server.core.ui.common.BaseComponentConfiguration
import com.gridnine.jasmine.server.core.ui.common.ComponentHorizontalAlignment
import com.gridnine.jasmine.server.core.ui.common.UiNode


interface DataGrid<E:Any>: UiNode {
    fun setLoader(loader: (DataGridRequest) -> DataGridResponse<E>)
    fun setFormatter(formatter: (item:E, fieldId:String)->String?)
    fun setDoubleClickListener(listener: ((item:E)->Unit)?)
    fun updateData()
    fun getSelected():List<E>
}

data class DataGridRequest(var offSet: Int = 0, var limit: Int = 0, var desc:Boolean? = false, var sortColumn: String? = null)

data class DataGridResponse<E:Any>(val count:Int, val data:List<E>)

class DataGridConfiguration : BaseComponentConfiguration(){
    var span = false
    var initSortingColumn:String? = null
    var initSortingOrderAsc = false
    var selectable = false
    val columns = arrayListOf<DataGridColumnConfiguration>()
}

class DataGridColumnConfiguration {
    lateinit var fieldId:String
    lateinit var title:String
    var width:String? = null
    var sortable = true
    var horizontalAlignment: ComponentHorizontalAlignment? = null
}





