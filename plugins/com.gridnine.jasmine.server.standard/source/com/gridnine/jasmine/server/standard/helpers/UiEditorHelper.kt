/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.standard.helpers

import com.gridnine.jasmine.server.core.model.common.BaseIdentity
import com.gridnine.jasmine.server.core.model.common.Xeption
import com.gridnine.jasmine.server.core.model.domain.BaseAsset
import com.gridnine.jasmine.server.core.model.domain.BaseDocument
import com.gridnine.jasmine.server.core.model.domain.DomainMetaRegistry
import com.gridnine.jasmine.server.core.model.ui.EditorReadDataBundle
import com.gridnine.jasmine.server.core.reflection.ReflectionFactory
import com.gridnine.jasmine.server.core.storage.Storage
import com.gridnine.jasmine.server.standard.StandardServerMessagesFactory
import com.gridnine.jasmine.server.standard.rest.ObjectEditorsRegistry
import kotlin.reflect.full.createInstance

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
}