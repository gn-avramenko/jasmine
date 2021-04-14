/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.ui.common

import com.gridnine.jasmine.common.core.meta.DomainMetaRegistry
import com.gridnine.jasmine.common.core.model.*
import com.gridnine.jasmine.common.core.reflection.ReflectionFactory
import com.gridnine.jasmine.common.core.storage.Storage
import java.time.LocalDateTime
import kotlin.reflect.full.createInstance

class UiVersionMetaData(val version:Int, val modifiedBy:String, val modified:LocalDateTime, val comment:String?):BaseIntrospectableObject(){
    override fun getValue(propertyName: String): Any? {
        return when(propertyName){
            versionField -> version
            modifiedByField -> modifiedBy
            modifiedField -> modified
            commentField -> comment
            else-> null
        }
    }

    companion object{
        const val versionField = "version"
        const val modifiedByField = "modifiedBy"
        const val modifiedField = "modified"
        const val commentField = "comment"
    }
}

object UiVersionsHelper{
    fun getVersionsMetadata(objectId:String, objectUid:String): List<UiVersionMetaData> {
        val metadata = Storage.get().getVersionsMetadata(ReflectionFactory.get().getClass(objectId), objectUid)
        return metadata.map {
            UiVersionMetaData(it.version+1, it.modifiedBy, it.modified, it.comment)
        }.sortedBy { -it.version }
    }

    fun getVersionReadBundle(objectId:String, objectUid:String, version:Int): EditorVersionReadDataBundle {
        val correctedObjectId = if (objectId.endsWith("JS")) objectId.substringBeforeLast("JS") else objectId
        val handlers = ObjectEditorsRegistry.get().getHandlers(correctedObjectId)
        if (handlers.isEmpty()) {
            throw Xeption.forDeveloper("no object editor handler found for class $correctedObjectId")
        }
        val handler = handlers[0]
        val context = hashMapOf<String, Any?>()
        val assetDescription = DomainMetaRegistry.get().assets[correctedObjectId]
        val ett =
                if (assetDescription != null) {
                    Storage.get().loadAssetVersion(ReflectionFactory.get().getClass<BaseAsset>(correctedObjectId), objectUid, version)
                } else {
                    Storage.get().loadDocumentVersion(ReflectionFactory.get().getClass<BaseDocument>(correctedObjectId), objectUid, version)
                }
                        ?: throw Xeption.forDeveloper("version not found: type = $correctedObjectId uid = $objectUid version = $version")
        val vmEntity = handler.getVMClass().createInstance()
        handlers.forEach {
            it.read(ett, vmEntity, context)
        }
        val vsEntity = handler.getVSClass().createInstance()
        handlers.forEach {
            it.fillSettings(ett, vsEntity, vmEntity, context)
        }
        return EditorVersionReadDataBundle(vmEntity, vsEntity)
    }

    fun restoreVersion(objectId:String, objectUid:String, version:Int) {

        val assetDescription = DomainMetaRegistry.get().assets[objectId]
        val versionOfObject: BaseIdentity =
                if (assetDescription != null) {
                    Storage.get().loadAssetVersion(ReflectionFactory.get().getClass<BaseAsset>(objectId), objectUid, version)
                } else {
                    Storage.get().loadDocumentVersion(ReflectionFactory.get().getClass<BaseDocument>(objectId), objectUid, version)
                }  ?: throw Xeption.forDeveloper("version not found, obj = ${objectId}, uid = $objectUid , version = $version")
        versionOfObject.setValue(BaseDocument.revision, -1)
        if(versionOfObject is BaseDocument){
            Storage.get().saveDocument(versionOfObject, true, "restored-version")
        } else {
            Storage.get().saveAsset(versionOfObject as BaseAsset, true, "restored-version")
        }
    }
}