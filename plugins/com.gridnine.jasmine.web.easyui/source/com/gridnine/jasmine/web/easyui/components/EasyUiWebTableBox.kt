/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.easyui.components

import com.gridnine.jasmine.web.core.ui.components.*
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS

@Suppress("unused")
class EasyUiWebTableBox(configure: WebTableBoxConfiguration.() -> Unit) : WebTableBox,EasyUiComponent {
    private val uid = MiscUtilsJS.createUUID()
    private val config: WebTableBoxConfiguration = WebTableBoxConfiguration()
    private val rows = arrayListOf<List<WebTableBoxCell>>()
    private var initialized = false
    private var tableJQ: dynamic = null
    private var tableBodyJQ: dynamic = null

    init {
        config.configure()
    }

    override fun addRow(position: Int?, components: List<WebTableBoxCell>) {
        val idx = position ?: rows.size
        rows.add(idx, components)
        if (initialized) {
            if (idx == 0) {
                tableBodyJQ.prepend(getRowContent(components))
            } else {
                tableBodyJQ.children("tr").eq(idx - 1).after(getRowContent(components))
            }
            components.forEach { it.component?.let { comp -> findEasyUiComponent(comp).decorate() } }
        }
    }



    private fun getRowContent(components: List<WebTableBoxCell>): String {
        return """
            <tr>
            ${
            components.joinToString("\n") {
                """<td colspan="${it.colspan}">${it.component?.let { comp -> findEasyUiComponent(comp).getHtml() }?: ""}</td>"""
            }
        }
        </tr>
        """.trimIndent()
    }

    override fun removeRow(position: Int) {
        val row = rows.removeAt(position)
        row.forEach { it.component?.let { comp -> findEasyUiComponent(comp).destroy() } }
        if (initialized) {
            tableBodyJQ.children("tr").eq(position).remove()
        }
    }

    override fun moveRow(fromPosition: Int, toPosition: Int) {
        val row = rows.removeAt(fromPosition)
        rows.add(toPosition, row)
        if (initialized) {
            if (toPosition > fromPosition) {
                tableBodyJQ.children("tr").eq(fromPosition).insertAfter(tableBodyJQ.children("tr").eq(toPosition))
            } else {
                tableBodyJQ.children("tr").eq(fromPosition).insertBefore(tableBodyJQ.children("tr").eq(toPosition))
            }
        }
    }

    override fun getRows(): List<List<WebNode?>> {
        return rows.map { row -> row.map { it.component } }
    }

    override fun getId(): String {
        return "table${uid}"
    }


    override fun getHtml(): String {
        return "<table cellspacing=0 cellpadding=0 border =0 id=\"table${uid}\" class = \"jasmine-table-box\" style=\"${if (config.width != null) "width:${config.width}" else ""};${if (config.height != null) "height:$${config.height}" else ""}\"/>"
    }

    override fun decorate() {
        tableJQ = jQuery("#table${uid}")
        val thead = if (!config.headerComponents.isEmpty()) {
            """
                <thead>
                    ${
                config.headerComponents.joinToString("\n") { wc ->
                    """<th>${wc?.let { findEasyUiComponent(it).getHtml() }?: ""}</th>"""
                }
            }
                </thead>
            """.trimIndent()

        } else {
            ""
        }
        val colGroup = """
            <colgroup>
            ${
            config.columnWidths.joinToString("\n") {
                """<col width="50px">"""
            }
        }
            </colgroup>
        """.trimIndent()
        tableJQ.html("""
                $colGroup
                $thead
                <tbody id ="tableBody${uid}"/>
        """.trimIndent())
        val calculatedWidths = config.columnWidths.map { ColumnWidthData(null, it) }
        val totalWidth = tableJQ.width() as Int
        calculateWidth(calculatedWidths, totalWidth)
        val width = calculatedWidths.map { it.calculatedWidth!! }.reduce { a,b -> a+b}
        if(width < totalWidth-5){
            tableJQ.width(width)
        }
        val children = tableJQ.find("colgroup col")
        calculatedWidths.withIndex().forEach { (idx, width)  ->
            children.eq(idx).attr("width", "${width.calculatedWidth}px")
        }
        tableBodyJQ = jQuery("#tableBody${uid}")
        config.headerComponents.forEach { it?.let { comp -> findEasyUiComponent(comp).decorate() } }
        rows.forEach { row ->
            tableBodyJQ.append(getRowContent(row))
            row.forEach { r -> r.component?.let { findEasyUiComponent(it).decorate() } }
        }
        initialized = true
    }

    private fun calculateWidth(calculatedWidths: List<ColumnWidthData>, totalWidth: Int) {
        if (calculatedWidths.isEmpty()) {
            return
        }
        val correctedTotalWidth = if (totalWidth > 0) totalWidth else 100
        val calculatedTotalWidth = calculatedWidths.map { it.configWidth.pref ?: 100 }.reduce { a, b -> a + b }
        val coeff = correctedTotalWidth.toDouble() / calculatedTotalWidth.toDouble()
        val corrected = arrayListOf<ColumnWidthData>()
        var newTotalWidth = totalWidth
        calculatedWidths.forEach {
            it.calculatedWidth = ((it.configWidth.pref ?: 100) * coeff).toInt()
            if (it.configWidth.min != null && it.calculatedWidth!! < it.configWidth.min!!) {
                it.calculatedWidth = it.configWidth.min!!
                corrected.add(it)
                newTotalWidth -= it.calculatedWidth!!
            } else if (it.configWidth.max != null && it.calculatedWidth!! > it.configWidth.max!!) {
                it.calculatedWidth = it.configWidth.max!!
                corrected.add(it)
                newTotalWidth -= it.calculatedWidth!!
            }
        }
        if (corrected.isNotEmpty()) {
            val rest = ArrayList(calculatedWidths)
            rest.removeAll(corrected)
            calculateWidth(rest, newTotalWidth)
        }
    }

    override fun destroy() {
        val result = rows.flatMap { row -> row.mapNotNull { it.component } }.toMutableList()
        config.headerComponents.forEach { wc ->
            wc?.let { result.add(it) }
        }
        result.forEach { findEasyUiComponent(it).destroy() }
    }

}

class ColumnWidthData(var calculatedWidth: Int?, val configWidth: WebTableBoxColumnWidth)