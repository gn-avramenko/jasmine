/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.gradle.plugin.tasks

import com.gridnine.spf.meta.SpfPluginsRegistry
import com.moowork.gradle.node.npm.NpmTask
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.lang.IllegalArgumentException
import javax.inject.Inject

open class NodeJsCopyJsFilesInBuildTask :DefaultTask{

    private var registry:SpfPluginsRegistry

    private var pluginsToFileMap : Map<String,File>

    @Inject
    constructor(registry: SpfPluginsRegistry, pluginsToFileMap:Map<String,File>){
        this.registry = registry
        this.pluginsToFileMap = pluginsToFileMap
        dependsOn(SetupNodeTask.taskName)
        dependsOn(CompileProjectTask.TASK_NAME)
        group="other"
    }

    @TaskAction
    fun copyJsFiles() {
        val files = hashSetOf<File>()
        registry.plugins.forEach { spfPlugin ->
            when (val pluginType = KotlinUtils.getType(spfPlugin)) {
                SpfPluginType.WEB, SpfPluginType.WEB_CORE, SpfPluginType.WEB_TEST -> {
                    val kotlinDir = project.file("build/plugins/${spfPlugin.id}/output")
                    kotlinDir.listFiles()?.forEach { file ->
                        if (file.isFile && file.name.endsWith(".js")) {
                            files.add(file)
                        }
                    }
                    if (pluginType == SpfPluginType.WEB_TEST) {
                        val suiteLauncher = spfPlugin.parameters.find { param -> param.id == "test-suite-launcher" }?.value
                        if (suiteLauncher?.isNotBlank() == true) {
                            files.add(File(pluginsToFileMap[spfPlugin.id], "resources/js/${suiteLauncher}"))
                        }
                    }
                }
            }
        }
        val coreTestPluginId = "com.gridnine.jasmine.web.core.test"
        val baseDir = pluginsToFileMap[coreTestPluginId]?:throw IllegalArgumentException("no file mapping found for plugin ${coreTestPluginId}")
        files.add(File(baseDir, "resources/js/core-test-initializer.js"))
        files.add(File(baseDir.parentFile.parentFile, "lib/js/kotlin.js"))
        files.forEach {
            val actualContent = it.readBytes()
            val targetFile = File("node_modules/${it.name}")
            if(targetFile.exists()){
                val existingContent = targetFile.readBytes()
                if(actualContent.contentEquals(existingContent)){
                    return@forEach
                }
            }
            targetFile.writeBytes(actualContent)
        }
    }
    companion object{
        const val taskName = "_NodeJsCopyJsFilesInBuildTask"
    }
}