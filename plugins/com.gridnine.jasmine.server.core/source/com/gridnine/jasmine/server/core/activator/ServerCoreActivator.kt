/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.activator

import com.gridnine.jasmine.common.core.app.ConfigurationProvider
import com.gridnine.jasmine.common.core.app.Environment
import com.gridnine.jasmine.common.core.app.IPluginActivator
import com.gridnine.jasmine.common.core.storage.CachedObjectsConverter
import com.gridnine.jasmine.server.core.storage.StorageRegistry
import com.gridnine.jasmine.server.core.storage.cache.*
import com.gridnine.jasmine.server.core.storage.cache.ehcache.EhCacheManager
import com.gridnine.jasmine.server.core.web.TomcatWebServer
import com.gridnine.jasmine.server.core.web.WebServer
import com.gridnine.jasmine.server.core.web.WebServerConfig
import org.slf4j.bridge.SLF4JBridgeHandler
import java.util.*

class ServerCoreActivator:IPluginActivator{

    override fun configure(config: Properties) {
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

    override fun activate() {
        publishTomcat()
    }

    private fun publishTomcat() {
        SLF4JBridgeHandler.removeHandlersForRootLogger()
        SLF4JBridgeHandler.install()
        val portStr = ConfigurationProvider.get().getProperty("tomcat.port")
        var port = 8080
        if (portStr != null && portStr.isNotBlank()) {
            port = Integer.parseInt(portStr.trim())
        }
        Environment.publish(WebServer::class, TomcatWebServer(port))
    }



}