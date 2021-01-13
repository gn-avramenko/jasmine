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
    constructor(plugin: SpfPlugin):this(){
        group = "individual-js-tests"
        dependsOn(StartTestServerInIDETask.getTaskName(plugin.id),StopTestServerInIDETask.getTaskName(plugin.id))
    }

    companion object{
        fun getTaskName(pluginId: String) = "${pluginId}-Individual-js-test"
    }
}