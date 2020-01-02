/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused", "RemoveRedundantQualifierName", "UNCHECKED_CAST", "MemberVisibilityCanBePrivate", "RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.rest

import com.gridnine.jasmine.server.core.rest.RestHandler
import com.gridnine.jasmine.server.core.rest.RestOperationContext
import com.gridnine.jasmine.server.sandbox.model.rest.CheckAuthRequest
import com.gridnine.jasmine.server.sandbox.model.rest.CheckAuthResponse

class SandboxCheckAuthHandler:RestHandler<CheckAuthRequest, CheckAuthResponse>{
    override fun service(request: CheckAuthRequest, ctx: RestOperationContext): CheckAuthResponse {
        val response = CheckAuthResponse()
        response.authorized = SandboxAuthFilter.getAuthInfo() != null
        return response
    }

}