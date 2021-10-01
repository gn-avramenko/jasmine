/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.standard.widgets

import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.components.BaseWebNodeWrapper
import com.gridnine.jasmine.web.core.ui.components.WebNode
import com.gridnine.jasmine.web.core.ui.components.WebTag
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import kotlinx.browser.window


@Suppress("unused")
class GeneralTableBoxWidget(configure:GeneralTableBoxWidgetConfiguration.()->Unit) : BaseWebNodeWrapper<WebTag>(){
    private val uid = MiscUtilsJS.createUUID()

    private var initialized = false

    private lateinit var tbody:WebTag

    private val rows = arrayListOf<List<WebGeneralTableBoxWidgetCell>>()

    init {
        val config = GeneralTableBoxWidgetConfiguration()
        config.configure()
        _node = WebUiLibraryAdapter.get().createTag("table","table${uid}" ).also {
            it.getAttributes().setAttributes("cellspacing" to "0", "cellpadding" to "0")
            it.getClass().addClasses("jasmine-table-box")
            config.width?.let { width -> it.getStyle().setParameters("width" to width) }
            config.height?.let { height -> it.getStyle().setParameters("height" to height) }
        }
        _node.setPostRenderAction {
            window.setTimeout({render(config)}, 50)

        }
    }

    fun addRow(position: Int?, components: List<WebGeneralTableBoxWidgetCell>) {
        val idx = position ?: rows.size
        rows.add(idx, components)
        if (initialized) {
            tbody.getChildren().addChild(idx, createRow(components))
        }
    }

    fun removeRow(position: Int) {
        rows.removeAt(position)
        if (initialized) {
            val child = tbody.getChildren()[position]
            tbody.getChildren().removeChild(child)
        }
    }

    fun moveRow(fromPosition: Int, toPosition: Int) {
        val row = rows.removeAt(fromPosition)
        rows.add(toPosition, row)
        if (initialized) {
            tbody.getChildren().moveChild(fromPosition, toPosition)
        }
    }

    fun getRows(): List<List<WebNode?>> {
        return rows.map { row -> row.map { it.component } }
    }

    private fun render(config: GeneralTableBoxWidgetConfiguration) {
        val tableElement = window.document.getElementById("table${uid}")
        val totalWidth = tableElement.asDynamic().offsetWidth as Int -2
        val calculatedWidths = config.columnWidths.map { ColumnWidthData(null, it) }
        calculateWidth(calculatedWidths, totalWidth)
        val width = calculatedWidths.map { it.calculatedWidth!! }.reduce { a,b -> a+b}
        if(width < totalWidth-5){
            tableElement.asDynamic().width = width
            console.log("expand table width from $totalWidth to $width")
        }
        val colgroup = WebUiLibraryAdapter.get().createTag("colgroup")
        calculatedWidths.forEach { cw ->
            val col = WebUiLibraryAdapter.get().createTag("col")
            col.getAttributes().setAttributes("width" to "${cw.calculatedWidth}px")
            colgroup.getChildren().addChild(col)
        }
        _node.getChildren().addChild(colgroup)
        if(config.headerComponents.isNotEmpty()){
            val thead =  WebUiLibraryAdapter.get().createTag("thead")
            config.headerComponents.forEach { hc ->
                val th =     WebUiLibraryAdapter.get().createTag("th")
                if(hc != null){
                    th.getChildren().addChild(hc)
                }
                thead.getChildren().addChild(th)
            }
            _node.getChildren().addChild(thead)
        }
        tbody = WebUiLibraryAdapter.get().createTag("tbody","tableBody${uid}" )
        rows.forEach { components ->
            tbody.getChildren().addChild(createRow(components))
        }
        _node.getChildren().addChild(tbody)
        initialized = true
    }

    private fun createRow(components: List<WebGeneralTableBoxWidgetCell>): WebNode {
        val tr = WebUiLibraryAdapter.get().createTag("tr")
        components.forEach { cell ->
            val td = WebUiLibraryAdapter.get().createTag("td").also { it.getAttributes().setAttributes("colspan" to "${cell.colspan}") }
            cell.component?.let {  td.getChildren().addChild(it)}
            tr.getChildren().addChild(td)
        }
        return tr
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
            if (it.configWidth.min != null && it.calculatedWidth!! < it.configWidth.min) {
                it.calculatedWidth = it.configWidth.min
                corrected.add(it)
                newTotalWidth -= it.calculatedWidth!!
            } else if (it.configWidth.max != null && it.calculatedWidth!! > it.configWidth.max) {
                it.calculatedWidth = it.configWidth.max
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
}
internal class ColumnWidthData(var calculatedWidth: Int?, val configWidth: WebGeneralTableBoxWidgetColumnWidth)

class GeneralTableBoxWidgetConfiguration:BaseWidgetConfiguration(){
    val headerComponents = arrayListOf<WebNode?>()
    val columnWidths = arrayListOf<WebGeneralTableBoxWidgetColumnWidth>()
}

class WebGeneralTableBoxWidgetColumnWidth(val min:Int?, val pref:Int?, val max:Int?)

class WebGeneralTableBoxWidgetCell(val component:WebNode? , val colspan:Int =1)

