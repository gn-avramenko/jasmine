/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.sandbox.activator

import com.gridnine.jasmine.server.sandbox.model.rest.CheckAuthRequestJS
import com.gridnine.jasmine.server.sandbox.model.ui.SandboxLoginDialogVMJS
import com.gridnine.jasmine.server.sandbox.model.ui.SandboxLoginDialogVSJS
import com.gridnine.jasmine.server.sandbox.model.ui.SandboxLoginDialogVVJS
import com.gridnine.jasmine.server.sandbox.model.ui.SandboxLoginDialogView
import com.gridnine.jasmine.web.core.application.CoreActivatorJS
import com.gridnine.jasmine.web.core.remote.StandardRpcManager
import com.gridnine.jasmine.web.core.ui.UiFactory
import com.gridnine.jasmine.web.easyui.EasyUiActivator
import com.gridnine.jasmine.web.sandbox.DomainReflectionUtils
import com.gridnine.jasmine.web.sandbox.RestReflectionUtilsJS
import com.gridnine.jasmine.web.sandbox.SandboxRestClient
import com.gridnine.jasmine.web.sandbox.UiReflectionUtilsJS


fun main() {
    val config = hashMapOf<String,Any?>()
    config[StandardRpcManager.BASE_REST_URL_KEY] = "/sandbox/easyui/ui-rest"
    val coreActivator = CoreActivatorJS()
    coreActivator.configure(config)
    DomainReflectionUtils.registerWebDomainClasses()
    RestReflectionUtilsJS.registerRestWebClasses()
    UiReflectionUtilsJS.registerWebUiClasses()
    EasyUiActivator().configure(config)
    coreActivator.activate().then {
        SandboxRestClient.sandbox_auth_checkAuth(CheckAuthRequestJS()).then {
            if(it.authorized == false){
                UiFactory.get().showDialog<SandboxLoginDialogVMJS, SandboxLoginDialogVSJS,SandboxLoginDialogVVJS,SandboxLoginDialogView>("com.gridnine.jasmine.server.sandbox.model.ui.SandboxLoginDialog", SandboxLoginDialogVMJS(), SandboxLoginDialogVSJS())
            }
        }
    }
}