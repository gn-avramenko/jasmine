/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.standard.helpers

import com.gridnine.jasmine.server.core.app.Disposable
import com.gridnine.jasmine.server.core.app.PublishableWrapper
import com.gridnine.jasmine.server.core.model.common.BaseIdentity
import com.gridnine.jasmine.server.core.model.common.Xeption
import com.gridnine.jasmine.server.core.model.domain.BaseAsset
import com.gridnine.jasmine.server.core.model.domain.BaseDocument
import com.gridnine.jasmine.server.core.model.domain.DomainMetaRegistry
import com.gridnine.jasmine.server.core.model.ui.*
import com.gridnine.jasmine.server.core.reflection.ReflectionFactory
import com.gridnine.jasmine.server.core.rest.RestOperationContext
import com.gridnine.jasmine.server.core.storage.Storage
import com.gridnine.jasmine.server.core.utils.ValidationUtils
import com.gridnine.jasmine.server.standard.StandardServerMessagesFactory
import com.gridnine.jasmine.server.standard.model.rest.SaveEditorDataRequest
import com.gridnine.jasmine.server.standard.model.rest.SaveEditorDataResponse
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

class ObjectEditorsRegistry: Disposable {
    private val handlers = hashMapOf<String, MutableList<ObjectEditorHandler<BaseIdentity, BaseVM, BaseVS, BaseVV>>>()

    fun <I:BaseIdentity, VM: BaseVM, VS: BaseVS, VV: BaseVV, H:ObjectEditorHandler<I,VM,VS,VV>> register(handler:H){
        handlers.getOrPut(handler.getObjectClass().qualifiedName!!, { arrayListOf()}).add(handler as ObjectEditorHandler<BaseIdentity, BaseVM, BaseVS, BaseVV>)
    }

    fun getHandlers(className:String):List<ObjectEditorHandler<BaseIdentity, BaseVM, BaseVS, BaseVV>>{
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

    fun getObjectClass(): KClass<E>

    fun getVMClass(): KClass<VM>

    fun getVSClass(): KClass<VS>

    fun getVVClass(): KClass<VV>

    fun fillNewEntity(entity: E, ctx: MutableMap<String, Any?>) {
        //noops
    }

    fun read(entity: E, vmEntity: VM, ctx: MutableMap<String, Any?>) {
        //noops
    }

    fun fillSettings(entity: E, vsEntity: VS, vmEntity: VM, ctx: MutableMap<String, Any?>) {
        //noops
    }

    fun validate(vmEntity: VM, vvEntity: VV, ctx: MutableMap<String, Any?>) {
        //noops
    }

    fun write(entity: E, vmEntity: VM, ctx: MutableMap<String, Any?>) {
        //noops
    }

    fun getTitle(entity: E, vmEntity: VM, vsEntity: VS, ctx: MutableMap<String, Any?>): String? {
        return null
    }

}

object UiEditorHelper {
     fun getReadDataBundle(objectId: String, objectUid:String?): EditorReadDataBundle {
        val objectId = if(objectId.endsWith("JS")) objectId.substringBeforeLast("JS") else objectId
        val handlers = ObjectEditorsRegistry.get().getHandlers(objectId)
        if(handlers.isEmpty()){
            throw Xeption.forDeveloper("no object editor handler found for class $objectId")
        }
        val handler = handlers[0]
        val context = hashMapOf<String, Any?>()
        val ett: BaseIdentity = if(objectUid == null){
            val newEtt = handler.getObjectClass().createInstance()
            handlers.forEach{
                it.fillNewEntity(newEtt,context)
            }
            newEtt
        } else {
            val assetDescription = DomainMetaRegistry.get().assets[objectId]
            if (assetDescription != null) {
                Storage.get().loadAsset(ReflectionFactory.get().getClass<BaseAsset>(objectId), objectUid)
            } else {
                Storage.get().loadDocument(ReflectionFactory.get().getClass<BaseDocument>(objectId), objectUid)
            }?: throw Xeption.forAdmin(StandardServerMessagesFactory.OBJECT_NOT_FOUND(objectId, objectUid))
        }
        val vmEntity = handler.getVMClass().createInstance()
        handlers.forEach {
            it.read(ett, vmEntity, context)
        }
        val vsEntity = handler.getVSClass().createInstance()
        handlers.forEach {
            it.fillSettings(ett, vsEntity, vmEntity, context)
        }
        var title:String? = null
        handlers.forEach {
            val intTitle = it.getTitle(ett, vmEntity, vsEntity, context)
            if (intTitle != null) {
                title = intTitle
            }
        }
        return EditorReadDataBundle(vmEntity, vsEntity, title?:"???")
    }

    fun saveEditorData(objectId:String , objectUid: String?, vm:BaseVM): EditorWriteDataBundle {
        val objectId = if(objectId.endsWith("JS")) objectId.substringBeforeLast("JS") else objectId
        val handlers = ObjectEditorsRegistry.get().getHandlers(objectId)
        if(handlers.isEmpty()){
            throw Xeption.forDeveloper("no object editor handler found for class $objectId")

        }
        val handler = handlers[0]
        val asset = DomainMetaRegistry.get().assets[objectId] != null
        val objUid = objectUid
        val ett:BaseIdentity =
                if(objUid == null){
                    val res  = ReflectionFactory.get().newInstance<BaseIdentity>(objectId)
                    res.uid = UUID.randomUUID().toString()
                    res
                } else {
                    if (asset) {
                        Storage.get().loadAsset(ReflectionFactory.get().getClass(objectId), objUid, ignoreCache = true)
                    } else {
                        Storage.get().loadDocument(ReflectionFactory.get().getClass(objectId), objUid, ignoreCache = true)
                    }?: throw Xeption.forAdmin(StandardServerMessagesFactory.OBJECT_NOT_FOUND(objectId, objUid))
                }


        val vvEntity =  handler.getVVClass().createInstance()
        val context = hashMapOf<String, Any?>()
        handlers.forEach {
            it.validate(vm, vvEntity, context)
        }
        if (ValidationUtils.hasValidationErrors(vvEntity)) {
            return EditorWriteDataBundle(null, null, vvEntity, null,null)
        }
        handlers.forEach {
            it.write(ett, vm, context)
        }
        if (asset) {
            Storage.get().saveAsset(ett as BaseAsset)
        } else {
            Storage.get().saveDocument(ett as BaseDocument)
        }
        val vmEntity = handler.getVMClass().createInstance()
        handlers.forEach {
            it.read(ett, vmEntity, context)
        }
        val vsEntity = handler.getVSClass().createInstance()
        val result = SaveEditorDataResponse()
        result.newUid = if(objUid == null) ett.uid else null
        result.viewModel = vmEntity
        result.viewSettings = vsEntity
        handlers.forEach {
            it.fillSettings(ett, vsEntity, vmEntity, context)
        }
        return EditorWriteDataBundle(vmEntity, vsEntity, null, if(objUid == null) ett.uid else null, handlers.mapNotNull { it.getTitle(ett, vmEntity, vsEntity, context) }.lastOrNull()?:"???")
    }
}