/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("unused")

package com.gridnine.jasmine.web.standard.widgets

import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.components.*
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS


class WebGridLayoutWidget(configure:WebGridLayoutWidgetConfiguration.()->Unit) :BaseWebNodeWrapper<WebTag>() {

    private val uid:String

    private val widths = arrayListOf<WebGridLayoutWidgetColumn>()

    private val rows = arrayListOf<WebGridLayoutWidgetRow>()

    private  val noPadding:Boolean
    init{
        val config = WebGridLayoutWidgetConfiguration()
        config.configure()
        uid = config.uid?:MiscUtilsJS.createUUID()
        _node = WebUiLibraryAdapter.get().createTag("div","gridLayout$uid")
        if(config.width != null) {
            _node.getStyle().setParameters("width" to config.width!!)
        }
        if(config.height != null) {
            _node.getStyle().setParameters("height" to config.height!!)
        }
        if(config.noPadding){
            _node.getStyle().setParameters("grid-column-gap" to "0px", "grid-row-gap" to "0px")
        }
        if(config.className != null){
            _node.getClass().addClasses(config.className!!)
        }
        _node.getStyle().setParameters("display" to "grid")
        noPadding = config.noPadding
    }


    fun setColumnsWidths(vararg widths:String?){
        this.widths.clear()
        this.widths.addAll(widths.map { WebGridLayoutWidgetColumn(it) })
        updateContainerStyleAttributes()
    }

    fun setColumnsWidths(widths:List<String?>){
        this.widths.clear()
        this.widths.addAll(widths.map { WebGridLayoutWidgetColumn(it) })
        updateContainerStyleAttributes()
    }
    fun addRow(cells: List<WebNode?>){
        addRow(null, cells.map { WebGridLayoutWidgetCell(it) })
    }
    fun addRow(vararg cells: WebNode?){
        addRow(null, cells.map { WebGridLayoutWidgetCell(it) })
    }

    fun addRow(vararg cells: WebGridLayoutWidgetCell){
        addRow(null, cells.toList())
    }
    fun addRow(height:String?, vararg cells: WebGridLayoutWidgetCell){
        addRow(height, cells.toList())
    }
    fun addRow(height: String?, cells:List<WebGridLayoutWidgetCell>){
        val row = WebGridLayoutWidgetRow(height)
        val rowIndex = this.rows.size
        row.cells.addAll(cells)
        this.rows.add(row)
        var columnIndex = 0
        row.cells.withIndex().forEach {(idx, cell) ->
            val newCell = WebUiLibraryAdapter.get().createTag("div")
            newCell.getStyle().setParameters("grid-row" to "${rowIndex + 1}", "grid-column" to "${columnIndex + 1}/${columnIndex + 1 + cell.columnSpan}")
            cell.comp?.let {  newCell.getChildren().addChild(it)}
            newCell.getClass().addClasses(getClassAttribute(cell.sClass, rowIndex, idx))
            _node.getChildren().addChild(newCell)
            columnIndex += cell.columnSpan
        }
        updateContainerStyleAttributes()
    }

    private fun getClassAttribute(sClass:String?, rowIndex: Int, columnIndex: Int): String {
        return when{
            sClass != null -> sClass
            noPadding -> {
                "jasmine-grid-container-no-padding"
            }
            else ->"${if (rowIndex == 0) "jasmine-grid-container-first-row " else "jasmine-grid-container-other-row "}${if (columnIndex == 0) "jasmine-grid-container-left-column" else "jasmine-grid-container-other-column"}"
        }
    }

    private fun updateContainerStyleAttributes() {
        _node.getStyle().setParameters("grid-template-columns" to widths.joinToString(" ") { getWidthOrHeight(it.width) })
        _node.getStyle().setParameters("grid-template-rows" to  rows.joinToString(" "){ getWidthOrHeight(it.height) })
    }

    private fun getWidthOrHeight(width:String?) :String{
        return width?.let{
            return when(it){
                "100%" -> "1fr"
                else -> it
            }
        }?:"auto"
    }
}

class WebGridLayoutWidgetConfiguration: BaseWebComponentConfiguration(){
    var uid:String? = null
    var noPadding = false
}

internal class WebGridLayoutWidgetColumn(val width:String?)

class WebGridLayoutWidgetCell(val comp:WebNode?, val columnSpan:Int =1, val sClass:String? = null)

internal class WebGridLayoutWidgetRow(val height:String?){
    val cells = arrayListOf<WebGridLayoutWidgetCell>()
}
