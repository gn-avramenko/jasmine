/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST", "UNUSED_VARIABLE")

package com.gridnine.jasmine.web.easyui.mainframe

import com.gridnine.jasmine.server.standard.model.rest.ListWorkspaceItemDTJS
import com.gridnine.jasmine.web.core.model.domain.DomainMetaRegistryJS
import com.gridnine.jasmine.web.core.model.ui.*
import com.gridnine.jasmine.web.easyui.jQuery
import com.gridnine.jasmine.web.easyui.widgets.EasyUiSelectWidget
import com.gridnine.jasmine.web.easyui.widgets.EasyUiTextBoxWidget

class EasyUiWorkspaceListEditor : EasyUiWorkspaceElementEditor<ListWorkspaceItemDTJS>{

    private lateinit var nameWidget:TextBoxWidget

    private lateinit var listSelectWidget: SelectWidget

    private lateinit var possibleListValues:List<SelectItemJS>


    override fun getContent():String {
        return "<div id=\"editor-wrapper\" fit=\"true\"><div region=\"north\" border=\"false\" style=\"height:80px\">${createNameDiv()}\n${createListControl()}</div><div region=\"center\" >${createAccordion()}</div></div>"
    }

    private fun createAccordion(): String {
        return "<div id=\"accordion\" fit=\"true\" class=\"easyui-accordion\">\n${createFieldsDiv()}\n${createSortOrdersDiv()}\n${createFiltersDiv()}\n${createCriterionsDiv()}\n</div>"
    }

    private fun createCriterionsDiv(): String {
        return "<div title=\"Критерии\"></div>"

    }

    private fun createFiltersDiv(): String {
        return "<div title=\"Фильтры\"></div>"
    }

    private fun createSortOrdersDiv(): String {
        return "<div title=\"Сортировки\"></div>"
    }

    private fun createFieldsDiv(): String {
        return "<div title=\"Поля\"><table id = \"fields-table\" style = \"width:${labelWidth+ inputWidth}px\"></div>"
    }

    private fun createListControl() = "<div style=\"padding-top:5px\"><div class=\"jasmine-label\" style=\"display:inline-block;width:${labelWidth}px;position:relative;top:2px\">Список: </div><input id =\"list-select\" style =\"width:${inputWidth}px\"></div>"

    private fun createNameDiv() = "<div style=\"padding-top:5px\"><div class=\"jasmine-label\" style=\"display:inline-block;width:${labelWidth}px;position:relative;top:2px\">Название: </div><input id =\"item-name\" style =\"width:${inputWidth}px\"></div>"

    override fun decorate() {
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
        }
        jQuery("#accordion").accordion()
    }

    override fun setData(data: ListWorkspaceItemDTJS) {
        nameWidget.setData(data.displayName)
        listSelectWidget.setData(data.listId?.let {
            SelectItemJS(it, possibleListValues.find { elm ->elm.id == it }?.caption)
        })
    }

    override fun getData(): ListWorkspaceItemDTJS {
        val result = ListWorkspaceItemDTJS()
        result.displayName = nameWidget.getData()
        result.listId = listSelectWidget.getData()?.id
        return result
    }

    override fun validate(): Boolean {
        var hasErrors = false
        run {
            val res = nameWidget.getData()
            if (res.isNullOrBlank()) {
                nameWidget.showValidation("Нужно заполнить название")
                hasErrors = true
            }
        }
        run{
            val res = listSelectWidget.getData()
            if (res == null) {
                listSelectWidget.showValidation("Нужно заполнить список")
                hasErrors = true
            }
        }
        return !hasErrors
    }

    companion object{
        private const val labelWidth = 70
        private const val inputWidth = 300
    }
}