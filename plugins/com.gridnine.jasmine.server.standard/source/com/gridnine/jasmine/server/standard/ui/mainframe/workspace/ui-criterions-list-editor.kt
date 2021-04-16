/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.standard.ui.mainframe.workspace

import com.gridnine.jasmine.common.core.model.Xeption
import com.gridnine.jasmine.common.standard.model.domain.*
import com.gridnine.jasmine.common.standard.model.l10n.StandardL10nMessagesFactory
import com.gridnine.jasmine.server.core.ui.common.BaseNodeWrapper
import com.gridnine.jasmine.server.core.ui.components.*


class CriterionsListEditor(internal val tableBox: Table, private val indent:Int) {

    lateinit var listId:String
    internal val handlers = arrayListOf<UiCriterionHandler<*>>()

    fun clearData(){
        tableBox.getRows().indices.forEach {
            tableBox.removeRow(0)
        }
        handlers.clear()
    }

    fun addEmptyRow(){
        val createMenuButton = UiLibraryAdapter.get().createMenuButton{
            iconClass = "z-icon-plus"
            items.add(MenuButtonStandardItem(StandardL10nMessagesFactory.Simple_criterion(), null, false){
                tableBox.removeRow(0)
                addRow(0, SimpleCriterionHandler(tableBox, listId, null))
            })
            items.add(MenuButtonStandardItem(StandardL10nMessagesFactory.And_criterion(), null, false){
                tableBox.removeRow(0)
                addRow(0, AndCriterionHandler(tableBox, indent, listId, null))
            })
            items.add(MenuButtonStandardItem(StandardL10nMessagesFactory.Or_criterion(), null, false){
                tableBox.removeRow(0)
                addRow(0, OrCriterionHandler(tableBox, indent, listId, null))
            })
            items.add(MenuButtonStandardItem(StandardL10nMessagesFactory.Not_criterion(), null, false){
                tableBox.removeRow(0)
               addRow(0, NotCriterionHandler(tableBox, indent, listId, null))
            })
        }
        tableBox.addRow(0, arrayListOf(TableCell(null, 3), TableCell(createMenuButton, 1)))
    }

    internal fun addRow(i: Int, handler: UiCriterionHandler<*>) {
        handlers.add(i, handler)
        val components = handler.getComponents()
        components.add(TableCell(CriterionsListToolsPanel(this, indent, listId, handler.getId())))
        tableBox.addRow(i, components)
    }

    fun setData(listId:String?, criterions: List<BaseWorkspaceCriterion>) {
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
                is SimpleWorkspaceCriterion ->SimpleCriterionHandler(tableBox, listId, it)
                is OrWorkspaceCriterion -> OrCriterionHandler(tableBox, indent, listId, it)
                is AndWorkspaceCriterion -> AndCriterionHandler(tableBox, indent, listId, it)
                is NotWorkspaceCriterion -> NotCriterionHandler(tableBox, indent, listId, it)
                else -> throw Xeption.forDeveloper("unsupported criterion type ${it::class}" )
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

    fun getData(): List<BaseWorkspaceCriterion> {
        return handlers.mapNotNull {it.getData()}
    }
}

class CriterionsListToolsPanel(private val listEditor: CriterionsListEditor, private val indent:Int, private val listId: String, private val rowId:String): BaseNodeWrapper<GridLayoutContainer>(){
    internal val upButton: LinkButton
    internal val downButton: LinkButton
    internal val plusButton: MenuButton
    internal val minusButton: LinkButton
    init {
        _node = UiLibraryAdapter.get().createGridLayoutContainer{
            noPadding = true
            columns.add(GridLayoutColumnConfiguration())
            columns.add(GridLayoutColumnConfiguration())
            columns.add(GridLayoutColumnConfiguration())
            columns.add(GridLayoutColumnConfiguration())
        }
        _node.addRow()
        upButton = UiLibraryAdapter.get().createLinkButton{
            iconClass = "z-icon-sort-up"
        }
        upButton.setHandler {
            val idx = listEditor.handlers.indexOfFirst { rowId == it.getId() }
            val item = listEditor.handlers.removeAt(idx)
            listEditor.handlers.add(idx-1, item)
            listEditor.tableBox.moveRow(idx, idx-1)
            listEditor.updateToolsVisibility()
        }
        _node.addCell(GridLayoutCell(upButton))
        downButton = UiLibraryAdapter.get().createLinkButton{
            iconClass = "z-icon-sort-down"
        }
        downButton.setHandler {
            val idx = listEditor.handlers.indexOfFirst { rowId == it.getId() }
            val item = listEditor.handlers.removeAt(idx)
            listEditor.handlers.add(idx+1, item)
            listEditor.tableBox.moveRow(idx, idx+1)
            listEditor.updateToolsVisibility()
        }
        _node.addCell(GridLayoutCell(downButton))
        plusButton = UiLibraryAdapter.get().createMenuButton{
            iconClass = "z-icon-plus"
            items.add(MenuButtonStandardItem("Простое условие", null, false){
                val idx = listEditor.handlers.indexOfFirst { rowId == it.getId() }
                listEditor.addRow(idx+1, SimpleCriterionHandler(listEditor.tableBox, listId,null))
            })
            items.add(MenuButtonStandardItem("Логическое И", null, false){
                val idx = listEditor.handlers.indexOfFirst { rowId == it.getId() }
                listEditor.addRow(idx+1, AndCriterionHandler(listEditor.tableBox, indent, listId,null))
            })
            items.add(MenuButtonStandardItem("Логическое ИЛИ", null, false){
                val idx = listEditor.handlers.indexOfFirst { rowId == it.getId() }
                listEditor.addRow(idx+1, OrCriterionHandler(listEditor.tableBox, indent, listId,null))
            })
            items.add(MenuButtonStandardItem("Логическое НЕ", null, false){
                val idx = listEditor.handlers.indexOfFirst { rowId == it.getId() }
                listEditor.addRow(idx+1, NotCriterionHandler(listEditor.tableBox, indent, listId,null))
            })
        }
        _node.addCell(GridLayoutCell(plusButton))
        minusButton = UiLibraryAdapter.get().createLinkButton{
            iconClass = "z-icon-minus"
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
        _node.addCell(GridLayoutCell(minusButton))
    }
}

