/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/


package com.gridnine.jasmine.server.sandbox.rest

import com.gridnine.jasmine.server.core.rest.RestHandler
import com.gridnine.jasmine.server.core.rest.RestOperationContext
import com.gridnine.jasmine.server.core.storage.Storage
import com.gridnine.jasmine.server.core.utils.EntityUtils
import com.gridnine.jasmine.server.sandbox.model.domain.SandboxUserAccount
import com.gridnine.jasmine.server.sandbox.model.domain.SandboxUserAccountIndex
import com.gridnine.jasmine.server.sandbox.model.rest.SandboxCreateUserAccountRequest
import com.gridnine.jasmine.server.sandbox.model.rest.SandboxCreateUserAccountResponse
import com.gridnine.jasmine.server.sandbox.model.rest.SandboxUpdatePasswordRequest
import com.gridnine.jasmine.server.sandbox.model.rest.SandboxUpdatePasswordResponse
import com.gridnine.jasmine.server.sandbox.model.ui.*
import com.gridnine.jasmine.server.standard.model.ui.StandardValidationMessages
import com.gridnine.jasmine.server.standard.rest.RestEditorHandler


class SandboxUserAccountEditorHandler : RestEditorHandler<SandboxUserAccount, SandboxUserAccountEditorVM, SandboxUserAccountEditorVS, SandboxUserAccountEditorVV>{
    override fun read(entity: SandboxUserAccount?, vmEntity: SandboxUserAccountEditorVM, ctx: MutableMap<String, Any>) {
        if(entity == null){
            vmEntity.name = "Новый пользователь"
            return
        }
        vmEntity.name = entity.name
        vmEntity.login = entity.login
    }

    override fun getTitle(entity: SandboxUserAccount?, vmEntity: SandboxUserAccountEditorVM, vsEntity: SandboxUserAccountEditorVS, ctx: MutableMap<String, Any>): String? {
        return vmEntity.name
    }

    override fun validate(vmEntity: SandboxUserAccountEditorVM, vvEntity: SandboxUserAccountEditorVV, ctx: MutableMap<String, Any>) {
        if(vmEntity.login == null){
            vvEntity.login = StandardValidationMessages.EMPTY_VALUE()
        }
        if(vmEntity.name == null){
            vvEntity.name = StandardValidationMessages.EMPTY_VALUE()
        }
    }

    override fun write(entity: SandboxUserAccount, vmEntity: SandboxUserAccountEditorVM, ctx: MutableMap<String, Any>) {
        entity.login = vmEntity.login
        entity.name = vmEntity.name
    }

}


class SandboxCreateUserAccountHandler : RestHandler<SandboxCreateUserAccountRequest, SandboxCreateUserAccountResponse>{
    override fun service(request: SandboxCreateUserAccountRequest, ctx: RestOperationContext): SandboxCreateUserAccountResponse {
        val result = SandboxCreateUserAccountResponse()
        val validation = SandboxCreateUserAccountDialogVV()
        result.validation = validation
        if(request.model.login.isNullOrBlank()){
            validation.login = StandardValidationMessages.EMPTY_VALUE()
            return result
        }
        if(request.model.password.isNullOrBlank()){
            validation.password = StandardValidationMessages.EMPTY_VALUE()
            return result
        }
        if(request.model.passwordRepeat.isNullOrBlank()){
            validation.passwordRepeat = StandardValidationMessages.EMPTY_VALUE()
            return result
        }
        if(request.model.passwordRepeat != request.model.password){
            validation.passwordRepeat = SandboxValidationMessages.PASSWORDS_DIFFERS()
            return result
        }
        val existingRef = Storage.get().findUniqueDocumentReference(SandboxUserAccountIndex::class, SandboxUserAccountIndex.login, request.model.login)
        if(existingRef != null){
            validation.login = SandboxValidationMessages.LOGIN_ALREADY_EXISTS()
            return result
        }
        val acc = SandboxUserAccount()
        acc.name = "Новая учетная запись"
        acc.login = request.model.login
        acc.password = request.model.password
        Storage.get().saveDocument(acc)
        result.result = EntityUtils.toReference(acc)
        return result
    }

}

class SandboxUpdatePasswordHandler : RestHandler<SandboxUpdatePasswordRequest, SandboxUpdatePasswordResponse>{
    override fun service(request: SandboxUpdatePasswordRequest, ctx: RestOperationContext): SandboxUpdatePasswordResponse {
        val result = SandboxUpdatePasswordResponse()
        val validation = SandboxUpdatePasswordDialogVV()
        result.validation = validation
        if(request.model.oldPassword.isNullOrBlank()){
            validation.oldPassword = StandardValidationMessages.EMPTY_VALUE()
            return result
        }
        if(request.model.password.isNullOrBlank()){
            validation.password = StandardValidationMessages.EMPTY_VALUE()
            return result
        }
        if(request.model.passwordRepeat.isNullOrBlank()){
            validation.passwordRepeat = StandardValidationMessages.EMPTY_VALUE()
            return result
        }
        if(request.model.passwordRepeat != request.model.password){
            validation.passwordRepeat = SandboxValidationMessages.PASSWORDS_DIFFERS()
            return result
        }
        val account = Storage.get().findUniqueDocument(SandboxUserAccountIndex::class, SandboxUserAccountIndex.login, request.login)
                ?: throw IllegalArgumentException("no account found for login ${request.login}")
        if(account.password != request.model.oldPassword){
            validation.oldPassword = SandboxValidationMessages.WRONG_PASSWORD()
            return result
        }
        account.password=request.model.password
        Storage.get().saveDocument(account)
        return result
    }

}