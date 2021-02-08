/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.richlet.pg

interface DataGridComponent<E:Any> {
    fun updateData()
    fun getSelected():List<E>
}

data class DataGridRequest(var page: Int = 0, var rows: Int = 0, var desc:Boolean? = false, var sortColumn: String? = null)

class DataGridResponse<E:Any>(val count:Int, val data:List<E>)

data class DataGridComponentConfiguration<E:Any>(val columns:List<Triple<String, String, Boolean>>, val loader: (DataGridRequest) -> DataGridResponse<E>,
                                                 var renderer: (row:E, fieldId:String) -> String?
                                                 , var dblClickListener:((E) ->Unit)?,
                                                 val selectable:Boolean = false,
                                                 val initSortingColumn:String? = null,
                                                 val initSortingOrderAsc:Boolean = true,
                                                 val hFlex:String? = null,
                                                 val vFlex:String? = null
            )