/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.widgets

import com.gridnine.jasmine.server.core.model.common.XeptionJS
import com.gridnine.jasmine.server.core.model.domain.ObjectReferenceJS
import com.gridnine.jasmine.server.core.model.ui.*
import com.gridnine.jasmine.web.core.ui.*
import com.gridnine.jasmine.web.core.ui.components.WebTableBox
import com.gridnine.jasmine.web.core.ui.components.WebTableBoxRow
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import kotlin.js.Date

class TableBoxWidget<VM:BaseTableBoxVMJS,VS:BaseTableBoxVSJS, VV:BaseTableBoxVVJS>(private val parent: WebComponent, configure:TableBoxWidgetConfiguration<VM,VS>.()->Unit):WebComponent{
    private val delegate:WebTableBox
    private val rowsAdditionalData = arrayListOf<TableBoxWidgetRowAdditionalData>()
    private val config = TableBoxWidgetConfiguration<VM,VS>()
    init {
        config.configure()
        delegate = UiLibraryAdapter.get().createTableBox(parent){
            width = config.width
            height = config.height
            showHeader = true
            showToolsColumn = config.showToolsColumn
            toolsColumnMaxWidth = config.toolsColumnMaxWidth
            config.columns.forEach { cell ->
                headerCellsTitles.add(cell.title)
                headerCellsWidths.add(cell.width)
            }
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

    fun getData(): List<VM> {
        return arrayListOf()
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
                val row = existingRows[idx]
                row.components.withIndex().forEach { (compIdx, comp) ->
                    setValue(comp, compIdx, value)
                }
            } else {
                val components = arrayListOf<WebComponent>()
                config.columns.withIndex().forEach {(collIdx, coll) ->
                    val comp = createWebComponent(coll.widgetDescription)
                    setValue(comp, collIdx, value)
                    components.add(comp)
                }
                delegate.addRow(null, WebTableBoxRow(components, null))
                rowsAdditionalData.add(TableBoxWidgetRowAdditionalData(value.uid, MiscUtilsJS.createUUID()))
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
        }
    }

    fun setReadonly(value: Boolean) {
        //noops
    }

    fun showValidation(vv: List<VV>) {
        //noops
    }

}
class TableBoxWidgetConfiguration<VM:BaseTableBoxVMJS,VS:BaseTableBoxVSJS>{
    var width:String? = null
    var height:String? = null
    val columns = arrayListOf<TableBoxWidgetColumnDescription>()
    var showToolsColumn = true
    var toolsColumnMaxWidth:String? = null
    lateinit var vmFactory:()->VM
    lateinit var vsFactory:()->VS
    fun column(id:String, widgetDescription:BaseWidgetDescriptionJS, title:String, width:String? = null){
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
    var width:String? = null
    lateinit var title:String
    lateinit var id:String
}





