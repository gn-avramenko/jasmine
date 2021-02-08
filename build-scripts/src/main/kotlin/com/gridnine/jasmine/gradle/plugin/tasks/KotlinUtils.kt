/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")

package com.gridnine.jasmine.gradle.plugin.tasks

import com.gridnine.spf.meta.SpfPlugin
import com.gridnine.spf.meta.SpfPluginsRegistry
import org.gradle.api.Project
import java.io.File
import java.util.*

object KotlinUtils{
    internal const val COMPILER_CLASSPATH_CONFIGURATION_NAME = "kotlinCompilerClasspath"
    internal const val KOTLIN_MODULE_GROUP = "org.jetbrains.kotlin"
    internal const val KOTLIN_COMPILER_EMBEDDABLE = "kotlin-compiler-embeddable"
    internal const val COMPILER_CLASS_JVM = "org.jetbrains.kotlin.cli.jvm.K2JVMCompiler"
    internal const val COMPILER_CLASS_JS = "org.jetbrains.kotlin.cli.js.K2JSCompiler"
    const val SERVER_CONFIGURATION_NAME = "server"
    const val SERVER_TEST_CONFIGURATION_NAME = "server_test"
    const val WEB_CONFIGURATION_NAME = "web_js"

    fun getDependentPlugins(plugin: SpfPlugin, registry: SpfPluginsRegistry):List<SpfPlugin>{
        val plug1Depths: MutableSet<String> = HashSet()
        collectDependencies(plug1Depths, plugin, registry)
        return registry.plugins.filter { plug1Depths.contains(it.id) }.toList()
    }

    private fun collectDependencies(depth: MutableSet<String>, plug: SpfPlugin, registry: SpfPluginsRegistry) {
        plug.pluginsDependencies.forEach{ dep->
            if (depth.add(dep.pluginId)) {
                collectDependencies(depth, registry.plugins.find{ dep.pluginId == it.id }!!, registry)
            }
        }
    }
    fun getType(plugin: SpfPlugin) : SpfPluginType{
        return SpfPluginType.valueOf(plugin.parameters.first { it.id == "type" }.value)
    }

    fun getPlugin(pluginId: String, registry: SpfPluginsRegistry) : SpfPlugin{
        return registry.plugins.find { it.id == pluginId }?:throw IllegalArgumentException("unable to find plugin with id $pluginId")
    }

    fun createConfiguration(configurationName: String, registry: SpfPluginsRegistry, project: Project, filesMap: Map<String, File>, vararg types: SpfPluginType) {
        project.configurations.create(configurationName)
        registry.plugins.filter { p -> types.contains(getType(p)) }.forEach { plugin ->
            plugin.libsDependencies.forEach {
                project.dependencies.add(configurationName, "${it.group}:${it.name}:${it.version}")
            }
            var resourcesFile = File(filesMap[plugin.id], "resources")
            if(resourcesFile.exists()){
                resourcesFile.listFiles()?.forEach {file ->
                    if(file.isFile && file.name.endsWith(".jar")){
                        project.dependencies.add(configurationName, project.files(file.absolutePath))
                    }
                }
            }
        }

    }

    fun getSpfFile(registry: SpfPluginsRegistry, filesMap: Map<String, File>):File{
        val jasmineDir = filesMap[registry.plugins.find { KotlinUtils.getType(it) == SpfPluginType.SERVER }!!.id]!!.parentFile.parentFile
        return File(jasmineDir, "lib/spf-1.0.jar")
    }
}

fun File.writeIfDiffers(content: ByteArray){
    if(this.exists() && this.readBytes().contentEquals(content)){
        return
    }
    this.writeBytes(content)
    println("content of $this was changed")
}

fun File.writeIfDiffers(content: String){
    if(this.exists() && this.readText() == content){
        return
    }
    val originalContent =   if(this.exists()) this.readText() else null
    if(originalContent != content) {
        this.writeText(content, Charsets.UTF_8)
        println("content of $this was changed")
        println("old content\n$originalContent")
        println("new content\n$content")
    }
}

fun File.emptyDir(){
    if(this.exists()){
        this.listFiles()?.forEach {
            it.deleteRecursively()
        }
    }
}

fun File.ensureDirExists(){
    if(this.exists()){
        return
    }
    if(this.isFile && !this.parentFile.exists()){
        this.parentFile.mkdirs()
        return
    }
    this.mkdirs()

}

enum class ExitCode(val code: Int) {
    OK(0), COMPILATION_ERROR(1), INTERNAL_ERROR(2), SCRIPT_EXECUTION_ERROR(3);

}

enum class SpfPluginType{
    CORE,
    SERVER,
    SERVER_TEST,
    SPF,
    WEB,
    WEB_CORE,
    WEB_TEST
}
