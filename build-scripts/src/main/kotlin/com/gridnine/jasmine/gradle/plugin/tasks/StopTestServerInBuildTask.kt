/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.gradle.plugin.tasks

import com.gridnine.jasmine.gradle.plugin.BaseStartServerTask
import com.gridnine.jasmine.gradle.plugin.JasmineConfigExtension
import com.gridnine.spf.app.SpfBoot
import com.gridnine.spf.meta.SpfPlugin
import com.gridnine.spf.meta.SpfPluginsRegistry
import java.io.File
import javax.inject.Inject

open class StopTestServerInBuildTask() : BaseStartServerTask() {

    @Inject
    constructor(registry: SpfPluginsRegistry, plugin: SpfPlugin, config:JasmineConfigExtension, filesMap: Map<String, File>):this(){
        dependsOn(StartTestServerInBuildTask.getTaskName(plugin.id))
        val launcherClassName = plugin.parameters.find { param -> param.id == "server-launcher-class" }!!.value
        jvmArgs = arrayListOf("-Dspf.mode=stop", "-Dspf.applicationClass=$launcherClassName")
        main = "com.gridnine.spf.app.SpfBoot"
        classpath = StartTestServerInBuildTask.getClassPath(project, registry, config,filesMap)

    }

    companion object{
        fun getTaskName(pluginId: String) = "_StopTestServerInBuildTask_${pluginId}"
    }

}