/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.standard.rest

import com.gridnine.jasmine.common.core.app.Registry
import com.gridnine.jasmine.common.core.model.Xeption
import com.gridnine.jasmine.common.standard.model.domain.*
import com.gridnine.jasmine.common.standard.model.rest.*


abstract class BaseWorkspaceItemListToDTConverter<M: BaseListWorkspaceItem, DT: BaseListWorkspaceItemDT>  : WorkspaceItemToDtConverter<M,DT>{
    override fun convert(item: M): DT {
        val result = createDT()
        result.uid = item.uid
        result.displayName = item.displayName
        result.columns.addAll(item.columns)
        result.filters.addAll(item.filters)
        result.listId = item.listId
        item.sortOrders.forEach {
            result.sortOrders.add(SortOrderDT().apply {
                field = it.field
                orderType  = it.orderType
            })
        }
        item.criterions.forEach {
            result.criterions.add(createCriterion(it))
        }
        return result
    }

    private fun createCriterion(it: BaseWorkspaceCriterion): BaseWorkspaceCriterionDT {
        return when(it){
            is SimpleWorkspaceCriterion ->{
                val res = SimpleWorkspaceCriterionDT()
                res.property = it.property
                res.condition = it.condition
                val cValue = it.value
                res.value = when(cValue){
                    is WorkspaceSimpleCriterionLongValue -> WorkspaceSimpleCriterionLongValueDT().apply {
                        value = cValue.value
                    }
                    is WorkspaceSimpleCriterionEntityValues -> WorkspaceSimpleCriterionEntityValuesDT().apply{
                        values.addAll(cValue.values)
                    }
                    is WorkspaceSimpleCriterionEnumValues -> WorkspaceSimpleCriterionEnumValuesDT().apply {
                        values.addAll(cValue.values)
                    }
                    is WorkspaceSimpleCriterionIntValue -> WorkspaceSimpleCriterionIntValueDT().apply {
                        value = cValue.value
                    }
                    is WorkspaceSimpleCriterionDateIntervalValue -> WorkspaceSimpleCriterionDateIntervalValueDT().apply{
                        startDate = cValue.startDate
                        endDate = cValue.endDate
                    }
                    is WorkspaceSimpleCriterionDateValue -> WorkspaceSimpleCriterionDateValueDT().apply {
                        value = cValue.value
                    }
                    is WorkspaceSimpleCriterionStringValues -> WorkspaceSimpleCriterionStringValuesDT().apply {
                        values.addAll(cValue.values)
                    }
                    is WorkspaceSimpleCriterionDateTimeValue -> WorkspaceSimpleCriterionDateTimeValueDT().apply {
                        value = cValue.value
                    }
                    is WorkspaceSimpleCriterionFloatValue -> WorkspaceSimpleCriterionFloatValueDT().apply {
                        value = cValue.value
                    }
                    is WorkspaceSimpleCriterionDateTimeIntervalValue -> WorkspaceSimpleCriterionDateTimeIntervalValueDT().apply {
                        startDate = cValue.startDate
                        endDate = cValue.endDate
                    }
                    else -> null
                }
                res
            }
            is AndWorkspaceCriterion -> {
                val res = AndWorkspaceCriterionDT()
                it.criterions.forEach {
                    res.criterions.add(createCriterion(it))
                }
                res
            }
            is OrWorkspaceCriterion -> {
                val res = OrWorkspaceCriterionDT()
                it.criterions.forEach {
                    res.criterions.add(createCriterion(it))
                }
                res
            }
            is NotWorkspaceCriterion -> {
                val res = NotWorkspaceCriterionDT()
                it.criterions.forEach {
                    res.criterions.add(createCriterion(it))
                }
                res
            }
            is DynamicWorkspaceCriterion ->{
                val res = DynamicWorkspaceCriterionDT()
                res.propertyId = it.propertyId
                res.conditionId = it.conditionId
                res.handlerId = it.handlerId
                res.value = Registry.get().get(WorkspaceDynamicValueToDtConverter.TYPE, it.value.javaClass.name)!!.convert(it.value)
                res
            }
            else -> throw Xeption.forDeveloper("unsupported criterion $it")
        }
    }

    abstract fun createDT():DT
}

class WorkspaceListItemToDTConverter : BaseWorkspaceItemListToDTConverter<ListWorkspaceItem, ListWorkspaceItemDT>(){

    override fun getId(): String {
        return ListWorkspaceItem::class.qualifiedName!!
    }

    override fun createDT(): ListWorkspaceItemDT {
        return ListWorkspaceItemDT()
    }

}