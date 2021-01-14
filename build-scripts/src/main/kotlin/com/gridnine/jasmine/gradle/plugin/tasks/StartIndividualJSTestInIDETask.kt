package com.gridnine.jasmine.gradle.plugin.tasks

import com.gridnine.jasmine.gradle.plugin.JasmineConfigExtension
import com.gridnine.spf.meta.SpfPlugin
import com.gridnine.spf.meta.SpfPluginsRegistry
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import javax.inject.Inject

@Suppress("unused")
open class StartIndividualJSTestInIDETask() :DefaultTask(){

    @Inject
    constructor(plugin: SpfPlugin, debug:Boolean):this(){
        group = "individual-js-tests"
        val individualLauncher = plugin.parameters.find{ param -> param.id == "individual-test-launcher" }!!.value
        dependsOn(StartTestServerInIDETask.getTaskName(plugin.id),StopTestServerInIDETask.getTaskName(plugin.id), NodeJsCopyJsFilesTask.taskName, NodeJsStartTestInIDETask.getTaskName(individualLauncher, plugin.id, debug))
    }

    companion object{
        fun getTaskName(pluginId: String, debug:Boolean) = "${if(debug) "debug-" else ""}${pluginId}-Individual-js-test"
    }
}