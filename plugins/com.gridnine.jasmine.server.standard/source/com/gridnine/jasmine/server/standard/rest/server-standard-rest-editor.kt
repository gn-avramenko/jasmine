/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.standard.rest

import com.gridnine.jasmine.common.standard.model.rest.GetEditorDataRequest
import com.gridnine.jasmine.common.standard.model.rest.GetEditorDataResponse
import com.gridnine.jasmine.common.standard.model.rest.SaveEditorDataRequest
import com.gridnine.jasmine.common.standard.model.rest.SaveEditorDataResponse
import com.gridnine.jasmine.server.core.rest.RestHandler
import com.gridnine.jasmine.server.core.rest.RestOperationContext
import com.gridnine.jasmine.server.standard.helpers.UiEditorHelper

class StandardGetEditorDataRestHandler:RestHandler<GetEditorDataRequest, GetEditorDataResponse>{
    override fun service(request: GetEditorDataRequest, ctx: RestOperationContext): GetEditorDataResponse {
        val bundle = UiEditorHelper.getReadDataBundle(request.objectId, request.objectUid)
        val response = GetEditorDataResponse()
        response.viewModel = bundle.vm
        response.viewSettings = bundle.vs
        response.title = bundle.title
        return response
    }
}

class StandardSaveEditorDataRestHandler : RestHandler<SaveEditorDataRequest, SaveEditorDataResponse> {
    override fun service(request: SaveEditorDataRequest,ctx: RestOperationContext): SaveEditorDataResponse {
        val bundle = UiEditorHelper.saveEditorData(request.objectId, request.objectUid,request.viewModel)
        val response = SaveEditorDataResponse()
        response.viewModel = bundle.vm
        response.viewSettings = bundle.vs
        response.newUid = bundle.newUid
        response.viewValidation = bundle.vv
        response.title = bundle.title?:""
        return response
    }
}

