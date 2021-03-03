/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.standard.helpers

import com.gridnine.jasmine.server.core.model.common.BaseIdentity
import com.gridnine.jasmine.server.core.model.common.BaseIntrospectableObject
import com.gridnine.jasmine.server.core.model.common.Xeption
import com.gridnine.jasmine.server.core.model.domain.BaseAsset
import com.gridnine.jasmine.server.core.model.domain.BaseDocument
import com.gridnine.jasmine.server.core.model.domain.DomainMetaRegistry
import com.gridnine.jasmine.server.core.model.ui.EditorReadDataBundle
import com.gridnine.jasmine.server.core.model.ui.EditorVersionReadDataBundle
import com.gridnine.jasmine.server.core.reflection.ReflectionFactory
import com.gridnine.jasmine.server.core.rest.RestOperationContext
import com.gridnine.jasmine.server.core.storage.Storage
import com.gridnine.jasmine.server.standard.model.rest.*
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
        val metadata = Storage.get().getVersionsMetadata<BaseIdentity>(ReflectionFactory.get().getClass(objectId), objectUid)
        val response = GetVersionsMetaResponse()
        return metadata.map {
            UiVersionMetaData(it.version+1, it.modifiedBy, it.modified, it.comment)
        }.sortedBy { -it.version }
    }

    fun getVersionReadBundle(objectId:String, objectUid:String, version:Int): EditorVersionReadDataBundle {
        val objectId = if (objectId.endsWith("JS")) objectId.substringBeforeLast("JS") else objectId
        val handlers = ObjectEditorsRegistry.get().getHandlers(objectId)
        if (handlers.isEmpty()) {
            throw Xeption.forDeveloper("no object editor handler found for class $objectId")
        }
        val handler = handlers[0]
        val context = hashMapOf<String, Any?>()
        val assetDescription = DomainMetaRegistry.get().assets[objectId]
        val ett =
                if (assetDescription != null) {
                    Storage.get().loadAssetVersion(ReflectionFactory.get().getClass<BaseAsset>(objectId), objectUid, version)
                } else {
                    Storage.get().loadDocumentVersion(ReflectionFactory.get().getClass<BaseDocument>(objectId), objectUid, version)
                }
                        ?: throw Xeption.forDeveloper("version not found: type = $objectId uid = $objectUid version = ${version}")
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
        val version: BaseIdentity =
                if (assetDescription != null) {
                    Storage.get().loadAssetVersion(ReflectionFactory.get().getClass<BaseAsset>(objectId), objectUid, version)
                } else {
                    Storage.get().loadDocumentVersion(ReflectionFactory.get().getClass<BaseDocument>(objectId), objectUid, version)
                }  ?: throw Xeption.forDeveloper("version not found, obj = ${objectId}, uid = ${objectUid} , version = ${version}")
        version.setValue(BaseDocument.revision, -1)
        if(version is BaseDocument){
            Storage.get().saveDocument(version, "restored-version")
        } else {
            Storage.get().saveAsset(version as BaseAsset, "restored-version")
        }
    }
}