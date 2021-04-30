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
abstract class NodeJsRunUglifyTask :NodeTask{
    @Inject
    constructor(){
        group="uglify"
        dependsOn(NodeJsInstallUglifyTask.taskName)
        script = File(project.projectDir, "node_modules/.bin/uglifyjs")
        setArgs(arrayListOf("/home/avramenko/IdeaProjects/jasmine-demo-master/submodules/jasmine/plugins/com.gridnine.jasmine.web.core/resources/jasmine-core/lib/kotlin.js","--compress","--mangle","--output","/home/avramenko/IdeaProjects/jasmine-demo-master/build/minify/jasmine-core-kotlin.js"))
    }
    companion object{
        const val taskName = "_NodeJsRunUglifyTask"
    }
}