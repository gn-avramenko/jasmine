

package com.gridnine.jasmine.server.sandbox.test.web

import com.gridnine.jasmine.server.core.app.ConfigurationProvider
import com.gridnine.jasmine.server.core.app.Environment
import com.gridnine.jasmine.server.core.app.IApplicationMetadataProvider
import com.gridnine.jasmine.server.core.app.IPluginActivator
import com.gridnine.jasmine.server.core.storage.StorageRegistry
import com.gridnine.jasmine.server.core.web.WebApplication
import com.gridnine.jasmine.server.core.web.WebServerConfig
import com.gridnine.jasmine.server.sandbox.rest.SandboxWorkspaceProvider
import com.gridnine.jasmine.server.sandbox.storage.SandboxComplexDocumentIndexHandler
import com.gridnine.jasmine.server.sandbox.storage.SandboxComplexDocumentVariantIndexHandler
import com.gridnine.jasmine.server.sandbox.storage.SandboxUserAccountIndexHandler
import com.gridnine.jasmine.server.spf.SpfApplicationMetadataProvider
import com.gridnine.jasmine.server.standard.rest.WorkspaceProvider
import com.gridnine.spf.app.SpfApplication
import com.gridnine.spf.app.SpfBoot
import com.gridnine.spf.meta.SpfPluginsRegistry
import java.io.File
import java.util.*

@Suppress("unused")
class SandboxWebTest :SpfApplication{


    override fun start(config: Properties) {
        val file = File("test/sandbox")
        if(!file.exists()) file.mkdirs()
        Environment.configure(file)
        Environment.publish(ConfigurationProvider::class, object: ConfigurationProvider {
            override fun getProperty(propertyName: String): String? {
                return null
            }
        })
        val urls = this::javaClass.javaClass.classLoader.getResources("plugin.xml").toList()
        val registry = SpfPluginsRegistry()
        registry.initRegistry(urls){
            "com.gridnine.jasmine.server.core" == it.id
                    || "com.gridnine.jasmine.server.db.h2" == it.id
                    || "com.gridnine.jasmine.server.sandbox" == it.id
                    || "com.gridnine.jasmine.server.standard" == it.id
        }
        Environment.publish(IApplicationMetadataProvider::class, SpfApplicationMetadataProvider(registry))
        val activators =IApplicationMetadataProvider.get().getExtensions("activator").filter { it.plugin.pluginId !=  "com.gridnine.jasmine.server.sandbox"}.map { ep ->ep.plugin.classLoader.loadClass(ep.getParameters("class").first()).constructors.first().newInstance() as IPluginActivator }.toList()
        activators.forEach { a ->a.configure(config) }
        StorageRegistry.get().register(SandboxComplexDocumentIndexHandler())
        StorageRegistry.get().register(SandboxComplexDocumentVariantIndexHandler())
        StorageRegistry.get().register(SandboxUserAccountIndexHandler())
        Environment.publish(WorkspaceProvider::class, SandboxWorkspaceProvider())
        val easyuiApp = WebApplication("/sandbox/easyui", javaClass.classLoader.getResource("sb_easyui")
                ?: throw IllegalArgumentException("unable to load resource sb_easyui"),
                javaClass.classLoader)
        WebServerConfig.get().addApplication(easyuiApp)
        activators.forEach { a ->a.activate() }
    }

    override fun stop() {
        Environment.dispose()
    }
}