/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
package com.gridnine.jasmine.gradle.plugin.tasks

import com.gridnine.jasmine.gradle.plugin.JasmineConfigExtension
import com.gridnine.spf.meta.SpfPluginsRegistry
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.lang.IllegalArgumentException
import javax.inject.Inject


@Suppress("unused")
open class CreateModulesTask() : DefaultTask() {

    private lateinit var registry: SpfPluginsRegistry
    private lateinit var config:JasmineConfigExtension
    private lateinit var pluginsToFileMap : Map<String,File>

    @Inject
    constructor(registry: SpfPluginsRegistry, config:JasmineConfigExtension, pluginsToFileMap:Map<String,File>):this(){
        this.registry= registry
        this.config = config
        this.group = "other"
        this.pluginsToFileMap = pluginsToFileMap
    }

    @TaskAction
    fun createArtifacts() {
        val projectName = project.name
        val rootDir = File(project.projectDir, ".idea/modules")
        if (!rootDir.exists()) {
            rootDir.mkdirs()
        }
        createPlugins()
        createProjectModule(projectName)
        createModules(projectName)
        createExternals()
    }

    private fun createExternals() {
        val sb = StringBuilder()
        registry.plugins.forEach { plugin ->
            val baseDir = pluginsToFileMap[plugin.id]?:throw IllegalArgumentException("no file mapping found for plugin ${plugin.id}")
            val pluginType = SpfPluginType.valueOf(plugin.parameters.find { par -> "type" == par.id }?.value
                    ?: throw IllegalArgumentException("plugin ${plugin.id} has no type attribute"))
            when (pluginType) {
                SpfPluginType.CORE, SpfPluginType.SERVER, SpfPluginType.SPF -> {
                    sb.append("\n${baseDir.absolutePath}/")
                    val classesDir = File(baseDir, "classes")
                    if (classesDir.exists()) {
                        sb.append("\n${classesDir.absolutePath}/")
                    }
                    val resourcesDir = File(baseDir, "resources")
                    if (resourcesDir.exists()) {
                        sb.append("\n${resourcesDir.absolutePath}/")
                    }
                }
                SpfPluginType.WEB -> {
                    val resourcesDir = File(baseDir, "resources")
                    if (resourcesDir.exists()) {
                        sb.append("\n${resourcesDir.absolutePath}/")
                    }
                }
                else -> {
                }
            }
        }
        val config = project.configurations.getByName("server")
        config.forEach{
            sb.append("\n${it.absolutePath}")
        }
        File(project.projectDir, "lib/externals.txt").writeIfDiffers(sb.substring(1))
    }

    private fun createPlugins() {
        registry.plugins.forEach { pluginDescr ->
            val baseDir = pluginsToFileMap[pluginDescr.id]?:throw IllegalArgumentException("no file mapping found for plugin ${pluginDescr.id}")
            val pluginRelativePath = baseDir.toRelativeString(project.projectDir)
            val pluginType = KotlinUtils.getType(pluginDescr)
            val content = xml("module", "version" to "4") {
                "component"("name" to "NewModuleRootManager") {
                    when (pluginType) {
                        SpfPluginType.SERVER, SpfPluginType.CORE, SpfPluginType.SERVER_TEST, SpfPluginType.SPF -> {
                            emptyTag("output", "url" to "file://\$MODULE_DIR\$/../../$pluginRelativePath/classes")
                            emptyTag("output-test", "url" to "file://\$MODULE_DIR\$/../../$pluginRelativePath/classes")
                            "content"("url" to "file://\$MODULE_DIR\$/../../$pluginRelativePath") {
                                if (File(project.projectDir, "$pluginRelativePath/source").exists()) {
                                    emptyTag("sourceFolder", "url" to "file://\$MODULE_DIR\$/../../$pluginRelativePath/source",
                                            "isTestSource" to if (pluginType == SpfPluginType.SERVER_TEST) "true" else "false")
                                }
                                if (File(project.projectDir, "$pluginRelativePath/source-gen").exists()) {
                                    emptyTag("sourceFolder", "url" to "file://\$MODULE_DIR\$/../../$pluginRelativePath/source-gen",
                                            "isTestSource" to "false", "generated" to "true")
                                }
                                if (File(project.projectDir, "$pluginRelativePath/classes").exists()) {
                                    emptyTag("excludeFolder", "url" to "file://\$MODULE_DIR\$/../../$pluginRelativePath/classes")
                                }
                            }
                        }
                        SpfPluginType.WEB,SpfPluginType.WEB_TEST -> {
                            val outputDir = "file://\$MODULE_DIR\$/../../$pluginRelativePath/${pluginDescr.parameters.find { param -> param.id == "kotlin-output-dir" }?.value}"
                            emptyTag("output", "url" to outputDir)
                            emptyTag("output-test", "url" to outputDir)
                            "content"("url" to "file://\$MODULE_DIR\$/../../$pluginRelativePath") {
                                if (File(project.projectDir, "$pluginRelativePath/source").exists()) {
                                    emptyTag("sourceFolder", "url" to "file://\$MODULE_DIR\$/../../$pluginRelativePath/source",
                                            "isTestSource" to "false", "type" to "kotlin-source")
                                }
                                if (File(project.projectDir, "$pluginRelativePath/source-gen").exists()) {
                                    emptyTag("sourceFolder", "url" to "file://\$MODULE_DIR\$/../../$pluginRelativePath/source-gen",
                                            "isTestSource" to "false", "type" to "kotlin-source", "generated" to "true")
                                }
                                emptyTag("excludeFolder", "url" to outputDir)
                            }
                        }
                    }

                    when (pluginType) {
                        SpfPluginType.SERVER, SpfPluginType.CORE -> {
                            emptyTag("orderEntry", "type" to "sourceFolder", "forTests" to "false")
                            emptyTag("orderEntry", "type" to "inheritedJdk")
                            emptyTag("orderEntry", "type" to "library", "name" to "server", "level" to "project")
                        }
                        SpfPluginType.SERVER_TEST -> {
                            emptyTag("orderEntry", "type" to "sourceFolder", "forTests" to "true")
                            emptyTag("orderEntry", "type" to "inheritedJdk")
                            emptyTag("orderEntry", "type" to "library", "name" to "server", "level" to "project")
                            emptyTag("orderEntry", "type" to "library", "name" to "server_test", "level" to "project")
                            emptyTag("orderEntry", "type" to "library", "name" to "spf", "level" to "project")
                        }
                        SpfPluginType.SPF -> {
                            emptyTag("orderEntry", "type" to "sourceFolder", "forTests" to "false")
                            emptyTag("orderEntry", "type" to "inheritedJdk")
                            emptyTag("orderEntry", "type" to "library", "name" to "server", "level" to "project")
                            emptyTag("orderEntry", "type" to "library", "name" to "spf", "level" to "project")
                        }
                        SpfPluginType.WEB -> {
                            emptyTag("orderEntry", "type" to "sourceFolder", "forTests" to "false")
                            emptyTag("orderEntry", "type" to "library", "name" to "web_js", "level" to "project")
                        }
                        SpfPluginType.WEB_TEST -> {
                            emptyTag("orderEntry", "type" to "sourceFolder", "forTests" to "true")
                            emptyTag("orderEntry", "type" to "library", "name" to "web_js", "level" to "project")
                        }
                    }
                    pluginDescr.pluginsDependencies.forEach {
                        emptyTag("orderEntry", "type" to "module", "module-name" to it.pluginId)
                    }
                }
            }
            File(project.projectDir, ".idea/modules/${pluginDescr.id}.iml").writeIfDiffers(content)
        }
    }

    private fun createProjectModule(projectName: String) {
        val content = xml("module", "version" to "4") {
            "component"("name" to "NewModuleRootManager") {
                emptyTag("exclude-output")
                emptyTag("orderEntry", "type" to "inheritedJdk")
                emptyTag("orderEntry", "type" to "library", "name" to "spf", "level" to "project")
            }
        }
        File(project.projectDir, ".idea/modules/${projectName}.iml").writeIfDiffers(content)
    }

    private fun createModules(projectName: String) {

        val content = xml("component", "name" to "ProjectModuleManager") {
            "modules"{
                emptyTag("module", "fileurl" to "file://\$PROJECT_DIR\$/.idea/modules/$projectName.iml",
                        "filepath" to "\$PROJECT_DIR\$/.idea/modules/$projectName.iml",
                        "group" to projectName)
                registry.plugins.forEach {
                    emptyTag("module", "fileurl" to "file://\$PROJECT_DIR\$/.idea/modules/${it.id}.iml",
                            "filepath" to "\$PROJECT_DIR\$/.idea/modules/${it.id}.iml",
                            "group" to projectName)
                }

            }
        }

        val file = File(project.projectDir, ".idea/modules.xml")
        if(file.exists()){
            val oldLines = file.readLines().filter { it.contains("<module fileurl") }.map { it.trim() }.toList().sorted()
            val newLines = content.lines().filter { it.contains("<module fileurl") }.map { it.trim().replace("/>"," />") }.toList().sorted()
            if(oldLines.containsAll(newLines) && newLines.size == oldLines.size){
                return
            }
            if(newLines.size == oldLines.size) {
                oldLines.withIndex().forEach { (idx, value) ->
                    if (value != newLines[idx]) {
                        println("old  line: $value")
                        println("new  line: ${newLines[idx]}")
                    }
                }
            }
        }
        file.writeIfDiffers(content)
    }

    companion object{
        const val TASK_NAME = "_CreateModulesTask"
    }

}