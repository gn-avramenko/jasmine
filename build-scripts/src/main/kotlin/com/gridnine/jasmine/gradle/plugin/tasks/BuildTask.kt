/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("LeakingThis")

package com.gridnine.jasmine.gradle.plugin.tasks

import com.gridnine.spf.meta.SpfPluginsRegistry
import org.gradle.api.DefaultTask
import javax.inject.Inject

@Suppress("unused")
open class BuildTask() :DefaultTask(){

    @Inject
    constructor(registry: SpfPluginsRegistry):this(){
        group = "other"
        registry.plugins.forEach {
            when(KotlinUtils.getType(it)){
                SpfPluginType.CORE,SpfPluginType.SERVER,SpfPluginType.SPF,SpfPluginType.SERVER_TEST ->{
                    dependsOn(CompileKotlinJVMPluginTask.getTaskName(it.id))
                }
                else ->{}
            }
        }
        dependsOn(CodeGenPluginTask::class.java)
    }

    companion object{
        const val TASK_NAME = "_build"
    }
}