/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UnsafeCastFromDynamic", "UNCHECKED_CAST")

package com.gridnine.jasmine.server.standard.rest

import com.gridnine.jasmine.common.core.app.Registry
import com.gridnine.jasmine.common.core.app.RegistryItem
import com.gridnine.jasmine.common.core.app.RegistryItemType
import com.gridnine.jasmine.common.core.model.SelectItem
import com.gridnine.jasmine.common.core.storage.BaseDynamicCriterionValue
import com.gridnine.jasmine.common.standard.model.domain.BaseWorkspaceItem
import com.gridnine.jasmine.common.standard.model.rest.*
import com.gridnine.jasmine.server.core.rest.RestHandler
import com.gridnine.jasmine.server.core.rest.RestOperationContext
import com.gridnine.jasmine.server.standard.model.WorkspaceProvider


//class StandardSaveWorkspaceRestHandler:RestHandler<SaveWorkspaceRequest, SaveWorkspaceResponse>{
//    override fun service(request: SaveWorkspaceRequest, ctx:RestOperationContext): SaveWorkspaceResponse {
//        val result = SaveWorkspaceResponse()
//        WorkspaceProvider.get().saveWorkspace(request.workspace)
//        result.workspace = request.workspace
//        return result
//    }
//}

interface WorkspaceItemToDtConverter<M:BaseWorkspaceItem, DT:BaseWorkspaceItemDT> : RegistryItem<WorkspaceItemToDtConverter<BaseWorkspaceItem, BaseWorkspaceItemDT>> {

    fun convert(item: M):DT

    override fun getType(): RegistryItemType<WorkspaceItemToDtConverter<BaseWorkspaceItem, BaseWorkspaceItemDT>> {
        return TYPE
    }

    companion object{
        val TYPE = RegistryItemType<WorkspaceItemToDtConverter<BaseWorkspaceItem, BaseWorkspaceItemDT>>("server-workspace-item-to-dt-converters")
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