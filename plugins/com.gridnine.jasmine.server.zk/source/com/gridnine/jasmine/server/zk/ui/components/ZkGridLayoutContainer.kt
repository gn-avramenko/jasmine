/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.zk.ui.components

import com.gridnine.jasmine.server.core.ui.components.*
import org.zkoss.zk.ui.HtmlBasedComponent
import org.zkoss.zul.Div

open class ZkGridLayoutContainer(configure: GridLayoutContainerConfiguration.() -> Unit) : GridLayoutContainer, ZkUiComponent{

    private val rows = arrayListOf<GridLayoutRow>()

    private var component:Div? = null

    private var columnIndex = 0

    private val configuration = GridLayoutContainerConfiguration()

    init {
        configuration.configure()
    }
    override fun addRow(height: String?) {
        if(rows.size > 0){
            val row = rows.last()
            val span = row.cells.map{it.columnSpan}.reduce{ i1, i2 ->  i1+i2}
            if(span < configuration.columns.size){
                row.cells.add(GridLayoutCell(null, configuration.columns.size-span))
            }
        }
        rows.add(GridLayoutRow(GridLayoutRowConfiguration(height)))
        component?.let{
            it.style = createContainerStyle()
            columnIndex = 0
        }
    }

    private fun createContainerStyle(): String {
        return """display: grid;grid-template-columns:${configuration.columns.joinToString(" ") { getWidthOrHeight(it.width) }};grid-template-rows: ${rows.joinToString(" "){ getWidthOrHeight(it.config.height) }};"""
    }


    private fun getWidthOrHeight(width:String?) :String{
        return width?.let{
            return when(it){
                "100%" -> "1fr"
                else -> it
            }
        }?:"auto"
    }

    override fun addCell(cell: GridLayoutCell) {
        rows.last().cells.add(cell)
        component?.let {
            addCellInternal(cell, rows.size-1)
        }
    }

    override fun getZkComponent(): HtmlBasedComponent {
        if(component != null){
            return component!!
        }
        val comp = Div()
        comp.style = createContainerStyle()
        configureBasicParameters(comp, configuration)
        component = comp
        rows.withIndex().forEach { (idx, row) ->
            row.cells.forEach {
                addCellInternal(it, idx)
            }
        }
        return comp
    }

    private fun addCellInternal(cell: GridLayoutCell, rowIndex:Int) {
        component!!.let {cont ->
            val cellDiv = Div()
            cellDiv.style = """grid-row: ${rowIndex+1};grid-column: ${columnIndex+1}/${columnIndex+1+cell.columnSpan};"""
            when {
                cell.sClass != null -> {
                    cellDiv.setClass(cell.sClass)
                }
                configuration.noPadding -> {
                    cellDiv.setClass("jasmine-grid-container-no-padding")
                }
                else -> {
                    cellDiv.setClass("${if (rowIndex == 0) "jasmine-grid-container-first-row " else "jasmine-grid-container-other-row "}${if (columnIndex == 0) "jasmine-grid-container-left-column" else "jasmine-grid-container-other-column"}")
                }
            }
            columnIndex+=cell.columnSpan
            if(columnIndex >= configuration.columns.size ){
                columnIndex = 0
            }
            cell.comp?.let {
                val divComp = findZkComponent(it).getZkComponent()
                divComp.parent = cellDiv
            }
            cellDiv.parent = cont
        }
    }


}