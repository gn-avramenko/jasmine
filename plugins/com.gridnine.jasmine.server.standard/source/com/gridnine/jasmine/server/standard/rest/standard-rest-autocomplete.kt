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
import com.gridnine.jasmine.server.core.storage.search.SearchCriterion
import com.gridnine.jasmine.server.core.storage.search.SearchQuery
import com.gridnine.jasmine.server.core.storage.search.searchQuery
import com.gridnine.jasmine.server.core.utils.ReflectionUtils
import com.gridnine.jasmine.server.standard.model.rest.*

class DefaultEntityAutocompleteRestHandler: RestHandler<EntityAutocompleteRequest, EntityAutocompleteResponse>{
    override fun service(request: EntityAutocompleteRequest, ctx: RestOperationContext): EntityAutocompleteResponse {
        var result = arrayListOf<EntityReference<BaseEntity>>()
        request.entitiesIds.forEach {
            val query = searchQuery {
                where {
                    request.searchText?.let {
                        ilike(BaseIndex.referenceCaption, "%$it%")
                    }
                }
                limit(request.limit!!)
            }
            val docs = Storage.get().searchDocuments(ReflectionUtils.getClass<BaseIndex<BaseDocument>>(it.substringBeforeLast("JS")), query)
            docs.forEach {
                result.add(it.document as EntityReference<BaseEntity>)
            }
        }
        result.sortBy{ t -> t.caption }
        val response = EntityAutocompleteResponse()
        if(result.size > request.limit!!){
            response.items.addAll(result.subList(0, request.limit!!))
        } else {
            response.items.addAll(result)
        }
        return response
    }

}