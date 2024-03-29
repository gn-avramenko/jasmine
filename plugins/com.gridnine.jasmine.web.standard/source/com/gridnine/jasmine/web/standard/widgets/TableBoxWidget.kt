/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("UNCHECKED_CAST", "unused")

package com.gridnine.jasmine.web.standard.widgets

import com.gridnine.jasmine.common.core.model.*
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.components.BaseWebNodeWrapper
import com.gridnine.jasmine.web.core.ui.components.WebLinkButton
import com.gridnine.jasmine.web.core.ui.components.WebNode
import com.gridnine.jasmine.web.core.ui.components.WebTag
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import kotlin.js.Date

class TableBoxWidget<VM:BaseTableBoxVMJS,VS:BaseTableBoxVSJS, VV:BaseTableBoxVVJS>(configure:TableBoxWidgetConfiguration<VM,VS>.()->Unit):BaseWebNodeWrapper<GeneralTableBoxWidget>(){
    internal val rowsAdditionalData = arrayListOf<TableBoxWidgetRowAdditionalData>()
    private val config = TableBoxWidgetConfiguration<VM,VS>()
    private  var createButton:WebLinkButton? = null
    private var readonly = false
    private var vsFactory:(()->VS)? = null
    init {
        config.configure()
        vsFactory = config.vsFactory
        _node = GeneralTableBoxWidget{
            width = config.width
            height = config.height
            config.columns.forEach {
                val label = WebLabelWidget(it.title) {
                    width = "100%"
                }
                headerComponents.add(label)
                columnWidths.add(WebGeneralTableBoxWidgetColumnWidth(null,it.width?:100, null))
            }
            if(config.showToolsColumn) {
                createButton = WebUiLibraryAdapter.get().createLinkButton {
                    icon = "core:plus"
                    specificProperties["type"] = "link"
                }
                createButton!!.setHandler {
                    addRow(0)
                }
                headerComponents.add(createButton)
                columnWidths.add(WebGeneralTableBoxWidgetColumnWidth(130, 130, 130))
            }
        }
    }

    fun setNewRowVSFactory(factory: ()->VS){
        vsFactory = factory
    }
    internal fun addRow(idx:Int){
        val uuid = MiscUtilsJS.createUUID()
        val rowId = MiscUtilsJS.createUUID()
        val vm = config.vmFactory.invoke()
        vm.uid = uuid
        val components = arrayListOf<WebGeneralTableBoxWidgetCell>()
        val configuration = vsFactory?.invoke()
        config.columns.withIndex().forEach {(collIdx, coll) ->
            val comp = createWebComponent(coll.widgetDescription)
            configure(comp, collIdx, configuration)
            components.add(WebGeneralTableBoxWidgetCell(comp))
        }
        if(config.showToolsColumn) {
            components.add(WebGeneralTableBoxWidgetCell(TableBoxWidgetToolsPanel(this@TableBoxWidget, rowId)))
        }
        _node.addRow(idx, components)
        rowsAdditionalData.add(idx, TableBoxWidgetRowAdditionalData(uuid, rowId))
        updateToolsVisibility()
    }


    fun getData(): List<VM> {
        val result = arrayListOf<VM>()
        val rows = _node.getRows()
        rows.withIndex().forEach {(rowIdx, row) ->
            val vm = config.vmFactory.invoke()
            vm.uid = rowsAdditionalData[rowIdx].uid
            val size = row.size
            row.withIndex().forEach { (idx, comp) ->
                if (idx < size - 1) {
                    val column = config.columns[idx]
                    when (column.widgetDescription.widgetType) {
                        WidgetTypeJS.TEXT_BOX -> vm.setValue(column.id, (comp as TextBoxWidget).getValue())
                        WidgetTypeJS.PASSWORD_BOX ->  TODO()
                        WidgetTypeJS.FLOAT_NUMBER_BOX -> vm.setValue(column.id, (comp as FloatNumberBoxWidget).getValue())
                        WidgetTypeJS.INTEGER_NUMBER_BOX -> vm.setValue(column.id, (comp as IntegerNumberBoxWidget).getValue())
                        WidgetTypeJS.BOOLEAN_BOX -> vm.setValue(column.id, (comp as BooleanBoxWidget).getValue())
                        WidgetTypeJS.ENTITY_SELECT_BOX -> vm.setValue(column.id, (comp as EntitySelectWidget).getValue())
                        WidgetTypeJS.ENUM_SELECT_BOX -> vm.setValue(column.id, (comp as EnumValueWidget<FakeEnumJS>).getValue())
                        WidgetTypeJS.DATE_BOX -> vm.setValue(column.id, (comp as DateBoxWidget).getValue())
                        WidgetTypeJS.DATE_TIME_BOX -> vm.setValue(column.id, (comp as DateTimeBoxWidget).getValue())
                        WidgetTypeJS.TABLE_BOX -> throw XeptionJS.forDeveloper("unsupported type : TABLE_BOX")
                        WidgetTypeJS.GENERAL_SELECT_BOX -> vm.setValue(column.id, (comp as GeneralSelectWidget).getValue())
                    }
                }
            }
            result.add(vm)
        }
        return result
    }

    fun readData(vm: List<VM>, vs: List<VS>?) {
        val size = vm.size
        for( n in rowsAdditionalData.size-1 downTo  size){
            rowsAdditionalData.removeAt(if(size > 0) size-1 else 0)
            _node.removeRow(if(size > 0) size-1 else 0)
        }
        val existingRows = _node.getRows()
        vm.withIndex().forEach{(idx, value) ->
            if(rowsAdditionalData.size> idx){
                rowsAdditionalData[idx].uid = value.uid
                val components = existingRows[idx]
                val aSize = components.size
                components.withIndex().forEach { (compIdx, comp) ->
                    if(compIdx < aSize -1) {
                        setValue(comp!!, compIdx, value)
                        configure(comp, compIdx, if(vs != null && vs.isNotEmpty()) vs[idx] else null)
                    }
                }
            } else {
                val components = arrayListOf<WebGeneralTableBoxWidgetCell>()
                config.columns.withIndex().forEach {(collIdx, coll) ->
                    val comp = createWebComponent(coll.widgetDescription)
                    setValue(comp, collIdx, value)
                    configure(comp, collIdx, if(vs != null && vs.isNotEmpty()) vs[idx] else null)
                    components.add(WebGeneralTableBoxWidgetCell(comp))
                }
                val rowId = MiscUtilsJS.createUUID()
                if(config.showToolsColumn) {
                    components.add(WebGeneralTableBoxWidgetCell(TableBoxWidgetToolsPanel(this@TableBoxWidget, rowId)))
                }
                _node.addRow(null, components)
                rowsAdditionalData.add(TableBoxWidgetRowAdditionalData(value.uid, rowId))
            }
        }
        updateToolsVisibility()
    }

    internal fun updateToolsVisibility(){
        if(config.showToolsColumn) {
            createButton!!.setEnabled(!readonly)
            val rows = _node.getRows()
            val size = rows.size
            rows.withIndex().forEach { (idx, row) ->
                val comp = row.last()
                if (comp is TableBoxWidgetToolsPanel) {
                    comp.downButton.setEnabled(!readonly && idx < size - 1)
                    comp.upButton.setEnabled(!readonly && idx > 0)
                    comp.plusButton.setEnabled(!readonly)
                    comp.minusButton.setEnabled(!readonly)
                    if (readonly) {
                        comp.rightDiv.getClass().addClasses("jasmine-table-right-div-disabled")
                    } else {
                        comp.rightDiv.getClass().removeClasses("jasmine-table-right-div-disabled")
                    }
                }
            }
        }
    }

    private fun createWebComponent(widgetDescription: BaseWidgetDescriptionJS): WebNode {
        return when(widgetDescription.widgetType){
            WidgetTypeJS.TEXT_BOX -> {
                widgetDescription as TextBoxWidgetDescriptionJS
                TextBoxWidget {
                    width = "100%"
                    notEditable = widgetDescription.notEditable
                }
            }
            WidgetTypeJS.PASSWORD_BOX ->  TODO()
            WidgetTypeJS.FLOAT_NUMBER_BOX -> {
                widgetDescription as BigDecimalNumberBoxWidgetDescriptionJS
                FloatNumberBoxWidget {
                    width = "100%"
                    notEditable = widgetDescription.notEditable
                }
            }
            WidgetTypeJS.INTEGER_NUMBER_BOX ->  IntegerNumberBoxWidget {
                widgetDescription as IntegerNumberBoxWidgetDescriptionJS
                width = "100%"
                nullable = !widgetDescription.nonNullable
                notEditable = widgetDescription.notEditable
            }
            WidgetTypeJS.BOOLEAN_BOX  -> {
                widgetDescription as BooleanBoxWidgetDescriptionJS
                BooleanBoxWidget {
                    width = "100%"
                    notEditable = widgetDescription.notEditable
                }
            }
            WidgetTypeJS.ENTITY_SELECT_BOX ->  EntitySelectWidget {
                widgetDescription as EntitySelectBoxWidgetDescriptionJS
                width = "100%"
                handler = AutocompleteHandler.createMetadataBasedAutocompleteHandler(widgetDescription.objectId)
                showClearIcon = true
                notEditable = widgetDescription.notEditable
            }
            WidgetTypeJS.ENUM_SELECT_BOX ->  EnumValueWidget<FakeEnumJS> {
                widgetDescription as EnumSelectBoxWidgetDescriptionJS
                width = "100%"
                enumClassName = widgetDescription.enumId
                notEditable = widgetDescription.notEditable
            }
            WidgetTypeJS.DATE_BOX -> {
                widgetDescription as DateBoxWidgetDescriptionJS
                DateBoxWidget {
                    width = "100%"
                    notEditable = widgetDescription.notEditable
                }
            }
            WidgetTypeJS.DATE_TIME_BOX -> {
                widgetDescription as DateTimeBoxWidgetDescriptionJS
                DateTimeBoxWidget {
                    width = "100%"
                    notEditable = widgetDescription.notEditable
                }
            }
            WidgetTypeJS.GENERAL_SELECT_BOX -> {
                widgetDescription as GeneralSelectBoxWidgetDescriptionJS
                GeneralSelectWidget {
                    width = "100%"
                    notEditable = widgetDescription.notEditable
                }
            }
            WidgetTypeJS.TABLE_BOX -> throw XeptionJS.forDeveloper("unsupported type : TABLE_BOX")
        }
    }

    private fun setValue(comp: WebNode, compIdx: Int, value: VM) {
        val column = config.columns[compIdx]
        when(column.widgetDescription.widgetType){
            WidgetTypeJS.TEXT_BOX -> (comp as TextBoxWidget).setValue(value.getValue(column.id) as String?)
            WidgetTypeJS.PASSWORD_BOX -> TODO()
            WidgetTypeJS.FLOAT_NUMBER_BOX -> (comp as FloatNumberBoxWidget).setValue(value.getValue(column.id) as Double?)
            WidgetTypeJS.INTEGER_NUMBER_BOX -> (comp as IntegerNumberBoxWidget).setValue(value.getValue(column.id) as Int?)
            WidgetTypeJS.BOOLEAN_BOX -> (comp as BooleanBoxWidget).setValue(value.getValue(column.id) as Boolean)
            WidgetTypeJS.ENTITY_SELECT_BOX -> (comp as EntitySelectWidget).setValue(value.getValue(column.id) as ObjectReferenceJS?)
            WidgetTypeJS.ENUM_SELECT_BOX -> (comp as EnumValueWidget<*>).setValue(value.getValue(column.id).asDynamic())
            WidgetTypeJS.DATE_BOX -> (comp as DateBoxWidget).setValue(value.getValue(column.id) as Date?)
            WidgetTypeJS.DATE_TIME_BOX  -> (comp as DateTimeBoxWidget).setValue(value.getValue(column.id) as Date?)
            WidgetTypeJS.TABLE_BOX -> throw XeptionJS.forDeveloper("unsupported type : TABLE_BOX")
            WidgetTypeJS.GENERAL_SELECT_BOX -> (comp as GeneralSelectWidget).setValue(value.getValue(column.id) as SelectItemJS?)
        }
    }

    private fun configure(comp: WebNode, compIdx: Int, value: VS?) {
        val column = config.columns[compIdx]
        val vsValue = value?.getValue(column.id) ?: return
        when(column.widgetDescription.widgetType){
            WidgetTypeJS.TEXT_BOX -> (comp as TextBoxWidget).configure(vsValue as TextBoxConfigurationJS)
            WidgetTypeJS.PASSWORD_BOX -> TODO()
            WidgetTypeJS.FLOAT_NUMBER_BOX -> (comp as FloatNumberBoxWidget).configure(vsValue as BigDecimalBoxConfigurationJS)
            WidgetTypeJS.INTEGER_NUMBER_BOX -> (comp as IntegerNumberBoxWidget).configure(vsValue as IntegerNumberBoxConfigurationJS)
            WidgetTypeJS.BOOLEAN_BOX -> (comp as BooleanBoxWidget).configure(vsValue as BooleanBoxConfigurationJS)
            WidgetTypeJS.ENTITY_SELECT_BOX -> (comp as EntitySelectWidget).configure(vsValue as EntitySelectBoxConfigurationJS)
            WidgetTypeJS.ENUM_SELECT_BOX -> (comp as EnumValueWidget<*>).configure(vsValue as EnumSelectBoxConfigurationJS)
            WidgetTypeJS.DATE_BOX ->(comp as DateBoxWidget).configure(vsValue as DateBoxConfigurationJS)
            WidgetTypeJS.DATE_TIME_BOX  -> (comp as DateTimeBoxWidget).configure(vsValue as DateTimeBoxConfigurationJS)
            WidgetTypeJS.TABLE_BOX -> throw XeptionJS.forDeveloper("unsupported type : TABLE_BOX")
            WidgetTypeJS.GENERAL_SELECT_BOX -> (comp as GeneralSelectWidget).configure(vsValue as GeneralSelectBoxConfigurationJS)
        }
    }

    fun setReadonly(value: Boolean) {
        readonly = value
        val rows = _node.getRows()
        rows.forEach { row ->
            val limit = if(config.showToolsColumn) row.size-1 else row.size
            row.withIndex().forEach {(idx, comp) ->
                if(idx < limit) {
                    when (config.columns[idx].widgetDescription.widgetType) {
                        WidgetTypeJS.TEXT_BOX -> (comp as TextBoxWidget).setReadonly(value)
                        WidgetTypeJS.PASSWORD_BOX -> TODO()
                        WidgetTypeJS.FLOAT_NUMBER_BOX -> (comp as FloatNumberBoxWidget).setReadonly(value)
                        WidgetTypeJS.INTEGER_NUMBER_BOX -> (comp as IntegerNumberBoxWidget).setReadonly(value)
                        WidgetTypeJS.BOOLEAN_BOX -> (comp as BooleanBoxWidget).setReadonly(value)
                        WidgetTypeJS.ENTITY_SELECT_BOX -> (comp as EntitySelectWidget).setReadonly(value)
                        WidgetTypeJS.ENUM_SELECT_BOX -> (comp as EnumValueWidget<*>).setReadonly(value)
                        WidgetTypeJS.DATE_BOX -> (comp as DateBoxWidget).setReadonly(value)
                        WidgetTypeJS.DATE_TIME_BOX -> (comp as DateTimeBoxWidget).setReadonly(value)
                        WidgetTypeJS.TABLE_BOX -> throw XeptionJS.forDeveloper("unsupported type : TABLE_BOX")
                        WidgetTypeJS.GENERAL_SELECT_BOX  -> (comp as GeneralSelectWidget).setReadonly(value)
                    }
                }
            }
        }
        updateToolsVisibility()
    }

    fun showValidation(vv: List<VV>) {
        val rows = _node.getRows()
        rows.withIndex().forEach { (rowIdx, row) ->
            val validation = vv[rowIdx]
            val size = row.size
            row.withIndex().forEach { (colIdx, comp) ->
                if (colIdx < size - (if(config.showToolsColumn) 1 else 0)) {
                    val column = config.columns[colIdx]
                    when (column.widgetDescription.widgetType) {
                        WidgetTypeJS.TEXT_BOX -> (comp as TextBoxWidget).showValidation(validation.getValue(column.id) as String?)
                        WidgetTypeJS.PASSWORD_BOX -> TODO()
                        WidgetTypeJS.FLOAT_NUMBER_BOX -> (comp as FloatNumberBoxWidget).showValidation(validation.getValue(column.id) as String?)
                        WidgetTypeJS.INTEGER_NUMBER_BOX -> (comp as IntegerNumberBoxWidget).showValidation(validation.getValue(column.id) as String?)
                        WidgetTypeJS.BOOLEAN_BOX -> {
                        }
                        WidgetTypeJS.ENTITY_SELECT_BOX -> (comp as EntitySelectWidget).showValidation(validation.getValue(column.id) as String?)
                        WidgetTypeJS.ENUM_SELECT_BOX -> (comp as EnumValueWidget<*>).showValidation(validation.getValue(column.id) as String?)
                        WidgetTypeJS.DATE_BOX -> (comp as DateBoxWidget).showValidation(validation.getValue(column.id) as String?)
                        WidgetTypeJS.DATE_TIME_BOX -> (comp as DateTimeBoxWidget).showValidation(validation.getValue(column.id) as String?)
                        WidgetTypeJS.TABLE_BOX -> throw XeptionJS.forDeveloper("unsupported type : TABLE_BOX")
                        WidgetTypeJS.GENERAL_SELECT_BOX -> (comp as GeneralSelectWidget).showValidation(validation.getValue(column.id) as String?)
                    }
                }
            }
        }
    }

}
class TableBoxWidgetConfiguration<VM:BaseTableBoxVMJS,VS:BaseTableBoxVSJS>{
    var width:String? = null
    var height:String? = null
    val columns = arrayListOf<TableBoxWidgetColumnDescription>()
    var showToolsColumn = true
    lateinit var vmFactory:()->VM
    var vsFactory:(()->VS)? = null
    fun column(id:String, widgetDescription:BaseWidgetDescriptionJS, title:String, width:Int? = null){
        val cell = TableBoxWidgetColumnDescription()
        cell.width = width
        cell.title = title
        cell.widgetDescription = widgetDescription
        cell.id = id
        columns.add(cell)
    }
}

internal data class TableBoxWidgetRowAdditionalData(var uid:String, val id:String)

class TableBoxWidgetColumnDescription{
    lateinit var widgetDescription: BaseWidgetDescriptionJS
    var width:Int? = null
    lateinit var title:String
    lateinit var id:String
}

class TableBoxWidgetToolsPanel( private val tableBox:TableBoxWidget<*,*,*>, private val rowId:String): BaseWebNodeWrapper<WebGridLayoutWidget>(){
    internal val upButton:WebLinkButton
    internal val downButton:WebLinkButton
    internal val plusButton:WebLinkButton
    internal val minusButton:WebLinkButton
    internal val rightDiv:WebTag
        init {
        upButton = WebUiLibraryAdapter.get().createLinkButton{
            icon = "core:up"
            specificProperties["type"] = "link"
        }
        upButton.setHandler {
            val idx = tableBox.rowsAdditionalData.indexOfFirst { rowId == it.id }
            val item = tableBox.rowsAdditionalData.removeAt(idx)
            tableBox.rowsAdditionalData.add(idx-1, item)
            tableBox.getNode().moveRow(idx, idx-1)
            tableBox.updateToolsVisibility()
        }
        downButton = WebUiLibraryAdapter.get().createLinkButton{
            icon = "core:down"
            specificProperties["type"] = "link"
        }
        downButton.setHandler {
            val idx = tableBox.rowsAdditionalData.indexOfFirst { rowId == it.id }
            val item = tableBox.rowsAdditionalData.removeAt(idx)
            tableBox.rowsAdditionalData.add(idx+1, item)
            tableBox.getNode().moveRow(idx, idx+1)
            tableBox.updateToolsVisibility()
        }
        plusButton = WebUiLibraryAdapter.get().createLinkButton{
            icon = "core:plus"
            specificProperties["type"] = "link"
        }
        plusButton.setHandler {
            val idx = tableBox.rowsAdditionalData.indexOfFirst { rowId == it.id }
            tableBox.addRow(idx+1)
        }
        minusButton = WebUiLibraryAdapter.get().createLinkButton{
            icon = "core:minus"
            specificProperties["type"] = "link"
        }
        minusButton.setHandler {
            val idx = tableBox.rowsAdditionalData.indexOfFirst { rowId == it.id }
            tableBox.rowsAdditionalData.removeAt(idx)
            tableBox.getNode().removeRow(idx)
            tableBox.updateToolsVisibility()
        }
        rightDiv = WebUiLibraryAdapter.get().createTag("div",MiscUtilsJS.createUUID())
            rightDiv.getStyle().setParameters("width" to "100%", "height" to "100%")
        rightDiv.getClass().addClasses("jasmine-table-right-div")

        _node = WebGridLayoutWidget {
            noPadding = true
        }.also {
            it.setColumnsWidths("auto","auto","auto","auto","1fr")
            it.addRow(upButton,downButton,plusButton,minusButton, rightDiv)
        }
    }
}




