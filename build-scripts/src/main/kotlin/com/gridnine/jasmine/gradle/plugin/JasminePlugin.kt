/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
package com.gridnine.jasmine.gradle.plugin

import com.gridnine.jasmine.gradle.plugin.tasks.*
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
        KotlinUtils.createConfiguration(KotlinUtils.SERVER_CONFIGURATION_NAME, registry, target, SpfPluginType.CORE, SpfPluginType.SERVER)
        KotlinUtils.createConfiguration(KotlinUtils.SERVER_TEST_CONFIGURATION_NAME, registry, target, SpfPluginType.SERVER_TEST)
        target.tasks.create(CreateArtifactsTask.TASK_NAME, CreateArtifactsTask::class.java, extension)
        target.tasks.create(CreateLibrariesTask.TASK_NAME, CreateLibrariesTask::class.java, extension)
        target.tasks.create(CreateModulesTask.TASK_NAME, CreateModulesTask::class.java,registry, extension, pluginsToFileMap)
        target.tasks.create(MakeProjectTask.TASK_NAME, MakeProjectTask::class.java)
    }

}