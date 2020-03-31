/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
package com.gridnine.jasmine.gradle.plugin

import com.gridnine.spf.meta.SpfPluginsRegistry
import com.moowork.gradle.node.npm.NpmTask
import com.moowork.gradle.node.task.NodeTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import java.io.File
import java.lang.IllegalStateException

class JasminePlugin : Plugin<Project> {


    override fun apply(target: Project) {
        val pluginsURLs = arrayListOf<java.net.URL>()
        File(target.projectDir, "plugins").listFiles()?.forEach {
            pluginsURLs.add(File(it, "plugin.xml").toURI().toURL())
        }

        val registry = SpfPluginsRegistry()
        registry.initRegistry(pluginsURLs)
        createConfiguration("server", registry, target, SpfPluginType.CORE, SpfPluginType.SERVER)
        createConfiguration("server_test", registry, target, SpfPluginType.SERVER_TEST)
        target.configurations.maybeCreate(KotlinUtils.COMPILER_CLASSPATH_CONFIGURATION_NAME).defaultDependencies {
            it.add(target.dependencies.create("${KotlinUtils.KOTLIN_MODULE_GROUP}:${KotlinUtils.KOTLIN_COMPILER_EMBEDDABLE}:${KotlinUtils.KOTLIN_PLUGIN_VERSION}"))
        }
        target.repositories.add(target.repositories.mavenCentral())
        target.tasks.create("_createLibraries", CreateLibrariesTask::class.java)
        target.tasks.create("_createArtifacts", CreateArtifactsTask::class.java)
        target.tasks.create("_createModules", CreateModulesTask::class.java).registry = registry
        target.tasks.create("_createTemplates", CreateTemplatesTask::class.java)
        target.tasks.create("makeProject") {
            it.group = "idea"
            it.dependsOn("_createLibraries", "_createArtifacts", "_createModules", "_createTemplates")
        }
        target.tasks.create("codeGen", CodeGenPluginTask::class.java) { task ->
            task.group = "idea"
            task.dependsOn("_buildJVMPlugin-${registry.plugins.find { KotlinUtils.getType(it) == SpfPluginType.CORE }?.id?:throw IllegalStateException("CORE plugin is absent")}")
            task.dependsOn("_buildJVMPlugin-${registry.plugins.find { KotlinUtils.getType(it)  == SpfPluginType.SPF }?.id?:throw IllegalStateException("SPF plugin is absent")}")
        }

        registry.plugins.forEach { spfPlugin ->
            when (val pluginType = KotlinUtils.getType(spfPlugin)) {
                SpfPluginType.CORE, SpfPluginType.SERVER, SpfPluginType.SERVER_TEST, SpfPluginType.SPF -> {
                    target.tasks.create("_buildJVMPlugin-${spfPlugin.id}", CompileKotlinJVMPluginTask::class.java) { task ->
                        task.group = "other"
                        task.registry = registry
                        task.pluginId = spfPlugin.id
                        task.dependencies.add("server")
                        if (pluginType == SpfPluginType.SERVER_TEST) {
                            task.dependencies.add("server_test")
                        }
                        spfPlugin.pluginsDependencies.forEach { dep ->
                            task.dependsOn.add("_buildJVMPlugin-${dep.pluginId}")
                            if (pluginType != SpfPluginType.CORE && pluginType != SpfPluginType.SPF) {
                                task.dependsOn.add("codeGen")
                            }
                        }
                        val sourceDir = File(target.projectDir, "plugins/${spfPlugin.id}/source")
                        if(sourceDir.exists()){
                            task.inputs.dir(sourceDir)
                        }
                        task.outputs.dir(File(target.projectDir, "build/plugins/${spfPlugin.id}/classes"))
                    }
                }
            }
        }
        target.extensions.configure("node"){it:com.moowork.gradle.node.NodeExtension ->
           it.download = true
        }
        target.tasks.create("_installMocha", NpmTask::class.java) {
            it.group = "other"
            it.setArgs(arrayListOf("install", "mocha"))
        }
        target.tasks.create("_installReporter", NpmTask::class.java) {
            it.group = "other"
            it.setArgs(arrayListOf("install", "mocha-jenkins-reporter"))
        }

        target.tasks.create("_populateNode", Copy::class.java) { task ->
            task.group = "other"
            registry.plugins.forEach { spfPlugin ->
                when (val pluginType = KotlinUtils.getType(spfPlugin)) {
                    SpfPluginType.WEB,SpfPluginType.WEB_TEST -> {
                        val kotlinDir = File("plugins/${spfPlugin.id}/${spfPlugin.parameters.find { it.id =="kotlin-output-dir" }!!.value}")
                        kotlinDir.listFiles().forEach {file ->
                            if(file.isFile()) {
                                task.from(file)
                            }
                        }
                        File(kotlinDir, "lib").listFiles().forEach{file ->
                            if(file.isFile()) {
                                task.from(file)
                            }
                        }
                        if(pluginType == SpfPluginType.WEB_TEST){
                            task.from(File("plugins/${spfPlugin.id}/resources/js/${spfPlugin.id}-launcher.js"))
                        }
                    }
                }
            }
            task.into("build/node_modules")
        }

        val testDepends = arrayListOf<String>()
        registry.plugins.filter { it -> KotlinUtils.getType(it) == SpfPluginType.WEB_TEST}.forEach{
            target.tasks.create("_${it.id}-jsTest", NodeTask::class.java) { task ->
                task.setIgnoreExitValue(true)
                task.dependsOn("_installMocha", "_installReporter","_populateNode")
                task.group = "other"
                task.script = File(target.projectDir, "node_modules/mocha/bin/mocha")
                task.setArgs(arrayListOf("--timeout","10000","--reporter", "mocha-jenkins-reporter", "--reporter-option", "junit_report_name=Tests,junit_report_path=build/junit-reports/${it.id}-junit.xml,junit_report_stack=1","build/node_modules/${it.id}-launcher.js"))
           }
            testDepends.add("_${it.id}-jsTest")
        }
        target.tasks.create("jsTests") { task ->
            task.group = "idea"
            task.dependsOn("_installMocha", "_installReporter","_populateNode")
            task.dependsOn.addAll(testDepends)
        }
    }

    private fun createConfiguration(configurationName: String, registry: SpfPluginsRegistry, project: Project, vararg types: SpfPluginType) {
        project.configurations.create(configurationName)
        val depts = arrayListOf<String>()
        registry.plugins.filter { p -> types.contains(KotlinUtils.getType(p)) }.forEach { plugin ->
            plugin.libsDependencies.forEach {
                depts.add("${it.group}:${it.name}:${it.version}")
            }
        }

        depts.forEach {
            project.dependencies.add(configurationName, it)
        }
    }
}

