/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")

package com.gridnine.jasmine.server.core.activator

import com.gridnine.jasmine.server.core.app.ConfigurationProvider
import com.gridnine.jasmine.server.core.app.Environment
import com.gridnine.jasmine.server.core.app.IApplicationMetadataProvider
import com.gridnine.jasmine.server.core.app.IPluginActivator
import com.gridnine.jasmine.server.core.model.domain.DomainMetaRegistry
import com.gridnine.jasmine.server.core.model.domain.DomainMetadataParser
import com.gridnine.jasmine.server.core.model.rest.RestMetaRegistry
import com.gridnine.jasmine.server.core.model.rest.RestMetadataParser
import com.gridnine.jasmine.server.core.model.ui.UiMetaRegistry
import com.gridnine.jasmine.server.core.model.ui.UiMetadataParser
import com.gridnine.jasmine.server.core.storage.StorageRegistry
import com.gridnine.jasmine.server.core.storage.cache.CacheAdvice
import com.gridnine.jasmine.server.core.storage.cache.CacheConfiguration
import com.gridnine.jasmine.server.core.storage.cache.ModelCacheConfigurator
import com.gridnine.jasmine.server.core.storage.cache.SimpleMapCacheStorage
import com.gridnine.jasmine.server.core.web.TomcatWebServer
import com.gridnine.jasmine.server.core.web.WebServer
import com.gridnine.jasmine.server.core.web.WebServerConfig
import org.slf4j.bridge.SLF4JBridgeHandler
import java.util.*

class CoreActivator:IPluginActivator{

    override fun configure(config: Properties) {
        val result = DomainMetaRegistry()
        registerMetadata(result)
        Environment.publish(result)
        val restRegistry = RestMetaRegistry()
        registerRestMetadata(restRegistry)
        Environment.publish(restRegistry)
        val uiRegistry = UiMetaRegistry()
        registerUiMetadata(uiRegistry)
        Environment.publish(uiRegistry)
        Environment.publish(StorageRegistry())
        publishCache()
        Environment.publish(WebServerConfig())
    }

    private fun publishCache() {
        val config = CacheConfiguration()
        Environment.publish(CacheConfiguration())
        ModelCacheConfigurator.configure(config)
        StorageRegistry.get().register(CacheAdvice(1.0, SimpleMapCacheStorage()))
    }

    private fun registerRestMetadata(restRegistry: RestMetaRegistry) {
        val extensions = IApplicationMetadataProvider.get()
                .getExtensions("rest-metadata")
        for (ext in extensions) {
            for (location in ext.getParameters("url")) {
                RestMetadataParser.updateRestMetaRegistry(restRegistry, location, ext.classLoader)
            }
        }

    }

    private fun registerUiMetadata(uiMetaRegistry: UiMetaRegistry) {
        val extensions = IApplicationMetadataProvider.get()
                .getExtensions("ui-metadata")
        for (ext in extensions) {
            for (location in ext.getParameters("url")) {
                UiMetadataParser.updateUiMetaRegistry(uiMetaRegistry, location, ext.classLoader)
            }
        }

    }



    private fun registerMetadata(result: DomainMetaRegistry) {
        val extensions = IApplicationMetadataProvider.get()
                .getExtensions("domain-metadata")
        for (ext in extensions) {
            for (location in ext.getParameters("url")) {
                DomainMetadataParser.updateDomainMetaRegistry(result, location, ext.classLoader)
            }
        }
    }


    override fun activate() {
        publishTomcat()
    }

    private fun publishTomcat() {
        SLF4JBridgeHandler.removeHandlersForRootLogger()
        SLF4JBridgeHandler.install()
        val portStr = ConfigurationProvider.get()
                .getProperty("tomcat.port")
        var port = 8080
        if (portStr != null && portStr.isNotBlank()) {
            port = Integer.parseInt(portStr.trim())
        }
        Environment.publish(WebServer::class, TomcatWebServer(port))
    }



}