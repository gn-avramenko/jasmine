/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.gradle.plugin.tasks

import com.github.gradle.node.npm.task.NpmTask
import javax.inject.Inject

@Suppress("ConvertSecondaryConstructorToPrimary", "unused", "LeakingThis")
abstract class NodeJsInstallWebpackTask :NpmTask{
    @Inject
    constructor(){
        group="webpack"
        args.set(arrayListOf("install", "webpack"))
    }
    companion object{
        const val taskName = "_NodeJsInstallWebpackTask"
    }
}