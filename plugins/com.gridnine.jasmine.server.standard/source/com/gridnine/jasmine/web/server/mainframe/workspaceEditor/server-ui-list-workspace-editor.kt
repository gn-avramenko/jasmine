/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.mainframe.workspaceEditor

import com.gridnine.jasmine.server.core.model.common.SelectItem
import com.gridnine.jasmine.server.core.model.domain.DomainMetaRegistry
import com.gridnine.jasmine.server.core.model.ui.GeneralSelectBoxConfiguration
import com.gridnine.jasmine.server.core.utils.ValidationUtils
import com.gridnine.jasmine.server.standard.model.domain.BaseWorkspaceCriterion
import com.gridnine.jasmine.server.standard.model.domain.ListWorkspaceItem
import com.gridnine.jasmine.server.standard.model.domain.SortOrder
import com.gridnine.jasmine.web.core.workspace.*
import com.gridnine.jasmine.web.server.components.*
import java.util.*

class ServerUiWorkspaceListEditorHandler:ServerUiWorkspaceElementEditorHandler<ServerUiWorkspaceListEditor,ListWorkspaceItem>{
    private val uid = UUID.randomUUID().toString()
    override fun getId(): String {
        return uid
    }

    override fun createEditor(): ServerUiWorkspaceListEditor {
        return ServerUiWorkspaceListEditor()
    }

    override fun setData(editor: ServerUiWorkspaceListEditor, data: ListWorkspaceItem) {
        val gpVM = WorkspaceListGeneralParametersEditorVM()
        var listId = data.listId
        gpVM.uid = data.uid
        gpVM.name = data.displayName
        val lists = DomainMetaRegistry.get().indexes.values.map { SelectItem(it.id, it.getDisplayName()!!) }.toMutableList()
        lists.addAll(DomainMetaRegistry.get().assets.values.map { SelectItem(it.id, it.getDisplayName()!!) })
        lists.sortBy { it.text }
        gpVM.list = lists.find { it.id == listId }
        val gpVS = WorkspaceListGeneralParametersEditorVS()
        gpVS.list = GeneralSelectBoxConfiguration().let {
            it.possibleValues.addAll(lists)
            it
        }
        editor.generalEditor.setData(gpVM, gpVS)

        val indexDescription = DomainMetaRegistry.get().indexes[listId]?:DomainMetaRegistry.get().assets[listId]
        if(indexDescription == null){
            return
        }
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
            columnVS.columnName = GeneralSelectBoxConfiguration().let {
                it.possibleValues.addAll(columns)
                it
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
            filterVS.filterName = GeneralSelectBoxConfiguration().let {
                it.possibleValues.addAll(columns)
                it
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
            sortOrderVS.columnName = GeneralSelectBoxConfiguration().let {
                it.possibleValues.addAll(columns)
                it
            }
            sortOrdersVS.sortOrders.add(sortOrderVS)
        }
        editor.sortOrdersEditor.setData(sortOrdersVM,sortOrdersVS)
        editor.criterionsEditor.setData(listId, data.criterions)

    }

    override fun getData(editor: ServerUiWorkspaceListEditor): ListWorkspaceItem {
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

    override fun validate(editor: ServerUiWorkspaceListEditor): Boolean {
        val generalData = editor.generalEditor.getData()
        val vv = WorkspaceListGeneralParametersEditorVV()
        if(generalData.name == null){
            vv.name = "Поле должно быть заполнено"
        }
        if(generalData.list == null){
            vv.list = "Поле должно быть заполнено"
        }
        return !ValidationUtils.hasValidationErrors(vv)
    }

    override fun getName(data: ListWorkspaceItem): String {
        return data.displayName?:"???"
    }
}

class ServerUiWorkspaceListEditor:BaseServerUiNodeWrapper<ServerUiGridLayoutContainer>(){

    val generalEditor:WorkspaceListGeneralParametersEditor

    val filtersEditor:WorkspaceListFiltersEditor

    val columnsEditor:WorkspaceListColumnsEditor

    val sortOrdersEditor:WorkspaceListSortOrdersEditor

    val criterionsEditor: ServerUiWorkspaceListCriterionsEditor

    init {
        _node = ServerUiLibraryAdapter.get().createGridLayoutContainer(ServerUiGridLayoutContainerConfiguration{
            width ="100%"
            height="100%"
            columns.add(ServerUiGridLayoutColumnConfiguration("100%"))
        })
        _node.addRow()
        generalEditor = WorkspaceListGeneralParametersEditor()
        _node.addCell(ServerUiGridLayoutCell(generalEditor))
        _node.addRow("100%")
        val accordion = ServerUiLibraryAdapter.get().createAccordionContainer(ServerUiAccordionContainerConfiguration("100%","100%"))
        columnsEditor = WorkspaceListColumnsEditor()
        accordion.addPanel(ServerUiAccordionPanel("columns", "Колонки",columnsEditor))
        filtersEditor = WorkspaceListFiltersEditor()
        accordion.addPanel(ServerUiAccordionPanel("filters","Фильтры", filtersEditor))
        sortOrdersEditor = WorkspaceListSortOrdersEditor()
        accordion.addPanel(ServerUiAccordionPanel("sortOrdersEditor","Сортировка",sortOrdersEditor))
        criterionsEditor = ServerUiWorkspaceListCriterionsEditor()
        accordion.addPanel(ServerUiAccordionPanel("criterions","Критерии",criterionsEditor))
        accordion.select("columns")
        _node.addCell(ServerUiGridLayoutCell(accordion))
        generalEditor.listWidget.changeListener = {
            columnsEditor.setData( WorkspaceListColumnsEditorVM(), WorkspaceListColumnsEditorVS())
            filtersEditor.setData( WorkspaceListFiltersEditorVM(), WorkspaceListFiltersEditorVS())
            sortOrdersEditor.setData(WorkspaceListSortOrdersEditorVM(), WorkspaceListSortOrdersEditorVS())
            criterionsEditor.setData(it?.id, arrayListOf<BaseWorkspaceCriterion>())
        }
    }
}




class ServerUiWorkspaceListCriterionsEditor: BaseServerUiNodeWrapper<ServerUiGridLayoutContainer>() {

    private val tableBox: ServerUiTable

    private var criterions = arrayListOf<BaseWorkspaceCriterion>()

    init {
        _node = ServerUiLibraryAdapter.get().createGridLayoutContainer(ServerUiGridLayoutContainerConfiguration{
            width = "100%"
            columns.add(ServerUiGridLayoutColumnConfiguration("100%"))
        })
        _node.addRow()
        tableBox = ServerUiLibraryAdapter.get().createTableBox(ServerUiTableConfiguration{
            width = "100%"
            columns.add(ServerUiTableColumnDescription("Поле", 300,300,300))
            columns.add(ServerUiTableColumnDescription("Условие", 200,200,200))
            columns.add(ServerUiTableColumnDescription("Значение", 200,200,null))
            columns.add(ServerUiTableColumnDescription(null, 140,140,140))
        })
        _node.addCell(ServerUiGridLayoutCell(tableBox))
    }


    fun setData(listId:String?, criterions: List<BaseWorkspaceCriterion>) {
        this.criterions.clear()
        this.criterions.addAll(criterions)
    }

    fun getData(): List<BaseWorkspaceCriterion> {
        return criterions
    }

}

