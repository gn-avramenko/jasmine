/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
package com.gridnine.jasmine.gradle.plugin

import com.gridnine.jasmine.gradle.plugin.tasks.*
import com.gridnine.spf.meta.SpfPluginsRegistry
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File
import java.lang.IllegalArgumentException
import java.net.URL

@Suppress("unused")
class JasminePlugin: Plugin<Project>{
    override fun apply(target: Project) {
        val extension = target.extensions.getByName("jasmine") as JasmineConfigExtension
        val pluginsURLs = arrayListOf<URL>()
        val pluginsToFileMap = hashMapOf<String,File>()
        extension.pluginsFiles.forEach{
            pluginsToFileMap[it.name] = it
            pluginsURLs.add(File(it, "plugin.xml").toURI().toURL())
        }
        val registry = SpfPluginsRegistry()
        registry.initRegistry(pluginsURLs)
        KotlinUtils.createConfiguration(KotlinUtils.SERVER_CONFIGURATION_NAME, registry, target, SpfPluginType.CORE, SpfPluginType.SERVER)
        KotlinUtils.createConfiguration(KotlinUtils.SERVER_TEST_CONFIGURATION_NAME, registry, target, SpfPluginType.SERVER_TEST)
        target.configurations.maybeCreate(KotlinUtils.COMPILER_CLASSPATH_CONFIGURATION_NAME).defaultDependencies {
            it.add(target.dependencies.create("${KotlinUtils.KOTLIN_MODULE_GROUP}:${KotlinUtils.KOTLIN_COMPILER_EMBEDDABLE}:${extension.kotlinVersion}"))
        }
        target.extensions.configure("node") { it: com.moowork.gradle.node.NodeExtension ->
            it.download = true
        }

        target.tasks.create(CreateArtifactsTask.TASK_NAME, CreateArtifactsTask::class.java, extension)
        target.tasks.create(CreateLibrariesTask.TASK_NAME, CreateLibrariesTask::class.java, extension)
        target.tasks.create(CreateModulesTask.TASK_NAME, CreateModulesTask::class.java,registry, extension, pluginsToFileMap)
        target.tasks.create(MakeProjectTask.TASK_NAME, MakeProjectTask::class.java)
        registry.plugins.forEach { plugin ->
            when(val pluginType = KotlinUtils.getType(plugin)){
                SpfPluginType.CORE,SpfPluginType.SERVER_TEST,SpfPluginType.SERVER,SpfPluginType.SPF ->{
                    target.tasks.create(CompileKotlinJVMPluginTask.getTaskName(plugin.id), CompileKotlinJVMPluginTask::class.java, plugin, registry,extension, pluginsToFileMap)
                }
                SpfPluginType.WEB ->{
                    //noops
                }
                SpfPluginType.WEB_TEST ->{
                    if(plugin.parameters.any{ param -> param.id == "individual-test-launcher" }) {
                        target.tasks.create(StartTestServerInIDETask.getTaskName(plugin.id), StartTestServerInIDETask::class.java, registry, plugin)
                        target.tasks.create(StopTestServerInIDETask.getTaskName(plugin.id), StopTestServerInIDETask::class.java, registry, plugin)
                        target.tasks.create(StartIndividualJSTestInIDETask.getTaskName(plugin.id), StartIndividualJSTestInIDETask::class.java, plugin)
                    }
                }
                else ->throw IllegalArgumentException("unsupported plugin type $pluginType" )
            }
        }
        target.tasks.create(CodeGenPluginTask.TASK_NAME, CodeGenPluginTask::class.java,registry, extension, pluginsToFileMap)
        target.tasks.create(BuildTask.TASK_NAME, BuildTask::class.java, registry)
        target.tasks.create(NodeJsInstallMochaTask.taskName, NodeJsInstallMochaTask::class.java)
        target.tasks.create(NodeJsInstalReporterTask.taskName, NodeJsInstalReporterTask::class.java)
        target.tasks.create(NodeJsInstallXmlHttpRequestTask.taskName, NodeJsInstallXmlHttpRequestTask::class.java)
        target.tasks.create(SetupNodeTask.taskName, SetupNodeTask::class.java)

    }

}