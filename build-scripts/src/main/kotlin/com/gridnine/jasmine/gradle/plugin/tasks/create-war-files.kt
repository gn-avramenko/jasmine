/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.gradle.plugin.tasks

import com.gridnine.jasmine.gradle.plugin.CompileKotlinJSPluginTask
import com.gridnine.spf.meta.SpfPlugin
import org.gradle.api.Project
import org.gradle.jvm.tasks.Jar
import java.io.File

object CreateWarTasksFactory{
    fun createTasks(plugin:SpfPlugin,  filesMap:Map<String, File>, project:Project){
        val pluginType = KotlinUtils.getType(plugin)
        if(pluginType != SpfPluginType.SERVER_TEST && pluginType != SpfPluginType.WEB_TEST) {
            val kotlinOutputDir = plugin.parameters.find { it.id == "kotlin-output-dir" }?.value?.substringAfterLast("/")
            if (kotlinOutputDir != null) {
                project.tasks.create(getTaskName(kotlinOutputDir), Jar::class.java) { task ->
                    task.doFirst{
                        if (KotlinUtils.getType(plugin) == SpfPluginType.WEB_CORE) {
                            File(filesMap[plugin.id]!!.parentFile.parentFile, "lib/js").listFiles()?.forEach {file ->
                                file.copyTo(task.project.file("build/tmp/lib-js/lib/${file.name}"))
                            }
                        }
                    }
                    task.dependsOn(CompileKotlinJSPluginTask.getTaskName(plugin.id))
                    task.destinationDirectory.set(File("build/dist/lib"))
                    task.from("build/plugins/${plugin.id}/output")

                    if (KotlinUtils.getType(plugin) == SpfPluginType.WEB_CORE) {
                        task.from(task.project.file("build/tmp/lib-js"))
                    }
                    task.include("**/*.js","**/*.xml", "**/*.map", "**/*.css", "**/*.png", "**/*.gif")
                    task.includeEmptyDirs = false
                    task.archiveFileName.set("${kotlinOutputDir}.war")
                }
            }
            File(filesMap[plugin.id], "resources").listFiles()?.forEach {
                if (it.name != kotlinOutputDir) {
                    project.tasks.create(getTaskName(it.name), Jar::class.java) { task ->
                        task.destinationDirectory.set(File("build/dist/lib"))
                        task.from(it.absolutePath)
                        task.include("**/*.js", "**/*.xml","**/*.html", "**/*.map", "**/*.css", "**/*.png", "**/*.gif")
                        task.includeEmptyDirs = false
                        task.archiveFileName.set("${it.name}.war")
                        task.mustRunAfter(CleanupTask.TASK_NAME)
                    }
                }
            }
        }
    }
    fun getTasksNames(plugin:SpfPlugin, filesMap:Map<String, File>):Array<String>{
        val result = linkedSetOf<String>()
        val pluginType = KotlinUtils.getType(plugin)
        if(pluginType != SpfPluginType.SERVER_TEST && pluginType != SpfPluginType.WEB_TEST) {
            val kotlinOutputDir = plugin.parameters.find { it.id == "kotlin-output-dir" }?.value?.substringAfterLast("/")
            kotlinOutputDir?.let { result.add(kotlinOutputDir) }
            File(filesMap[plugin.id], "resources").listFiles()?.forEach {
                if (it.isDirectory) {
                    result.add(it.name)
                }
            }
        }
        return result.map { getTaskName(it) }.toTypedArray()
    }
    private fun getTaskName(directoryName:String) = "_createWar_${directoryName}"
}
