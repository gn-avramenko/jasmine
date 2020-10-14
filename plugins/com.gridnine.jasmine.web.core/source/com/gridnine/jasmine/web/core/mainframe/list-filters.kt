/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.mainframe

import com.gridnine.jasmine.server.standard.model.rest.*
import com.gridnine.jasmine.server.standard.model.ui.YesNoEnumJS
import com.gridnine.jasmine.web.core.CoreWebMessagesJS
import com.gridnine.jasmine.web.core.reflection.ReflectionFactoryJS
import com.gridnine.jasmine.web.core.ui.AutocompleteUtils
import com.gridnine.jasmine.web.core.ui.FakeEnumJS
import com.gridnine.jasmine.web.core.ui.UiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.WebComponent
import com.gridnine.jasmine.web.core.ui.components.WebGridLayoutCell
import com.gridnine.jasmine.web.core.ui.components.WebGridLayoutContainer
import com.gridnine.jasmine.web.core.ui.widgets.*
import kotlin.reflect.KClass

internal interface ListFilterHandler<V : BaseListFilterValueDTJS, W : WebComponent> {
    fun createEditor(parent: WebComponent): W
    fun getValue(editor: W): V?
    fun reset(editor: W)
    fun isNotEmpty(comp: W): Boolean
}

internal class StringFilterHandler : ListFilterHandler<ListFilterStringValuesDTJS, TextBoxWidget> {
    override fun createEditor(parent: WebComponent): TextBoxWidget {
        return TextBoxWidget(parent, {
            width = "100%"
        })
    }

    override fun getValue(editor: TextBoxWidget): ListFilterStringValuesDTJS? {
        val value = editor.getValue()
        return value?.let {
            val result = ListFilterStringValuesDTJS()
            result.values.addAll(it.split(","))
            result
        }
    }

    override fun reset(editor: TextBoxWidget) {
        editor.setValue(null)
    }

    override fun isNotEmpty(comp: TextBoxWidget): Boolean {
        return getValue(comp)?.values?.isNotEmpty() ?: false
    }
}

internal class BooleanFilterHandler : ListFilterHandler<ListFilterBooleanValuesDTJS, EnumComboBoxWidget<YesNoEnumJS>> {
    override fun createEditor(parent: WebComponent): EnumComboBoxWidget<YesNoEnumJS> {
        val widget = EnumComboBoxWidget<YesNoEnumJS>(parent, {
            width = "100%"
            enumClass = YesNoEnumJS::class
        })
        widget.setValue(YesNoEnumJS.NOT_IMPORTANT)
        return widget
    }

    override fun getValue(editor: EnumComboBoxWidget<YesNoEnumJS>): ListFilterBooleanValuesDTJS? {
        val value = editor.getValue()
        return value?.let {
            val result = ListFilterBooleanValuesDTJS()
            result.value = when(it){
                YesNoEnumJS.YES -> true
                YesNoEnumJS.NO -> false
                YesNoEnumJS.NOT_IMPORTANT -> null
            }
            result
        }
    }

    override fun reset(editor: EnumComboBoxWidget<YesNoEnumJS>) {
        editor.setValue(YesNoEnumJS.NOT_IMPORTANT)
    }

    override fun isNotEmpty(comp: EnumComboBoxWidget<YesNoEnumJS>): Boolean {
        return comp.getValue() != YesNoEnumJS.NOT_IMPORTANT
    }


}

internal class DateIntervalEditor(parent: WebComponent, private val delegate:WebGridLayoutContainer = UiLibraryAdapter.get().createGridLayoutContainer(parent){
    width = "100%"
}):WebComponent by delegate{

    internal val fromDateBox:DateBoxWidget
    internal val toDateBox:DateBoxWidget
    init {
        delegate.defineColumn("auto")
        delegate.defineColumn("100%")
        delegate.addRow()
        val fromLabel = UiLibraryAdapter.get().createLabel(delegate)
        fromLabel.setText(CoreWebMessagesJS.from)
        delegate.addCell(WebGridLayoutCell(fromLabel))
        fromDateBox = DateBoxWidget(delegate, {
            width = "100%"
        })
        delegate.addCell(WebGridLayoutCell(fromDateBox))
        delegate.addRow()
        val toLabel = UiLibraryAdapter.get().createLabel(delegate)
        toLabel.setText(CoreWebMessagesJS.to)
        delegate.addCell(WebGridLayoutCell(toLabel))
        toDateBox = DateBoxWidget(delegate, {
            width = "100%"
        })
        delegate.addCell(WebGridLayoutCell(toDateBox))
    }
}
internal class DateFilterHandler : ListFilterHandler<ListFilterDateIntervalValueDTJS, DateIntervalEditor> {
    override fun createEditor(parent: WebComponent): DateIntervalEditor {
       return DateIntervalEditor(parent)
    }

    override fun getValue(editor: DateIntervalEditor): ListFilterDateIntervalValueDTJS? {
        val from = editor.fromDateBox.getValue()
        val to = editor.toDateBox.getValue()
        if(from==null && to == null){
            return null
        }
        val result = ListFilterDateIntervalValueDTJS()
        result.startDate = from
        result.endDate = to
        return result
    }

    override fun reset(editor: DateIntervalEditor) {
        editor.fromDateBox.setValue(null)
        editor.toDateBox.setValue(null)
    }

    override fun isNotEmpty(editor: DateIntervalEditor): Boolean {
        val from = editor.fromDateBox.getValue()
        val to = editor.toDateBox.getValue()
        return from != null || to != null
    }

}

internal class DateTimeIntervalEditor(parent: WebComponent, private val delegate:WebGridLayoutContainer = UiLibraryAdapter.get().createGridLayoutContainer(parent){
    width = "100%"
}):WebComponent by delegate{

    internal val fromDateTimeBox:DateTimeBoxWidget
    internal val toDateTimeBox:DateTimeBoxWidget
    init {
        delegate.defineColumn("auto")
        delegate.defineColumn("100%")
        delegate.addRow()
        val fromLabel = UiLibraryAdapter.get().createLabel(delegate)
        fromLabel.setText(CoreWebMessagesJS.from)
        delegate.addCell(WebGridLayoutCell(fromLabel))
        fromDateTimeBox = DateTimeBoxWidget(delegate, {
            width = "100%"
        })
        delegate.addCell(WebGridLayoutCell(fromDateTimeBox))
        delegate.addRow()
        val toLabel = UiLibraryAdapter.get().createLabel(delegate)
        toLabel.setText(CoreWebMessagesJS.to)
        delegate.addCell(WebGridLayoutCell(toLabel))
        toDateTimeBox = DateTimeBoxWidget(delegate, {
            width = "100%"
        })
        delegate.addCell(WebGridLayoutCell(toDateTimeBox))
    }
}

internal class DateTimeFilterHandler : ListFilterHandler<ListFilterDateTimeIntervalValueDTJS, DateTimeIntervalEditor> {
    override fun createEditor(parent: WebComponent): DateTimeIntervalEditor {
        return DateTimeIntervalEditor(parent)
    }

    override fun getValue(editor: DateTimeIntervalEditor): ListFilterDateTimeIntervalValueDTJS? {
        val from = editor.fromDateTimeBox.getValue()
        val to = editor.toDateTimeBox.getValue()
        if(from==null && to == null){
            return null
        }
        val result = ListFilterDateTimeIntervalValueDTJS()
        result.startDate = from
        result.endDate = to
        return result
    }

    override fun reset(editor: DateTimeIntervalEditor) {
        editor.fromDateTimeBox.setValue(null)
        editor.toDateTimeBox.setValue(null)
    }

    override fun isNotEmpty(editor: DateTimeIntervalEditor): Boolean {
        val from = editor.fromDateTimeBox.getValue()
        val to = editor.toDateTimeBox.getValue()
        return from != null || to != null
    }

}


internal class FloatNumberIntervalEditor(parent: WebComponent, private val delegate:WebGridLayoutContainer = UiLibraryAdapter.get().createGridLayoutContainer(parent){
    width = "100%"
}):WebComponent by delegate{

    internal val fromNumberBox:FloatNumberBoxWidget
    internal val toNumberBox:FloatNumberBoxWidget
    init {
        delegate.defineColumn("auto")
        delegate.defineColumn("100%")
        delegate.addRow()
        val fromLabel = UiLibraryAdapter.get().createLabel(delegate)
        fromLabel.setText(CoreWebMessagesJS.from)
        delegate.addCell(WebGridLayoutCell(fromLabel))
        fromNumberBox = FloatNumberBoxWidget(delegate, {
            width = "100%"
        })
        delegate.addCell(WebGridLayoutCell(fromNumberBox))
        delegate.addRow()
        val toLabel = UiLibraryAdapter.get().createLabel(delegate)
        toLabel.setText(CoreWebMessagesJS.to)
        delegate.addCell(WebGridLayoutCell(toLabel))
        toNumberBox = FloatNumberBoxWidget(delegate, {
            width = "100%"
        })
        delegate.addCell(WebGridLayoutCell(toNumberBox))
    }
}

internal class FloatNumberFilterHandler : ListFilterHandler<ListFilterFloatIntervalValueDTJS, FloatNumberIntervalEditor> {
    override fun createEditor(parent: WebComponent): FloatNumberIntervalEditor {
        return FloatNumberIntervalEditor(parent)
    }

    override fun getValue(editor: FloatNumberIntervalEditor): ListFilterFloatIntervalValueDTJS? {
        val from = editor.fromNumberBox.getValue()
        val to = editor.toNumberBox.getValue()
        if(from==null && to == null){
            return null
        }
        val result = ListFilterFloatIntervalValueDTJS()
        result.fromValue = from
        result.toValue = to
        return result
    }

    override fun reset(editor: FloatNumberIntervalEditor) {
        editor.fromNumberBox.setValue(null)
        editor.toNumberBox.setValue(null)
    }

    override fun isNotEmpty(editor: FloatNumberIntervalEditor): Boolean {
        val from = editor.fromNumberBox.getValue()
        val to = editor.toNumberBox.getValue()
        return from != null || to != null
    }

}

internal class EnumValueFilterHandler(private val className:String) : ListFilterHandler<ListFilterEnumValuesDTJS, EnumMultiValuesWidget<FakeEnumJS>> {

    override fun createEditor(parent: WebComponent): EnumMultiValuesWidget<FakeEnumJS> {
        val widget = EnumMultiValuesWidget<FakeEnumJS>(parent, {
            width = "100%"
            enumClassName = className
            showClearIcon = true
        })
        return widget
    }

    override fun getValue(editor: EnumMultiValuesWidget<FakeEnumJS>): ListFilterEnumValuesDTJS? {
        val values = editor.getValues()
        if(values.isEmpty()){
            return null
        }
        val result = ListFilterEnumValuesDTJS()
        result.enumClassName = className
        result.values.addAll(values.map { it.name })
        return result
    }

    override fun reset(editor: EnumMultiValuesWidget<FakeEnumJS>) {
        editor.setValues(emptyList())
    }

    override fun isNotEmpty(comp: EnumMultiValuesWidget<FakeEnumJS>): Boolean {
        return comp.getValues().isNotEmpty()
    }


}

internal class EntityValuesFilterHandler(private val className:String) : ListFilterHandler<ListFilterEntityValuesDTJS, EntityMultiValuesWidget> {

    override fun createEditor(parent: WebComponent): EntityMultiValuesWidget {
        val widget = EntityMultiValuesWidget(parent, {
            width = "100%"
            handler = AutocompleteUtils.getAutocompleteHandler(className)
            showClearIcon = true
        })
        return widget
    }

    override fun getValue(editor: EntityMultiValuesWidget): ListFilterEntityValuesDTJS? {
        return null
//        val values = editor.getValues()
//        if(values.isEmpty()){
//            return null
//        }
//        val result = ListFilterEntityValuesDTJS()
//        result.values.addAll(values)
//        return result
    }

    override fun reset(editor: EntityMultiValuesWidget) {
        //editor.setValues(emptyList())
    }

    override fun isNotEmpty(comp: EntityMultiValuesWidget): Boolean {
        return true//comp.getValues().isNotEmpty()
    }


}