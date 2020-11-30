/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.easyui.adapter.elements

import com.gridnine.jasmine.web.core.ui.WebComponent
import com.gridnine.jasmine.web.core.ui.components.WebTableBox
import com.gridnine.jasmine.web.core.ui.components.WebTableBoxConfiguration
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import com.gridnine.jasmine.web.easyui.adapter.jQuery

class EasyUiWebTableBox(private val parent:WebComponent?, configure:WebTableBoxConfiguration.()->Unit) : WebTableBox{
    private val uid = MiscUtilsJS.createUUID()
    private val config:WebTableBoxConfiguration = WebTableBoxConfiguration()
    private val rows = arrayListOf<List<WebComponent?>>()
    private var initialized = false
    private  var tableJQ:dynamic = null
    private  var tableBodyJQ:dynamic = null
    init {
        config.configure()
    }

    override fun addRow(position: Int?, components: List<WebComponent?>) {
        val idx = position?:rows.size
        rows.add(idx, components)
        if(initialized){
            if(idx == 0){
                tableBodyJQ.prepend(getRowContent(components))
            } else {
                tableBodyJQ.children("tr").eq(idx-1).after(getRowContent(components))
            }
            components.forEach { it?.decorate() }
        }
    }

    private fun decorate(comps: List<WebComponent?>) {
        comps.forEach { it?.decorate() }
    }

    private fun getRowContent(components: List<WebComponent?>): String {
        val content =  """
            <tr>
            ${components.joinToString ("\n" ){
            """<td>${it?.getHtml()?:""}</td>"""
        }}
        </tr>
        """.trimIndent()
        return content
    }

    override fun removeRow(position: Int) {
        val row = rows.removeAt(position)
        row.forEach { it?.destroy() }
        if(initialized){
            tableBodyJQ.children("tr").eq(position).remove()
        }
    }

    override fun moveRow(fromPosition: Int, toPosition: Int) {
        val row = rows.removeAt(fromPosition)
        rows.add(toPosition, row)
        if(initialized){
            if(toPosition>fromPosition){
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
        config.headerComponents.forEach {wc ->
            wc?.let { result.add(it)}
        }
        return result
    }

    override fun getHtml(): String {
        return "<table cellspacing=0 cellpadding=0 border =0 id=\"table${uid}\" class = \"jasmine-table-box\" style=\"${if (config.width != null) "width:${config.width}" else ""};${if (config.height != null) "height:$${config.height}" else ""}\"/>"
    }

    override fun decorate() {
       tableJQ = jQuery("#table${uid}")
        val  thead = if(!config.headerComponents.isEmpty()){
            """
                <thead>
                    ${config.headerComponents.joinToString("\n") {wc -> 
                """<th>${wc?.getHtml()?:""}</th>"""
            }}
                </thead>
            """.trimIndent()

        } else {
            ""
        }
        val colGroup = """
            <colgroup>
            ${config.columnWidths.joinToString("\n") {cw -> 
            """<col width="${cw.pref?:100}px">"""
        }}
            </colgroup>
        """.trimIndent()
        tableJQ.html("""
            <table>
                $colGroup
                $thead
                <tbody id ="tableBody${uid}"/>
            </table>
        """.trimIndent())
        tableBodyJQ = jQuery("#tableBody${uid}")
        config.headerComponents.forEach { it?.decorate() }
        rows.forEach {row->
            tableBodyJQ.append(getRowContent(row))
            decorate(row)
        }
        initialized = true
    }

    override fun destroy() {
        getChildren().forEach { it.destroy() }
    }


}