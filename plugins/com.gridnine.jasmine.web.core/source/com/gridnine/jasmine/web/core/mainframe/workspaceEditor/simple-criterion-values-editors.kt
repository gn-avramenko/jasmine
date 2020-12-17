/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.mainframe.workspaceEditor

import com.gridnine.jasmine.server.standard.model.domain.*
import com.gridnine.jasmine.web.core.RestReflectionUtilsJS
import com.gridnine.jasmine.web.core.reflection.ReflectionFactoryJS
import com.gridnine.jasmine.web.core.ui.*
import com.gridnine.jasmine.web.core.ui.components.WebGridLayoutCell
import com.gridnine.jasmine.web.core.ui.components.WebGridLayoutContainer
import com.gridnine.jasmine.web.core.ui.widgets.*
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS

class NullValueEditor(private val parent:WebComponent):SimpleCriterionValueEditor<BaseWorkspaceSimpleCriterionValueJS>{
    override fun getType(): SimpleCriterionValueType {
        return SimpleCriterionValueType.NULL
    }

    override fun setValue(value: BaseWorkspaceSimpleCriterionValueJS?) {
        //nooops
    }

    override fun getValue(): BaseWorkspaceSimpleCriterionValueJS? {
        return null
    }

    override fun getParent(): WebComponent? {
        return parent
    }

    override fun getChildren(): List<WebComponent> {
        return emptyList()
    }

    override fun getHtml(): String {
        return ""
    }

    override fun decorate() {
        //noops
    }

    override fun destroy() {
        //noops
    }

}


class StringValuesValueEditor(private val parent:WebComponent):SimpleCriterionValueEditor<WorkspaceSimpleCriterionStringValuesJS>{
    private val widget:TextBoxWidget
    init {
        widget = TextBoxWidget(parent){width="100%"}
    }
    override fun getType(): SimpleCriterionValueType {
        return SimpleCriterionValueType.STRING_VALUES
    }

    override fun setValue(value: WorkspaceSimpleCriterionStringValuesJS?) {
        widget.setValue(value?.let { it.values.joinToString(",") })
    }

    override fun getValue(): WorkspaceSimpleCriterionStringValuesJS? {
        return widget.getValue()?.let {
            val result = WorkspaceSimpleCriterionStringValuesJS()
            result.uid = MiscUtilsJS.createUUID()
            result.values.addAll(it.split(","))
            result
        }
    }

    override fun getParent(): WebComponent? {
        return parent
    }

    override fun getChildren(): List<WebComponent> {
        return emptyList()
    }

    override fun getHtml(): String {
        return widget.getHtml()
    }

    override fun decorate() {
        widget.decorate()
    }

    override fun destroy() {
        widget.destroy()
    }

}

class EnumValuesValueEditor(private val parent:WebComponent, private val clsName:String):SimpleCriterionValueEditor<WorkspaceSimpleCriterionEnumValuesJS>{
    private val widget:EnumMultiValuesWidget<FakeEnumJS>
    init {
        widget = EnumMultiValuesWidget(parent){
            enumClassName = clsName
            width="100%"
        }
    }
    override fun getType(): SimpleCriterionValueType {
        return SimpleCriterionValueType.ENUM_VALUES
    }

    override fun setValue(value: WorkspaceSimpleCriterionEnumValuesJS?) {
        widget.setValues(value?.values?.map { ReflectionFactoryJS.get().getEnum<FakeEnumJS>(clsName, it)}?: emptyList<FakeEnumJS>())
    }

    override fun getValue(): WorkspaceSimpleCriterionEnumValuesJS? {
        val values = widget.getValues()
        if(values.isEmpty()){
            return null
        }
        val result = WorkspaceSimpleCriterionEnumValuesJS()
        result.uid = MiscUtilsJS.createUUID()
        result.enumClassName = clsName
        result.values.addAll(widget.getValues().map { it.name })
        return result
    }

    override fun getParent(): WebComponent? {
        return parent
    }

    override fun getChildren(): List<WebComponent> {
        return emptyList()
    }

    override fun getHtml(): String {
        return widget.getHtml()
    }

    override fun decorate() {
        widget.decorate()
    }

    override fun destroy() {
        widget.destroy()
    }

}


class LongValueEditor(private val parent:WebComponent):SimpleCriterionValueEditor<WorkspaceSimpleCriterionLongValueJS>{
    private val widget:LongNumberBoxWidget
    init {
        widget = LongNumberBoxWidget(parent){width="100%"}
    }
    override fun getType(): SimpleCriterionValueType {
        return SimpleCriterionValueType.LONG_VALUE
    }

    override fun setValue(value: WorkspaceSimpleCriterionLongValueJS?) {
        widget.setValue(value?.value)
    }

    override fun getValue(): WorkspaceSimpleCriterionLongValueJS? {
        return widget.getValue()?.let {
            val result = WorkspaceSimpleCriterionLongValueJS()
            result.uid = MiscUtilsJS.createUUID()
            result.value = it
            result
        }
    }

    override fun getParent(): WebComponent? {
        return parent
    }

    override fun getChildren(): List<WebComponent> {
        return emptyList()
    }

    override fun getHtml(): String {
        return widget.getHtml()
    }

    override fun decorate() {
        widget.decorate()
    }

    override fun destroy() {
        widget.destroy()
    }

}

class IntValueEditor(private val parent:WebComponent):SimpleCriterionValueEditor<WorkspaceSimpleCriterionIntValueJS>{
    private val widget:IntegerNumberBoxWidget
    init {
        widget = IntegerNumberBoxWidget(parent){width="100%"}
    }
    override fun getType(): SimpleCriterionValueType {
        return SimpleCriterionValueType.INT_VALUE
    }

    override fun setValue(value: WorkspaceSimpleCriterionIntValueJS?) {
        widget.setValue(value?.value)
    }

    override fun getValue(): WorkspaceSimpleCriterionIntValueJS? {
        return widget.getValue()?.let {
            val result = WorkspaceSimpleCriterionIntValueJS()
            result.uid = MiscUtilsJS.createUUID()
            result.value = it
            result
        }
    }

    override fun getParent(): WebComponent? {
        return parent
    }

    override fun getChildren(): List<WebComponent> {
        return emptyList()
    }

    override fun getHtml(): String {
        return widget.getHtml()
    }

    override fun decorate() {
        widget.decorate()
    }

    override fun destroy() {
        widget.destroy()
    }

}

class FloatlValueEditor(private val parent:WebComponent):SimpleCriterionValueEditor<WorkspaceSimpleCriterionFloatValueJS>{
    private val widget:FloatNumberBoxWidget
    init {
        widget = FloatNumberBoxWidget(parent){width="100%"}
    }
    override fun getType(): SimpleCriterionValueType {
        return SimpleCriterionValueType.BIG_DECIMAL_VALUE
    }

    override fun setValue(value: WorkspaceSimpleCriterionFloatValueJS?) {
        widget.setValue(value?.value)
    }

    override fun getValue(): WorkspaceSimpleCriterionFloatValueJS? {
        return widget.getValue()?.let {
            val result = WorkspaceSimpleCriterionFloatValueJS()
            result.uid = MiscUtilsJS.createUUID()
            result.value = it
            result
        }
    }

    override fun getParent(): WebComponent? {
        return parent
    }

    override fun getChildren(): List<WebComponent> {
        return emptyList()
    }

    override fun getHtml(): String {
        return widget.getHtml()
    }

    override fun decorate() {
        widget.decorate()
    }

    override fun destroy() {
        widget.destroy()
    }

}


class EntityReferenceValuesValueEditor(private val parent:WebComponent, private val clsName:String):SimpleCriterionValueEditor<WorkspaceSimpleCriterionEntityValuesJS>{
    private val widget:EntityMultiValuesWidget
    init {
        widget = EntityMultiValuesWidget(parent){
            handler = ClientRegistry.get().get(ObjectHandler.TYPE, clsName)!!.getAutocompleteHandler()
            width="100%"
        }
    }
    override fun getType(): SimpleCriterionValueType {
        return SimpleCriterionValueType.ENTITY_REFERENCE_VALUES
    }

    override fun setValue(value: WorkspaceSimpleCriterionEntityValuesJS?) {
        widget.setValues(value?.values?: emptyList())
    }

    override fun getValue(): WorkspaceSimpleCriterionEntityValuesJS? {
        val values = widget.getValues()
        if(values.isEmpty()){
            return null
        }
        val result = WorkspaceSimpleCriterionEntityValuesJS()
        result.uid = MiscUtilsJS.createUUID()
        result.values.addAll(widget.getValues())
        return result
    }

    override fun getParent(): WebComponent? {
        return parent
    }

    override fun getChildren(): List<WebComponent> {
        return emptyList()
    }

    override fun getHtml(): String {
        return widget.getHtml()
    }

    override fun decorate() {
        widget.decorate()
    }

    override fun destroy() {
        widget.destroy()
    }

}

class DateValueEditor(private val parent:WebComponent):SimpleCriterionValueEditor<WorkspaceSimpleCriterionDateValueJS>{
    private val widget:DateBoxWidget
    init {
        widget = DateBoxWidget(parent){width="100%"}
    }
    override fun getType(): SimpleCriterionValueType {
        return SimpleCriterionValueType.DATE_VALUE
    }

    override fun setValue(value: WorkspaceSimpleCriterionDateValueJS?) {
        widget.setValue(value?.value)
    }

    override fun getValue(): WorkspaceSimpleCriterionDateValueJS? {
        return widget.getValue()?.let {
            val result = WorkspaceSimpleCriterionDateValueJS()
            result.uid = MiscUtilsJS.createUUID()
            result.value = it
            result
        }
    }

    override fun getParent(): WebComponent? {
        return parent
    }

    override fun getChildren(): List<WebComponent> {
        return emptyList()
    }

    override fun getHtml(): String {
        return widget.getHtml()
    }

    override fun decorate() {
        widget.decorate()
    }

    override fun destroy() {
        widget.destroy()
    }

}

class DateTimeValueEditor(private val parent:WebComponent):SimpleCriterionValueEditor<WorkspaceSimpleCriterionDateTimeValueJS>{
    private val widget:DateTimeBoxWidget
    init {
        widget = DateTimeBoxWidget(parent){width="100%"}
    }
    override fun getType(): SimpleCriterionValueType {
        return SimpleCriterionValueType.DATE_TIME_VALUE
    }

    override fun setValue(value: WorkspaceSimpleCriterionDateTimeValueJS?) {
        widget.setValue(value?.value)
    }

    override fun getValue(): WorkspaceSimpleCriterionDateTimeValueJS? {
        return widget.getValue()?.let {
            val result = WorkspaceSimpleCriterionDateTimeValueJS()
            result.uid = MiscUtilsJS.createUUID()
            result.value = it
            result
        }
    }

    override fun getParent(): WebComponent? {
        return parent
    }

    override fun getChildren(): List<WebComponent> {
        return emptyList()
    }

    override fun getHtml(): String {
        return widget.getHtml()
    }

    override fun decorate() {
        widget.decorate()
    }

    override fun destroy() {
        widget.destroy()
    }

}


class DateIntervalEditor(private val parent:WebComponent):SimpleCriterionValueEditor<WorkspaceSimpleCriterionDateIntervalValueJS>{
    private val startDate:DateBoxWidget
    private val endDate:DateBoxWidget
    private val layout :WebGridLayoutContainer
    init {
        layout = UiLibraryAdapter.get().createGridLayoutContainer(parent){
            width="100%"
            noPadding = true
        }
        layout.defineColumn("auto")
        layout.defineColumn("90%")
        layout.addRow()
        val startLabel = UiLibraryAdapter.get().createLabel(layout)
        startLabel.setText("с")
        layout.addCell(WebGridLayoutCell(startLabel))
        startDate = DateBoxWidget(layout){width="100%"}
        layout.addCell(WebGridLayoutCell(startDate))
        layout.addRow()
        val endLabel = UiLibraryAdapter.get().createLabel(layout)
        endLabel.setText("по")
        layout.addCell(WebGridLayoutCell(endLabel))
        endDate = DateBoxWidget(layout){width="100%"}
        layout.addCell(WebGridLayoutCell(endDate))
    }
    override fun getType(): SimpleCriterionValueType {
        return SimpleCriterionValueType.DATE_INTERVAL
    }

    override fun setValue(value: WorkspaceSimpleCriterionDateIntervalValueJS?) {
        if(value == null){
            return
        }
        startDate.setValue(value?.startDate)
        endDate.setValue(value?.startDate)
    }

    override fun getValue(): WorkspaceSimpleCriterionDateIntervalValueJS? {
        val std = startDate.getValue()
        val ed = endDate.getValue()
        if(std == null && ed == null){
            return null
        }
        val result = WorkspaceSimpleCriterionDateIntervalValueJS()
        result.startDate = std
        result.endDate = ed
        return result
    }

    override fun getParent(): WebComponent? {
        return parent
    }

    override fun getChildren(): List<WebComponent> {
        return emptyList()
    }

    override fun getHtml(): String {
        return layout.getHtml()
    }

    override fun decorate() {
        layout.decorate()
    }

    override fun destroy() {
        layout.destroy()
    }

}

class DateTimeIntervalEditor(private val parent:WebComponent):SimpleCriterionValueEditor<WorkspaceSimpleCriterionDateTimeIntervalValueJS>{
    private val startDate:DateTimeBoxWidget
    private val endDate:DateTimeBoxWidget
    private val layout :WebGridLayoutContainer
    init {
        layout = UiLibraryAdapter.get().createGridLayoutContainer(parent){
            width="100%"
            noPadding = true
        }
        layout.defineColumn("auto")
        layout.defineColumn("90%")
        layout.addRow()
        val startLabel = UiLibraryAdapter.get().createLabel(layout)
        startLabel.setText("с")
        layout.addCell(WebGridLayoutCell(startLabel))
        startDate = DateTimeBoxWidget(layout){width="100%"}
        layout.addCell(WebGridLayoutCell(startDate))
        layout.addRow()
        val endLabel = UiLibraryAdapter.get().createLabel(layout)
        endLabel.setText("по")
        layout.addCell(WebGridLayoutCell(endLabel))
        endDate = DateTimeBoxWidget(layout){width="100%"}
        layout.addCell(WebGridLayoutCell(endDate))
    }
    override fun getType(): SimpleCriterionValueType {
        return SimpleCriterionValueType.DATE_INTERVAL
    }

    override fun setValue(value: WorkspaceSimpleCriterionDateTimeIntervalValueJS?) {
        if(value == null){
            return
        }
        startDate.setValue(value?.startDate)
        endDate.setValue(value?.startDate)
    }

    override fun getValue(): WorkspaceSimpleCriterionDateTimeIntervalValueJS? {
        val std = startDate.getValue()
        val ed = endDate.getValue()
        if(std == null && ed == null){
            return null
        }
        val result = WorkspaceSimpleCriterionDateTimeIntervalValueJS()
        result.startDate = std
        result.endDate = ed
        return result
    }

    override fun getParent(): WebComponent? {
        return parent
    }

    override fun getChildren(): List<WebComponent> {
        return emptyList()
    }

    override fun getHtml(): String {
        return layout.getHtml()
    }

    override fun decorate() {
        layout.decorate()
    }

    override fun destroy() {
        layout.destroy()
    }

}