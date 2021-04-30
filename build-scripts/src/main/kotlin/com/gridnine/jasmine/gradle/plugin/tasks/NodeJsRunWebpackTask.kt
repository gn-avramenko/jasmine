/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.gradle.plugin.tasks

import com.moowork.gradle.node.npm.NpmTask
import com.moowork.gradle.node.task.NodeTask
import java.io.File
import javax.inject.Inject

@Suppress("ConvertSecondaryConstructorToPrimary", "unused", "LeakingThis")
abstract class NodeJsRunWebpackTask :NodeTask{
    @Inject
    constructor(){
        group="webpack"
        dependsOn(NodeJsInstallWebpackTask.taskName,NodeJsInstallWebpackCliTask.taskName,)
        script = File(project.projectDir, "node_modules/.bin/webpack")
    }
    companion object{
        const val taskName = "_NodeJsRunWebpackTask"
    }
}