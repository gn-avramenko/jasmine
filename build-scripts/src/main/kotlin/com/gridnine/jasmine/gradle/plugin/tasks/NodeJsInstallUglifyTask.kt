/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.gradle.plugin.tasks

import com.github.gradle.node.npm.task.NpmTask
import javax.inject.Inject

@Suppress("ConvertSecondaryConstructorToPrimary", "unused", "LeakingThis")
abstract class NodeJsInstallUglifyTask :NpmTask{
    @Inject
    constructor(){
        group="uglify"
        args.set(arrayListOf("install", "uglify-js"))
    }
    companion object{
        const val taskName = "_NodeJsInstallUglifyTask"
    }
}