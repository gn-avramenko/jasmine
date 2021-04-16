/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.standard.ui.mainframe.workspace

import com.gridnine.jasmine.common.core.model.BaseIdentity
import com.gridnine.jasmine.common.core.model.FakeEnum
import com.gridnine.jasmine.common.core.reflection.ReflectionFactory
import com.gridnine.jasmine.common.standard.model.domain.*
import com.gridnine.jasmine.server.core.ui.common.BaseNodeWrapper
import com.gridnine.jasmine.server.core.ui.components.GridLayoutCell
import com.gridnine.jasmine.server.core.ui.components.GridLayoutColumnConfiguration
import com.gridnine.jasmine.server.core.ui.components.GridLayoutContainer
import com.gridnine.jasmine.server.core.ui.components.UiLibraryAdapter
import com.gridnine.jasmine.server.core.ui.widgets.*
import java.util.*


class NullValueEditor:SimpleCriterionValueEditor<BaseWorkspaceSimpleCriterionValue>,BaseNodeWrapper<GridLayoutContainer>(){
    init {
        _node = UiLibraryAdapter.get().createGridLayoutContainer{
            width = "100%"
        }
    }
    override fun getType(): SimpleCriterionValueType {
        return SimpleCriterionValueType.NULL
    }

    override fun setValue(value: BaseWorkspaceSimpleCriterionValue?) {
        //nooops
    }

    override fun getValue(): BaseWorkspaceSimpleCriterionValue? {
        return null
    }

}


class StringValuesValueEditor:SimpleCriterionValueEditor<WorkspaceSimpleCriterionStringValues>,BaseNodeWrapper<TextBoxWidget>(){

    init {
        _node = TextBoxWidget {
            width ="100%"
        }
    }
    override fun getType(): SimpleCriterionValueType {
        return SimpleCriterionValueType.STRING_VALUES
    }

    override fun setValue(value: WorkspaceSimpleCriterionStringValues?) {
        _node.setValue(value?.values?.joinToString(","))
    }

    override fun getValue(): WorkspaceSimpleCriterionStringValues? {
        return _node.getValue()?.let {
            val result = WorkspaceSimpleCriterionStringValues()
            result.uid = UUID.randomUUID().toString()
            result.values.addAll(it.split(","))
            result
        }
    }

}

@Suppress("UNCHECKED_CAST")
class EnumValuesValueEditor(private val clsName:String):SimpleCriterionValueEditor<WorkspaceSimpleCriterionEnumValues>,BaseNodeWrapper<EnumMultiValuesWidget<FakeEnum>>(){
    init {
        _node = EnumMultiValuesWidget{
            enumClassName = clsName
            width="100%"
        }
    }
    override fun getType(): SimpleCriterionValueType {
        return SimpleCriterionValueType.ENUM_VALUES
    }

    override fun setValue(value: WorkspaceSimpleCriterionEnumValues?) {
        _node.setValues((value?.values?.map { ReflectionFactory.get().safeGetEnum(clsName, it)} as List<FakeEnum>?)?: emptyList())
    }

    override fun getValue(): WorkspaceSimpleCriterionEnumValues? {
        val values = _node.getValues()
        if(values.isEmpty()){
            return null
        }
        val result = WorkspaceSimpleCriterionEnumValues()
        result.uid = UUID.randomUUID().toString()
        result.enumClassName = clsName
        result.values.addAll(_node.getValues().map { it.name })
        return result
    }
}


class LongValueEditor:SimpleCriterionValueEditor<WorkspaceSimpleCriterionLongValue>,BaseNodeWrapper<IntBoxWidget>(){
    
    init {
        _node = IntBoxWidget {
            width="100%"
        }
    }
    override fun getType(): SimpleCriterionValueType {
        return SimpleCriterionValueType.LONG_VALUE
    }

    override fun setValue(value: WorkspaceSimpleCriterionLongValue?) {
        _node.setValue(value?.value?.toInt())
    }

    override fun getValue(): WorkspaceSimpleCriterionLongValue? {
        return _node.getValue()?.let {
            val result = WorkspaceSimpleCriterionLongValue()
            result.uid = UUID.randomUUID().toString()
            result.value = it.toLong()
            result
        }
    }

}

class IntValueEditor:SimpleCriterionValueEditor<WorkspaceSimpleCriterionIntValue>,BaseNodeWrapper<IntBoxWidget>(){
    init {
        _node = IntBoxWidget {
            width="100%"
        }
    }
    override fun getType(): SimpleCriterionValueType {
        return SimpleCriterionValueType.INT_VALUE
    }

    override fun setValue(value: WorkspaceSimpleCriterionIntValue?) {
        _node.setValue(value?.value)
    }

    override fun getValue(): WorkspaceSimpleCriterionIntValue? {
        return _node.getValue()?.let {
            val result = WorkspaceSimpleCriterionIntValue()
            result.uid = UUID.randomUUID().toString()
            result.value = it
            result
        }
    }
}

class FloatValueEditor:SimpleCriterionValueEditor<WorkspaceSimpleCriterionFloatValue>,BaseNodeWrapper<BigDecimalBoxWidget>(){

    init {
        _node = BigDecimalBoxWidget {
            width = "100%"
        }
    }
    override fun getType(): SimpleCriterionValueType {
        return SimpleCriterionValueType.BIG_DECIMAL_VALUE
    }

    override fun setValue(value: WorkspaceSimpleCriterionFloatValue?) {
        _node.setValue(value?.value)
    }

    override fun getValue(): WorkspaceSimpleCriterionFloatValue? {
        return _node.getValue()?.let {
            val result = WorkspaceSimpleCriterionFloatValue()
            result.uid = UUID.randomUUID().toString()
            result.value = it
            result
        }
    }

}


class EntityReferenceValuesValueEditor(private val clsName:String):SimpleCriterionValueEditor<WorkspaceSimpleCriterionEntityValues>,BaseNodeWrapper<EntityMultiValuesWidget<BaseIdentity>>(){
    
    init {
        _node = EntityMultiValuesWidget{
            width="100%"
            handler = AutocompleteHandler.createMetadataBasedAutocompleteHandler(clsName)
        }
    }
    override fun getType(): SimpleCriterionValueType {
        return SimpleCriterionValueType.ENTITY_REFERENCE_VALUES
    }

    override fun setValue(value: WorkspaceSimpleCriterionEntityValues?) {
        _node.setValues(value?.values?: emptyList())
    }

    override fun getValue(): WorkspaceSimpleCriterionEntityValues? {
        val values = _node.getValues()
        if(values.isEmpty()){
            return null
        }
        val result = WorkspaceSimpleCriterionEntityValues()
        result.uid = UUID.randomUUID().toString()
        result.values.addAll(_node.getValues())
        return result
    }

}

class DateValueEditor:SimpleCriterionValueEditor<WorkspaceSimpleCriterionDateValue>,BaseNodeWrapper<DateBoxWidget>(){
    
    init {
        _node = DateBoxWidget { 
            width = "100%"
        }
    }
    override fun getType(): SimpleCriterionValueType {
        return SimpleCriterionValueType.DATE_VALUE
    }

    override fun setValue(value: WorkspaceSimpleCriterionDateValue?) {
        _node.setValue(value?.value)
    }

    override fun getValue(): WorkspaceSimpleCriterionDateValue? {
        return _node.getValue()?.let {
            val result = WorkspaceSimpleCriterionDateValue()
            result.uid = UUID.randomUUID().toString()
            result.value = it
            result
        }
    }

}

class DateTimeValueEditor:SimpleCriterionValueEditor<WorkspaceSimpleCriterionDateTimeValue>,BaseNodeWrapper<DateTimeBoxWidget>(){

    init {
        _node = DateTimeBoxWidget {
            width = "100%"
        }
    }
    override fun getType(): SimpleCriterionValueType {
        return SimpleCriterionValueType.DATE_TIME_VALUE
    }

    override fun setValue(value: WorkspaceSimpleCriterionDateTimeValue?) {
        _node.setValue(value?.value)
    }

    override fun getValue(): WorkspaceSimpleCriterionDateTimeValue? {
        return _node.getValue()?.let {
            val result = WorkspaceSimpleCriterionDateTimeValue()
            result.uid = UUID.randomUUID().toString()
            result.value = it
            result
        }
    }


}


class DateIntervalEditor:SimpleCriterionValueEditor<WorkspaceSimpleCriterionDateIntervalValue>,BaseNodeWrapper<GridLayoutContainer>(){
    private val startDate:DateBoxWidget
    private val endDate:DateBoxWidget
    init {
        _node = UiLibraryAdapter.get().createGridLayoutContainer{
            width="100%"
            noPadding = true
            columns.add(GridLayoutColumnConfiguration("auto"))
            columns.add(GridLayoutColumnConfiguration("90%"))
        }
        _node.addRow()
        val startLabel = UiLibraryAdapter.get().createLabel{}
        startLabel.setText("с")
        _node.addCell(GridLayoutCell(startLabel))
        startDate = DateBoxWidget {
            width="100%"
        }
        _node.addCell(GridLayoutCell(startDate))
        _node.addRow()
        val endLabel = UiLibraryAdapter.get().createLabel{}
        endLabel.setText("по")
        _node.addCell(GridLayoutCell(endLabel))
        endDate = DateBoxWidget {
            width="100%"
        }
        _node.addCell(GridLayoutCell(endDate))
    }
    override fun getType(): SimpleCriterionValueType {
        return SimpleCriterionValueType.DATE_INTERVAL
    }

    override fun setValue(value: WorkspaceSimpleCriterionDateIntervalValue?) {
        if(value == null){
            return
        }
        startDate.setValue(value.startDate)
        endDate.setValue(value.endDate)
    }

    override fun getValue(): WorkspaceSimpleCriterionDateIntervalValue? {
        val std = startDate.getValue()
        val ed = endDate.getValue()
        if(std == null && ed == null){
            return null
        }
        val result = WorkspaceSimpleCriterionDateIntervalValue()
        result.uid = UUID.randomUUID().toString()
        result.startDate = std
        result.endDate = ed
        return result
    }



}

class DateTimeIntervalEditor:SimpleCriterionValueEditor<WorkspaceSimpleCriterionDateTimeIntervalValue>,BaseNodeWrapper<GridLayoutContainer>(){
    private val startDate:DateTimeBoxWidget
    private val endDate:DateTimeBoxWidget
    init {
        _node = UiLibraryAdapter.get().createGridLayoutContainer{
            width="100%"
            noPadding = true
            columns.add(GridLayoutColumnConfiguration("auto"))
            columns.add(GridLayoutColumnConfiguration("90%"))
        }
        _node.addRow()
        val startLabel = UiLibraryAdapter.get().createLabel{}
        startLabel.setText("с")
        _node.addCell(GridLayoutCell(startLabel))
        startDate = DateTimeBoxWidget {
            width="100%"
        }
        _node.addCell(GridLayoutCell(startDate))
        _node.addRow()
        val endLabel = UiLibraryAdapter.get().createLabel{}
        endLabel.setText("по")
        _node.addCell(GridLayoutCell(endLabel))
        endDate = DateTimeBoxWidget {
            width="100%"
        }
        _node.addCell(GridLayoutCell(endDate))
    }
    override fun getType(): SimpleCriterionValueType {
        return SimpleCriterionValueType.DATE_INTERVAL
    }

    override fun setValue(value: WorkspaceSimpleCriterionDateTimeIntervalValue?) {
        if(value == null){
            return
        }
        startDate.setValue(value.startDate)
        endDate.setValue(value.endDate)
    }

    override fun getValue(): WorkspaceSimpleCriterionDateTimeIntervalValue? {
        val std = startDate.getValue()
        val ed = endDate.getValue()
        if(std == null && ed == null){
            return null
        }
        val result = WorkspaceSimpleCriterionDateTimeIntervalValue()
        result.uid = UUID.randomUUID().toString()
        result.startDate = std
        result.endDate = ed
        return result
    }



}