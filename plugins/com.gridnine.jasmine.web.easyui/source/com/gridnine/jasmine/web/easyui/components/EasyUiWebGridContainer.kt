/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.easyui.components

import com.gridnine.jasmine.web.core.ui.components.GridRow
import com.gridnine.jasmine.web.core.ui.components.WebGridContainerConfiguration
import com.gridnine.jasmine.web.core.ui.components.WebGridLayoutContainer


class EasyUiWebGridContainer(configure: WebGridContainerConfiguration.() -> Unit) : WebGridLayoutContainer, EasyUiComponent {

    private var initialized = false

    private val uid: String

    private val config = WebGridContainerConfiguration()

    init {
        config.configure()
        uid = config.uid
    }


    override fun getHtml(): String {
        return """
            <div id = "gridLayout$uid" ${getStyleAttribute()}>
                ${config.rows.withIndex().joinToString("\n") { generateRow(it)  }}
            </div>
        """.trimIndent()
//    return  """<table id = "gridLayout$uid" style = "${getWidthAttribute()} ${getHeightAttribute()}" ${if(config.noPadding) "cellspacing=0 cellpadding=0 border=0" else ""}>
//            <tr>${config.columns.joinToString("\n") {
//                """<td class = "jasmine-grid-layout-td" style="${if(it.width != null) "width:${it.width}" else ""};" />"""
//            }}
//            </tr>
//        </tr>
//        ${config.rows.joinToString {row ->
//            """
//                <tr style ="${if(row.config.height!=null) "height:${row.config.height}" else ""}">
//                    ${row.cells.joinToString("\n") { cell ->
//                    """
//                        <td class= "jasmine-grid-layout-td", hSpan = ${cell.columnSpan}>
//                            ${cell.comp?.let { findEasyUiComponent(it).getHtml() }?:""}
//                        </td>
//                    }
//                    """.trimIndent()
//            }}
//                </tr>
//            """.trimIndent()
//        }}
//        """.trimIndent()
//        val table = HtmlUtilsJS.table(id = "gridLayout${uid}", style = "${if (config.width != null) "width:${config.width}" else ""};${if (config.height != null) "height:${config.height}" else ""};") {
//            tr {
//                config.columns.forEach {
//                    td(`class` = "jasmine-grid-layout-td", style = "${if (it.width != null) "width:${it.width}" else ""};") {}
//                }
//            }
//            config.rows.forEach { row ->
//                tr(style = if (row.config.height != null) "height:${row.config.height}" else "") {
//                    row.cells.forEach { cell ->
//                        td(`class` = "jasmine-grid-layout-td", hSpan = cell.columnSpan) {
//                            (cell.comp?.let { findEasyUiComponent(it).getHtml() } ?: "")()
//                        }
//                    }
//                }
//            }
//        }
//        if (config.noPadding) {
//            table.attributes["cellspacing"] = "0"
//            table.attributes["cellpadding"] = "0"
//            table.attributes["border"] = "0"
//        }
//        return table.toString()
    }

    private fun generateRow(rowData: IndexedValue<GridRow>) :String {
        var columnIndex = 0
        return rowData.value.cells.joinToString("\n"){ cell->
            val hRes = """
              <div style = "grid-row: ${rowData.index + 1};grid-column: ${columnIndex + 1}/${columnIndex + 1 + cell.columnSpan};" class = "${getClassAttribute(rowData.index, columnIndex)}">
                ${cell.comp?.let { findEasyUiComponent(it).getHtml() }?:""}
            </div>
            """.trimIndent()
            columnIndex += cell.columnSpan
            hRes
        }
    }

    private fun getClassAttribute(rowIndex: Int, columnIndex: Int): String {
        return if(config.noPadding){
            "jasmine-grid-container-no-padding"
        } else {
            "${if (rowIndex == 0) "jasmine-grid-container-first-row " else "jasmine-grid-container-other-row "}${if (columnIndex == 0) "jasmine-grid-container-left-column" else "jasmine-grid-container-other-column"}"
        }
    }

    private fun getStyleAttribute(): String {
        return """style="display:grid; ${getWidthAttribute()} ${getHeightAttribute()} ${getNoPaddingAttribute()} grid-template-columns:${config.columns.joinToString(" ") { getWidthOrHeight(it.width) }};grid-template-rows: ${config.rows.joinToString(" "){ getWidthOrHeight(it.config.height) }}; " """
    }

    private fun getWidthOrHeight(width:String?) :String{
        return width?.let{
            return when(it){
                "100%" -> "1fr"
                else -> it
            }
        }?:"auto"
    }


    private fun getWidthAttribute(): String {
        return if (config.width != null) {
            return """width:${config.width};"""
        } else {
            ""
        }
    }

    private fun getHeightAttribute(): String {
        return if (config.height != null) {
            return """height:${config.height};"""
        } else {
            ""
        }
    }

    private fun getNoPaddingAttribute(): String {
        return if (config.noPadding) {
            return """grid-column-gap: 0px;grid-row-gap: 0px;"""
        } else {
            ""
        }
    }


    override fun decorate() {
        config.rows.flatMap { it.cells }.forEach { it.comp?.apply { findEasyUiComponent(this).decorate() } }
        initialized = true
    }

    override fun destroy() {
        if (initialized) {
            config.rows.flatMap { it.cells }.forEach { it.comp?.apply { findEasyUiComponent(this).destroy() } }
        }
    }

    override fun getId(): String {
        return "gridLayout$uid"
    }

}


