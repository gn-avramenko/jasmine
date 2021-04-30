/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.gradle.plugin.tasks

import com.moowork.gradle.node.npm.NpmTask
import javax.inject.Inject

@Suppress("ConvertSecondaryConstructorToPrimary", "unused", "LeakingThis")
abstract class NodeJsInstallWebpackCliTask :NpmTask{
    @Inject
    constructor(){
        group="webpack"
        dependsOn(NodeJsInstallWebpackTask.taskName)
        setArgs(arrayListOf("install", "webpack-cli"))
    }
    companion object{
        const val taskName = "_NodeJsInstallWebpackCliTask"
    }
}