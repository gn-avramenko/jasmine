package com.gridnine.jasmine.gradle.plugin.tasks

import com.gridnine.jasmine.gradle.plugin.JasmineConfigExtension
import com.gridnine.spf.meta.SpfPluginsRegistry
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.net.URLClassLoader
import javax.inject.Inject

@Suppress("unused")
open class CodeGenPluginTask() :DefaultTask(){

    private lateinit var registry:SpfPluginsRegistry

    private lateinit var config:JasmineConfigExtension

    private lateinit var pluginsLocations:Map<String,File>
    @Inject
    constructor(registry: SpfPluginsRegistry, config:JasmineConfigExtension, pluginsLocations:Map<String,File>):this(){
        group = "idea"
        this.registry =registry
        this.config = config
        this.pluginsLocations = pluginsLocations
        registry.plugins.forEach { plugin ->
            when(KotlinUtils.getType(plugin)){
                SpfPluginType.SPF,SpfPluginType.CORE -> {
                    dependsOn(CompileKotlinJVMPluginTask.getTaskName(plugin.id))
                }
                else ->{}
            }
        }
    }

    @TaskAction
    fun codegen(){
        val urls = project.configurations.getByName(KotlinUtils.SERVER_CONFIGURATION_NAME).map { it.toURI().toURL() }.toMutableList()
        registry.plugins.forEach { plugin ->
            when(KotlinUtils.getType(plugin)){
                SpfPluginType.SPF,SpfPluginType.CORE -> {
                    urls.add(File(project.projectDir, "build/plugins/${plugin.id}/classes").toURI().toURL())
                }
                else ->{}
            }
        }
        urls.add(File(project.projectDir, "${config.libRelativePath}/spf-1.0.jar").toURI().toURL())
        val classLoader = URLClassLoader(urls.toTypedArray(), this::class.java.classLoader)
        val codeGen = Class.forName("com.gridnine.jasmine.server.spf.SpfCodeGen", true, classLoader)
        val method = codeGen.getMethod("generateCode")
        val instance = codeGen.constructors[0].newInstance(registry, project.name, pluginsLocations)
        method.invoke(instance)
    }

    companion object{
        const val TASK_NAME = "codeGen"
    }
}