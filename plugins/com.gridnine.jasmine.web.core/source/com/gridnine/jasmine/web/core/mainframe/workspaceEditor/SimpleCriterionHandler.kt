/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.mainframe.workspaceEditor

import com.gridnine.jasmine.server.core.model.common.SelectItemJS
import com.gridnine.jasmine.server.core.model.domain.DatabaseCollectionTypeJS
import com.gridnine.jasmine.server.core.model.domain.DatabasePropertyTypeJS
import com.gridnine.jasmine.server.core.model.domain.DomainMetaRegistryJS
import com.gridnine.jasmine.server.core.model.domain.IndexPropertyDescriptionJS
import com.gridnine.jasmine.server.standard.model.domain.SimpleWorkspaceCriterionJS
import com.gridnine.jasmine.server.standard.model.domain.WorkspaceSimpleCriterionConditionJS
import com.gridnine.jasmine.web.core.ui.UiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.components.WebTableBox
import com.gridnine.jasmine.web.core.ui.components.WebTableBoxCell
import com.gridnine.jasmine.web.core.ui.widgets.GeneralSelectWidget
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS

class SimpleCriterionHandler(private val tableBox: WebTableBox, private val listId: String, private val initData: SimpleWorkspaceCriterionJS?) : CriterionHandler<SimpleWorkspaceCriterionJS> {
    private val uuid = MiscUtilsJS.createUUID()

    private val properties = arrayListOf<CriterionPropertyWrapper>()

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
        val propertySelect = GeneralSelectWidget(tableBox) {
            width = "100%"
            showClearIcon = false
        }
        propertySelect.setPossibleValues(properties.map { SelectItemJS(it.id, it.text) })
        result.add(WebTableBoxCell(propertySelect))
        val conditionSelect = GeneralSelectWidget(tableBox) {
            width = "100%"
            showClearIcon = false
        }
        result.add(WebTableBoxCell(conditionSelect))
        val valueControl = UiLibraryAdapter.get().createDivsContainer(tableBox) {
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
                }
            }
        }
        propertySelect.changeListener = {prop ->
            conditionSelect.setPossibleValues(properties.find { it.id == prop?.id }?.let { getConditions(it) }?: emptyList())
        }
        return result
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
            DatabasePropertyTypeJS.BOOLEAN -> arrayListOf(WorkspaceSimpleCriterionConditionJS.EQUALS, WorkspaceSimpleCriterionConditionJS.NOT_EQUALS, WorkspaceSimpleCriterionConditionJS.NOT_SET)
            DatabasePropertyTypeJS.TEXT -> arrayListOf(WorkspaceSimpleCriterionConditionJS.EQUALS, WorkspaceSimpleCriterionConditionJS.NOT_EQUALS, WorkspaceSimpleCriterionConditionJS.SET, WorkspaceSimpleCriterionConditionJS.NOT_SET, WorkspaceSimpleCriterionConditionJS.GREATER_THAN_OR_EQUALS, WorkspaceSimpleCriterionConditionJS.GREATER_THAN, WorkspaceSimpleCriterionConditionJS.LESS_THAN_OR_EQUALS, WorkspaceSimpleCriterionConditionJS.LESS_THAN)
            DatabasePropertyTypeJS.LONG -> arrayListOf(WorkspaceSimpleCriterionConditionJS.EQUALS, WorkspaceSimpleCriterionConditionJS.NOT_EQUALS, WorkspaceSimpleCriterionConditionJS.SET, WorkspaceSimpleCriterionConditionJS.NOT_SET, WorkspaceSimpleCriterionConditionJS.CONTAINS, WorkspaceSimpleCriterionConditionJS.NOT_CONTAINS)
        }
    }

}

class CriterionPropertyWrapper(val id: String, val text: String, val collection: Boolean, val propertyType: DatabasePropertyTypeJS?, val collectionType: DatabaseCollectionTypeJS?, val className: String?)