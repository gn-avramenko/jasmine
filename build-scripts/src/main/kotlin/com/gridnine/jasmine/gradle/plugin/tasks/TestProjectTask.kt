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
open class TestProjectTask() :DefaultTask(){

    @Inject
    constructor(registry: SpfPluginsRegistry):this(){
        group = "jenkins"
        registry.plugins.forEach {
            when(KotlinUtils.getType(it)){
                 SpfPluginType.COMMON_TEST,SpfPluginType.SERVER_TEST ->{
                    dependsOn(TestJvmPluginTask.getTaskName(it.id))
                }
                SpfPluginType.WEB_TEST ->{
                    val suiteLauncher = it.parameters.find{ param -> param.id == "test-suite-launcher" }?.value
                    if(suiteLauncher != null) {
                        dependsOn(TestJsPluginTask.getTaskName(it.id))
                    }
                }
                else ->{}
            }
        }
    }

    companion object{
        const val TASK_NAME = "jenkins-test"
    }
}