package com.gridnine.jasmine.gradle.plugin


import com.gridnine.spf.meta.SpfPluginsRegistry
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.io.PrintStream
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.net.URLClassLoader

open class CompileKotlinJVMPluginTask :DefaultTask(){

    lateinit var pluginId:String

    lateinit var registry:SpfPluginsRegistry

    val dependencies = arrayListOf<String>()

    @TaskAction
    fun compile(){
        val compilerClassPath = project.configurations.getByName(KotlinUtils.COMPILER_CLASSPATH_CONFIGURATION_NAME).toList()
        val out = PrintStream(System.out)
        val classLoader = URLClassLoader(compilerClassPath.map { it.toURI().toURL() }.toTypedArray())
        val servicesClass = Class.forName("org.jetbrains.kotlin.config.Services", true, classLoader)
        val emptyServices = servicesClass.getField("EMPTY").get(servicesClass)
        val compiler = Class.forName(KotlinUtils.COMPILER_CLASS_JVM, true, classLoader)
        val exec = compiler.getMethod(
                "execAndOutputXml",
                PrintStream::class.java,
                servicesClass,
                Array<String>::class.java
        )
        val cpFiles = hashSetOf<File>()
        dependencies.forEach{
            cpFiles.addAll(project.configurations.getByName(it).toSet())
        }
        val plugin = registry.plugins.find { pluginId == it.id }?:throw IllegalArgumentException ("unable to find plugin $pluginId")
        plugin.pluginsDependencies.forEach {
            cpFiles.add(File(project.projectDir, "build/plugins/${it.pluginId}/classes"))
        }
        if(KotlinUtils.getType(plugin) == SpfPluginType.SPF){
            cpFiles.add(File(project.projectDir, "lib/spf-1.0.jar"))
        }

        val cp = cpFiles.joinToString(separator = ":"){it.absolutePath}

        val args = arrayOf("${project.projectDir.absolutePath}/plugins/$pluginId/source"
                ,"-d", "${project.projectDir.absolutePath}/build/plugins/$pluginId/classes",
                "-no-stdlib", "-no-reflect", "-jvm-target", "12","-cp",cp)

        val res = exec.invoke(compiler.getConstructor().newInstance(), out, emptyServices, args)
        val exitCode = ExitCode.valueOf(res.toString())
        if(exitCode != ExitCode.OK){
            throw Exception("error compiling $pluginId")
        }
    }

}