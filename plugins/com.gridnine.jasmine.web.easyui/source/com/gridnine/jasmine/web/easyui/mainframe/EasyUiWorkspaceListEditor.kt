/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST", "UNUSED_VARIABLE")

package com.gridnine.jasmine.web.easyui.mainframe

import com.gridnine.jasmine.server.standard.model.rest.ListWorkspaceItemDTJS
import com.gridnine.jasmine.server.standard.model.rest.SortOrderDTJS
import com.gridnine.jasmine.server.standard.model.rest.SortOrderTypeDTJS
import com.gridnine.jasmine.web.core.model.domain.BaseIndexDescriptionJS
import com.gridnine.jasmine.web.core.model.domain.DomainMetaRegistryJS
import com.gridnine.jasmine.web.core.model.ui.*
import com.gridnine.jasmine.web.easyui.jQuery
import com.gridnine.jasmine.web.easyui.widgets.EasyUiSelectWidget
import com.gridnine.jasmine.web.easyui.widgets.EasyUiTableWidget
import com.gridnine.jasmine.web.easyui.widgets.EasyUiTextBoxWidget

class EasyUiWorkspaceListEditor : EasyUiWorkspaceElementEditor<ListWorkspaceItemDTJS>{

    private lateinit var nameWidget:TextBoxWidget

    private lateinit var listSelectWidget: SelectWidget

    private lateinit var possibleListValues:List<SelectItemJS>

    private lateinit var possibleFieldValues:List<SelectItemJS>

    private lateinit var possibleSortOrderValues:List<SelectItemJS>

    private lateinit var fieldsWidget: TableWidget<SimplePropertyWrapperVMJS<SelectItemJS>,SimplePropertyWrapperVSJS<SelectColumnConfigurationJS>,SimplePropertyWrapperVVJS>

    private lateinit var filtersWidget: TableWidget<SimplePropertyWrapperVMJS<SelectItemJS>,SimplePropertyWrapperVSJS<SelectColumnConfigurationJS>,SimplePropertyWrapperVVJS>

    private lateinit var sortOrdersWidget: TableWidget<SortOrderWrapperVMJS,SortOrderWrapperVSJS,SortOrderWrapperVVJS>

    private lateinit var criterionsEditor: EasyUiWorkspaceCriterionsEditor

    override fun getContent():String {
        return "<div id=\"editor-wrapper\" fit=\"true\"><div region=\"north\" border=\"false\" style=\"height:80px\">${createNameDiv()}\n${createListControl()}</div><div region=\"center\" >${createAccordion()}</div></div>"
    }

    private fun createAccordion(): String {
        return "<div id=\"accordion\" fit=\"true\" class=\"easyui-accordion\">\n${createFieldsDiv()}\n${createSortOrdersDiv()}\n${createFiltersDiv()}\n${createCriterionsDiv()}\n</div>"
    }

    private fun createCriterionsDiv(): String {
        return "<div title=\"Критерии\"><div id = \"criterions\" style = \"width:100%;height:100%\"/></div>"

    }

    private fun createFiltersDiv(): String {
        return "<div title=\"Фильтры\"><table id = \"filters\" style = \"width:${labelWidth+ inputWidth}px\"/></div>"
    }

    private fun createSortOrdersDiv(): String {
        return "<div title=\"Сортировки\"><table id = \"sortOrders\" style = \"width:${2*(labelWidth + inputWidth)}px\"/></div>"
    }

    private fun createFieldsDiv(): String {
        return "<div title=\"Поля\"><table id = \"fields\" style = \"width:${labelWidth+ inputWidth}px\"/></div>"
    }

    private fun createListControl() = "<div style=\"padding-top:5px\"><div class=\"jasmine-label\" style=\"display:inline-block;width:${labelWidth}px;position:relative;top:2px\">Список: </div><input id =\"list-select\" style =\"width:${inputWidth}px\"></div>"

    private fun createNameDiv() = "<div style=\"padding-top:5px\"><div class=\"jasmine-label\" style=\"display:inline-block;width:${labelWidth}px;position:relative;top:2px\">Название: </div><input id =\"item-name\" style =\"width:${inputWidth}px\"></div>"

    override fun decorate() {
        possibleSortOrderValues = arrayListOf(SelectItemJS(SortOrderTypeDTJS.ASC.name, "По возрастанию"), SelectItemJS(SortOrderTypeDTJS.DESC.name, "По убыванию"))
        jQuery("#editor-wrapper").layout()
        run {
            val description = TextboxDescriptionJS("item-name")
            nameWidget = EasyUiTextBoxWidget("", description)
            nameWidget.configure(Unit)
        }
        run {
            val description = SelectDescriptionJS("list-select")
            val config = SelectConfigurationJS()
            config.nullAllowed = false
            UiMetaRegistryJS.get().lists.values.forEach {
                val descriptionId = "${it.objectId}JS"
                val descr = DomainMetaRegistryJS.get().indexes[descriptionId]?:DomainMetaRegistryJS.get().assets[descriptionId]?:throw IllegalArgumentException("unable to find description for list $descriptionId")
                config.possibleValues.add(SelectItemJS(it.id, descr.displayName))
            }
            listSelectWidget = EasyUiSelectWidget("", description)
            listSelectWidget.configure(config)
            possibleListValues = config.possibleValues
            listSelectWidget.valueChangeListener = {newValue, _ ->
                possibleFieldValues = getPossibleFieldValues(newValue?.id)
                val fieldConfig = SelectColumnConfigurationJS()
                fieldConfig.nullAllowed = false
                fieldConfig.possibleValues.addAll(possibleFieldValues)
                val conf = SimplePropertyWrapperVSJS(fieldConfig)
                val fieldsTableConfig = TableConfigurationJS<SimplePropertyWrapperVSJS<SelectColumnConfigurationJS>>()
                fieldsTableConfig.columnSettings = conf
                fieldsWidget.configure(fieldsTableConfig)
                fieldsWidget.readData(arrayListOf())
                val filtersTableConfig = TableConfigurationJS<SimplePropertyWrapperVSJS<SelectColumnConfigurationJS>>()
                filtersTableConfig.columnSettings = conf
                filtersWidget.configure(filtersTableConfig)
                filtersWidget.readData(arrayListOf())
                val sortOrderTableConfig = TableConfigurationJS<SortOrderWrapperVSJS>()
                val sortOrderColumnSettings = SortOrderWrapperVSJS()
                sortOrderColumnSettings.field = fieldConfig
                val sortOrderConfig = SelectColumnConfigurationJS()
                sortOrderConfig.nullAllowed = false
                sortOrderConfig.possibleValues.addAll(possibleSortOrderValues)
                sortOrderColumnSettings.order = sortOrderConfig
                sortOrderTableConfig.columnSettings = sortOrderColumnSettings
                sortOrdersWidget.configure(sortOrderTableConfig)
                sortOrdersWidget.readData(arrayListOf())
                criterionsEditor = EasyUiWorkspaceCriterionsEditor("criterions", newValue?.id)
                criterionsEditor.clear()
            }
        }
        run{
            val tableDescription = TableDescriptionJS("","com.gridnine.jasmine.web.core.model.ui.SimplePropertyWrapper")
            val columnDescr = SelectTableColumnDescriptionJS(SimplePropertyWrapperVMJS.property, "Поле")
            tableDescription.columns[columnDescr.id] = columnDescr
            fieldsWidget = EasyUiTableWidget("fields", tableDescription)
        }
        run{
            val tableDescription = TableDescriptionJS("","com.gridnine.jasmine.web.core.model.ui.SimplePropertyWrapper")
            val columnDescr = SelectTableColumnDescriptionJS(SimplePropertyWrapperVMJS.property, "Поле")
            tableDescription.columns[columnDescr.id] = columnDescr
            filtersWidget = EasyUiTableWidget("filters", tableDescription)
        }
        run{
            val tableDescription = TableDescriptionJS("","com.gridnine.jasmine.web.easyui.mainframe.EasyUiWorkspaceListEditor.SortOrderWrapper")
            val fieldColumnDescr = SelectTableColumnDescriptionJS(SortOrderWrapperVMJS.field, "Поле")
            tableDescription.columns[fieldColumnDescr.id] = fieldColumnDescr
            val sortOrderDescription = SelectTableColumnDescriptionJS(SortOrderWrapperVMJS.order, "Сортировка")
            tableDescription.columns[sortOrderDescription.id] = sortOrderDescription
            sortOrdersWidget = EasyUiTableWidget("sortOrders", tableDescription)
        }

        jQuery("#accordion").accordion()
    }




    override fun setData(data: ListWorkspaceItemDTJS) {
        nameWidget.setData(data.displayName)
        listSelectWidget.setData(data.listId?.let {
            SelectItemJS(it, possibleListValues.find { elm ->elm.id == it }?.caption)
        })
        val fieldsList = data.columns.withIndex().map { (idx, c) -> SimplePropertyWrapperVMJS("field$idx", SelectItemJS(c, possibleFieldValues.find { it.id == c }?.caption)) }.toList()
        fieldsWidget.readData(fieldsList)

        val filtersList = data.filters.withIndex().map { (idx, c) -> SimplePropertyWrapperVMJS("filter$idx", SelectItemJS(c, possibleFieldValues.find { it.id == c }?.caption)) }.toList()
        filtersWidget.readData(filtersList)

        val sortOrderList = data.sortOrders.withIndex().map { (idx, order) ->
                val res = SortOrderWrapperVMJS()
                res.uid = "sortOrder$idx"
                res.field = SelectItemJS(order.field, possibleFieldValues.find { it.id == order.field }?.caption)
                res.order = SelectItemJS(order.orderType?.name, possibleSortOrderValues.find { it.id == order.orderType?.name}?.caption)
                res
        }.toList()
        sortOrdersWidget.readData(sortOrderList)
        criterionsEditor.readData(data.criterions)
    }

    override fun getData(): ListWorkspaceItemDTJS {
        val result = ListWorkspaceItemDTJS()
        result.displayName = nameWidget.getData()
        result.listId = listSelectWidget.getData()?.id
        val fields = arrayListOf<SimplePropertyWrapperVMJS<SelectItemJS>>()
        fieldsWidget.writeData(fields)
        result.columns.clear()
        result.columns.addAll(fields.mapNotNull { it.property?.id }.distinct().toList())
        val filters = arrayListOf<SimplePropertyWrapperVMJS<SelectItemJS>>()
        filtersWidget.writeData(filters)
        result.filters.clear()
        result.filters.addAll(filters.mapNotNull { it.property?.id }.distinct().toList())

        val sortOrders = arrayListOf<SortOrderWrapperVMJS>()
        sortOrdersWidget.writeData(sortOrders)
        result.sortOrders.clear()
        result.sortOrders.addAll(sortOrders.mapNotNull {
            if(it.order == null || it.field == null){
                return@mapNotNull null
            }
            val res = SortOrderDTJS()
            res.field = it.field?.id
            res.orderType = SortOrderTypeDTJS.valueOf(it.order!!.id!!)
            res
        }.toList())
        criterionsEditor.writeData(result.criterions)

        return result
    }

    override fun validate(): Boolean {
        var hasErrors = false
        run {
            val res = nameWidget.getData()
            if (res.isNullOrBlank()) {
                nameWidget.showValidation("Нужно заполнить название")
                hasErrors = true
            } else {
                nameWidget.showValidation(null)
            }
        }
        run{
            val res = listSelectWidget.getData()
            if (res == null) {
                listSelectWidget.showValidation("Нужно заполнить список")
                hasErrors = true
            } else{
                listSelectWidget.showValidation(null)
            }
        }
        run{
            val fields = arrayListOf<SimplePropertyWrapperVMJS<SelectItemJS>>()
            fieldsWidget.writeData(fields)
            val validation = arrayListOf<SimplePropertyWrapperVVJS>()
            fields.forEach {
                val validationItem= SimplePropertyWrapperVVJS()
                validation.add(validationItem)
                if(it.property == null){
                    validationItem.property = "Нужно заполнить поле"
                    hasErrors = true
                }
            }
            fieldsWidget.showValidation(validation)

        }
        run{
            val filters = arrayListOf<SimplePropertyWrapperVMJS<SelectItemJS>>()
            filtersWidget.writeData(filters)
            val validation = arrayListOf<SimplePropertyWrapperVVJS>()
            filters.forEach {
                val validationItem= SimplePropertyWrapperVVJS()
                validation.add(validationItem)
                if(it.property == null){
                    validationItem.property = "Нужно заполнить поле"
                    hasErrors = true
                }
            }
            filtersWidget.showValidation(validation)

        }
        run{
            val sortOrders = arrayListOf<SortOrderWrapperVMJS>()
            sortOrdersWidget.writeData(sortOrders)
            val validation = arrayListOf<SortOrderWrapperVVJS>()
            sortOrders.forEach {
                val validationItem= SortOrderWrapperVVJS()
                validation.add(validationItem)
                if(it.field == null){
                    validationItem.field = "Нужно заполнить поле"
                    hasErrors = true
                }
                if(it.order == null){
                    validationItem.order = "Нужно заполнить поле"
                    hasErrors = true
                }
            }
            sortOrdersWidget.showValidation(validation)

        }
        return !hasErrors
    }

    class SortOrderWrapperVMJS:BaseVMEntityJS(){
         var field:SelectItemJS? = null
         var order:SelectItemJS? = null

        override fun getValue(propertyName: String): Any? {
            if(SortOrderWrapperVMJS.field == propertyName){
                return field
            }
            if(SortOrderWrapperVMJS.order == propertyName){
                return order
            }
            return super.getValue(propertyName)
        }

        override fun setValue(propertyName: String, value: Any?) {
            if(SortOrderWrapperVMJS.field == propertyName){
                field = value as SelectItemJS?
                return
            }
            if(SortOrderWrapperVMJS.order == propertyName){
                order = value as SelectItemJS?
                return
            }
            super.setValue(propertyName, value)
        }

        companion object{
            const val field = "field"
            const val order = "order"
        }
    }

    class SortOrderWrapperVSJS:BaseVSEntityJS(){
        lateinit var field:SelectColumnConfigurationJS
        lateinit var order:SelectColumnConfigurationJS

        override fun getValue(propertyName: String): Any? {
            if(SortOrderWrapperVSJS.field == propertyName){
                return field
            }
            if(SortOrderWrapperVSJS.order == propertyName){
                return order
            }
            return super.getValue(propertyName)
        }

        override fun setValue(propertyName: String, value: Any?) {
            if(SortOrderWrapperVSJS.field == propertyName){
                field = value as SelectColumnConfigurationJS
                return
            }
            if(SortOrderWrapperVSJS.order == propertyName){
                order = value as SelectColumnConfigurationJS
                return
            }
            super.setValue(propertyName, value)
        }

        companion object{
            const val field = "field"
            const val order = "order"
        }
    }

    class SortOrderWrapperVVJS:BaseVVEntityJS(){
        var field:String? = null
        var order:String? = null

        override fun getValue(propertyName: String): Any? {
            if(SortOrderWrapperVVJS.field == propertyName){
                return field
            }
            if(SortOrderWrapperVVJS.order == propertyName){
                return order
            }
            return super.getValue(propertyName)
        }

        override fun setValue(propertyName: String, value: Any?) {
            if(SortOrderWrapperVVJS.field == propertyName){
                field = value as String?
                return
            }
            if(SortOrderWrapperVVJS.order == propertyName){
                order = value as String
                return
            }
            super.setValue(propertyName, value)
        }

        companion object{
            const val field = "field"
            const val order = "order"
        }
    }

    companion object{
        private const val labelWidth = 70
        private const val inputWidth = 300

        fun getListDescription(listId:String?): BaseIndexDescriptionJS? {
            if(listId == null){
                return null
            }
            val listDescr = UiMetaRegistryJS.get().lists[listId]?:throw IllegalArgumentException("unable to find description for list $listId")
            val domainDescr = listDescr.objectId+"JS"
            return DomainMetaRegistryJS.get().indexes[domainDescr]?:DomainMetaRegistryJS.get().assets[domainDescr]?:throw IllegalArgumentException("unable to find description for list $domainDescr")

        }

        fun getPossibleFieldValues(listId:String?): List<SelectItemJS> {
            val descr = getListDescription(listId)?: return emptyList<SelectItemJS>()
            val result = arrayListOf<SelectItemJS>()
            descr.properties.values.forEach { result.add(SelectItemJS(it.id,it.displayName)) }
            descr.collections.values.forEach { result.add(SelectItemJS(it.id,it.displayName)) }
            result.sortBy { it.caption }
            return result
        }
    }
}

