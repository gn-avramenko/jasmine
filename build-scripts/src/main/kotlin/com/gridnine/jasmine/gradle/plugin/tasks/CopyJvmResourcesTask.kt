/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.gradle.plugin.tasks

import com.gridnine.jasmine.gradle.plugin.JasmineConfigExtension
import com.gridnine.spf.meta.SpfPlugin
import com.gridnine.spf.meta.SpfPluginsRegistry
import org.gradle.api.tasks.Copy
import java.io.File
import javax.inject.Inject

@Suppress("unused", "LeakingThis")
open class CopyJvmResourcesTask():Copy(){

    @Inject
    constructor(plugin: SpfPlugin,  filesMap:Map<String, File>):this(){
        group = "other"
        dependsOn(CompileKotlinJVMPluginTask.getTaskName(plugin.id))
        from(File(filesMap[plugin.id], "source"))
        include("**/*.xml", "**/*.properties")
        into(project.file("build/plugins/${plugin.id}/classes"))
    }


    companion object{
        fun getTaskName(pluginId:String) = "_copyResources_$pluginId"
    }
}