/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")

package com.gridnine.jasmine.gradle.plugin

import com.gridnine.spf.meta.SpfPlugin

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

enum class ExitCode(val code: Int) {
    OK(0), COMPILATION_ERROR(1), INTERNAL_ERROR(2), SCRIPT_EXECUTION_ERROR(3);

}

enum class SpfPluginType{
    CORE,
    SERVER,
    SERVER_TEST,
    SPF
}
