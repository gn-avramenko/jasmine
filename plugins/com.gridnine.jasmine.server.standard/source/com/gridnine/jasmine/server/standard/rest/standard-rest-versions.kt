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
import com.gridnine.jasmine.server.standard.model.rest.*
import kotlin.reflect.full.createInstance

class StandardRestGetVersionsMetadataHandler:RestHandler<GetVersionsMetadataRequest,GetVersionsMetaResponse>{
    override fun service(request: GetVersionsMetadataRequest, ctx: RestOperationContext): GetVersionsMetaResponse {
        val metadata =  Storage.get().getVersionsMetadata<BaseIdentity>(ReflectionFactory.get().getClass(request.objectId), request.objectUid)
        val response = GetVersionsMetaResponse()
        response.versions.addAll(metadata.map {
            val item = ObjectVersionMetaData()
            item.version = it.version+1
            item.comment = it.comment
            item.modified = it.modified
            item.modifiedBy = it.modifiedBy
            item
        })
        return response
    }
}

class StandardGetVersionEditorDataRestHandler:RestHandler<GetVersionEditorDataRequest, GetVersionEditorDataResponse>{
    override fun service(request: GetVersionEditorDataRequest, ctx: RestOperationContext): GetVersionEditorDataResponse {
        val objectId = if(request.objectId.endsWith("JS")) request.objectId.substringBeforeLast("JS") else request.objectId
        val objectUid = request.objectUid
        val handlers = ObjectEditorsRegistry.get().getHandlers(objectId)
        if(handlers.isEmpty()){
            throw Xeption.forDeveloper("no object editor handler found for class $objectId")
        }
        val handler = handlers[0]
        val context = hashMapOf<String, Any?>()
        val assetDescription = DomainMetaRegistry.get().assets[objectId]
        val ett =
            if (assetDescription != null) {
                Storage.get().loadAssetVersion(ReflectionFactory.get().getClass<BaseAsset>(objectId), objectUid, request.version-1)
            } else {
                Storage.get().loadDocumentVersion(ReflectionFactory.get().getClass<BaseDocument>(objectId), objectUid, request.version-1)
            }?: throw Xeption.forDeveloper("version not found: type = $objectId uid = $objectUid version = ${request.version -1}")
        val vmEntity = handler.getVMClass().createInstance()
        handlers.forEach {
            it.read(ett, vmEntity, context)
        }
        val vsEntity = handler.getVSClass().createInstance()
        handlers.forEach {
            it.fillSettings(ett, vsEntity, vmEntity, context)
        }
        val response = GetVersionEditorDataResponse()
        response.viewModel = vmEntity
        response.viewSettings = vsEntity
        var title:String? = null
        handlers.forEach {
            val intTitle = it.getTitle(ett, vmEntity, vsEntity, context)
            if (intTitle != null) {
                title = intTitle
            }
        }
        response.title = StandardServerMessagesFactory.VERSION_TITLE(title?:"???",request.version).toString()
        return response
    }
}