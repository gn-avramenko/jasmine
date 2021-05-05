/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.easyui.components

import com.gridnine.jasmine.web.core.ui.components.GridRow
import com.gridnine.jasmine.web.core.ui.components.WebGridContainerConfiguration
import com.gridnine.jasmine.web.core.ui.components.WebGridLayoutContainer


class EasyUiWebGridContainer(configure:WebGridContainerConfiguration.()->Unit) :WebGridLayoutContainer,EasyUiComponent{

    private var initialized = false

    private val uid:String

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
    }

    private fun generateRow(rowData: IndexedValue<GridRow>) :String {
        var columnIndex = 0
        val result = rowData.value.cells.joinToString ("\n"){cell->
            val hRes = """
              <div style = "grid-row: "${rowData.index + 1}";grid-column: "${columnIndex + 1}"/"${columnIndex + 1 + cell.columnSpan}";" class = "${getClassAttribute(rowData.index, columnIndex)}">
                ${cell.comp?.let { findEasyUiComponent(it).getHtml() }?:""}
            </div>
            """.trimIndent()
            columnIndex += cell.columnSpan
            hRes
        }
        return result
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
        return if(config.width != null){
            return """width:${config.width};"""
        } else {
            ""
        }
    }
    private fun getHeightAttribute(): String {
        return if(config.height != null){
            return """height:${config.height};"""
        } else {
            ""
        }
    }

    private fun getNoPaddingAttribute(): String {
        return if(config.noPadding){
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
        if(initialized) {
            config.rows.flatMap { it.cells }.forEach { it.comp?.apply { findEasyUiComponent(this).destroy() } }
        }
    }

}


