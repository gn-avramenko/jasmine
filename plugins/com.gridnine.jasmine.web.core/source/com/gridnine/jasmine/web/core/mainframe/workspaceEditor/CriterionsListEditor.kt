/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.mainframe.workspaceEditor

import com.gridnine.jasmine.server.core.model.common.XeptionJS
import com.gridnine.jasmine.server.standard.model.domain.*
import com.gridnine.jasmine.web.core.ui.UiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.WebComponent
import com.gridnine.jasmine.web.core.ui.components.*

class CriterionsListEditor(internal val tableBox: WebTableBox, private val indent:Int) {

    lateinit var listId:String
    internal val handlers = arrayListOf<CriterionHandler<*>>()
    fun clearData(){
        tableBox.getRows().indices.forEach {
            tableBox.removeRow(0)
        }
        handlers.clear()
    }

    fun addEmptyRow(){
        val createMenuButton = UiLibraryAdapter.get().createMenuButton(tableBox) {
            icon = "core:plus"
            items.add(WebMenuItemConfiguration("simple-restriction"){title = "Простое условие"})
            items.add(WebMenuItemConfiguration("and-restriction"){title = "Логическое И"})
            items.add(WebMenuItemConfiguration("or-restriction"){title = "Логическое ИЛИ"})
            items.add(WebMenuItemConfiguration("not-restriction"){title = "Логическое НЕ"})
        }
        createMenuButton.setHandler("simple-restriction"){
            tableBox.removeRow(0)
            addRow(0, SimpleCriterionHandler(tableBox, listId, null))
        }
        createMenuButton.setHandler("and-restriction"){
            tableBox.removeRow(0)
            addRow(0, AndCriterionHandler(tableBox, indent, listId, null))
        }
        createMenuButton.setHandler("or-restriction"){
            tableBox.removeRow(0)
            addRow(0, OrCriterionHandler(tableBox, indent, listId, null))
        }
        createMenuButton.setHandler("not-restriction"){
            tableBox.removeRow(0)
            addRow(0, NotCriterionHandler(tableBox, indent, listId, null))
        }
        tableBox.addRow(0, arrayListOf(WebTableBoxCell(null, 3), WebTableBoxCell(createMenuButton, 1)))
    }

    internal fun addRow(i: Int, handler: CriterionHandler<*>) {
        handlers.add(i, handler)
        val components = handler.getComponents()
        components.add(WebTableBoxCell(ToolsPanel(this, indent, listId, handler.getId())))
        tableBox.addRow(i, components)
    }

    fun readData(listId:String, criterions: List<BaseWorkspaceCriterionJS>) {
        this.listId = listId
        clearData()
        if(criterions.isEmpty()){
            addEmptyRow()
            return
        }
        criterions.forEach {
            val handler = when(it){
                is SimpleWorkspaceCriterionJS ->SimpleCriterionHandler(tableBox, listId, it)
                is OrWorkspaceCriterionJS -> OrCriterionHandler(tableBox, indent, listId, it)
                is AndWorkspaceCriterionJS -> AndCriterionHandler(tableBox, indent, listId, it)
                is NotWorkspaceCriterionJS -> NotCriterionHandler(tableBox, indent, listId, it)
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
            if(panel is ToolsPanel){
                panel.upButton.setEnabled(idx > 0)
                panel.downButton.setEnabled(idx < size-1)
            }
        }
    }

    fun getData(): List<BaseWorkspaceCriterionJS> {
        return handlers.mapNotNull {it.getData()}
    }
}

class ToolsPanel(private val listEditor: CriterionsListEditor, private val indent:Int, private val listId: String, private val rowId:String): WebComponent {
    internal val delegate: WebGridLayoutContainer
    internal val upButton: WebLinkButton
    internal val downButton: WebLinkButton
    internal val plusButton: WebMenuButton
    internal val minusButton: WebLinkButton
    init {
        delegate = UiLibraryAdapter.get().createGridLayoutContainer(this){
            noPadding = true
        }
        delegate.defineColumn()
        delegate.defineColumn()
        delegate.defineColumn()
        delegate.defineColumn()
        delegate.addRow()
        upButton = UiLibraryAdapter.get().createLinkButton(delegate){
            icon = "core:up"
        }
        upButton.setHandler {
            val idx = listEditor.handlers.indexOfFirst { rowId == it.getId() }!!
            val item = listEditor.handlers.removeAt(idx)
            listEditor.handlers.add(idx-1, item)
            listEditor.tableBox.moveRow(idx, idx-1)
            listEditor.updateToolsVisibility()
        }
        delegate.addCell(WebGridLayoutCell(upButton))
        downButton = UiLibraryAdapter.get().createLinkButton(delegate){
            icon = "core:down"
        }
        downButton.setHandler {
            val idx = listEditor.handlers.indexOfFirst { rowId == it.getId() }!!
            val item = listEditor.handlers.removeAt(idx)
            listEditor.handlers.add(idx+1, item)
            listEditor.tableBox.moveRow(idx, idx+1)
            listEditor.updateToolsVisibility()
        }
        delegate.addCell(WebGridLayoutCell(downButton))
        plusButton = UiLibraryAdapter.get().createMenuButton(this) {
            icon = "core:plus"
            items.add(WebMenuItemConfiguration("simple-restriction"){title = "Простое условие"})
            items.add(WebMenuItemConfiguration("and-restriction"){title = "Логическое И"})
            items.add(WebMenuItemConfiguration("or-restriction"){title = "Логическое ИЛИ"})
            items.add(WebMenuItemConfiguration("not-restriction"){title = "Логическое НЕ"})
        }
        plusButton.setHandler("simple-restriction"){
            val idx = listEditor.handlers.indexOfFirst { rowId == it.getId() }!!
            listEditor.addRow(idx+1, SimpleCriterionHandler(listEditor.tableBox, listId,null))
        }
        plusButton.setHandler("and-restriction"){
            val idx = listEditor.handlers.indexOfFirst { rowId == it.getId() }!!
            listEditor.addRow(idx+1, AndCriterionHandler(listEditor.tableBox, indent,  listId,null))
        }
        plusButton.setHandler("or-restriction"){
            val idx = listEditor.handlers.indexOfFirst { rowId == it.getId() }!!
            listEditor.addRow(idx+1, OrCriterionHandler(listEditor.tableBox, indent,  listId,null))
        }
        plusButton.setHandler("not-restriction"){
            val idx = listEditor.handlers.indexOfFirst { rowId == it.getId() }!!
            listEditor.addRow(idx+1, NotCriterionHandler(listEditor.tableBox, indent,  listId,null))
        }
        delegate.addCell(WebGridLayoutCell(plusButton))
        minusButton = UiLibraryAdapter.get().createLinkButton(delegate){
            icon = "core:minus"
        }
        minusButton.setHandler {
            val idx = listEditor.handlers.indexOfFirst { rowId == it.getId() }!!
            listEditor.handlers.removeAt(idx)
            listEditor.tableBox.removeRow(idx)
            listEditor.updateToolsVisibility()
            if(listEditor.handlers.isEmpty()){
                listEditor.addEmptyRow()
            }
        }
        delegate.addCell(WebGridLayoutCell(minusButton))
    }
    override fun getParent(): WebComponent? {
        return listEditor.tableBox
    }

    override fun getChildren(): List<WebComponent> {
        return arrayListOf(delegate)
    }

    override fun getHtml(): String {
        return delegate.getHtml()
    }

    override fun decorate() {
        delegate.decorate()
    }

    override fun destroy() {
        delegate.destroy()
    }

}

interface CriterionHandler<T:BaseWorkspaceCriterionJS>{
    fun getComponents(): MutableList<WebTableBoxCell>
    fun getId(): String
    fun getData(): T?
}

