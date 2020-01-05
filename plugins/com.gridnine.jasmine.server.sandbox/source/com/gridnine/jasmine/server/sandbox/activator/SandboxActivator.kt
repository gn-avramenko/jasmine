/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")

package com.gridnine.jasmine.server.sandbox.activator

import com.gridnine.jasmine.server.core.app.Environment
import com.gridnine.jasmine.server.core.app.IPluginActivator
import com.gridnine.jasmine.server.core.rest.NoCacheFilter
import com.gridnine.jasmine.server.core.storage.Storage
import com.gridnine.jasmine.server.core.storage.StorageRegistry
import com.gridnine.jasmine.server.core.storage.search.SearchQuery
import com.gridnine.jasmine.server.core.web.WebAppFilter
import com.gridnine.jasmine.server.core.web.WebApplication
import com.gridnine.jasmine.server.core.web.WebServerConfig
import com.gridnine.jasmine.server.sandbox.model.domain.SandboxUserAccount
import com.gridnine.jasmine.server.sandbox.model.domain.SandboxUserAccountIndex
import com.gridnine.jasmine.server.sandbox.rest.SandboxWorkspaceProvider
import com.gridnine.jasmine.server.sandbox.storage.SandboxComplexDocumentIndexHandler
import com.gridnine.jasmine.server.sandbox.storage.SandboxUserAccountIndexHandler
import com.gridnine.jasmine.server.standard.rest.WorkspaceProvider
import java.lang.IllegalArgumentException
import java.util.*

class SandboxActivator:IPluginActivator{
    override fun configure(config: Properties) {
        StorageRegistry.get().register(SandboxComplexDocumentIndexHandler())
        StorageRegistry.get().register(SandboxUserAccountIndexHandler())
        val easyuiApp = WebApplication("/sandbox/easyui", javaClass.classLoader.getResource("sb_easyui")
                ?:throw IllegalArgumentException("unable to load resource sb_easyui"),
                javaClass.classLoader)
        WebServerConfig.get().addApplication(easyuiApp)
        val webCoreApp = WebApplication("/web-core", javaClass.classLoader.getResource("webapp-core")
                ?:throw IllegalArgumentException("unable to load resource webapp-core"),
                javaClass.classLoader)
        WebServerConfig.get().addApplication(webCoreApp)
        val sandboxApp = WebApplication("/web-sandbox", javaClass.classLoader.getResource("web-sandbox")
                ?:throw IllegalArgumentException("unable to load resource web-sandbox"),
                javaClass.classLoader)
        WebServerConfig.get().addApplication(sandboxApp)
        val easyuiWebapp = WebApplication("/web-easyui-libs", javaClass.classLoader.getResource("easyui-libs")
                ?:throw IllegalArgumentException("unable to load resource easyui-libs"),
                javaClass.classLoader)
        WebServerConfig.get().addApplication(easyuiWebapp)

        val easyuiScriptWebapp = WebApplication("/web-easyui-script", javaClass.classLoader.getResource("easyui-script")
                ?:throw IllegalArgumentException("unable to load resource easyui-script"),
                javaClass.classLoader)
        WebServerConfig.get().addApplication(easyuiScriptWebapp)
        WebServerConfig.get().globalFilters.add(WebAppFilter("nocache", NoCacheFilter::class))
        Environment.publish(WorkspaceProvider::class, SandboxWorkspaceProvider())
    }

    override fun activate() {
        val size = Storage.get().searchDocuments(SandboxUserAccountIndex::class, SearchQuery()).size
        if(size == 0){
            val adminAccount = SandboxUserAccount()
            adminAccount.name = "Jasmine Admin"
            adminAccount.login ="admin"
            adminAccount.password = "admin"
            Storage.get().saveDocument(adminAccount)
            for(n in 1..9){
                val userAccount = SandboxUserAccount()
                userAccount.name = "User $n"
                userAccount.login = "user$n"
                userAccount.password = "user$n"
                Storage.get().saveDocument(userAccount)
            }
        }
    }
}