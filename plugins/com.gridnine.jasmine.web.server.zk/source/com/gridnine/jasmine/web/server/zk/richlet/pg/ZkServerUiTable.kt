/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.richlet.pg

import org.zkoss.zk.ui.HtmlBasedComponent
import org.zkoss.zk.ui.event.Events
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
        components.forEach { comp ->
            val cell = Cell()
            cell.style = "padding:0px"
            cell.parent = row
            cell.colspan = comp.colspan
            val cellComp = comp.component
            if (cellComp is ZkServerUiComponent) {
                cellComp.parent = ZkServerUiTable@ this
                cellComp.createComponent().parent = cell
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
        val children = comp.rows.getChildren<Row>()
        val child = children[fromPosition]
        val toChild = if (children.size - 1 > toPosition) children[toPosition + 1] else null
        comp.rows.removeChild(child)
        if (toChild != null) {
            comp.rows.insertBefore(child, toChild)
        } else {
            child.parent = comp.rows
        }
    }

    override fun getRows(): List<List<ServerUiComponent?>> {
        return rows.map { row -> row.map { it.component } }
    }

    override fun createComponent(): HtmlBasedComponent {
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
        comp.setWidgetListener("onAfterSize", "jasmineUpdateTableColumnsWidths($(this._node))")

//        addRowInt(rows)
//        comp.setWidgetListener("onAfterSize", """
//            var tableNode = $(this._node)
//            console.log("table width = " + tableNode.width())
//            var lastColumn = tableNode.find("colgroup").eq(0).children().eq(2)
//            console.log(lastColumn)
//            console.log("last column width " + lastColumn.css("width"))
//            lastColumn.css("width", "20px")
//            console.log("new last column width " + lastColumn.css("width"))
//            """.trimMargin())

        return comp
    }

    override fun getParent(): ServerUiComponent? {
        return parent
    }

}