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
    var kotlinVersion = "1.3.71"
    val pluginsFiles = arrayListOf<File>()
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
