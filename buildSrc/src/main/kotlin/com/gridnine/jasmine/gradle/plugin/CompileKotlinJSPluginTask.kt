package com.gridnine.jasmine.gradle.plugin


import com.gridnine.spf.meta.SpfPluginsRegistry
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.io.PrintStream
import java.net.URLClassLoader

open class CompileKotlinJSPluginTask :DefaultTask(){

    lateinit var pluginId:String

    lateinit var registry:SpfPluginsRegistry

    lateinit var outputDir:String

    @TaskAction
    fun compile(){
        val compilerClassPath = project.configurations.getByName(KotlinUtils.COMPILER_CLASSPATH_CONFIGURATION_NAME).toList()
        val out = PrintStream(System.out)
        val classLoader = URLClassLoader(compilerClassPath.map { it.toURI().toURL() }.toTypedArray())
        val servicesClass = Class.forName("org.jetbrains.kotlin.config.Services", true, classLoader)
        val emptyServices = servicesClass.getField("EMPTY").get(servicesClass)
        val compiler = Class.forName(KotlinUtils.COMPILER_CLASS_JS, true, classLoader)
        val exec = compiler.getMethod(
                "execAndOutputXml",
                PrintStream::class.java,
                servicesClass,
                Array<String>::class.java
        )
        val cpFiles = hashSetOf<File>()
        cpFiles.addAll(project.configurations.getByName("web-js").toSet().filter { it.name.contains("kotlin-stdlib-js") })

        val plugin = registry.plugins.find { pluginId == it.id }?:throw IllegalArgumentException ("unable to find plugin $pluginId")
        plugin.pluginsDependencies.forEach {pd ->
            val outputDir = registry.plugins.find { it.id == pd.pluginId }!!.parameters.find { it.id =="kotlin-output-dir" }!!.value
            cpFiles.add(File(project.projectDir, "build/plugins/${pd.pluginId}/${outputDir}"))
        }

        val argsLst = arrayListOf("${project.projectDir.absolutePath}/plugins/$pluginId/source")
        if(File("${project.projectDir.absolutePath}/plugins/$pluginId/source-gen").exists()){
            argsLst.add("${project.projectDir.absolutePath}/plugins/$pluginId/source-gen")
        }
        argsLst.addAll(arrayListOf("-output", "${project.projectDir.absolutePath}/build/plugins/$pluginId/${outputDir}/${pluginId}.js",
                "-no-stdlib", "-source-map", "-source-map-embed-sources", "always", "-meta-info", "-module-kind", "umd"))

        if(cpFiles.isNotEmpty()){
            argsLst.addAll(arrayListOf("-libraries", cpFiles.joinToString(separator = ":"){it.absolutePath}))
        }
        val res = exec.invoke(compiler.getConstructor().newInstance(), out, emptyServices, argsLst.toArray(arrayOfNulls<String>(0)))
        val exitCode = ExitCode.valueOf(res.toString())
        if(exitCode != ExitCode.OK){
            throw Exception("error compiling $pluginId")
        }
    }

}