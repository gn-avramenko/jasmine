/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("LeakingThis")

package com.gridnine.jasmine.gradle.plugin.tasks

import com.gridnine.spf.meta.SpfPlugin
import com.gridnine.spf.meta.SpfPluginsRegistry
import org.gradle.api.DefaultTask
import javax.inject.Inject

@Suppress("unused")
open class TestJsPluginTask() :DefaultTask(){

    @Inject
    constructor(plugin:SpfPlugin):this(){
        group = "other"
        dependsOn(StopTestServerInBuildTask.getTaskName(plugin.id))
        dependsOn(StopTestServerInBuildTask.getTaskName(plugin.id))
    }

    companion object{
        fun getTaskName(pluginId:String) = "_TestJsPluginTask_$pluginId"
    }
}