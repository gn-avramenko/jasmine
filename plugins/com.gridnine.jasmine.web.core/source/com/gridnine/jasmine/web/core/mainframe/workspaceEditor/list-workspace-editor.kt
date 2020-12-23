/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.mainframe.workspaceEditor

import com.gridnine.jasmine.server.core.model.common.SelectItemJS
import com.gridnine.jasmine.server.core.model.domain.DomainMetaRegistryJS
import com.gridnine.jasmine.server.core.model.l10n.L10nMetaRegistryJS
import com.gridnine.jasmine.server.core.model.ui.*
import com.gridnine.jasmine.server.standard.model.domain.BaseWorkspaceCriterionJS
import com.gridnine.jasmine.server.standard.model.domain.ListWorkspaceItemJS
import com.gridnine.jasmine.server.standard.model.domain.SortOrderJS
import com.gridnine.jasmine.web.core.CoreWebMessagesJS
import com.gridnine.jasmine.web.core.ui.DefaultUIParameters
import com.gridnine.jasmine.web.core.ui.UiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.WebComponent
import com.gridnine.jasmine.web.core.ui.WebEditor
import com.gridnine.jasmine.web.core.ui.components.*
import com.gridnine.jasmine.web.core.ui.widgets.GeneralSelectWidget
import com.gridnine.jasmine.web.core.ui.widgets.GridCellWidget
import com.gridnine.jasmine.web.core.ui.widgets.TableBoxWidget
import com.gridnine.jasmine.web.core.ui.widgets.TextBoxWidget
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import com.gridnine.jasmine.web.core.utils.UiUtils
import com.gridnine.jasmine.web.core.utils.ValidationUtilsJS
import com.gridnine.jasmine.web.core.workspace.*

class WorkspaceListEditorHandler:WorkspaceElementEditorHandler<WorkspaceListEditor,ListWorkspaceItemJS>{
    override fun getId(): String {
        return "listEditor"
    }

    override fun createEditor(parent: WebComponent): WorkspaceListEditor {
        return WorkspaceListEditor(parent)
    }

    override fun setData(editor: WorkspaceListEditor, data: ListWorkspaceItemJS) {
        val gpVM = WorkspaceListGeneralParametersEditorVMJS()
        var listId = data.listId+"JS"
        gpVM.uid = data.uid
        gpVM.name = data.displayName
        val lists = DomainMetaRegistryJS.get().indexes.values.map { SelectItemJS(it.id, it.displayName) }.toMutableList()
        lists.addAll(DomainMetaRegistryJS.get().assets.values.map { SelectItemJS(it.id, it.displayName) })
        lists.sortBy { it.text }
        gpVM.list = lists.find { it.id == listId }
        val gpVS = WorkspaceListGeneralParametersEditorVSJS()
        gpVS.list = GeneralSelectBoxConfigurationJS().let {
            it.possibleValues.addAll(lists)
            it
        }
        editor.generalEditor.readData(gpVM, gpVS)

        val indexDescription = DomainMetaRegistryJS.get().indexes[listId]?:DomainMetaRegistryJS.get().assets[listId]
        if(indexDescription == null){
            return
        }
        val columns = indexDescription.properties.values.map {  SelectItemJS(it.id, it.displayName)}.toMutableList()
        columns.addAll(indexDescription.collections.values.map {  SelectItemJS(it.id, it.displayName)})
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
            columnVS.columnName = GeneralSelectBoxConfigurationJS().let {
                it.possibleValues.addAll(columns)
                it
            }
            columnsVS.columns.add(columnVS)
        }
        editor.columnsEditor.readData(columnsVM,columnsVS)
        val filtersVM = WorkspaceListFiltersEditorVMJS()
        val filtersVS = WorkspaceListFiltersEditorVSJS()
        data.filters.withIndex().forEach { (idx, col) ->
            val filterVM = WorkspaceListFiltersTableVMJS()
            filterVM.uid = "filter$idx"
            filterVM.filterName = columns.find { it.id == col }!!
            filtersVM.filters.add(filterVM)
            val filterVS = WorkspaceListFiltersTableVSJS()
            filterVS.uid = "filter$idx"
            filterVS.filterName = GeneralSelectBoxConfigurationJS().let {
                it.possibleValues.addAll(columns)
                it
            }
            filtersVS.filters.add(filterVS)
        }
        editor.filtersEditor.readData(filtersVM,filtersVS)

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
            sortOrderVS.columnName = GeneralSelectBoxConfigurationJS().let {
                it.possibleValues.addAll(columns)
                it
            }
            sortOrdersVS.sortOrders.add(sortOrderVS)
        }
        editor.sortOrdersEditor.readData(sortOrdersVM,sortOrdersVS)
        editor.criterionsEditor.readData(listId, data.criterions)

    }

    override fun getData(editor: WorkspaceListEditor): ListWorkspaceItemJS {
        val result = ListWorkspaceItemJS()
        val generalData = editor.generalEditor.getData()
        result.uid = generalData.uid!!
        result.listId = MiscUtilsJS.toServerClassName(generalData.list!!.id)
        result.displayName = generalData.name
        result.columns.addAll(editor.columnsEditor.getData().columns.map { it.columnName!!.id })
        result.filters.addAll(editor.filtersEditor.getData().filters.map { it.filterName!!.id })
        result.sortOrders.addAll(editor.sortOrdersEditor.getData().sortOrders.map {
            val res = SortOrderJS()
            res.field = it.columnName?.id
            res.orderType = it.sortOrder
            res
        })
        result.criterions.addAll(editor.criterionsEditor.getData())
        return result
    }

    override fun validate(editor: WorkspaceListEditor): Boolean {
        val generalData = editor.generalEditor.getData()
        val vv = WorkspaceListGeneralParametersEditorVVJS()
        if(generalData.name == null){
            vv.name = "Поле должно быть заполнено"
        }
        if(generalData.list == null){
            vv.list = "Поле должно быть заполнено"
        }
        return !ValidationUtilsJS.hasValidationErrors(vv)
    }

    override fun getName(data: ListWorkspaceItemJS): String {
        return data.displayName?:"???"
    }
}

class WorkspaceListEditor(private val parent:WebComponent):WebComponent{
    private val delegate: WebGridLayoutContainer

    val generalEditor:WorkspaceListGeneralParametersEditor

    val filtersEditor:WorkspaceListFiltersEditor

    val columnsEditor:WorkspaceListColumnsEditor

    val sortOrdersEditor:WorkspaceListSortOrdersEditor

    val criterionsEditor:WorkspaceListCriterionsEditor
    init {
        delegate = UiLibraryAdapter.get().createGridLayoutContainer(this) {
            width ="100%"
            height="100%"
        }
        delegate.defineColumn("100%")
        delegate.addRow()
        generalEditor = WorkspaceListGeneralParametersEditor(delegate)
        delegate.addCell(WebGridLayoutCell(generalEditor))
        delegate.addRow("100%")
        val accordion = UiLibraryAdapter.get().createAccordionContainer(delegate){
            width ="100%"
            height="100%"
        }
        columnsEditor = WorkspaceListColumnsEditor(accordion)
        accordion.addPanel(WebAccordionContainer.panel {
            id = "columns"
            title = CoreWebMessagesJS.Columns
            content = columnsEditor
        })
        filtersEditor = WorkspaceListFiltersEditor(accordion)
        accordion.addPanel(WebAccordionContainer.panel {
            id = "filters"
            title = CoreWebMessagesJS.Filters
            content = filtersEditor
        })
        sortOrdersEditor = WorkspaceListSortOrdersEditor(accordion)
        accordion.addPanel(WebAccordionContainer.panel {
            id = "sortOrdersEditor"
            title = "Сортировка"
            content = sortOrdersEditor
        })
        criterionsEditor = WorkspaceListCriterionsEditor(accordion)
        accordion.addPanel(WebAccordionContainer.panel {
            id = "criterions"
            title = "Критерии"
            content = criterionsEditor
        })
        accordion.select("columns")
        delegate.addCell(WebGridLayoutCell(accordion))
        generalEditor.listWidget.changeListener = {
            columnsEditor.readData( WorkspaceListColumnsEditorVMJS(), WorkspaceListColumnsEditorVSJS())
            filtersEditor.readData( WorkspaceListFiltersEditorVMJS(), WorkspaceListFiltersEditorVSJS())
            criterionsEditor.readData(it!!.id, arrayListOf<BaseWorkspaceCriterionJS>())
        }
    }
    override fun getParent(): WebComponent? {
        return parent
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




class WorkspaceListCriterionsEditor(private val parent:WebComponent): WebComponent {

    private val delegate:WebGridLayoutContainer = UiLibraryAdapter.get().createGridLayoutContainer(this) {}

    private val tableBox: WebTableBox

    private var criterionsListEditor:CriterionsListEditor

    init {
        delegate.defineColumn("100%")
        delegate.addRow()
        tableBox = UiLibraryAdapter.get().createTableBox(delegate){
            width = "100%"
            columnWidths.add(WebTableBoxColumnWidth(300, 300, 300))
            columnWidths.add(WebTableBoxColumnWidth(200, 200, 200))
            columnWidths.add(WebTableBoxColumnWidth(200, 200, null))
            columnWidths.add(WebTableBoxColumnWidth(140, 140, 140))
            val fieldLabel = UiLibraryAdapter.get().createLabel(delegate)
            fieldLabel.setText("Поле")
            headerComponents.add(fieldLabel)
            val conditionLabel = UiLibraryAdapter.get().createLabel(delegate)
            conditionLabel.setText("Условие")
            headerComponents.add(conditionLabel)
            val valueLabel = UiLibraryAdapter.get().createLabel(delegate)
            valueLabel.setText("Значение")
            headerComponents.add(valueLabel)
            headerComponents.add(null)
        }
        delegate.addCell(WebGridLayoutCell(tableBox))
        criterionsListEditor = CriterionsListEditor(tableBox, 0)

    }

    override fun getParent(): WebComponent? {
        return parent
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

    fun readData(listId:String, criterions: List<BaseWorkspaceCriterionJS>) {
        criterionsListEditor.readData(listId, criterions)
    }

    fun getData(): List<BaseWorkspaceCriterionJS> {
        return criterionsListEditor.getData()
    }



}

