/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.mainframe

import com.gridnine.jasmine.server.core.model.common.BaseIdentity
import com.gridnine.jasmine.server.core.model.common.FakeEnum
import com.gridnine.jasmine.server.core.reflection.ReflectionFactory
import com.gridnine.jasmine.server.core.utils.TextUtils
import com.gridnine.jasmine.server.standard.model.*
import com.gridnine.jasmine.server.standard.model.ui.YesNoEnum
import com.gridnine.jasmine.web.server.components.*
import com.gridnine.jasmine.web.server.widgets.*

internal interface ServerUiListFilterHandler<V : BaseListFilterValue, W : ServerUiNode> {
    fun createEditor(): W
    fun getValue(editor: W): V?
    fun reset(editor: W)
    fun isNotEmpty(comp: W): Boolean
}

internal class ServerUiStringFilterHandler : ServerUiListFilterHandler<ListFilterStringValues, ServerUiTextBoxWidget> {
    override fun createEditor(): ServerUiTextBoxWidget {
        return ServerUiTextBoxWidget(ServerUiTextBoxWidgetConfiguration{
            width = "100%"
        })
    }

    override fun getValue(editor: ServerUiTextBoxWidget): ListFilterStringValues? {
        val value = editor.getValue()
        return value?.let {
            val result = ListFilterStringValues()
            result.values.addAll(it.split(",").filter { TextUtils.isNotBlank(it) }.map { it.trim() })
            result
        }
    }

    override fun reset(editor: ServerUiTextBoxWidget) {
        editor.setValue(null)
    }

    override fun isNotEmpty(comp: ServerUiTextBoxWidget): Boolean {
        return getValue(comp)?.values?.isNotEmpty() ?: false
    }
}

internal class ServerUiBooleanFilterHandler : ServerUiListFilterHandler<ListFilterBooleanValues, ServerUiEnumValueWidget<YesNoEnum>> {
    override fun createEditor(): ServerUiEnumValueWidget<YesNoEnum> {
        val widget = ServerUiEnumValueWidget<YesNoEnum>( ServerUiEnumValueWidgetConfiguration{
            width = "100%"
            enumClass = YesNoEnum::class
            allowNull = false
        })
        widget.setValue(YesNoEnum.NOT_IMPORTANT)
        return widget
    }

    override fun getValue(editor: ServerUiEnumValueWidget<YesNoEnum>): ListFilterBooleanValues? {
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

    override fun reset(editor: ServerUiEnumValueWidget<YesNoEnum>) {
        editor.setValue(YesNoEnum.NOT_IMPORTANT)
    }

    override fun isNotEmpty(comp: ServerUiEnumValueWidget<YesNoEnum>): Boolean {
        return comp.getValue() != YesNoEnum.NOT_IMPORTANT
    }


}

internal class ServerUiEnumValueFilterHandler(private val className:String) : ServerUiListFilterHandler<ListFilterEnumValues, ServerUiEnumMultiValuesWidget<*>> {
    override fun createEditor(): ServerUiEnumMultiValuesWidget<*> {
        val widget = ServerUiEnumMultiValuesWidget<FakeEnum>(ServerUiEnumMultiValuesWidgetConfiguration{
            width = "100%"
            enumClass = ReflectionFactory.get().getClass<FakeEnum>(className)
        })
        return widget
    }

    override fun getValue(editor: ServerUiEnumMultiValuesWidget<*>): ListFilterEnumValues? {
        val values = editor.getValues()
        if(values.isEmpty()){
            return null
        }
        val result = ListFilterEnumValues()
        result.enumClassName = className
        result.values.addAll(values.map { it.name })
        return result
    }

    override fun reset(editor: ServerUiEnumMultiValuesWidget<*>) {
        editor.setValues(emptyList())
    }

    override fun isNotEmpty(comp: ServerUiEnumMultiValuesWidget<*>): Boolean {
        return comp.getValues().isNotEmpty()
    }

}

internal class ServerUiDateIntervalListFilterEditor : BaseServerUiNodeWrapper<ServerUiGridLayoutContainer>(){

    internal val fromDateWidget:ServerUiDateBoxWidget

    internal val toDateWidget:ServerUiDateBoxWidget

    init {
        _node = ServerUiLibraryAdapter.get().createGridLayoutContainer(ServerUiGridLayoutContainerConfiguration{
            width = "100%"
            columns.add(ServerUiGridLayoutColumnConfiguration("auto"))
            columns.add(ServerUiGridLayoutColumnConfiguration("100%"))
        })
        _node.addRow()
        val startLabel = ServerUiLibraryAdapter.get().createLabel(ServerUiLabelConfiguration())
        startLabel.setText("с:")
        _node.addCell(ServerUiGridLayoutCell(startLabel))
        fromDateWidget = ServerUiDateBoxWidget(ServerUiDateBoxWidgetConfiguration{
            width = "100%"
        })
        _node.addCell(ServerUiGridLayoutCell(fromDateWidget))
        _node.addRow()
        val endLabel = ServerUiLibraryAdapter.get().createLabel(ServerUiLabelConfiguration())
        endLabel.setText("по:")
        _node.addCell(ServerUiGridLayoutCell(endLabel))
        toDateWidget = ServerUiDateBoxWidget(ServerUiDateBoxWidgetConfiguration{
            width = "100%"
        })
        _node.addCell(ServerUiGridLayoutCell(toDateWidget))
    }

}

internal class ServerUiDateFilterHandler : ServerUiListFilterHandler<ListFilterDateIntervalValue, ServerUiDateIntervalListFilterEditor> {
    override fun createEditor(): ServerUiDateIntervalListFilterEditor {
        return ServerUiDateIntervalListFilterEditor()
    }

    override fun getValue(editor: ServerUiDateIntervalListFilterEditor): ListFilterDateIntervalValue? {
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

    override fun reset(editor: ServerUiDateIntervalListFilterEditor) {
        editor.fromDateWidget.setValue(null)
        editor.toDateWidget.setValue(null)
    }

    override fun isNotEmpty(editor: ServerUiDateIntervalListFilterEditor): Boolean {
        val from = editor.fromDateWidget.getValue()
        val to = editor.toDateWidget.getValue()
        return from != null || to != null
    }

}

internal class ServerUiDateTimeIntervalListFilterEditor : BaseServerUiNodeWrapper<ServerUiGridLayoutContainer>(){

    internal val fromDateWidget:ServerUiDateTimeBoxWidget

    internal val toDateWidget:ServerUiDateTimeBoxWidget

    init {
        _node = ServerUiLibraryAdapter.get().createGridLayoutContainer(ServerUiGridLayoutContainerConfiguration{
            width = "100%"
            columns.add(ServerUiGridLayoutColumnConfiguration("auto"))
            columns.add(ServerUiGridLayoutColumnConfiguration("100%"))
        })
        _node.addRow()
        val startLabel = ServerUiLibraryAdapter.get().createLabel(ServerUiLabelConfiguration())
        startLabel.setText("с:")
        _node.addCell(ServerUiGridLayoutCell(startLabel))
        fromDateWidget = ServerUiDateTimeBoxWidget(ServerUiDateTimeBoxWidgetConfiguration{
            width = "100%"
        })
        _node.addCell(ServerUiGridLayoutCell(fromDateWidget))
        _node.addRow()
        val endLabel = ServerUiLibraryAdapter.get().createLabel(ServerUiLabelConfiguration())
        endLabel.setText("по:")
        _node.addCell(ServerUiGridLayoutCell(endLabel))
        toDateWidget = ServerUiDateTimeBoxWidget(ServerUiDateTimeBoxWidgetConfiguration{
            width = "100%"
        })
        _node.addCell(ServerUiGridLayoutCell(toDateWidget))
    }

}

internal class ServerUiDateTimeFilterHandler : ServerUiListFilterHandler<ListFilterDateTimeIntervalValue, ServerUiDateTimeIntervalListFilterEditor> {
    override fun createEditor(): ServerUiDateTimeIntervalListFilterEditor {
        return ServerUiDateTimeIntervalListFilterEditor()
    }

    override fun getValue(editor: ServerUiDateTimeIntervalListFilterEditor): ListFilterDateTimeIntervalValue? {
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

    override fun reset(editor: ServerUiDateTimeIntervalListFilterEditor) {
        editor.fromDateWidget.setValue(null)
        editor.toDateWidget.setValue(null)
    }

    override fun isNotEmpty(editor: ServerUiDateTimeIntervalListFilterEditor): Boolean {
        val from = editor.fromDateWidget.getValue()
        val to = editor.toDateWidget.getValue()
        return from != null || to != null
    }

}

internal class ServerUiBigDecimalIntervalListFilterEditor : BaseServerUiNodeWrapper<ServerUiGridLayoutContainer>(){

    internal val fromValueWidget:ServerUiBigDecimalBoxWidget

    internal val toValueWidget:ServerUiBigDecimalBoxWidget

    init {
        _node = ServerUiLibraryAdapter.get().createGridLayoutContainer(ServerUiGridLayoutContainerConfiguration{
            width = "100%"
            columns.add(ServerUiGridLayoutColumnConfiguration("auto"))
            columns.add(ServerUiGridLayoutColumnConfiguration("100%"))
        })
        _node.addRow()
        val startLabel = ServerUiLibraryAdapter.get().createLabel(ServerUiLabelConfiguration())
        startLabel.setText("с:")
        _node.addCell(ServerUiGridLayoutCell(startLabel))
        fromValueWidget = ServerUiBigDecimalBoxWidget(ServerUiBigDecimalBoxWidgetConfiguration{
            width = "100%"
            precision = 2
        })
        _node.addCell(ServerUiGridLayoutCell(fromValueWidget))
        _node.addRow()
        val endLabel = ServerUiLibraryAdapter.get().createLabel(ServerUiLabelConfiguration())
        endLabel.setText("по:")
        _node.addCell(ServerUiGridLayoutCell(endLabel))
        toValueWidget = ServerUiBigDecimalBoxWidget(ServerUiBigDecimalBoxWidgetConfiguration{
            width = "100%"
            precision = 2
        })
        _node.addCell(ServerUiGridLayoutCell(toValueWidget))
    }

}


internal class ServerUiBigDecimalFilterHandler : ServerUiListFilterHandler<ListFilterFloatIntervalValue, ServerUiBigDecimalIntervalListFilterEditor> {
    override fun createEditor(): ServerUiBigDecimalIntervalListFilterEditor {
        return ServerUiBigDecimalIntervalListFilterEditor()
    }

    override fun getValue(editor: ServerUiBigDecimalIntervalListFilterEditor): ListFilterFloatIntervalValue? {
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

    override fun reset(editor: ServerUiBigDecimalIntervalListFilterEditor) {
        editor.fromValueWidget.setValue(null)
        editor.toValueWidget.setValue(null)
    }

    override fun isNotEmpty(editor: ServerUiBigDecimalIntervalListFilterEditor): Boolean {
        val from = editor.fromValueWidget.getValue()
        val to = editor.toValueWidget.getValue()
        return from != null || to != null
    }

}

internal class ServerUiEntityValuesFilterHandler(private val className:String) : ServerUiListFilterHandler<ListFilterEntityValues, ServerUiEntityMultiValuesWidget<BaseIdentity>> {

    override fun createEditor(): ServerUiEntityMultiValuesWidget<BaseIdentity> {
        return ServerUiEntityMultiValuesWidget<BaseIdentity>(
                ServerUiEntityMultiValuesWidgetConfiguration{
                    width = "100%"
                    handler = ServerUiAutocompleteHandler.createMetadataBasedAutocompleteHandler(className)
        })
    }

    override fun getValue(editor: ServerUiEntityMultiValuesWidget<BaseIdentity>): ListFilterEntityValues? {
        val values = editor.getValues()
        if(values.isEmpty()){
            return null
        }
        val result = ListFilterEntityValues()
        result.values.addAll(values)
        return result
    }

    override fun reset(editor: ServerUiEntityMultiValuesWidget<BaseIdentity>) {
        editor.setValues(emptyList())
    }

    override fun isNotEmpty(comp: ServerUiEntityMultiValuesWidget<BaseIdentity>): Boolean {
        return comp.getValues().isNotEmpty()
    }


}