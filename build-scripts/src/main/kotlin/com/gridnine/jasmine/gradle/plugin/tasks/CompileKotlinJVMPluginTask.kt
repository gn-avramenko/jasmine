package com.gridnine.jasmine.gradle.plugin.tasks


import com.gridnine.jasmine.gradle.plugin.JasmineConfigExtension
import com.gridnine.spf.meta.SpfPlugin
import com.gridnine.spf.meta.SpfPluginsRegistry
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.io.PrintStream
import java.lang.IllegalArgumentException
import java.net.URLClassLoader
import javax.inject.Inject

@Suppress("unused", "LeakingThis")
open class CompileKotlinJVMPluginTask() :DefaultTask(){

    private lateinit var plugin: SpfPlugin

    private lateinit var registry:SpfPluginsRegistry
    private lateinit var config:JasmineConfigExtension
    private lateinit var filesMap:Map<String,File>


    @Inject
    constructor(plugin: SpfPlugin,registry: SpfPluginsRegistry,config:JasmineConfigExtension, filesMap:Map<String,File>):this(){
        this.plugin = plugin
        this.registry = registry
        this.config = config
        this.filesMap = filesMap
        val sourceDir = File(filesMap[plugin.id], "/source")
        if (sourceDir.exists()) {
            inputs.dir(sourceDir)
        }
        val sourceGenDir = File(filesMap[plugin.id], "/source-gen")
        if (sourceGenDir.exists()) {
            inputs.dir(sourceGenDir)
        }
        outputs.dir(File(project.projectDir, "build/plugins/${plugin.id}/classes"))
        group = "other"
        plugin.pluginsDependencies.forEach {dep ->
            dependsOn(getTaskName(dep.pluginId))
        }
        if(KotlinUtils.getType(plugin) != SpfPluginType.CORE && KotlinUtils.getType(plugin) != SpfPluginType.SPF){
            dependsOn(CodeGenPluginTask.TASK_NAME)
        }
        mustRunAfter(CleanupTask.TASK_NAME)
    }

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
        val spfLib = File(project.projectDir, "${config.libRelativePath}/spf-1.0.jar")
        when(val pluginType = KotlinUtils.getType(plugin)){
            SpfPluginType.SERVER,SpfPluginType.CORE ->{
                cpFiles.addAll(project.configurations.getByName(KotlinUtils.SERVER_CONFIGURATION_NAME).toSet())
            }
            SpfPluginType.SPF ->{
                cpFiles.addAll(project.configurations.getByName(KotlinUtils.SERVER_CONFIGURATION_NAME).toSet())
                cpFiles.add(spfLib)
            }
            SpfPluginType.SERVER_TEST ->{
                cpFiles.addAll(project.configurations.getByName(KotlinUtils.SERVER_CONFIGURATION_NAME).toSet())
                cpFiles.addAll(project.configurations.getByName(KotlinUtils.SERVER_TEST_CONFIGURATION_NAME).toSet())
                cpFiles.add(spfLib)
            }
            else -> throw IllegalArgumentException("unsupported plugin type $pluginType")
        }
        plugin.pluginsDependencies.forEach {
            cpFiles.add(File(project.projectDir, "build/plugins/${it.pluginId}/classes"))
        }
        val cp = cpFiles.joinToString(separator = ":"){it.absolutePath}

        val argsLst = arrayListOf("${filesMap[plugin.id]}/source")
        if(File("${filesMap[plugin.id]}/source-gen").exists()){
            argsLst.add("${filesMap[plugin.id]}/source-gen")
        }
        argsLst.addAll(arrayListOf("-d", "${project.projectDir.absolutePath}/build/plugins/${plugin.id}/classes",
                "-no-stdlib", "-no-reflect", "-jvm-target", "1.8","-cp",cp))

        val res = exec.invoke(compiler.getConstructor().newInstance(), out, emptyServices, argsLst.toArray(arrayOfNulls<String>(0)))
        val exitCode = ExitCode.valueOf(res.toString())
        if(exitCode != ExitCode.OK){
            throw Exception("error compiling ${plugin.id}")
        }
    }

    companion object{
        fun getTaskName(pluginId:String)="_compileJvmPlugin_$pluginId"
    }

}