/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
package com.gridnine.jasmine.common.spf

import com.gridnine.jasmine.common.core.app.*
import com.gridnine.spf.app.SpfApplication
import com.gridnine.spf.meta.SpfPluginsRegistry
import java.io.File
import java.util.*


class SpfApplicationMetadataProvider(private val registry: SpfPluginsRegistry) : IApplicationMetadataProvider {

    private val clsLoader = SpfApplicationMetadataProvider::class.java.classLoader

    override val plugins: List<IPlugin> by lazy {
        registry.plugins.map {
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
        val root = File(".")
        Environment.configure(root)
        Environment.publish(ConfigurationProvider::class, object: ConfigurationProvider {
            override fun getProperty(propertyName: String): String? {
                return config.getProperty(propertyName)
            }
        })
        val urls = this::javaClass.javaClass.classLoader.getResources("plugin.xml").toList()
        val registry = SpfPluginsRegistry()
        registry.initRegistry(urls)
//        println("plugins")
//        registry.plugins.forEach {
//            println(it.id)
//        }
//        println("activators")
//        registry.getExtensions("activator").forEach {
//            println(it.pluginId)
//        }
        Environment.publish(IApplicationMetadataProvider::class, SpfApplicationMetadataProvider(registry))
        val activators = IApplicationMetadataProvider.get().getExtensions("activator").map { ep ->ep.plugin.classLoader.loadClass(ep.getParameters("class").first()).constructors.first().newInstance() as IPluginActivator }.toList()
        activators.forEach { a ->a.configure(config) }
        activators.forEach { a ->a.activate(config) }

    }

    override fun stop() {
        Environment.dispose()
    }

}