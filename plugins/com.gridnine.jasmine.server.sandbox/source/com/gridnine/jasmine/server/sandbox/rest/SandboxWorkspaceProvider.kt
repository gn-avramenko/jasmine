/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused", "RemoveRedundantQualifierName", "UNCHECKED_CAST", "MemberVisibilityCanBePrivate", "RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.rest

import com.gridnine.jasmine.server.core.storage.Storage
import com.gridnine.jasmine.server.sandbox.model.domain.*
import com.gridnine.jasmine.server.standard.model.rest.*
import com.gridnine.jasmine.server.standard.rest.WorkspaceProvider
import kotlin.IllegalArgumentException

class SandboxWorkspaceProvider : WorkspaceProvider {
    override fun getWorkspace(): WorkspaceDT {
        val loginName = SandboxAuthFilter.getAuthInfo()?.loginName
                ?: throw IllegalArgumentException("user is not logged in")
        val workspace = Storage.get().loadDocument(Workspace::class, "${loginName}_workspace")
                ?: createDefaultWorkspace()
        val result = WorkspaceDT()
        workspace.groups.forEach { group ->
            val groupDT = WorkspaceGroupDT()
            groupDT.displayName = group.displayName
            result.groups.add(groupDT)
            group.items.forEach { item ->
                val itemDT = when (item) {
                    is ListWorkspaceItem -> {
                        val listItemDT = ListWorkspaceItemDT()
                        listItemDT.listId = item.listId
                        listItemDT.columns.addAll(item.columns)
                        listItemDT.filters.addAll(item.filters)
                        item.sortOrders.forEach { sortOrder ->
                            val sortOrderDT = SortOrderDT()
                            sortOrderDT.field = sortOrder.field
                            sortOrderDT.orderType = when (sortOrder.orderType) {
                                SortOrderType.ASC -> SortOrderTypeDT.ASC
                                SortOrderType.DESC -> SortOrderTypeDT.DESC
                                else -> throw IllegalArgumentException("unsupported sort order type ${sortOrder.orderType}")
                            }
                        }
                        listItemDT.criterions.addAll(toCriterions(item.criterions))

                        listItemDT
                    }
                    else -> throw IllegalArgumentException("unsupported item type $item")
                }
                itemDT.displayName = item.displayName
                groupDT.items.add(itemDT)
            }
        }
        return result
    }

    private fun toCriterions(criterions: ArrayList<BaseWorkspaceCriterion>): List<BaseWorkspaceCriterionDT> {
        val result = arrayListOf<BaseWorkspaceCriterionDT>()
        criterions.forEach { criterion ->
            result.add(when (criterion) {
                is OrWorkspaceCriterion -> {
                    val criterionDT = OrWorkspaceCriterionDT()
                    criterionDT.criterions.addAll(toCriterions(criterion.criterions))
                    criterionDT
                }
                is AndWorkspaceCriterion -> {
                    val criterionDT = AndWorkspaceCriterionDT()
                    criterionDT.criterions.addAll(toCriterions(criterion.criterions))
                    criterionDT
                }
                is NotWorkspaceCriterion -> {
                    val criterionDT = NotWorkspaceCriterionDT()
                    criterionDT.criterions.addAll(toCriterions(criterion.criterions))
                    criterionDT
                }
                is SimpleWorkspaceCriterion -> {
                    val criterionDT = SimpleWorkspaceCriterionDT()
                    criterionDT.property = criterion.property
                    criterionDT.condition = criterion.condition?.let { WorkspaceSimpleCriterionConditionDT.valueOf(it.name) }
                    criterionDT.value = when (val criterionValue = criterion.value) {
                        null -> null
                        is WorkspaceSimpleCriterionStringValues -> {
                            val valuedDT = WorkspaceSimpleCriterionStringValuesDT()
                            valuedDT.values.addAll(criterionValue.values)
                            valuedDT
                        }
                        is WorkspaceSimpleCriterionEntityValues -> {
                            val valuedDT = WorkspaceSimpleCriterionEntityValuesDT()
                            valuedDT.values.addAll(criterionValue.values)
                            valuedDT
                        }
                        is WorkspaceSimpleCriterionEnumValues  -> {
                            val value = WorkspaceSimpleCriterionEnumValuesDT()
                            value.values.addAll(criterionValue.values)
                            value.enumClassName  = criterionValue.enumClassName
                            value
                        }
                        is WorkspaceSimpleCriterionDateValue -> {
                            val valuedDT = WorkspaceSimpleCriterionDateValueDT()
                            valuedDT.value = criterionValue.value
                            valuedDT
                        }
                        is WorkspaceSimpleCriterionDateIntervalValue -> {
                            val valuedDT = WorkspaceSimpleCriterionDateIntervalValueDT()
                            valuedDT.startDate = criterionValue.startDate
                            valuedDT.endDate = criterionValue.endDate
                            valuedDT
                        }
                        is WorkspaceSimpleCriterionDateTimeValue -> {
                            val valuedDT = WorkspaceSimpleCriterionDateTimeValueDT()
                            valuedDT.value = criterionValue.value
                            valuedDT
                        }
                        is WorkspaceSimpleCriterionDateTimeIntervalValue -> {
                            val valuedDT = WorkspaceSimpleCriterionDateTimeIntervalValueDT()
                            valuedDT.startDate = criterionValue.startDate
                            valuedDT.endDate = criterionValue.endDate
                            valuedDT
                        }
                        is WorkspaceSimpleCriterionFloatValue -> {
                            val valuedDT = WorkspaceSimpleCriterionFloatValueDT()
                            valuedDT.value = criterionValue.value
                            valuedDT
                        }
                        is WorkspaceSimpleCriterionIntValue -> {
                            val valuedDT = WorkspaceSimpleCriterionIntValueDT()
                            valuedDT.value = criterionValue.value
                            valuedDT
                        }
                        else -> throw IllegalArgumentException("unsuported value $criterionValue")
                    }
                    criterionDT
                }
                else -> throw IllegalArgumentException("unsupported criterion $criterion")
            })
        }
        return result
    }

    private fun createDefaultWorkspace(): Workspace {
        val result = Workspace()
        run {
            val group = WorkspaceGroup()
            group.displayName = "Настройки"
            val item = ListWorkspaceItem()
            item.columns.add(SandboxUserAccountIndex.login.name)
            item.columns.add(SandboxUserAccountIndex.name.name)
            item.filters.add(SandboxUserAccountIndex.login.name)
            val order = SortOrder()
            order.orderType = SortOrderType.ASC
            order.field = SandboxUserAccountIndex.login.name
            item.sortOrders
            item.listId = "com.gridnine.jasmine.server.sandbox.model.ui.SandboxUserAccountList"
            item.displayName = "Профили"
            run {
                val criterion = SimpleWorkspaceCriterion()
                criterion.property = SandboxUserAccountIndex.login.name
                criterion.condition = WorkspaceSimpleCriterionCondition.EQUALS
                val value = WorkspaceSimpleCriterionStringValues()
                value.values.add("admin")
                criterion.value = value
                item.criterions.add(criterion)
            }
            group.items.add(item)
            result.groups.add(group)
        }
        run {
            val group = WorkspaceGroup()
            group.displayName = "Объекты"
            run {
                val item = ListWorkspaceItem()
                item.columns.add(SandboxComplexDocumentIndex.stringProperty.name)
                item.columns.add(SandboxComplexDocumentIndex.enumProperty.name)
                item.columns.add(SandboxComplexDocumentIndex.booleanProperty.name)
                item.columns.add(SandboxComplexDocumentIndex.dateProperty.name)
                item.columns.add(SandboxComplexDocumentIndex.dateTimeProperty.name)
                item.columns.add(SandboxComplexDocumentIndex.floatProperty.name)
                item.listId = "com.gridnine.jasmine.server.sandbox.model.ui.SandboxComplexDocumentList"
                item.displayName = "Сложные объекты"
                group.items.add(item)
            }
            run {
                val item = ListWorkspaceItem()
                item.columns.add(SandboxComplexDocumentVariantIndex.title.name)
                item.listId = "com.gridnine.jasmine.server.sandbox.model.ui.SandboxComplexDocumentVariantList"
                item.displayName = "Вложенные объекты"
                group.items.add(item)
            }
            result.groups.add(group)
        }
        return result
    }

    override fun saveWorkspace(workspace: WorkspaceDT): WorkspaceDT {
        val loginName = SandboxAuthFilter.getAuthInfo()?.loginName
                ?: throw IllegalArgumentException("user is not logged in")
        val result = Workspace()
        result.uid = "${loginName}_workspace"
        workspace.groups.forEach { groupDT ->
            val group = WorkspaceGroup()
            group.displayName = groupDT.displayName
            result.groups.add(group)
            groupDT.items.forEach { listItemDT ->
                val item = when (listItemDT) {
                    is ListWorkspaceItemDT -> {
                        val listItem = ListWorkspaceItem()
                        listItem.listId = listItemDT.listId
                        listItem.columns.addAll(listItemDT.columns)
                        listItem.filters.addAll(listItemDT.filters)
                        listItemDT.sortOrders.forEach { sortOrderDT ->
                            val sortOrder = SortOrder()
                            sortOrder.field = sortOrderDT.field
                            sortOrder.orderType = when (sortOrderDT.orderType) {
                                SortOrderTypeDT.ASC -> SortOrderType.ASC
                                SortOrderTypeDT.DESC -> SortOrderType.DESC
                                else -> throw IllegalArgumentException("unsupported sort order type ${sortOrderDT.orderType}")
                            }
                        }
                        listItem.criterions.addAll(fromCriterions(listItemDT.criterions))

                        listItem
                    }
                    else -> throw IllegalArgumentException("unsupported item type $listItemDT")
                }
                item.displayName = listItemDT.displayName
                group.items.add(item)
            }
        }
        Storage.get().saveDocument(result)
        return workspace
    }

    private fun fromCriterions(criterions: ArrayList<BaseWorkspaceCriterionDT>): List<BaseWorkspaceCriterion> {
        val result = arrayListOf<BaseWorkspaceCriterion>()
        criterions.forEach { criterionDT ->
            result.add(when (criterionDT) {
                is OrWorkspaceCriterionDT -> {
                    val criterion = OrWorkspaceCriterion()
                    criterion.criterions.addAll(fromCriterions(criterionDT.criterions))
                    criterion
                }
                is AndWorkspaceCriterionDT -> {
                    val criterion = AndWorkspaceCriterion()
                    criterion.criterions.addAll(fromCriterions(criterionDT.criterions))
                    criterion
                }
                is NotWorkspaceCriterionDT -> {
                    val criterion = NotWorkspaceCriterion()
                    criterion.criterions.addAll(fromCriterions(criterionDT.criterions))
                    criterion
                }
                is SimpleWorkspaceCriterionDT -> {
                    val criterion = SimpleWorkspaceCriterion()
                    criterion.property = criterionDT.property
                    criterion.condition = criterionDT.condition?.let { WorkspaceSimpleCriterionCondition.valueOf(it.name) }
                    criterion.value = when (val criterionValue = criterionDT.value) {
                        null -> null
                        is WorkspaceSimpleCriterionStringValuesDT -> {
                            val value = WorkspaceSimpleCriterionStringValues()
                            value.values.addAll(criterionValue.values)
                            value
                        }
                        is WorkspaceSimpleCriterionEntityValuesDT  -> {
                            val value = WorkspaceSimpleCriterionEntityValues()
                            value.values.addAll(criterionValue.values)
                            value
                        }
                        is WorkspaceSimpleCriterionEnumValuesDT  -> {
                            val value = WorkspaceSimpleCriterionEnumValues()
                            value.values.addAll(criterionValue.values)
                            value.enumClassName  = criterionValue.enumClassName
                            value
                        }
                        is WorkspaceSimpleCriterionDateValueDT  -> {
                            val value = WorkspaceSimpleCriterionDateValue()
                            value.value = criterionValue.value
                            value
                        }
                        is WorkspaceSimpleCriterionDateIntervalValueDT  -> {
                            val value = WorkspaceSimpleCriterionDateIntervalValue()
                            value.startDate = criterionValue.startDate
                            value.endDate = criterionValue.endDate
                            value
                        }
                        is WorkspaceSimpleCriterionDateTimeValueDT  -> {
                            val value = WorkspaceSimpleCriterionDateTimeValue()
                            value.value = criterionValue.value
                            value
                        }
                        is WorkspaceSimpleCriterionDateTimeIntervalValueDT  -> {
                            val value = WorkspaceSimpleCriterionDateTimeIntervalValue()
                            value.startDate = criterionValue.startDate
                            value.endDate = criterionValue.endDate
                            value
                        }
                        is WorkspaceSimpleCriterionFloatValueDT -> {
                            val value = WorkspaceSimpleCriterionFloatValue()
                            value.value = criterionValue.value
                            value
                        }
                        is WorkspaceSimpleCriterionIntValueDT -> {
                            val value = WorkspaceSimpleCriterionIntValue()
                            value.value = criterionValue.value
                            value
                        }
                        else -> throw IllegalArgumentException("unsuported value $criterionValue")
                    }
                    criterion
                }
                else -> throw IllegalArgumentException("unsupported criterion $criterionDT")
            })
        }
        return result
    }

}