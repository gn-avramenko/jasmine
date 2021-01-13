/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.gradle.plugin.tasks

import org.gradle.api.DefaultTask
import javax.inject.Inject

open class SetupNodeTask :DefaultTask{
    @Inject
    constructor(){
        group="individual-js-tests"
        dependsOn(NodeJsInstallMochaTask.taskName, NodeJsInstallXmlHttpRequestTask.taskName,NodeJsInstalReporterTask.taskName)
    }
    companion object{
        const val taskName = "setupNode"
    }
}