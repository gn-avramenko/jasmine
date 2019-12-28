/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")

package com.gridnine.jasmine.gradle.plugin

import com.gridnine.spf.meta.SpfPlugin
import java.io.File

object KotlinUtils{
    internal const val COMPILER_CLASSPATH_CONFIGURATION_NAME = "kotlinCompilerClasspath"
    internal const val KOTLIN_MODULE_GROUP = "org.jetbrains.kotlin"
    internal const val KOTLIN_COMPILER_EMBEDDABLE = "kotlin-compiler-embeddable"
    internal const val COMPILER_CLASS_JVM = "org.jetbrains.kotlin.cli.jvm.K2JVMCompiler"
    internal const val COMPILER_CLASS_JS = "org.jetbrains.kotlin.cli.js.K2JSCompiler"
    internal const val KOTLIN_PLUGIN_VERSION="1.3.61"

    fun getType(plugin:SpfPlugin) : SpfPluginType{
        return SpfPluginType.valueOf(plugin.parameters.first { it.id == "type" }.value)
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
    WEB
}
