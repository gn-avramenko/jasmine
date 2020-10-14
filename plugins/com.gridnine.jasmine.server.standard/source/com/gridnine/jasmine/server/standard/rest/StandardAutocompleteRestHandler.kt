/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UnsafeCastFromDynamic", "UNCHECKED_CAST")

package com.gridnine.jasmine.server.standard.rest

import com.gridnine.jasmine.server.core.model.common.BaseIdentity
import com.gridnine.jasmine.server.core.model.domain.*
import com.gridnine.jasmine.server.core.reflection.ReflectionFactory
import com.gridnine.jasmine.server.core.rest.RestHandler
import com.gridnine.jasmine.server.core.rest.RestOperationContext
import com.gridnine.jasmine.server.core.storage.Storage
import com.gridnine.jasmine.server.core.storage.search.*
import com.gridnine.jasmine.server.standard.model.rest.AutocompleteRequest
import com.gridnine.jasmine.server.standard.model.rest.AutocompletetResponse

class StandardAutocompleteRestHandler: RestHandler<AutocompleteRequest, AutocompletetResponse>{
    override fun service(request: AutocompleteRequest, ctx: RestOperationContext): AutocompletetResponse {
       val query = SearchQuery()
       query.limit = request.limit
       query.preferredProperties.add(request.autocompleteFieldName)
       query.orders[request.autocompleteFieldName]= SortOrder.ASC
       request.pattern?.let { query.criterions.add(SimpleCriterion(request.autocompleteFieldName, SimpleCriterion.Operation.ILIKE, "%${it}%")) }
       val result = AutocompletetResponse()
        if(DomainMetaRegistry.get().assets.containsKey(request.listId)){
            Storage.get().searchAssets(ReflectionFactory.get().getClass<BaseAsset>(request.listId), query).forEach {
                val item = AutocompleteItemDT()
                item.document = EntityUtils.toReference(it)
                item.searchFieldValue = it.getValue(request.autocompleteFieldName) as String?
                result.items.add(item)
            }
        } else {
            Storage.get().searchDocuments(ReflectionFactory.get().getClass<BaseIndex<BaseDocument>>(request.listId), query).forEach {
                val item = AutocompleteItemDT()
                item.document = it.document as ObjectReference<BaseIdentity>
                item.searchFieldValue = if(request.autocompleteFieldName == "${BaseIndex.documentField}Caption") it.document!!.caption else it.getValue(request.autocompleteFieldName) as String?
                result.items.add(item)
            }
        }
        return result;
    }

}