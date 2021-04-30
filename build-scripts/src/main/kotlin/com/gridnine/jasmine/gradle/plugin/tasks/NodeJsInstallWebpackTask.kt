/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.gradle.plugin.tasks

import com.moowork.gradle.node.npm.NpmTask
import javax.inject.Inject

@Suppress("ConvertSecondaryConstructorToPrimary", "unused", "LeakingThis")
abstract class NodeJsInstallWebpackTask :NpmTask{
    @Inject
    constructor(){
        group="webpack"
        setArgs(arrayListOf("install", "webpack"))
    }
    companion object{
        const val taskName = "_NodeJsInstallWebpackTask"
    }
}