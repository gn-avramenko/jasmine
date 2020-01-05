/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.sandbox.login

import com.gridnine.jasmine.server.sandbox.model.rest.LoginRequestJS
import com.gridnine.jasmine.server.sandbox.model.ui.SandboxLoginDialogVMJS
import com.gridnine.jasmine.server.sandbox.model.ui.SandboxLoginDialogVSJS
import com.gridnine.jasmine.server.sandbox.model.ui.SandboxLoginDialogVVJS
import com.gridnine.jasmine.server.sandbox.model.ui.SandboxLoginDialogView
import com.gridnine.jasmine.web.core.ui.Dialog
import com.gridnine.jasmine.web.core.ui.DialogButtonHandler
import com.gridnine.jasmine.web.core.ui.UiFactory
import com.gridnine.jasmine.web.sandbox.SandboxRestClient
import kotlin.browser.window

class SandboxLoginButtonHandler:DialogButtonHandler<SandboxLoginDialogVMJS, SandboxLoginDialogVSJS,SandboxLoginDialogVVJS,SandboxLoginDialogView>{
    override fun handle(dialog: Dialog<SandboxLoginDialogVMJS, SandboxLoginDialogVSJS, SandboxLoginDialogVVJS, SandboxLoginDialogView>) {
        val vm = SandboxLoginDialogVMJS()
        dialog.view.writeData(vm)
        val login = vm.login
        if(login != null){
            window.localStorage.setItem("jasmine.sandbox.login", login)
        }
        val request = LoginRequestJS()
        request.data = vm
        SandboxRestClient.sandbox_auth_login(request).then {
            if(it.successfull != true){
                dialog.view.showValidation(it.validation?:throw IllegalArgumentException("no validation in response"))
                return@then
            }
            dialog.close()
            UiFactory.get().publishMainFrame()
        }
    }

}


