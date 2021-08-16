/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.gradle.plugin.tasks

import com.github.gradle.node.npm.task.NpmTask
import javax.inject.Inject

@Suppress("ConvertSecondaryConstructorToPrimary", "unused", "LeakingThis")
abstract class NodeJsInstallMochaTask :NpmTask{
    @Inject
    constructor(){
        group="other"
        args.set(arrayListOf("install", "mocha"))
    }
    companion object{
        const val taskName = "_NodeJsInstallMochaTask"
    }
}