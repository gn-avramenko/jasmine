/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")
package com.gridnine.jasmine.server.spf

import com.gridnine.jasmine.server.core.app.*
import com.gridnine.spf.app.SpfApplication
import com.gridnine.spf.meta.SpfPluginsRegistry
import java.io.File
import java.util.*


class SpfApplicationMetadataProvider(private val registry: SpfPluginsRegistry) : IApplicationMetadataProvider {

    private val clsLoader = SpfApplicationMetadataProvider::class.java.classLoader

    override val plugins: List<IPlugin>
        get() {
            return registry.plugins.map {
                object : IPlugin {
                    override val pluginId: String
                        get() = it.id
                    override val classLoader: ClassLoader
                        get() = clsLoader
                }
            }.toList()
        }


    override fun getExtensions(extensionPointId: String): List<IExtension> {

        return registry.getExtensions(extensionPointId).map {spfExtendsion ->
            object : IExtension {
                override val plugin: IPlugin
                    get() = plugins.find { it.pluginId == spfExtendsion.pluginId}!!


                override fun getParameters(paramName: String): List<String> {
                    return spfExtendsion.parameters.filter { p -> p.id == paramName }.map { p -> p.value }.toList()
                }


            }
        }.toList()
    }
}

class SpfApplicationImpl: SpfApplication {
    override fun start(config: Properties) {
        Environment.configure(File("."))
        Environment.publish(ConfigurationProvider::class, object:ConfigurationProvider{
            override fun getProperty(propertyName: String): String? {
                return config.getProperty(propertyName)
            }
        })
        val urls = this::javaClass.javaClass.classLoader.getResources("plugin.xml").toList()
        val registry = SpfPluginsRegistry()
        registry.initRegistry(urls)
        Environment.publish(IApplicationMetadataProvider::class, SpfApplicationMetadataProvider(registry))
        val activators =IApplicationMetadataProvider.get().getExtensions("activator").map { ep ->ep.plugin.classLoader.loadClass(ep.getParameters("class").first()).constructors.first().newInstance() as IPluginActivator }.toList()
        activators.forEach { a ->a.configure(config) }
        activators.forEach { a ->a.activate() }

    }

    override fun stop() {
        Environment.dispose()
    }

}