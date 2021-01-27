/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.gradle.plugin.tasks

import com.gridnine.jasmine.gradle.plugin.BaseStartServerTask
import com.gridnine.spf.app.SpfBoot
import com.gridnine.spf.meta.SpfPlugin
import com.gridnine.spf.meta.SpfPluginsRegistry
import javax.inject.Inject

open class StopTestServerInIDETask() : BaseStartServerTask() {

    @Inject
    constructor(registry: SpfPluginsRegistry, plugin: SpfPlugin):this(){
        dependsOn(StartTestServerInIDETask.getTaskName(plugin.id))
        val launcherClassName = plugin.parameters.find { param -> param.id == "server-launcher-class" }!!.value
        jvmArgs = arrayListOf("-Dspf.mode=stop", "-Dspf.applicationClass=$launcherClassName")
        main = "com.gridnine.spf.app.SpfBoot"
        classpath = StartTestServerInIDETask.getClassPath(project)
        val individualLauncher = plugin.parameters.find{ param -> param.id == "individual-test-launcher" }?.value
        if(individualLauncher != null){
            shouldRunAfter(NodeJsStartTestInIDETask.getTaskName(individualLauncher, plugin.id, false),NodeJsStartTestInIDETask.getTaskName(individualLauncher, plugin.id, true))
        }
    }

    companion object{
        fun getTaskName(pluginId: String) = "_jsTestStopServerInIDE_${pluginId}"
    }

}