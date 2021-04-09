/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.widgets

import com.gridnine.jasmine.server.core.model.common.BaseIdentity
import com.gridnine.jasmine.server.core.model.common.FakeEnum
import com.gridnine.jasmine.server.core.model.common.SelectItem
import com.gridnine.jasmine.server.core.model.common.Xeption
import com.gridnine.jasmine.server.core.model.domain.ObjectReference
import com.gridnine.jasmine.server.core.model.ui.*
import com.gridnine.jasmine.server.core.reflection.ReflectionFactory
import com.gridnine.jasmine.web.server.components.*
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

open class ServerUiTableWidget<VM:BaseTableBoxVM,VS:BaseTableBoxVS, VV:BaseTableBoxVV>(configure:ServerUiTableWidgetConfiguration<VM>.()->Unit):BaseServerUiNodeWrapper<ServerUiTable>(){
    private val config = ServerUiTableWidgetConfiguration<VM>()
    lateinit var vsFactory:()->VS?
    internal val rowsAdditionalData = arrayListOf<ServerUiTableWidgetRowAdditionalData>()
    private var readonly = false
    init {
        config.configure()
        _node = ServerUiLibraryAdapter.get().createTableBox(ServerUiTableConfiguration{
            width = config.width
            height = config.height
            config.columns.forEach { cc ->
                columns.add(ServerUiTableColumnDescription(cc.title, null, cc.width,null))
            }
            columns.add(ServerUiTableColumnDescription("", 135, 135,135))
        })
    }
    internal fun addEmptyRow(){
        val components = arrayListOf<ServerUiTableCell>()
        components.add(ServerUiTableCell(null,  config.columns.size))
        components.add(ServerUiTableCell(ServerUiTableWidgetEmptyRowToolsPanel(this)))
        _node.addRow(null, components)
    }
    internal fun addRow(idx:Int){
        if(rowsAdditionalData.isEmpty()){
            _node.removeRow(0)
        }
        val uuid = UUID.randomUUID().toString()
        val rowId = UUID.randomUUID().toString()
        val vm = config.vmFactory.invoke()
        vm.uid = uuid
        val vs = vsFactory.invoke()
        vs?.let { it.uid = uuid }
        val components = arrayListOf<ServerUiTableCell>()
        val configuration = vsFactory.invoke()
        config.columns.withIndex().forEach {(collIdx, coll) ->
            val comp = createWebComponent(coll.widgetDescription)
            configure(comp, collIdx, configuration)
            components.add(ServerUiTableCell(comp))
        }
        components.add(ServerUiTableCell(ServerUiTableWidgetToolsPanel(this, rowId)))
        _node.addRow(idx, components)
        rowsAdditionalData.add(idx, ServerUiTableWidgetRowAdditionalData(uuid, rowId))
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
                        WidgetType.TEXT_BOX -> vm.setValue(column.id, (comp as ServerUiTextBoxWidget).getValue())
                        WidgetType.PASSWORD_BOX -> vm.setValue(column.id, (comp as ServerUiPasswordBoxWidget).getValue())
                        WidgetType.FLOAT_NUMBER_BOX -> vm.setValue(column.id, (comp as ServerUiBigDecimalBoxWidget).getValue())
                        WidgetType.INTEGER_NUMBER_BOX -> vm.setValue(column.id, (comp as ServerUiIntBoxWidget).getValue())
                        WidgetType.BOOLEAN_BOX -> vm.setValue(column.id, (comp as ServerUiBooleanBoxWidget).getValue())
                        WidgetType.ENTITY_SELECT_BOX -> vm.setValue(column.id, (comp as ServerUiEntityValueWidget<*>).getValue())
                        WidgetType.ENUM_SELECT_BOX -> vm.setValue(column.id, (comp as ServerUiEnumValueWidget<*>).getValue())
                        WidgetType.DATE_BOX -> vm.setValue(column.id, (comp as ServerUiDateBoxWidget).getValue())
                        WidgetType.DATE_TIME_BOX -> vm.setValue(column.id, (comp as ServerUiDateTimeBoxWidget).getValue())
                        WidgetType.TABLE_BOX -> throw Xeption.forDeveloper("unsupported type : TABLE_BOX")
                        WidgetType.GENERAL_SELECT_BOX -> vm.setValue(column.id, (comp as ServerUiGeneralSelectValueWidget).getValue())
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
                val size = components.size
                components.withIndex().forEach { (compIdx, comp) ->
                    if(compIdx < size -1) {
                        setValue(comp!!, compIdx, value)
                        configure(comp!!, compIdx, vs?.get(idx))
                    }
                }
            } else {
                val components = arrayListOf<ServerUiTableCell>()
                config.columns.withIndex().forEach {(collIdx, coll) ->
                    val comp = createWebComponent(coll.widgetDescription)
                    setValue(comp, collIdx, value)
                    configure(comp!!, collIdx, vs?.get(idx))
                    components.add(ServerUiTableCell(comp))
                }
                val rowId = UUID.randomUUID().toString()
                components.add(ServerUiTableCell(ServerUiTableWidgetToolsPanel(this, rowId)))
                _node.addRow(null, components)
                rowsAdditionalData.add(ServerUiTableWidgetRowAdditionalData(value.uid, rowId))
            }
        }
        updateToolsVisibility()
    }

    internal fun updateToolsVisibility(){
        val rows = _node.getRows()
        val size = rows.size
        rows.withIndex().forEach { (idx, row) ->
            val comp = row.last()
            if(comp is ServerUiTableWidgetToolsPanel){
                comp.downButton.setEnabled(!readonly && idx<size-1)
                comp.upButton.setEnabled(!readonly && idx>0)
                comp.plusButton.setEnabled(!readonly)
                comp.minusButton.setEnabled(!readonly)
            }
            if(comp is ServerUiTableWidgetEmptyRowToolsPanel){
                comp.getNode().setEnabled(!readonly)
            }
        }
    }

    private fun createWebComponent(widgetDescription: BaseWidgetDescription): ServerUiNode {
        return when(widgetDescription.widgetType){
            WidgetType.TEXT_BOX -> ServerUiTextBoxWidget{
                width = "100%"
            }
            WidgetType.PASSWORD_BOX ->  ServerUiPasswordBoxWidget{
                width = "100%"
            }
            WidgetType.FLOAT_NUMBER_BOX ->  ServerUiBigDecimalBoxWidget{
                width = "100%"
            }
            WidgetType.INTEGER_NUMBER_BOX ->  ServerUiIntBoxWidget{
                widgetDescription as IntegerNumberBoxWidgetDescription
                width = "100%"
                nullable = !widgetDescription.nonNullable
            }
            WidgetType.BOOLEAN_BOX  ->  ServerUiBooleanBoxWidget{}
            WidgetType.ENTITY_SELECT_BOX ->  ServerUiEntityValueWidget<BaseIdentity>{
                widgetDescription as EntitySelectBoxWidgetDescription
                width = "100%"
                handler = ServerUiAutocompleteHandler.createMetadataBasedAutocompleteHandler(widgetDescription.objectId!!)
                showClearIcon = true
            }
            WidgetType.ENUM_SELECT_BOX ->  ServerUiEnumValueWidget<FakeEnum>{
                widgetDescription as EnumSelectBoxWidgetDescription
                width = "100%"
                enumClass = ReflectionFactory.get().getClass(widgetDescription.enumId)
            }
            WidgetType.DATE_BOX ->  ServerUiDateBoxWidget{
                width = "100%"
            }
            WidgetType.DATE_TIME_BOX ->  ServerUiDateTimeBoxWidget{
                width = "100%"
            }
            WidgetType.GENERAL_SELECT_BOX ->  ServerUiGeneralSelectValueWidget{
                width = "100%"
            }
            WidgetType.TABLE_BOX -> throw Xeption.forDeveloper("unsupported type : TABLE_BOX")
            WidgetType.HIDDEN -> throw Xeption.forDeveloper("unsupported type : HIDDEN")
        }
    }

    private fun setValue(comp: ServerUiNode, compIdx: Int, value: VM) {
        val column = config.columns[compIdx]
        when(column.widgetDescription.widgetType){
            WidgetType.TEXT_BOX -> (comp as ServerUiTextBoxWidget).setValue(value.getValue(column.id) as String?)
            WidgetType.PASSWORD_BOX -> (comp as ServerUiPasswordBoxWidget).setValue(value.getValue(column.id) as String?)
            WidgetType.FLOAT_NUMBER_BOX -> (comp as ServerUiBigDecimalBoxWidget).setValue(value.getValue(column.id) as BigDecimal?)
            WidgetType.INTEGER_NUMBER_BOX -> (comp as ServerUiIntBoxWidget).setValue(value.getValue(column.id) as Int?)
            WidgetType.BOOLEAN_BOX -> (comp as ServerUiBooleanBoxWidget).setValue(value.getValue(column.id) as Boolean)
            WidgetType.ENTITY_SELECT_BOX -> (comp as ServerUiEntityValueWidget<BaseIdentity>).setValue(value.getValue(column.id) as ObjectReference<BaseIdentity>?)
            WidgetType.ENUM_SELECT_BOX -> (comp as ServerUiEnumValueWidget<*>).setUncastedValue(value.getValue(column.id))
            WidgetType.DATE_BOX -> (comp as ServerUiDateBoxWidget).setValue(value.getValue(column.id) as LocalDate?)
            WidgetType.DATE_TIME_BOX  -> (comp as ServerUiDateTimeBoxWidget).setValue(value.getValue(column.id) as LocalDateTime?)
            WidgetType.TABLE_BOX -> throw Xeption.forDeveloper("unsupported type : TABLE_BOX")
            WidgetType.GENERAL_SELECT_BOX -> (comp as ServerUiGeneralSelectValueWidget).setValue(value.getValue(column.id) as SelectItem?)
        }
    }

    private fun configure(comp: ServerUiNode, compIdx: Int, value: VS?) {
        val column = config.columns[compIdx]
        val vsValue = value?.getValue(column.id) ?: return
        when(column.widgetDescription.widgetType){
            WidgetType.TEXT_BOX -> (comp as ServerUiTextBoxWidget).configure(vsValue as TextBoxConfiguration)
            WidgetType.PASSWORD_BOX -> (comp as ServerUiPasswordBoxWidget).configure(vsValue as PasswordBoxConfiguration)
            WidgetType.FLOAT_NUMBER_BOX -> (comp as ServerUiBigDecimalBoxWidget).configure(vsValue as FloatNumberBoxConfiguration)
            WidgetType.INTEGER_NUMBER_BOX -> (comp as ServerUiIntBoxWidget).configure(vsValue as IntegerNumberBoxConfiguration)
            WidgetType.BOOLEAN_BOX -> (comp as ServerUiBooleanBoxWidget).configure(vsValue as BooleanBoxConfiguration)
            WidgetType.ENTITY_SELECT_BOX -> (comp as ServerUiEntityValueWidget<*>).configure(vsValue as EntitySelectBoxConfiguration)
            WidgetType.ENUM_SELECT_BOX -> (comp as ServerUiEnumValueWidget<*>).configure(vsValue as EnumSelectBoxConfiguration)
            WidgetType.DATE_BOX ->(comp as ServerUiDateBoxWidget).configure(vsValue as DateBoxConfiguration)
            WidgetType.DATE_TIME_BOX  -> (comp as ServerUiDateTimeBoxWidget).configure(vsValue as DateTimeBoxConfiguration)
            WidgetType.TABLE_BOX -> throw Xeption.forDeveloper("unsupported type : TABLE_BOX")
            WidgetType.GENERAL_SELECT_BOX -> (comp as ServerUiGeneralSelectValueWidget).configure(vsValue as GeneralSelectBoxConfiguration)
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
                            WidgetType.TEXT_BOX -> (comp as ServerUiTextBoxWidget).setReadonly(value)
                            WidgetType.PASSWORD_BOX -> (comp as ServerUiPasswordBoxWidget).setReadonly(value)
                            WidgetType.FLOAT_NUMBER_BOX -> (comp as ServerUiBigDecimalBoxWidget).setReadonly(value)
                            WidgetType.INTEGER_NUMBER_BOX -> (comp as ServerUiIntBoxWidget).setReadonly(value)
                            WidgetType.BOOLEAN_BOX -> (comp as ServerUiBooleanBoxWidget).setReadonly(value)
                            WidgetType.ENTITY_SELECT_BOX -> (comp as ServerUiEntityValueWidget<*>).setReadonly(value)
                            WidgetType.ENUM_SELECT_BOX -> (comp as ServerUiEnumValueWidget<*>).setReadonly(value)
                            WidgetType.DATE_BOX -> (comp as ServerUiDateBoxWidget).setReadonly(value)
                            WidgetType.DATE_TIME_BOX -> (comp as ServerUiDateTimeBoxWidget).setReadonly(value)
                            WidgetType.TABLE_BOX -> throw Xeption.forDeveloper("unsupported type : TABLE_BOX")
                            WidgetType.GENERAL_SELECT_BOX -> (comp as ServerUiGeneralSelectValueWidget).setReadonly(value)
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
                        WidgetType.TEXT_BOX -> (comp as ServerUiTextBoxWidget).showValidation(value as String?)
                        WidgetType.PASSWORD_BOX -> (comp as ServerUiPasswordBoxWidget).showValidation(value as String?)
                        WidgetType.FLOAT_NUMBER_BOX -> (comp as ServerUiBigDecimalBoxWidget).showValidation(value as String?)
                        WidgetType.INTEGER_NUMBER_BOX -> (comp as ServerUiIntBoxWidget).showValidation(value as String?)
                        WidgetType.BOOLEAN_BOX -> {
                        }
                        WidgetType.ENTITY_SELECT_BOX -> (comp as ServerUiEntityValueWidget<*>).showValidation(value as String?)
                        WidgetType.ENUM_SELECT_BOX -> (comp as ServerUiEnumValueWidget<*>).showValidation(value as String?)
                        WidgetType.DATE_BOX -> (comp as ServerUiDateBoxWidget).showValidation(value as String?)
                        WidgetType.DATE_TIME_BOX -> (comp as ServerUiDateTimeBoxWidget).showValidation(value as String?)
                        WidgetType.TABLE_BOX -> throw Xeption.forDeveloper("unsupported type : TABLE_BOX")
                        WidgetType.GENERAL_SELECT_BOX -> (comp as ServerUiGeneralSelectValueWidget).showValidation(value as String?)
                    }
                }
            }
        }
    }

}
class ServerUiTableWidgetConfiguration<VM:BaseTableBoxVM>(){
    var width:String? = null
    var height:String? = null
    val columns = arrayListOf<ServerUiTableWidgetColumnDescription>()
    var showToolsColumn = true
    lateinit var vmFactory:()->VM
    fun column(id:String, widgetDescription:BaseWidgetDescription, title:String, width:Int? = null){
        val cell = ServerUiTableWidgetColumnDescription()
        cell.width = width
        cell.title = title
        cell.widgetDescription = widgetDescription
        cell.id = id
        columns.add(cell)
    }
}

internal data class ServerUiTableWidgetRowAdditionalData(var uid:String, val id:String)

class ServerUiTableWidgetColumnDescription{
    lateinit var widgetDescription: BaseWidgetDescription
    var width:Int? = null
    lateinit var title:String
    lateinit var id:String
}
internal  class ServerUiTableWidgetEmptyRowToolsPanel(private val tableWidget:ServerUiTableWidget<*,*,*>): BaseServerUiNodeWrapper<ServerUiLinkButton>(){
    init {
        _node = ServerUiLibraryAdapter.get().createLinkButton(ServerUiLinkButtonConfiguration{
            iconClass = "z-icon-plus"
        })
        _node.setHandler {
            tableWidget.addRow(0)
        }
    }
}

internal  class ServerUiTableWidgetToolsPanel(private val tableWidget:ServerUiTableWidget<*,*,*>, private val rowId:String): BaseServerUiNodeWrapper<ServerUiGridLayoutContainer>(){
    internal val upButton:ServerUiLinkButton
    internal val downButton:ServerUiLinkButton
    internal val plusButton:ServerUiLinkButton
    internal val minusButton:ServerUiLinkButton
    init {
        _node = ServerUiLibraryAdapter.get().createGridLayoutContainer(ServerUiGridLayoutContainerConfiguration{
            noPadding = true
            columns.add(ServerUiGridLayoutColumnConfiguration())
            columns.add(ServerUiGridLayoutColumnConfiguration())
            columns.add(ServerUiGridLayoutColumnConfiguration())
            columns.add(ServerUiGridLayoutColumnConfiguration())
            columns.add(ServerUiGridLayoutColumnConfiguration("100%"))
        })
        _node.addRow()
        upButton = ServerUiLibraryAdapter.get().createLinkButton(ServerUiLinkButtonConfiguration{
           iconClass ="z-icon-sort-up"
        })
        upButton.setHandler {
            val idx = tableWidget.rowsAdditionalData.indexOfFirst { rowId == it.id }!!
            val item = tableWidget.rowsAdditionalData.removeAt(idx)
            tableWidget.rowsAdditionalData.add(idx-1, item)
            tableWidget.getNode().moveRow(idx, idx-1)
            tableWidget.updateToolsVisibility()
        }
        _node.addCell(ServerUiGridLayoutCell(upButton))

        downButton =  ServerUiLibraryAdapter.get().createLinkButton(ServerUiLinkButtonConfiguration{
            iconClass ="z-icon-sort-down"
        })
        downButton.setHandler {
            val idx = tableWidget.rowsAdditionalData.indexOfFirst { rowId == it.id }!!
            val item = tableWidget.rowsAdditionalData.removeAt(idx)
            tableWidget.rowsAdditionalData.add(idx+1, item)
            tableWidget.getNode().moveRow(idx, idx+1)
            tableWidget.updateToolsVisibility()
        }
        _node.addCell(ServerUiGridLayoutCell(downButton))
        plusButton =ServerUiLibraryAdapter.get().createLinkButton(ServerUiLinkButtonConfiguration{
            iconClass ="z-icon-plus"
        })
        plusButton.setHandler {
            val idx = tableWidget.rowsAdditionalData.indexOfFirst { rowId == it.id }!!
            tableWidget.addRow(idx+1)
        }
        _node.addCell(ServerUiGridLayoutCell(plusButton))
        minusButton = ServerUiLibraryAdapter.get().createLinkButton(ServerUiLinkButtonConfiguration{
            iconClass ="z-icon-minus"
        })
        minusButton.setHandler {
            val idx = tableWidget.rowsAdditionalData.indexOfFirst { rowId == it.id }!!
            tableWidget.rowsAdditionalData.removeAt(idx)
            tableWidget.getNode().removeRow(idx)
            if(tableWidget.rowsAdditionalData.isEmpty()){
                tableWidget.addEmptyRow()
            }
            tableWidget.updateToolsVisibility()
        }
        _node.addCell(ServerUiGridLayoutCell(minusButton))
    }
}




