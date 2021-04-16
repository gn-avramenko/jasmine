/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.standard.ui.mainframe.workspace

import com.gridnine.jasmine.common.core.meta.DatabaseCollectionType
import com.gridnine.jasmine.common.core.meta.DatabasePropertyType
import com.gridnine.jasmine.common.core.meta.DomainMetaRegistry
import com.gridnine.jasmine.common.core.model.SelectItem
import com.gridnine.jasmine.common.standard.model.domain.BaseWorkspaceSimpleCriterionValue
import com.gridnine.jasmine.common.standard.model.domain.SimpleWorkspaceCriterion
import com.gridnine.jasmine.common.standard.model.domain.WorkspaceSimpleCriterionCondition
import com.gridnine.jasmine.server.core.ui.components.DivsContainer
import com.gridnine.jasmine.server.core.ui.components.Table
import com.gridnine.jasmine.server.core.ui.components.TableCell
import com.gridnine.jasmine.server.core.ui.components.UiLibraryAdapter
import com.gridnine.jasmine.server.core.ui.widgets.GeneralSelectBoxValueWidget
import java.util.*

@Suppress("UNCHECKED_CAST", "UNREACHABLE_CODE")
class SimpleCriterionHandler(private val tableBox: Table, listId: String, private val initData: SimpleWorkspaceCriterion?) : UiCriterionHandler<SimpleWorkspaceCriterion> {
    private val uuid = UUID.randomUUID().toString()

    private val properties = arrayListOf<CriterionPropertyWrapper>()

    private var lastEditor: SimpleCriterionValueEditor<*>? = null

    private val valueDivId= "valueDiv$uuid"
    private lateinit var valueControl:DivsContainer
    private lateinit var  propertySelect:GeneralSelectBoxValueWidget
    private lateinit var  conditionSelect:GeneralSelectBoxValueWidget

    init {
        val indexDescription = DomainMetaRegistry.get().indexes[listId] ?: DomainMetaRegistry.get().assets[listId]
        ?: error("no description for $listId")
        indexDescription.properties.values.forEach {
            properties.add(CriterionPropertyWrapper(it.id, it.getDisplayName()!!, false, it.type, null, it.className))
        }
        indexDescription.collections.values.forEach {
            properties.add(CriterionPropertyWrapper(it.id, it.getDisplayName()!!, false, null, it.elementType, it.elementClassName))
        }
        properties.sortBy { it.text }
    }

    override fun getComponents(): MutableList<TableCell> {

        val result = arrayListOf<TableCell>()
        propertySelect = GeneralSelectBoxValueWidget {
            width = "100%"
            showClearIcon = false
            showAllPossibleValues = true
        }
        propertySelect.setPossibleValues(properties.map { SelectItem(it.id, it.text) })
        result.add(TableCell(propertySelect))
        conditionSelect = GeneralSelectBoxValueWidget {
            width = "100%"
            showClearIcon = false
            showAllPossibleValues = true
        }
        result.add(TableCell(conditionSelect))
        valueControl = UiLibraryAdapter.get().createDivsContainer{
            width = "100%"
        }
        result.add(TableCell(valueControl))
        initData?.property?.let { property ->
            val propWrapper = properties.find { it.id == property }
            propWrapper?.let {
                propertySelect.setValue(SelectItem(it.id, it.text))
                conditionSelect.setPossibleValues(getConditions(it))
                initData.condition?.let { crit ->
                    conditionSelect.setValue(SelectItem(crit.name, getDisplayName(crit)))
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
            val condition = it?.id?.let {toCondition(it)}
            setEditor(property,condition, null)
        }
        return result
    }

    private fun toCondition(id:String):WorkspaceSimpleCriterionCondition{
        return WorkspaceSimpleCriterionCondition.valueOf(id)
    }

    private fun setEditor(property: CriterionPropertyWrapper?, condition: WorkspaceSimpleCriterionCondition?, value: BaseWorkspaceSimpleCriterionValue?) {
        val editorType = getEditorType(property, condition)
        if(lastEditor == null || lastEditor!!.getType() != editorType){
            if(lastEditor != null) {
                val div = valueControl.getDiv(valueDivId)
                if (div != null) {
                    valueControl.removeDiv(valueDivId)
                }
            }
            lastEditor = when(editorType){
                SimpleCriterionValueType.NULL -> NullValueEditor()
                SimpleCriterionValueType.STRING_VALUES -> StringValuesValueEditor()
                SimpleCriterionValueType.ENUM_VALUES -> EnumValuesValueEditor(property!!.className!!)
                SimpleCriterionValueType.INT_VALUE -> IntValueEditor()
                SimpleCriterionValueType.LONG_VALUE ->  LongValueEditor()
                SimpleCriterionValueType.BIG_DECIMAL_VALUE -> FloatValueEditor()
                SimpleCriterionValueType.ENTITY_REFERENCE_VALUES -> EntityReferenceValuesValueEditor( property!!.className!!)
                SimpleCriterionValueType.DATE_VALUE -> DateValueEditor()
                SimpleCriterionValueType.DATE_INTERVAL -> DateIntervalEditor()
                SimpleCriterionValueType.DATE_TIME_VALUE -> DateTimeValueEditor()
                SimpleCriterionValueType.DATE_TIME_INTERVAL -> DateTimeIntervalEditor()
            }
            valueControl.addDiv(valueDivId, lastEditor!!)
            valueControl.show(valueDivId)
        }
        (lastEditor as SimpleCriterionValueEditor<BaseWorkspaceSimpleCriterionValue>).setValue(value)
    }

    private fun getEditorType(property: CriterionPropertyWrapper?, condition: WorkspaceSimpleCriterionCondition?): SimpleCriterionValueType {
        if(property == null || condition == null){
            return  SimpleCriterionValueType.NULL
        }
        if(property.collection){
            return TODO()
        }
        return when(property.propertyType!!){
            DatabasePropertyType.STRING,DatabasePropertyType.TEXT ->
                when(condition){
                    WorkspaceSimpleCriterionCondition.EQUALS,
                    WorkspaceSimpleCriterionCondition.NOT_EQUALS,
                    WorkspaceSimpleCriterionCondition.CONTAINS,
                    WorkspaceSimpleCriterionCondition.NOT_CONTAINS -> SimpleCriterionValueType.STRING_VALUES
                    WorkspaceSimpleCriterionCondition.SET,WorkspaceSimpleCriterionCondition.NOT_SET -> SimpleCriterionValueType.NULL
                    else ->throw IllegalArgumentException("unsupported condition $condition")
                }
            DatabasePropertyType.ENUM ->
                when(condition) {
                    WorkspaceSimpleCriterionCondition.EQUALS, WorkspaceSimpleCriterionCondition.NOT_EQUALS -> SimpleCriterionValueType.ENUM_VALUES
                    WorkspaceSimpleCriterionCondition.SET,WorkspaceSimpleCriterionCondition.NOT_SET -> SimpleCriterionValueType.NULL
                    else ->throw IllegalArgumentException("unsupported condition $condition")
                }
            DatabasePropertyType.INT -> {
                when(condition){
                    WorkspaceSimpleCriterionCondition.EQUALS, WorkspaceSimpleCriterionCondition.NOT_EQUALS,  WorkspaceSimpleCriterionCondition.GREATER_THAN_OR_EQUALS,
                    WorkspaceSimpleCriterionCondition.GREATER_THAN, WorkspaceSimpleCriterionCondition.LESS_THAN_OR_EQUALS, WorkspaceSimpleCriterionCondition.LESS_THAN -> SimpleCriterionValueType.INT_VALUE
                    WorkspaceSimpleCriterionCondition.SET,WorkspaceSimpleCriterionCondition.NOT_SET -> SimpleCriterionValueType.NULL
                    else ->throw IllegalArgumentException("unsupported condition $condition")
                }
            }
            DatabasePropertyType.BIG_DECIMAL -> {
                when(condition){
                    WorkspaceSimpleCriterionCondition.EQUALS, WorkspaceSimpleCriterionCondition.NOT_EQUALS,  WorkspaceSimpleCriterionCondition.GREATER_THAN_OR_EQUALS,
                    WorkspaceSimpleCriterionCondition.GREATER_THAN, WorkspaceSimpleCriterionCondition.LESS_THAN_OR_EQUALS, WorkspaceSimpleCriterionCondition.LESS_THAN -> SimpleCriterionValueType.BIG_DECIMAL_VALUE
                    WorkspaceSimpleCriterionCondition.SET,WorkspaceSimpleCriterionCondition.NOT_SET -> SimpleCriterionValueType.NULL
                    else ->throw IllegalArgumentException("unsupported condition $condition")
                }
            }
            DatabasePropertyType.ENTITY_REFERENCE -> {
                when(condition) {
                    WorkspaceSimpleCriterionCondition.EQUALS, WorkspaceSimpleCriterionCondition.NOT_EQUALS -> SimpleCriterionValueType.ENTITY_REFERENCE_VALUES
                    WorkspaceSimpleCriterionCondition.SET,WorkspaceSimpleCriterionCondition.NOT_SET -> SimpleCriterionValueType.NULL
                    else ->throw IllegalArgumentException("unsupported condition $condition")
                }
            }
            DatabasePropertyType.LOCAL_DATE_TIME -> {
                when(condition){
                    WorkspaceSimpleCriterionCondition.EQUALS, WorkspaceSimpleCriterionCondition.NOT_EQUALS,WorkspaceSimpleCriterionCondition.GREATER_THAN, WorkspaceSimpleCriterionCondition.GREATER_THAN_OR_EQUALS,
                    WorkspaceSimpleCriterionCondition.LESS_THAN,WorkspaceSimpleCriterionCondition.LESS_THAN_OR_EQUALS->SimpleCriterionValueType.DATE_TIME_VALUE
                    WorkspaceSimpleCriterionCondition.SET,WorkspaceSimpleCriterionCondition.NOT_SET -> SimpleCriterionValueType.NULL
                    WorkspaceSimpleCriterionCondition.WITHIN_PERIOD -> SimpleCriterionValueType.DATE_TIME_INTERVAL
                    else ->throw IllegalArgumentException("unsupported condition $condition")
                }
            }
            DatabasePropertyType.LOCAL_DATE -> {
                when(condition){
                    WorkspaceSimpleCriterionCondition.EQUALS, WorkspaceSimpleCriterionCondition.NOT_EQUALS,WorkspaceSimpleCriterionCondition.GREATER_THAN, WorkspaceSimpleCriterionCondition.GREATER_THAN_OR_EQUALS,
                    WorkspaceSimpleCriterionCondition.LESS_THAN,WorkspaceSimpleCriterionCondition.LESS_THAN_OR_EQUALS->SimpleCriterionValueType.DATE_VALUE
                    WorkspaceSimpleCriterionCondition.SET,WorkspaceSimpleCriterionCondition.NOT_SET -> SimpleCriterionValueType.NULL
                    WorkspaceSimpleCriterionCondition.WITHIN_PERIOD -> SimpleCriterionValueType.DATE_INTERVAL
                    else ->throw IllegalArgumentException("unsupported condition $condition")
                }
            }
            DatabasePropertyType.BOOLEAN -> SimpleCriterionValueType.NULL
            DatabasePropertyType.LONG -> {
                when(condition){
                    WorkspaceSimpleCriterionCondition.EQUALS, WorkspaceSimpleCriterionCondition.NOT_EQUALS,  WorkspaceSimpleCriterionCondition.GREATER_THAN_OR_EQUALS,
                    WorkspaceSimpleCriterionCondition.GREATER_THAN, WorkspaceSimpleCriterionCondition.LESS_THAN_OR_EQUALS, WorkspaceSimpleCriterionCondition.LESS_THAN -> SimpleCriterionValueType.LONG_VALUE
                    WorkspaceSimpleCriterionCondition.SET,WorkspaceSimpleCriterionCondition.NOT_SET -> SimpleCriterionValueType.NULL
                    else ->throw IllegalArgumentException("unsupported condition $condition")
                }
            }
        }
    }

    private fun getDisplayName(condition:WorkspaceSimpleCriterionCondition):String{
        return DomainMetaRegistry.get().enums[WorkspaceSimpleCriterionCondition::class.qualifiedName]!!.items[condition.name]!!.getDisplayName()!!
    }
    override fun getId(): String {
        return uuid
    }

    private fun getConditions(property: CriterionPropertyWrapper): List<SelectItem> {
        return getConditionsInternal(property).map { SelectItem(it.name,getDisplayName(it) ) }
                .sortedBy { it.text }
    }

    private fun getConditionsInternal(property: CriterionPropertyWrapper): List<WorkspaceSimpleCriterionCondition> {
        if (property.collection) {
            return when (property.collectionType!!) {
                DatabaseCollectionType.STRING -> arrayListOf(WorkspaceSimpleCriterionCondition.SET, WorkspaceSimpleCriterionCondition.NOT_SET, WorkspaceSimpleCriterionCondition.CONTAINS, WorkspaceSimpleCriterionCondition.NOT_CONTAINS)
                DatabaseCollectionType.ENUM -> arrayListOf(WorkspaceSimpleCriterionCondition.SET, WorkspaceSimpleCriterionCondition.NOT_SET, WorkspaceSimpleCriterionCondition.CONTAINS, WorkspaceSimpleCriterionCondition.NOT_CONTAINS)
                DatabaseCollectionType.ENTITY_REFERENCE -> arrayListOf(WorkspaceSimpleCriterionCondition.SET, WorkspaceSimpleCriterionCondition.NOT_SET, WorkspaceSimpleCriterionCondition.CONTAINS, WorkspaceSimpleCriterionCondition.NOT_CONTAINS)
            }
        }
        return when (property.propertyType!!) {
            DatabasePropertyType.STRING -> arrayListOf(WorkspaceSimpleCriterionCondition.EQUALS, WorkspaceSimpleCriterionCondition.NOT_EQUALS, WorkspaceSimpleCriterionCondition.SET, WorkspaceSimpleCriterionCondition.NOT_SET, WorkspaceSimpleCriterionCondition.CONTAINS, WorkspaceSimpleCriterionCondition.NOT_CONTAINS)
            DatabasePropertyType.ENUM -> arrayListOf(WorkspaceSimpleCriterionCondition.EQUALS, WorkspaceSimpleCriterionCondition.NOT_EQUALS, WorkspaceSimpleCriterionCondition.SET, WorkspaceSimpleCriterionCondition.NOT_SET)
            DatabasePropertyType.INT -> arrayListOf(WorkspaceSimpleCriterionCondition.EQUALS, WorkspaceSimpleCriterionCondition.NOT_EQUALS, WorkspaceSimpleCriterionCondition.SET, WorkspaceSimpleCriterionCondition.NOT_SET, WorkspaceSimpleCriterionCondition.GREATER_THAN_OR_EQUALS, WorkspaceSimpleCriterionCondition.GREATER_THAN, WorkspaceSimpleCriterionCondition.LESS_THAN_OR_EQUALS, WorkspaceSimpleCriterionCondition.LESS_THAN)
            DatabasePropertyType.BIG_DECIMAL -> arrayListOf(WorkspaceSimpleCriterionCondition.EQUALS, WorkspaceSimpleCriterionCondition.NOT_EQUALS, WorkspaceSimpleCriterionCondition.SET, WorkspaceSimpleCriterionCondition.NOT_SET, WorkspaceSimpleCriterionCondition.GREATER_THAN_OR_EQUALS, WorkspaceSimpleCriterionCondition.GREATER_THAN, WorkspaceSimpleCriterionCondition.LESS_THAN_OR_EQUALS, WorkspaceSimpleCriterionCondition.LESS_THAN)
            DatabasePropertyType.ENTITY_REFERENCE -> arrayListOf(WorkspaceSimpleCriterionCondition.EQUALS, WorkspaceSimpleCriterionCondition.NOT_EQUALS, WorkspaceSimpleCriterionCondition.SET, WorkspaceSimpleCriterionCondition.NOT_SET)
            DatabasePropertyType.LOCAL_DATE_TIME -> arrayListOf(WorkspaceSimpleCriterionCondition.GREATER_THAN, WorkspaceSimpleCriterionCondition.GREATER_THAN_OR_EQUALS, WorkspaceSimpleCriterionCondition.SET, WorkspaceSimpleCriterionCondition.NOT_SET, WorkspaceSimpleCriterionCondition.LESS_THAN, WorkspaceSimpleCriterionCondition.LESS_THAN_OR_EQUALS, WorkspaceSimpleCriterionCondition.WITHIN_PERIOD)
            DatabasePropertyType.LOCAL_DATE -> arrayListOf(WorkspaceSimpleCriterionCondition.EQUALS, WorkspaceSimpleCriterionCondition.NOT_EQUALS, WorkspaceSimpleCriterionCondition.GREATER_THAN, WorkspaceSimpleCriterionCondition.GREATER_THAN_OR_EQUALS, WorkspaceSimpleCriterionCondition.SET, WorkspaceSimpleCriterionCondition.NOT_SET, WorkspaceSimpleCriterionCondition.LESS_THAN, WorkspaceSimpleCriterionCondition.LESS_THAN_OR_EQUALS, WorkspaceSimpleCriterionCondition.WITHIN_PERIOD)
            DatabasePropertyType.BOOLEAN -> arrayListOf(WorkspaceSimpleCriterionCondition.YES, WorkspaceSimpleCriterionCondition.NO, WorkspaceSimpleCriterionCondition.NOT_SET)
            DatabasePropertyType.TEXT -> arrayListOf(WorkspaceSimpleCriterionCondition.EQUALS, WorkspaceSimpleCriterionCondition.NOT_EQUALS, WorkspaceSimpleCriterionCondition.SET, WorkspaceSimpleCriterionCondition.NOT_SET, WorkspaceSimpleCriterionCondition.GREATER_THAN_OR_EQUALS, WorkspaceSimpleCriterionCondition.GREATER_THAN, WorkspaceSimpleCriterionCondition.LESS_THAN_OR_EQUALS, WorkspaceSimpleCriterionCondition.LESS_THAN)
            DatabasePropertyType.LONG -> arrayListOf(WorkspaceSimpleCriterionCondition.EQUALS, WorkspaceSimpleCriterionCondition.NOT_EQUALS, WorkspaceSimpleCriterionCondition.SET, WorkspaceSimpleCriterionCondition.NOT_SET, WorkspaceSimpleCriterionCondition.CONTAINS, WorkspaceSimpleCriterionCondition.NOT_CONTAINS)
        }
    }

    override fun getData(): SimpleWorkspaceCriterion? {
        val property = propertySelect.getValue()?.id?: return null
        val condition = conditionSelect.getValue()?.id?.let { toCondition(it) }?:return null
        val result = SimpleWorkspaceCriterion()
        result.property = property
        result.condition = condition
        if(lastEditor?.getType() == SimpleCriterionValueType.NULL){
            return result
        }
        result.value = lastEditor?.getValue()?:return null
        return result
    }

}

