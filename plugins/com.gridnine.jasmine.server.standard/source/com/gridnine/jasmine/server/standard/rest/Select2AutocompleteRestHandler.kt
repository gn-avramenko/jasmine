/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.standard.rest

import com.gridnine.jasmine.common.core.meta.DomainMetaRegistry
import com.gridnine.jasmine.common.core.model.BaseAsset
import com.gridnine.jasmine.common.core.model.BaseDocument
import com.gridnine.jasmine.common.core.model.BaseIndex
import com.gridnine.jasmine.common.core.model.SelectItem
import com.gridnine.jasmine.common.core.reflection.ReflectionFactory
import com.gridnine.jasmine.common.core.storage.SearchQuery
import com.gridnine.jasmine.common.core.storage.SimpleCriterion
import com.gridnine.jasmine.common.core.storage.SortOrder
import com.gridnine.jasmine.common.core.storage.Storage
import com.gridnine.jasmine.common.standard.model.rest.Select2AutocompleteRequest
import com.gridnine.jasmine.common.standard.model.rest.Select2AutocompleteResponse
import com.gridnine.jasmine.server.core.rest.RestHandler
import com.gridnine.jasmine.server.core.rest.RestOperationContext

class Select2AutocompleteRestHandler: RestHandler<Select2AutocompleteRequest, Select2AutocompleteResponse>{
    override fun service(request: Select2AutocompleteRequest, ctx: RestOperationContext): Select2AutocompleteResponse {
       val query = SearchQuery()
       query.limit = request.limit
       val fieldName = request.queryParameters.find { it.key == "autocompleteFieldName"}!!.value!!
        val listId = request.queryParameters.find { it.key == "listId"}!!.value!!
       query.preferredProperties.add(fieldName)
       query.orders[fieldName]= SortOrder.ASC
       request.searchText?.let { query.criterions.add(SimpleCriterion(fieldName, SimpleCriterion.Operation.ILIKE, "%${it}%")) }
       val result = Select2AutocompleteResponse()
        if(DomainMetaRegistry.get().assets.containsKey(listId)){
            Storage.get().searchAssets(ReflectionFactory.get().getClass<BaseAsset>(listId), query).forEach {
                result.values.add(SelectItem("${it::class.qualifiedName}||${it.uid}", it.getValue(fieldName) as String))
            }
        } else {
            Storage.get().searchDocuments(ReflectionFactory.get().getClass<BaseIndex<BaseDocument>>(listId), query).forEach {
                result.values.add(SelectItem("${it.document!!.type.qualifiedName}||${it.uid}", if(fieldName== "${BaseIndex.documentField}Caption") it.document!!.caption!! else it.getValue(fieldName) as String))
            }
        }
        return result;
    }

}