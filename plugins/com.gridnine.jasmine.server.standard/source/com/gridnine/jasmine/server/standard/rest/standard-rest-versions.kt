/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.standard.rest

import com.gridnine.jasmine.common.standard.model.rest.*
import com.gridnine.jasmine.common.standard.rest.ObjectVersionMetaData
import com.gridnine.jasmine.server.core.rest.RestHandler
import com.gridnine.jasmine.server.core.rest.RestOperationContext
import com.gridnine.jasmine.server.standard.helpers.UiVersionsHelper

class StandardRestGetVersionsMetadataHandler : RestHandler<GetVersionsMetadataRequest, GetVersionsMetaResponse> {
    override fun service(request: GetVersionsMetadataRequest, ctx: RestOperationContext): GetVersionsMetaResponse {
        val response = GetVersionsMetaResponse()
        UiVersionsHelper.getVersionsMetadata(request.objectId, request.objectUid).forEach{
            val item = ObjectVersionMetaData()
            item.version = it.version
            item.comment = it.comment
            item.modified = it.modified
            item.modifiedBy = it.modifiedBy
            response.versions.add(item)
        }
        return response
    }
}

class StandardGetVersionEditorDataRestHandler : RestHandler<GetVersionEditorDataRequest, GetVersionEditorDataResponse> {
    override fun service(request: GetVersionEditorDataRequest, ctx: RestOperationContext): GetVersionEditorDataResponse {
        val bundle = UiVersionsHelper.getVersionReadBundle(request.objectId, request.objectUid, request.version)
        val result = GetVersionEditorDataResponse()
        result.viewModel = bundle.vm
        result.viewSettings = bundle.vs
        return result
    }
}

class StandardRestRestoreVersionHandler : RestHandler<RestoreVersionRequest, RestoreVersionResponse
        > {
    override fun service(request: RestoreVersionRequest, ctx: RestOperationContext): RestoreVersionResponse {
        UiVersionsHelper.restoreVersion(request.objectId, request.objectUid, request.version)
        return RestoreVersionResponse()
    }
}