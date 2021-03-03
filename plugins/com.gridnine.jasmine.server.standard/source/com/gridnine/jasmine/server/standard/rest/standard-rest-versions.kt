/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.standard.rest

import com.gridnine.jasmine.server.core.model.common.BaseIdentity
import com.gridnine.jasmine.server.core.model.common.Xeption
import com.gridnine.jasmine.server.core.model.domain.BaseAsset
import com.gridnine.jasmine.server.core.model.domain.BaseDocument
import com.gridnine.jasmine.server.core.model.domain.DomainMetaRegistry
import com.gridnine.jasmine.server.core.model.l10n.CoreServerMessagesFactory
import com.gridnine.jasmine.server.core.reflection.ReflectionFactory
import com.gridnine.jasmine.server.core.rest.RestHandler
import com.gridnine.jasmine.server.core.rest.RestOperationContext
import com.gridnine.jasmine.server.core.storage.Storage
import com.gridnine.jasmine.server.standard.StandardServerMessagesFactory
import com.gridnine.jasmine.server.standard.helpers.ObjectEditorsRegistry
import com.gridnine.jasmine.server.standard.helpers.UiVersionsHelper
import com.gridnine.jasmine.server.standard.model.rest.*
import kotlin.reflect.full.createInstance

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

class StandardRestRestoreVersionHandler : RestHandler<RestoreVersionRequest, RestoreVersionResponse> {
    override fun service(request: RestoreVersionRequest, ctx: RestOperationContext): RestoreVersionResponse {
        UiVersionsHelper.restoreVersion(request.objectId, request.objectUid, request.version)
        return RestoreVersionResponse()
    }
}