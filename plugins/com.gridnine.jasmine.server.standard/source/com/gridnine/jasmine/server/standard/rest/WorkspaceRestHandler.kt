/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UnsafeCastFromDynamic", "UNCHECKED_CAST")

package com.gridnine.jasmine.server.standard.rest

import com.gridnine.jasmine.server.core.app.Environment
import com.gridnine.jasmine.server.core.rest.RestHandler
import com.gridnine.jasmine.server.core.rest.RestOperationContext
import com.gridnine.jasmine.server.standard.model.rest.*


interface WorkspaceProvider{
    fun getWorkspace():WorkspaceDT
    fun saveWorkspace(workspace:WorkspaceDT):WorkspaceDT

    companion object{
        fun get() = Environment.getPublished(WorkspaceProvider::class)
    }
}

class StandardSaveWorkspaceRestHandler:RestHandler<SaveWorkspaceRequest, SaveWorkspaceResponse>{
    override fun service(request: SaveWorkspaceRequest, ctx:RestOperationContext): SaveWorkspaceResponse {
        val result = SaveWorkspaceResponse()
        result.workspace = WorkspaceProvider.get().saveWorkspace(request.workspace)
        return result
    }
}

class StandardGetWorkspaceRestHandler:RestHandler<GetWorkspaceRequest, GetWorkspaceResponse>{
    override fun service(request: GetWorkspaceRequest, ctx:RestOperationContext): GetWorkspaceResponse {
        val result = GetWorkspaceResponse()
        result.workspace = WorkspaceProvider.get().getWorkspace()
        return result
    }

}