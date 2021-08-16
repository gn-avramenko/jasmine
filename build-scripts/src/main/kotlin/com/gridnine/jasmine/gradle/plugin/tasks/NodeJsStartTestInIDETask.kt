/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.gradle.plugin.tasks

import com.github.gradle.node.task.NodeTask
import java.io.File
import javax.inject.Inject

@Suppress("ConvertSecondaryConstructorToPrimary", "unused", "LeakingThis")
abstract class NodeJsStartTestInIDETask : NodeTask {
    @Inject
    constructor(launcherName:String, pluginId:String,debug:Boolean){
        group="other"
        ignoreExitValue.set(true)
        dependsOn(NodeJsCopyJsFilesTask.taskName)
        shouldRunAfter(StartTestServerInIDETask.getTaskName(pluginId))
        script.set(File(project.projectDir, "node_modules/mocha/bin/mocha"))
        if(debug){
            args.set(arrayListOf("--inspect-brk", "--timeout", "10000", "node_modules/$launcherName"))
        } else {
            args.set(arrayListOf("--timeout", "10000", "node_modules/$launcherName"))
        }
    }
    companion object{
        fun getTaskName(launcherName:String, pluginId:String, debug:Boolean) = "_NodeJsStartTestInIDETask_${pluginId}_${launcherName}_${if(debug) "debug" else "standard"}"
    }
}