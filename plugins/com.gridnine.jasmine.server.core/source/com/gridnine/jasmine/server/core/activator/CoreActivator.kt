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
import com.gridnine.jasmine.server.core.lock.LockManager
import com.gridnine.jasmine.server.core.lock.StandardLockManager
import com.gridnine.jasmine.server.core.model.custom.CustomMetaRegistry
import com.gridnine.jasmine.server.core.model.custom.CustomMetadataParser
import com.gridnine.jasmine.server.core.model.domain.DomainMetaRegistry
import com.gridnine.jasmine.server.core.model.domain.DomainMetadataParser
import com.gridnine.jasmine.server.core.model.rest.RestMetaRegistry
import com.gridnine.jasmine.server.core.model.rest.RestMetadataParser
import com.gridnine.jasmine.server.core.reflection.ReflectionFactory
import com.gridnine.jasmine.server.core.serialization.JsonSerializer
import com.gridnine.jasmine.server.core.storage.StorageRegistry
import com.gridnine.jasmine.server.core.storage.cache.*
import com.gridnine.jasmine.server.core.storage.cache.ehcache.EhCacheManager
import com.gridnine.jasmine.server.core.web.TomcatWebServer
import com.gridnine.jasmine.server.core.web.WebServer
import com.gridnine.jasmine.server.core.web.WebServerConfig
import org.slf4j.bridge.SLF4JBridgeHandler
import java.util.*

class CoreActivator:IPluginActivator{

    override fun configure(config: Properties) {
        val result = DomainMetaRegistry()
        registerDomainMetadata(result)
        Environment.publish(ReflectionFactory())
        Environment.publish(LockManager::class, StandardLockManager())
        Environment.publish(result)
        val restRegistry = RestMetaRegistry()
        registerRestMetadata(restRegistry)
        Environment.publish(restRegistry)
        val customRegistry = CustomMetaRegistry()
        registerCustomMetadata(customRegistry)
        Environment.publish(customRegistry)

        Environment.publish(JsonSerializer())

        Environment.publish(StorageRegistry())
        publishCache()
        Environment.publish(WebServerConfig())
    }

    private fun publishCache() {
        Environment.publish(CacheConfiguration())
        Environment.publish(CacheManager::class, EhCacheManager())
        Environment.publish(CachedObjectsConverter())
        val advice = CacheStorageAdvice(1.0)
        StorageRegistry.get().register(advice)
        StorageRegistry.get().register(InvalidateCacheStorageInterceptor(1.0, advice))
    }

    private fun registerRestMetadata(restRegistry: RestMetaRegistry) {
        val extensions = IApplicationMetadataProvider.get()
                .getExtensions("rest-metadata")
        for (ext in extensions) {
            for (location in ext.getParameters("url")) {
                RestMetadataParser.updateRestMetaRegistry(restRegistry, location, ext.plugin.classLoader)
            }
        }

    }


    private fun registerDomainMetadata(result: DomainMetaRegistry) {
        val extensions = IApplicationMetadataProvider.get()
                .getExtensions("domain-metadata")
        for (ext in extensions) {
            for (location in ext.getParameters("url")) {
                DomainMetadataParser.updateDomainMetaRegistry(result, location, ext.plugin.classLoader)
            }
        }
    }


    private fun registerCustomMetadata(result: CustomMetaRegistry) {
        val extensions = IApplicationMetadataProvider.get()
                .getExtensions("custom-metadata")
        for (ext in extensions) {
            for (location in ext.getParameters("url")) {
                CustomMetadataParser.updateCustomMetaRegistry(result, location, ext.plugin.classLoader)
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