/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.mainframe.workspaceEditor

import com.gridnine.jasmine.server.core.model.common.SelectItemJS
import com.gridnine.jasmine.server.core.model.domain.DatabaseCollectionTypeJS
import com.gridnine.jasmine.server.core.model.domain.DatabasePropertyTypeJS
import com.gridnine.jasmine.server.core.model.domain.DomainMetaRegistryJS
import com.gridnine.jasmine.server.standard.model.domain.BaseWorkspaceSimpleCriterionValueJS
import com.gridnine.jasmine.server.standard.model.domain.SimpleWorkspaceCriterionJS
import com.gridnine.jasmine.server.standard.model.domain.WorkspaceSimpleCriterionConditionJS
import com.gridnine.jasmine.web.core.reflection.ReflectionFactoryJS
import com.gridnine.jasmine.web.core.ui.UiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.WebComponent
import com.gridnine.jasmine.web.core.ui.components.WebDivsContainer
import com.gridnine.jasmine.web.core.ui.components.WebTableBox
import com.gridnine.jasmine.web.core.ui.components.WebTableBoxCell
import com.gridnine.jasmine.web.core.ui.widgets.GeneralSelectWidget
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS

class SimpleCriterionHandler(private val tableBox: WebTableBox, private val listId: String, private val initData: SimpleWorkspaceCriterionJS?) : CriterionHandler<SimpleWorkspaceCriterionJS> {
    private val uuid = MiscUtilsJS.createUUID()

    private val properties = arrayListOf<CriterionPropertyWrapper>()

    private var lastEditor: SimpleCriterionValueEditor<*>? = null

    private val valueDivId= "valueDiv$uuid"
    private lateinit var valueControl:WebDivsContainer
    private lateinit var  propertySelect:GeneralSelectWidget
    private lateinit var  conditionSelect:GeneralSelectWidget

    init {
        val indexDescription = DomainMetaRegistryJS.get().indexes[listId] ?: DomainMetaRegistryJS.get().assets[listId]
        ?: error("no description for $listId")
        indexDescription.properties.values.forEach {
            properties.add(CriterionPropertyWrapper(it.id, it.displayName, false, it.type, null, it.className))
        }
        indexDescription.collections.values.forEach {
            properties.add(CriterionPropertyWrapper(it.id, it.displayName, false, null, it.elementType, it.elementClassName))
        }
        properties.sortBy { it.text }
    }

    override fun getComponents(): MutableList<WebTableBoxCell> {

        val result = arrayListOf<WebTableBoxCell>()
        propertySelect = GeneralSelectWidget(tableBox) {
            width = "100%"
            showClearIcon = false
        }
        propertySelect.setPossibleValues(properties.map { SelectItemJS(it.id, it.text) })
        result.add(WebTableBoxCell(propertySelect))
        conditionSelect = GeneralSelectWidget(tableBox) {
            width = "100%"
            showClearIcon = false
        }
        result.add(WebTableBoxCell(conditionSelect))
        valueControl = UiLibraryAdapter.get().createDivsContainer(tableBox) {
            width = "100%"
        }
        result.add(WebTableBoxCell(valueControl))
        initData?.property?.let { property ->
            val propWrapper = properties.find { it.id == property }
            propWrapper?.let {
                propertySelect.setValue(SelectItemJS(it.id, it.text))
                conditionSelect.setPossibleValues(getConditions(it))
                initData?.condition?.let {
                    conditionSelect.setValue(SelectItemJS(it.name, getDisplayName(it)))
                    setEditor(propWrapper, it, initData.value)
                }
            }
        }
        propertySelect.changeListener = {prop ->
            conditionSelect.setPossibleValues(properties.find { it.id == prop?.id }?.let { getConditions(it) }?: emptyList())
        }
        conditionSelect.changeListener = {
            setEditor(properties.find { it.id == propertySelect.getValue()?.id },
                    it?.id?.let {toCondition(it)}, null)
        }
        return result
    }

    private fun toCondition(id:String):WorkspaceSimpleCriterionConditionJS{
        return ReflectionFactoryJS.get().getEnum<WorkspaceSimpleCriterionConditionJS>("com.gridnine.jasmine.server.standard.model.domain.WorkspaceSimpleCriterionConditionJS", id)
    }

    private fun setEditor(property: CriterionPropertyWrapper?, condition: WorkspaceSimpleCriterionConditionJS?, value: BaseWorkspaceSimpleCriterionValueJS?) {
        val editorType = getEditorType(property, condition)
        if(lastEditor == null || lastEditor!!.getType() != editorType){
            if(lastEditor != null) {
                val div = valueControl.getDiv(valueDivId)
                if (div != null) {
                    valueControl.removeDiv(valueDivId)
                }
            }
            lastEditor = when(editorType){
                SimpleCriterionValueType.NULL -> NullValueEditor(valueControl)
                SimpleCriterionValueType.STRING_VALUES -> StringValuesValueEditor(valueControl)
                SimpleCriterionValueType.ENUM_VALUES -> EnumValuesValueEditor(valueControl, property!!.className!!)
                SimpleCriterionValueType.INT_VALUE -> IntValueEditor(valueControl)
                SimpleCriterionValueType.LONG_VALUE ->  LongValueEditor(valueControl)
                SimpleCriterionValueType.BIG_DECIMAL_VALUE -> FloatlValueEditor(valueControl)
                SimpleCriterionValueType.ENTITY_REFERENCE_VALUES -> EntityReferenceValuesValueEditor(valueControl, property!!.className!!)
                SimpleCriterionValueType.DATE_VALUE -> DateValueEditor(valueControl)
                SimpleCriterionValueType.DATE_INTERVAL -> DateIntervalEditor(valueControl)
                SimpleCriterionValueType.DATE_TIME_VALUE -> DateTimeValueEditor(valueControl)
                SimpleCriterionValueType.DATE_TIME_INTERVAL -> DateTimeIntervalEditor(valueControl)
            }
            valueControl.addDiv(valueDivId, lastEditor!!)
            valueControl.show(valueDivId)
        }
        (lastEditor as SimpleCriterionValueEditor<BaseWorkspaceSimpleCriterionValueJS>).setValue(value)
    }

    private fun getEditorType(property: CriterionPropertyWrapper?, condition: WorkspaceSimpleCriterionConditionJS?): SimpleCriterionValueType {
        if(property == null || condition == null){
            return  SimpleCriterionValueType.NULL
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
                    WorkspaceSimpleCriterionConditionJS.NOT_CONTAINS -> SimpleCriterionValueType.STRING_VALUES
                    WorkspaceSimpleCriterionConditionJS.SET,WorkspaceSimpleCriterionConditionJS.NOT_SET -> SimpleCriterionValueType.NULL
                    else ->throw IllegalArgumentException("unsupported condition $condition")
                }
            DatabasePropertyTypeJS.ENUM ->
                when(condition) {
                    WorkspaceSimpleCriterionConditionJS.EQUALS, WorkspaceSimpleCriterionConditionJS.NOT_EQUALS -> SimpleCriterionValueType.ENUM_VALUES
                    WorkspaceSimpleCriterionConditionJS.SET,WorkspaceSimpleCriterionConditionJS.NOT_SET -> SimpleCriterionValueType.NULL
                    else ->throw IllegalArgumentException("unsupported condition $condition")
                }
            DatabasePropertyTypeJS.INT -> {
                when(condition){
                    WorkspaceSimpleCriterionConditionJS.EQUALS, WorkspaceSimpleCriterionConditionJS.NOT_EQUALS,  WorkspaceSimpleCriterionConditionJS.GREATER_THAN_OR_EQUALS,
                    WorkspaceSimpleCriterionConditionJS.GREATER_THAN, WorkspaceSimpleCriterionConditionJS.LESS_THAN_OR_EQUALS, WorkspaceSimpleCriterionConditionJS.LESS_THAN -> SimpleCriterionValueType.INT_VALUE
                    WorkspaceSimpleCriterionConditionJS.SET,WorkspaceSimpleCriterionConditionJS.NOT_SET -> SimpleCriterionValueType.NULL
                    else ->throw IllegalArgumentException("unsupported condition $condition")
                }
            }
            DatabasePropertyTypeJS.BIG_DECIMAL -> {
                when(condition){
                    WorkspaceSimpleCriterionConditionJS.EQUALS, WorkspaceSimpleCriterionConditionJS.NOT_EQUALS,  WorkspaceSimpleCriterionConditionJS.GREATER_THAN_OR_EQUALS,
                    WorkspaceSimpleCriterionConditionJS.GREATER_THAN, WorkspaceSimpleCriterionConditionJS.LESS_THAN_OR_EQUALS, WorkspaceSimpleCriterionConditionJS.LESS_THAN -> SimpleCriterionValueType.BIG_DECIMAL_VALUE
                    WorkspaceSimpleCriterionConditionJS.SET,WorkspaceSimpleCriterionConditionJS.NOT_SET -> SimpleCriterionValueType.NULL
                    else ->throw IllegalArgumentException("unsupported condition $condition")
                }
            }
            DatabasePropertyTypeJS.ENTITY_REFERENCE -> {
                when(condition) {
                    WorkspaceSimpleCriterionConditionJS.EQUALS, WorkspaceSimpleCriterionConditionJS.NOT_EQUALS -> SimpleCriterionValueType.ENTITY_REFERENCE_VALUES
                    WorkspaceSimpleCriterionConditionJS.SET,WorkspaceSimpleCriterionConditionJS.NOT_SET -> SimpleCriterionValueType.NULL
                    else ->throw IllegalArgumentException("unsupported condition $condition")
                }
            }
            DatabasePropertyTypeJS.LOCAL_DATE_TIME -> {
                when(condition){
                    WorkspaceSimpleCriterionConditionJS.EQUALS, WorkspaceSimpleCriterionConditionJS.NOT_EQUALS,WorkspaceSimpleCriterionConditionJS.GREATER_THAN, WorkspaceSimpleCriterionConditionJS.GREATER_THAN_OR_EQUALS,
                    WorkspaceSimpleCriterionConditionJS.LESS_THAN,WorkspaceSimpleCriterionConditionJS.LESS_THAN_OR_EQUALS->SimpleCriterionValueType.DATE_TIME_VALUE
                    WorkspaceSimpleCriterionConditionJS.SET,WorkspaceSimpleCriterionConditionJS.NOT_SET -> SimpleCriterionValueType.NULL
                    WorkspaceSimpleCriterionConditionJS.WITHIN_PERIOD -> SimpleCriterionValueType.DATE_TIME_INTERVAL
                    else ->throw IllegalArgumentException("unsupported condition $condition")
                }
            }
            DatabasePropertyTypeJS.LOCAL_DATE -> {
                when(condition){
                    WorkspaceSimpleCriterionConditionJS.EQUALS, WorkspaceSimpleCriterionConditionJS.NOT_EQUALS,WorkspaceSimpleCriterionConditionJS.GREATER_THAN, WorkspaceSimpleCriterionConditionJS.GREATER_THAN_OR_EQUALS,
                    WorkspaceSimpleCriterionConditionJS.LESS_THAN,WorkspaceSimpleCriterionConditionJS.LESS_THAN_OR_EQUALS->SimpleCriterionValueType.DATE_VALUE
                    WorkspaceSimpleCriterionConditionJS.SET,WorkspaceSimpleCriterionConditionJS.NOT_SET -> SimpleCriterionValueType.NULL
                    WorkspaceSimpleCriterionConditionJS.WITHIN_PERIOD -> SimpleCriterionValueType.DATE_INTERVAL
                    else ->throw IllegalArgumentException("unsupported condition $condition")
                }
            }
            DatabasePropertyTypeJS.BOOLEAN -> SimpleCriterionValueType.NULL
            DatabasePropertyTypeJS.LONG -> {
                when(condition){
                    WorkspaceSimpleCriterionConditionJS.EQUALS, WorkspaceSimpleCriterionConditionJS.NOT_EQUALS,  WorkspaceSimpleCriterionConditionJS.GREATER_THAN_OR_EQUALS,
                    WorkspaceSimpleCriterionConditionJS.GREATER_THAN, WorkspaceSimpleCriterionConditionJS.LESS_THAN_OR_EQUALS, WorkspaceSimpleCriterionConditionJS.LESS_THAN -> SimpleCriterionValueType.LONG_VALUE
                    WorkspaceSimpleCriterionConditionJS.SET,WorkspaceSimpleCriterionConditionJS.NOT_SET -> SimpleCriterionValueType.NULL
                    else ->throw IllegalArgumentException("unsupported condition $condition")
                }
            }
        }
    }

    private fun getDisplayName(condition:WorkspaceSimpleCriterionConditionJS):String{
        return DomainMetaRegistryJS.get().enums["com.gridnine.jasmine.server.standard.model.domain.WorkspaceSimpleCriterionConditionJS"]!!.items[condition.name]!!.displayName
    }
    override fun getId(): String {
        return uuid
    }

    private fun getConditions(property: CriterionPropertyWrapper): List<SelectItemJS> {
        return getConditionsInternal(property).map { SelectItemJS(it.name,getDisplayName(it) ) }
                .sortedBy { it.text }
    }

    private fun getConditionsInternal(property: CriterionPropertyWrapper): List<WorkspaceSimpleCriterionConditionJS> {
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

    override fun getData(): SimpleWorkspaceCriterionJS? {
        val property = propertySelect.getValue()?.id?: return null
        val condition = conditionSelect.getValue()?.id?.let { toCondition(it) }?:return null
        val result = SimpleWorkspaceCriterionJS()
        result.property = property
        result.condition = condition
        if(lastEditor?.getType() == SimpleCriterionValueType.NULL){
            return result
        }
        result.value = lastEditor?.getValue()?:return null
        return result
    }

}

class CriterionPropertyWrapper(val id: String, val text: String, val collection: Boolean, val propertyType: DatabasePropertyTypeJS?, val collectionType: DatabaseCollectionTypeJS?, val className: String?)

interface SimpleCriterionValueEditor<T:BaseWorkspaceSimpleCriterionValueJS>:WebComponent{
    fun getType():SimpleCriterionValueType
    fun setValue(value: T?)
    fun getValue():T?
}

enum class SimpleCriterionValueType{
    NULL,
    STRING_VALUES,
    ENUM_VALUES,
    INT_VALUE,
    LONG_VALUE,
    BIG_DECIMAL_VALUE,
    ENTITY_REFERENCE_VALUES,
    DATE_VALUE,
    DATE_INTERVAL,
    DATE_TIME_VALUE,
    DATE_TIME_INTERVAL
}