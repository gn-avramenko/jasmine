package com.gridnine.jasmine.gradle.plugin

import com.gridnine.spf.meta.SpfPluginsRegistry
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File
import java.net.URL

@Suppress("unused")
class JasminePlugin: Plugin<Project>{
    override fun apply(target: Project) {
        val extension = target.extensions.getByName("jasmine") as JasmineConfigExtension
        val pluginsURLs = arrayListOf<URL>()
        val pluginsToFileMap = hashMapOf<String,File>()
        extension.pluginsFiles.forEach{
            pluginsToFileMap[it.name] = it
            pluginsURLs.add(File(it, "plugin.xml").toURI().toURL())
        }
        val registry = SpfPluginsRegistry()
        registry.initRegistry(pluginsURLs)
        println(registry.plugins)
    }

}