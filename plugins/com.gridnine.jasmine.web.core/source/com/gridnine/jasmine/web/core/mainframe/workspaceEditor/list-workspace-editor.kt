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
        editor.criterionsEditor.readData(data.criterions)

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
            criterionsEditor.readData(arrayListOf<BaseWorkspaceCriterionJS>())
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

class WorkspaceListGeneralParametersEditor(private val parent:WebComponent):WebEditor<WorkspaceListGeneralParametersEditorVMJS, WorkspaceListGeneralParametersEditorVSJS,WorkspaceListGeneralParametersEditorVVJS>{

    private val delegate: WebGridLayoutContainer

    val nameWidget: TextBoxWidget

    val listWidget:GeneralSelectWidget

    var uid:String? = null

    init {
        delegate = UiLibraryAdapter.get().createGridLayoutContainer(this) {}
        delegate.defineColumn(DefaultUIParameters.controlWidthAsString)
        delegate.defineColumn(DefaultUIParameters.controlWidthAsString)
        delegate.addRow()
        val nameCell = GridCellWidget(delegate, L10nMetaRegistryJS.get().messages["com.gridnine.jasmine.web.core.workspace.WorkspaceListGeneralParametersEditor"]?.get("name")
                ?: "???") { par ->
            TextBoxWidget(par) {
                width = "100%"
            }
        }
        delegate.addCell(WebGridLayoutCell(nameCell))
        nameWidget = nameCell.widget
        val listCell = GridCellWidget(delegate, L10nMetaRegistryJS.get().messages["com.gridnine.jasmine.web.core.workspace.WorkspaceListGeneralParametersEditor"]?.get("list")
                ?: "???") { par ->
            GeneralSelectWidget(par) {
                width = "100%"
            }
        }
        delegate.addCell(WebGridLayoutCell(listCell))
        listWidget = listCell.widget
    }

    override fun readData(vm: WorkspaceListGeneralParametersEditorVMJS, vs: WorkspaceListGeneralParametersEditorVSJS) {
        nameWidget.setValue(vm.name)
        uid = vm.uid
        vs.name?.let { nameWidget.configure(it) }
        listWidget.setValue(vm.list)
        vs.list?.let { listWidget.configure(it) }
    }

    override fun setReadonly(value: Boolean) {
        nameWidget.setReadonly(value)
    }

    override fun getParent(): WebComponent? {
        return parent
    }

    override fun destroy() {
        delegate.destroy()
    }

    override fun getData(): WorkspaceListGeneralParametersEditorVMJS {
        val result = WorkspaceListGeneralParametersEditorVMJS()
        result.name = nameWidget.getValue()
        result.list = listWidget.getValue()
        result.uid = uid
        return result
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

    override fun showValidation(validation: WorkspaceListGeneralParametersEditorVVJS) {
        validation.name?.let { nameWidget.showValidation(it) }
        validation.list?.let { listWidget.showValidation(it) }
    }

}

class WorkspaceListColumnsEditor(private val parent:WebComponent): WebEditor<WorkspaceListColumnsEditorVMJS, WorkspaceListColumnsEditorVSJS, WorkspaceListColumnsEditorVVJS> {

    private val delegate:WebGridLayoutContainer = UiLibraryAdapter.get().createGridLayoutContainer(this) {}

    val tableWidget: TableBoxWidget<WorkspaceListColumnsTableVMJS, WorkspaceListColumnsTableVSJS, WorkspaceListColumnsTableVVJS>

    init {
        delegate.defineColumn("100%")
        delegate.addRow()
        tableWidget = TableBoxWidget(parent){
            width = "100%"
            column("columnName", GeneralSelectBoxWidgetDescriptionJS(false ), L10nMetaRegistryJS.get().messages["com.gridnine.jasmine.web.core.workspace.WorkspaceListColumnsTable"]!!["columnName"] ?: error(""), 200)
            showToolsColumn = true
            vmFactory = {WorkspaceListColumnsTableVMJS()}
            vsFactory = {
                val listId = UiUtils.findParent(this@WorkspaceListColumnsEditor,  WorkspaceListEditor::class)!!.generalEditor.listWidget.getValue()!!.id
                val indexDescription = DomainMetaRegistryJS.get().indexes[listId]?:DomainMetaRegistryJS.get().assets[listId]!!
                val columns = indexDescription.properties.values.map {  SelectItemJS(it.id, it.displayName)}.toMutableList()
                columns.addAll(indexDescription.collections.values.map {  SelectItemJS(it.id, it.displayName)})
                columns.sortBy { it.text }
                val result = WorkspaceListColumnsTableVSJS()
                result.uid =MiscUtilsJS.createUUID()
                result.columnName = GeneralSelectBoxConfigurationJS().let {
                    it.possibleValues.addAll(columns)
                    it
                }
                result
            }
        }
        delegate.addCell(WebGridLayoutCell(tableWidget))

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

    override fun getData(): WorkspaceListColumnsEditorVMJS {
        val result = WorkspaceListColumnsEditorVMJS()
        result.columns.addAll(tableWidget.getData())
        return result
    }

    override fun readData(vm: WorkspaceListColumnsEditorVMJS, vs: WorkspaceListColumnsEditorVSJS) {
        tableWidget.readData(vm.columns, vs.columns)
    }

    override fun setReadonly(value: Boolean) {
        tableWidget.setReadonly(value)
    }

    override fun showValidation(validation: WorkspaceListColumnsEditorVVJS) {
        tableWidget.showValidation(validation.columns)
    }


}

class WorkspaceListFiltersEditor(private val parent:WebComponent): WebEditor<WorkspaceListFiltersEditorVMJS, WorkspaceListFiltersEditorVSJS, WorkspaceListFiltersEditorVVJS> {

    private val delegate:WebGridLayoutContainer = UiLibraryAdapter.get().createGridLayoutContainer(this) {}

    private val tableWidget: TableBoxWidget<WorkspaceListFiltersTableVMJS, WorkspaceListFiltersTableVSJS, WorkspaceListFiltersTableVVJS>

    init {
        delegate.defineColumn("100%")
        delegate.addRow()
        tableWidget = TableBoxWidget(parent){
            width = "100%"
            column("filterName", GeneralSelectBoxWidgetDescriptionJS(false ), L10nMetaRegistryJS.get().messages["com.gridnine.jasmine.web.core.workspace.WorkspaceListFiltersTable"]!!["filterName"] ?: error(""), 200)
            showToolsColumn = true
            vmFactory = {WorkspaceListFiltersTableVMJS()}
            vsFactory = {
                val listId = UiUtils.findParent(this@WorkspaceListFiltersEditor,  WorkspaceListEditor::class)!!.generalEditor.listWidget.getValue()!!.id
                val indexDescription = DomainMetaRegistryJS.get().indexes[listId]?:DomainMetaRegistryJS.get().assets[listId]!!
                val columns = indexDescription.properties.values.map {  SelectItemJS(it.id, it.displayName)}.toMutableList()
                columns.addAll(indexDescription.collections.values.map {  SelectItemJS(it.id, it.displayName)})
                columns.sortBy { it.text }
                val result = WorkspaceListFiltersTableVSJS()
                result.uid =MiscUtilsJS.createUUID()
                result.filterName = GeneralSelectBoxConfigurationJS().let {
                    it.possibleValues.addAll(columns)
                    it
                }
                result
            }
        }
        delegate.addCell(WebGridLayoutCell(tableWidget))

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

    override fun getData(): WorkspaceListFiltersEditorVMJS {
        val result = WorkspaceListFiltersEditorVMJS()
        result.filters.addAll(tableWidget.getData())
        return result
    }

    override fun readData(vm: WorkspaceListFiltersEditorVMJS, vs: WorkspaceListFiltersEditorVSJS) {
        tableWidget.readData(vm.filters, vs.filters)
    }

    override fun setReadonly(value: Boolean) {
        tableWidget.setReadonly(value)
    }

    override fun showValidation(validation: WorkspaceListFiltersEditorVVJS) {
        tableWidget.showValidation(validation.filters)
    }


}

class WorkspaceListSortOrdersEditor(private val parent:WebComponent): WebEditor<WorkspaceListSortOrdersEditorVMJS, WorkspaceListSortOrdersEditorVSJS, WorkspaceListSortOrdersEditorVVJS> {

    private val delegate:WebGridLayoutContainer = UiLibraryAdapter.get().createGridLayoutContainer(this) {}

    private val tableWidget: TableBoxWidget<WorkspaceListSortOrdersTableVMJS, WorkspaceListSortOrdersTableVSJS, WorkspaceListSortOrdersTableVVJS>

    init {
        delegate.defineColumn("100%")
        delegate.addRow()
        tableWidget = TableBoxWidget(parent){
            width = "100%"
            column("columnName", GeneralSelectBoxWidgetDescriptionJS(false ), L10nMetaRegistryJS.get().messages["com.gridnine.jasmine.web.core.workspace.WorkspaceListSortOrdersTable"]!!["columnName"] ?: error(""), 200)
            column("sortOrder", EnumSelectBoxWidgetDescriptionJS(false , "com.gridnine.jasmine.server.standard.model.domain.SortOrderTypeJS"), L10nMetaRegistryJS.get().messages["com.gridnine.jasmine.web.core.workspace.WorkspaceListSortOrdersTable"]!!["sortOrder"] ?: error(""), 100)
            showToolsColumn = true
            vmFactory = {WorkspaceListSortOrdersTableVMJS()}
            vsFactory = {
                val listId = UiUtils.findParent(this@WorkspaceListSortOrdersEditor,  WorkspaceListEditor::class)!!.generalEditor.listWidget.getValue()!!.id
                val indexDescription = DomainMetaRegistryJS.get().indexes[listId]?:DomainMetaRegistryJS.get().assets[listId]!!
                val columns = indexDescription.properties.values.map {  SelectItemJS(it.id, it.displayName)}.toMutableList()
                columns.addAll(indexDescription.collections.values.map {  SelectItemJS(it.id, it.displayName)})
                columns.sortBy { it.text }
                val result = WorkspaceListSortOrdersTableVSJS()
                result.uid =MiscUtilsJS.createUUID()
                result.columnName = GeneralSelectBoxConfigurationJS().let {
                    it.possibleValues.addAll(columns)
                    it
                }
                result
            }
        }
        delegate.addCell(WebGridLayoutCell(tableWidget))

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

    override fun getData(): WorkspaceListSortOrdersEditorVMJS {
        val result = WorkspaceListSortOrdersEditorVMJS()
        result.sortOrders.addAll(tableWidget.getData())
        return result
    }

    override fun readData(vm: WorkspaceListSortOrdersEditorVMJS, vs: WorkspaceListSortOrdersEditorVSJS) {
        tableWidget.readData(vm.sortOrders, vs.sortOrders)
    }

    override fun setReadonly(value: Boolean) {
        tableWidget.setReadonly(value)
    }

    override fun showValidation(validation: WorkspaceListSortOrdersEditorVVJS) {
        tableWidget.showValidation(validation.sortOrders)
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
            columnWidths.add(WebTableBoxColumnWidth(null, 300, null))
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

    fun readData(criterions: List<BaseWorkspaceCriterionJS>) {
        criterionsListEditor.readData(criterions)
    }

    fun getData(): List<BaseWorkspaceCriterionJS> {
        return arrayListOf()
    }



}

