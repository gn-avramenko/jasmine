/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.components

import com.gridnine.jasmine.web.server.components.ServerUiNode
import com.gridnine.jasmine.web.server.components.ServerUiTable
import com.gridnine.jasmine.web.server.components.ServerUiTableCell
import com.gridnine.jasmine.web.server.components.ServerUiTableConfiguration
import org.zkoss.zk.ui.HtmlBasedComponent
import org.zkoss.zul.*

open class ZkServerUiTable(private val config: ServerUiTableConfiguration) : ServerUiTable, ZkServerUiComponent() {

    private val rows = arrayListOf<List<ServerUiTableCell>>()

    private var component: Grid? = null
    override fun addRow(position: Int?, components: List<ServerUiTableCell>) {
        val pos = position ?: rows.size
        rows.add(pos, components)
        if (component != null) {
            addRowInternal(pos, components)
        }
    }

    private fun addRowInternal(pos: Int, components: List<ServerUiTableCell>) {
        val comp = component!!
        val row = Row()
        components.forEach { component ->
            val cell = Cell()
            cell.style = "padding:0px"
            cell.parent = row
            cell.colspan = component.colspan
            val cellComp = component.component
            if (cellComp != null) {
                val zkComp = findZkComponent(cellComp)
                zkComp.parent = this
                zkComp.getZkComponent().parent = cell
//                cell.setWidgetListener("onAfterSize", "console.log('cell resized')")
//                zkComp.getZkComponent().setWidgetListener("onAfterSize", "console.log('comp resized')")
            }
        }
        val children = comp.rows.getChildren<Row>()
        if (children.size > pos) {
            comp.rows.insertBefore(row, children[pos])
        } else {
            row.parent = comp.rows
        }
    }

    override fun removeRow(position: Int) {
        rows.removeAt(position)
        if (component != null) {
            removeRowInternal(position)
        }
    }

    private fun removeRowInternal(position: Int) {
        val comp = component!!
        val children = comp.rows.getChildren<Row>()
        if (position < children.size) {
            comp.rows.removeChild(children[position])
        }
    }

    override fun moveRow(fromPosition: Int, toPosition: Int) {
        val row = rows.removeAt(fromPosition)
        rows.add(toPosition, row)
        if (component != null) {
            moveRowInternal(fromPosition, toPosition)
        }
    }

    private fun moveRowInternal(fromPosition: Int, toPosition: Int) {
        val comp = component!!
        val tp = if(fromPosition > toPosition) toPosition else fromPosition
        val fp =if(fromPosition > toPosition) fromPosition else toPosition
        val children = comp.rows.getChildren<Row>()
        val child = children[fp]
        val toChild = children[tp]
        comp.rows.removeChild(child)
        comp.rows.insertBefore(child, toChild)

    }

    override fun getRows(): List<List<ServerUiNode?>> {
        return rows.map { row -> row.map { it.component } }
    }

    override fun getZkComponent(): HtmlBasedComponent {
        if(component != null){
            return component!!
        }
        val comp = Grid()
        component = comp
        comp.isSpan = true
        if (config.width == "100%") {
            comp.hflex = "1"
        } else if (config.width != null) {
            comp.width = config.width
        }
        if (config.height == "100%") {
            comp.vflex = "1"
        } else if (config.height != null) {
            comp.height = config.height
        }
        if (!config.noHeader) {
            val columns = Columns()
            columns.parent = comp
            config.columns.forEach {
                val column = Column()
                column.label = it.label ?: ""
                if (it.prefWidth != null) {
                    column.width = "${it.prefWidth}px"
                } else {
                    column.width = "100%"
                }
                column.parent = columns
            }
        }
        val rows = Rows()
        rows.parent = comp
        this.rows.withIndex().forEach { (idx, value) ->
            addRowInternal(idx, value)
        }
        comp.setClientDataAttribute("columns", "[" + config.columns.joinToString(",") {
            """{"minWidth":${it.minWidth}, "prefWidth":${it.prefWidth}, "maxWidth":${it.maxWidth} }"""
        } + "]")
        comp.setWidgetOverride("onSize", "function() {this.\$onSize();jasmineUpdateTableColumnsWidths(\$(this._node));}")

        return comp
    }

    override fun getParent(): ServerUiNode? {
        return parent
    }

}