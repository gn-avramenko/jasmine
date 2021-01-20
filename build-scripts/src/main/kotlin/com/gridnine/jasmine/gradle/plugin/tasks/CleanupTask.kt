/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("LeakingThis")

package com.gridnine.jasmine.gradle.plugin.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

@Suppress("unused")
open class CleanupTask @Inject constructor() : DefaultTask() {

    init {
        group = "other"
    }

    @TaskAction
    fun cleanup() {
        project.file("build").let {
            it.emptyDir()
            it.ensureDirExists()
        }
        project.file("build/dist").ensureDirExists()
    }

    companion object{
        const val TASK_NAME = "_CleanupTask"
    }
}