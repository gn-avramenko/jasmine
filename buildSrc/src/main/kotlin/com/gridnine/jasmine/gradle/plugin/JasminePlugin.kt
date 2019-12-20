/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
package com.gridnine.jasmine.gradle.plugin

import com.gridnine.spf.meta.SpfPluginsRegistry
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File
import java.lang.IllegalStateException

class JasminePlugin : Plugin<Project> {


    override fun apply(target: Project) {
        val pluginsURLs = arrayListOf<java.net.URL>()
        File(target.projectDir, "plugins").listFiles()?.forEach {
            pluginsURLs.add(File(it, "plugin.xml").toURI().toURL())
        }

        val registry = SpfPluginsRegistry()
        registry.initRegistry(pluginsURLs)
        createConfiguration("server", registry, target, SpfPluginType.CORE, SpfPluginType.SERVER)
        createConfiguration("server_test", registry, target, SpfPluginType.SERVER_TEST)
        target.configurations.maybeCreate(KotlinUtils.COMPILER_CLASSPATH_CONFIGURATION_NAME).defaultDependencies {
            it.add(target.dependencies.create("${KotlinUtils.KOTLIN_MODULE_GROUP}:${KotlinUtils.KOTLIN_COMPILER_EMBEDDABLE}:${KotlinUtils.KOTLIN_PLUGIN_VERSION}"))
        }
        target.repositories.add(target.repositories.mavenCentral())
        target.tasks.create("_createLibraries", CreateLibrariesTask::class.java)
        target.tasks.create("_createArtifacts", CreateArtifactsTask::class.java)
        target.tasks.create("_createModules", CreateModulesTask::class.java).registry = registry
        target.tasks.create("_createTemplates", CreateTemplatesTask::class.java)
        target.tasks.create("makeProject") {
            it.group = "idea"
            it.dependsOn("_createLibraries", "_createArtifacts", "_createModules", "_createTemplates")
        }
        target.tasks.create("codeGen", CodeGenPluginTask::class.java) { task ->
            task.group = "idea"
            task.dependsOn("_buildJVMPlugin-${registry.plugins.find { KotlinUtils.getType(it) == SpfPluginType.CORE }?.id?:throw IllegalStateException("CORE plugin is absent")}")
            task.dependsOn("_buildJVMPlugin-${registry.plugins.find { KotlinUtils.getType(it)  == SpfPluginType.SPF }?.id?:throw IllegalStateException("SPF plugin is absent")}")
        }

        registry.plugins.forEach { spfPlugin ->
            when (val pluginType = KotlinUtils.getType(spfPlugin)) {
                SpfPluginType.CORE, SpfPluginType.SERVER, SpfPluginType.SERVER_TEST, SpfPluginType.SPF -> {
                    target.tasks.create("_buildJVMPlugin-${spfPlugin.id}", CompileKotlinJVMPluginTask::class.java) { task ->
                        task.group = "other"
                        task.registry = registry
                        task.pluginId = spfPlugin.id
                        task.dependencies.add("server")
                        if (pluginType == SpfPluginType.SERVER_TEST) {
                            task.dependencies.add("server_test")
                        }
                        spfPlugin.pluginsDependencies.forEach { dep ->
                            task.dependsOn.add("_buildJVMPlugin-${dep.pluginId}")
                            if (pluginType != SpfPluginType.CORE && pluginType != SpfPluginType.SPF) {
                                task.dependsOn.add("codeGen")
                            }
                        }
                        val sourceDir = File(target.projectDir, "plugins/${spfPlugin.id}/source")
                        if(sourceDir.exists()){
                            task.inputs.dir(sourceDir)
                        }
                        task.outputs.dir(File(target.projectDir, "build/plugins/${spfPlugin.id}/classes"))
                    }
                }
            }
        }


    }

    private fun createConfiguration(configurationName: String, registry: SpfPluginsRegistry, project: Project, vararg types: SpfPluginType) {
        project.configurations.create(configurationName)
        val depts = arrayListOf<String>()
        registry.plugins.filter { p -> types.contains(KotlinUtils.getType(p)) }.forEach { plugin ->
            plugin.libsDependencies.forEach {
                depts.add("${it.group}:${it.name}:${it.version}")
            }
        }

        depts.forEach {
            project.dependencies.add(configurationName, it)
        }
    }
}

