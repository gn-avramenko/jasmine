/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.standard.ui.mainframe

import com.gridnine.jasmine.common.core.model.BaseIdentity
import com.gridnine.jasmine.common.core.model.FakeEnum
import com.gridnine.jasmine.common.core.reflection.ReflectionFactory
import com.gridnine.jasmine.common.core.utils.TextUtils
import com.gridnine.jasmine.common.standard.model.*
import com.gridnine.jasmine.common.standard.model.ui.YesNoEnum
import com.gridnine.jasmine.server.core.ui.common.BaseNodeWrapper
import com.gridnine.jasmine.server.core.ui.common.UiNode
import com.gridnine.jasmine.server.core.ui.components.GridLayoutCell
import com.gridnine.jasmine.server.core.ui.components.GridLayoutColumnConfiguration
import com.gridnine.jasmine.server.core.ui.components.GridLayoutContainer
import com.gridnine.jasmine.server.core.ui.components.UiLibraryAdapter
import com.gridnine.jasmine.server.core.ui.widgets.*

internal interface ListFilterHandler<V : BaseListFilterValue, W : UiNode> {
    fun createEditor(): W
    fun getValue(editor: W): V?
    fun reset(editor: W)
    fun isNotEmpty(comp: W): Boolean
}

internal class StringFilterHandler : ListFilterHandler<ListFilterStringValues, TextBoxWidget> {
    override fun createEditor(): TextBoxWidget {
        return TextBoxWidget{
            width = "100%"
        }
    }

    override fun getValue(editor: TextBoxWidget): ListFilterStringValues? {
        val value = editor.getValue()
        return value?.let {
            val result = ListFilterStringValues()
            result.values.addAll(it.split(",").filter { item -> TextUtils.isNotBlank(item) }.map { item -> item.trim() })
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

internal class BooleanFilterHandler : ListFilterHandler<ListFilterBooleanValues, EnumBoxValueWidget<YesNoEnum>> {
    override fun createEditor(): EnumBoxValueWidget<YesNoEnum> {
        val widget = EnumBoxValueWidget<YesNoEnum>{
            width = "100%"
            enumClass = YesNoEnum::class
            allowNull = false
        }
        widget.setValue(YesNoEnum.NOT_IMPORTANT)
        return widget
    }

    override fun getValue(editor: EnumBoxValueWidget<YesNoEnum>): ListFilterBooleanValues? {
        val value = editor.getValue()
        return value?.let {
            val result = ListFilterBooleanValues()
            result.value = when(it){
                YesNoEnum.YES -> true
                YesNoEnum.NO -> false
                YesNoEnum.NOT_IMPORTANT -> null
            }
            result
        }
    }

    override fun reset(editor: EnumBoxValueWidget<YesNoEnum>) {
        editor.setValue(YesNoEnum.NOT_IMPORTANT)
    }

    override fun isNotEmpty(comp: EnumBoxValueWidget<YesNoEnum>): Boolean {
        return comp.getValue() != YesNoEnum.NOT_IMPORTANT
    }


}

internal class EnumValueFilterHandler(private val className:String) : ListFilterHandler<ListFilterEnumValues, EnumMultiValuesWidget<*>> {
    override fun createEditor(): EnumMultiValuesWidget<*> {
        return EnumMultiValuesWidget<FakeEnum>{
            width = "100%"
            enumClass = ReflectionFactory.get().getClass(className)
        }
    }

    override fun getValue(editor: EnumMultiValuesWidget<*>): ListFilterEnumValues? {
        val values = editor.getValues()
        if(values.isEmpty()){
            return null
        }
        val result = ListFilterEnumValues()
        result.enumClassName = className
        result.values.addAll(values.map { it.name })
        return result
    }

    override fun reset(editor: EnumMultiValuesWidget<*>) {
        editor.setValues(emptyList())
    }

    override fun isNotEmpty(comp: EnumMultiValuesWidget<*>): Boolean {
        return comp.getValues().isNotEmpty()
    }

}

internal class DateIntervalListFilterEditor : BaseNodeWrapper<GridLayoutContainer>(){

    internal val fromDateWidget:DateBoxWidget

    internal val toDateWidget:DateBoxWidget

    init {
        _node = UiLibraryAdapter.get().createGridLayoutContainer{
            width = "100%"
            columns.add(GridLayoutColumnConfiguration("auto"))
            columns.add(GridLayoutColumnConfiguration("100%"))
        }
        _node.addRow()
        val startLabel = UiLibraryAdapter.get().createLabel { }
        startLabel.setText("с:")
        _node.addCell(GridLayoutCell(startLabel))
        fromDateWidget = DateBoxWidget{
            width = "100%"
        }
        _node.addCell(GridLayoutCell(fromDateWidget))
        _node.addRow()
        val endLabel = UiLibraryAdapter.get().createLabel { }
        endLabel.setText("по:")
        _node.addCell(GridLayoutCell(endLabel))
        toDateWidget = DateBoxWidget{
            width = "100%"
        }
        _node.addCell(GridLayoutCell(toDateWidget))
    }

}

internal class DateFilterHandler : ListFilterHandler<ListFilterDateIntervalValue, DateIntervalListFilterEditor> {
    override fun createEditor(): DateIntervalListFilterEditor {
        return DateIntervalListFilterEditor()
    }

    override fun getValue(editor: DateIntervalListFilterEditor): ListFilterDateIntervalValue? {
        val from = editor.fromDateWidget.getValue()
        val to = editor.toDateWidget.getValue()
        if(from==null && to == null){
            return null
        }
        val result = ListFilterDateIntervalValue()
        result.startDate = from
        result.endDate = to
        return result
    }

    override fun reset(editor: DateIntervalListFilterEditor) {
        editor.fromDateWidget.setValue(null)
        editor.toDateWidget.setValue(null)
    }

    override fun isNotEmpty(comp: DateIntervalListFilterEditor): Boolean {
        val from = comp.fromDateWidget.getValue()
        val to = comp.toDateWidget.getValue()
        return from != null || to != null
    }

}

internal class DateTimeIntervalListFilterEditor : BaseNodeWrapper<GridLayoutContainer>(){

    internal val fromDateWidget:DateTimeBoxWidget

    internal val toDateWidget:DateTimeBoxWidget

    init {
        _node = UiLibraryAdapter.get().createGridLayoutContainer{
            width = "100%"
            columns.add(GridLayoutColumnConfiguration("auto"))
            columns.add(GridLayoutColumnConfiguration("100%"))
        }
        _node.addRow()
        val startLabel = UiLibraryAdapter.get().createLabel { }
        startLabel.setText("с:")
        _node.addCell(GridLayoutCell(startLabel))
        fromDateWidget = DateTimeBoxWidget{
            width = "100%"
        }
        _node.addCell(GridLayoutCell(fromDateWidget))
        _node.addRow()
        val endLabel = UiLibraryAdapter.get().createLabel{}
        endLabel.setText("по:")
        _node.addCell(GridLayoutCell(endLabel))
        toDateWidget = DateTimeBoxWidget{
            width = "100%"
        }
        _node.addCell(GridLayoutCell(toDateWidget))
    }

}

internal class DateTimeFilterHandler : ListFilterHandler<ListFilterDateTimeIntervalValue, DateTimeIntervalListFilterEditor> {
    override fun createEditor(): DateTimeIntervalListFilterEditor {
        return DateTimeIntervalListFilterEditor()
    }

    override fun getValue(editor: DateTimeIntervalListFilterEditor): ListFilterDateTimeIntervalValue? {
        val from = editor.fromDateWidget.getValue()
        val to = editor.toDateWidget.getValue()
        if(from==null && to == null){
            return null
        }
        val result = ListFilterDateTimeIntervalValue()
        result.startDate = from
        result.endDate = to
        return result
    }

    override fun reset(editor: DateTimeIntervalListFilterEditor) {
        editor.fromDateWidget.setValue(null)
        editor.toDateWidget.setValue(null)
    }

    override fun isNotEmpty(comp: DateTimeIntervalListFilterEditor): Boolean {
        val from = comp.fromDateWidget.getValue()
        val to = comp.toDateWidget.getValue()
        return from != null || to != null
    }

}

internal class BigDecimalIntervalListFilterEditor : BaseNodeWrapper<GridLayoutContainer>(){

    internal val fromValueWidget:BigDecimalBoxWidget

    internal val toValueWidget:BigDecimalBoxWidget

    init {
        _node = UiLibraryAdapter.get().createGridLayoutContainer{
            width = "100%"
            columns.add(GridLayoutColumnConfiguration("auto"))
            columns.add(GridLayoutColumnConfiguration("100%"))
        }
        _node.addRow()
        val startLabel = UiLibraryAdapter.get().createLabel {  }
        startLabel.setText("с:")
        _node.addCell(GridLayoutCell(startLabel))
        fromValueWidget = BigDecimalBoxWidget{
            width = "100%"
            precision = 2
        }
        _node.addCell(GridLayoutCell(fromValueWidget))
        _node.addRow()
        val endLabel = UiLibraryAdapter.get().createLabel {  }
        endLabel.setText("по:")
        _node.addCell(GridLayoutCell(endLabel))
        toValueWidget = BigDecimalBoxWidget{
            width = "100%"
            precision = 2
        }
        _node.addCell(GridLayoutCell(toValueWidget))
    }

}


internal class BigDecimalFilterHandler : ListFilterHandler<ListFilterFloatIntervalValue, BigDecimalIntervalListFilterEditor> {
    override fun createEditor(): BigDecimalIntervalListFilterEditor {
        return BigDecimalIntervalListFilterEditor()
    }

    override fun getValue(editor: BigDecimalIntervalListFilterEditor): ListFilterFloatIntervalValue? {
        val from = editor.fromValueWidget.getValue()
        val to = editor.toValueWidget.getValue()
        if(from==null && to == null){
            return null
        }
        val result = ListFilterFloatIntervalValue()
        result.fromValue = from
        result.toValue = to
        return result
    }

    override fun reset(editor: BigDecimalIntervalListFilterEditor) {
        editor.fromValueWidget.setValue(null)
        editor.toValueWidget.setValue(null)
    }

    override fun isNotEmpty(comp: BigDecimalIntervalListFilterEditor): Boolean {
        val from = comp.fromValueWidget.getValue()
        val to = comp.toValueWidget.getValue()
        return from != null || to != null
    }

}

internal class EntityValuesFilterHandler(private val className:String) : ListFilterHandler<ListFilterEntityValues, EntityMultiValuesWidget<BaseIdentity>> {

    override fun createEditor(): EntityMultiValuesWidget<BaseIdentity> {
        return EntityMultiValuesWidget {
                    width = "100%"
                    handler = AutocompleteHandler.createMetadataBasedAutocompleteHandler(className)
        }
    }

    override fun getValue(editor: EntityMultiValuesWidget<BaseIdentity>): ListFilterEntityValues? {
        val values = editor.getValues()
        if(values.isEmpty()){
            return null
        }
        val result = ListFilterEntityValues()
        result.values.addAll(values)
        return result
    }

    override fun reset(editor: EntityMultiValuesWidget<BaseIdentity>) {
        editor.setValues(emptyList())
    }

    override fun isNotEmpty(comp: EntityMultiValuesWidget<BaseIdentity>): Boolean {
        return comp.getValues().isNotEmpty()
    }


}