/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("LeakingThis")

package com.gridnine.jasmine.gradle.plugin.tasks

import com.gridnine.spf.meta.SpfPlugin
import org.gradle.jvm.tasks.Jar
import javax.inject.Inject

@Suppress("unused")
open class CreateWarForJsPluginTask() :Jar(){

    @Inject
    constructor(plugin: SpfPlugin):this(){
        group = "other"
        dependsOn(CompileKotlinJSPluginTask.getTaskName(plugin.id))
        destinationDirectory.set(project.file("build/dist/lib"))
        from(project.file("build/plugins/${plugin.id}"))
        archiveFileName.set("${plugin.id}.war")
    }

    companion object{
        fun getTaskName(pluginId:String) = "_CreateWarForJsPluginTask_${pluginId}"
    }
}