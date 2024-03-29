/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.standard.workspaceEditor

import com.gridnine.jasmine.common.core.model.XeptionJS
import com.gridnine.jasmine.common.standard.model.rest.*
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.components.*
import com.gridnine.jasmine.web.standard.widgets.GeneralTableBoxWidget
import com.gridnine.jasmine.web.standard.widgets.WebGeneralTableBoxWidgetCell
import com.gridnine.jasmine.web.standard.widgets.WebGridLayoutWidget

private const val SIMPLE_CRITERION_ID = "SIMPLE_CRITERION_ID"

private const val AND_CRITERION_ID = "AND_CRITERION_ID"

private const val OR_CRITERION_ID = "OR_CRITERION_ID"

private const val NOT_CRITERION_ID = "NOT_CRITERION_ID"

private const val DYNAMIC_CRITERION_ID = "DYNAMIC_CRITERION_ID"

class WebWorkspaceCriterionsListEditor(internal val tableBox: GeneralTableBoxWidget, private val indent:Int) {

    lateinit var listId:String
    internal val handlers = arrayListOf<WebCriterionHandler<*>>()

    fun clearData(){
        tableBox.getRows().indices.forEach { _ ->
            tableBox.removeRow(0)
        }
        handlers.clear()
    }

    fun addEmptyRow(){
        val createMenuButton = WebUiLibraryAdapter.get().createMenuButton{
            icon = "core:plus"
            specificProperties["type"] = "link"
            elements.add(StandardMenuItem().apply {
                id = SIMPLE_CRITERION_ID
                title= "Простое условие"
            })
            elements.add(StandardMenuItem().apply {
                id = DYNAMIC_CRITERION_ID
                title= "Динамическое условие"
            })
            elements.add(StandardMenuItem().apply {
                id = AND_CRITERION_ID
                title= "Логическое И"
            })
            elements.add(StandardMenuItem().apply {
                id = OR_CRITERION_ID
                title= "Логическое ИЛИ"
            })
            elements.add(StandardMenuItem().apply {
                id = NOT_CRITERION_ID
                title= "Логическое НЕ"
            })
        }
        createMenuButton.setHandler(SIMPLE_CRITERION_ID){
            tableBox.removeRow(0)
            addRow(0, WebSimpleCriterionHandler(listId, null))
        }
        createMenuButton.setHandler(AND_CRITERION_ID){
            tableBox.removeRow(0)
            addRow(0, WebAndCriterionHandler(tableBox, indent, listId, null))
        }
        createMenuButton.setHandler(OR_CRITERION_ID){
            tableBox.removeRow(0)
            addRow(0, WebOrCriterionHandler(tableBox, indent, listId, null))
        }
        createMenuButton.setHandler(NOT_CRITERION_ID){
            tableBox.removeRow(0)
            addRow(0, WebNotCriterionHandler(tableBox, indent, listId, null))
        }
        createMenuButton.setHandler(DYNAMIC_CRITERION_ID){
            tableBox.removeRow(0)
            addRow(0, WebDynamicCriterionHandler(listId, null))
        }
        tableBox.addRow(0, arrayListOf(WebGeneralTableBoxWidgetCell(null, 3), WebGeneralTableBoxWidgetCell(createMenuButton, 1)))
    }

    internal fun addRow(i: Int, handler: WebCriterionHandler<*>) {
        handlers.add(i, handler)
        val components = handler.getComponents()
        components.add(WebGeneralTableBoxWidgetCell(CriterionsListToolsPanel(this, indent, listId, handler.getId())))
        tableBox.addRow(i, components)
    }

    fun setData(listId:String?, criterions: List<BaseWorkspaceCriterionDTJS>) {
        if(listId == null){
            clearData()
            return
        }
        this.listId = listId
        clearData()
        if(criterions.isEmpty()){
            addEmptyRow()
            return
        }
        criterions.forEach {
            val handler = when(it){
                is SimpleWorkspaceCriterionDTJS ->WebSimpleCriterionHandler(listId, it)
                is OrWorkspaceCriterionDTJS -> WebOrCriterionHandler(tableBox, indent, listId, it)
                is AndWorkspaceCriterionDTJS -> WebAndCriterionHandler(tableBox, indent, listId, it)
                is NotWorkspaceCriterionDTJS -> WebNotCriterionHandler(tableBox, indent, listId, it)
                is DynamicWorkspaceCriterionDTJS -> WebDynamicCriterionHandler(listId, it)
                else -> throw XeptionJS.forDeveloper("unsupported criterion type ${it::class}" )
            }
            addRow(handlers.size, handler)
        }
    }

    fun updateToolsVisibility() {
        val rows = tableBox.getRows()
        val size = rows.size
        tableBox.getRows().withIndex().forEach {(idx, widget) ->
            val panel = widget.last()
            if(panel is CriterionsListToolsPanel){
                panel.upButton.setEnabled(idx > 0)
                panel.downButton.setEnabled(idx < size-1)
            }
        }
    }

    fun getData(): List<BaseWorkspaceCriterionDTJS> {
        return handlers.mapNotNull {it.getData()}
    }
}

class CriterionsListToolsPanel(private val listEditor: WebWorkspaceCriterionsListEditor, private val indent:Int, private val listId: String, private val rowId:String): BaseWebNodeWrapper<WebGridLayoutWidget>(){
    internal val upButton: WebLinkButton
    internal val downButton: WebLinkButton
    private val plusButton: WebMenuButton
    private val minusButton: WebLinkButton
    init {
        upButton = WebUiLibraryAdapter.get().createLinkButton{
            icon = "core:up"
            specificProperties["type"] = "link"
        }
        upButton.setHandler {
            val idx = listEditor.handlers.indexOfFirst { rowId == it.getId() }
            val item = listEditor.handlers.removeAt(idx)
            listEditor.handlers.add(idx-1, item)
            listEditor.tableBox.moveRow(idx, idx-1)
            listEditor.updateToolsVisibility()
        }
        downButton = WebUiLibraryAdapter.get().createLinkButton{
            icon = "core:down"
            specificProperties["type"] = "link"
        }
        downButton.setHandler {
            val idx = listEditor.handlers.indexOfFirst { rowId == it.getId() }
            val item = listEditor.handlers.removeAt(idx)
            listEditor.handlers.add(idx+1, item)
            listEditor.tableBox.moveRow(idx, idx+1)
            listEditor.updateToolsVisibility()
        }
        plusButton = WebUiLibraryAdapter.get().createMenuButton{
            icon = "core:plus"
            specificProperties["type"] = "link"
            elements.add(StandardMenuItem().apply {
                id = SIMPLE_CRITERION_ID
                title= "Простое условие"
            })
            elements.add(StandardMenuItem().apply {
                id = DYNAMIC_CRITERION_ID
                title= "Динамическое условие"
            })
            elements.add(StandardMenuItem().apply {
                id = AND_CRITERION_ID
                title= "Логическое И"
            })
            elements.add(StandardMenuItem().apply {
                id = OR_CRITERION_ID
                title= "Логическое ИЛИ"
            })
            elements.add(StandardMenuItem().apply {
                id = NOT_CRITERION_ID
                title= "Логическое НЕ"
            })
        }
        plusButton.setHandler(SIMPLE_CRITERION_ID){
            val idx = listEditor.handlers.indexOfFirst { rowId == it.getId() }
            listEditor.addRow(idx+1, WebSimpleCriterionHandler(listId,null))
        }
        plusButton.setHandler(AND_CRITERION_ID){
            val idx = listEditor.handlers.indexOfFirst { rowId == it.getId() }
            listEditor.addRow(idx+1, WebAndCriterionHandler(listEditor.tableBox, indent, listId,null))
        }
        plusButton.setHandler(OR_CRITERION_ID){
            val idx = listEditor.handlers.indexOfFirst { rowId == it.getId() }
            listEditor.addRow(idx+1, WebOrCriterionHandler(listEditor.tableBox, indent, listId,null))
        }
        plusButton.setHandler(NOT_CRITERION_ID){
            val idx = listEditor.handlers.indexOfFirst { rowId == it.getId() }
            listEditor.addRow(idx+1, WebNotCriterionHandler(listEditor.tableBox, indent, listId,null))
        }
        plusButton.setHandler(DYNAMIC_CRITERION_ID){
            val idx = listEditor.handlers.indexOfFirst { rowId == it.getId() }
            listEditor.addRow(idx+1, WebDynamicCriterionHandler(listId,null))
        }
        minusButton = WebUiLibraryAdapter.get().createLinkButton{
            icon = "core:minus"
            specificProperties["type"] = "link"
        }
        minusButton.setHandler {
            val idx = listEditor.handlers.indexOfFirst { rowId == it.getId() }
            listEditor.handlers.removeAt(idx)
            listEditor.tableBox.removeRow(idx)
            listEditor.updateToolsVisibility()
            if(listEditor.handlers.isEmpty()){
                listEditor.addEmptyRow()
            }
        }
        _node = WebGridLayoutWidget {
            noPadding = true
        }.also {
            it.setColumnsWidths("auto","auto","auto","auto","1fr")
            it.addRow(upButton, downButton, plusButton,minusButton, null)
        }
    }
}
