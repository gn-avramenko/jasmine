/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.standard.workspaceEditor

import com.gridnine.jasmine.common.core.meta.DomainMetaRegistryJS
import com.gridnine.jasmine.common.core.model.SelectItemJS
import com.gridnine.jasmine.common.standard.model.rest.BaseWorkspaceCriterionDTJS
import com.gridnine.jasmine.common.standard.model.rest.ListWorkspaceItemDTJS
import com.gridnine.jasmine.common.standard.model.rest.SortOrderDTJS
import com.gridnine.jasmine.common.standard.model.workspace.*
import com.gridnine.jasmine.web.core.remote.launch
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.components.BaseWebNodeWrapper
import com.gridnine.jasmine.web.core.ui.components.DefaultUIParameters
import com.gridnine.jasmine.web.standard.OptionsIds
import com.gridnine.jasmine.web.standard.WebMessages
import com.gridnine.jasmine.web.standard.mainframe.WebOptionsHandler
import com.gridnine.jasmine.web.standard.widgets.*
import kotlin.reflect.KClass

class WorkspaceListItemVariantHandler:WorkspaceItemVariantHandler<ListWorkspaceItemDTJS,WorkspaceListItemVariantEditor>{
    override fun getModelClass(): KClass<ListWorkspaceItemDTJS> {
        return ListWorkspaceItemDTJS::class
    }
    override fun createEditor(): WorkspaceListItemVariantEditor {
        return WorkspaceListItemVariantEditor()
    }

    override fun setData(editor: WorkspaceListItemVariantEditor, data: ListWorkspaceItemDTJS) {
        launch {
            editor.setData(data)
        }
    }

    override fun getData(editor: WorkspaceListItemVariantEditor): ListWorkspaceItemDTJS {
        return editor.getData()
    }
}

class WorkspaceListItemVariantEditor: BaseWebNodeWrapper<WebGridLayoutWidget>(){

    private val listTypeWidget:GeneralSelectWidget
    private val filtersEditor: WorkspaceListFiltersEditor

    private val columnsEditor:WorkspaceListColumnsEditor

    private val sortOrdersEditor: WorkspaceListSortOrdersEditor

    private var listInitialized = false

    private val criterionsEditor: WorkspaceListCriterionsEditor
    init {
        listTypeWidget = GeneralSelectWidget {
            width = DefaultUIParameters.controlWidthAsString
        }
        val accordion = WebUiLibraryAdapter.get().createAccordionContainer {
           fit = true
        }
        columnsEditor = WorkspaceListColumnsEditor()
        accordion.addPanel {
            title = WebMessages.Columns
            content = columnsEditor
        }
        filtersEditor = WorkspaceListFiltersEditor()
        accordion.addPanel {
            title = WebMessages.Filters
            content = filtersEditor
        }
        sortOrdersEditor = WorkspaceListSortOrdersEditor()
        accordion.addPanel {
            title = "Сортировка"
            content = sortOrdersEditor
        }
        criterionsEditor = WorkspaceListCriterionsEditor()
        accordion.addPanel {
            title = "Критерии"
            content = criterionsEditor
        }
        _node = WebGridLayoutWidget {
            width = "100%"
            height = "100%"
        }.also {
            it.setColumnsWidths("100%")
            it.addRow(WebGridCellWidget("Тип объекта", listTypeWidget))
            it.addRow("100%", arrayListOf(WebGridLayoutWidgetCell(accordion)))
        }
        listTypeWidget.setChangeListener {
            if(it != null) {
                WebOptionsHandler.get().ensureOptionLoaded(OptionsIds.standard_list_ids, it.id)
                columnsEditor.readData(WorkspaceListColumnsEditorVMJS(), null)
                filtersEditor.readData( WorkspaceListFiltersEditorVMJS(),  null)
                sortOrdersEditor.readData(WorkspaceListSortOrdersEditorVMJS(), null)
                criterionsEditor.setData(it.id, arrayListOf())
            }
        }
    }

    fun getData(): ListWorkspaceItemDTJS {
        val result = ListWorkspaceItemDTJS()
        result.listId = listTypeWidget.getValue()!!.id
        result.columns.addAll(columnsEditor.getData().columns.map { it.columnName!!.id })
        result.filters.addAll(filtersEditor.getData().filters.map { it.filterName!!.id })
        result.sortOrders.addAll(sortOrdersEditor.getData().sortOrders.map {
            val res = SortOrderDTJS()
            res.field = it.columnName?.id
            res.orderType = it.sortOrder
            res
        })
        result.criterions.addAll(criterionsEditor.getData())
        return result
    }

    suspend fun setData(data: ListWorkspaceItemDTJS){
        val items = WebOptionsHandler.get().getOptionsFor(OptionsIds.standard_list_ids).sortedBy { it.text }
        if(!listInitialized){
            listTypeWidget.setPossibleValues(items)
            listInitialized = true
        }
        WebOptionsHandler.get().ensureOptionLoaded(OptionsIds.standard_list_ids, data.listId!!)
        listTypeWidget.setValue(items.find { it.id == data.listId })
        val listId = data.listId+"JS"
        val indexDescription = DomainMetaRegistryJS.get().indexes[listId]?:DomainMetaRegistryJS.get().assets[listId]!!
        val columns = indexDescription.properties.values.map {  SelectItemJS(it.id, it.displayName!!)}.toMutableList()
        columns.addAll(indexDescription.collections.values.map {  SelectItemJS(it.id, it.displayName!!)})
        columns.sortBy { it.text }
        val columnsVM = WorkspaceListColumnsEditorVMJS()
        val columnsVS = WorkspaceListColumnsEditorVSJS()
        data.columns.withIndex().forEach { (idx, col) ->
            val columnVM = WorkspaceListColumnsTableVMJS()
            columnVM.uid = "column$idx"
            columnVM.columnName = columns.find { it.id == col }!!
            columnsVM.columns.add(columnVM)
            val columnVS = WorkspaceListColumnsTableVSJS()
            columnVS.uid = "column$idx"
            columnVS.setColumnName {
                possibleValues.addAll(columns)
            }
            columnsVS.columns.add(columnVS)
        }
        columnsEditor.readData(columnsVM,columnsVS)
        columnsEditor.columnsWidget.setNewRowVSFactory {
            WorkspaceListColumnsTableVSJS().apply {
                setColumnName { possibleValues.addAll(columns)}
            }
        }
        val filtersVM = WorkspaceListFiltersEditorVMJS()
        val filtersVS = WorkspaceListFiltersEditorVSJS()
        data.filters.withIndex().forEach { (idx, col) ->
            val filterVM = WorkspaceListFiltersTableVMJS()
            filterVM.uid = "filter$idx"
            filterVM.filterName = columns.find { it.id == col }!!
            filtersVM.filters.add(filterVM)
            val filterVS = WorkspaceListFiltersTableVSJS()
            filterVS.uid = "filter$idx"
            filterVS.setFilterName {
                possibleValues.addAll(columns)
            }
            filtersVS.filters.add(filterVS)
        }
        filtersEditor.readData(filtersVM,filtersVS)
        filtersEditor.filtersWidget.setNewRowVSFactory {
            WorkspaceListFiltersTableVSJS().apply {
                setFilterName { possibleValues.addAll(columns)}
            }
        }
        val sortOrdersVM = WorkspaceListSortOrdersEditorVMJS()
        val sortOrdersVS = WorkspaceListSortOrdersEditorVSJS()
        data.sortOrders.withIndex().forEach { (idx, col) ->
            val sortOrderVM = WorkspaceListSortOrdersTableVMJS()
            sortOrderVM.uid = "sortOrder$idx"
            sortOrderVM.columnName = columns.find { it.id == col.field }
            sortOrderVM.sortOrder = col.orderType
            sortOrdersVM.sortOrders.add(sortOrderVM)
            val sortOrderVS = WorkspaceListSortOrdersTableVSJS()
            sortOrderVS.uid = "sortOrder$idx"
            sortOrderVS.setColumnName {
                possibleValues.addAll(columns)
            }
            sortOrdersVS.sortOrders.add(sortOrderVS)
        }
        sortOrdersEditor.readData(sortOrdersVM,sortOrdersVS)
        sortOrdersEditor.sortOrdersWidget.setNewRowVSFactory {
            WorkspaceListSortOrdersTableVSJS().apply {
                setColumnName { possibleValues.addAll(columns)}
            }
        }
        criterionsEditor.setData(listId, data.criterions)
    }
}

class WorkspaceListCriterionsEditor: BaseWebNodeWrapper<WebGridLayoutWidget>() {

    private val tableBox: GeneralTableBoxWidget

    private val criterionsListEditor:WebWorkspaceCriterionsListEditor

    init {
        tableBox = GeneralTableBoxWidget {
            width = "100%"
            headerComponents.add(WebLabelWidget("Поле"))
            headerComponents.add(WebLabelWidget("Условие"))
            headerComponents.add(WebLabelWidget("Значение"))
            columnWidths.add(WebGeneralTableBoxWidgetColumnWidth(300,300,300))
            columnWidths.add(WebGeneralTableBoxWidgetColumnWidth(180,180,180))
            columnWidths.add(WebGeneralTableBoxWidgetColumnWidth(500,500,null))
            columnWidths.add(WebGeneralTableBoxWidgetColumnWidth(140,140,140))
        }
        criterionsListEditor = WebWorkspaceCriterionsListEditor(tableBox, 0)
        _node = WebGridLayoutWidget{
            width = "100%"
        }.also {
            it.setColumnsWidths("100%")
            it.addRow(tableBox)
        }
    }


    fun setData(listId:String?, criterions: List<BaseWorkspaceCriterionDTJS>) {
        criterionsListEditor.setData(listId, criterions)
    }

    fun getData(): List<BaseWorkspaceCriterionDTJS> {
        return criterionsListEditor.getData()
    }

}