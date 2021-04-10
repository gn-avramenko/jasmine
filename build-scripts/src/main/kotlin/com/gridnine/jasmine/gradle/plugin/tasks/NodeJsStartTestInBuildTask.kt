/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.gradle.plugin.tasks

import com.moowork.gradle.node.task.NodeTask
import java.io.File
import javax.inject.Inject
@Suppress("ConvertSecondaryConstructorToPrimary", "unused", "LeakingThis")
abstract class NodeJsStartTestInBuildTask : NodeTask {
    @Inject
    constructor(launcherName:String, pluginId:String){
        group="other"
        setIgnoreExitValue(true)
        dependsOn(NodeJsCopyJsFilesInBuildTask.taskName)
        dependsOn(StartTestServerInBuildTask.getTaskName(pluginId))
        script = File(project.projectDir, "node_modules/mocha/bin/mocha")
        setArgs(arrayListOf("--timeout", "10000", "--reporter", "mocha-jenkins-reporter", "--reporter-option", "junit_report_name=Tests,junit_report_path=build/junit-reports/${pluginId}-junit.xml,junit_report_stack=1", "node_modules/$launcherName"))
    }
    companion object{
        fun getTaskName(launcherName:String, pluginId:String) = "_NodeJsStartTestInBuildTask_${pluginId}_${launcherName}"
    }
}