/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("LeakingThis")

package com.gridnine.jasmine.gradle.plugin.tasks

import com.gridnine.jasmine.gradle.plugin.CompileKotlinJSPluginTask
import com.gridnine.spf.meta.SpfPlugin
import com.gridnine.spf.meta.SpfPluginsRegistry
import org.gradle.api.DefaultTask
import org.gradle.jvm.tasks.Jar
import java.io.File
import javax.inject.Inject

@Suppress("unused")
open class CreateJarForJvmPluginTask() :Jar(){

    @Inject
    constructor(plugin: SpfPlugin):this(){
        group = "other"
        dependsOn(CompileKotlinJVMPluginTask.getTaskName(plugin.id))
        destinationDirectory.set(project.file("build/dist/lib"))
        from(project.file("build/plugins/${plugin.id}/classes"))
        archiveFileName.set("${plugin.id}.jar")
    }

    companion object{
        fun getTaskName(pluginId:String) = "_CreateJarForJvmPluginTask_${pluginId}"
    }
}