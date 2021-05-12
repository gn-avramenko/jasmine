/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("unused")

package com.gridnine.jasmine.web.standard.list

import com.gridnine.jasmine.common.core.model.FakeEnumJS
import com.gridnine.jasmine.common.standard.model.rest.*
import com.gridnine.jasmine.common.standard.model.ui.YesNoEnumJS
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.components.BaseWebNodeWrapper
import com.gridnine.jasmine.web.core.ui.components.WebGridLayoutContainer
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import com.gridnine.jasmine.web.standard.widgets.*


internal class StringFilterHandler : ListFilterHandler<ListFilterStringValuesDTJS, TextBoxWidget> {
    override fun createEditor(): TextBoxWidget {
        return TextBoxWidget{
            width = "100%"
        }
    }

    override fun getValue(editor: TextBoxWidget): ListFilterStringValuesDTJS? {
        val value = editor.getValue()
        return value?.let { it ->
            val result = ListFilterStringValuesDTJS()
            result.values.addAll(it.split(",").filter { value -> MiscUtilsJS.isNotBlank(value) }.map { value -> value.trim() })
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

internal class BooleanFilterHandler : ListFilterHandler<ListFilterBooleanValuesDTJS, EnumValueWidget<YesNoEnumJS>> {
    override fun createEditor(): EnumValueWidget<YesNoEnumJS> {
        val widget = EnumValueWidget<YesNoEnumJS>{
            width = "100%"
            enumClass = YesNoEnumJS::class
            allowNull = false
        }
        widget.setValue(YesNoEnumJS.NOT_IMPORTANT)
        return widget
    }

    override fun getValue(editor: EnumValueWidget<YesNoEnumJS>): ListFilterBooleanValuesDTJS? {
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

    override fun reset(editor: EnumValueWidget<YesNoEnumJS>) {
        editor.setValue(YesNoEnumJS.NOT_IMPORTANT)
    }

    override fun isNotEmpty(comp: EnumValueWidget<YesNoEnumJS>): Boolean {
        return comp.getValue() != YesNoEnumJS.NOT_IMPORTANT
    }


}

internal class DateIntervalEditor:BaseWebNodeWrapper<WebGridLayoutContainer>(){

    lateinit var fromDateBox:DateBoxWidget
    lateinit var toDateBox:DateBoxWidget
    init {
        _node = WebUiLibraryAdapter.get().createGridContainer {
            width = "100%"
            column("auto")
            column("100%")
            row {
                cell(WebUiLibraryAdapter.get().createLabel {  }.apply { setText("с") })
                fromDateBox = DateBoxWidget{
                    width = "100%"
                }
                cell(fromDateBox)
            }
            row {
                cell(WebUiLibraryAdapter.get().createLabel {  }.apply { setText("по") })
                toDateBox = DateBoxWidget{
                    width = "100%"
                }
                cell(toDateBox)
            }
        }
    }
}
internal class DateFilterHandler : ListFilterHandler<ListFilterDateIntervalValueDTJS, DateIntervalEditor> {
    override fun createEditor(): DateIntervalEditor {
       return DateIntervalEditor()
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

    override fun isNotEmpty(comp: DateIntervalEditor): Boolean {
        val from = comp.fromDateBox.getValue()
        val to = comp.toDateBox.getValue()
        return from != null || to != null
    }

}

internal class DateTimeIntervalEditor:BaseWebNodeWrapper<WebGridLayoutContainer>(){

    lateinit var fromDateTimeBox:DateTimeBoxWidget
    lateinit var toDateTimeBox:DateTimeBoxWidget
    init {
        _node = WebUiLibraryAdapter.get().createGridContainer {
            width = "100px"
            column("auto")
            column("100%")
            row {
                cell(WebUiLibraryAdapter.get().createLabel {  }.apply { setText("с") })
                fromDateTimeBox = DateTimeBoxWidget{
                    width = "100%"
                }
                cell(fromDateTimeBox)
            }
            row {
                cell(WebUiLibraryAdapter.get().createLabel {  }.apply { setText("по") })
                toDateTimeBox = DateTimeBoxWidget{
                    width = "100%"
                }
                cell(toDateTimeBox)
            }
        }
    }
}


internal class DateTimeFilterHandler : ListFilterHandler<ListFilterDateTimeIntervalValueDTJS, DateTimeIntervalEditor> {
    override fun createEditor(): DateTimeIntervalEditor {
        return DateTimeIntervalEditor()
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

    override fun isNotEmpty(comp: DateTimeIntervalEditor): Boolean {
        val from = comp.fromDateTimeBox.getValue()
        val to = comp.toDateTimeBox.getValue()
        return from != null || to != null
    }

}

internal class FloatNumberIntervalEditor:BaseWebNodeWrapper<WebGridLayoutContainer>(){

    lateinit var fromNumberBox:FloatNumberBoxWidget
    lateinit var toNumberBox:FloatNumberBoxWidget
    init {
        _node = WebUiLibraryAdapter.get().createGridContainer {
            width = "100%"
            column("auto")
            column("100%")
            row {
                cell(WebUiLibraryAdapter.get().createLabel {  }.apply { setText("с") })
                fromNumberBox = FloatNumberBoxWidget{
                    width = "100%"
                }
                cell(fromNumberBox)
            }
            row {
                cell(WebUiLibraryAdapter.get().createLabel {  }.apply { setText("по") })
                toNumberBox = FloatNumberBoxWidget{
                    width = "100%"
                }
                cell(toNumberBox)
            }
        }
    }
}

internal class FloatNumberFilterHandler : ListFilterHandler<ListFilterFloatIntervalValueDTJS, FloatNumberIntervalEditor> {
    override fun createEditor(): FloatNumberIntervalEditor {
        return FloatNumberIntervalEditor()
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

    override fun isNotEmpty(comp: FloatNumberIntervalEditor): Boolean {
        val from = comp.fromNumberBox.getValue()
        val to = comp.toNumberBox.getValue()
        return from != null || to != null
    }

}

internal class EnumValueFilterHandler(private val className:String) : ListFilterHandler<ListFilterEnumValuesDTJS, EnumMultiValuesWidget<FakeEnumJS>> {

    override fun createEditor(): EnumMultiValuesWidget<FakeEnumJS> {
        return EnumMultiValuesWidget {
            width = "100%"
            enumClassName = className
            showClearIcon = true
        }
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

    override fun createEditor(): EntityMultiValuesWidget {
        return EntityMultiValuesWidget{
            width = "100%"
            handler = AutocompleteHandler.createMetadataBasedAutocompleteHandler(className)
            showClearIcon = true
        }
    }

    override fun getValue(editor: EntityMultiValuesWidget): ListFilterEntityValuesDTJS? {
        val values = editor.getValues()
        if(values.isEmpty()){
            return null
        }
        val result = ListFilterEntityValuesDTJS()
        result.values.addAll(values)
        return result
    }

    override fun reset(editor: EntityMultiValuesWidget) {
        editor.setValues(emptyList())
    }

    override fun isNotEmpty(comp: EntityMultiValuesWidget): Boolean {
        return comp.getValues().isNotEmpty()
    }


}