/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.gradle.plugin.tasks

import org.gradle.api.DefaultTask

@Suppress("unused", "LeakingThis")
open class MakeProjectTask:DefaultTask(){
    init{
        dependsOn(CreateArtifactsTask.TASK_NAME, CreateLibrariesTask.TASK_NAME,
        CreateModulesTask.TASK_NAME)
        group = "idea"
    }

    companion object{
        const val TASK_NAME = "makeProject"
    }
}