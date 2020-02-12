/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UnsafeCastFromDynamic", "UNCHECKED_CAST")

package com.gridnine.jasmine.server.standard.rest

import com.gridnine.jasmine.server.core.model.common.BaseEntity
import com.gridnine.jasmine.server.core.model.domain.BaseDocument
import com.gridnine.jasmine.server.core.model.domain.BaseIndex
import com.gridnine.jasmine.server.core.model.domain.EntityReference
import com.gridnine.jasmine.server.core.rest.RestHandler
import com.gridnine.jasmine.server.core.rest.RestOperationContext
import com.gridnine.jasmine.server.core.storage.Storage
import com.gridnine.jasmine.server.core.storage.search.searchQuery
import com.gridnine.jasmine.server.core.utils.ReflectionUtils
import com.gridnine.jasmine.server.standard.model.rest.EntityAutocompleteRequest
import com.gridnine.jasmine.server.standard.model.rest.EntityAutocompleteResponse

class DefaultEntityAutocompleteRestHandler: RestHandler<EntityAutocompleteRequest, EntityAutocompleteResponse>{
    override fun service(request: EntityAutocompleteRequest, ctx: RestOperationContext): EntityAutocompleteResponse {
        val result = arrayListOf<EntityReference<BaseEntity>>()
        request.entitiesIds.forEach {
            val query = searchQuery {
                where {
                    request.searchText?.let { st ->
                        ilike(BaseIndex.referenceCaption, "%$st%")
                    }
                }
                limit(request.limit)
            }
            val docs = Storage.get().searchDocuments(ReflectionUtils.getClass<BaseIndex<BaseDocument>>(it.substringBeforeLast("JS")), query)
            docs.forEach { idx ->
                result.add(idx.document as EntityReference<BaseEntity>)
            }
        }
        result.sortBy{ it.caption }
        val response = EntityAutocompleteResponse()
        if(result.size > request.limit){
            response.items.addAll(result.subList(0, request.limit))
        } else {
            response.items.addAll(result)
        }
        return response
    }

}