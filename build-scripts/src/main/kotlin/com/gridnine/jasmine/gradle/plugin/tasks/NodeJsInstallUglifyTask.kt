/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.gradle.plugin.tasks

import com.moowork.gradle.node.npm.NpmTask
import javax.inject.Inject

@Suppress("ConvertSecondaryConstructorToPrimary", "unused", "LeakingThis")
abstract class NodeJsInstallUglifyTask :NpmTask{
    @Inject
    constructor(){
        group="uglify"
        setArgs(arrayListOf("install", "uglify-js"))
    }
    companion object{
        const val taskName = "_NodeJsInstallUglifyTask"
    }
}