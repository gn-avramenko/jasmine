/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.richlet.pg.components.zk

import com.gridnine.jasmine.web.server.zk.richlet.pg.ServerUiComponent
import com.gridnine.jasmine.web.server.zk.richlet.pg.ZkServerUiComponent
import com.gridnine.jasmine.web.server.zk.richlet.pg.components.*
import org.zkoss.zk.ui.HtmlBasedComponent
import org.zkoss.zul.Div

open class ZkServerUiGridLayoutContainer(private val configuration: ServerUiGridLayoutContainerConfiguration) : ServerUiGridLayoutContainer, ZkServerUiComponent(){

    private val rows = arrayListOf<ServerUiGridLayoutRow>()

    private var component:Div? = null

    private var columnIndex = 0

    override fun addRow(height: String?) {
        if(rows.size > 0){
            val row = rows.last()
            val span = row.cells.map{it.columnSpan}.reduce{ i1, i2 ->  i1+i2}
            if(span < configuration.columns.size){
                row.cells.add(ServerUiGridLayoutCell(null, configuration.columns.size-span))
            }
        }
        rows.add(ServerUiGridLayoutRow(ServerUiGridLayoutRowConfiguration(height)))
        component?.let{
            it.style = createContainerStyle()
            columnIndex = 0;
        }
    }

    private fun createContainerStyle(): String {
        return """display: grid;grid-template-columns:${configuration.columns.joinToString(" ") { getWidthOrHeight(it.width, "200px") }};grid-template-rows: ${rows.joinToString(" "){ getWidthOrHeight(it.config.height, "auto") }};"""
    }


    private fun getWidthOrHeight(width:String?, default:String) :String{
        return width?.let{
            return when(it){
                "100%" -> "1fr"
                else -> it
            }
        }?:default
    }

    override fun addCell(cell: ServerUiGridLayoutCell) {
        rows.last().cells.add(cell)
        component?.let {
            addCellInternal(cell, rows.size-1)
        }
    }

    override fun getComponent(): HtmlBasedComponent {
        if(component != null){
            return component!!
        }
        val comp = Div()
        comp.style = createContainerStyle()
        val width = configuration.width
        if(width == "100%"){
            comp.hflex = "1"
        } else {
            comp.width = width
        }
        val height = configuration.height
        if(height == "100%"){
            comp.vflex = "1"
        } else {
            comp.height = height
        }
        component = comp
        rows.withIndex().forEach { (idx, row) ->
            row.cells.forEach {
                addCellInternal(it, idx)
            }
        }
        return comp
    }

    private fun addCellInternal(cell: ServerUiGridLayoutCell, rowIndex:Int) {
        component!!.let {cont ->
            val cellDiv = Div()
            cellDiv.style = """grid-row: ${rowIndex+1};grid-column: ${columnIndex+1}/${columnIndex+1+cell.columnSpan};padding: ${if(configuration.noPadding) "0px" else "5px"}"""
            columnIndex+=cell.columnSpan
            if(columnIndex >= configuration.columns.size ){
                columnIndex = 0
            }
            cell.comp?.let {
                it as ZkServerUiComponent
                it.parent = this
                val divComp = it.getComponent()
                divComp.parent = cellDiv
            }
            cellDiv.parent = cont
        }
    }

    override fun getParent(): ServerUiComponent? {
        return parent
    }

}