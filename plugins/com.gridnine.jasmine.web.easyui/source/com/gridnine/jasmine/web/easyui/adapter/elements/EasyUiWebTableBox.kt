/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.easyui.adapter.elements

import com.gridnine.jasmine.web.core.ui.WebComponent
import com.gridnine.jasmine.web.core.ui.components.WebTableBox
import com.gridnine.jasmine.web.core.ui.components.WebTableBoxConfiguration
import com.gridnine.jasmine.web.core.ui.components.WebTableBoxRow
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import com.gridnine.jasmine.web.easyui.adapter.jQuery

class EasyUiWebTableBox(private val parent:WebComponent?, configure:WebTableBoxConfiguration.()->Unit) : WebTableBox{
    private val uid = MiscUtilsJS.createUUID()
    private val config:WebTableBoxConfiguration = WebTableBoxConfiguration()
    private val rows = arrayListOf<WebTableBoxRow>()
    private var initialized = false
    private  var tableJQ:dynamic = null
    private  var tableBodyJQ:dynamic = null
    init {
        config.configure()
    }

    override fun addRow(position: Int?, row: WebTableBoxRow) {
        val idx = position?:rows.size
        rows.add(idx, row)
        if(initialized){
            if(idx == 0){
                tableBodyJQ.prepend(getRowContent(row))
                decorate(row)
            } else {
                tableBodyJQ.children("tr").eq(idx-1).append(getRowContent(row))
                decorate(row)
            }
        }
    }

    private fun decorate(row: WebTableBoxRow) {
        row.components.forEach { it.decorate() }
        row.tools?.decorate()
    }

    private fun getRowContent(row: WebTableBoxRow): String {
        val content =  """
            <tr>
            ${row.components.joinToString ("\n" ){
            """<td><div style="width:100%">${it.getHtml()}</div></td>"""
        }}
        ${row.tools?.let { "<td>${it.getHtml()}</td>" }?:""}
                </tr>
            """.trimIndent()
        return content
    }

    override fun removeRow(position: Int) {
        val row = rows.removeAt(position)
        destroy(row)
        if(initialized){
            tableBodyJQ.children("tr").eq(position-1).remove()
        }
    }

    override fun getRows(): List<WebTableBoxRow> {
        return rows
    }

    override fun getParent(): WebComponent? {
        return parent
    }

    override fun getChildren(): List<WebComponent> {
        val result = arrayListOf<WebComponent>()
        rows.forEach {
            result.addAll(it.components)
            it.tools?.let { comp -> result.add(comp) }
        }
        return result
    }

    override fun getHtml(): String {
        return "<table cellspacing=0 cellpadding=0 border =0 id=\"table${uid}\" class = \"jasmine-table-box\" style=\"${if (config.width != null) "width:${config.width}" else ""};${if (config.height != null) "height:$${config.height}" else ""}\"/>"
    }

    override fun decorate() {
       tableJQ = jQuery("#table${uid}")

        if(config.showHeader){
            val headers = arrayListOf<HeaderCellData>()
            config.headerCellsTitles.withIndex().forEach {(idx, value) ->
                headers.add(HeaderCellData(value, config.headerCellsWidths[idx], null))
            }
            if(config.showToolsColumn){
                headers.add(HeaderCellData(null, config.toolsColumnMaxWidth, config.toolsColumnMaxWidth))
            }
            val header = """
                <table>
                            <thead>
            <tr>
            ${headers.joinToString ("\n" ){
                "<th style=\"${if (it.width != null) "width:${it.width}" else ""}; ${if (it.maxWidth != null) "max-width:${it.maxWidth}" else ""}\">${it.title ?: ""}</th>"
            }}
                </tr>
                </thead>
                <tbody id="tableBody${uid}">
                </tbody>
                </table>
            """.trimIndent()
            tableJQ.html(header)
        } else {
            val headers = config.headerCellsWidths.map { HeaderCellData(null, null, null) }.toMutableList()
            if(config.showToolsColumn){
                headers.add(HeaderCellData(null, config.toolsColumnMaxWidth, config.toolsColumnMaxWidth))
            }
            val header = """
                <table>
                           <tbody id="tableBody${uid}">
            <tr>
            ${headers.joinToString ("\n" ){
                "<td style=\"${if (it.width != null) "width:${it.width}" else ""}; ${if (it.maxWidth != null) "max-width:${it.maxWidth}" else ""}\"/>"
            }}
                </tr>
                </tbody>
                </table>
            """.trimIndent()
            tableJQ.html(header)
        }
        tableBodyJQ = jQuery("#tableBody${uid}")
        rows.forEach {row->
            tableBodyJQ.append(getRowContent(row))
            decorate(row)
        }
        initialized = true
    }

    override fun destroy() {
        rows.forEach {destroy(it)}
    }

    private fun destroy(row: WebTableBoxRow) {
        row.components.forEach { it.destroy() }
        row.tools?.destroy()
    }

    class HeaderCellData(val title:String?,val width:String?,val maxWidth:String?)
}