/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
package com.gridnine.jasmine.gradle.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File


open class CreateArtifactsTask: DefaultTask() {

    @TaskAction
    fun createArtifacts() {
        val projectName = project.name
        val rootDir = File(project.projectDir, ".idea")
        if(!rootDir.exists()){
            rootDir.mkdirs()
        }
        createWorkspace()
        createCompilerSettings(projectName)
        createGradle()
        createKotlinc()
        createMisc()
        createVcs()
    }

    private fun createVcs() {
        val content = xml("project", "version" to "4"){
            "component"("name" to "VcsDirectoryMappings"){
                emptyTag("mapping", "directory" to "", "vcs" to "Git")
            }
        }
        File(project.projectDir, ".idea/vcs.xml").writeText(content, charset("utf-8"))
    }

    private fun createMisc() {
        val content = xml("project", "version" to "4"){
            "component"("name" to "JavaScriptSettings"){
                emptyTag("option", "name" to "languageLevel", "value" to "ES6.8")
            }
            emptyTag("component", "name" to "ProjectRootManager", "version" to "2",
                    "languageLevel" to "JDK_12",  "default" to "false", "project-jdk-name" to "12",  "project-jdk-type" to "JavaSDK" )
        }
        File(project.projectDir, ".idea/misc.xml").writeText(content, charset("utf-8"))
    }

    private fun createKotlinc() {
        val content = xml("project", "version" to "4"){
            "component"("name" to "Kotlin2JsCompilerArguments"){
                emptyTag("option", "name" to "sourceMap", "value" to "true")
                emptyTag("option", "name" to "sourceMapEmbedSources", "value" to "always")
            }
            "component"("name" to "Kotlin2JvmCompilerArguments"){
                emptyTag("option", "name" to "jvmTarget", "value" to "12")
            }
        }
        File(project.projectDir, ".idea/kotlinc.xml").writeText(content, charset("utf-8"))
    }

    private fun createGradle() {
        val content = xml("project", "version" to "4"){
            "component"("name" to "GradleSettings"){
                "option"("name" to "linkedExternalProjectsSettings"){
                    "GradleProjectSettings"{
                        emptyTag("option", "name" to "distributionType", "value" to "DEFAULT_WRAPPED")
                        emptyTag("option", "name" to "externalProjectPath", "value" to "\$PROJECT_DIR\$")
                        "option"("name" to "modules"){
                            "set"{
                                emptyTag("option", "value" to "\$PROJECT_DIR\$")
                                emptyTag("option", "value" to "\$PROJECT_DIR\$/buildSrc")
                            }
                        }
                    }
                }
            }
        }
        File(project.projectDir, ".idea/gradle.xml").writeText(content, charset("utf-8"))
    }

    private fun createCompilerSettings(projectName:String) {
        val content = xml("project", "version" to "4"){
            "component"("name" to "CompilerConfiguration"){
                "bytecodeTargetLevel"{
                    emptyTag("module", "name" to "${projectName}_buildSrc_main", "target" to "12")
                    emptyTag("module", "name" to "${projectName}_main", "target" to "12")
                }
            }
        }
        File(project.projectDir, ".idea/compiler.xml").writeText(content, charset("utf-8"))

    }

    private fun createWorkspace() {
        val content = xml("project", "version" to "4"){
            //TODO
        }
        File(project.projectDir, ".idea/workspace.xml").writeText(content, charset("utf-8"))

    }
}