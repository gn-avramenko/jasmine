/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.rest

import com.gridnine.jasmine.common.core.model.BaseRestEntity
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class RestOperationContext(val request:HttpServletRequest, val response:HttpServletResponse)

interface RestHandler<RQ:BaseRestEntity, RP:BaseRestEntity>{
    fun service(request:RQ, ctx:RestOperationContext):RP
}