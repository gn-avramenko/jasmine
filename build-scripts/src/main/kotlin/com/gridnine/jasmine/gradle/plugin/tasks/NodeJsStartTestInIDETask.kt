/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.gradle.plugin.tasks

import com.moowork.gradle.node.task.NodeTask
import java.io.File
import javax.inject.Inject

@Suppress("ConvertSecondaryConstructorToPrimary", "unused", "LeakingThis")
abstract class NodeJsStartTestInIDETask : NodeTask {
    @Inject
    constructor(launcherName:String, pluginId:String,debug:Boolean){
        group="other"
        setIgnoreExitValue(true)
        dependsOn(NodeJsCopyJsFilesTask.taskName)
        //shouldRunAfter(StartTestServerInIDETask.getTaskName(pluginId))
        script = File(project.projectDir, "node_modules/mocha/bin/mocha")
        if(debug){
            setArgs(arrayListOf("--inspect-brk", "--timeout", "10000", "node_modules/$launcherName"))
        } else {
            setArgs(arrayListOf("--timeout", "10000", "node_modules/$launcherName"))
        }
    }
    companion object{
        fun getTaskName(launcherName:String, pluginId:String, debug:Boolean) = "_NodeJsStartTestInIDETask_${pluginId}_${launcherName}_${if(debug) "debug" else "standard"}"
    }
}