/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("LeakingThis")

package com.gridnine.jasmine.gradle.plugin.tasks

import com.gridnine.jasmine.gradle.plugin.JasmineConfigExtension
import com.gridnine.spf.meta.SpfPluginsRegistry
import org.gradle.api.DefaultTask
import java.io.File
import javax.inject.Inject

@Suppress("unused")
open class MakeDistTask() :DefaultTask(){

    @Inject
    constructor(registry: SpfPluginsRegistry, config: JasmineConfigExtension, filesMap:Map<String,File>):this(){
        group = "jenkins"
        dependsOn(CompileProjectTask.TASK_NAME)
        registry.plugins.forEach {
            dependsOn(*CreateWarTasksFactory.getTasksNames(it, filesMap))
            when(KotlinUtils.getType(it)){
                SpfPluginType.COMMON_CORE, SpfPluginType.SERVER_CORE,SpfPluginType.COMMON,SpfPluginType.SERVER,SpfPluginType.SPF ->{
                    dependsOn(CreateJarForJvmPluginTask.getTaskName(it.id))
                }
                else -> {}
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
                    java -Xms128M -Xmx256M -Dspf.mode=shell -Dlogback.configurationFile=config/logback.xml -Dspf.applicationClass=com.gridnine.jasmine.common.spf.SpfApplicationImpl -jar lib/spf.jar > logs/init.log 2>&1 &
                """.trimIndent())
//            file.writeText("""
//                    cd ..
//                    java -Xms128M -Xmx256M -Dspf.mode=start -Dlogback.configurationFile=config/logback.xml -Dspf.applicationClass=com.gridnine.jasmine.server.spf.SpfApplicationImpl -jar lib/spf.jar
//                """.trimIndent())

            file.setExecutable(true)
            val file2 = project.file("build/dist/bin/stop.sh")
            file2.writeText("""
                    cd ..
                    java -Xms128M -Xmx256M -Dspf.mode=stop -Dspf.applicationClass=com.gridnine.jasmine.server.spf.SpfApplicationImpl -jar lib/spf.jar
                """.trimIndent())
            file2.setExecutable(true)
            project.file("config").copyRecursively(project.file("build/dist/config"))
            project.file("build/dist/logs").mkdirs()

            val sources = registry.plugins.filter {
                val type = KotlinUtils.getType(it)
                type == SpfPluginType.WEB || type == SpfPluginType.WEB_CORE
            }.flatMap {
                val pluginDir = filesMap[it.id]!!
                val result = arrayListOf<String>()
                val sourceFile = File(pluginDir, "source")
                if(sourceFile.exists()){
                    result.add(sourceFile.toRelativeString(project.file(".")))
                }
                val sourceGenFile = File(pluginDir, "source-gen")
                if(sourceGenFile.exists()){
                    result.add(sourceGenFile.toRelativeString(project.file(".")))
                }
                result
            }.joinToString ("\r\n                            "){
             "kotlin.srcDir(\"${it}\")"
            }
            project.file("js-build.gradle.kts").writeIfDiffers("""
                import org.gradle.jvm.tasks.*

                plugins {
                    kotlin("js") version "${config.kotlinVersion}"
                }

                dependencies {
                    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:${config.kotlinCoroutinesJSVersion}")
                }

                repositories{
                    mavenCentral()
                }

                kotlin {
                    js {
                        sourceSets["main"].apply {
                            $sources
                        }
                        browser{
                            distribution {
                                directory = File(project.rootDir, "temp/js/output/")
                            }
                        }
                    }
                }

                task("_unzip_war", Copy::class) {
                    doFirst{
                        println("deleting directory")
                        val file = project.file("temp/war/input")
                        if(file.exists()){
                            file.deleteRecursively()
                        }
                    }
                    shouldRunAfter("build")
                    from(zipTree(file("build/dist/lib/${config.indexWar}")))
                    into(project.file("temp/war/input"))
                }

                task("update-index-war", Jar::class){
                    dependsOn("_unzip_war")
                    destinationDirectory.set(project.file("temp/war/output"))
                    from(project.file("temp/war/input"))
                    archiveFileName.set("${config.indexWar}")
                    doFirst{
                        project.file("temp/js/output/${project.name}.js").copyTo(project.file("temp/war/input/${project.name}.js"))
                        project.file("temp/js/output/${project.name}.js.map").copyTo(project.file("temp/war/input/${project.name}.js.map"))
                        project.file("temp/war/input/index.html").delete()
                        project.file("temp/war/input/index-prod.html").renameTo(project.file("temp/war/input/index.html"))
                    }
                    doLast{
                        project.file("build/dist/lib/${config.indexWar}").delete()
                        project.file("temp/war/output/${config.indexWar}").renameTo(project.file("build/dist/lib/${config.indexWar}"))
                    }
                }
            """.trimIndent())

        }
    }


    companion object{
        const val TASK_NAME = "jenkins-dist"
    }
}