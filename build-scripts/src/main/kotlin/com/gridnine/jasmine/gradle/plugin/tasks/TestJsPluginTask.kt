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
    constructor(plugin:SpfPlugin, registry: SpfPluginsRegistry):this(){
        group = "other"
        val suiteLauncher = plugin.parameters.find{ param -> param.id == "test-suite-launcher" }!!.value
        dependsOn(StartTestServerInBuildTask.getTaskName(plugin.id),StopTestServerInBuildTask.getTaskName(plugin.id), NodeJsCopyJsFilesInBuildTask.taskName, NodeJsStartTestInBuildTask.getTaskName(suiteLauncher, plugin.id))
        KotlinUtils.getDependentPlugins(plugin, registry).forEach {
            if(KotlinUtils.getType(it) == SpfPluginType.WEB_TEST){
                if(it.parameters.find{ param -> param.id == "test-suite-launcher" } != null){
                    dependsOn(getTaskName(it.id))
                }
            }
        }
    }

    companion object{
        fun getTaskName(pluginId:String) = "_TestJsPluginTask_$pluginId"
    }
}