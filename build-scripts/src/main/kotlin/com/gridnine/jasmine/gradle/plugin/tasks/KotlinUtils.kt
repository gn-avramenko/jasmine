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

object KotlinUtils{
    internal const val COMPILER_CLASSPATH_CONFIGURATION_NAME = "kotlinCompilerClasspath"
    internal const val KOTLIN_MODULE_GROUP = "org.jetbrains.kotlin"
    internal const val KOTLIN_COMPILER_EMBEDDABLE = "kotlin-compiler-embeddable"
    internal const val COMPILER_CLASS_JVM = "org.jetbrains.kotlin.cli.jvm.K2JVMCompiler"
    internal const val COMPILER_CLASS_JS = "org.jetbrains.kotlin.cli.js.K2JSCompiler"
    const val SERVER_CONFIGURATION_NAME = "server"
    const val SERVER_TEST_CONFIGURATION_NAME = "server_test"

    fun getType(plugin:SpfPlugin) : SpfPluginType{
        return SpfPluginType.valueOf(plugin.parameters.first { it.id == "type" }.value)
    }

    fun createConfiguration(configurationName: String, registry: SpfPluginsRegistry, project: Project, vararg types: SpfPluginType) {
        project.configurations.create(configurationName)
        registry.plugins.filter { p -> types.contains(getType(p)) }.forEach { plugin ->
            plugin.libsDependencies.forEach {
                project.dependencies.add(configurationName, "${it.group}:${it.name}:${it.version}")
            }
        }

    }
}

fun File.writeIfDiffers(content:ByteArray){
    if(this.exists() && this.readBytes().contentEquals(content)){
        return
    }
    this.writeBytes(content)
    println("content of $this was changed")
}

fun File.writeIfDiffers(content:String){
    if(this.exists() && this.readText() == content){
        return
    }
    val originalContent =   if(this.exists()) this.readText() else null
    if(originalContent != content) {
        this.writeText(content, Charsets.UTF_8)
        println("content of $this was changed")
        println ("old content\n$originalContent")
        println ("new content\n$content")
    }
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
    WEB_TEST
}
