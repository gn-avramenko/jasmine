/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
package com.gridnine.jasmine.gradle.plugin.tasks

import com.gridnine.jasmine.gradle.plugin.JasmineConfigExtension
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File
import javax.inject.Inject


@Suppress("unused")
open class CreateArtifactsTask(): DefaultTask() {

    private lateinit var config:JasmineConfigExtension
    @Inject
    constructor(config:JasmineConfigExtension):this(){
        this.config = config
        group="other"
    }

    @TaskAction
    fun createArtifacts() {
        val rootDir = File(project.projectDir, ".idea")
        if(!rootDir.exists()){
            rootDir.mkdirs()
        }
        createWorkspace()
        createCompilerSettings()
        createKotlinc()
        createMisc()
        createVcs()
    }



    private fun createVcs() {
        val file = File(project.projectDir, ".idea/vcs.xml")
        if(file.exists()) return
        val content = xml("project", "version" to "4"){
            "component"("name" to "VcsDirectoryMappings"){
                emptyTag("mapping", "directory" to "", "vcs" to "Git")
            }
        }
        file.writeIfDiffers(content)
    }

    private fun createMisc() {
        val file  = File(project.projectDir, ".idea/misc.xml")
        if(file.exists()){
            return
        }
        val content = xml("project", "version" to "4"){
            "component"("name" to "JavaScriptSettings"){
                emptyTag("option", "name" to "languageLevel", "value" to "ES6.8")
            }
            emptyTag("component", "name" to "ProjectRootManager", "version" to "2",
                    "languageLevel" to config.languageLevel,  "default" to "false", "project-jdk-name" to "1.8",  "project-jdk-type" to "JavaSDK" )
        }
        file.writeIfDiffers(content)
    }

    private fun createKotlinc() {
        val file = File(project.projectDir, ".idea/kotlinc.xml")
        if(file.exists())return
        val content = xml("project", "version" to "4"){
            "component"("name" to "Kotlin2JsCompilerArguments"){
                emptyTag("option", "name" to "moduleKind", "value" to "umd")
                emptyTag("option", "name" to "sourceMap", "value" to "true")
                emptyTag("option", "name" to "sourceMapEmbedSources", "value" to "always")
            }
            "component"("name" to "Kotlin2JvmCompilerArguments"){
                emptyTag("option", "name" to "jvmTarget", "value" to config.targetByteCodeLevel)
            }
        }
        file.writeIfDiffers(content)
    }



    private fun createCompilerSettings() {
        val file = File(project.projectDir, ".idea/compiler.xml")
        if(file.exists())return
        val content = xml("project", "version" to "4"){
            "component"("name" to "CompilerConfiguration"){
                emptyTag("bytecodeTargetLevel", "target" to config.targetByteCodeLevel)
            }
        }
        file.writeIfDiffers(content)

    }

    private fun createWorkspace() {
        val file = File(project.projectDir, ".idea/workspace.xml")
        if(file.exists()){
            return
        }
        val content = xml("project", "version" to "4"){
            "component"("name" to "RunManager", "selected"  to "Application.${project.name}"){
                "configuration"("name" to project.name , "type" to "Application", "factoryName" to "Application"){
                   emptyTag("option", "name" to "MAIN_CLASS_NAME",  "value" to "com.gridnine.spf.app.SpfBoot")
                   emptyTag("module", "name" to project.name)
                   emptyTag("option","name" to "VM_PARAMETERS", "value" to "-Dspf.mode=start -Dspf.applicationClass=com.gridnine.jasmine.server.spf.SpfApplicationImpl")
                    "method"("v" to "2"){
                        emptyTag("option","name" to "Make", "enabled" to "true")
                    }
                }
            }
        }
        file.writeIfDiffers(content)
    }

    companion object{
        const val TASK_NAME = "_createArtifactsTask"
    }
}