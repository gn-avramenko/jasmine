/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.easyui.adapter.elements

import com.gridnine.jasmine.server.core.model.common.XeptionJS
import com.gridnine.jasmine.web.core.ui.WebComponent
import com.gridnine.jasmine.web.core.ui.components.*
import com.gridnine.jasmine.web.core.utils.HtmlUtilsJS
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS

class EasyUiWebGridLayoutContainer(private val parent:WebComponent?, configure:WebGridLayoutContainerConfiguration.()->Unit) :WebGridLayoutContainer{

    private val rows = arrayListOf<GridLayoutRow>()

    private val columns = arrayListOf<WebGridLayoutColumnConfiguration>()

    private var initialized = false

    private var currentRow: GridLayoutRow? = null

    private var width:String? = null

    private var height:String? = null

    private val uid = MiscUtilsJS.createUUID()

    init {
        (parent?.getChildren() as MutableList<WebComponent>?)?.add(this)
        val config = WebGridLayoutContainerConfiguration()
        config.configure()
        width = config.width
        height = config.height

    }

    override fun defineColumn(width: String?) {
        checkInitialized()
        columns.add(WebGridLayoutColumnConfiguration(width))
    }


    private fun checkInitialized() {
        if(initialized) {
            throw XeptionJS.forDeveloper("unable to modify GridLayout after initialization")
        }
    }

    override fun addRow(height: String?) {
        checkInitialized()
        val row = GridLayoutRow(WebGridLayoutRowConfiguration(height))
        rows.add(row)
        currentRow = row
    }

    override fun addCell(cell: WebGridLayoutCell) {
        checkInitialized()
        val row = currentRow ?: throw XeptionJS.forDeveloper("no row added")
        row.cells.add(cell)
    }

    override fun getParent(): WebComponent? {
        return parent;
    }

    override fun getChildren(): List<WebComponent> {
        val result = arrayListOf<WebComponent>()
        rows.forEach { row -> row.cells.forEach { cell -> if(cell.comp != null)  result.add(cell.comp!!) }}
        return result
    }

    override fun getHtml(): String {
        return HtmlUtilsJS.table(id ="gridLayout${uid}",style="${if(width != null) "width:$width" else ""};${if(height != null) "height:$height" else ""};") {
            tr {
                columns.forEach {
                    td (style="${if(it.width != null) "width:${it.width}" else ""};"){}
                }
            }
            rows.forEach {row ->
                tr(style = if(row.config.height!=null) "height:${row.config.height}" else ""){
                    row.cells.forEach { cell ->
                        td(hSpan = cell.columnSpan){
                            cell.comp?.let { text(it.getHtml())}
                        }
                    }
                }
            }
        }.toString()

    }

    override fun decorate() {
        rows.forEach { row -> row.cells.forEach { cell -> cell.comp?.decorate() } }
    }

}

class WebGridLayoutColumnConfiguration(val width:String?)

class WebGridLayoutRowConfiguration(val height:String?)

class GridLayoutRow(val config:WebGridLayoutRowConfiguration){
    val cells = arrayListOf<WebGridLayoutCell>()
}
