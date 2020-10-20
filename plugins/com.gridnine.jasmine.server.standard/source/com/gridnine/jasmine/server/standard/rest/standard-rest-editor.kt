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
import com.gridnine.jasmine.server.core.model.ui.UiMetaRegistry
import com.gridnine.jasmine.server.core.reflection.ReflectionFactory
import com.gridnine.jasmine.server.core.rest.RestHandler
import com.gridnine.jasmine.server.core.rest.RestOperationContext
import com.gridnine.jasmine.server.core.storage.Storage
import com.gridnine.jasmine.server.standard.model.rest.GetEditorDataRequest
import com.gridnine.jasmine.server.standard.model.rest.GetEditorDataResponse
import java.util.*
import kotlin.reflect.KClass

class StandardGetEditorDataRestHandler:RestHandler<GetEditorDataRequest, GetEditorDataResponse>{
    override fun service(request: GetEditorDataRequest, ctx: RestOperationContext): GetEditorDataResponse {
        val handlers = ObjectEditorsRegistry.get().getHandlers(request.objectId)
        if(handlers.isEmpty()){
            throw Xeption.forDeveloper("no object editor handler found for class ${request.objectId}")
        }
//        val ett: BaseIdentity = run {
//            val assetDescription = DomainMetaRegistry.get().assets[request.objectId]
//            if (assetDescription != null) {
//                Storage.get().loadAsset(ReflectionFactory.get().getClass<BaseAsset>(request.objectId), request.objectUid)
//            } else {
//                Storage.get().loadDocument(ReflectionFactory.get().getClass<BaseDocument>(request.objectId), request.objectUid)
//            }
//        }?:Xeption.forAdmin(Sta)
//
//        val vmEntity = ReflectionUtils.newInstance<BaseVMEntity>(view.viewModel)
//        vmEntity.uid = ett?.uid ?: request.objectUid?: UUID.randomUUID().toString()
//        val context = hashMapOf<String, Any>()
//        getHandlers(request.objectId).forEach {
//            it.read(ett, vmEntity, context)
//        }
//        val vsEntity = ReflectionUtils.newInstance<BaseVSEntity>(view.viewSettings)
//        getHandlers(request.objectId).forEach {
//            it.fillSettings(ett, vsEntity, vmEntity, context)
//        }
//        val result = GetEditorDataResponse()
//        result.viewModel = vmEntity
//        result.viewSettings = vsEntity
//        result.title = "???"
//        getHandlers(request.objectId).forEach {
//            val title = it.getTitle(ett, vmEntity, vsEntity, context)
//            if (title != null) {
//                result.title = title
//            }
//        }
//        return result

        val response = GetEditorDataResponse()
        response.title = "Test title"
        response.viewModel = Class.forName("com.gridnine.jasmine.web.demo.DemoUserAccountEditorVM").newInstance() as BaseVM
        response.viewSettings = Class.forName("com.gridnine.jasmine.web.demo.DemoUserAccountEditorVS").newInstance() as BaseVS
        return response
    }


}

class ObjectEditorsRegistry:Disposable{
    private val handlers = hashMapOf<String, MutableList<ObjectEditorHandler<BaseIdentity, BaseVM, BaseVS, BaseVV>>>()

    fun <I:BaseIdentity, VM:BaseVM, VS:BaseVS, VV:BaseVV, H:ObjectEditorHandler<I,VM,VS,VV>> register(handler:H){
        handlers.getOrPut(handler.getObjectClass().qualifiedName!!, { arrayListOf()}).add(handler as ObjectEditorHandler<BaseIdentity, BaseVM, BaseVS, BaseVV>)
    }

    fun getHandlers(className:String):List<ObjectEditorHandler<BaseIdentity,BaseVM,BaseVS,BaseVV>>{
        return handlers[className]?:throw Xeption.forDeveloper("no handler defined for ${className}")
    }

    override fun dispose() {
        handlers.clear()
        wrapper.dispose()
    }
    companion object {
        private val wrapper = PublishableWrapper(ObjectEditorsRegistry::class)
        fun get() = wrapper.get()
    }
}

interface ObjectEditorHandler<E : BaseIdentity, VM : BaseVM, VS : BaseVS, VV : BaseVV> {

    fun getObjectClass():KClass<E>

    fun getVMClass():KClass<VM>

    fun getVSClass():KClass<VS>

    fun getVVClass():KClass<VV>

    fun fillNewEntity(entity: E, ctx: MutableMap<String, Any?>) {
        //noops
    }

    fun read(entity: E?, vmEntity: VM, ctx: MutableMap<String, Any?>) {
        //noops
    }

    fun fillSettings(entity: E?, vsEntity: VS, vmEntity: VM, ctx: MutableMap<String, Any?>) {
        //noops
    }

    fun validate(vmEntity: VM, vvEntity: VV, ctx: MutableMap<String, Any?>) {
        //noops
    }

    fun write(entity: E, vmEntity: VM, ctx: MutableMap<String, Any?>) {
        //noops
    }

    fun getTitle(entity: E?, vmEntity: VM, vsEntity: VS, ctx: MutableMap<String, Any?>): String? {
        return null
    }

}