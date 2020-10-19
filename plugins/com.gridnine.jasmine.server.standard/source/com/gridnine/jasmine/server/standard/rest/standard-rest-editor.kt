/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.standard.rest

import com.gridnine.jasmine.server.core.model.ui.BaseVM
import com.gridnine.jasmine.server.core.model.ui.BaseVS
import com.gridnine.jasmine.server.core.rest.RestHandler
import com.gridnine.jasmine.server.core.rest.RestOperationContext
import com.gridnine.jasmine.server.standard.model.rest.GetEditorDataRequest
import com.gridnine.jasmine.server.standard.model.rest.GetEditorDataResponse

class StandardGetEditorDataRestHandler:RestHandler<GetEditorDataRequest, GetEditorDataResponse>{
    override fun service(request: GetEditorDataRequest, ctx: RestOperationContext): GetEditorDataResponse {
        val response = GetEditorDataResponse()
        response.title = "Test title"
        response.viewModel = Class.forName("com.gridnine.jasmine.web.demo.DemoUserAccountEditorVM").newInstance() as BaseVM
        response.viewSettings = Class.forName("com.gridnine.jasmine.web.demo.DemoUserAccountEditorVS").newInstance() as BaseVS
        return response
    }

}