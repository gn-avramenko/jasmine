/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.standard.rest

import com.gridnine.jasmine.common.core.app.Registry
import com.gridnine.jasmine.common.core.model.Xeption
import com.gridnine.jasmine.common.standard.model.domain.*
import com.gridnine.jasmine.common.standard.model.rest.*


abstract class BaseWorkspaceItemListFromDTConverter<M: BaseListWorkspaceItem, DT: BaseListWorkspaceItemDT>  : WorkspaceItemFromDtConverter<M,DT>{
    override fun convert(item: DT): M {
        val result = createModel()
        result.uid = item.uid!!
        result.displayName = item.displayName
        result.columns.addAll(item.columns)
        result.filters.addAll(item.filters)
        result.listId = item.listId
        item.sortOrders.forEach {
            result.sortOrders.add(SortOrder().apply {
                field = it.field
                orderType  = it.orderType
            })
        }
        item.criterions.forEach {
            result.criterions.add(createCriterion(it))
        }
        return result
    }

    private fun createCriterion(it: BaseWorkspaceCriterionDT):  BaseWorkspaceCriterion{
        return when(it){
            is SimpleWorkspaceCriterionDT ->{
                val res = SimpleWorkspaceCriterion()
                res.property = it.property
                res.condition = it.condition
                val cValue = it.value
                res.value = when(cValue){
                    is WorkspaceSimpleCriterionLongValueDT -> WorkspaceSimpleCriterionLongValue().apply {
                        value = cValue.value
                    }
                    is WorkspaceSimpleCriterionEntityValuesDT -> WorkspaceSimpleCriterionEntityValues().apply{
                        values.addAll(cValue.values)
                    }
                    is WorkspaceSimpleCriterionEnumValuesDT -> WorkspaceSimpleCriterionEnumValues().apply {
                        values.addAll(cValue.values)
                    }
                    is WorkspaceSimpleCriterionIntValueDT -> WorkspaceSimpleCriterionIntValue().apply {
                        value = cValue.value
                    }
                    is WorkspaceSimpleCriterionDateIntervalValueDT -> WorkspaceSimpleCriterionDateIntervalValue().apply{
                        startDate = cValue.startDate
                        endDate = cValue.endDate
                    }
                    is WorkspaceSimpleCriterionDateValueDT -> WorkspaceSimpleCriterionDateValue().apply {
                        value = cValue.value
                    }
                    is WorkspaceSimpleCriterionStringValuesDT -> WorkspaceSimpleCriterionStringValues().apply {
                        values.addAll(cValue.values)
                    }
                    is WorkspaceSimpleCriterionDateTimeValueDT -> WorkspaceSimpleCriterionDateTimeValue().apply {
                        value = cValue.value
                    }
                    is WorkspaceSimpleCriterionFloatValueDT -> WorkspaceSimpleCriterionFloatValue().apply {
                        value = cValue.value
                    }
                    is WorkspaceSimpleCriterionDateTimeIntervalValueDT -> WorkspaceSimpleCriterionDateTimeIntervalValue().apply {
                        startDate = cValue.startDate
                        endDate = cValue.endDate
                    }
                    else -> null
                }
                res
            }
            is AndWorkspaceCriterionDT -> {
                val res = AndWorkspaceCriterion()
                it.criterions.forEach {
                    res.criterions.add(createCriterion(it))
                }
                res
            }
            is OrWorkspaceCriterionDT -> {
                val res = OrWorkspaceCriterion()
                it.criterions.forEach {
                    res.criterions.add(createCriterion(it))
                }
                res
            }
            is NotWorkspaceCriterionDT -> {
                val res = NotWorkspaceCriterion()
                it.criterions.forEach {
                    res.criterions.add(createCriterion(it))
                }
                res
            }
            is DynamicWorkspaceCriterionDT ->{
                val res = DynamicWorkspaceCriterion()
                res.propertyId = it.propertyId
                res.conditionId = it.conditionId
                res.handlerId = it.handlerId
                res.value = Registry.get().get(WorkspaceDynamicValueFromDtConverter.TYPE, it.value.javaClass.name)!!.convert(it.value)
                res
            }
            else -> throw Xeption.forDeveloper("unsupported criterion $it")
        }
    }

    abstract fun createModel():M
}

class WorkspaceListItemFromDTConverter : BaseWorkspaceItemListFromDTConverter<ListWorkspaceItem, ListWorkspaceItemDT>(){

    override fun getId(): String {
        return ListWorkspaceItem::class.qualifiedName!!
    }

    override fun createModel(): ListWorkspaceItem {
       return ListWorkspaceItem()
    }

}