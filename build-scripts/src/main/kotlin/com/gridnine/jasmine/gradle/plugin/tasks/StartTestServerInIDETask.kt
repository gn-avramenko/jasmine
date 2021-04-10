/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.gradle.plugin.tasks

import com.gridnine.spf.app.SpfBoot
import com.gridnine.spf.meta.SpfPlugin
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import javax.inject.Inject

@Suppress("unused", "LeakingThis")
open class StartTestServerInIDETask() : BaseStartServerTask() {

    @Inject
    constructor(plugin: SpfPlugin):this(){
        val launcherClassName = plugin.parameters.find { param -> param.id == "server-launcher-class" }?.value
        group = "other"
        jvmArgs = arrayListOf("-Dspf.mode=shell", "-Dspf.applicationClass=$launcherClassName", "-Dspf.externalsFileName=externals-test")
        main = "com.gridnine.spf.app.SpfBoot"
        this.classpath = getClassPath(project)
        this.doLast {
            while (!SpfBoot.isApplicationRunning()) {
                Thread.sleep(1000L)
            }
        }
        shouldRunAfter(NodeJsCopyJsFilesTask.taskName)
    }

    companion object{
        fun getTaskName(pluginId: String) = "_jsTestStartServerInIDE_${pluginId}"
        fun getClassPath(project:Project): FileCollection {
            var file = project.file("lib/spf-1.0.jar")
            if(!file.exists()){
                file = project.file("submodules/jasmine/lib/spf-1.0.jar")
            }
            val fileNames = arrayListOf(file.absolutePath)
            fileNames.addAll(project.file("lib/externals-test.txt").readLines())
            return project.files(fileNames.toTypedArray())
        }
    }

}