/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.widgets

import com.gridnine.jasmine.server.core.model.common.SelectItemJS
import com.gridnine.jasmine.server.core.model.common.XeptionJS
import com.gridnine.jasmine.server.core.model.domain.ObjectReferenceJS
import com.gridnine.jasmine.server.core.model.ui.*
import com.gridnine.jasmine.web.core.ui.*
import com.gridnine.jasmine.web.core.ui.components.*
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import kotlin.js.Date

class TableBoxWidget<VM:BaseTableBoxVMJS,VS:BaseTableBoxVSJS, VV:BaseTableBoxVVJS>(private val parent: WebComponent, configure:TableBoxWidgetConfiguration<VM,VS>.()->Unit):WebComponent{
    internal val delegate:WebTableBox
    internal val rowsAdditionalData = arrayListOf<TableBoxWidgetRowAdditionalData>()
    private val config = TableBoxWidgetConfiguration<VM,VS>()
    private lateinit var createButton:WebLinkButton
    private var readonly = false
    init {
        config.configure()
        delegate = UiLibraryAdapter.get().createTableBox(parent){
            width = config.width
            height = config.height
            config.columns.forEach {
                val label = UiLibraryAdapter.get().createLabel(this@TableBoxWidget)
                label.setWidth("100%")
                label.setText(it.title)
                headerComponents.add(label)
                columnWidths.add(WebTableBoxColumnWidth(null,it.width?:100, null))
            }
            createButton = UiLibraryAdapter.get().createLinkButton(this@TableBoxWidget){
                icon="core:plus"
            }
            createButton.setHandler {
                addRow(0)
            }
            headerComponents.add(createButton)
            columnWidths.add(WebTableBoxColumnWidth(130,130, 130))
        }
    }

    internal fun addRow(idx:Int){
        val uuid = MiscUtilsJS.createUUID()
        val rowId = MiscUtilsJS.createUUID()
        val vm = config.vmFactory.invoke()
        vm.uid = uuid
        val vs = config.vsFactory.invoke()
        vs.uid= uuid
        val components = arrayListOf<WebComponent?>()
        config.columns.withIndex().forEach {(collIdx, coll) ->
            val comp = createWebComponent(coll.widgetDescription)
            components.add(comp)
        }
        components.add(TableBoxWidgetToolsPanel(delegate, this@TableBoxWidget, rowId))
        delegate.addRow(idx, components)
        rowsAdditionalData.add(idx, TableBoxWidgetRowAdditionalData(uuid, rowId))
        updateToolsVisibility()
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

    fun getData(): List<VM> {
        val result = arrayListOf<VM>()
        val rows = delegate.getRows()
        rows.withIndex().forEach {(rowIdx, row) ->
            val vm = config.vmFactory.invoke()
            vm.uid = rowsAdditionalData[rowIdx].uid
            val size = row.size
            row.withIndex().forEach { (idx, comp) ->
                if (idx < size - 1) {
                    val column = config.columns[idx]
                    when (column.widgetDescription.widgetType) {
                        WidgetTypeJS.TEXT_BOX -> vm.setValue(column.id, (comp as TextBoxWidget).getValue())
                        WidgetTypeJS.PASSWORD_BOX -> vm.setValue(column.id, (comp as PasswordBoxWidget).getValue())
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

    fun readData(vm: List<VM>, vs: List<VS>) {
        for( n in rowsAdditionalData.size downTo vm.size){
            rowsAdditionalData.removeAt(vm.size-1)
            delegate.removeRow(vm.size-1)
        }
        val existingRows = delegate.getRows()
        vm.withIndex().forEach{(idx, value) ->
            if(rowsAdditionalData.size> idx){
                rowsAdditionalData[idx].uid = value.uid
                val components = existingRows[idx]
                val size = components.size
                components.withIndex().forEach { (compIdx, comp) ->
                    if(compIdx < size -1) {
                        setValue(comp!!, compIdx, value)
                        configure(comp!!, compIdx, vs[idx])
                    }
                }
            } else {
                val components = arrayListOf<WebComponent?>()
                config.columns.withIndex().forEach {(collIdx, coll) ->
                    val comp = createWebComponent(coll.widgetDescription)
                    setValue(comp, collIdx, value)
                    configure(comp!!, collIdx, vs[idx])
                    components.add(comp)
                }
                val rowId = MiscUtilsJS.createUUID()
                components.add(TableBoxWidgetToolsPanel(delegate, this@TableBoxWidget, rowId))
                delegate.addRow(null, components)
                rowsAdditionalData.add(TableBoxWidgetRowAdditionalData(value.uid, rowId))
            }
        }
        updateToolsVisibility()
    }

    internal fun updateToolsVisibility(){
        createButton?.let { it.setEnabled(!readonly) }
        val rows = delegate.getRows()
        val size = rows.size
        rows.withIndex().forEach { (idx, row) ->
            val comp = row.last()
            if(comp is TableBoxWidgetToolsPanel){
                comp.downButton.setEnabled(!readonly && idx<size-1)
                comp.upButton.setEnabled(!readonly && idx>0)
                comp.plusButton.setEnabled(!readonly)
                comp.minusButton.setEnabled(!readonly)
            }
        }
    }

    private fun createWebComponent(widgetDescription: BaseWidgetDescriptionJS): WebComponent {
        return when(widgetDescription.widgetType){
            WidgetTypeJS.TEXT_BOX -> TextBoxWidget(delegate) {
                width = "100%"
            }
            WidgetTypeJS.PASSWORD_BOX ->  PasswordBoxWidget(delegate) {
                width = "100%"
            }
            WidgetTypeJS.FLOAT_NUMBER_BOX ->  FloatNumberBoxWidget(delegate) {
                width = "100%"
            }
            WidgetTypeJS.INTEGER_NUMBER_BOX ->  IntegerNumberBoxWidget(delegate) {
                widgetDescription as IntegerNumberBoxWidgetDescriptionJS
                width = "100%"
                nullable = !widgetDescription.nonNullable
            }
            WidgetTypeJS.BOOLEAN_BOX  ->  BooleanBoxWidget(delegate) {
                width = "100%"
            }
            WidgetTypeJS.ENTITY_SELECT_BOX ->  EntitySelectWidget(delegate) {
                widgetDescription as EntitySelectBoxWidgetDescriptionJS
                width = "100%"
                handler = ClientRegistry.get().get(ObjectHandler.TYPE, widgetDescription.objectId)!!.getAutocompleteHandler()
                showClearIcon = true
            }
            WidgetTypeJS.ENUM_SELECT_BOX ->  EnumValueWidget<FakeEnumJS>(delegate) {
                widgetDescription as EnumSelectBoxWidgetDescriptionJS
                width = "100%"
                enumClassName = widgetDescription.enumId
            }
            WidgetTypeJS.DATE_BOX ->  DateBoxWidget(delegate) {
                width = "100%"
            }
            WidgetTypeJS.DATE_TIME_BOX ->  DateTimeBoxWidget(delegate) {
                width = "100%"
            }
            WidgetTypeJS.GENERAL_SELECT_BOX ->  GeneralSelectWidget(delegate) {
                width = "100%"
            }
            WidgetTypeJS.TABLE_BOX -> throw XeptionJS.forDeveloper("unsupported type : TABLE_BOX")
        }
    }

    private fun setValue(comp: WebComponent, compIdx: Int, value: VM) {
        val column = config.columns[compIdx]
        when(column.widgetDescription.widgetType){
            WidgetTypeJS.TEXT_BOX -> (comp as TextBoxWidget).setValue(value.getValue(column.id) as String?)
            WidgetTypeJS.PASSWORD_BOX -> (comp as PasswordBoxWidget).setValue(value.getValue(column.id) as String?)
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

    private fun configure(comp: WebComponent, compIdx: Int, value: VS) {
        val column = config.columns[compIdx]
        val vsValue = value.getValue(column.id) ?: return
        when(column.widgetDescription.widgetType){
            WidgetTypeJS.TEXT_BOX -> (comp as TextBoxWidget).configure(vsValue as TextBoxConfigurationJS)
            WidgetTypeJS.PASSWORD_BOX -> (comp as PasswordBoxWidget).configure(vsValue as PasswordBoxConfigurationJS)
            WidgetTypeJS.FLOAT_NUMBER_BOX -> (comp as FloatNumberBoxWidget).configure(vsValue as FloatNumberBoxConfigurationJS)
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
        val rows = delegate.getRows()
        rows.forEach { row ->
            val size = row.size
            row.withIndex().forEach {(idx, comp) ->
                if(idx < size -1) {
                    when (config.columns[idx].widgetDescription.widgetType) {
                        WidgetTypeJS.TEXT_BOX -> (comp as TextBoxWidget).setReadonly(value)
                        WidgetTypeJS.PASSWORD_BOX -> (comp as PasswordBoxWidget).setReadonly(value)
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
        val rows = delegate.getRows()
        rows.withIndex().forEach { (rowIdx, row) ->
            val validation = vv[rowIdx]
            val size = row.size
            row.withIndex().forEach { (colIdx, comp) ->
                if (colIdx < size - 1) {
                    val column = config.columns[colIdx]
                    when (column.widgetDescription.widgetType) {
                        WidgetTypeJS.TEXT_BOX -> (comp as TextBoxWidget).showValidation(validation.getValue(column.id) as String?)
                        WidgetTypeJS.PASSWORD_BOX -> (comp as PasswordBoxWidget).showValidation(validation.getValue(column.id) as String?)
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
    lateinit var vsFactory:()->VS
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

class TableBoxWidgetToolsPanel(private val parent:WebComponent, private val tableBox:TableBoxWidget<*,*,*>, private val rowId:String): WebComponent{
    internal val delegate:WebGridLayoutContainer
    internal val upButton:WebLinkButton
    internal val downButton:WebLinkButton
    internal val plusButton:WebLinkButton
    internal val minusButton:WebLinkButton
    init {
        delegate = UiLibraryAdapter.get().createGridLayoutContainer(this){
            noPadding = true
        }
        delegate.defineColumn()
        delegate.defineColumn()
        delegate.defineColumn()
        delegate.defineColumn()
        delegate.addRow()
        upButton = UiLibraryAdapter.get().createLinkButton(delegate){
            icon = "core:up"
        }
        upButton.setHandler {
            val idx = tableBox.rowsAdditionalData.indexOfFirst { rowId == it.id }!!
            val item = tableBox.rowsAdditionalData.removeAt(idx)
            tableBox.rowsAdditionalData.add(idx-1, item)
            tableBox.delegate.moveRow(idx, idx-1)
            tableBox.updateToolsVisibility()
        }
        delegate.addCell(WebGridLayoutCell(upButton))
        downButton = UiLibraryAdapter.get().createLinkButton(delegate){
            icon = "core:down"
        }
        downButton.setHandler {
            val idx = tableBox.rowsAdditionalData.indexOfFirst { rowId == it.id }!!
            val item = tableBox.rowsAdditionalData.removeAt(idx)
            tableBox.rowsAdditionalData.add(idx+1, item)
            tableBox.delegate.moveRow(idx, idx+1)
            tableBox.updateToolsVisibility()
        }
        delegate.addCell(WebGridLayoutCell(downButton))
        plusButton = UiLibraryAdapter.get().createLinkButton(delegate){
            icon = "core:plus"
        }
        plusButton.setHandler {
            val idx = tableBox.rowsAdditionalData.indexOfFirst { rowId == it.id }!!
            tableBox.addRow(idx+1)
        }
        delegate.addCell(WebGridLayoutCell(plusButton))
        minusButton = UiLibraryAdapter.get().createLinkButton(delegate){
            icon = "core:minus"
        }
        minusButton.setHandler {
            val idx = tableBox.rowsAdditionalData.indexOfFirst { rowId == it.id }!!
            tableBox.rowsAdditionalData.removeAt(idx)
            tableBox.delegate.removeRow(idx)
            tableBox.updateToolsVisibility()
        }
        delegate.addCell(WebGridLayoutCell(minusButton))
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




