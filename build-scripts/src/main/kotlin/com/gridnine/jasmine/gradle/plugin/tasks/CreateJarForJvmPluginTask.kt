/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("LeakingThis")

package com.gridnine.jasmine.gradle.plugin.tasks

import com.gridnine.spf.meta.SpfPlugin
import org.gradle.jvm.tasks.Jar
import java.io.File
import javax.inject.Inject

@Suppress("unused")
open class CreateJarForJvmPluginTask() :Jar(){

    @Inject
    constructor(plugin: SpfPlugin, filesMap:Map<String, File>):this(){
        group = "other"
        dependsOn(CompileKotlinJVMPluginTask.getTaskName(plugin.id),CopyJvmResourcesTask.getTaskName(plugin.id))
        destinationDirectory.set(project.file("build/dist/lib"))
        from(project.file("build/plugins/${plugin.id}/classes"))
        from(File(filesMap[plugin.id], "plugin.xml"))

        archiveFileName.set("${plugin.id}.jar")
    }

    companion object{
        fun getTaskName(pluginId:String) = "_CreateJarForJvmPluginTask_${pluginId}"
    }
}