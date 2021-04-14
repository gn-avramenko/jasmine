/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.ui.common

import com.gridnine.jasmine.common.core.app.Disposable
import com.gridnine.jasmine.common.core.app.PublishableWrapper
import com.gridnine.jasmine.common.core.meta.DomainMetaRegistry
import com.gridnine.jasmine.common.core.model.*
import com.gridnine.jasmine.common.core.reflection.ReflectionFactory
import com.gridnine.jasmine.common.core.storage.Storage
import com.gridnine.jasmine.server.core.model.l10n.CoreServerL10nMessagesFactory
import com.gridnine.jasmine.server.core.ui.utils.UiUtils
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

@Suppress("UNCHECKED_CAST")
class ObjectEditorsRegistry: Disposable {
    private val handlers = hashMapOf<String, MutableList<ObjectEditorHandler<BaseIdentity, BaseVM, BaseVS, BaseVV>>>()

    fun <I:BaseIdentity, VM: BaseVM, VS: BaseVS, VV: BaseVV, H:ObjectEditorHandler<I,VM,VS,VV>> register(handler:H){
        handlers.getOrPut(handler.getObjectClass().qualifiedName!!, { arrayListOf()}).add(handler as ObjectEditorHandler<BaseIdentity, BaseVM, BaseVS, BaseVV>)
    }

    fun getHandlers(className:String):List<ObjectEditorHandler<BaseIdentity, BaseVM, BaseVS, BaseVV>>{
        return handlers[className]?:throw Xeption.forDeveloper("no handler defined for $className")
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
        val correctedObjectId = if(objectId.endsWith("JS")) objectId.substringBeforeLast("JS") else objectId
        val handlers = ObjectEditorsRegistry.get().getHandlers(correctedObjectId)
        if(handlers.isEmpty()){
            throw Xeption.forDeveloper("no object editor handler found for class $correctedObjectId")
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
            val assetDescription = DomainMetaRegistry.get().assets[correctedObjectId]
            if (assetDescription != null) {
                Storage.get().loadAsset(ReflectionFactory.get().getClass<BaseAsset>(correctedObjectId), objectUid)
            } else {
                Storage.get().loadDocument(ReflectionFactory.get().getClass<BaseDocument>(correctedObjectId), objectUid)
            }?: throw Xeption.forAdmin(CoreServerL10nMessagesFactory.Object_not_found(correctedObjectId, objectUid))
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
        val correctedObjectId = if(objectId.endsWith("JS")) objectId.substringBeforeLast("JS") else objectId
        val handlers = ObjectEditorsRegistry.get().getHandlers(correctedObjectId)
        if(handlers.isEmpty()){
            throw Xeption.forDeveloper("no object editor handler found for class $correctedObjectId")

        }
        val handler = handlers[0]
        val asset = DomainMetaRegistry.get().assets[correctedObjectId] != null
        val ett:BaseIdentity =
                if(objectUid == null){
                    val res  = ReflectionFactory.get().newInstance<BaseIdentity>(correctedObjectId)
                    res.uid = UUID.randomUUID().toString()
                    res
                } else {
                    if (asset) {
                        Storage.get().loadAsset(ReflectionFactory.get().getClass(correctedObjectId), objectUid, ignoreCache = true)
                    } else {
                        Storage.get().loadDocument(ReflectionFactory.get().getClass(correctedObjectId), objectUid, ignoreCache = true)
                    }?: throw Xeption.forAdmin(CoreServerL10nMessagesFactory.Object_not_found(correctedObjectId, objectUid))
                }


        val vvEntity =  handler.getVVClass().createInstance()
        val context = hashMapOf<String, Any?>()
        handlers.forEach {
            it.validate(vm, vvEntity, context)
        }
        if (UiUtils.hasValidationErrors(vvEntity)) {
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
        handlers.forEach {
            it.fillSettings(ett, vsEntity, vmEntity, context)
        }
        return EditorWriteDataBundle(vmEntity, vsEntity, null, if(objectUid == null) ett.uid else null, handlers.mapNotNull { it.getTitle(ett, vmEntity, vsEntity, context) }.lastOrNull()?:"???")
    }
}