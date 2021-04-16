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
open class CompileProjectTask() :DefaultTask(){

    @Inject
    constructor(registry: SpfPluginsRegistry):this(){
        group = "other"
        registry.plugins.forEach {
            when(KotlinUtils.getType(it)){
                SpfPluginType.COMMON_CORE, SpfPluginType.COMMON_TEST, SpfPluginType.SERVER_CORE,SpfPluginType.COMMON,SpfPluginType.SERVER,SpfPluginType.SPF,SpfPluginType.SERVER_TEST ->{
                    dependsOn(CompileKotlinJVMPluginTask.getTaskName(it.id))
                    dependsOn(CopyJvmResourcesTask.getTaskName(it.id))
                }
                SpfPluginType.WEB, SpfPluginType.WEB_CORE,SpfPluginType.WEB_TEST ->{
                    dependsOn(CompileKotlinJSPluginTask.getTaskName(it.id))
                }
            }
        }
        dependsOn(CodeGenPluginTask.TASK_NAME)
    }

    companion object{
        const val TASK_NAME = "_compileProject"
    }
}