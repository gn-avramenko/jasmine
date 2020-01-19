/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.easyui.mainframe

import com.gridnine.jasmine.server.standard.model.rest.*
import com.gridnine.jasmine.web.core.model.common.FakeEnumJS
import com.gridnine.jasmine.web.core.model.domain.DomainMetaRegistryJS
import com.gridnine.jasmine.web.core.model.domain.EntityReferenceJS
import com.gridnine.jasmine.web.core.model.ui.*
import com.gridnine.jasmine.web.easyui.widgets.*

interface EasyUiListFilter<T:BaseListFilterValueDTJS> {
    fun getFilterValue():T?
    fun resetFilter()
    fun getFieldId():String
}

internal class EasyUiListStringFilter(private val field:String, controlId:String):EasyUiListFilter<ListFilterStringValuesDTJS>{
    private val widget:TextBoxWidget = EasyUiTextBoxWidget(controlId, TextboxDescriptionJS(""))

    init {
        widget.configure(Unit)
    }
    override fun getFilterValue(): ListFilterStringValuesDTJS? {
        val value = widget.getData()
        return value?.let{
            val result = ListFilterStringValuesDTJS()
            result.values.addAll(it.split(","))
            result
        }
    }

    override fun resetFilter() {
        widget.setData(null)
    }

    override fun getFieldId(): String {
        return field
    }

}

internal class EasyUiListLocalDateFilter(private val field:String, fromControlId:String, toControlId:String):EasyUiListFilter<ListFilterDateIntervalValueDTJS>{
    private val fromWidget:DateBoxWidget = EasyUiDateBoxWidget(fromControlId, DateboxDescriptionJS(""))
    private val toWidget:DateBoxWidget = EasyUiDateBoxWidget(toControlId, DateboxDescriptionJS(""))

    init {
        fromWidget.configure(Unit)
        toWidget.configure(Unit)
    }

    override fun getFilterValue(): ListFilterDateIntervalValueDTJS? {
        val fromValue = fromWidget.getData()
        val toValue = toWidget.getData()
        if(fromValue == null && toValue == null){
            return null
        }
        val result = ListFilterDateIntervalValueDTJS()
        result.startDate = fromValue
        result.endDate = toValue
        return result

    }

    override fun resetFilter() {
        fromWidget.setData(null)
        toWidget.setData(null)
    }

    override fun getFieldId(): String {
        return field
    }

}

internal class EasyUiListLocalDateTimeFilter(private val field:String, fromControlId:String, toControlId:String):EasyUiListFilter<ListFilterDateTimeIntervalValueDTJS>{
    private val fromWidget:DateTimeBoxWidget = EasyUiDateTimeBoxWidget(fromControlId, DateTimeBoxDescriptionJS(""))
    private val toWidget:DateTimeBoxWidget = EasyUiDateTimeBoxWidget(toControlId, DateTimeBoxDescriptionJS(""))
    init {
        fromWidget.configure(Unit)
        toWidget.configure(Unit)
    }
    override fun getFilterValue(): ListFilterDateTimeIntervalValueDTJS? {
        val fromValue = fromWidget.getData()
        val toValue = toWidget.getData()
        if(fromValue == null && toValue == null){
            return null
        }
        val result = ListFilterDateTimeIntervalValueDTJS()
        result.startDate = fromValue
        result.endDate = toValue
        return result

    }

    override fun resetFilter() {
        fromWidget.setData(null)
        toWidget.setData(null)
    }

    override fun getFieldId(): String {
        return field
    }

}

internal class EasyUiListEnumFilter(private val field:String, private  val enumId:String, controlId:String):EasyUiListFilter<ListFilterEnumValuesDTJS>{
    private val widget:EnumMultiSelectWidget<FakeEnumJS> = EasyUiEnumMultiSelectWidget(controlId, EnumSelectDescriptionJS("", enumId))
    init {
        val config = EnumSelectConfigurationJS<FakeEnumJS>()
        config.nullAllowed = false
        widget.configure(config)
    }

    override fun getFilterValue(): ListFilterEnumValuesDTJS? {
        val values = arrayListOf<FakeEnumJS>()
        widget.writeData(values)
        if(values.isEmpty()){
            return null
        }
        val result = ListFilterEnumValuesDTJS()
        result.enumClassName = enumId
        result.values.addAll(values.map { it.name })
        return result
    }

    override fun resetFilter() {
        widget.readData(emptyList())
    }

    override fun getFieldId(): String {
        return field
    }

}

internal class EasyUiListBooleanFilter(private val field:String,  controlId:String):EasyUiListFilter<ListFilterBooleanValuesDTJS>{
    private val widget:SelectWidget
    init {
        val config = SelectConfigurationJS()
        config.nullAllowed = true
        config.possibleValues.addAll(arrayListOf(SelectItemJS("NOT_SET", "Не задано"),SelectItemJS("TRUE", "Истина"),SelectItemJS("FALSE", "Ложь")))
        widget = EasyUiSelectWidget(controlId, SelectDescriptionJS(""))
        widget.configure(config)
    }



    override fun getFilterValue(): ListFilterBooleanValuesDTJS? {
        val value = widget.getData()
        val result = ListFilterBooleanValuesDTJS()
        if(value?.id == null){
            return null
        }
        result.value = when(value.id){
            "FALSE" -> false
            "TRUE" -> true
            else -> null
        }
        return result
    }

    override fun resetFilter() {
        widget.setData(null)
    }

    override fun getFieldId(): String {
        return field
    }

}


internal class EasyUiListEntityFilter(private val field:String, private  val className:String, controlId:String):EasyUiListFilter<ListFilterEntityValuesDTJS>{
    private val widget:EntityMultiSelectWidget = EasyUiEntityMultiSelectWidget(controlId, EntitySelectDescriptionJS("", className))

    init {
        val config = EntitySelectConfigurationJS()
        config.limit = 10
        config.nullAllowed = false
        DomainMetaRegistryJS.get().indexes.values.filter { it.document  == className}.forEach {
            val dataSource = EntityAutocompleteDataSourceJS()
            dataSource.indexClassName = it.id
            dataSource.name = it.displayName
            config.dataSources.add(dataSource)
        }
        config.nullAllowed = false
        widget.configure(config)
    }

    override fun getFilterValue(): ListFilterEntityValuesDTJS? {
        val data = arrayListOf<EntityReferenceJS>()
        widget.writeData(data)
        if(data.isEmpty()){
            return null
        }
        val result = ListFilterEntityValuesDTJS()
        result.values.addAll(data)
        return result
    }

    override fun resetFilter() {
        widget.readData(emptyList())
    }

    override fun getFieldId(): String {
        return field
    }

}


internal class EasyUiListIntFilter(private val field:String, fromControlId:String, toControlId:String):EasyUiListFilter<ListFilterIntIntervalValueDTJS>{
    private val fromWidget:IntegerBoxWidget = EasyUiIntBoxWidget(fromControlId, IntegerBoxDescriptionJS("", false))
    private val toWidget:IntegerBoxWidget = EasyUiIntBoxWidget(toControlId, IntegerBoxDescriptionJS("", false))
    init{
        fromWidget.configure(Unit)
        toWidget.configure(Unit)
    }
    override fun getFilterValue(): ListFilterIntIntervalValueDTJS? {
        val fromValue = fromWidget.getData()
        val toValue = toWidget.getData()
        if(fromValue == null && toValue == null){
            return null
        }
        val result = ListFilterIntIntervalValueDTJS()
        result.fromValue = fromValue
        result.toValue= toValue
        return result

    }

    override fun resetFilter() {
        fromWidget.setData(null)
        toWidget.setData(null)
    }

    override fun getFieldId(): String {
        return field
    }

}
internal class EasyUiListFloatFilter(private val field:String, fromControlId:String, toControlId:String):EasyUiListFilter<ListFilterFloatIntervalValueDTJS>{
    private val fromWidget:FloatBoxWidget = EasyUiFloatBoxWidget(fromControlId, FloatBoxDescriptionJS("", false))
    private val toWidget:FloatBoxWidget = EasyUiFloatBoxWidget(toControlId, FloatBoxDescriptionJS("", false))

    init {
        fromWidget.configure(Unit)
        toWidget.configure(Unit)
    }
    override fun getFilterValue(): ListFilterFloatIntervalValueDTJS? {
        val fromValue = fromWidget.getData()
        val toValue = toWidget.getData()
        if(fromValue == null && toValue == null){
            return null
        }
        val result = ListFilterFloatIntervalValueDTJS()
        result.fromValue = fromValue
        result.toValue= toValue
        return result

    }

    override fun resetFilter() {
        fromWidget.setData(null)
        toWidget.setData(null)
    }

    override fun getFieldId(): String {
        return field
    }

}