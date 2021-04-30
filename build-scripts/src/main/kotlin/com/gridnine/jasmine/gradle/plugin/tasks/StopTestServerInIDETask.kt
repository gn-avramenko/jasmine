/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.gradle.plugin.tasks

import com.gridnine.spf.meta.SpfPlugin
import javax.inject.Inject

@Suppress("ConvertSecondaryConstructorToPrimary", "unused", "LeakingThis")
open class StopTestServerInIDETask() : BaseStartServerTask() {

    @Inject
    constructor(plugin: SpfPlugin):this(){
        //dependsOn(StartTestServerInIDETask.getTaskName(plugin.id))
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