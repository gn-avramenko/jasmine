/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("LeakingThis")

package com.gridnine.jasmine.gradle.plugin.tasks

import com.gridnine.spf.meta.SpfPluginsRegistry
import org.gradle.api.DefaultTask
import java.io.File
import javax.inject.Inject

@Suppress("unused")
open class MakeDistTask() :DefaultTask(){

    @Inject
    constructor(registry: SpfPluginsRegistry, filesMap:Map<String,File>):this(){
        group = "jenkins"
        dependsOn(CompileProjectTask.TASK_NAME)
        registry.plugins.forEach {
            dependsOn(*CreateWarTasksFactory.getTasksNames(it, filesMap))
            when(KotlinUtils.getType(it)){
                SpfPluginType.CORE,SpfPluginType.SERVER,SpfPluginType.SPF ->{
                    dependsOn(CreateJarForJvmPluginTask.getTaskName(it.id))
                }
            }
        }
        dependsOn(CodeGenPluginTask.TASK_NAME)
        dependsOn(CleanupTask.TASK_NAME)
        doLast{
           KotlinUtils.getSpfFile(registry, filesMap).copyTo(project.file("build/dist/lib/spf.jar"))
            project.configurations.getByName(KotlinUtils.SERVER_CONFIGURATION_NAME).forEach {
                it.copyTo(project.file("build/dist/lib/${it.name}"))
            }
            val file = project.file("build/dist/bin/run.sh")
            file.parentFile.ensureDirExists()
            file.writeText("""
                    cd ..
                    java -Xms128M -Xmx256M -Dspf.mode=shell -Dlogback.configurationFile=config/logback.xml -Dspf.applicationClass=com.gridnine.jasmine.server.spf.SpfApplicationImpl -jar lib/spf.jar > logs/init.log 2>&1 &
                """.trimIndent())
            file.setExecutable(true)
            val file2 = project.file("build/dist/bin/stop.sh")
            file2.writeText("""
                    cd ..
                    java -Xms128M -Xmx256M -Dspf.mode=stop -Dspf.applicationClass=com.gridnine.jasmine.server.spf.SpfApplicationImpl -jar lib/spf.jar
                """.trimIndent())
            file2.setExecutable(true)
            project.file("config").copyRecursively(project.file("build/dist/config"))
            project.file("build/dist/logs").mkdirs()
        }
    }


    companion object{
        const val TASK_NAME = "jenkins-dist"
    }
}