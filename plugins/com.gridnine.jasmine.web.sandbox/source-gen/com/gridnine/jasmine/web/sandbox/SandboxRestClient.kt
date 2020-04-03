/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.web.sandbox

object SandboxRestClient{
    fun sandbox_auth_checkAuth(request:com.gridnine.jasmine.server.sandbox.model.rest.CheckAuthRequestJS): kotlin.js.Promise<com.gridnine.jasmine.server.sandbox.model.rest.CheckAuthResponseJS>{
        return com.gridnine.jasmine.web.core.remote.RpcManager.get().post("sandbox_auth_checkAuth",request)
    }
    fun sandbox_auth_login(request:com.gridnine.jasmine.server.sandbox.model.rest.LoginRequestJS): kotlin.js.Promise<com.gridnine.jasmine.server.sandbox.model.rest.LoginResponseJS>{
        return com.gridnine.jasmine.web.core.remote.RpcManager.get().post("sandbox_auth_login",request)
    }
    fun sandbox_auth_logout(request:com.gridnine.jasmine.server.sandbox.model.rest.LogoutRequestJS): kotlin.js.Promise<com.gridnine.jasmine.server.sandbox.model.rest.LogoutResponseJS>{
        return com.gridnine.jasmine.web.core.remote.RpcManager.get().post("sandbox_auth_logout",request)
    }
    fun sandbox_userAccount_createAccount(request:com.gridnine.jasmine.server.sandbox.model.rest.SandboxCreateUserAccountRequestJS): kotlin.js.Promise<com.gridnine.jasmine.server.sandbox.model.rest.SandboxCreateUserAccountResponseJS>{
        return com.gridnine.jasmine.web.core.remote.RpcManager.get().post("sandbox_userAccount_createAccount",request)
    }
    fun sandbox_userAccount_updatePassword(request:com.gridnine.jasmine.server.sandbox.model.rest.SandboxUpdatePasswordRequestJS): kotlin.js.Promise<com.gridnine.jasmine.server.sandbox.model.rest.SandboxUpdatePasswordResponseJS>{
        return com.gridnine.jasmine.web.core.remote.RpcManager.get().post("sandbox_userAccount_updatePassword",request)
    }
}