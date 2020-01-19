/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.easyui.mainframe

import com.gridnine.jasmine.server.standard.model.rest.BaseWorkspaceSimpleCriterionValueDTJS
import com.gridnine.jasmine.server.standard.model.rest.SimpleWorkspaceCriterionDTJS
import com.gridnine.jasmine.server.standard.model.rest.WorkspaceSimpleCriterionConditionDTJS
import com.gridnine.jasmine.web.core.model.domain.BaseIndexDescriptionJS
import com.gridnine.jasmine.web.core.model.domain.DatabaseCollectionTypeJS
import com.gridnine.jasmine.web.core.model.domain.DatabasePropertyTypeJS
import com.gridnine.jasmine.web.core.model.ui.SelectConfigurationJS
import com.gridnine.jasmine.web.core.model.ui.SelectDescriptionJS
import com.gridnine.jasmine.web.core.model.ui.SelectItemJS
import com.gridnine.jasmine.web.core.model.ui.SelectWidget
import com.gridnine.jasmine.web.core.utils.TextUtilsJS
import com.gridnine.jasmine.web.core.utils.HtmlUtilsJS
import com.gridnine.jasmine.web.easyui.jQuery
import com.gridnine.jasmine.web.easyui.widgets.EasyUiSelectWidget


class SimpleCriterionHandler(private val listId: String, private val indent:Int) : CriterionHandler<SimpleWorkspaceCriterionDTJS> {

    val uid = TextUtilsJS.createUUID()

    private lateinit var propertyWidget: SelectWidget

    private var conditionWidget: SelectWidget? = null

    private var valueWidget: EasyUiCriterionValueRenderer<BaseWorkspaceSimpleCriterionValueDTJS>? = null

    private val propertyPossibleValues = EasyUiWorkspaceListEditor.getPossibleFieldValues(listId)

    override fun getUid(): String {
        return uid
    }

    override fun getContent(): String {
        return HtmlUtilsJS.html {
            table(id = "${uid}Table", style = "width:100%;border-collapse: collapse") {
                tr {
                    td(style = "width:${CriterionsContainerEditor.propertyFieldWidth-indent}px;border:1px solid  #D3D3D3;padding:0px") { "<input style=\"width:100%\" id = \"${uid}Property\"/>"() }
                    td(style = "width:${CriterionsContainerEditor.conditionFieldWidth}px;border:1px solid #D3D3D3;padding:0px") { "<input style=\"width:100%\" id = \"${uid}Condition\"/>"() }
                    td(style = "border:1px solid #D3D3D3;padding:0px", id = "${uid}Value" ) { }
                    td(style = "width:${CriterionsContainerEditor.controlWidth}px") {
                        ("<div style=\"display:inline;width:15px;height:30px\" id=\"${uid}Up\" class = \"jasmine-datagrid-sort-asc\"></div>" +
                                "<div style=\"display:inline;width:15px;height:30px\" id=\"${uid}Down\" class = \"jasmine-datagrid-sort-desc\"></div>" +
                                "<div style=\"display:inline;width:15px;height:30px\" id=\"${uid}Add\" class = \"jasmine-datagrid-expand\"></div>" +
                                "<div style=\"display:inline;width:15px;height:30px\" id=\"${uid}Remove\" class = \"jasmine-datagrid-collapse\"></div>"
                                )()
                    }
                }
            }

        }.toString()
    }

    override fun decorate() {
        val propertyDescription = SelectDescriptionJS("")
        propertyWidget = EasyUiSelectWidget("${uid}Property", propertyDescription)
        val propertyConfig = SelectConfigurationJS()
        propertyConfig.nullAllowed = false
        propertyConfig.possibleValues.addAll(propertyPossibleValues)
        propertyWidget.configure(propertyConfig)

        propertyWidget.valueChangeListener = { newValue, _ ->
            val conditionDescr = SelectDescriptionJS("")
            conditionWidget = EasyUiSelectWidget("${uid}Condition", conditionDescr)
            val conditionConfig = SelectConfigurationJS()
            conditionConfig.nullAllowed = false
            if(newValue != null) {
                val conditions = getConditions(getPropertyType(newValue.id!!, EasyUiWorkspaceListEditor.getListDescription(listId)!!))
                conditionConfig.possibleValues.addAll(conditions.map { SelectItemJS(it.name, it.toString()) }.sortedBy { it.caption }.toList())
            }
            val cw1 = conditionWidget!!
            cw1.configure(conditionConfig)
            jQuery("#${uid}Value").empty()
            cw1.valueChangeListener = { _, _ ->
                val property = propertyWidget.getData()
                val condition = conditionWidget?.getData?.invoke()
                valueWidget = null
                jQuery("#${uid}Value").empty()
                if(property != null && condition != null){
                    val propertyType = getPropertyType(property.id!!, EasyUiWorkspaceListEditor.getListDescription(listId)!!)
                    val listDescr  = EasyUiWorkspaceListEditor.getListDescription(listId)?:throw IllegalArgumentException("no description found for id $listId")
                    val className = if(listDescr.properties.containsKey(property.id!!)){
                        listDescr.properties[property.id!!]!!.className
                    } else {
                        listDescr.collections[property.id!!]!!.elementClassName
                    }
                    valueWidget = getValueWidget(propertyType,EasyUiListCondition.valueOf(condition.id!!), className) as EasyUiCriterionValueRenderer<BaseWorkspaceSimpleCriterionValueDTJS>?
                    valueWidget?.let {
                        jQuery("#${uid}Value").html(it.getContent())
                        it.decorate()
                    }
                }
            }
        }
    val conditionDescr = SelectDescriptionJS("")
            conditionWidget = EasyUiSelectWidget("${uid}Condition", conditionDescr)
            val conditionConfig = SelectConfigurationJS()
            conditionConfig.nullAllowed = false
            conditionWidget!!.configure(conditionConfig)
    }

    override fun setData(data: SimpleWorkspaceCriterionDTJS?) {
        data?.property?.let { prop-> propertyWidget.setData(SelectItemJS(prop, propertyPossibleValues.find { it.id == prop }?.caption)) }
        data?.condition?.let{ cond ->
            val item = propertyWidget.getData()
            if(item != null){
                val possibleCondition = getConditions(getPropertyType(item.id!!, EasyUiWorkspaceListEditor.getListDescription(listId)!!)).find { it.name == cond.name }
                if(possibleCondition != null){
                    conditionWidget!!.setData(SelectItemJS(possibleCondition.name, possibleCondition.toString()))
                }
            }
        }
        data?.value?.let { value ->
            valueWidget?.setData(value)
        }
    }

    override fun getData(): SimpleWorkspaceCriterionDTJS? {
        val property = propertyWidget.getData() ?: return null
        val condition = conditionWidget?.getData?.invoke()?:return null
        val value = valueWidget?.getData()
        val result = SimpleWorkspaceCriterionDTJS()
        result.property = property.id
        result.condition = getCondition(EasyUiListCondition.valueOf(condition.id!!))
        result.value = value
        return result
    }

}

private enum class EasyUiListPropertyType{
    STRING,
    ENUM,
    FLOAT,
    INT,
    ENTITY_REFERENCE,
    LOCAL_DATE_TIME,
    LOCAL_DATE,
    BOOLEAN,
    COLLECTION_STRING,
    COLLECTION_ENUM,
    COLLECTION_ENTITY_REFERENCE
}



private enum class EasyUiListCondition{
    EQUALS{
        override fun toString(): String {
            return "равно"
        }
    },
    NOT_EQUALS{
        override fun toString(): String {
            return "не равно"
        }
    },
    GREATER_THAN{
        override fun toString(): String {
            return "больше"
        }
    },
    GREATER_THAN_OR_EQUALS{
        override fun toString(): String {
            return "больше либо равно"
        }
    },
    LESS_THAN_OR_EQUALS{
        override fun toString(): String {
            return "меньше либо равно"
        }
    },
    LESS_THAN{
        override fun toString(): String {
            return "меньше"
        }
    },
    SET{
        override fun toString(): String {
            return "задано"
        }
    },
    NOT_SET{
        override fun toString(): String {
            return "не задано"
        }
    },
    CONTAINS{
        override fun toString(): String {
            return "содержит"
        }
    },
    NOT_CONTAINS{
        override fun toString(): String {
            return "не содержит"
        }
    },
    WITHIN_PERIOD{
        override fun toString(): String {
            return "внутри периода"
        }
    },
    TRUE{
        override fun toString(): String {
            return "истина"
        }
    },
    FALSE{
        override fun toString(): String {
            return "ложь"
        }
    }

}

private fun getCondition(condition:WorkspaceSimpleCriterionConditionDTJS):EasyUiListCondition{
    return when(condition){
        WorkspaceSimpleCriterionConditionDTJS.CONTAINS->EasyUiListCondition.CONTAINS
        WorkspaceSimpleCriterionConditionDTJS.EQUALS->EasyUiListCondition.EQUALS
        WorkspaceSimpleCriterionConditionDTJS.GREATER_THAN->EasyUiListCondition.GREATER_THAN
        WorkspaceSimpleCriterionConditionDTJS.GREATER_THAN_OR_EQUALS->EasyUiListCondition.GREATER_THAN_OR_EQUALS
        WorkspaceSimpleCriterionConditionDTJS.LESS_THAN->EasyUiListCondition.LESS_THAN
        WorkspaceSimpleCriterionConditionDTJS.LESS_THAN_OR_EQUALS->EasyUiListCondition.LESS_THAN_OR_EQUALS
        WorkspaceSimpleCriterionConditionDTJS.NOT_CONTAINS->EasyUiListCondition.NOT_CONTAINS
        WorkspaceSimpleCriterionConditionDTJS.NOT_EQUALS->EasyUiListCondition.NOT_EQUALS
        WorkspaceSimpleCriterionConditionDTJS.NOT_SET->EasyUiListCondition.NOT_SET
        WorkspaceSimpleCriterionConditionDTJS.SET->EasyUiListCondition.SET
        WorkspaceSimpleCriterionConditionDTJS.WITHIN_PERIOD->EasyUiListCondition.WITHIN_PERIOD
        WorkspaceSimpleCriterionConditionDTJS.TRUE->EasyUiListCondition.TRUE
        WorkspaceSimpleCriterionConditionDTJS.FALSE->EasyUiListCondition.FALSE

    }
}

private fun getCondition(condition:EasyUiListCondition):WorkspaceSimpleCriterionConditionDTJS{
    return when(condition){
        EasyUiListCondition.CONTAINS->WorkspaceSimpleCriterionConditionDTJS.CONTAINS
        EasyUiListCondition.EQUALS->WorkspaceSimpleCriterionConditionDTJS.EQUALS
        EasyUiListCondition.GREATER_THAN->WorkspaceSimpleCriterionConditionDTJS.GREATER_THAN
        EasyUiListCondition.GREATER_THAN_OR_EQUALS->WorkspaceSimpleCriterionConditionDTJS.GREATER_THAN_OR_EQUALS
        EasyUiListCondition.LESS_THAN->WorkspaceSimpleCriterionConditionDTJS.LESS_THAN
        EasyUiListCondition.LESS_THAN_OR_EQUALS->WorkspaceSimpleCriterionConditionDTJS.LESS_THAN_OR_EQUALS
        EasyUiListCondition.NOT_CONTAINS->WorkspaceSimpleCriterionConditionDTJS.NOT_CONTAINS
        EasyUiListCondition.NOT_EQUALS->WorkspaceSimpleCriterionConditionDTJS.NOT_EQUALS
        EasyUiListCondition.NOT_SET->WorkspaceSimpleCriterionConditionDTJS.NOT_SET
        EasyUiListCondition.SET->WorkspaceSimpleCriterionConditionDTJS.SET
        EasyUiListCondition.WITHIN_PERIOD->WorkspaceSimpleCriterionConditionDTJS.WITHIN_PERIOD
        EasyUiListCondition.TRUE->WorkspaceSimpleCriterionConditionDTJS.TRUE
        EasyUiListCondition.FALSE->WorkspaceSimpleCriterionConditionDTJS.FALSE

    }
}



private fun getPropertyType(id:String, descr:BaseIndexDescriptionJS):EasyUiListPropertyType{
    val propertyDescr = descr.properties[id]
    if(propertyDescr != null){
        return when(propertyDescr.type){
            DatabasePropertyTypeJS.STRING -> EasyUiListPropertyType.STRING
            DatabasePropertyTypeJS.TEXT -> EasyUiListPropertyType.STRING
            DatabasePropertyTypeJS.LOCAL_DATE -> EasyUiListPropertyType.LOCAL_DATE
            DatabasePropertyTypeJS.LOCAL_DATE_TIME -> EasyUiListPropertyType.LOCAL_DATE_TIME
            DatabasePropertyTypeJS.ENUM -> EasyUiListPropertyType.ENUM
            DatabasePropertyTypeJS.BOOLEAN -> EasyUiListPropertyType.BOOLEAN
            DatabasePropertyTypeJS.ENTITY_REFERENCE -> EasyUiListPropertyType.ENTITY_REFERENCE
            DatabasePropertyTypeJS.LONG -> EasyUiListPropertyType.INT
            DatabasePropertyTypeJS.INT -> EasyUiListPropertyType.INT
            DatabasePropertyTypeJS.BIG_DECIMAL -> EasyUiListPropertyType.FLOAT
        }
    }
    val collectionDescription = descr.collections[id]?:throw IllegalArgumentException("unable to find description for property $id")
    return when(collectionDescription.elementType){
        DatabaseCollectionTypeJS.STRING -> EasyUiListPropertyType.COLLECTION_STRING
        DatabaseCollectionTypeJS.ENUM -> EasyUiListPropertyType.COLLECTION_ENUM
        DatabaseCollectionTypeJS.ENTITY_REFERENCE -> EasyUiListPropertyType.COLLECTION_ENTITY_REFERENCE
    }
}

private fun getConditions(propertyType: EasyUiListPropertyType):List<EasyUiListCondition>{
    return when(propertyType){
        EasyUiListPropertyType.STRING -> arrayListOf(EasyUiListCondition.EQUALS, EasyUiListCondition.NOT_EQUALS, EasyUiListCondition.SET,EasyUiListCondition.NOT_SET, EasyUiListCondition.CONTAINS, EasyUiListCondition.NOT_CONTAINS)
        EasyUiListPropertyType.ENUM -> arrayListOf(EasyUiListCondition.EQUALS, EasyUiListCondition.NOT_EQUALS, EasyUiListCondition.SET,EasyUiListCondition.NOT_SET)
        EasyUiListPropertyType.INT -> arrayListOf(EasyUiListCondition.EQUALS, EasyUiListCondition.NOT_EQUALS, EasyUiListCondition.SET,EasyUiListCondition.NOT_SET, EasyUiListCondition.GREATER_THAN_OR_EQUALS, EasyUiListCondition.GREATER_THAN, EasyUiListCondition.LESS_THAN_OR_EQUALS, EasyUiListCondition.LESS_THAN)
        EasyUiListPropertyType.FLOAT -> arrayListOf(EasyUiListCondition.EQUALS, EasyUiListCondition.NOT_EQUALS, EasyUiListCondition.SET,EasyUiListCondition.NOT_SET, EasyUiListCondition.GREATER_THAN_OR_EQUALS, EasyUiListCondition.GREATER_THAN, EasyUiListCondition.LESS_THAN_OR_EQUALS, EasyUiListCondition.LESS_THAN)
        EasyUiListPropertyType.ENTITY_REFERENCE -> arrayListOf(EasyUiListCondition.EQUALS, EasyUiListCondition.NOT_EQUALS, EasyUiListCondition.SET,EasyUiListCondition.NOT_SET)
        EasyUiListPropertyType.LOCAL_DATE_TIME -> arrayListOf(EasyUiListCondition.GREATER_THAN, EasyUiListCondition.GREATER_THAN_OR_EQUALS, EasyUiListCondition.SET,EasyUiListCondition.NOT_SET, EasyUiListCondition.LESS_THAN,EasyUiListCondition.LESS_THAN_OR_EQUALS,EasyUiListCondition.WITHIN_PERIOD)
        EasyUiListPropertyType.LOCAL_DATE -> arrayListOf(EasyUiListCondition.EQUALS, EasyUiListCondition.NOT_EQUALS,EasyUiListCondition.GREATER_THAN, EasyUiListCondition.GREATER_THAN_OR_EQUALS, EasyUiListCondition.SET,EasyUiListCondition.NOT_SET, EasyUiListCondition.LESS_THAN,EasyUiListCondition.LESS_THAN_OR_EQUALS,EasyUiListCondition.WITHIN_PERIOD)
        EasyUiListPropertyType.BOOLEAN -> arrayListOf(EasyUiListCondition.FALSE, EasyUiListCondition.TRUE,EasyUiListCondition.NOT_SET)
        EasyUiListPropertyType.COLLECTION_STRING -> arrayListOf(EasyUiListCondition.SET,EasyUiListCondition.NOT_SET,EasyUiListCondition.CONTAINS,EasyUiListCondition.NOT_CONTAINS)
        EasyUiListPropertyType.COLLECTION_ENUM -> arrayListOf(EasyUiListCondition.SET,EasyUiListCondition.NOT_SET,EasyUiListCondition.CONTAINS,EasyUiListCondition.NOT_CONTAINS)
        EasyUiListPropertyType.COLLECTION_ENTITY_REFERENCE -> arrayListOf(EasyUiListCondition.SET,EasyUiListCondition.NOT_SET,EasyUiListCondition.CONTAINS,EasyUiListCondition.NOT_CONTAINS)
    }
}

interface EasyUiCriterionValueRenderer<T:BaseWorkspaceSimpleCriterionValueDTJS>{
    fun getContent():String
    fun decorate()
    fun setData(value:T?)
    fun getData():T?
}


private fun getValueWidget(propertyType: EasyUiListPropertyType, condition: EasyUiListCondition, className:String?): EasyUiCriterionValueRenderer<*>?{
    return when(propertyType){
        EasyUiListPropertyType.STRING -> {
            when(condition){
                EasyUiListCondition.EQUALS,
                EasyUiListCondition.NOT_EQUALS,
                EasyUiListCondition.CONTAINS,
                EasyUiListCondition.NOT_CONTAINS -> EasyUiCriterionStringValueRenderer()
                EasyUiListCondition.SET,EasyUiListCondition.NOT_SET -> null
                else ->throw IllegalArgumentException("unsupported condition $condition")
            }
        }
        EasyUiListPropertyType.ENUM -> {
            when(condition) {
                EasyUiListCondition.EQUALS, EasyUiListCondition.NOT_EQUALS -> EasyUiCriterionEnumValuesRenderer(className!!)
                EasyUiListCondition.SET,EasyUiListCondition.NOT_SET -> null
                else ->throw IllegalArgumentException("unsupported condition $condition")
            }
        }
        EasyUiListPropertyType.INT -> {
            when(condition){
                EasyUiListCondition.EQUALS, EasyUiListCondition.NOT_EQUALS,  EasyUiListCondition.GREATER_THAN_OR_EQUALS,
                EasyUiListCondition.GREATER_THAN, EasyUiListCondition.LESS_THAN_OR_EQUALS, EasyUiListCondition.LESS_THAN -> EasyUiCriterionIntValueRenderer()
                EasyUiListCondition.SET,EasyUiListCondition.NOT_SET -> null
                else ->throw IllegalArgumentException("unsupported condition $condition")
            }
        }
        EasyUiListPropertyType.FLOAT -> {
            when(condition){
                EasyUiListCondition.EQUALS, EasyUiListCondition.NOT_EQUALS,  EasyUiListCondition.GREATER_THAN_OR_EQUALS,
                EasyUiListCondition.GREATER_THAN, EasyUiListCondition.LESS_THAN_OR_EQUALS, EasyUiListCondition.LESS_THAN -> EasyUiCriterionFloatValueRenderer()
                EasyUiListCondition.SET,EasyUiListCondition.NOT_SET -> null
                else ->throw IllegalArgumentException("unsupported condition $condition")
            }
        }
        EasyUiListPropertyType.ENTITY_REFERENCE -> {
            when(condition) {
                EasyUiListCondition.EQUALS, EasyUiListCondition.NOT_EQUALS -> EasyUiCriterionEntityValuesRenderer(className!!)
                EasyUiListCondition.SET,EasyUiListCondition.NOT_SET -> null
                else ->throw IllegalArgumentException("unsupported condition $condition")
            }
        }
        EasyUiListPropertyType.LOCAL_DATE_TIME -> {
            when(condition){
                EasyUiListCondition.EQUALS, EasyUiListCondition.NOT_EQUALS,EasyUiListCondition.GREATER_THAN, EasyUiListCondition.GREATER_THAN_OR_EQUALS,
                EasyUiListCondition.LESS_THAN,EasyUiListCondition.LESS_THAN_OR_EQUALS->EasyUiCriterionDateTimeValueRenderer()
                EasyUiListCondition.SET,EasyUiListCondition.NOT_SET -> null
                EasyUiListCondition.WITHIN_PERIOD -> EasyUiCriterionDateTimeIntervalValueRenderer()
                else ->throw IllegalArgumentException("unsupported condition $condition")
            }
        }
        EasyUiListPropertyType.LOCAL_DATE -> {
            when(condition){
                EasyUiListCondition.EQUALS, EasyUiListCondition.NOT_EQUALS,EasyUiListCondition.GREATER_THAN, EasyUiListCondition.GREATER_THAN_OR_EQUALS,
                EasyUiListCondition.LESS_THAN,EasyUiListCondition.LESS_THAN_OR_EQUALS->EasyUiCriterionDateValueRenderer()
                EasyUiListCondition.SET,EasyUiListCondition.NOT_SET -> null
                EasyUiListCondition.WITHIN_PERIOD -> EasyUiCriterionDateIntervalValueRenderer()
                else ->throw IllegalArgumentException("unsupported condition $condition")
            }
        }
        EasyUiListPropertyType.BOOLEAN -> null
        EasyUiListPropertyType.COLLECTION_STRING -> TODO()
        EasyUiListPropertyType.COLLECTION_ENUM -> TODO()
        EasyUiListPropertyType.COLLECTION_ENTITY_REFERENCE -> TODO()
    }
}
