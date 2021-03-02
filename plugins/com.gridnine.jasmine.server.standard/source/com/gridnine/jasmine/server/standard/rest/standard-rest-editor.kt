/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.standard.rest

import com.gridnine.jasmine.server.core.app.Disposable
import com.gridnine.jasmine.server.core.app.PublishableWrapper
import com.gridnine.jasmine.server.core.model.common.BaseIdentity
import com.gridnine.jasmine.server.core.model.common.Xeption
import com.gridnine.jasmine.server.core.model.domain.BaseAsset
import com.gridnine.jasmine.server.core.model.domain.BaseDocument
import com.gridnine.jasmine.server.core.model.domain.DomainMetaRegistry
import com.gridnine.jasmine.server.core.model.ui.BaseVM
import com.gridnine.jasmine.server.core.model.ui.BaseVS
import com.gridnine.jasmine.server.core.model.ui.BaseVV
import com.gridnine.jasmine.server.core.reflection.ReflectionFactory
import com.gridnine.jasmine.server.core.rest.RestHandler
import com.gridnine.jasmine.server.core.rest.RestOperationContext
import com.gridnine.jasmine.server.core.storage.Storage
import com.gridnine.jasmine.server.core.utils.ValidationUtils
import com.gridnine.jasmine.server.standard.StandardServerMessagesFactory
import com.gridnine.jasmine.server.standard.helpers.ObjectEditorsRegistry
import com.gridnine.jasmine.server.standard.helpers.UiEditorHelper
import com.gridnine.jasmine.server.standard.model.rest.GetEditorDataRequest
import com.gridnine.jasmine.server.standard.model.rest.GetEditorDataResponse
import com.gridnine.jasmine.server.standard.model.rest.SaveEditorDataRequest
import com.gridnine.jasmine.server.standard.model.rest.SaveEditorDataResponse
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

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

