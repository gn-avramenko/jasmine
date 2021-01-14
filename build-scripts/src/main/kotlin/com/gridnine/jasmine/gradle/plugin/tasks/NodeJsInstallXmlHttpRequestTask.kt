/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.gradle.plugin.tasks

import com.moowork.gradle.node.npm.NpmTask
import javax.inject.Inject

abstract class NodeJsInstallXmlHttpRequestTask :NpmTask{
    @Inject
    constructor(){
        group="other"
        setArgs(arrayListOf("install", "xmlhttprequest"))
    }
    companion object{
        const val taskName = "_NodeJsInstallXmlHttpRequestTask"
    }
}