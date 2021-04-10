/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.gradle.plugin.tasks

import com.gridnine.jasmine.gradle.plugin.JasmineConfigExtension
import com.gridnine.spf.app.SpfBoot
import com.gridnine.spf.meta.SpfPlugin
import com.gridnine.spf.meta.SpfPluginsRegistry
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import java.io.File
import javax.inject.Inject

@Suppress("unused")
open class StartTestServerInBuildTask() : BaseStartServerTask() {

    @Inject
    constructor(registry: SpfPluginsRegistry, plugin: SpfPlugin,config:JasmineConfigExtension, filesMap: Map<String, File>):this(){
        val launcherClassName = plugin.parameters.find { param -> param.id == "server-launcher-class" }?.value
        group = "other"
        jvmArgs = arrayListOf("-Dspf.mode=shell", "-Dspf.applicationClass=$launcherClassName")
        main = "com.gridnine.spf.app.SpfBoot"
        this.classpath = getClassPath(project, registry, config, filesMap)
        registry.plugins.forEach {
            if(KotlinUtils.getType(it) == SpfPluginType.SERVER_TEST){
                dependsOn(TestJvmPluginTask.getTaskName(it.id))
            }
        }
        KotlinUtils.getDependentPlugins(plugin, registry).forEach {
            if(KotlinUtils.getType(it) == SpfPluginType.WEB_TEST){
                val suiteLauncher = it.parameters.find{ param -> param.id == "test-suite-launcher" }?.value
                if(suiteLauncher != null) {
                    dependsOn(TestJsPluginTask.getTaskName(it.id))
                }
            }
        }
        this.doLast {
            while (!SpfBoot.isApplicationRunning()) {
                Thread.sleep(1000L)
            }
        }
    }

    companion object{
        fun getTaskName(pluginId: String) = "_jsTestStartServerInBuild_${pluginId}"
        fun getClassPath(project:Project, registry:SpfPluginsRegistry, config:JasmineConfigExtension, filesMap:Map<String, File>): FileCollection {
            val fileNames = linkedSetOf(project.file("${config.libRelativePath}/spf-1.0.jar").absolutePath)
            registry.plugins.forEach {
                fileNames.add(project.file("build/plugins/${it.id}/classes").absolutePath)
                val pluginFile = filesMap[it.id]!!
                fileNames.add(pluginFile.absolutePath)
                val resourcesFile = File(pluginFile, "resources")
                if(resourcesFile.exists()){
                    fileNames.add(resourcesFile.absolutePath)
                }
            }
            fileNames.addAll(getLibs(KotlinUtils.SERVER_CONFIGURATION_NAME, project))
            fileNames.addAll(getLibs(KotlinUtils.SERVER_TEST_CONFIGURATION_NAME, project))
            return  project.files(fileNames.toTypedArray())
        }
        private fun getLibs(serverConfigurationName: String, project: Project): Collection<String> {
            return project.configurations.getByName(serverConfigurationName).map { it.absolutePath }.toSet()
        }
    }

}