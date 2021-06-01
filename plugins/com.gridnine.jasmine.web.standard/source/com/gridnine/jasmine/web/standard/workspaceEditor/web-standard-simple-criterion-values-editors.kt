/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.standard.workspaceEditor

import com.gridnine.jasmine.common.core.model.FakeEnumJS
import com.gridnine.jasmine.common.standard.model.rest.*
import com.gridnine.jasmine.web.core.reflection.ReflectionFactoryJS
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.components.BaseWebNodeWrapper
import com.gridnine.jasmine.web.core.ui.components.WebGridLayoutContainer
import com.gridnine.jasmine.web.standard.widgets.*


class WebNullValueEditor:WebSimpleCriterionValueEditor<BaseWorkspaceSimpleCriterionValueDTJS>,BaseWebNodeWrapper<WebGridLayoutContainer>(){
    init {
        _node = WebUiLibraryAdapter.get().createGridContainer{
            width = "100%"
        }
    }
    override fun getType(): WebSimpleCriterionValueType {
        return WebSimpleCriterionValueType.NULL
    }

    override fun setValue(value: BaseWorkspaceSimpleCriterionValueDTJS?) {
        //null
    }

    override fun getValue(): BaseWorkspaceSimpleCriterionValueDTJS? {
        return null
    }

}


class WebStringValuesValueEditor:WebSimpleCriterionValueEditor<WorkspaceSimpleCriterionStringValuesDTJS>,BaseWebNodeWrapper<TextBoxWidget>(){

    init {
        _node = TextBoxWidget {
            width ="100%"
        }
    }
    override fun getType(): WebSimpleCriterionValueType {
        return WebSimpleCriterionValueType.STRING_VALUES
    }

    override fun setValue(value: WorkspaceSimpleCriterionStringValuesDTJS?) {
        _node.setValue(value?.values?.joinToString(","))
    }

    override fun getValue(): WorkspaceSimpleCriterionStringValuesDTJS? {
        return _node.getValue()?.let {
            val result = WorkspaceSimpleCriterionStringValuesDTJS()
            result.values.addAll(it.split(","))
            result
        }
    }

}

@Suppress("UNCHECKED_CAST")
class WebEnumValuesValueEditor(private val clsName:String):WebSimpleCriterionValueEditor<WorkspaceSimpleCriterionEnumValuesDTJS>,BaseWebNodeWrapper<EnumMultiValuesWidget<FakeEnumJS>>(){
    init {
        _node = EnumMultiValuesWidget{
            enumClassName = clsName
            width="100%"
        }
    }
    override fun getType(): WebSimpleCriterionValueType {
        return WebSimpleCriterionValueType.STRING_VALUES
    }

    override fun setValue(value: WorkspaceSimpleCriterionEnumValuesDTJS?) {
        _node.setValues((value?.values?.map { ReflectionFactoryJS.get().getEnum<FakeEnumJS>(clsName, it)})?: emptyList())
    }

    override fun getValue(): WorkspaceSimpleCriterionEnumValuesDTJS? {
        val values = _node.getValues()
        if(values.isEmpty()){
            return null
        }
        val result = WorkspaceSimpleCriterionEnumValuesDTJS()
        result.enumClassName = clsName
        result.values.addAll(_node.getValues().map { it.name })
        return result
    }
}


class WebLongValueEditor:WebSimpleCriterionValueEditor<WorkspaceSimpleCriterionLongValueDTJS>,BaseWebNodeWrapper<IntegerNumberBoxWidget>(){
    
    init {
        _node = IntegerNumberBoxWidget {
            width="100%"
        }
    }
    override fun getType(): WebSimpleCriterionValueType {
        return WebSimpleCriterionValueType.LONG_VALUE
    }

    override fun setValue(value: WorkspaceSimpleCriterionLongValueDTJS?) {
        _node.setValue(value?.value?.toInt())
    }

    override fun getValue(): WorkspaceSimpleCriterionLongValueDTJS? {
        return _node.getValue()?.let {
            val result = WorkspaceSimpleCriterionLongValueDTJS()
            result.value = it.toLong()
            result
        }
    }

}

class WebIntValueEditor:WebSimpleCriterionValueEditor<WorkspaceSimpleCriterionIntValueDTJS>,BaseWebNodeWrapper<IntegerNumberBoxWidget>(){
    init {
        _node = IntegerNumberBoxWidget {
            width="100%"
        }
    }
    override fun getType(): WebSimpleCriterionValueType {
        return WebSimpleCriterionValueType.INT_VALUE
    }

    override fun setValue(value: WorkspaceSimpleCriterionIntValueDTJS?) {
        _node.setValue(value?.value)
    }

    override fun getValue(): WorkspaceSimpleCriterionIntValueDTJS? {
        return _node.getValue()?.let {
            val result = WorkspaceSimpleCriterionIntValueDTJS()
            result.value = it
            result
        }
    }
}

class WebFloatValueEditor:WebSimpleCriterionValueEditor<WorkspaceSimpleCriterionFloatValueDTJS>,BaseWebNodeWrapper<FloatNumberBoxWidget>(){

    init {
        _node = FloatNumberBoxWidget {
            width = "100%"
        }
    }
    override fun getType(): WebSimpleCriterionValueType {
        return WebSimpleCriterionValueType.BIG_DECIMAL_VALUE
    }

    override fun setValue(value: WorkspaceSimpleCriterionFloatValueDTJS?) {
        _node.setValue(value?.value)
    }

    override fun getValue(): WorkspaceSimpleCriterionFloatValueDTJS? {
        return _node.getValue()?.let {
            val result = WorkspaceSimpleCriterionFloatValueDTJS()
            result.value = it
            result
        }
    }

}


class WebEntityReferenceValuesValueEditor(private val clsName:String):WebSimpleCriterionValueEditor<WorkspaceSimpleCriterionEntityValuesDTJS>,BaseWebNodeWrapper<EntityMultiValuesWidget>(){
    
    init {
        _node = EntityMultiValuesWidget{
            width="100%"
            handler = AutocompleteHandler.createMetadataBasedAutocompleteHandler(clsName)
        }
    }
    override fun getType(): WebSimpleCriterionValueType {
        return WebSimpleCriterionValueType.ENTITY_REFERENCE_VALUES
    }

    override fun setValue(value: WorkspaceSimpleCriterionEntityValuesDTJS?) {
        _node.setValues(value?.values?: emptyList())
    }

    override fun getValue(): WorkspaceSimpleCriterionEntityValuesDTJS? {
        val values = _node.getValues()
        if(values.isEmpty()){
            return null
        }
        val result = WorkspaceSimpleCriterionEntityValuesDTJS()
        result.values.addAll(_node.getValues())
        return result
    }

}

class WebDateValueEditor:WebSimpleCriterionValueEditor<WorkspaceSimpleCriterionDateValueDTJS>,BaseWebNodeWrapper<DateBoxWidget>(){
    
    init {
        _node = DateBoxWidget { 
            width = "100%"
        }
    }
    override fun getType(): WebSimpleCriterionValueType {
        return WebSimpleCriterionValueType.DATE_VALUE
    }

    override fun setValue(value: WorkspaceSimpleCriterionDateValueDTJS?) {
        _node.setValue(value?.value)
    }

    override fun getValue(): WorkspaceSimpleCriterionDateValueDTJS? {
        return _node.getValue()?.let {
            val result = WorkspaceSimpleCriterionDateValueDTJS()
            result.value = it
            result
        }
    }

}

class WebDateTimeValueEditor:WebSimpleCriterionValueEditor<WorkspaceSimpleCriterionDateTimeValueDTJS>,BaseWebNodeWrapper<DateTimeBoxWidget>(){

    init {
        _node = DateTimeBoxWidget {
            width = "100%"
        }
    }
    override fun getType(): WebSimpleCriterionValueType {
        return WebSimpleCriterionValueType.DATE_TIME_VALUE
    }

    override fun setValue(value: WorkspaceSimpleCriterionDateTimeValueDTJS?) {
        _node.setValue(value?.value)
    }

    override fun getValue(): WorkspaceSimpleCriterionDateTimeValueDTJS? {
        return _node.getValue()?.let {
            val result = WorkspaceSimpleCriterionDateTimeValueDTJS()
            result.value = it
            result
        }
    }


}


class WebDateIntervalEditor:WebSimpleCriterionValueEditor<WorkspaceSimpleCriterionDateIntervalValueDTJS>,BaseWebNodeWrapper<WebGridLayoutContainer>(){
    private val startDate:DateBoxWidget
    private val endDate:DateBoxWidget
    init {
        val startLabel = WebUiLibraryAdapter.get().createLabel{}
        startLabel.setText("с")
        startDate = DateBoxWidget {
            width="100%"
        }
        val endLabel = WebUiLibraryAdapter.get().createLabel{}
        endLabel.setText("по")
        endDate = DateBoxWidget {
            width="100%"
        }
        _node = WebUiLibraryAdapter.get().createGridContainer{
            width="100%"
            noPadding = true
            column("auto")
            column("100%")
            row{
                cell(startLabel)
                cell(startDate)
            }
            row{
                cell(endLabel)
                cell(endDate)
            }
        }
    }
    override fun getType(): WebSimpleCriterionValueType {
        return WebSimpleCriterionValueType.DATE_INTERVAL
    }

    override fun setValue(value: WorkspaceSimpleCriterionDateIntervalValueDTJS?) {
        if(value == null){
            return
        }
        startDate.setValue(value.startDate)
        endDate.setValue(value.endDate)
    }

    override fun getValue(): WorkspaceSimpleCriterionDateIntervalValueDTJS? {
        val std = startDate.getValue()
        val ed = endDate.getValue()
        if(std == null && ed == null){
            return null
        }
        val result = WorkspaceSimpleCriterionDateIntervalValueDTJS()
        result.startDate = std
        result.endDate = ed
        return result
    }



}

class WebDateTimeIntervalEditor:WebSimpleCriterionValueEditor<WorkspaceSimpleCriterionDateTimeIntervalValueDTJS>,BaseWebNodeWrapper<WebGridLayoutContainer>(){
    private val startDate:DateTimeBoxWidget
    private val endDate:DateTimeBoxWidget
    init {
        val startLabel = WebUiLibraryAdapter.get().createLabel{}
        startLabel.setText("с")
        startDate = DateTimeBoxWidget {
            width="100%"
        }
        val endLabel = WebUiLibraryAdapter.get().createLabel{}
        endLabel.setText("по")
        endDate = DateTimeBoxWidget {
            width="100%"
        }
        _node = WebUiLibraryAdapter.get().createGridContainer{
            width="100%"
            noPadding = true
            column("auto")
            column("100%")
            row{
                cell(startLabel)
                cell(startDate)
            }
            row{
                cell(endLabel)
                cell(endDate)
            }
        }
    }
    override fun getType(): WebSimpleCriterionValueType {
        return WebSimpleCriterionValueType.DATE_INTERVAL
    }

    override fun setValue(value: WorkspaceSimpleCriterionDateTimeIntervalValueDTJS?) {
        if(value == null){
            return
        }
        startDate.setValue(value.startDate)
        endDate.setValue(value.endDate)
    }

    override fun getValue(): WorkspaceSimpleCriterionDateTimeIntervalValueDTJS? {
        val std = startDate.getValue()
        val ed = endDate.getValue()
        if(std == null && ed == null){
            return null
        }
        val result = WorkspaceSimpleCriterionDateTimeIntervalValueDTJS()
        result.startDate = std
        result.endDate = ed
        return result
    }



}
