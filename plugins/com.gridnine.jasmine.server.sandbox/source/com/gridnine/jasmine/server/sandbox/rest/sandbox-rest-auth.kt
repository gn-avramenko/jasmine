/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused", "RemoveRedundantQualifierName", "UNCHECKED_CAST", "MemberVisibilityCanBePrivate", "RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.rest

import com.gridnine.jasmine.server.core.rest.RestHandler
import com.gridnine.jasmine.server.core.rest.RestOperationContext
import com.gridnine.jasmine.server.core.storage.Storage
import com.gridnine.jasmine.server.core.utils.ValidationUtils
import com.gridnine.jasmine.server.sandbox.model.domain.SandboxUserAccountIndex
import com.gridnine.jasmine.server.sandbox.model.rest.*
import com.gridnine.jasmine.server.sandbox.model.ui.SandboxLoginDialogVV
import com.gridnine.jasmine.server.sandbox.model.ui.SandboxValidationMessages
import com.gridnine.jasmine.server.standard.model.ui.StandardValidationMessages
import java.lang.IllegalArgumentException

class SandboxCheckAuthHandler:RestHandler<CheckAuthRequest, CheckAuthResponse>{
    override fun service(request: CheckAuthRequest, ctx: RestOperationContext): CheckAuthResponse {
        val response = CheckAuthResponse()
        response.authorized = SandboxAuthFilter.getAuthInfo() != null
        return response
    }

}

class SandboxLoginHandler:RestHandler<LoginRequest, LoginResponse>{
    override fun service(request: LoginRequest, ctx: RestOperationContext): LoginResponse {
        val result = LoginResponse()
        val data = request.data?:throw IllegalArgumentException("data is absent")
        val validation = SandboxLoginDialogVV()
        result.validation = validation
        if(data.login.isNullOrBlank()){
            validation.login = StandardValidationMessages.EMPTY_VALUE()
        }
        if(data.password.isNullOrBlank()){
            validation.password = StandardValidationMessages.EMPTY_VALUE()
        }
        if(ValidationUtils.hasValidationErrors(validation)){
            result.successfull = false
            return result;
        }
        val userAccount = Storage.get().findUniqueDocument(SandboxUserAccountIndex::class, SandboxUserAccountIndex.login, data.login)
        if(userAccount == null){
            validation.login = SandboxValidationMessages.WRONG_LOGIN()
            result.successfull = false
            return result;
        }
        if(userAccount.password != data.password){
            validation.password = SandboxValidationMessages.WRONG_PASSWORD()
            result.successfull = false
            return result;
        }
        result.successfull = true
        SandboxAuthFilter.setCookie(data.login!!, data.password!!, ctx.response)
        return result
    }

}

class SandboxLogoutHandler:RestHandler<LogoutRequest,LogoutResponse>{
    override fun service(request: LogoutRequest, ctx: RestOperationContext): LogoutResponse {
        SandboxAuthFilter.removeCookie(ctx.response)
        return LogoutResponse()
    }

}