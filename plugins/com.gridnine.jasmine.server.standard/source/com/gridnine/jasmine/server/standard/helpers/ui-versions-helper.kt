/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.standard.helpers

import com.gridnine.jasmine.server.core.model.common.BaseIdentity
import com.gridnine.jasmine.server.core.model.common.BaseIntrospectableObject
import com.gridnine.jasmine.server.core.reflection.ReflectionFactory
import com.gridnine.jasmine.server.core.storage.Storage
import com.gridnine.jasmine.server.standard.model.rest.GetVersionsMetaResponse
import java.time.LocalDateTime

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
}