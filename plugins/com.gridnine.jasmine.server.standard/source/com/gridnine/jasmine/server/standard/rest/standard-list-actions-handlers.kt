/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.standard.rest

import com.gridnine.jasmine.common.core.meta.DomainMetaRegistry
import com.gridnine.jasmine.common.core.model.BaseAsset
import com.gridnine.jasmine.common.core.model.BaseDocument
import com.gridnine.jasmine.common.core.model.ObjectReference
import com.gridnine.jasmine.common.core.reflection.ReflectionFactory
import com.gridnine.jasmine.common.core.storage.Storage
import com.gridnine.jasmine.common.standard.model.rest.DeleteObjectsResponse
import com.gridnine.jasmine.server.core.rest.RestHandler
import com.gridnine.jasmine.server.core.rest.RestOperationContext
import com.gridnine.jasmine.server.standard.model.rest.DeleteObjectsRequest

class StandardRestDeleteObjectsHandler :RestHandler<DeleteObjectsRequest, DeleteObjectsResponse>{
    override fun service(request: DeleteObjectsRequest, ctx: RestOperationContext): DeleteObjectsResponse {
        request.objects.forEach {
            if(DomainMetaRegistry.get().assets.containsKey(it.objectType)){
                Storage.get().deleteAsset(ObjectReference(ReflectionFactory.get().getClass<BaseAsset>(it.objectType), it.objectUid, null))
            } else {
                Storage.get().deleteDocument(ObjectReference(ReflectionFactory.get().getClass<BaseDocument>(it.objectType), it.objectUid, null))
            }
        }
        return DeleteObjectsResponse()
    }

}