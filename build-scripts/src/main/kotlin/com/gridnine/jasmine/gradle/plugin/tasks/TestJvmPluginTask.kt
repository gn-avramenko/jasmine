/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.gradle.plugin.tasks

import com.gridnine.jasmine.gradle.plugin.JasmineConfigExtension
import com.gridnine.spf.meta.SpfPlugin
import com.gridnine.spf.meta.SpfPluginsRegistry
import org.gradle.api.tasks.testing.Test
import javax.inject.Inject

@Suppress("unused")
open class TestJvmPluginTask() : Test() {

    @Inject
    constructor(plugin:SpfPlugin, registry: SpfPluginsRegistry, config:JasmineConfigExtension) : this() {
        group = "other"
        dependsOn(CompileKotlinJVMPluginTask.getTaskName(plugin.id))
        dependsOn(CopyJvmResourcesTask.getTaskName(plugin.id))
        val dependentPlugins = KotlinUtils.getDependentPlugins(plugin, registry)
        dependentPlugins.forEach { dp ->
            when(KotlinUtils.getType(dp)){
                SpfPluginType.CORE,SpfPluginType.SERVER,SpfPluginType.SERVER_TEST ->{
                    dependsOn(CompileKotlinJVMPluginTask.getTaskName(dp.id))
                    dependsOn(CopyJvmResourcesTask.getTaskName(dp.id))
                }
                SpfPluginType.SERVER_TEST ->{
                    dependsOn(CompileKotlinJVMPluginTask.getTaskName(dp.id))
                    dependsOn(CopyJvmResourcesTask.getTaskName(dp.id))
                    shouldRunAfter(getTaskName(dp.id))
                }
                else->{}
            }
        }
        useJUnit()
        testClassesDirs = project.files("build/plugins/${plugin.id}/classes")
        val fileNames = linkedSetOf(project.file("${config.libRelativePath}/spf-1.0.jar").absolutePath)
        fileNames.add(project.file("build/plugins/${plugin.id}/classes").absolutePath)
        dependentPlugins.forEach {
            fileNames.add(project.file("build/plugins/${it.id}/classes").absolutePath)
        }
        fileNames.addAll(getLibs(KotlinUtils.SERVER_CONFIGURATION_NAME))
        fileNames.addAll(getLibs(KotlinUtils.SERVER_TEST_CONFIGURATION_NAME))
        classpath = project.files(fileNames.toArray(emptyArray<String>()))
        ignoreFailures = true
        //println("classpath of ${getTaskName(plugin.id)} is ${fileNames.joinToString("\r\n")}")
        binaryResultsDirectory.set(project.file("build/test-results"))
        reports { rp ->
            rp.html.isEnabled = false
            rp.junitXml.destination = project.file("build/junit-reports/${plugin.id}")
        }
    }

    private fun getLibs(serverConfigurationName: String): Collection<String> {
        return project.configurations.getByName(serverConfigurationName).map { it.absolutePath }.toSet()
    }

    companion object{
        fun getTaskName(pluginId: String) = "_testJvmPlugin_$pluginId"
    }
}