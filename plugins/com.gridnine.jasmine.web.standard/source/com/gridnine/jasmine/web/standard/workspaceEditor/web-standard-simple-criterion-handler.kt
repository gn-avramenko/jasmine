/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.standard.workspaceEditor

import com.gridnine.jasmine.common.core.meta.DatabaseCollectionTypeJS
import com.gridnine.jasmine.common.core.meta.DatabasePropertyTypeJS
import com.gridnine.jasmine.common.core.meta.DomainMetaRegistryJS
import com.gridnine.jasmine.common.core.model.SelectItemJS
import com.gridnine.jasmine.common.core.model.XeptionJS
import com.gridnine.jasmine.common.standard.model.domain.WorkspaceSimpleCriterionConditionJS
import com.gridnine.jasmine.common.standard.model.rest.BaseWorkspaceSimpleCriterionValueDTJS
import com.gridnine.jasmine.common.standard.model.rest.SimpleWorkspaceCriterionDTJS
import com.gridnine.jasmine.web.core.reflection.ReflectionFactoryJS
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.components.WebDivsContainer
import com.gridnine.jasmine.web.core.ui.components.WebTableBoxCell
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import com.gridnine.jasmine.web.standard.widgets.GeneralSelectWidget

@Suppress("UNCHECKED_CAST", "UNREACHABLE_CODE")
class WebSimpleCriterionHandler(listId: String, private val initData: SimpleWorkspaceCriterionDTJS?) : WebCriterionHandler<SimpleWorkspaceCriterionDTJS> {
    private val uuid = MiscUtilsJS.createUUID()

    private val properties = arrayListOf<WebCriterionPropertyWrapper>()

    private var lastEditor: WebSimpleCriterionValueEditor<*>? = null

    private val valueDivId= "valueDiv$uuid"
    private lateinit var valueControl:WebDivsContainer
    private lateinit var  propertySelect:GeneralSelectWidget
    private lateinit var  conditionSelect:GeneralSelectWidget

    init {
        val indexDescription = DomainMetaRegistryJS.get().indexes[listId] ?: DomainMetaRegistryJS.get().assets[listId]
        ?: throw XeptionJS.forDeveloper("no description for $listId")
        indexDescription.properties.values.forEach {
            properties.add(WebCriterionPropertyWrapper(it.id, it.displayName!!, false, it.type, null, it.className))
        }
        indexDescription.collections.values.forEach {
            properties.add(WebCriterionPropertyWrapper(it.id, it.displayName!!, false, null, it.elementType, it.elementClassName))
        }
        properties.sortBy { it.text }
    }

    override fun getComponents(): MutableList<WebTableBoxCell> {

        val result = arrayListOf<WebTableBoxCell>()
        propertySelect = GeneralSelectWidget {
            width = "100%"
            showClearIcon = false
        }
        propertySelect.setPossibleValues(properties.map { SelectItemJS(it.id, it.text) })
        result.add(WebTableBoxCell(propertySelect))
        conditionSelect = GeneralSelectWidget {
            width = "100%"
            showClearIcon = false
        }
        result.add(WebTableBoxCell(conditionSelect))
        valueControl = WebUiLibraryAdapter.get().createDivsContainer{
            width = "100%"
        }
        result.add(WebTableBoxCell(valueControl))
        initData?.property?.let { property ->
            val propWrapper = properties.find { it.id == property }
            propWrapper?.let {
                propertySelect.setValue(SelectItemJS(it.id, it.text))
                conditionSelect.setPossibleValues(getConditions(it))
                initData.condition?.let { crit ->
                    conditionSelect.setValue(SelectItemJS(crit.name, getDisplayName(crit)))
                    setEditor(propWrapper, crit, initData.value)
                }
            }
        }
        propertySelect.setChangeListener { prop ->
            val property = properties.find { it.id == prop?.id }
            val possibleConditions = property?.let { getConditions(it) }?: emptyList()
            conditionSelect.setPossibleValues(possibleConditions)
            val condition = if(possibleConditions.isNotEmpty()) possibleConditions[0] else null
            conditionSelect.setValue(condition)
            setEditor(property, condition?.id?.let { toCondition(it) }, null)
        }
        conditionSelect.setChangeListener {
            val property = properties.find { prop -> prop.id == propertySelect.getValue()?.id }
            val condition = it?.id?.let { condItem -> toCondition(condItem)}
            setEditor(property,condition, null)
        }
        return result
    }

    private fun toCondition(id:String):WorkspaceSimpleCriterionConditionJS{
        return WorkspaceSimpleCriterionConditionJS.valueOf(id)
    }

    private fun setEditor(property: WebCriterionPropertyWrapper?, condition: WorkspaceSimpleCriterionConditionJS?, value: BaseWorkspaceSimpleCriterionValueDTJS?) {
        val editorType = getEditorType(property, condition)
        if(lastEditor == null || lastEditor!!.getType() != editorType){
            if(lastEditor != null) {
                val div = valueControl.getDiv(valueDivId)
                if (div != null) {
                    valueControl.removeDiv(valueDivId)
                }
            }
            lastEditor = when(editorType){
                WebSimpleCriterionValueType.NULL -> WebNullValueEditor()
                WebSimpleCriterionValueType.STRING_VALUES -> WebStringValuesValueEditor()
                WebSimpleCriterionValueType.ENUM_VALUES -> WebEnumValuesValueEditor(property!!.className!!)
                WebSimpleCriterionValueType.INT_VALUE -> WebIntValueEditor()
                WebSimpleCriterionValueType.LONG_VALUE ->  WebLongValueEditor()
                WebSimpleCriterionValueType.BIG_DECIMAL_VALUE -> WebFloatValueEditor()
                WebSimpleCriterionValueType.ENTITY_REFERENCE_VALUES -> WebEntityReferenceValuesValueEditor( property!!.className!!)
                WebSimpleCriterionValueType.DATE_VALUE -> WebDateValueEditor()
                WebSimpleCriterionValueType.DATE_INTERVAL -> WebDateIntervalEditor()
                WebSimpleCriterionValueType.DATE_TIME_VALUE -> WebDateTimeValueEditor()
                WebSimpleCriterionValueType.DATE_TIME_INTERVAL -> WebDateTimeIntervalEditor()
            }
            valueControl.addDiv(valueDivId, lastEditor!!)
            valueControl.show(valueDivId)
        }
        (lastEditor as WebSimpleCriterionValueEditor<BaseWorkspaceSimpleCriterionValueDTJS>).setValue(value)
    }

    private fun getEditorType(property: WebCriterionPropertyWrapper?, condition: WorkspaceSimpleCriterionConditionJS?): WebSimpleCriterionValueType {
        if(property == null || condition == null){
            return  WebSimpleCriterionValueType.NULL
        }
        if(property.collection){
            return TODO()
        }
        return when(property.propertyType!!){
            DatabasePropertyTypeJS.STRING,DatabasePropertyTypeJS.TEXT ->
                when(condition){
                    WorkspaceSimpleCriterionConditionJS.EQUALS,
                    WorkspaceSimpleCriterionConditionJS.NOT_EQUALS,
                    WorkspaceSimpleCriterionConditionJS.CONTAINS,
                    WorkspaceSimpleCriterionConditionJS.NOT_CONTAINS -> WebSimpleCriterionValueType.STRING_VALUES
                    WorkspaceSimpleCriterionConditionJS.SET,WorkspaceSimpleCriterionConditionJS.NOT_SET -> WebSimpleCriterionValueType.NULL
                    else ->throw IllegalArgumentException("unsupported condition $condition")
                }
            DatabasePropertyTypeJS.ENUM ->
                when(condition) {
                    WorkspaceSimpleCriterionConditionJS.EQUALS, WorkspaceSimpleCriterionConditionJS.NOT_EQUALS -> WebSimpleCriterionValueType.ENUM_VALUES
                    WorkspaceSimpleCriterionConditionJS.SET,WorkspaceSimpleCriterionConditionJS.NOT_SET -> WebSimpleCriterionValueType.NULL
                    else ->throw IllegalArgumentException("unsupported condition $condition")
                }
            DatabasePropertyTypeJS.INT -> {
                when(condition){
                    WorkspaceSimpleCriterionConditionJS.EQUALS, WorkspaceSimpleCriterionConditionJS.NOT_EQUALS,  WorkspaceSimpleCriterionConditionJS.GREATER_THAN_OR_EQUALS,
                    WorkspaceSimpleCriterionConditionJS.GREATER_THAN, WorkspaceSimpleCriterionConditionJS.LESS_THAN_OR_EQUALS, WorkspaceSimpleCriterionConditionJS.LESS_THAN -> WebSimpleCriterionValueType.INT_VALUE
                    WorkspaceSimpleCriterionConditionJS.SET,WorkspaceSimpleCriterionConditionJS.NOT_SET -> WebSimpleCriterionValueType.NULL
                    else ->throw IllegalArgumentException("unsupported condition $condition")
                }
            }
            DatabasePropertyTypeJS.BIG_DECIMAL -> {
                when(condition){
                    WorkspaceSimpleCriterionConditionJS.EQUALS, WorkspaceSimpleCriterionConditionJS.NOT_EQUALS,  WorkspaceSimpleCriterionConditionJS.GREATER_THAN_OR_EQUALS,
                    WorkspaceSimpleCriterionConditionJS.GREATER_THAN, WorkspaceSimpleCriterionConditionJS.LESS_THAN_OR_EQUALS, WorkspaceSimpleCriterionConditionJS.LESS_THAN -> WebSimpleCriterionValueType.BIG_DECIMAL_VALUE
                    WorkspaceSimpleCriterionConditionJS.SET,WorkspaceSimpleCriterionConditionJS.NOT_SET -> WebSimpleCriterionValueType.NULL
                    else ->throw IllegalArgumentException("unsupported condition $condition")
                }
            }
            DatabasePropertyTypeJS.ENTITY_REFERENCE -> {
                when(condition) {
                    WorkspaceSimpleCriterionConditionJS.EQUALS, WorkspaceSimpleCriterionConditionJS.NOT_EQUALS -> WebSimpleCriterionValueType.ENTITY_REFERENCE_VALUES
                    WorkspaceSimpleCriterionConditionJS.SET,WorkspaceSimpleCriterionConditionJS.NOT_SET -> WebSimpleCriterionValueType.NULL
                    else ->throw IllegalArgumentException("unsupported condition $condition")
                }
            }
            DatabasePropertyTypeJS.LOCAL_DATE_TIME -> {
                when(condition){
                    WorkspaceSimpleCriterionConditionJS.EQUALS, WorkspaceSimpleCriterionConditionJS.NOT_EQUALS,WorkspaceSimpleCriterionConditionJS.GREATER_THAN, WorkspaceSimpleCriterionConditionJS.GREATER_THAN_OR_EQUALS,
                    WorkspaceSimpleCriterionConditionJS.LESS_THAN,WorkspaceSimpleCriterionConditionJS.LESS_THAN_OR_EQUALS->WebSimpleCriterionValueType.DATE_TIME_VALUE
                    WorkspaceSimpleCriterionConditionJS.SET,WorkspaceSimpleCriterionConditionJS.NOT_SET -> WebSimpleCriterionValueType.NULL
                    WorkspaceSimpleCriterionConditionJS.WITHIN_PERIOD -> WebSimpleCriterionValueType.DATE_TIME_INTERVAL
                    else ->throw IllegalArgumentException("unsupported condition $condition")
                }
            }
            DatabasePropertyTypeJS.LOCAL_DATE -> {
                when(condition){
                    WorkspaceSimpleCriterionConditionJS.EQUALS, WorkspaceSimpleCriterionConditionJS.NOT_EQUALS,WorkspaceSimpleCriterionConditionJS.GREATER_THAN, WorkspaceSimpleCriterionConditionJS.GREATER_THAN_OR_EQUALS,
                    WorkspaceSimpleCriterionConditionJS.LESS_THAN,WorkspaceSimpleCriterionConditionJS.LESS_THAN_OR_EQUALS->WebSimpleCriterionValueType.DATE_VALUE
                    WorkspaceSimpleCriterionConditionJS.SET,WorkspaceSimpleCriterionConditionJS.NOT_SET -> WebSimpleCriterionValueType.NULL
                    WorkspaceSimpleCriterionConditionJS.WITHIN_PERIOD -> WebSimpleCriterionValueType.DATE_INTERVAL
                    else ->throw IllegalArgumentException("unsupported condition $condition")
                }
            }
            DatabasePropertyTypeJS.BOOLEAN -> WebSimpleCriterionValueType.NULL
            DatabasePropertyTypeJS.LONG -> {
                when(condition){
                    WorkspaceSimpleCriterionConditionJS.EQUALS, WorkspaceSimpleCriterionConditionJS.NOT_EQUALS,  WorkspaceSimpleCriterionConditionJS.GREATER_THAN_OR_EQUALS,
                    WorkspaceSimpleCriterionConditionJS.GREATER_THAN, WorkspaceSimpleCriterionConditionJS.LESS_THAN_OR_EQUALS, WorkspaceSimpleCriterionConditionJS.LESS_THAN -> WebSimpleCriterionValueType.LONG_VALUE
                    WorkspaceSimpleCriterionConditionJS.SET,WorkspaceSimpleCriterionConditionJS.NOT_SET -> WebSimpleCriterionValueType.NULL
                    else ->throw IllegalArgumentException("unsupported condition $condition")
                }
            }
        }
    }

    private fun getDisplayName(condition:WorkspaceSimpleCriterionConditionJS):String{
        return DomainMetaRegistryJS.get().enums[ReflectionFactoryJS.get().getQualifiedClassName(WorkspaceSimpleCriterionConditionJS::class)]!!.items[condition.name]!!.displayName!!
    }
    override fun getId(): String {
        return uuid
    }

    private fun getConditions(property: WebCriterionPropertyWrapper): List<SelectItemJS> {
        return getConditionsInternal(property).map { SelectItemJS(it.name,getDisplayName(it) ) }
                .sortedBy { it.text }
    }

    private fun getConditionsInternal(property: WebCriterionPropertyWrapper): List<WorkspaceSimpleCriterionConditionJS> {
        if (property.collection) {
            return when (property.collectionType!!) {
                DatabaseCollectionTypeJS.STRING -> arrayListOf(WorkspaceSimpleCriterionConditionJS.SET, WorkspaceSimpleCriterionConditionJS.NOT_SET, WorkspaceSimpleCriterionConditionJS.CONTAINS, WorkspaceSimpleCriterionConditionJS.NOT_CONTAINS)
                DatabaseCollectionTypeJS.ENUM -> arrayListOf(WorkspaceSimpleCriterionConditionJS.SET, WorkspaceSimpleCriterionConditionJS.NOT_SET, WorkspaceSimpleCriterionConditionJS.CONTAINS, WorkspaceSimpleCriterionConditionJS.NOT_CONTAINS)
                DatabaseCollectionTypeJS.ENTITY_REFERENCE -> arrayListOf(WorkspaceSimpleCriterionConditionJS.SET, WorkspaceSimpleCriterionConditionJS.NOT_SET, WorkspaceSimpleCriterionConditionJS.CONTAINS, WorkspaceSimpleCriterionConditionJS.NOT_CONTAINS)
            }
        }
        return when (property.propertyType!!) {
            DatabasePropertyTypeJS.STRING -> arrayListOf(WorkspaceSimpleCriterionConditionJS.EQUALS, WorkspaceSimpleCriterionConditionJS.NOT_EQUALS, WorkspaceSimpleCriterionConditionJS.SET, WorkspaceSimpleCriterionConditionJS.NOT_SET, WorkspaceSimpleCriterionConditionJS.CONTAINS, WorkspaceSimpleCriterionConditionJS.NOT_CONTAINS)
            DatabasePropertyTypeJS.ENUM -> arrayListOf(WorkspaceSimpleCriterionConditionJS.EQUALS, WorkspaceSimpleCriterionConditionJS.NOT_EQUALS, WorkspaceSimpleCriterionConditionJS.SET, WorkspaceSimpleCriterionConditionJS.NOT_SET)
            DatabasePropertyTypeJS.INT -> arrayListOf(WorkspaceSimpleCriterionConditionJS.EQUALS, WorkspaceSimpleCriterionConditionJS.NOT_EQUALS, WorkspaceSimpleCriterionConditionJS.SET, WorkspaceSimpleCriterionConditionJS.NOT_SET, WorkspaceSimpleCriterionConditionJS.GREATER_THAN_OR_EQUALS, WorkspaceSimpleCriterionConditionJS.GREATER_THAN, WorkspaceSimpleCriterionConditionJS.LESS_THAN_OR_EQUALS, WorkspaceSimpleCriterionConditionJS.LESS_THAN)
            DatabasePropertyTypeJS.BIG_DECIMAL -> arrayListOf(WorkspaceSimpleCriterionConditionJS.EQUALS, WorkspaceSimpleCriterionConditionJS.NOT_EQUALS, WorkspaceSimpleCriterionConditionJS.SET, WorkspaceSimpleCriterionConditionJS.NOT_SET, WorkspaceSimpleCriterionConditionJS.GREATER_THAN_OR_EQUALS, WorkspaceSimpleCriterionConditionJS.GREATER_THAN, WorkspaceSimpleCriterionConditionJS.LESS_THAN_OR_EQUALS, WorkspaceSimpleCriterionConditionJS.LESS_THAN)
            DatabasePropertyTypeJS.ENTITY_REFERENCE -> arrayListOf(WorkspaceSimpleCriterionConditionJS.EQUALS, WorkspaceSimpleCriterionConditionJS.NOT_EQUALS, WorkspaceSimpleCriterionConditionJS.SET, WorkspaceSimpleCriterionConditionJS.NOT_SET)
            DatabasePropertyTypeJS.LOCAL_DATE_TIME -> arrayListOf(WorkspaceSimpleCriterionConditionJS.GREATER_THAN, WorkspaceSimpleCriterionConditionJS.GREATER_THAN_OR_EQUALS, WorkspaceSimpleCriterionConditionJS.SET, WorkspaceSimpleCriterionConditionJS.NOT_SET, WorkspaceSimpleCriterionConditionJS.LESS_THAN, WorkspaceSimpleCriterionConditionJS.LESS_THAN_OR_EQUALS, WorkspaceSimpleCriterionConditionJS.WITHIN_PERIOD)
            DatabasePropertyTypeJS.LOCAL_DATE -> arrayListOf(WorkspaceSimpleCriterionConditionJS.EQUALS, WorkspaceSimpleCriterionConditionJS.NOT_EQUALS, WorkspaceSimpleCriterionConditionJS.GREATER_THAN, WorkspaceSimpleCriterionConditionJS.GREATER_THAN_OR_EQUALS, WorkspaceSimpleCriterionConditionJS.SET, WorkspaceSimpleCriterionConditionJS.NOT_SET, WorkspaceSimpleCriterionConditionJS.LESS_THAN, WorkspaceSimpleCriterionConditionJS.LESS_THAN_OR_EQUALS, WorkspaceSimpleCriterionConditionJS.WITHIN_PERIOD)
            DatabasePropertyTypeJS.BOOLEAN -> arrayListOf(WorkspaceSimpleCriterionConditionJS.YES, WorkspaceSimpleCriterionConditionJS.NO, WorkspaceSimpleCriterionConditionJS.NOT_SET)
            DatabasePropertyTypeJS.TEXT -> arrayListOf(WorkspaceSimpleCriterionConditionJS.EQUALS, WorkspaceSimpleCriterionConditionJS.NOT_EQUALS, WorkspaceSimpleCriterionConditionJS.SET, WorkspaceSimpleCriterionConditionJS.NOT_SET, WorkspaceSimpleCriterionConditionJS.GREATER_THAN_OR_EQUALS, WorkspaceSimpleCriterionConditionJS.GREATER_THAN, WorkspaceSimpleCriterionConditionJS.LESS_THAN_OR_EQUALS, WorkspaceSimpleCriterionConditionJS.LESS_THAN)
            DatabasePropertyTypeJS.LONG -> arrayListOf(WorkspaceSimpleCriterionConditionJS.EQUALS, WorkspaceSimpleCriterionConditionJS.NOT_EQUALS, WorkspaceSimpleCriterionConditionJS.SET, WorkspaceSimpleCriterionConditionJS.NOT_SET, WorkspaceSimpleCriterionConditionJS.CONTAINS, WorkspaceSimpleCriterionConditionJS.NOT_CONTAINS)
        }
    }

    override fun getData(): SimpleWorkspaceCriterionDTJS? {
        val property = propertySelect.getValue()?.id?: return null
        val condition = conditionSelect.getValue()?.id?.let { toCondition(it) }?:return null
        val result = SimpleWorkspaceCriterionDTJS()
        result.property = property
        result.condition = condition
        if(lastEditor?.getType() == WebSimpleCriterionValueType.NULL){
            return result
        }
        result.value = lastEditor?.getValue()?:return null
        return result
    }

}

