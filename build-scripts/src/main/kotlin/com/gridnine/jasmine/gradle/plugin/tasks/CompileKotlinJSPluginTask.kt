package com.gridnine.jasmine.gradle.plugin


import com.gridnine.jasmine.gradle.plugin.tasks.CleanupTask
import com.gridnine.jasmine.gradle.plugin.tasks.CodeGenPluginTask
import com.gridnine.jasmine.gradle.plugin.tasks.ExitCode
import com.gridnine.jasmine.gradle.plugin.tasks.KotlinUtils
import com.gridnine.spf.meta.SpfPlugin
import com.gridnine.spf.meta.SpfPluginsRegistry
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.io.PrintStream
import java.net.URLClassLoader
import javax.inject.Inject

open class CompileKotlinJSPluginTask():DefaultTask(){

    private lateinit var plugin: SpfPlugin

    private lateinit var registry:SpfPluginsRegistry
    private lateinit var config:JasmineConfigExtension
    private lateinit var filesMap:Map<String,File>
    private lateinit var outputDir:File

    @Inject
    constructor(plugin: SpfPlugin, registry: SpfPluginsRegistry, config:JasmineConfigExtension, filesMap:Map<String,File>):this() {
        this.plugin = plugin
        this.registry = registry
        this.config = config
        this.filesMap = filesMap

        plugin.pluginsDependencies.forEach { dep ->
            dependsOn(getTaskName(dep.pluginId))
        }
        dependsOn(CodeGenPluginTask.TASK_NAME)
        val sourceDir = File(filesMap[plugin.id], "/source")
        if (sourceDir.exists()) {
            inputs.dir(sourceDir)
        }
        val sourceGenDir = File(filesMap[plugin.id], "/source-gen")
        if (sourceGenDir.exists()) {
            inputs.dir(sourceGenDir)
        }
        outputDir = project.file("build/plugins/${plugin.id}/output")
        outputs.dir(outputDir)
        mustRunAfter(CleanupTask.TASK_NAME)
    }

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
        cpFiles.addAll(project.configurations.getByName(KotlinUtils.WEB_CONFIGURATION_NAME).toSet().filter { it.name.contains("kotlin-stdlib-js") })

        plugin.pluginsDependencies.forEach {pd ->
            cpFiles.add(File(project.projectDir, "build/plugins/${pd.pluginId}/output"))
        }

        val sourceDir = File(filesMap[plugin.id], "/source")
        val argsLst = arrayListOf<String>()
        if (sourceDir.exists()) {
            argsLst.add(sourceDir.absolutePath)
        }
        val sourceGenDir = File(filesMap[plugin.id], "/source-gen")
        if (sourceGenDir.exists()) {
            argsLst.add(sourceGenDir.absolutePath)
        }
        argsLst.addAll(arrayListOf("-output", "${project.projectDir.absolutePath}/build/plugins/${plugin.id}/output/${plugin.id}.js",
                "-no-stdlib", "-source-map", "-source-map-embed-sources", "always", "-meta-info", "-module-kind", "umd"))

        if(cpFiles.isNotEmpty()){
            argsLst.addAll(arrayListOf("-libraries", cpFiles.joinToString(separator = ":"){it.absolutePath}))
        }
        val res = exec.invoke(compiler.getConstructor().newInstance(), out, emptyServices, argsLst.toArray(arrayOfNulls<String>(0)))
        val exitCode = ExitCode.valueOf(res.toString())
        if(exitCode != ExitCode.OK){
            throw Exception("error compiling ${plugin.id}")
        }
    }

    companion object{
        fun getTaskName(pluginId:String)="_compileJsPlugin_$pluginId"
    }
}