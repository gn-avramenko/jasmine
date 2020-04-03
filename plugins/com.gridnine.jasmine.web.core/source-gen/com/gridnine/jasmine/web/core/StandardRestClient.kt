/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.web.core

object StandardRestClient{
    fun standard_standard_meta(request:com.gridnine.jasmine.server.standard.model.rest.GetMetadataRequestJS): kotlin.js.Promise<com.gridnine.jasmine.server.standard.model.rest.GetMetadataResponseJS>{
        return com.gridnine.jasmine.web.core.remote.RpcManager.get().post("standard_standard_meta",request)
    }
    fun standard_standard_getWorkspace(request:com.gridnine.jasmine.server.standard.model.rest.GetWorkspaceRequestJS): kotlin.js.Promise<com.gridnine.jasmine.server.standard.model.rest.GetWorkspaceResponseJS>{
        return com.gridnine.jasmine.web.core.remote.RpcManager.get().post("standard_standard_getWorkspace",request)
    }
    fun standard_standard_saveWorkspace(request:com.gridnine.jasmine.server.standard.model.rest.SaveWorkspaceRequestJS): kotlin.js.Promise<com.gridnine.jasmine.server.standard.model.rest.SaveWorkspaceResponseJS>{
        return com.gridnine.jasmine.web.core.remote.RpcManager.get().post("standard_standard_saveWorkspace",request)
    }
    fun standard_standard_list(request:com.gridnine.jasmine.server.standard.model.rest.GetListRequestJS): kotlin.js.Promise<com.gridnine.jasmine.server.standard.model.rest.GetListResponseJS>{
        return com.gridnine.jasmine.web.core.remote.RpcManager.get().post("standard_standard_list",request)
    }
    fun standard_standard_getEditorData(request:com.gridnine.jasmine.server.standard.model.rest.GetEditorDataRequestJS): kotlin.js.Promise<com.gridnine.jasmine.server.standard.model.rest.GetEditorDataResponseJS>{
        return com.gridnine.jasmine.web.core.remote.RpcManager.get().post("standard_standard_getEditorData",request)
    }
    fun standard_standard_saveEditorData(request:com.gridnine.jasmine.server.standard.model.rest.SaveEditorDataRequestJS): kotlin.js.Promise<com.gridnine.jasmine.server.standard.model.rest.SaveEditorDataResponseJS>{
        return com.gridnine.jasmine.web.core.remote.RpcManager.get().post("standard_standard_saveEditorData",request)
    }
    fun standard_standard_deleteObject(request:com.gridnine.jasmine.server.standard.model.rest.DeleteObjectRequestJS): kotlin.js.Promise<com.gridnine.jasmine.server.standard.model.rest.DeleteObjectResponseJS>{
        return com.gridnine.jasmine.web.core.remote.RpcManager.get().post("standard_standard_deleteObject",request)
    }
    fun standard_standard_defaultAutocomplete(request:com.gridnine.jasmine.server.standard.model.rest.EntityAutocompleteRequestJS): kotlin.js.Promise<com.gridnine.jasmine.server.standard.model.rest.EntityAutocompleteResponseJS>{
        return com.gridnine.jasmine.web.core.remote.RpcManager.get().post("standard_standard_defaultAutocomplete",request)
    }
}