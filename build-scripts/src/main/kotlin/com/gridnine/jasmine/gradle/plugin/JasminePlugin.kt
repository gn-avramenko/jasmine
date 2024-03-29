/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
package com.gridnine.jasmine.gradle.plugin

import com.github.gradle.node.NodeExtension
import com.gridnine.jasmine.gradle.plugin.tasks.*
import com.gridnine.spf.meta.SpfPluginsRegistry
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.attributes.AttributeDisambiguationRule
import org.gradle.api.attributes.MultipleCandidatesDetails
import java.io.File
import java.lang.IllegalArgumentException
import java.net.URL

@Suppress("unused")
class JasminePlugin: Plugin<Project>{
    override fun apply(target: Project) {
        println("""java version is ${System.getProperty("java.version")}""")
        val extension = target.extensions.getByName("jasmine") as JasmineConfigExtension
        val pluginsURLs = arrayListOf<URL>()
        val pluginsToFileMap = hashMapOf<String,File>()
        extension.pluginsFiles.forEach{
            pluginsToFileMap[it.name] = it
            pluginsURLs.add(File(it, "plugin.xml").toURI().toURL())
        }
        val registry = SpfPluginsRegistry()
        registry.initRegistry(pluginsURLs)
        KotlinUtils.createConfiguration(KotlinUtils.SERVER_CONFIGURATION_NAME, registry, target, pluginsToFileMap, SpfPluginType.COMMON_CORE, SpfPluginType.COMMON, SpfPluginType.SERVER_CORE, SpfPluginType.SERVER)
        KotlinUtils.createConfiguration(KotlinUtils.COMMON_CONFIGURATION_NAME, registry, target, pluginsToFileMap, SpfPluginType.COMMON_CORE, SpfPluginType.COMMON)
        KotlinUtils.createConfiguration(KotlinUtils.COMMON_TEST_CONFIGURATION_NAME, registry, target,pluginsToFileMap, SpfPluginType.COMMON_TEST)
        KotlinUtils.createConfiguration(KotlinUtils.SERVER_TEST_CONFIGURATION_NAME, registry, target,pluginsToFileMap, SpfPluginType.COMMON_TEST, SpfPluginType.SERVER_TEST)
        target.configurations.maybeCreate(KotlinUtils.COMPILER_CLASSPATH_CONFIGURATION_NAME).defaultDependencies {
            it.add(target.dependencies.create("${KotlinUtils.KOTLIN_MODULE_GROUP}:${KotlinUtils.KOTLIN_COMPILER_EMBEDDABLE}:${extension.kotlinVersion}"))
        }
        if(extension.enableWebTasks) {
            target.extensions.configure("node") { it: NodeExtension ->
                it.download.set(true)
            }
            KotlinUtils.createConfiguration(KotlinUtils.WEB_CONFIGURATION_NAME, registry, target, pluginsToFileMap, SpfPluginType.WEB, SpfPluginType.WEB_CORE)
            val corePluginId = registry.plugins.find { KotlinUtils.getType(it) == SpfPluginType.WEB_CORE }!!.id
            File(pluginsToFileMap[corePluginId], "lib").listFiles().forEach { file ->
                target.dependencies.add(KotlinUtils.WEB_CONFIGURATION_NAME, target.files(file.absolutePath))
            }
//            target.dependencies.add(KotlinUtils.WEB_CONFIGURATION_NAME, "org.jetbrains.kotlin:kotlin-stdlib-js:${extension.kotlinVersion}")
//            target.dependencies.add(KotlinUtils.WEB_CONFIGURATION_NAME, "org.jetbrains.kotlinx:kotlinx-coroutines-core-js:${extension.kotlinCoroutinesJSVersion}")
//            val jsCompilerAttr = Attribute.of("org.jetbrains.kotlin.js.compiler", String::class.java)
//            val jsUsageAttr = Attribute.of("org.gradle.usage", String::class.java)
//
//            target.dependencies.attributesSchema.attribute(jsCompilerAttr) {
//                it.disambiguationRules.add(KotlinJsCompilerDisambiguationRule::class.java)
//            }
//            target.dependencies.attributesSchema.attribute(jsUsageAttr) {
//                it.disambiguationRules.add(KotlinJsUsageDisambiguationRule::class.java)
//            }
        }

        target.tasks.create(CreateArtifactsTask.TASK_NAME, CreateArtifactsTask::class.java, extension)
        target.tasks.create(CreateLibrariesTask.TASK_NAME, CreateLibrariesTask::class.java, extension)
        target.tasks.create(CreateModulesTask.TASK_NAME, CreateModulesTask::class.java,registry, extension, pluginsToFileMap)
        target.tasks.create(MakeProjectTask.TASK_NAME, MakeProjectTask::class.java)
//        target.tasks.create(NodeJsInstallWebpackTask.taskName, NodeJsInstallWebpackTask::class.java)
//        target.tasks.create(NodeJsInstallWebpackCliTask.taskName, NodeJsInstallWebpackCliTask::class.java)
//        target.tasks.create(NodeJsRunWebpackTask.taskName, NodeJsRunWebpackTask::class.java)
//        target.tasks.create(NodeJsInstallUglifyTask.taskName, NodeJsInstallUglifyTask::class.java)
//        target.tasks.create(NodeJsRunUglifyTask.taskName, NodeJsRunUglifyTask::class.java)
        registry.plugins.forEach { plugin ->
            CreateWarTasksFactory.createTasks(plugin, pluginsToFileMap, target)
            when(val pluginType = KotlinUtils.getType(plugin)){
                SpfPluginType.COMMON_CORE, SpfPluginType.SERVER_CORE,SpfPluginType.COMMON,SpfPluginType.COMMON_TEST,SpfPluginType.SERVER_TEST,SpfPluginType.SERVER,SpfPluginType.SPF ->{
                    target.tasks.create(CopyJvmResourcesTask.getTaskName(plugin.id), CopyJvmResourcesTask::class.java, plugin, pluginsToFileMap)
                    target.tasks.create(CompileKotlinJVMPluginTask.getTaskName(plugin.id), CompileKotlinJVMPluginTask::class.java, plugin, registry,extension, pluginsToFileMap)
                    if(pluginType != SpfPluginType.SERVER_TEST && pluginType != SpfPluginType.COMMON_TEST){
                        target.tasks.create(CreateJarForJvmPluginTask.getTaskName(plugin.id), CreateJarForJvmPluginTask::class.java, plugin, pluginsToFileMap)
                    } else{
                        target.tasks.create(TestJvmPluginTask.getTaskName(plugin.id), TestJvmPluginTask::class.java, plugin, registry, extension)
                    }
                }
                SpfPluginType.WEB,SpfPluginType.WEB_CORE ->{
                    target.tasks.create(CompileKotlinJSPluginTask.getTaskName(plugin.id), CompileKotlinJSPluginTask::class.java, plugin, registry,extension, pluginsToFileMap)
                }
                SpfPluginType.WEB_TEST ->{
                    target.tasks.create(CompileKotlinJSPluginTask.getTaskName(plugin.id), CompileKotlinJSPluginTask::class.java, plugin, registry,extension, pluginsToFileMap)
                    val individualLauncher = plugin.parameters.find{ param -> param.id == "individual-test-launcher" }?.value
                    if(individualLauncher != null){
                            target.tasks.create(StartTestServerInIDETask.getTaskName(plugin.id), StartTestServerInIDETask::class.java,  plugin)
                            target.tasks.create(StopTestServerInIDETask.getTaskName(plugin.id), StopTestServerInIDETask::class.java, plugin)
                            target.tasks.create(NodeJsStartTestInIDETask.getTaskName(individualLauncher, plugin.id, false), NodeJsStartTestInIDETask::class.java, individualLauncher, plugin.id, false)
                            target.tasks.create(NodeJsStartTestInIDETask.getTaskName(individualLauncher, plugin.id, true), NodeJsStartTestInIDETask::class.java, individualLauncher, plugin.id, true)
                            target.tasks.create(StartIndividualJSTestInIDETask.getTaskName(plugin.id, false), StartIndividualJSTestInIDETask::class.java, plugin, false)
                            target.tasks.create(StartIndividualJSTestInIDETask.getTaskName(plugin.id, true), StartIndividualJSTestInIDETask::class.java, plugin, true)
                    }
                    val suiteLauncher = plugin.parameters.find{ param -> param.id == "test-suite-launcher" }?.value
                    if(suiteLauncher != null){
                        target.tasks.create(StartTestServerInBuildTask.getTaskName(plugin.id), StartTestServerInBuildTask::class.java, registry, plugin, extension,pluginsToFileMap)
                        target.tasks.create(StopTestServerInBuildTask.getTaskName(plugin.id), StopTestServerInBuildTask::class.java, registry, plugin, extension,pluginsToFileMap)
                        target.tasks.create(TestJsPluginTask.getTaskName(plugin.id), TestJsPluginTask::class.java,  plugin,registry)
                        target.tasks.create(NodeJsStartTestInBuildTask.getTaskName(suiteLauncher, plugin.id), NodeJsStartTestInBuildTask::class.java,  suiteLauncher, plugin.id)

                    }
                }
                else ->throw IllegalArgumentException("unsupported plugin type $pluginType" )
            }
        }
        target.tasks.create(CodeGenPluginTask.TASK_NAME, CodeGenPluginTask::class.java,registry, extension, pluginsToFileMap)
        target.tasks.create(CompileProjectTask.TASK_NAME, CompileProjectTask::class.java, registry)
        if(extension.enableWebTasks) {
            target.tasks.create(NodeJsInstallMochaTask.taskName, NodeJsInstallMochaTask::class.java)
            target.tasks.create(NodeJsInstalReporterTask.taskName, NodeJsInstalReporterTask::class.java)
            target.tasks.create(NodeJsInstallXmlHttpRequestTask.taskName, NodeJsInstallXmlHttpRequestTask::class.java)
            target.tasks.create(SetupNodeTask.taskName, SetupNodeTask::class.java)
            target.tasks.create(NodeJsCopyJsFilesTask.taskName, NodeJsCopyJsFilesTask::class.java, registry, pluginsToFileMap)
        }
        target.tasks.create(MakeDistTask.TASK_NAME, MakeDistTask::class.java, registry, extension, pluginsToFileMap)
        target.tasks.create(CleanupTask.TASK_NAME, CleanupTask::class.java)
        if(extension.enableWebTasks) {
            target.tasks.create(NodeJsCopyJsFilesInBuildTask.taskName, NodeJsCopyJsFilesInBuildTask::class.java, registry, pluginsToFileMap)
        }
        target.tasks.create(TestProjectTask.TASK_NAME, TestProjectTask::class.java, registry)
    }

    private class KotlinJsCompilerDisambiguationRule : AttributeDisambiguationRule<String> {
        override fun execute(details: MultipleCandidatesDetails<String>) {
            details.closestMatch("legacy")
        }
    }

    private class KotlinJsUsageDisambiguationRule : AttributeDisambiguationRule<String> {
        override fun execute(details: MultipleCandidatesDetails<String>) {
            details.closestMatch("kotlin-api")
        }
    }

}