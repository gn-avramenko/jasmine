/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.easyui.adapter.elements

import com.gridnine.jasmine.web.core.ui.WebComponent
import com.gridnine.jasmine.web.core.ui.components.WebTableBox
import com.gridnine.jasmine.web.core.ui.components.WebTableBoxColumnWidth
import com.gridnine.jasmine.web.core.ui.components.WebTableBoxConfiguration
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import com.gridnine.jasmine.web.easyui.adapter.jQuery

class EasyUiWebTableBox(private val parent: WebComponent?, configure: WebTableBoxConfiguration.() -> Unit) : WebTableBox {
    private val uid = MiscUtilsJS.createUUID()
    private val config: WebTableBoxConfiguration = WebTableBoxConfiguration()
    private val rows = arrayListOf<List<WebComponent?>>()
    private var initialized = false
    private var tableJQ: dynamic = null
    private var tableBodyJQ: dynamic = null

    init {
        config.configure()
    }

    override fun addRow(position: Int?, components: List<WebComponent?>) {
        val idx = position ?: rows.size
        rows.add(idx, components)
        if (initialized) {
            if (idx == 0) {
                tableBodyJQ.prepend(getRowContent(components))
            } else {
                tableBodyJQ.children("tr").eq(idx - 1).after(getRowContent(components))
            }
            components.forEach { it?.decorate() }
        }
    }

    private fun decorate(comps: List<WebComponent?>) {
        comps.forEach { it?.decorate() }
    }

    private fun getRowContent(components: List<WebComponent?>): String {
        val content = """
            <tr>
            ${
            components.joinToString("\n") {
                """<td>${it?.getHtml() ?: ""}</td>"""
            }
        }
        </tr>
        """.trimIndent()
        return content
    }

    override fun removeRow(position: Int) {
        val row = rows.removeAt(position)
        row.forEach { it?.destroy() }
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

    override fun getRows(): List<List<WebComponent?>> {
        return rows
    }

    override fun getParent(): WebComponent? {
        return parent
    }

    override fun getChildren(): List<WebComponent> {
        val result = arrayListOf<WebComponent>()
        rows.forEach {
            result.addAll(it.filterNotNull())
        }
        config.headerComponents.forEach { wc ->
            wc?.let { result.add(it) }
        }
        return result
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
                    """<th>${wc?.getHtml() ?: ""}</th>"""
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
            config.columnWidths.joinToString("\n") { cw ->
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
        tableBodyJQ = jQuery("#tableBody${uid}")
        config.headerComponents.forEach { it?.decorate() }
        rows.forEach { row ->
            tableBodyJQ.append(getRowContent(row))
            decorate(row)
        }
        val calculatedWidths = config.columnWidths.map { ColumnWidthData(null, it) }
        val totalWidth = tableJQ.width() as Int
        calculateWidth(calculatedWidths, totalWidth)
        val children = tableJQ.find("colgroup col")
        calculatedWidths.withIndex().forEach { (idx, width)  ->
            children.eq(idx).attr("width", "${width.calculatedWidth}px")
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
                newTotalWidth = newTotalWidth - it.calculatedWidth!!
            } else if (it.configWidth.max != null && it.calculatedWidth!! > it.configWidth.max!!) {
                it.calculatedWidth = it.configWidth.max!!
                corrected.add(it)
                newTotalWidth = newTotalWidth - it.calculatedWidth!!
            }
        }
        if (corrected.isNotEmpty()) {
            val rest = ArrayList(calculatedWidths)
            rest.removeAll(corrected)
            calculateWidth(rest, newTotalWidth)
        }
    }

    override fun destroy() {
        getChildren().forEach { it.destroy() }
    }

}

class ColumnWidthData(var calculatedWidth: Int?, val configWidth: WebTableBoxColumnWidth)