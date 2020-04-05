package com.gridnine.jasmine.web.sandbox.test.suite

import com.gridnine.jasmine.server.sandbox.model.ui.SandboxLoginDialog
import com.gridnine.jasmine.server.sandbox.model.ui.SandboxLoginDialogVMJS
import com.gridnine.jasmine.server.sandbox.model.ui.SandboxLoginDialogVSJS
import com.gridnine.jasmine.web.core.application.CoreActivatorJS
import com.gridnine.jasmine.web.core.model.ui.TestableDialogButtonWidget
import com.gridnine.jasmine.web.core.remote.StandardRpcManager
import com.gridnine.jasmine.web.core.test.activator.CoreTestActivator
import com.gridnine.jasmine.web.core.test.ext.before
import com.gridnine.jasmine.web.core.test.ext.beforeEach
import com.gridnine.jasmine.web.core.test.ext.debugger
import com.gridnine.jasmine.web.core.test.ext.describe
import com.gridnine.jasmine.web.core.ui.UiFactory
import com.gridnine.jasmine.web.sandbox.DomainReflectionUtils
import com.gridnine.jasmine.web.sandbox.RestReflectionUtilsJS
import com.gridnine.jasmine.web.sandbox.UiReflectionUtilsJS
import org.w3c.xhr.XMLHttpRequest
import kotlin.js.Promise


class SandboxIndividualTest{
    fun describeSuite(){
        describe("sandbox-individual-test"){
            before {
                val config = hashMapOf<String,Any?>()
                config[StandardRpcManager.BASE_REST_URL_KEY] = "/sandbox/easyui/ui-rest"
                val coreActivator = CoreActivatorJS()
                coreActivator.configure(config)
                val testActivator = CoreTestActivator()
                testActivator.configure("http://localhost:8080/sandbox/easyui/ui-rest")
                DomainReflectionUtils.registerWebDomainClasses()
                RestReflectionUtilsJS.registerRestWebClasses()
                UiReflectionUtilsJS.registerWebUiClasses()
                coreActivator.activate().then {
                    Promise<Unit>{resolve, reject ->
                        val vm = SandboxLoginDialogVMJS()
                        val dialog = UiFactory.get().showDialog(SandboxLoginDialog(), vm, SandboxLoginDialogVSJS())
                        dialog.view.login.setData("admin")
                        dialog.view.password.setData("admin")
                        (dialog.buttons.find { it.id == "loginButton" } as TestableDialogButtonWidget<Unit>).click().then(resolve).catch(reject)
                    }
                }
            }
            CreateNewAccountTest().createNewAccountTest()
        }
    }
}