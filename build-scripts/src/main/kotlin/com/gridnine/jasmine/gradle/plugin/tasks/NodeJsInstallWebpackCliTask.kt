/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.gradle.plugin.tasks

import com.github.gradle.node.npm.task.NpmTask
import javax.inject.Inject

@Suppress("ConvertSecondaryConstructorToPrimary", "unused", "LeakingThis")
abstract class NodeJsInstallWebpackCliTask :NpmTask{
    @Inject
    constructor(){
        group="webpack"
        dependsOn(NodeJsInstallWebpackTask.taskName)
        args.set(arrayListOf("install", "webpack-cli"))
    }
    companion object{
        const val taskName = "_NodeJsInstallWebpackCliTask"
    }
}