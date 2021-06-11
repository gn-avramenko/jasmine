package com.gridnine.jasmine.server.core.test


import com.gridnine.jasmine.common.core.app.ConfigurationProvider
import com.gridnine.jasmine.common.core.app.Environment
import com.gridnine.jasmine.common.core.app.IApplicationMetadataProvider
import com.gridnine.jasmine.common.core.app.IPluginActivator
import com.gridnine.jasmine.common.spf.SpfApplicationMetadataProvider
import com.gridnine.spf.app.SpfApplication
import com.gridnine.spf.meta.SpfPluginsRegistry
import java.io.File
import java.util.*

@Suppress("unused")
abstract class BaseWebTest : SpfApplication {

    protected abstract fun getPluginId():String

    override fun start(config: Properties) {
        val file = File("test/demo")
        if(file.exists()){
            file.deleteRecursively()
        }
        if (!file.exists()) file.mkdirs()
        Environment.configure(file)
        Environment.publish(ConfigurationProvider::class, object : ConfigurationProvider {
            override fun getProperty(propertyName: String): String? {
                if("db-dialect" == propertyName){
                    return "h2"
                }
                if("db.h2.connectionUrl" == propertyName){
                    return "jdbc:h2:mem:jasmine"
                }
                return null
            }
        })
        val clLoader = this::class.java.classLoader
        val urls = clLoader.getResources("plugin.xml").toList()
        var registry = SpfPluginsRegistry()
        registry.initRegistry(urls)
        val demoPlugin = registry.plugins.find { it.id == getPluginId() }!!
        val dependencies = hashSetOf<String>()
        collectDependencies(demoPlugin.id, registry, dependencies)
        registry = SpfPluginsRegistry()
        registry.initRegistry(urls){
            dependencies.contains(it.id)
        }
        Environment.publish(IApplicationMetadataProvider::class, SpfApplicationMetadataProvider(registry))
        val activators = IApplicationMetadataProvider.get().getExtensions("activator").map { ep -> ep.plugin.classLoader.loadClass(ep.getParameters("class").first()).constructors.first().newInstance() as IPluginActivator }.toList()
        activators.forEach { a -> a.configure(config) }
        activators.forEach { a -> a.activate(config) }
    }

    private fun collectDependencies(id: String, registry: SpfPluginsRegistry, dependencies: HashSet<String>) {
        if(dependencies.contains(id)){
            return
        }
        dependencies.add(id)
        registry.plugins.find { it.id == id }!!.pluginsDependencies.forEach {
            collectDependencies(it.pluginId, registry, dependencies)
        }
    }


    override fun stop() {
        Environment.dispose()
    }
}