/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.standard.rest

import com.gridnine.jasmine.server.core.model.domain.BaseAsset
import com.gridnine.jasmine.server.core.model.domain.BaseDocument
import com.gridnine.jasmine.server.core.model.domain.DomainMetaRegistry
import com.gridnine.jasmine.server.core.model.domain.ObjectReference
import com.gridnine.jasmine.server.core.reflection.ReflectionFactory
import com.gridnine.jasmine.server.core.rest.RestHandler
import com.gridnine.jasmine.server.core.rest.RestOperationContext
import com.gridnine.jasmine.server.core.storage.Storage
import com.gridnine.jasmine.server.standard.model.rest.DeleteObjectsRequest
import com.gridnine.jasmine.server.standard.model.rest.DeleteObjectsResponse

class StandardRestDeleteObjectsHandler : RestHandler<DeleteObjectsRequest, DeleteObjectsResponse>{
    override fun service(request: DeleteObjectsRequest, ctx: RestOperationContext): DeleteObjectsResponse {
        val result = DeleteObjectsResponse()
        request.objects.forEach {
            if(DomainMetaRegistry.get().assets.containsKey(it.objectType)){
                Storage.get().deleteAsset(ObjectReference(ReflectionFactory.get().getClass<BaseAsset>(it.objectType), it.objectUid, null))
            } else {
                Storage.get().deleteDocument(ObjectReference(ReflectionFactory.get().getClass<BaseDocument>(it.objectType), it.objectUid, null))
            }
        }
        return result
    }
}