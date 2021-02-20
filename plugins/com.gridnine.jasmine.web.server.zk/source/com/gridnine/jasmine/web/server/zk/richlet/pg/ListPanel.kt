/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.richlet.pg

import com.gridnine.jasmine.web.server.components.ServerUiComponent
import com.gridnine.jasmine.web.server.components.ServerUiComponentHorizontalAlignment.*
import com.gridnine.jasmine.web.server.components.ServerUiDataGridColumnConfiguration
import com.gridnine.jasmine.web.server.components.ServerUiDataGridComponentConfiguration
import com.gridnine.jasmine.web.server.components.ServerUiDataGridResponse
import com.gridnine.jasmine.web.server.zk.components.ZkDataGridComponent
import com.gridnine.jasmine.web.server.zk.components.ZkServerUiComponent
import org.zkoss.zk.ui.HtmlBasedComponent
import org.zkoss.zk.ui.event.Events
import org.zkoss.zul.*

class ListPanel : ZkServerUiComponent(){
    private var component:Vlayout?  = null
    init{
        component = Vlayout()
        component!!.hflex = "100%"
        component!!.vflex = "100%"
        val l1 = Hlayout()
        l1.hflex = "1"
        val button = Button()
        button.label = "Load"
        l1.appendChild(button)
        val hGlue = Div()
        hGlue.hflex = "1"
        l1.appendChild(hGlue)
        val searchBox = Textbox()
        searchBox.width = "200px"
        l1.appendChild(searchBox)
        component!!.appendChild(l1)

        val config = ServerUiDataGridComponentConfiguration()
        config.width = "100%"
        config.height = "100%"
        config.initSortingColumn = ListPanelItem.stringField1Name
        config.initSortingOrderAsc = true
        config.selectable = true
        config.span = true
        run{
            val column = ServerUiDataGridColumnConfiguration()
            column.horizontalAlignment = LEFT
            column.fieldId = ListPanelItem.stringField1Name
            column.title = "Поле 1"
            column.width= "200px"
            column.sortable = true
            config.columns.add(column)
        }
        run{
            val column = ServerUiDataGridColumnConfiguration()
            column.horizontalAlignment = RIGHT
            column.fieldId = ListPanelItem.stringField2Name
            column.title = "Поле 2"
            column.width= "200px"
            column.sortable = true
            config.columns.add(column)
        }
        val grid = ZkDataGridComponent<ListPanelItem>(config)
        grid.setFormatter{ item, fieldId ->
            when(fieldId){
                ListPanelItem.stringField1Name -> item.stringField1
                ListPanelItem.stringField2Name -> item.stringField2
                else -> TODO()
            }
        }
        grid.setDoubleClickListener {
            println("selected ${it.stringField1}")
        }
        grid.setLoader{
            val result = arrayListOf<ListPanelItem>()
            for(n in 1..100){
                val item = ListPanelItem()
                item.stringField1 = "поле 1 - $n"
                item.stringField2 = "поле 2 - $n"
                result.add(item)
            }
            if(it.sortColumn != null){
                if(it.desc == true){
                    result.sortByDescending {item ->
                        when(it.sortColumn){
                            ListPanelItem.stringField1Name -> item.stringField1
                            ListPanelItem.stringField2Name -> item.stringField2
                            else -> null
                        }
                    }
                } else if(it.desc == false){
                    result.sortBy {item ->
                        when(it.sortColumn){
                            ListPanelItem.stringField1Name -> item.stringField1
                            ListPanelItem.stringField2Name -> item.stringField2
                            else -> null
                        }
                    }
                }

            }
            ServerUiDataGridResponse(result.size, result.subList(it.offSet, if (it.offSet+it.limit > result.size) result.size else (it.offSet+it.limit)))
        }
        button.addEventListener(Events.ON_CLICK){
            println("selected: ${ grid.getSelected().map { it.stringField1 }.joinToString (",") }}")
        }
        component!!.appendChild(grid.getComponent())
    }

    override fun getComponent(): HtmlBasedComponent {
        return component!!
    }

    override fun getParent(): ServerUiComponent? {
        return parent
    }
}



class ListPanelItem{
    var stringField1:String? = null
    var stringField2:String? = null
    companion object{
        const val stringField1Name = "stringField1"
        const val stringField2Name = "stringField2"
    }
}