package com.gridnine.jasmine.gradle.plugin.tasks

import com.gridnine.spf.meta.SpfPlugin
import org.gradle.api.DefaultTask
import javax.inject.Inject

@Suppress("unused")
open class StartSuiteJsTestInBuildTask() :DefaultTask(){

    @Inject
    constructor(plugin: SpfPlugin):this(){
        group = "other"
        val suiteLauncher = plugin.parameters.find{ param -> param.id == "suite-test-launcher" }!!.value
        dependsOn(StartTestServerInBuildTask.getTaskName(plugin.id),StopTestServerInBuildTask.getTaskName(plugin.id), NodeJsCopyJsFilesInBuildTask.taskName, NodeJsStartTestInBuildTask.getTaskName(suiteLauncher, plugin.id))
    }

    companion object{
        fun getTaskName(pluginId: String) = "_suite-js-test_${pluginId}"
    }
}