/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("UNCHECKED_CAST")

package com.gridnine.jasmine.server.core.ui.widgets

import com.gridnine.jasmine.common.core.meta.*
import com.gridnine.jasmine.common.core.model.*
import com.gridnine.jasmine.common.core.reflection.ReflectionFactory
import com.gridnine.jasmine.common.core.utils.TextUtils
import com.gridnine.jasmine.server.core.ui.common.BaseNodeWrapper
import com.gridnine.jasmine.server.core.ui.common.BaseWidgetConfiguration
import com.gridnine.jasmine.server.core.ui.common.UiNode
import com.gridnine.jasmine.server.core.ui.components.*
import com.gridnine.jasmine.server.core.ui.components.TableColumnDescription
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

open class TableWidget<VM:BaseTableBoxVM,VS:BaseTableBoxVS, VV:BaseTableBoxVV>(configure: TableWidgetConfiguration<VM>.()->Unit):BaseNodeWrapper<Table>(){
    private val config = TableWidgetConfiguration<VM>()
    lateinit var vsFactory:()->VS?
    internal val rowsAdditionalData = arrayListOf<TableWidgetRowAdditionalData>()
    private var readonly = false
    init {
        config.configure()
        _node = UiLibraryAdapter.get().createTable{
            width = config.width
            height = config.height
            config.columns.forEach { cc ->
                columns.add(TableColumnDescription(cc.title, null, cc.width,null))
            }
            columns.add(TableColumnDescription("", 135, 135,135))
        }
    }
    internal fun addEmptyRow(){
        val components = arrayListOf<TableCell>()
        components.add(TableCell(null,  config.columns.size))
        components.add(TableCell(TableWidgetEmptyRowToolsPanel(this)))
        _node.addRow(null, components)
    }
    internal fun addRow(idx:Int){
        if(rowsAdditionalData.isEmpty()){
            _node.removeRow(0)
        }
        val uuid = TextUtils.generateUid()
        val rowId = TextUtils.generateUid()
        val vm = config.vmFactory.invoke()
        vm.uid = uuid
        val vs = vsFactory.invoke()
        vs?.let { it.uid = uuid }
        val components = arrayListOf<TableCell>()
        val configuration = vsFactory.invoke()
        config.columns.withIndex().forEach {(collIdx, coll) ->
            val comp = createWebComponent(coll.widgetDescription)
            configure(comp, collIdx, configuration)
            components.add(TableCell(comp))
        }
        components.add(TableCell(TableWidgetToolsPanel(this, rowId)))
        _node.addRow(idx, components)
        rowsAdditionalData.add(idx, TableWidgetRowAdditionalData(uuid, rowId))
        updateToolsVisibility()
    }

    fun getData(): List<VM> {
        if(rowsAdditionalData.isEmpty()){
            return emptyList()
        }
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
                        WidgetType.TEXT_BOX -> vm.setValue(column.id, (comp as TextBoxWidget).getValue())
                        WidgetType.PASSWORD_BOX -> vm.setValue(column.id, (comp as PasswordBoxWidget).getValue())
                        WidgetType.BIG_DECIMAL_NUMBER_BOX -> vm.setValue(column.id, (comp as BigDecimalBoxWidget).getValue())
                        WidgetType.INTEGER_NUMBER_BOX -> vm.setValue(column.id, (comp as IntBoxWidget).getValue())
                        WidgetType.BOOLEAN_BOX -> vm.setValue(column.id, (comp as BooleanBoxWidget).getValue())
                        WidgetType.ENTITY_SELECT_BOX -> vm.setValue(column.id, (comp as EntityValueWidget<*>).getValue())
                        WidgetType.ENUM_SELECT_BOX -> vm.setValue(column.id, (comp as EnumBoxValueWidget<*>).getValue())
                        WidgetType.DATE_BOX -> vm.setValue(column.id, (comp as DateBoxWidget).getValue())
                        WidgetType.DATE_TIME_BOX -> vm.setValue(column.id, (comp as DateTimeBoxWidget).getValue())
                        WidgetType.TABLE_BOX -> throw Xeption.forDeveloper("unsupported type : TABLE_BOX")
                        WidgetType.GENERAL_SELECT_BOX -> vm.setValue(column.id, (comp as GeneralSelectBoxValueWidget).getValue())
                        WidgetType.HIDDEN -> throw Xeption.forDeveloper("unsupported column type: ${column.widgetDescription.widgetType}")
                    }
                }
            }
            result.add(vm)
        }
        return result
    }

    fun setData(vm: List<VM>, vs: List<VS>?) {
        val size = vm.size
        val additionalDataSize = rowsAdditionalData.size
        if(additionalDataSize > 0) {
            for (n in additionalDataSize - 1 downTo size) {
                rowsAdditionalData.removeAt(if (size > 0) size - 1 else 0)
                _node.removeRow(if (size > 0) size - 1 else 0)
            }
        } else if (_node.getRows().isNotEmpty()){
            _node.removeRow(0)
        }
        if(size==0){
            addEmptyRow()
            updateToolsVisibility()
            return
        }
        val existingRows = _node.getRows()
        vm.withIndex().forEach{(idx, value) ->
            if(rowsAdditionalData.size> idx){
                rowsAdditionalData[idx].uid = value.uid
                val components = existingRows[idx]
                val componentsSize = components.size
                components.withIndex().forEach { (compIdx, comp) ->
                    if(compIdx < componentsSize -1) {
                        setValue(comp!!, compIdx, value)
                        configure(comp, compIdx, vs?.get(idx))
                    }
                }
            } else {
                val components = arrayListOf<TableCell>()
                config.columns.withIndex().forEach {(collIdx, coll) ->
                    val comp = createWebComponent(coll.widgetDescription)
                    setValue(comp, collIdx, value)
                    configure(comp, collIdx, vs?.get(idx))
                    components.add(TableCell(comp))
                }
                val rowId = UUID.randomUUID().toString()
                components.add(TableCell(TableWidgetToolsPanel(this, rowId)))
                _node.addRow(null, components)
                rowsAdditionalData.add(TableWidgetRowAdditionalData(value.uid, rowId))
            }
        }
        updateToolsVisibility()
    }

    internal fun updateToolsVisibility(){
        val rows = _node.getRows()
        val size = rows.size
        rows.withIndex().forEach { (idx, row) ->
            val comp = row.last()
            if(comp is TableWidgetToolsPanel){
                comp.downButton.setEnabled(!readonly && idx<size-1)
                comp.upButton.setEnabled(!readonly && idx>0)
                comp.plusButton.setEnabled(!readonly)
                comp.minusButton.setEnabled(!readonly)
            }
            if(comp is TableWidgetEmptyRowToolsPanel){
                comp.getNode().setEnabled(!readonly)
            }
        }
    }

    private fun createWebComponent(widgetDescription: BaseWidgetDescription): UiNode {
        return when(widgetDescription.widgetType){
            WidgetType.TEXT_BOX -> TextBoxWidget{
                width = "100%"
            }
            WidgetType.PASSWORD_BOX ->  PasswordBoxWidget{
                width = "100%"
            }
            WidgetType.BIG_DECIMAL_NUMBER_BOX ->  BigDecimalBoxWidget{
                width = "100%"
            }
            WidgetType.INTEGER_NUMBER_BOX ->  IntBoxWidget{
                widgetDescription as IntegerNumberBoxWidgetDescription
                width = "100%"
                nullable = !widgetDescription.nonNullable
            }
            WidgetType.BOOLEAN_BOX  ->  BooleanBoxWidget{}
            WidgetType.ENTITY_SELECT_BOX ->  EntityValueWidget<BaseIdentity>{
                widgetDescription as EntitySelectBoxWidgetDescription
                width = "100%"
                handler = AutocompleteHandler.createMetadataBasedAutocompleteHandler(widgetDescription.objectId)
                showClearIcon = true
            }
            WidgetType.ENUM_SELECT_BOX ->  EnumBoxValueWidget<FakeEnum>{
                widgetDescription as EnumSelectBoxWidgetDescription
                width = "100%"
                enumClass = ReflectionFactory.get().getClass(widgetDescription.enumId)
            }
            WidgetType.DATE_BOX ->  DateBoxWidget{
                width = "100%"
            }
            WidgetType.DATE_TIME_BOX ->  DateTimeBoxWidget{
                width = "100%"
            }
            WidgetType.GENERAL_SELECT_BOX ->  GeneralSelectBoxValueWidget{
                width = "100%"
            }
            WidgetType.TABLE_BOX -> throw Xeption.forDeveloper("unsupported type : TABLE_BOX")
            WidgetType.HIDDEN -> throw Xeption.forDeveloper("unsupported type : HIDDEN")
        }
    }

    private fun setValue(comp: UiNode, compIdx: Int, value: VM) {
        val column = config.columns[compIdx]
        when(column.widgetDescription.widgetType){
            WidgetType.TEXT_BOX -> (comp as TextBoxWidget).setValue(value.getValue(column.id) as String?)
            WidgetType.PASSWORD_BOX -> (comp as PasswordBoxWidget).setValue(value.getValue(column.id) as String?)
            WidgetType.BIG_DECIMAL_NUMBER_BOX -> (comp as BigDecimalBoxWidget).setValue(value.getValue(column.id) as BigDecimal?)
            WidgetType.INTEGER_NUMBER_BOX -> (comp as IntBoxWidget).setValue(value.getValue(column.id) as Int?)
            WidgetType.BOOLEAN_BOX -> (comp as BooleanBoxWidget).setValue(value.getValue(column.id) as Boolean)
            WidgetType.ENTITY_SELECT_BOX -> (comp as EntityValueWidget<BaseIdentity>).setValue(value.getValue(column.id) as ObjectReference<BaseIdentity>?)
            WidgetType.ENUM_SELECT_BOX -> (comp as EnumBoxValueWidget<*>).setUncastedValue(value.getValue(column.id))
            WidgetType.DATE_BOX -> (comp as DateBoxWidget).setValue(value.getValue(column.id) as LocalDate?)
            WidgetType.DATE_TIME_BOX  -> (comp as DateTimeBoxWidget).setValue(value.getValue(column.id) as LocalDateTime?)
            WidgetType.TABLE_BOX -> throw Xeption.forDeveloper("unsupported type : TABLE_BOX")
            WidgetType.GENERAL_SELECT_BOX -> (comp as GeneralSelectBoxValueWidget).setValue(value.getValue(column.id) as SelectItem?)
            WidgetType.HIDDEN -> throw Xeption.forDeveloper("unsupported type : HIDDEN")
        }
    }

    private fun configure(comp: UiNode, compIdx: Int, value: VS?) {
        val column = config.columns[compIdx]
        val vsValue = value?.getValue(column.id) ?: return
        when(column.widgetDescription.widgetType){
            WidgetType.TEXT_BOX -> (comp as TextBoxWidget).configure(vsValue as TextBoxConfiguration)
            WidgetType.PASSWORD_BOX -> (comp as PasswordBoxWidget).configure(vsValue as PasswordBoxConfiguration)
            WidgetType.BIG_DECIMAL_NUMBER_BOX -> (comp as BigDecimalBoxWidget).configure(vsValue as BigDecimalBoxConfiguration)
            WidgetType.INTEGER_NUMBER_BOX -> (comp as IntBoxWidget).configure(vsValue as IntegerNumberBoxConfiguration)
            WidgetType.BOOLEAN_BOX -> (comp as BooleanBoxWidget).configure(vsValue as BooleanBoxConfiguration)
            WidgetType.ENTITY_SELECT_BOX -> (comp as EntityValueWidget<*>).configure(vsValue as EntitySelectBoxConfiguration)
            WidgetType.ENUM_SELECT_BOX -> (comp as EnumBoxValueWidget<*>).configure(vsValue as EnumSelectBoxConfiguration)
            WidgetType.DATE_BOX ->(comp as DateBoxWidget).configure(vsValue as DateBoxConfiguration)
            WidgetType.DATE_TIME_BOX  -> (comp as DateTimeBoxWidget).configure(vsValue as DateTimeBoxConfiguration)
            WidgetType.TABLE_BOX -> throw Xeption.forDeveloper("unsupported type : TABLE_BOX")
            WidgetType.GENERAL_SELECT_BOX -> (comp as GeneralSelectBoxValueWidget).configure(vsValue as GeneralSelectBoxConfiguration)
            WidgetType.HIDDEN -> throw Xeption.forDeveloper("unsupported type : HIDDEN")
        }
    }

    fun setReadonly(value: Boolean) {
        readonly = value
        if(rowsAdditionalData.isNotEmpty()) {
            val rows = _node.getRows()
            rows.forEach { row ->
                val size = row.size
                row.withIndex().forEach { (idx, comp) ->
                    if (idx < size - 1) {
                        when (config.columns[idx].widgetDescription.widgetType) {
                            WidgetType.TEXT_BOX -> (comp as TextBoxWidget).setReadonly(value)
                            WidgetType.PASSWORD_BOX -> (comp as PasswordBoxWidget).setReadonly(value)
                            WidgetType.BIG_DECIMAL_NUMBER_BOX -> (comp as BigDecimalBoxWidget).setReadonly(value)
                            WidgetType.INTEGER_NUMBER_BOX -> (comp as IntBoxWidget).setReadonly(value)
                            WidgetType.BOOLEAN_BOX -> (comp as BooleanBoxWidget).setReadonly(value)
                            WidgetType.ENTITY_SELECT_BOX -> (comp as EntityValueWidget<*>).setReadonly(value)
                            WidgetType.ENUM_SELECT_BOX -> (comp as EnumBoxValueWidget<*>).setReadonly(value)
                            WidgetType.DATE_BOX -> (comp as DateBoxWidget).setReadonly(value)
                            WidgetType.DATE_TIME_BOX -> (comp as DateTimeBoxWidget).setReadonly(value)
                            WidgetType.TABLE_BOX -> throw Xeption.forDeveloper("unsupported type : TABLE_BOX")
                            WidgetType.GENERAL_SELECT_BOX -> (comp as GeneralSelectBoxValueWidget).setReadonly(value)
                            WidgetType.HIDDEN -> throw Xeption.forDeveloper("unsupported type : HIDDEN")
                        }
                    }
                }
            }
        }
        updateToolsVisibility()
    }

    fun showValidation(vv: List<VV>?) {
        if(rowsAdditionalData.isEmpty()){
            return
        }
        val rows = _node.getRows()
        rows.withIndex().forEach { (rowIdx, row) ->
            val validation = vv?.get(rowIdx)
            val size = row.size
            row.withIndex().forEach { (colIdx, comp) ->
                if (colIdx < size - 1) {
                    val column = config.columns[colIdx]
                    val value = validation?.getValue(column.id)
                    when (column.widgetDescription.widgetType) {
                        WidgetType.TEXT_BOX -> (comp as TextBoxWidget).showValidation(value as String?)
                        WidgetType.PASSWORD_BOX -> (comp as PasswordBoxWidget).showValidation(value as String?)
                        WidgetType.BIG_DECIMAL_NUMBER_BOX -> (comp as BigDecimalBoxWidget).showValidation(value as String?)
                        WidgetType.INTEGER_NUMBER_BOX -> (comp as IntBoxWidget).showValidation(value as String?)
                        WidgetType.BOOLEAN_BOX -> {
                        }
                        WidgetType.ENTITY_SELECT_BOX -> (comp as EntityValueWidget<*>).showValidation(value as String?)
                        WidgetType.ENUM_SELECT_BOX -> (comp as EnumBoxValueWidget<*>).showValidation(value as String?)
                        WidgetType.DATE_BOX -> (comp as DateBoxWidget).showValidation(value as String?)
                        WidgetType.DATE_TIME_BOX -> (comp as DateTimeBoxWidget).showValidation(value as String?)
                        WidgetType.TABLE_BOX -> throw Xeption.forDeveloper("unsupported type : TABLE_BOX")
                        WidgetType.GENERAL_SELECT_BOX -> (comp as GeneralSelectBoxValueWidget).showValidation(value as String?)
                        WidgetType.HIDDEN -> throw Xeption.forDeveloper("unsupported type : HIDDEN")
                    }
                }
            }
        }
    }

}
class TableWidgetConfiguration<VM:BaseTableBoxVM>:BaseWidgetConfiguration(){
    val columns = arrayListOf<TableWidgetColumnDescription>()
    var showToolsColumn = true
    lateinit var vmFactory:()->VM
    fun column(id:String, widgetDescription:BaseWidgetDescription, title:String, width:Int? = null){
        val cell = TableWidgetColumnDescription()
        cell.width = width
        cell.title = title
        cell.widgetDescription = widgetDescription
        cell.id = id
        columns.add(cell)
    }
}

internal data class TableWidgetRowAdditionalData(var uid:String, val id:String)

class TableWidgetColumnDescription{
    lateinit var widgetDescription: BaseWidgetDescription
    var width:Int? = null
    lateinit var title:String
    lateinit var id:String
}
internal  class TableWidgetEmptyRowToolsPanel(private val tableWidget: TableWidget<*, *, *>): BaseNodeWrapper<LinkButton>(){
    init {
        _node = UiLibraryAdapter.get().createLinkButton{
            iconClass = "z-icon-plus"
        }
        _node.setHandler {
            tableWidget.addRow(0)
        }
    }
}

internal  class TableWidgetToolsPanel(private val tableWidget: TableWidget<*, *, *>, private val rowId:String): BaseNodeWrapper<GridLayoutContainer>(){
    internal val upButton:LinkButton
    internal val downButton:LinkButton
    internal val plusButton:LinkButton
    internal val minusButton:LinkButton
    init {
        _node = UiLibraryAdapter.get().createGridLayoutContainer{
            noPadding = true
            columns.add(GridLayoutColumnConfiguration())
            columns.add(GridLayoutColumnConfiguration())
            columns.add(GridLayoutColumnConfiguration())
            columns.add(GridLayoutColumnConfiguration())
            columns.add(GridLayoutColumnConfiguration("100%"))
        }
        _node.addRow()
        upButton = UiLibraryAdapter.get().createLinkButton{
           iconClass ="z-icon-sort-up"
        }
        upButton.setHandler {
            val idx = tableWidget.rowsAdditionalData.indexOfFirst { rowId == it.id }
            val item = tableWidget.rowsAdditionalData.removeAt(idx)
            tableWidget.rowsAdditionalData.add(idx-1, item)
            tableWidget.getNode().moveRow(idx, idx-1)
            tableWidget.updateToolsVisibility()
        }
        _node.addCell(GridLayoutCell(upButton))

        downButton =  UiLibraryAdapter.get().createLinkButton{
            iconClass ="z-icon-sort-down"
        }
        downButton.setHandler {
            val idx = tableWidget.rowsAdditionalData.indexOfFirst { rowId == it.id }
            val item = tableWidget.rowsAdditionalData.removeAt(idx)
            tableWidget.rowsAdditionalData.add(idx+1, item)
            tableWidget.getNode().moveRow(idx, idx+1)
            tableWidget.updateToolsVisibility()
        }
        _node.addCell(GridLayoutCell(downButton))
        plusButton =UiLibraryAdapter.get().createLinkButton{
            iconClass ="z-icon-plus"
        }
        plusButton.setHandler {
            val idx = tableWidget.rowsAdditionalData.indexOfFirst { rowId == it.id }
            tableWidget.addRow(idx+1)
        }
        _node.addCell(GridLayoutCell(plusButton))
        minusButton = UiLibraryAdapter.get().createLinkButton{
            iconClass ="z-icon-minus"
        }
        minusButton.setHandler {
            val idx = tableWidget.rowsAdditionalData.indexOfFirst { rowId == it.id }
            tableWidget.rowsAdditionalData.removeAt(idx)
            tableWidget.getNode().removeRow(idx)
            if(tableWidget.rowsAdditionalData.isEmpty()){
                tableWidget.addEmptyRow()
            }
            tableWidget.updateToolsVisibility()
        }
        _node.addCell(GridLayoutCell(minusButton))
    }
}




