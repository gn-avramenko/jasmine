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
import org.gradle.api.tasks.testing.Test
import org.gradle.jvm.tasks.Jar
import java.io.File

class JasminePlugin : Plugin<Project> {


    override fun apply(target: Project) {
        val pluginsURLs = arrayListOf<java.net.URL>()
        File(target.projectDir, "plugins").listFiles()?.forEach {
            pluginsURLs.add(File(it, "plugin.xml").toURI().toURL())
        }
        target.extensions.create("development", ServerDescriptionExtension::class.java)
        val registry = SpfPluginsRegistry()
        registry.initRegistry(pluginsURLs)
        createConfiguration("server", registry, target, SpfPluginType.CORE, SpfPluginType.SERVER)
        createConfiguration("server_test", registry, target, SpfPluginType.SERVER_TEST)
        target.configurations.maybeCreate(KotlinUtils.COMPILER_CLASSPATH_CONFIGURATION_NAME).defaultDependencies {
            it.add(target.dependencies.create("${KotlinUtils.KOTLIN_MODULE_GROUP}:${KotlinUtils.KOTLIN_COMPILER_EMBEDDABLE}:${KotlinUtils.KOTLIN_PLUGIN_VERSION}"))
        }
        target.configurations.maybeCreate("web-js")
        target.dependencies.add("web-js", "org.jetbrains.kotlin:kotlin-stdlib-js:${KotlinUtils.KOTLIN_PLUGIN_VERSION}")
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
            task.dependsOn("_compileJVMPlugin-${registry.plugins.find { KotlinUtils.getType(it) == SpfPluginType.CORE }?.id
                    ?: throw IllegalStateException("CORE plugin is absent")}")
            task.dependsOn("_compileJVMPlugin-${registry.plugins.find { KotlinUtils.getType(it) == SpfPluginType.SPF }?.id
                    ?: throw IllegalStateException("SPF plugin is absent")}")
        }

        val buildDepends = arrayListOf<String>()
        val externals = arrayListOf<String>()
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
                    target.tasks.create("_copyResourcesJVMPlugin-${spfPlugin.id}", Copy::class.java) { task ->
                        task.dependsOn("_compileJVMPlugin-${spfPlugin.id}")
                        task.from(File(target.projectDir, "plugins/${spfPlugin.id}/source"))
                        task.include("**/*.xml", "**/*.properties")
                        task.into(File(target.projectDir, "build/plugins/${spfPlugin.id}/classes"))
                    }
                    target.tasks.create("_copyXmlPlugin-${spfPlugin.id}", Copy::class.java) { task ->
                        task.dependsOn("_compileJVMPlugin-${spfPlugin.id}")
                        task.from(File(target.projectDir, "plugins/${spfPlugin.id}"))
                        task.include("*.xml")
                        task.into(File(target.projectDir, "build/plugins/${spfPlugin.id}/classes"))
                    }
                    buildDepends.add("_copyResourcesJVMPlugin-${spfPlugin.id}")
                    buildDepends.add("_copyXmlPlugin-${spfPlugin.id}")
                    externals.add(File(target.projectDir, "build/plugins/${spfPlugin.id}/classes").absolutePath)
                    val resourcesDir = File(target.projectDir, "plugins/${spfPlugin.id}/resources")
                    if (resourcesDir.exists()) {
                        target.tasks.create("_copyResourcesPlugin-${spfPlugin.id}", Copy::class.java) { task ->
                            task.from(resourcesDir)
                            task.into("build/plugins/${spfPlugin.id}/resources")
                        }
                        externals.add(File(target.projectDir, "build/plugins/${spfPlugin.id}/resources").absolutePath)
                        buildDepends.add("_copyResourcesPlugin-${spfPlugin.id}")
                    }


                }
                SpfPluginType.WEB, SpfPluginType.WEB_TEST -> {
                    target.tasks.create("_compileJSPlugin-${spfPlugin.id}", CompileKotlinJSPluginTask::class.java) { task ->
                        task.group = "other"
                        task.registry = registry
                        task.pluginId = spfPlugin.id
                        task.outputDir = spfPlugin.parameters.find { it.id == "kotlin-output-dir" }!!.value
                        task.dependsOn.add("codeGen")
                        spfPlugin.pluginsDependencies.forEach { dep ->
                            task.dependsOn.add("_compileJSPlugin-${dep.pluginId}")
                        }
                        val sourceDir = File(target.projectDir, "plugins/${spfPlugin.id}/source")
                        if (sourceDir.exists()) {
                            task.inputs.dir(sourceDir)
                        }
                        val sourceGenDir = File(target.projectDir, "plugins/${spfPlugin.id}/source-gen")
                        if (sourceGenDir.exists()) {
                            task.inputs.dir(sourceGenDir)
                        }
                        task.outputs.dir(File(target.projectDir, "build/plugins/${spfPlugin.id}/${task.outputDir}"))
                    }
                    val resourcesDir = File(target.projectDir, "plugins/${spfPlugin.id}/resources")
                    if (resourcesDir.exists()) {
                        val outputDir = spfPlugin.parameters.find { it.id == "kotlin-output-dir" }?.value
                        target.tasks.create("_copyResourcesJSPlugin-${spfPlugin.id}", Copy::class.java) { task ->
                            task.from(resourcesDir)
                            task.dependsOn("_compileJSPlugin-${spfPlugin.id}")
                            task.into("build/plugins/${spfPlugin.id}/resources")
                            if (outputDir != null) {
                                task.exclude("**/${outputDir}/**")
                            }
                        }
                        buildDepends.add("_copyResourcesJSPlugin-${spfPlugin.id}")
                    }
                    buildDepends.add("_compileJSPlugin-${spfPlugin.id}")
                }
            }
        }
        val config = target.configurations.getByName("server")
        config.forEach {
            if (!externals.contains(it.absolutePath)) {
                externals.add(it.absolutePath)
            }
        }
        val testConfig = target.configurations.getByName("server_test")
        testConfig.forEach {
            if (!externals.contains(it.absolutePath)) {
                externals.add(it.absolutePath)
            }
        }
        val file = File(target.projectDir, "build/lib/externals.txt");
        if (!file.parentFile.exists()) file.parentFile.mkdirs()
        file.writeText(externals.joinToString("\r\n"))

        target.tasks.create("_makeLib", Copy::class.java) { task ->
            task.group = "other"
            task.from("lib/spf-1.0.jar")
            task.into("build/lib")
            buildDepends.add("_makeLib")
        }
        target.tasks.create("_copyKotlinLib", Copy::class.java) { task ->
            task.group = "other"
            task.from("lib/js")
            val plugin = registry.plugins.find { it.id == "com.gridnine.jasmine.web.core" }!!
            task.into("build/plugins/${plugin.id}/${plugin.parameters.find { it.id == "kotlin-output-dir" }!!.value}/lib")
            buildDepends.add("_copyKotlinLib")
        }
        target.tasks.create("build", DefaultTask::class.java) { task ->
            task.group = "build"
            task.dependsOn.addAll(buildDepends)
        }


        target.extensions.configure("node") { it: com.moowork.gradle.node.NodeExtension ->
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
        target.tasks.create("_installXMLHttpRequest", NpmTask::class.java) {
            it.group = "other"
            it.setArgs(arrayListOf("install", "xmlhttprequest"))
        }

        target.tasks.create("_populateNode", Copy::class.java) { task ->
            task.group = "other"
            registry.plugins.forEach { spfPlugin ->
                when (val pluginType = KotlinUtils.getType(spfPlugin)) {
                    SpfPluginType.WEB, SpfPluginType.WEB_TEST -> {
                        val kotlinDir = File("plugins/${spfPlugin.id}/${spfPlugin.parameters.find { it.id == "kotlin-output-dir" }!!.value}")
                        kotlinDir.listFiles()?.forEach { file ->
                            if (file.isFile()) {
                                task.from(file)
                            }
                        }
                        File(kotlinDir, "lib").listFiles()?.forEach { file ->
                            if (file.isFile()) {
                                task.from(file)
                            }
                        }
                        if (pluginType == SpfPluginType.WEB_TEST) {
                            val suiteLauncher = spfPlugin.parameters.find { param -> param.id == "test-suite-launcher" }?.value
                            val individualLauncher = spfPlugin.parameters.find { param -> param.id == "individual-test-launcher" }?.value
                            if (suiteLauncher?.isNotBlank() == true) {
                                task.from(File("plugins/${spfPlugin.id}/resources/js/${suiteLauncher}"))
                            }
                            if (individualLauncher?.isNotBlank() == true) {
                                task.from(File("plugins/${spfPlugin.id}/resources/js/${individualLauncher}"))
                            }
                        }
                    }
                }
            }
            task.from("plugins/com.gridnine.jasmine.web.core.test/resources/js/core-test-initializer.js")
            task.into("build/node_modules")
        }

        registry.plugins.filter { it -> KotlinUtils.getType(it) == SpfPluginType.WEB_TEST }.forEach {
            val launcherClassName = it.parameters.find { param -> param.id == "server-launcher-class" }?.value
            val suiteLauncher = it.parameters.find { param -> param.id == "test-suite-launcher" }?.value
            val individualLauncher = it.parameters.find { param -> param.id == "individual-test-launcher" }?.value

            if (launcherClassName?.isNotBlank() == true) {
                target.tasks.create("_${it.id}-jsTestStartServer", StartServerTask::class.java) { task ->
                    task.setJvmArgs(arrayListOf("-Dspf.mode=shell", "-Dspf.applicationClass=$launcherClassName"))
                    task.main = "com.gridnine.spf.app.SpfBoot"
                    task.classpath = createClassPath(target)
                    task.doLast {
                        while (!SpfBoot.isApplicationRunning()) {
                            Thread.sleep(1000L)
                        }
                    }
                }
                target.tasks.create("_${it.id}-jsTestStopServer", JavaExec::class.java) { task ->
                    task.dependsOn("_${it.id}-jsTestStartServer")
                    if (suiteLauncher?.isNotBlank() == true) {
                        task.mustRunAfter("_${it.id}-jsSuiteTest")
                    }
                    if (individualLauncher?.isNotBlank() == true) {
                        task.mustRunAfter("_${it.id}-jsIndividualTest")
                        task.mustRunAfter("_${it.id}-jsIndividualTestDebug")
                    }
                    task.setJvmArgs(arrayListOf("-Dspf.mode=stop", "-Dspf.applicationClass=$launcherClassName"))
                    task.main = "com.gridnine.spf.app.SpfBoot"
                    task.classpath = createClassPath(target)
                }

            }
            if (suiteLauncher?.isNotBlank() == true) {
                target.tasks.create("_${it.id}-jsSuiteTest", NodeTask::class.java) { task ->
                    task.setIgnoreExitValue(true)
                    if (launcherClassName?.isNotBlank() == true) {
                        task.dependsOn("_${it.id}-jsTestStartServer")
                    }
                    task.group = "other"
                    task.script = File(target.projectDir, "node_modules/mocha/bin/mocha")
                    task.setArgs(arrayListOf("--timeout", "10000", "--reporter", "mocha-jenkins-reporter", "--reporter-option", "junit_report_name=Tests,junit_report_path=build/junit-reports/${it.id}-junit.xml,junit_report_stack=1", "build/node_modules/$suiteLauncher"))
                }
                target.tasks.create("${it.id}-jsSuiteTest", DefaultTask::class.java) { task ->
                    task.group = "js-tests"
                    task.dependsOn("_${it.id}-jsSuiteTest")
                    if (launcherClassName?.isNotBlank() == true) {
                        task.dependsOn("_${it.id}-jsTestStopServer")
                    }
                }
            }
            if (individualLauncher?.isNotBlank() == true) {
                target.tasks.create("_${it.id}-jsIndividualTest", NodeTask::class.java) { task ->
                    task.setIgnoreExitValue(true)
                    task.dependsOn("_populateNode")
                    if (launcherClassName?.isNotBlank() == true) {
                        task.dependsOn("_${it.id}-jsTestStartServer")
                    }
                    task.group = "other"
                    task.script = File(target.projectDir, "node_modules/mocha/bin/mocha")
                    task.setArgs(arrayListOf("--timeout", "10000", "build/node_modules/$individualLauncher"))
                }
                target.tasks.create("_${it.id}-jsIndividualTestDebug", NodeTask::class.java) { task ->
                    task.setIgnoreExitValue(true)
                    task.dependsOn("_populateNode")
                    if (launcherClassName?.isNotBlank() == true) {
                        task.dependsOn("_${it.id}-jsTestStartServer")
                    }
                    task.group = "other"
                    task.script = File(target.projectDir, "node_modules/mocha/bin/mocha")
                    task.setArgs(arrayListOf("--inspect-brk", "--timeout", "10000", "build/node_modules/$individualLauncher"))
                }
                target.tasks.create("${it.id}-jsIndividualTest", DefaultTask::class.java) { task ->
                    task.group = "js-tests"
                    task.dependsOn("_${it.id}-jsIndividualTest")
                    if (launcherClassName?.isNotBlank() == true) {
                        task.dependsOn("_${it.id}-jsTestStopServer")
                    }
                }
                target.tasks.create("${it.id}-jsIndividualTestDebug", DefaultTask::class.java) { task ->
                    task.group = "js-tests"
                    task.dependsOn("_${it.id}-jsIndividualTestDebug")
                    if (launcherClassName?.isNotBlank() == true) {
                        task.dependsOn("_${it.id}-jsTestStopServer")
                    }
                }
            }
        }
        target.tasks.create("setupNode") { task ->
            task.group = "idea"
            task.dependsOn("_installMocha", "_installReporter", "_installXMLHttpRequest", "_populateNode")
        }


        val testJvmDepends = arrayListOf<String>()
        registry.plugins.forEach {
            when (KotlinUtils.getType(it)) {
                SpfPluginType.SERVER_TEST -> {
                    target.tasks.create("_testJVMPlugin-${it.id}", Test::class.java) { task ->
                        task.useJUnit()
                        task.testClassesDirs = target.files("build/plugins/${it.id}/classes")
                        val fileNames = arrayListOf(target.file("build/lib/spf-1.0.jar").absolutePath)
                        fileNames.addAll(target.file("build/lib/externals.txt").readLines())
                        task.classpath = target.files(fileNames.toArray(emptyArray<String>()))
                        task.binResultsDir = File("build/test-results")
                        task.reports { rp ->
                            rp.html.isEnabled = false
                            rp.junitXml.destination = target.file("build/junit-reports/${it.id}")
                        }
                        task.mustRunAfter("build")
                        testJvmDepends.add("_testJVMPlugin-${it.id}")
                    }
                }
                else -> {
                }

            }
        }
        target.tasks.create("_buildPopulateNode", Copy::class.java) { task ->
            task.group = "other"
            task.mustRunAfter("build")
            registry.plugins.forEach { spfPlugin ->
                when (val pluginType = KotlinUtils.getType(spfPlugin)) {
                    SpfPluginType.WEB, SpfPluginType.WEB_TEST -> {
                        val kotlinDir = File(target.projectDir, "build/plugins/${spfPlugin.id}/${spfPlugin.parameters.find { it.id == "kotlin-output-dir" }!!.value}")
                        task.from(kotlinDir)
                        if (pluginType == SpfPluginType.WEB_TEST) {
                            val suiteLauncher = spfPlugin.parameters.find { param -> param.id == "test-suite-launcher" }?.value
                            task.from(File(target.projectDir, "plugins/${spfPlugin.id}/resources/js/${suiteLauncher}"))
                        }
                    }
                }
            }
            task.include("**/*.js", "**/*.map")
            task.from(target.file("lib/js/kotlin.js"))
            task.from("plugins/com.gridnine.jasmine.web.core.test/resources/js/core-test-initializer.js")
            task.into("build/node_modules")
        }
        val jsTestsDepends = arrayListOf<String>()
        registry.plugins.filter { it -> KotlinUtils.getType(it) == SpfPluginType.WEB_TEST }.forEach {
            val launcherClassName = it.parameters.find { param -> param.id == "server-launcher-class" }?.value
            val suiteLauncher = it.parameters.find { param -> param.id == "test-suite-launcher" }?.value
            if (suiteLauncher?.isNotBlank() == true) {
                if (launcherClassName?.isNotBlank() == true) {
                    target.tasks.create("_${it.id}-jsBuildTestStartServer", StartServerTask::class.java) { task ->
                        task.workingDir("build")
                        task.setJvmArgs(arrayListOf("-Dspf.mode=shell", "-Dspf.applicationClass=$launcherClassName"))
                        task.main = "com.gridnine.spf.app.SpfBoot"
                        task.classpath = createBuildClassPath(target)
                        task.doLast {
                            var count = 0;
                            while (!SpfBoot.isApplicationRunning()) {
                                Thread.sleep(1000L)
                                count++;
                                if (count > 1000) {
                                    throw Exception("app not started")
                                }
                            }

                        }
                    }
                    target.tasks.create("_${it.id}-jsBuildTestStopServer", JavaExec::class.java) { task ->
                        task.dependsOn("_${it.id}-jsBuildTestStartServer")
                        if (suiteLauncher?.isNotBlank() == true) {
                            task.mustRunAfter("_${it.id}-jsBuildSuiteTest")
                        }
                        task.workingDir("build")
                        task.setJvmArgs(arrayListOf("-Dspf.mode=stop", "-Dspf.applicationClass=$launcherClassName"))
                        task.main = "com.gridnine.spf.app.SpfBoot"
                        task.classpath = createBuildClassPath(target)
                    }

                }
                target.tasks.create("_${it.id}-jsBuildSuiteTest", NodeTask::class.java) { task ->
                    task.setIgnoreExitValue(true)
                    if (launcherClassName?.isNotBlank() == true) {
                        task.dependsOn("_${it.id}-jsBuildTestStartServer")
                    }
                    task.group = "other"
                    task.script = File(target.projectDir, "node_modules/mocha/bin/mocha")
                    task.setArgs(arrayListOf("--timeout", "10000", "--reporter", "mocha-jenkins-reporter", "--reporter-option", "junit_report_name=Tests,junit_report_path=build/junit-reports/${it.id}-junit.xml,junit_report_stack=1", "build/node_modules/$suiteLauncher"))
                }
                target.tasks.create("_${it.id}-jsBuildFullSuiteTest", DefaultTask::class.java) { task ->
                    task.group = "other"
                    task.mustRunAfter("build")
                    task.dependsOn("_${it.id}-jsBuildSuiteTest")
                    if (launcherClassName?.isNotBlank() == true) {
                        task.dependsOn("_${it.id}-jsBuildTestStopServer")
                    }
                }
                jsTestsDepends.add("_${it.id}-jsBuildFullSuiteTest")
            }
        }
        target.tasks.create("_buildSetupNode") { task ->
            task.group = "other"
            task.dependsOn("_installMocha", "_installReporter", "_installXMLHttpRequest", "_buildPopulateNode")
        }
        target.tasks.create("test") { task ->
            task.group = "build"
            task.dependsOn("build", "_buildPopulateNode", "_buildSetupNode")
            task.dependsOn(testJvmDepends.toArray(emptyArray<String>()))
            task.dependsOn(jsTestsDepends.toArray(emptyArray<String>()))
        }

        val distDepends = arrayListOf<String>()

        registry.plugins.forEach { spfPlugin ->
            when (val pluginType = KotlinUtils.getType(spfPlugin)) {
                SpfPluginType.CORE, SpfPluginType.SERVER, SpfPluginType.WEB, SpfPluginType.SPF -> {
                    if (pluginType != SpfPluginType.WEB) {
                        target.tasks.create("_buildClassesJarPlugin-${spfPlugin.id}", Jar::class.java) { task ->
                            task.mustRunAfter("build")
                            task.destinationDirectory.set(File("build/dist/lib"))
                            task.from("build/plugins/${spfPlugin.id}/classes")
                            task.archiveFileName.set("${spfPlugin.id}.jar")
                        }
                        distDepends.add("_buildClassesJarPlugin-${spfPlugin.id}")
                    }
                    val resourceFile = target.file("plugins/${spfPlugin.id}/resources");
                    val kotlinDir = spfPlugin.parameters.find { it.id == "kotlin-output-dir" }?.value?.substringAfterLast("/")
                    var addKotlinDir = kotlinDir != null && resourceFile.listFiles()?.find { it.name == kotlinDir } == null
                    val warDirs = resourceFile.listFiles()?.filter { it.isDirectory }?.map { it.name }?.toMutableList()
                            ?: arrayListOf<String>()
                    if (addKotlinDir) warDirs.add(kotlinDir!!)
                    warDirs.forEach {
                        target.tasks.create("_buildWar-$it", Jar::class.java) { task ->
                            task.mustRunAfter("build")
                            task.destinationDirectory.set(File("build/dist/lib"))
                            task.from("build/plugins/${spfPlugin.id}/resources/$it")
                            if (pluginType == SpfPluginType.WEB) {
                                task.include("**/*.js", "**/*.map","**/*.css","**/*.png","**/*.gif")
                            }
                            task.includeEmptyDirs = false
                            task.archiveFileName.set("${it}.war")
                        }
                        distDepends.add("_buildWar-$it")
                    }
                }
            }
        }
        target.tasks.create("_buildCopySpfLib", Copy::class.java) { task ->
            task.mustRunAfter("build")
            task.from("lib")
            task.into("build/dist/lib")
            task.include("spf-1.0.jar")
        }
        distDepends.add("_buildCopySpfLib")
        target.tasks.create("_buildCopyServerLibs", Copy::class.java) { task ->
            task.mustRunAfter("build")
            val config = target.configurations.getByName("server").forEach {
                task.from(it)
            }
            task.includeEmptyDirs = false
            task.into("build/dist/lib")
        }
        distDepends.add("_buildCopyServerLibs")
        target.tasks.create("_createLaunchScripts", DefaultTask::class.java) { task ->
            task.mustRunAfter("build")
            task.doLast {
                val file = target.file("build/dist/bin/run.sh")
                if (!file.parentFile.exists()) {
                    file.parentFile.mkdirs()
                }
                file.writeText("""
                    cd ..
                    java -Xms128M -Xmx256M -Dspf.mode=shell -Dlogback.configurationFile=config/logback.xml -Dspf.applicationClass=com.gridnine.jasmine.server.spf.SpfApplicationImpl -jar lib/spf-1.0.jar > logs/init.log 2>&1 &
                """.trimIndent())
                file.setExecutable(true)
                val file2 = target.file("build/dist/bin/stop.sh")
                file2.writeText("""
                    cd ..
                    java -Xms128M -Xmx256M -Dspf.mode=stop -Dspf.applicationClass=com.gridnine.jasmine.server.spf.SpfApplicationImpl -jar lib/spf-1.0.jar
                """.trimIndent())
                file2.setExecutable(true)
            }
        }
        distDepends.add("_createLaunchScripts")
        target.tasks.create("_createLogs", DefaultTask::class.java) { task ->
            task.group = "other"
            task.doLast{
                val file = target.file("build/dist/logs")
                if(!file.exists()){
                    file.mkdirs()
                }
            }
        }
        distDepends.add("_createLogs")
        target.tasks.create("_createConfig", Copy::class.java) { task ->
            task.group = "other"
            task.from("config")
            task.into("build/dist/config")
        }
        distDepends.add("_createConfig")
        target.tasks.create("dist") { task ->
            task.group = "build"
            task.mustRunAfter("build")
            task.dependsOn(distDepends.toArray(emptyArray<String>()))
            task.doLast{
                println("address is " + (target.extensions.getByName("development") as ServerDescriptionExtension).spfUpdaterAddress)
            }
        }
        target.tasks.create("deployDev", DeployApplicationTask::class.java){ task ->
            task.group = "build"
            task.host = "localhost"
            task.port = 21567;
        }
    }

    private fun createBuildClassPath(target: Project): FileCollection {
        val fileNames = arrayListOf(target.file("build/lib/spf-1.0.jar").absolutePath)
        return target.files(fileNames.toArray(emptyArray<String>()))
    }

    private fun createClassPath(target: Project): FileCollection {
        val fileNames = arrayListOf(target.file("lib/spf-1.0.jar").absolutePath)
        fileNames.addAll(target.file("lib/externals.txt").readLines())
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

fun org.gradle.api.Project.`development`(configure: ServerDescriptionExtension.() -> Unit): Unit =
        (this as org.gradle.api.plugins.ExtensionAware).extensions.configure("development", configure)

