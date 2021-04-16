/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.standard.ui.mainframe.workspace

import com.gridnine.jasmine.common.core.meta.DomainMetaRegistry
import com.gridnine.jasmine.common.core.model.SelectItem
import com.gridnine.jasmine.common.standard.model.domain.BaseWorkspaceCriterion
import com.gridnine.jasmine.common.standard.model.domain.ListWorkspaceItem
import com.gridnine.jasmine.common.standard.model.domain.SortOrder
import com.gridnine.jasmine.common.standard.model.workspace.*
import com.gridnine.jasmine.server.core.ui.common.BaseNodeWrapper
import com.gridnine.jasmine.server.core.ui.components.*
import com.gridnine.jasmine.server.core.ui.utils.UiUtils
import java.util.*

class WorkspaceListEditorHandler:WorkspaceElementEditorHandler<WorkspaceListEditor,ListWorkspaceItem>{
    private val uid = UUID.randomUUID().toString()
    override fun getId(): String {
        return uid
    }

    override fun createEditor(): WorkspaceListEditor {
        return WorkspaceListEditor()
    }

    override fun setData(editor: WorkspaceListEditor, data: ListWorkspaceItem) {
        val gpVM = WorkspaceListGeneralParametersEditorVM()
        val listId = data.listId
        gpVM.uid = data.uid
        gpVM.name = data.displayName
        val lists = DomainMetaRegistry.get().indexes.values.map { SelectItem(it.id, it.getDisplayName()!!) }.toMutableList()
        lists.addAll(DomainMetaRegistry.get().assets.values.map { SelectItem(it.id, it.getDisplayName()!!) })
        lists.sortBy { it.text }
        gpVM.list = lists.find { it.id == listId }
        val gpVS = WorkspaceListGeneralParametersEditorVS()
        gpVS.setList {
            possibleValues.addAll(lists)
        }
        editor.generalEditor.setData(gpVM, gpVS)

        val indexDescription = DomainMetaRegistry.get().indexes[listId]?:DomainMetaRegistry.get().assets[listId]
        ?: return
        val columns = indexDescription.properties.values.map {  SelectItem(it.id, it.getDisplayName()!!)}.toMutableList()
        columns.addAll(indexDescription.collections.values.map {  SelectItem(it.id, it.getDisplayName()!!)})
        columns.sortBy { it.text }
        val columnsVM = WorkspaceListColumnsEditorVM()
        val columnsVS = WorkspaceListColumnsEditorVS()
        data.columns.withIndex().forEach { (idx, col) ->
            val columnVM = WorkspaceListColumnsTableVM()
            columnVM.uid = "column$idx"
            columnVM.columnName = columns.find { it.id == col }!!
            columnsVM.columns.add(columnVM)
            val columnVS = WorkspaceListColumnsTableVS()
            columnVS.uid = "column$idx"
            columnVS.setColumnName {
                possibleValues.addAll(columns)
            }
            columnsVS.columns.add(columnVS)
        }
        editor.columnsEditor.setData(columnsVM,columnsVS)
        val filtersVM = WorkspaceListFiltersEditorVM()
        val filtersVS = WorkspaceListFiltersEditorVS()
        data.filters.withIndex().forEach { (idx, col) ->
            val filterVM = WorkspaceListFiltersTableVM()
            filterVM.uid = "filter$idx"
            filterVM.filterName = columns.find { it.id == col }!!
            filtersVM.filters.add(filterVM)
            val filterVS = WorkspaceListFiltersTableVS()
            filterVS.uid = "filter$idx"
            filterVS.setFilterName {
                possibleValues.addAll(columns)
            }
            filtersVS.filters.add(filterVS)
        }
        editor.filtersEditor.setData(filtersVM,filtersVS)

        val sortOrdersVM = WorkspaceListSortOrdersEditorVM()
        val sortOrdersVS = WorkspaceListSortOrdersEditorVS()
        data.sortOrders.withIndex().forEach { (idx, col) ->
            val sortOrderVM = WorkspaceListSortOrdersTableVM()
            sortOrderVM.uid = "sortOrder$idx"
            sortOrderVM.columnName = columns.find { it.id == col.field }
            sortOrderVM.sortOrder = col.orderType
            sortOrdersVM.sortOrders.add(sortOrderVM)
            val sortOrderVS = WorkspaceListSortOrdersTableVS()
            sortOrderVS.uid = "sortOrder$idx"
            sortOrderVS.setColumnName {
                possibleValues.addAll(columns)
            }
            sortOrdersVS.sortOrders.add(sortOrderVS)
        }
        editor.sortOrdersEditor.setData(sortOrdersVM,sortOrdersVS)
        editor.criterionsEditor.setData(listId, data.criterions)

    }

    override fun getData(editor: WorkspaceListEditor): ListWorkspaceItem {
        val result = ListWorkspaceItem()
        val generalData = editor.generalEditor.getData()
        result.uid = generalData.uid!!
        result.listId = generalData.list!!.id
        result.displayName = generalData.name
        result.columns.addAll(editor.columnsEditor.getData().columns.map { it.columnName!!.id })
        result.filters.addAll(editor.filtersEditor.getData().filters.map { it.filterName!!.id })
        result.sortOrders.addAll(editor.sortOrdersEditor.getData().sortOrders.map {
            val res = SortOrder()
            res.field = it.columnName?.id
            res.orderType = it.sortOrder
            res
        })
        result.criterions.addAll(editor.criterionsEditor.getData())
        return result
    }

    override fun validate(editor: WorkspaceListEditor): Boolean {
        val generalData = editor.generalEditor.getData()
        val vv = WorkspaceListGeneralParametersEditorVV()
        if(generalData.name == null){
            vv.name = "Поле должно быть заполнено"
        }
        if(generalData.list == null){
            vv.list = "Поле должно быть заполнено"
        }
        return !UiUtils.hasValidationErrors(vv)
    }

    override fun getName(data: ListWorkspaceItem): String {
        return data.displayName?:"???"
    }
}

class WorkspaceListEditor:BaseNodeWrapper<GridLayoutContainer>(){

    val generalEditor:WorkspaceListGeneralParametersEditor

    val filtersEditor:WorkspaceListFiltersEditor

    val columnsEditor:WorkspaceListColumnsEditor

    val sortOrdersEditor:WorkspaceListSortOrdersEditor

    val criterionsEditor: WorkspaceListCriterionsEditor

    init {
        _node = UiLibraryAdapter.get().createGridLayoutContainer{
            width ="100%"
            height="100%"
            columns.add(GridLayoutColumnConfiguration("100%"))
        }
        _node.addRow()
        generalEditor = WorkspaceListGeneralParametersEditor()
        _node.addCell(GridLayoutCell(generalEditor))
        _node.addRow("100%")
        val accordion = UiLibraryAdapter.get().createAccordionContainer {
            width = "100%"
            height = "100%"
        }
        columnsEditor = WorkspaceListColumnsEditor()
        accordion.addPanel(AccordionPanel("columns", "Колонки",columnsEditor))
        filtersEditor = WorkspaceListFiltersEditor()
        accordion.addPanel(AccordionPanel("filters","Фильтры", filtersEditor))
        sortOrdersEditor = WorkspaceListSortOrdersEditor()
        accordion.addPanel(AccordionPanel("sortOrdersEditor","Сортировка",sortOrdersEditor))
        criterionsEditor = WorkspaceListCriterionsEditor()
        accordion.addPanel(AccordionPanel("criterions","Критерии",criterionsEditor))
        accordion.select("columns")
        _node.addCell(GridLayoutCell(accordion))
        generalEditor.listWidget.setChangeListener {
            columnsEditor.setData( WorkspaceListColumnsEditorVM(), WorkspaceListColumnsEditorVS())
            filtersEditor.setData( WorkspaceListFiltersEditorVM(), WorkspaceListFiltersEditorVS())
            sortOrdersEditor.setData(WorkspaceListSortOrdersEditorVM(), WorkspaceListSortOrdersEditorVS())
            criterionsEditor.setData(it?.id, arrayListOf())
        }
    }
}




class WorkspaceListCriterionsEditor: BaseNodeWrapper<GridLayoutContainer>() {

    private val tableBox: Table

    private val criterionsListEditor:CriterionsListEditor

    init {
        _node = UiLibraryAdapter.get().createGridLayoutContainer{
            width = "100%"
            columns.add(GridLayoutColumnConfiguration("100%"))
        }
        _node.addRow()
        tableBox = UiLibraryAdapter.get().createTable {
            width = "100%"
            columns.add(TableColumnDescription("Поле", 300,300,300))
            columns.add(TableColumnDescription("Условие", 200,200,200))
            columns.add(TableColumnDescription("Значение", 200,200,null))
            columns.add(TableColumnDescription(null, 140,140,140))
        }
        _node.addCell(GridLayoutCell(tableBox))
        criterionsListEditor = CriterionsListEditor(tableBox, 0)
    }


    fun setData(listId:String?, criterions: List<BaseWorkspaceCriterion>) {
        criterionsListEditor.setData(listId, criterions)
    }

    fun getData(): List<BaseWorkspaceCriterion> {
        return criterionsListEditor.getData()
    }

}

