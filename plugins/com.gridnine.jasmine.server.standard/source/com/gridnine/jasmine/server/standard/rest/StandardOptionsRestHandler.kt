/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.standard.rest

import com.gridnine.jasmine.common.core.meta.UiMetaRegistry
import com.gridnine.jasmine.common.core.model.SelectItem
import com.gridnine.jasmine.common.standard.model.rest.GetOptionsRequest
import com.gridnine.jasmine.common.standard.model.rest.GetOptionsResponse
import com.gridnine.jasmine.server.core.rest.RestHandler
import com.gridnine.jasmine.server.core.rest.RestOperationContext

class StandardOptionsRestHandler :RestHandler<GetOptionsRequest,GetOptionsResponse>{
    override fun service(request: GetOptionsRequest, ctx: RestOperationContext): GetOptionsResponse {
        val result = GetOptionsResponse()
        UiMetaRegistry.get().optionsGroups[request.groupId]!!.options.forEach {
            val displayName = it.getDisplayName()
            result.options.add(SelectItem(it.id, displayName!!))
        }
        return result
    }
}