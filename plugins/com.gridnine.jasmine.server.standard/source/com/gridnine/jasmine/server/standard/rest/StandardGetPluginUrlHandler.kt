/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.standard.rest

import com.gridnine.jasmine.common.core.meta.WebPluginsAssociationsRegistry
import com.gridnine.jasmine.common.standard.model.rest.GetPluginUrlRequest
import com.gridnine.jasmine.common.standard.model.rest.GetPluginUrlResponse
import com.gridnine.jasmine.server.core.rest.RestHandler
import com.gridnine.jasmine.server.core.rest.RestOperationContext

class StandardGetPluginUrlHandler : RestHandler<GetPluginUrlRequest, GetPluginUrlResponse>{
    override fun service(request: GetPluginUrlRequest, ctx: RestOperationContext): GetPluginUrlResponse {
        val result = GetPluginUrlResponse()
        result.pluginId = WebPluginsAssociationsRegistry.get().associations[request.id.substringBeforeLast("JS")]!!
        result.url = WebPluginsAssociationsRegistry.get().links[result.pluginId]!!
        return result
    }

}