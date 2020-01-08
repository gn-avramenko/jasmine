/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.easyui.mainframe

import com.gridnine.jasmine.server.standard.model.rest.*
import com.gridnine.jasmine.web.core.model.domain.BaseIndexDescriptionJS
import com.gridnine.jasmine.web.core.model.domain.DatabaseCollectionTypeJS
import com.gridnine.jasmine.web.core.model.domain.DatabasePropertyTypeJS
import com.gridnine.jasmine.web.core.model.ui.*
import com.gridnine.jasmine.web.core.ui.TextUtilsJS
import com.gridnine.jasmine.web.core.utils.HtmlUtilsJS
import com.gridnine.jasmine.web.easyui.jQuery
import com.gridnine.jasmine.web.easyui.widgets.EasyUiSelectWidget
import com.gridnine.jasmine.web.easyui.widgets.EasyUiTextBoxWidget


class EasyUiWorkspaceCriterionsEditor(private val divId: String, private val listId: String?) {

    private val handlers = arrayListOf<CriterionHandler<*>>()

    init {
        val content = HtmlUtilsJS.html {
            div(id = "${divId}Header", style = "width:100%") {
                table(style = "width:100%;border-collapse: collapse") {
                    tr {
                        td(style = "width:300px;border:1px solid  #D3D3D3;padding:5px") { "Поле"() }
                        td(style = "width:200px;border:1px solid #D3D3D3;padding:5px") { "Условие"() }
                        td(style = "border:1px solid #D3D3D3;padding:5px") { "Значение"() }
                        td(style = "width:20px") {
                            div(style = "display:inline;float:right;width:15px;height:20px", id = "criterionsAddToolButton", `class` = "jasmine-datagrid-expand") { }
                        }
                    }
                }
            }
            div(id = "${divId}Criterions", style = "width:100%") {

            }
            div(id = "criterionsAddDialogMenu", style = "display:none") {
                div (id="criterionsAddSimpleCriterionMenuItem"){ "простое условие"()  }
            }
        }.toString()
        jQuery("#${divId}").html(content)
        val menuDiv = jQuery("#criterionsAddDialogMenu")
        menuDiv.menu(object{
            val onClick = { item:dynamic ->
                when(item.id){
                    "criterionsAddSimpleCriterionMenuItem" ->{
                        if(listId != null) {
                            val handler = SimpleCriterionHandler(divId, listId)
                            val crit = SimpleWorkspaceCriterionDTJS()
                            handler.addCriterion(crit, 0)
                            handlers.add(0, handler)
                        }
                    }
                    else -> throw IllegalArgumentException("unknown item ${item.id}")
                }
            }
        })
        val addButtonDiv = jQuery("#criterionsAddToolButton")
        addButtonDiv.click {
            val pos = addButtonDiv.asDynamic().offset()
            menuDiv.menu("show",object{
                val left = pos.left
                val top = pos.top
            })
        }
    }

    fun clear() {
        jQuery("#${divId}Criterions").empty()
    }

    fun setData(criterions: List<BaseWorkspaceCriterionDTJS>) {
        clear()
        if (listId == null) {
            return
        }
        criterions.forEach { crit ->
            when (crit) {
                is SimpleWorkspaceCriterionDTJS -> {
                    val handler = SimpleCriterionHandler(divId, listId)
                    handler.addCriterion(crit,null)
                    handlers.add(handler)
                }
                else -> {
                }
            }
        }
    }
}

interface CriterionHandler<T : BaseWorkspaceCriterionDTJS> {
    fun addCriterion(crit: T, idx:Int?)
}

class SimpleCriterionHandler(private val divId: String, private val listId: String) : CriterionHandler<SimpleWorkspaceCriterionDTJS> {

    private val uid = TextUtilsJS.createUUID()

    private lateinit var propertyWidget: SelectWidget

    private var conditionWidget: SelectWidget? = null

    private var valueWidget: EasyUiCriterionValueRenderer<BaseWorkspaceSimpleCriterionValueDTJS>? = null

    override fun addCriterion(crit: SimpleWorkspaceCriterionDTJS, idx:Int?) {
        val content = HtmlUtilsJS.html {
            table(id = "${uid}Table", style = "width:100%;border-collapse: collapse") {
                tr {
                    td(style = "width:300px;border:1px solid  #D3D3D3;padding:0px") { "<input style=\"width:100%\" id = \"${uid}Property\"/>"() }
                    td(style = "width:200px;border:1px solid #D3D3D3;padding:0px") { "<input style=\"width:100%\" id = \"${uid}Condition\"/>"() }
                    td(style = "border:1px solid #D3D3D3;padding:0px", id = "${uid}Value" ) { }
                    td(style = "width:70px") {
                        ("<div style=\"display:inline;width:15px;height:30px\" id=\"${uid}Up\" class = \"jasmine-datagrid-sort-asc\"></div>" +
                                "<div style=\"display:inline;width:15px;height:30px\" id=\"${uid}Down\" class = \"jasmine-datagrid-sort-desc\"></div>" +
                                "<div style=\"display:inline;width:15px;height:30px\" id=\"${uid}Add\" class = \"jasmine-datagrid-expand\"></div>" +
                                "<div style=\"display:inline;width:15px;height:30px\" id=\"${uid}Remove\" class = \"jasmine-datagrid-collapse\"></div>"
                                )()
                    }
                }
            }

        }.toString()
        if(idx == null) {
            jQuery("#${divId}Criterions").append(content)
        } else {
            val criterionsDiv = jQuery("#${divId}Criterions")
            val children = criterionsDiv.asDynamic().children()
            if(children.length ==0 ){
                criterionsDiv.append(content)
            } else {
                jQuery(content).asDynamic().insertBefore(children[0])
            }
        }
        val propertyDescription = SelectDescriptionJS("")
        val propertyWidget = EasyUiSelectWidget("${uid}Property", propertyDescription)
        val propertyConfig = SelectConfigurationJS()
        propertyConfig.nullAllowed = false
        propertyConfig.possibleValues.addAll(EasyUiWorkspaceListEditor.getPossibleFieldValues(listId))
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
                    valueWidget = getValueWidget(propertyType,EasyUiListCondition.valueOf(condition.id!!)) as EasyUiCriterionValueRenderer<BaseWorkspaceSimpleCriterionValueDTJS>
                    valueWidget?.let {
                        jQuery("#${uid}Value").html(it.getContent())
                        it.decorate()
                    }
                }
            }
        }

        crit.property?.let { prop-> propertyWidget.setData(SelectItemJS(prop, propertyConfig.possibleValues.find { it.id == prop }?.caption)) }
        crit.condition?.let{cond ->
            val item = propertyWidget.getData()
            if(item != null){
                val possibleCondition = getConditions(getPropertyType(item.id!!, EasyUiWorkspaceListEditor.getListDescription(listId)!!)).find { it.name == cond.name }
                if(possibleCondition != null){
                    conditionWidget!!.setData(SelectItemJS(possibleCondition.name, possibleCondition.toString()))
                }
            }
        }
        crit.value?.let {value ->
            valueWidget?.setData(value)
        }
        @Suppress("SENSELESS_COMPARISON")
        if(conditionWidget == null){
            val conditionDescr = SelectDescriptionJS("")
            conditionWidget = EasyUiSelectWidget("${uid}Condition", conditionDescr)
            val conditionConfig = SelectConfigurationJS()
            conditionConfig.nullAllowed = false
            conditionWidget!!.configure(conditionConfig)
        }
    }

}

private enum class EasyUiListPropertyType{
    STRING,
    ENUM,
    NUMBER,
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
            DatabasePropertyTypeJS.LONG -> EasyUiListPropertyType.NUMBER
            DatabasePropertyTypeJS.INT -> EasyUiListPropertyType.NUMBER
            DatabasePropertyTypeJS.BIG_DECIMAL -> EasyUiListPropertyType.NUMBER
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
        EasyUiListPropertyType.NUMBER -> arrayListOf(EasyUiListCondition.EQUALS, EasyUiListCondition.NOT_EQUALS, EasyUiListCondition.SET,EasyUiListCondition.NOT_SET, EasyUiListCondition.GREATER_THAN_OR_EQUALS, EasyUiListCondition.GREATER_THAN, EasyUiListCondition.LESS_THAN_OR_EQUALS, EasyUiListCondition.LESS_THAN)
        EasyUiListPropertyType.ENTITY_REFERENCE -> arrayListOf(EasyUiListCondition.EQUALS, EasyUiListCondition.NOT_EQUALS, EasyUiListCondition.SET,EasyUiListCondition.NOT_SET)
        EasyUiListPropertyType.LOCAL_DATE_TIME -> arrayListOf(EasyUiListCondition.GREATER_THAN, EasyUiListCondition.GREATER_THAN_OR_EQUALS, EasyUiListCondition.SET,EasyUiListCondition.NOT_SET, EasyUiListCondition.LESS_THAN,EasyUiListCondition.LESS_THAN_OR_EQUALS,EasyUiListCondition.WITHIN_PERIOD)
        EasyUiListPropertyType.LOCAL_DATE -> arrayListOf(EasyUiListCondition.EQUALS, EasyUiListCondition.NOT_EQUALS,EasyUiListCondition.GREATER_THAN, EasyUiListCondition.GREATER_THAN_OR_EQUALS, EasyUiListCondition.SET,EasyUiListCondition.NOT_SET, EasyUiListCondition.LESS_THAN,EasyUiListCondition.LESS_THAN_OR_EQUALS,EasyUiListCondition.WITHIN_PERIOD)
        EasyUiListPropertyType.BOOLEAN -> arrayListOf(EasyUiListCondition.EQUALS, EasyUiListCondition.NOT_EQUALS)
        EasyUiListPropertyType.COLLECTION_STRING -> arrayListOf(EasyUiListCondition.SET,EasyUiListCondition.NOT_SET,EasyUiListCondition.CONTAINS,EasyUiListCondition.NOT_CONTAINS)
        EasyUiListPropertyType.COLLECTION_ENUM -> arrayListOf(EasyUiListCondition.SET,EasyUiListCondition.NOT_SET,EasyUiListCondition.CONTAINS,EasyUiListCondition.NOT_CONTAINS)
        EasyUiListPropertyType.COLLECTION_ENTITY_REFERENCE -> arrayListOf(EasyUiListCondition.SET,EasyUiListCondition.NOT_SET,EasyUiListCondition.CONTAINS,EasyUiListCondition.NOT_CONTAINS)
    }
}

private interface EasyUiCriterionValueRenderer<T:BaseWorkspaceSimpleCriterionValueDTJS>{
    fun getContent():String
    fun decorate()
    fun setData(value:T?)
    fun getData():T?
}

private class EasyUiCriterionStringValueRenderer:EasyUiCriterionValueRenderer<WorkspaceSimpleCriterionStringValueDTJS>{
    private val uid = TextUtilsJS.createUUID()

    private lateinit var widget:EasyUiTextBoxWidget
    override fun getContent(): String {
        return "<input id = \"${uid}Control\" style = \"width:100%\">"
    }

    override fun decorate() {

        widget = EasyUiTextBoxWidget("${uid}Control", TextboxDescriptionJS(""))
        widget.configure(Unit)
    }

    override fun setData(value: WorkspaceSimpleCriterionStringValueDTJS?) {
        widget.setData(value?.value)
    }

    override fun getData(): WorkspaceSimpleCriterionStringValueDTJS?{
        return widget.getData()?.let {
            val res = WorkspaceSimpleCriterionStringValueDTJS()
            res.value = it
            res
        }
    }
}


private fun getValueWidget(propertyType: EasyUiListPropertyType, condition: EasyUiListCondition): EasyUiCriterionValueRenderer<*>{
    return when(propertyType){
        EasyUiListPropertyType.STRING -> {
            when(condition){
                EasyUiListCondition.EQUALS,
                EasyUiListCondition.NOT_EQUALS,
                EasyUiListCondition.CONTAINS,
                EasyUiListCondition.NOT_CONTAINS -> EasyUiCriterionStringValueRenderer()
                else ->throw IllegalArgumentException("unsupported condition $condition")
            }
        }
        EasyUiListPropertyType.ENUM -> TODO()
        EasyUiListPropertyType.NUMBER -> TODO()
        EasyUiListPropertyType.ENTITY_REFERENCE -> TODO()
        EasyUiListPropertyType.LOCAL_DATE_TIME -> TODO()
        EasyUiListPropertyType.LOCAL_DATE -> TODO()
        EasyUiListPropertyType.BOOLEAN -> TODO()
        EasyUiListPropertyType.COLLECTION_STRING -> TODO()
        EasyUiListPropertyType.COLLECTION_ENUM -> TODO()
        EasyUiListPropertyType.COLLECTION_ENTITY_REFERENCE -> TODO()
    }
}