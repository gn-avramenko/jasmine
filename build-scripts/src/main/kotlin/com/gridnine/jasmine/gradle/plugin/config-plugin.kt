/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("unused")

package com.gridnine.jasmine.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import java.io.File

class JasmineConfigPlugin :Plugin<Project> {
    override fun apply(target: Project) {
        target.extensions.create("jasmine", JasmineConfigExtension::class.java, target)
    }
}

open class JasmineConfigExtension(private val project:Project){
    var kotlinVersion = "1.4.10"
    var targetByteCodeLevel = "1.8"
    var languageLevel = "JDK_1_8"
    var libRelativePath = "lib"
    val pluginsFiles = arrayListOf<File>()
    var indexWar = ""
    var enableWebTasks = false
    fun plugins(submodulePath: String, configure:JasminePluginConfigurator.()->Unit) {
        JasminePluginConfigurator(submodulePath, this, project).configure()
    }


}
class JasminePluginConfigurator(private val submodulePath:String, private val extension: JasmineConfigExtension, private val project:Project){
    fun plugin(path:String){
        val file = project.file("${submodulePath}/${path}")
        if(!file.exists() || !file.isDirectory){
            throw Exception("file ${file.absolutePath} does not exist" )
        }
        extension.pluginsFiles.add(file)
    }
}


fun Project.jasmine(configure: JasmineConfigExtension.() -> Unit): Unit =
        (this as ExtensionAware).extensions.configure("jasmine", configure)
