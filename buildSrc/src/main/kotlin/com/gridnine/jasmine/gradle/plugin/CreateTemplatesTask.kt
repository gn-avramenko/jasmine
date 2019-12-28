/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
package com.gridnine.jasmine.gradle.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File


open class CreateTemplatesTask: DefaultTask() {

    @TaskAction
    fun createTemplates() {
        val rootDir = File(project.projectDir, ".idea/fileTemplates/internal")
        if(!rootDir.exists()){
            rootDir.mkdirs()
        }
        createKotlinFileTemplate(rootDir)
        createKotlinScriptTemplate(rootDir)
    }

    private fun createKotlinFileTemplate(rootDir: File) {
        val projectName  ="\${PROJECT_NAME}"
        val packageName  ="\${PACKAGE_NAME}"
        val name  ="\${NAME}"
        val content = """
            /*****************************************************************
             * Gridnine AB http://www.gridnine.com
             * Project:             $projectName
             *****************************************************************/
            #if (            $packageName             &&             $packageName            != "")package             $packageName

            #end
            #parse("File Header.java")
            class             $name             {
            }
        """.trimIndent()
        File(rootDir, "Kotlin File.kt").writeIfDiffers(content)
    }

    private fun createKotlinScriptTemplate(rootDir: File) {
        val projectName  ="\${PROJECT_NAME}"
        val content = """
            /*****************************************************************
             * Gridnine AB http://www.gridnine.com
             * Project:             $projectName
             *****************************************************************/
        """.trimIndent()
        File(rootDir, "Kotlin Script.kt").writeIfDiffers(content)
    }
}