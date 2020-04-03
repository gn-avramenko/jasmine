/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
package com.gridnine.jasmine.gradle.plugin

import com.gridnine.spf.app.SpfBoot
import com.gridnine.spf.meta.SpfPluginsRegistry
import com.moowork.gradle.node.npm.NpmTask
import com.moowork.gradle.node.task.NodeTask
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.JavaExec
import java.io.File

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
        target.configurations.maybeCreate("web-js")
        target.dependencies.add("web-js","org.jetbrains.kotlin:kotlin-stdlib-js:${KotlinUtils.KOTLIN_PLUGIN_VERSION}")
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
            task.dependsOn("_compileJVMPlugin-${registry.plugins.find { KotlinUtils.getType(it) == SpfPluginType.CORE }?.id?:throw IllegalStateException("CORE plugin is absent")}")
            task.dependsOn("_compileJVMPlugin-${registry.plugins.find { KotlinUtils.getType(it)  == SpfPluginType.SPF }?.id?:throw IllegalStateException("SPF plugin is absent")}")
        }

        val buildDepends = arrayListOf<String>()
        registry.plugins.forEach { spfPlugin ->
            when (val pluginType = KotlinUtils.getType(spfPlugin)) {
                SpfPluginType.CORE, SpfPluginType.SERVER, SpfPluginType.SERVER_TEST, SpfPluginType.SPF -> {
                    target.tasks.create("_compileJVMPlugin-${spfPlugin.id}", CompileKotlinJVMPluginTask::class.java) { task ->
                        task.group = "other"
                        task.registry = registry
                        task.pluginId = spfPlugin.id
                        task.dependencies.add("server")
                        if (pluginType == SpfPluginType.SERVER_TEST) {
                            task.dependencies.add("server_test")
                        }
                        spfPlugin.pluginsDependencies.forEach { dep ->
                            task.dependsOn.add("_compileJVMPlugin-${dep.pluginId}")
                            if (pluginType != SpfPluginType.CORE && pluginType != SpfPluginType.SPF) {
                                task.dependsOn.add("codeGen")
                            }
                        }
                        val sourceDir = File(target.projectDir, "plugins/${spfPlugin.id}/source")
                        if (sourceDir.exists()) {
                            task.inputs.dir(sourceDir)
                        }
                        val sourceGenDir = File(target.projectDir, "plugins/${spfPlugin.id}/source-gen")
                        if (sourceGenDir.exists()) {
                            task.inputs.dir(sourceGenDir)
                        }
                        task.outputs.dir(File(target.projectDir, "build/plugins/${spfPlugin.id}/classes"))
                    }
                    buildDepends.add("_compileJVMPlugin-${spfPlugin.id}")
                    val resourcesDir = File(target.projectDir, "plugins/${spfPlugin.id}/resources")
                    if(resourcesDir.exists()){
                        target.tasks.create("_copyResourcesPlugin-${spfPlugin.id}", Copy::class.java) { task ->
                            task.from(resourcesDir)
                            task.into("build/plugins/${spfPlugin.id}/resources")
                        }
                        buildDepends.add("_copyResourcesPlugin-${spfPlugin.id}")
                    }

//                    target.tasks.create("_buildJarPlugin-${spfPlugin.id}", Jar::class.java) { task ->
//                        task.dependsOn("_compileJVMPlugin-${spfPlugin.id}")
//                        task.destinationDirectory.set(File("build/lib"))
//                        task.from("build/plugins/${spfPlugin.id}/classes")
//                        if(File("plugins/${spfPlugin.id}/resources").exists()){
//                            task.from("plugins/${spfPlugin.id}/resources")
//                        }
//                        task.archiveFileName.set("${spfPlugin.id}.jar")
//                    }
//                    buildDepends.add("_buildJarPlugin-${spfPlugin.id}")

                }
                SpfPluginType.WEB, SpfPluginType.WEB_TEST -> {
                    target.tasks.create("_compileJSPlugin-${spfPlugin.id}", CompileKotlinJSPluginTask::class.java) { task ->
                        task.group = "other"
                        task.registry = registry
                        task.pluginId = spfPlugin.id
                        spfPlugin.pluginsDependencies.forEach { dep ->
                            task.dependsOn.add("_compileJSPlugin-${dep.pluginId}")
                            //task.dependsOn.add("codeGen")
                        }
                        val sourceDir = File(target.projectDir, "plugins/${spfPlugin.id}/source")
                        if (sourceDir.exists()) {
                            task.inputs.dir(sourceDir)
                        }
                        val sourceGenDir = File(target.projectDir, "plugins/${spfPlugin.id}/source-gen")
                        if (sourceGenDir.exists()) {
                            task.inputs.dir(sourceGenDir)
                        }
                        task.outputs.dir(File(target.projectDir, "build/plugins/${spfPlugin.id}/classes"))
                    }
                    buildDepends.add("_compileJSPlugin-${spfPlugin.id}")
                }
            }
        }
        target.tasks.create("build", DefaultTask::class.java) { task ->
            task.group = "build"
            task.dependsOn.addAll(buildDepends)
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
                        kotlinDir.listFiles()?.forEach {file ->
                            if(file.isFile()) {
                                task.from(file)
                            }
                        }
                        File(kotlinDir, "lib").listFiles()?.forEach{file ->
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
            val launcherClassName = it.parameters.find { param -> param.id == "server-launcher-class"}?.value
            if(launcherClassName?.isNotBlank() == true) {
                    target.tasks.create("_${it.id}-jsTestStartServer", StartServerTask::class.java) { task ->
                    task.setJvmArgs(arrayListOf("-Dspf.mode=shell", "-Dspf.applicationClass=$launcherClassName"))
                    task.main = "com.gridnine.spf.app.SpfBoot"
                    task.classpath = createClassPath(target)
                    task.doLast{
                        while (!SpfBoot.isApplicationRunning()){
                            Thread.sleep(1000L)
                        }
                    }
                }

                target.tasks.create("_${it.id}-jsTest", NodeTask::class.java) { task ->
                    task.setIgnoreExitValue(true)
                    task.dependsOn("_installMocha", "_installReporter", "_populateNode","_${it.id}-jsTestStartServer")
                    task.group = "other"
                    task.script = File(target.projectDir, "node_modules/mocha/bin/mocha")
                    task.setArgs(arrayListOf("--timeout", "10000", "--reporter", "mocha-jenkins-reporter", "--reporter-option", "junit_report_name=Tests,junit_report_path=build/junit-reports/${it.id}-junit.xml,junit_report_stack=1", "build/node_modules/${it.id}-launcher.js"))
                }
                target.tasks.create("_${it.id}-jsTestStopServer", JavaExec::class.java) { task ->
                    task.dependsOn("_${it.id}-jsTestStartServer","_${it.id}-jsTest")
                    task.setJvmArgs(arrayListOf("-Dspf.mode=stop", "-Dspf.applicationClass=$launcherClassName"))
                    task.main = "com.gridnine.spf.app.SpfBoot"
                    task.classpath = createClassPath(target)
                    task.doLast{
                        println("application stopped")
                    }
                }
               testDepends.add("_${it.id}-jsTestStartServer")
               testDepends.add("_${it.id}-jsTestStopServer")
            }
        }
        target.tasks.create("jsTests") { task ->
            task.group = "idea"
            task.dependsOn("_installMocha", "_installReporter","_populateNode")
            task.dependsOn.addAll(testDepends)
        }
    }

    private fun createClassPath(target:Project): FileCollection {
        val fileNames = arrayListOf(target.file("lib/spf-1.0.jar").absolutePath)
        fileNames.addAll(target.file("lib/externals-test.txt").readLines())
        return target.files(fileNames.toArray(emptyArray<String>()))
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

