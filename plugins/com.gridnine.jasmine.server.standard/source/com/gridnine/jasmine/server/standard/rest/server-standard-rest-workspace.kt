/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UnsafeCastFromDynamic", "UNCHECKED_CAST")

package com.gridnine.jasmine.server.standard.rest

import com.gridnine.jasmine.common.core.app.Registry
import com.gridnine.jasmine.common.core.app.RegistryItem
import com.gridnine.jasmine.common.core.app.RegistryItemType
import com.gridnine.jasmine.common.core.meta.BaseModelElementDescription
import com.gridnine.jasmine.common.core.meta.DomainMetaRegistry
import com.gridnine.jasmine.common.core.model.SelectItem
import com.gridnine.jasmine.common.core.storage.BaseDynamicCriterionValue
import com.gridnine.jasmine.common.core.storage.DynamicCriterionCondition
import com.gridnine.jasmine.common.core.storage.DynamicCriterionHandler
import com.gridnine.jasmine.common.core.utils.TextUtils
import com.gridnine.jasmine.common.standard.model.domain.BaseWorkspaceItem
import com.gridnine.jasmine.common.standard.model.domain.Workspace
import com.gridnine.jasmine.common.standard.model.domain.WorkspaceGroup
import com.gridnine.jasmine.common.standard.model.rest.*
import com.gridnine.jasmine.server.core.rest.RestHandler
import com.gridnine.jasmine.server.core.rest.RestOperationContext
import com.gridnine.jasmine.server.standard.model.WorkspaceProvider


interface WorkspaceItemToDtConverter<M:BaseWorkspaceItem, DT:BaseWorkspaceItemDT> : RegistryItem<WorkspaceItemToDtConverter<BaseWorkspaceItem, BaseWorkspaceItemDT>> {

    fun convert(item: M):DT

    override fun getType(): RegistryItemType<WorkspaceItemToDtConverter<BaseWorkspaceItem, BaseWorkspaceItemDT>> {
        return TYPE
    }

    companion object{
        val TYPE = RegistryItemType<WorkspaceItemToDtConverter<BaseWorkspaceItem, BaseWorkspaceItemDT>>("server-workspace-item-to-dt-converters")
    }
}

interface WorkspaceItemFromDtConverter<M:BaseWorkspaceItem, DT:BaseWorkspaceItemDT> : RegistryItem<WorkspaceItemFromDtConverter<BaseWorkspaceItem, BaseWorkspaceItemDT>> {

    fun convert(item: DT):M

    override fun getType(): RegistryItemType<WorkspaceItemFromDtConverter<BaseWorkspaceItem, BaseWorkspaceItemDT>> {
        return TYPE
    }

    companion object{
        val TYPE = RegistryItemType<WorkspaceItemFromDtConverter<BaseWorkspaceItem, BaseWorkspaceItemDT>>("server-workspace-item-from-dt-converters")
    }
}

interface WorkspaceDynamicValueToDtConverter<M:BaseDynamicCriterionValue, DT:BaseDynamicCriterionValueDT> : RegistryItem<WorkspaceDynamicValueToDtConverter<BaseDynamicCriterionValue, BaseDynamicCriterionValueDT>> {

    fun convert(item: M):DT

    override fun getType(): RegistryItemType<WorkspaceDynamicValueToDtConverter<BaseDynamicCriterionValue, BaseDynamicCriterionValueDT>> {
        return TYPE
    }

    companion object{
        val TYPE = RegistryItemType<WorkspaceDynamicValueToDtConverter<BaseDynamicCriterionValue, BaseDynamicCriterionValueDT>>("server-workspace-dynamic-value-to-dt-converters")
    }
}

interface WorkspaceDynamicValueFromDtConverter<M:BaseDynamicCriterionValue, DT:BaseDynamicCriterionValueDT> : RegistryItem<WorkspaceDynamicValueFromDtConverter<BaseDynamicCriterionValue, BaseDynamicCriterionValueDT>> {

    fun convert(item: DT):M

    override fun getType(): RegistryItemType<WorkspaceDynamicValueFromDtConverter<BaseDynamicCriterionValue, BaseDynamicCriterionValueDT>> {
        return TYPE
    }

    companion object{
        val TYPE = RegistryItemType<WorkspaceDynamicValueFromDtConverter<BaseDynamicCriterionValue, BaseDynamicCriterionValueDT>>("server-workspace-dynamic-value-from-dt-converters")
    }
}

class StandardGetWorkspaceRestHandler:RestHandler<GetWorkspaceRequest, GetWorkspaceResponse>{
    override fun service(request: GetWorkspaceRequest, ctx:RestOperationContext): GetWorkspaceResponse {
        val result = GetWorkspaceResponse()
        result.workspace = WorkspaceDT()
        WorkspaceProvider.get().getWorkspace().groups.forEach { group ->
            val wg = WorkspaceGroupDT()
            result.workspace.groups.add(wg)
            wg.displayName = group.displayName
            wg.uid = group.uid
            group.items.forEach {item ->
                val wi = SelectItem(item.uid, item.displayName!!)
                wg.items.add(wi)
            }
        }
        return result
    }

}


class StandardGetWorkspaceItemRestHandler:RestHandler<GetWorkspaceItemRequest, GetWorkspaceItemResponse>{
    override fun service(request: GetWorkspaceItemRequest, ctx:RestOperationContext): GetWorkspaceItemResponse {
        val item = WorkspaceProvider.get().getWorkspace().groups.flatMap { it.items }.find { it.uid == request.uid }!!
        val result = GetWorkspaceItemResponse()
        result.workspaceItem = Registry.get().get(WorkspaceItemToDtConverter.TYPE, item::class.qualifiedName!!)!!.convert(item)
        return result
    }

}

class StandardSaveWorkspaceRestHandler:RestHandler<SaveWorkspaceRequest,SaveWorkspaceResponse>{
    override fun service(request: SaveWorkspaceRequest, ctx: RestOperationContext): SaveWorkspaceResponse {
        val ws = WorkspaceProvider.get().getWorkspace()
        val sourceElements = hashMapOf<String,BaseWorkspaceItem>()
        ws.groups.flatMap { it.items }.forEach { sourceElements[it.uid] = it }
        val result = Workspace()
        result.uid = ws.uid
        request.workspace.groups.forEach {gr ->
            val group = WorkspaceGroup()
            group.uid = gr.uid!!
            group.displayName = gr.displayName
            result.groups.add(group)
            gr.items.forEach {item ->
                request.updatedItems.find { it.uid == item.id }?.let {
                    group.items.add(Registry.get().get(WorkspaceItemFromDtConverter.TYPE, it::class.qualifiedName!!)!!.convert(it))
                    it
                }?:group.items.add(sourceElements[item.id]!!)
            }
        }
        WorkspaceProvider.get().saveWorkspace(result)
        return SaveWorkspaceResponse()
    }

}

class StandardDynamicQueryPropertiesRestHandler:RestHandler<GetDynamicQueryPropertiesRequest, GetDynamicQueryPropertiesResponse>{
    private val cache = hashMapOf<String, List<BaseModelElementDescription>>()
    override fun service(
        request: GetDynamicQueryPropertiesRequest,
        ctx: RestOperationContext
    ): GetDynamicQueryPropertiesResponse {
        val listId = request.listId.substringBeforeLast("JS")
        val fields = cache.getOrPut(listId){
            val listDescr = DomainMetaRegistry.get().indexes[listId]?:DomainMetaRegistry.get().assets[listId]!!
            val handlers = Registry.get().allOf(DynamicCriterionHandler.TYPE)
            val result = listDescr.properties.filter { prop->handlers.any{it.isApplicable(listId, prop.key)} }.map { it.value}.toMutableList() as MutableList<BaseModelElementDescription>
            result.addAll(listDescr.collections.filter { prop->handlers.any{it.isApplicable(listId, prop.key)} }.map { it.value})
            result
        }.map { SelectItem(it.id, it.getDisplayName()!!) }.filter { TextUtils.isBlank(request.pattern) || it.text.toLowerCase().contains(request.pattern!!.toLowerCase()) }.sortedBy { it.text }
        return GetDynamicQueryPropertiesResponse().apply {
            items.addAll(fields)
        }
    }
}

class StandardDynamicQueryConditionsRestHandler:RestHandler<GetDynamicQueryConditionsRequest, GetDynamicQueryConditionsResponse>{

    private val cache = hashMapOf<String, MutableMap<String, List<DynamicCriterionCondition>>>()

    override fun service(
        request: GetDynamicQueryConditionsRequest,
        ctx: RestOperationContext
    ): GetDynamicQueryConditionsResponse {
        val listId = request.listId.substringBeforeLast("JS")
        val propertyId = request.propertyId
        val conditions = cache.getOrPut(listId){
            hashMapOf()
        }.getOrPut(propertyId){
            val handlers = Registry.get().allOf(DynamicCriterionHandler.TYPE)
            handlers.filter { it.isApplicable(listId, propertyId) }.flatMap { it.getConditionIds() }.distinct().map { Registry.get().get(DynamicCriterionCondition.TYPE, it)!!}
        }.map { SelectItem(it.getId(), it.getDisplayName()) }.filter { TextUtils.isBlank(request.pattern) || it.text.toLowerCase().contains(request.pattern!!.toLowerCase()) }.sortedBy { it.text }
        return GetDynamicQueryConditionsResponse().apply {
            items.addAll(conditions)
        }
    }
}

class StandardDynamicQueryHandlersRestHandler:RestHandler<GetDynamicQueryHandlersRequest, GetDynamicQueryHandlersResponse>{
    private val cache = hashMapOf<String, MutableMap<String, List<DynamicCriterionHandler<*>>>>()
    override fun service(
        request: GetDynamicQueryHandlersRequest,
        ctx: RestOperationContext
    ): GetDynamicQueryHandlersResponse {
        val listId = request.listId.substringBeforeLast("JS")
        val propertyId = request.propertyId
        val conditionId = request.conditionId
        val filteredHandlers= cache.getOrPut(listId){
            hashMapOf()
        }.getOrPut("${propertyId}_${conditionId}"){
            val handlers = Registry.get().allOf(DynamicCriterionHandler.TYPE)
            handlers.filter { it.isApplicable(listId, propertyId) && it.getConditionIds().contains(conditionId) }
        }.map { SelectItem("${it.getRestCorrectionClass()!!.qualifiedName}_${it.getId()}", it.getDisplayName()) }.filter { TextUtils.isBlank(request.pattern) || it.text.toLowerCase().contains(request.pattern!!.toLowerCase()) }.sortedBy { it.text }
        return GetDynamicQueryHandlersResponse().apply {
            items.addAll(filteredHandlers)
        }
    }

}
