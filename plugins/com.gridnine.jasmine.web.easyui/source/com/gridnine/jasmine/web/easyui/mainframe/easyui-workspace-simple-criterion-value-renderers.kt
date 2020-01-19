/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.easyui.mainframe

import com.gridnine.jasmine.server.sandbox.model.domain.WorkspaceSimpleCriterionEnumValuesDTJS
import com.gridnine.jasmine.server.standard.model.rest.*
import com.gridnine.jasmine.web.core.model.common.FakeEnumJS
import com.gridnine.jasmine.web.core.model.domain.DomainMetaRegistryJS
import com.gridnine.jasmine.web.core.model.domain.EntityReferenceJS
import com.gridnine.jasmine.web.core.model.ui.*
import com.gridnine.jasmine.web.core.utils.ReflectionFactoryJS
import com.gridnine.jasmine.web.core.utils.TextUtilsJS
import com.gridnine.jasmine.web.easyui.widgets.*

class EasyUiCriterionStringValueRenderer:EasyUiCriterionValueRenderer<WorkspaceSimpleCriterionStringValuesDTJS>{
    private val uid = TextUtilsJS.createUUID()

    private lateinit var widget:EasyUiTextBoxWidget
    override fun getContent(): String {
        return "<input id = \"${uid}Control\" style = \"width:100%\">"
    }

    override fun decorate() {

        widget = EasyUiTextBoxWidget("${uid}Control", TextboxDescriptionJS(""))
        widget.configure(Unit)
    }

    override fun setData(value: WorkspaceSimpleCriterionStringValuesDTJS?) {
         widget.setData(value?.values?.joinToString())
    }

    override fun getData(): WorkspaceSimpleCriterionStringValuesDTJS?{
        return widget.getData()?.let {
            val res = WorkspaceSimpleCriterionStringValuesDTJS()
            res.values.addAll(it.split(","))
            res
        }
    }
}

class EasyUiCriterionDateValueRenderer:EasyUiCriterionValueRenderer<WorkspaceSimpleCriterionDateValueDTJS>{
    private val uid = TextUtilsJS.createUUID()

    private lateinit var widget:DateBoxWidget
    override fun getContent(): String {
        return "<input id = \"${uid}Control\" style = \"width:100%\">"
    }

    override fun decorate() {

        widget = EasyUiDateBoxWidget("${uid}Control", DateboxDescriptionJS(""))
        widget.configure(Unit)
    }

    override fun setData(value: WorkspaceSimpleCriterionDateValueDTJS?) {
        widget.setData(value?.value)
    }

    override fun getData(): WorkspaceSimpleCriterionDateValueDTJS?{
        return widget.getData()?.let {
            val res = WorkspaceSimpleCriterionDateValueDTJS()
            res.value = it
            res
        }
    }
}

class EasyUiCriterionDateIntervalValueRenderer:EasyUiCriterionValueRenderer<WorkspaceSimpleCriterionDateIntervalValueDTJS>{
    private val uid = TextUtilsJS.createUUID()

    private lateinit var startWidget:DateBoxWidget
    private lateinit var endWidget:DateBoxWidget
    override fun getContent(): String {
        return "<input id = \"${uid}StartControl\" style = \"width:130px\"><nobr><input id = \"${uid}EndControl\" style = \"width:130px\">"
    }

    override fun decorate() {

        startWidget = EasyUiDateBoxWidget("${uid}StartControl", DateboxDescriptionJS(""))
        startWidget.configure(Unit)
        endWidget = EasyUiDateBoxWidget("${uid}EndControl", DateboxDescriptionJS(""))
        endWidget.configure(Unit)
    }

    override fun setData(value: WorkspaceSimpleCriterionDateIntervalValueDTJS?) {
        startWidget.setData(value?.startDate)
        endWidget.setData(value?.endDate)
    }

    override fun getData(): WorkspaceSimpleCriterionDateIntervalValueDTJS?{
        val startDate = startWidget.getData()
        val endDate = endWidget.getData()
        if(startDate == null&&endDate == null){
            return null
        }
        val res = WorkspaceSimpleCriterionDateIntervalValueDTJS()
        res.startDate = startDate
        res.endDate = endDate
        return res
    }
}

class EasyUiCriterionDateTimeValueRenderer:EasyUiCriterionValueRenderer<WorkspaceSimpleCriterionDateTimeValueDTJS>{
    private val uid = TextUtilsJS.createUUID()

    private lateinit var widget:DateTimeBoxWidget
    override fun getContent(): String {
        return "<input id = \"${uid}Control\" style = \"width:100%\">"
    }

    override fun decorate() {

        widget = EasyUiDateTimeBoxWidget("${uid}Control", DateTimeBoxDescriptionJS(""))
        widget.configure(Unit)
    }

    override fun setData(value: WorkspaceSimpleCriterionDateTimeValueDTJS?) {
        widget.setData(value?.value)
    }

    override fun getData(): WorkspaceSimpleCriterionDateTimeValueDTJS?{
        return widget.getData()?.let {
            val res = WorkspaceSimpleCriterionDateTimeValueDTJS()
            res.value = it
            res
        }
    }
}

class EasyUiCriterionDateTimeIntervalValueRenderer:EasyUiCriterionValueRenderer<WorkspaceSimpleCriterionDateTimeIntervalValueDTJS>{
    private val uid = TextUtilsJS.createUUID()

    private lateinit var startWidget:DateTimeBoxWidget
    private lateinit var endWidget:DateTimeBoxWidget
    override fun getContent(): String {
        return "<input id = \"${uid}StartControl\" style = \"width:150px\"><nobr><input id = \"${uid}EndControl\" style = \"width:150px\">"
    }

    override fun decorate() {

        startWidget = EasyUiDateTimeBoxWidget("${uid}StartControl", DateTimeBoxDescriptionJS(""))
        startWidget.configure(Unit)
        endWidget = EasyUiDateTimeBoxWidget("${uid}EndControl", DateTimeBoxDescriptionJS(""))
        endWidget.configure(Unit)
    }

    override fun setData(value: WorkspaceSimpleCriterionDateTimeIntervalValueDTJS?) {
        startWidget.setData(value?.startDate)
        endWidget.setData(value?.endDate)
    }

    override fun getData(): WorkspaceSimpleCriterionDateTimeIntervalValueDTJS?{
        val startDate = startWidget.getData()
        val endDate = endWidget.getData()
        if(startDate == null&&endDate == null){
            return null
        }
        val res = WorkspaceSimpleCriterionDateTimeIntervalValueDTJS()
        res.startDate = startDate
        res.endDate = endDate
        return res
    }
}

class EasyUiCriterionFloatValueRenderer:EasyUiCriterionValueRenderer<WorkspaceSimpleCriterionFloatValueDTJS>{
    private val uid = TextUtilsJS.createUUID()

    private lateinit var widget:FloatBoxWidget
    override fun getContent(): String {
        return "<input id = \"${uid}Control\" style = \"width:100%\">"
    }

    override fun decorate() {

        widget = EasyUiFloatBoxWidget("${uid}Control", FloatBoxDescriptionJS("",true))
        widget.configure(Unit)
    }

    override fun setData(value: WorkspaceSimpleCriterionFloatValueDTJS?) {
        widget.setData(value?.value)
    }

    override fun getData(): WorkspaceSimpleCriterionFloatValueDTJS?{
        return widget.getData()?.let {
            val res = WorkspaceSimpleCriterionFloatValueDTJS()
            res.value = it
            res
        }
    }
}

class EasyUiCriterionIntValueRenderer:EasyUiCriterionValueRenderer<WorkspaceSimpleCriterionIntValueDTJS>{
    private val uid = TextUtilsJS.createUUID()

    private lateinit var widget:IntegerBoxWidget
    override fun getContent(): String {
        return "<input id = \"${uid}Control\" style = \"width:100%\">"
    }

    override fun decorate() {

        widget = EasyUiIntBoxWidget("${uid}Control", IntegerBoxDescriptionJS("",true))
        widget.configure(Unit)
    }

    override fun setData(value: WorkspaceSimpleCriterionIntValueDTJS?) {
        widget.setData(value?.value)
    }

    override fun getData(): WorkspaceSimpleCriterionIntValueDTJS?{
        return widget.getData()?.let {
            val res = WorkspaceSimpleCriterionIntValueDTJS()
            res.value = it
            res
        }
    }
}

class EasyUiCriterionEnumValuesRenderer(private val enumId:String):EasyUiCriterionValueRenderer<WorkspaceSimpleCriterionEnumValuesDTJS>{
    private val uid = TextUtilsJS.createUUID()

    private lateinit var widget:EnumMultiSelectWidget<FakeEnumJS>
    override fun getContent(): String {
        return "<input id = \"${uid}Control\" style = \"width:100%\">"
    }

    override fun decorate() {

        widget = EasyUiEnumMultiSelectWidget("${uid}Control", EnumSelectDescriptionJS("",enumId))
        val config = EnumSelectConfigurationJS<FakeEnumJS>()
        config.nullAllowed = false
        widget.configure(config)
    }

    override fun setData(value: WorkspaceSimpleCriterionEnumValuesDTJS?) {
        if(value == null){
            widget.readData(emptyList())
            return
        }
        widget.readData(value.values.map { ReflectionFactoryJS.get().getEnum<FakeEnumJS>(value.enumClassName!!, it) })
    }

    override fun getData(): WorkspaceSimpleCriterionEnumValuesDTJS?{
        val data  = arrayListOf<FakeEnumJS>()
        widget.writeData(data)
        if(data.isEmpty()){
            return null
        }
        val res = WorkspaceSimpleCriterionEnumValuesDTJS()
        res.enumClassName = enumId
        res.values.addAll(data.map { it.name })
        return res
    }
}

class EasyUiCriterionEntityValuesRenderer(private val entityClassName:String):EasyUiCriterionValueRenderer<WorkspaceSimpleCriterionEntityValuesDTJS>{
    private val uid = TextUtilsJS.createUUID()

    private lateinit var widget:EntityMultiSelectWidget
    override fun getContent(): String {
        return "<input id = \"${uid}Control\" style = \"width:100%\">"
    }

    override fun decorate() {
        widget = EasyUiEntityMultiSelectWidget("${uid}Control", EntitySelectDescriptionJS("",entityClassName))
        val config = EntitySelectConfigurationJS()
        config.limit = 10
        config.nullAllowed = false
        DomainMetaRegistryJS.get().indexes.values.filter { it.document  == entityClassName}.forEach {
            val dataSource = EntityAutocompleteDataSourceJS()
            dataSource.indexClassName = it.id
            dataSource.name = it.displayName
            config.dataSources.add(dataSource)
        }
        config.nullAllowed = false
        widget.configure(config)
    }

    override fun setData(value: WorkspaceSimpleCriterionEntityValuesDTJS?) {
        if(value == null){
            widget.readData(emptyList())
            return
        }
        widget.readData(value.values)
    }

    override fun getData(): WorkspaceSimpleCriterionEntityValuesDTJS?{
        val data  = arrayListOf<EntityReferenceJS>()
        widget.writeData(data)
        if(data.isEmpty()){
            return null
        }
        val res = WorkspaceSimpleCriterionEntityValuesDTJS()
        res.values.addAll(data)
        return res
    }
}
