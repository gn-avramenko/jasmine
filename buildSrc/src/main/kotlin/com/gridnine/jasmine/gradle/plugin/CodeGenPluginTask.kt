package com.gridnine.jasmine.gradle.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.net.URLClassLoader

open class CodeGenPluginTask :DefaultTask(){

    @TaskAction
    fun codegen(){
        val urls = project.configurations.getByName("server").map { it.toURI().toURL() }.toMutableList()
        urls.add(File(project.projectDir, "build/plugins/com.gridnine.jasmine.server.core/classes").toURI().toURL())
        urls.add(File(project.projectDir, "build/plugins/com.gridnine.jasmine.server.spf/classes").toURI().toURL())
        urls.add(File(project.projectDir, "lib/spf-1.0.jar").toURI().toURL())
        val classLoader = URLClassLoader(urls.toTypedArray())
        val codeGen = Class.forName("com.gridnine.jasmine.server.spf.SpfCodeGen", true, classLoader)
        val method = codeGen.getMethod("generateCode")
        method.invoke(codeGen.getConstructor(File::class.java, String::class.java).newInstance(project.projectDir, project.name))
    }
}