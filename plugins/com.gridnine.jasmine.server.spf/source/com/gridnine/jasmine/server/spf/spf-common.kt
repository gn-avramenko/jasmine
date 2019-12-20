/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")
package com.gridnine.jasmine.server.spf

import com.gridnine.jasmine.server.core.app.IApplicationMetadataProvider
import com.gridnine.jasmine.server.core.app.IExtension
import com.gridnine.jasmine.server.core.app.IPlugin
import com.gridnine.spf.meta.SpfPluginsRegistry


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

        return registry.getExtensions(extensionPointId).map {
            object : IExtension {
                override val classLoader: ClassLoader
                    get() = clsLoader

                override fun getParameters(paramName: String): List<String> {
                    return it.parameters.filter { p -> p.id == paramName }.map { p -> p.value }.toList()
                }


            }
        }.toList()
    }
}