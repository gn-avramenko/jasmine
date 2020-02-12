/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UnsafeCastFromDynamic", "UNCHECKED_CAST")

package com.gridnine.jasmine.server.standard.rest

import com.gridnine.jasmine.server.core.model.common.BaseEntity
import com.gridnine.jasmine.server.core.model.domain.BaseAsset
import com.gridnine.jasmine.server.core.model.domain.BaseDocument
import com.gridnine.jasmine.server.core.model.domain.DomainMetaRegistry
import com.gridnine.jasmine.server.core.model.ui.BaseVMEntity
import com.gridnine.jasmine.server.core.model.ui.BaseVSEntity
import com.gridnine.jasmine.server.core.model.ui.BaseVVEntity
import com.gridnine.jasmine.server.core.model.ui.UiMetaRegistry
import com.gridnine.jasmine.server.core.rest.RestHandler
import com.gridnine.jasmine.server.core.rest.RestOperationContext
import com.gridnine.jasmine.server.core.storage.Storage
import com.gridnine.jasmine.server.core.storage.TransactionContext
import com.gridnine.jasmine.server.core.utils.ReflectionUtils
import com.gridnine.jasmine.server.core.utils.ValidationUtils
import com.gridnine.jasmine.server.standard.model.rest.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap

interface RestEditorHandler<E : BaseEntity, VM : BaseVMEntity, VS : BaseVSEntity, VV : BaseVVEntity> {

    fun read(entity: E?, vmEntity: VM, ctx: MutableMap<String, Any>) {
        //noops
    }

    fun fillSettings(entity: E?, vsEntity: VS, vmEntity: VM, ctx: MutableMap<String, Any>) {
        //noops
    }

    fun validate(vmEntity: VM, vvEntity: VV, ctx: MutableMap<String, Any>) {
        //noops
    }

    fun write(entity: E, vmEntity: VM, ctx: MutableMap<String, Any>) {
        //noops
    }

    fun getTitle(entity: E?, vmEntity: VM, vsEntity: VS, ctx: MutableMap<String, Any>): String? {
        return null
    }

}

private val handlers = ConcurrentHashMap<String, MutableList<RestEditorHandler<BaseEntity, BaseVMEntity, BaseVSEntity, BaseVVEntity>>>()

private fun getHandlers(objectId: String): MutableList<RestEditorHandler<BaseEntity, BaseVMEntity, BaseVSEntity, BaseVVEntity>> {
    return handlers.getOrPut(objectId, {
        val editor = UiMetaRegistry.get().editors.values.find { it.entityId == objectId }
                ?: throw IllegalArgumentException("unable to find editor for object $objectId")
        editor.handlers.map {
            ReflectionUtils.newInstance<RestEditorHandler<BaseEntity, BaseVMEntity, BaseVSEntity, BaseVVEntity>>(it)
        }.toMutableList()
    })
}

class StandardGetEditorDataRestHandler : RestHandler<GetEditorDataRequest, GetEditorDataResponse> {
    
    override fun service(request: GetEditorDataRequest,ctx: RestOperationContext): GetEditorDataResponse {
        val editor = UiMetaRegistry.get().editors.values.find { it.entityId == request.objectId }
                ?: throw IllegalArgumentException("unable to find editor for object ${request.objectId}")
        val view = UiMetaRegistry.get().views[editor.viewId]?:throw IllegalArgumentException("unable to find description for view ${editor.viewId}")
        val ett: BaseEntity? = request.objectUid?.let let@{
            val assetDescription = DomainMetaRegistry.get().assets[request.objectId]
            if (assetDescription != null) {
                return@let  Storage.get().loadAsset(ReflectionUtils.getClass<BaseAsset>(request.objectId), it)
            }
            return@let Storage.get().loadDocument(ReflectionUtils.getClass<BaseDocument>(request.objectId), it)
        }

        val vmEntity = ReflectionUtils.newInstance<BaseVMEntity>(view.viewModel)
        vmEntity.uid = ett?.uid ?: request.objectUid?:UUID.randomUUID().toString()
        val context = hashMapOf<String, Any>()
        getHandlers(request.objectId).forEach {
            it.read(ett, vmEntity, context)
        }
        val vsEntity = ReflectionUtils.newInstance<BaseVSEntity>(view.viewSettings)
        getHandlers(request.objectId).forEach {
            it.fillSettings(ett, vsEntity, vmEntity, context)
        }
        val result = GetEditorDataResponse()
        result.viewModel = vmEntity
        result.viewSettings = vsEntity
        result.title = "???"
        getHandlers(request.objectId).forEach {
            val title = it.getTitle(ett, vmEntity, vsEntity, context)
            if (title != null) {
                result.title = title
            }
        }
        return result
    }
}

class StandardSaveEditorDataRestHandler : RestHandler<SaveEditorDataRequest, SaveEditorDataResponse> {
    override fun service(request: SaveEditorDataRequest,ctx: RestOperationContext): SaveEditorDataResponse {
        val editor = UiMetaRegistry.get().editors.values.find { it.entityId == request.objectId }
                ?: throw IllegalArgumentException("unable to find editor for object ${request.objectId}")
        val view = UiMetaRegistry.get().views[editor.viewId]?:throw IllegalArgumentException("unable to find description for view ${editor.viewId}")
        val asset = DomainMetaRegistry.get().assets[request.objectId] != null
        val ett = if (asset) {
            Storage.get().loadAsset(ReflectionUtils.getClass<BaseAsset>(request.objectId), request.viewModel.uid!!)
        } else {
            Storage.get().loadDocument(ReflectionUtils.getClass<BaseDocument>(request.objectId), request.viewModel.uid!!)
        } ?: let {  val res  = ReflectionUtils.newInstance<BaseEntity>(request.objectId)
            res.uid = UUID.randomUUID().toString()
            res}
        val vvEntity =  ReflectionUtils.newInstance<BaseVVEntity>(view.viewValidation)
        val context = hashMapOf<String, Any>()
        getHandlers(request.objectId).forEach {
            it.validate(request.viewModel, vvEntity, context)
        }
        if (ValidationUtils.hasValidationErrors(vvEntity)) {
            val response = SaveEditorDataResponse()
            response.viewValidation = vvEntity
            return response
        }

        Storage.get().executeInTransaction { _: TransactionContext ->
            getHandlers(request.objectId).forEach {
                it.write(ett, request.viewModel, context)
            }
            if (asset) {
                Storage.get().saveAsset(ett as BaseAsset)
            } else {
                Storage.get().saveDocument(ett as BaseDocument)
            }
        }
        val vmEntity = ReflectionUtils.newInstance<BaseVMEntity>(view.viewModel)
        vmEntity.uid = ett.uid
        getHandlers(request.objectId).forEach {
            it.read(ett, vmEntity, context)
        }
        val vsEntity = ReflectionUtils.newInstance<BaseVSEntity>(view.viewSettings)
        val result = SaveEditorDataResponse()
        result.viewModel = vmEntity
        result.viewSettings = vsEntity
        getHandlers(request.objectId).forEach {
            it.fillSettings(ett, vsEntity, vmEntity, context)
        }
        result.title = getHandlers(request.objectId).mapNotNull { it.getTitle(ett, vmEntity, vsEntity, context) }.firstOrNull()?:"???"
        return result
    }


}

class StandardDeleteObjectRestHandler : RestHandler<DeleteObjectRequest, DeleteObjectResponse> {
    override fun service(request: DeleteObjectRequest,ctx: RestOperationContext): DeleteObjectResponse {
        val assetDescription = DomainMetaRegistry.get().assets[request.objectId]
        if (assetDescription != null) {
            val ett = Storage.get().loadAsset(ReflectionUtils.getClass<BaseAsset>(request.objectId), request.objectUid)
            if (ett != null) {
                Storage.get().deleteAsset(ett)
            }
        } else {
            val ett = Storage.get().loadDocument(ReflectionUtils.getClass<BaseDocument>(request.objectId), request.objectUid)
            if (ett != null) {
                Storage.get().deleteDocument(ett)
            }
        }
        return DeleteObjectResponse()
    }

}