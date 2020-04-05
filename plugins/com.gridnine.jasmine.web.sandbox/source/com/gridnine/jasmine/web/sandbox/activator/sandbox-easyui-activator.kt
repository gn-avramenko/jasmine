/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.sandbox.activator

import com.gridnine.jasmine.server.sandbox.model.rest.CheckAuthRequestJS
import com.gridnine.jasmine.server.sandbox.model.rest.LogoutRequestJS
import com.gridnine.jasmine.server.sandbox.model.ui.SandboxLoginDialog
import com.gridnine.jasmine.server.sandbox.model.ui.SandboxLoginDialogVMJS
import com.gridnine.jasmine.server.sandbox.model.ui.SandboxLoginDialogVSJS
import com.gridnine.jasmine.web.core.application.CoreActivatorJS
import com.gridnine.jasmine.web.core.remote.StandardRpcManager
import com.gridnine.jasmine.web.core.ui.MainFrame
import com.gridnine.jasmine.web.core.ui.MainFrameConfiguration
import com.gridnine.jasmine.web.core.ui.MainFrameTool
import com.gridnine.jasmine.web.core.ui.UiFactory
import com.gridnine.jasmine.web.easyui.EasyUiActivator
import com.gridnine.jasmine.web.sandbox.DomainReflectionUtils
import com.gridnine.jasmine.web.sandbox.RestReflectionUtilsJS
import com.gridnine.jasmine.web.sandbox.SandboxRestClient
import com.gridnine.jasmine.web.sandbox.UiReflectionUtilsJS
import org.w3c.dom.get
import kotlin.browser.window
import kotlin.reflect.typeOf


fun main() {
    if(window.asDynamic().testMode){
        return
    }
    val config = hashMapOf<String,Any?>()
    config[StandardRpcManager.BASE_REST_URL_KEY] = "/sandbox/easyui/ui-rest"
    val coreActivator = CoreActivatorJS()
    coreActivator.configure(config)
    DomainReflectionUtils.registerWebDomainClasses()
    RestReflectionUtilsJS.registerRestWebClasses()
    UiReflectionUtilsJS.registerWebUiClasses()
    EasyUiActivator().configure(config)
    MainFrameConfiguration.get().logoText = "JAdmin"
    MainFrameConfiguration.get().addTool(object:MainFrameTool{
        override val displayName: String
            get() = "Выход"
        override val weight: Double
            get() = 1000.toDouble()

        override fun handle(mainFrame: MainFrame) {
            UiFactory.get().showConfirmDialog("Вы действительно хотите выйти из программы?"){
                SandboxRestClient.sandbox_auth_logout(LogoutRequestJS()).then {
                    window.location.reload()
                }
            }
        }

    })
    coreActivator.activate().then {
         SandboxRestClient.sandbox_auth_checkAuth(CheckAuthRequestJS()).then client@ {
            if(it.authorized == false){
                val vm = SandboxLoginDialogVMJS()
                val login = window.localStorage["jasmine.sandbox.login"]
                vm.login = login
                UiFactory.get().showDialog(SandboxLoginDialog(), vm, SandboxLoginDialogVSJS())
                return@client
            }
            UiFactory.get().publishMainFrame()
        }
    }

}