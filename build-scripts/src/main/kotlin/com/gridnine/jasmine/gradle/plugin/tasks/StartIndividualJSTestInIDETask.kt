package com.gridnine.jasmine.gradle.plugin.tasks

import com.gridnine.spf.meta.SpfPlugin
import org.gradle.api.DefaultTask
import javax.inject.Inject

@Suppress("ConvertSecondaryConstructorToPrimary", "unused", "LeakingThis")
open class StartIndividualJSTestInIDETask() :DefaultTask(){

    @Inject
    constructor(plugin: SpfPlugin, debug:Boolean):this(){
        group = "individual-js-tests"
        val individualLauncher = plugin.parameters.find{ param -> param.id == "individual-test-launcher" }!!.value
        //dependsOn(StartTestServerInIDETask.getTaskName(plugin.id),StopTestServerInIDETask.getTaskName(plugin.id), NodeJsCopyJsFilesTask.taskName, NodeJsStartTestInIDETask.getTaskName(individualLauncher, plugin.id, debug))
        dependsOn(NodeJsCopyJsFilesTask.taskName, NodeJsStartTestInIDETask.getTaskName(individualLauncher, plugin.id, debug))
    }

    companion object{
        fun getTaskName(pluginId: String, debug:Boolean) = "individual-js-test_${if(debug) "debug-" else ""}${pluginId}"
    }
}