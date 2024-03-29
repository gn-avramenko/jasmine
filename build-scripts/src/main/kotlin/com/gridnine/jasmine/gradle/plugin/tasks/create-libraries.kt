/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("DEPRECATION", "unused")

package com.gridnine.jasmine.gradle.plugin.tasks

import com.gridnine.jasmine.gradle.plugin.JasmineConfigExtension
import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.component.ComponentIdentifier
import org.gradle.api.internal.artifacts.result.DefaultResolvedArtifactResult
import org.gradle.api.tasks.TaskAction
import org.gradle.jvm.JvmLibrary
import org.gradle.language.base.artifact.SourcesArtifact
import java.io.File
import javax.inject.Inject

open class CreateLibrariesTask(): DefaultTask(){

    private lateinit var config:JasmineConfigExtension

    @Inject
    constructor(config:JasmineConfigExtension):this(){
        this.config = config
    }

    @TaskAction
    fun createLibraries(){
        createLibrary(KotlinUtils.COMMON_CONFIGURATION_NAME)
        createLibrary(KotlinUtils.COMMON_TEST_CONFIGURATION_NAME)
        createLibrary(KotlinUtils.SERVER_CONFIGURATION_NAME)
        createLibrary(KotlinUtils.SERVER_TEST_CONFIGURATION_NAME)
        createLibrary(KotlinUtils.WEB_CONFIGURATION_NAME, "kotlin-stdlib-common-.*")
        createSpfLibrary()
    }



    private fun createSpfLibrary() {
        LibraryUtils.createLibrary(LibraryUtils.LibraryData("spf"){
            classes.add("jar://\$PROJECT_DIR\$/${config.libRelativePath}/spf-1.0.jar!/")
            sources.add("jar://\$PROJECT_DIR\$/${config.libRelativePath}/src/spf-1.0-sources.jar!/")
        }, project.projectDir)
    }

    @Suppress("UnstableApiUsage")
    private fun createLibrary(configurationName: String, excludePattern: String? = null) {
        val config = project.configurations.getByName(configurationName)
        val libraryData = LibraryUtils.LibraryData(configurationName)
        config.forEach{
            if(excludePattern == null || !it.name.matches(Regex(excludePattern))){
                libraryData.classes.add("jar://${it.absolutePath}!/")
            }
        }
        val components = arrayListOf<ComponentIdentifier>()
        config.incoming.resolutionResult.allDependencies.forEach{ res ->
            if(res is org.gradle.api.internal.artifacts.result.DefaultResolvedDependencyResult){
                components.add(res.selected.id)
            }
        }

        val result = project.dependencies.createArtifactResolutionQuery()
                .forComponents(*components.toArray(arrayOfNulls<ComponentIdentifier>(components.size)))
                .withArtifacts(JvmLibrary::class.java, SourcesArtifact::class.java).execute()
        result.resolvedComponents.forEach{comp ->
            comp.getArtifacts(SourcesArtifact::class.java).forEach{
                if(it is DefaultResolvedArtifactResult){
                    if(excludePattern == null || !it.file.name.matches(Regex(excludePattern))) {
                        libraryData.sources.add("jar://${it.file.absolutePath}!/")
                    }
                }
            }
        }
        libraryData.sources.sort()
        libraryData.classes.sort()
        LibraryUtils.createLibrary(libraryData, project.projectDir)
    }

    companion object{
        const val TASK_NAME = "_createLibraries"
    }
}

object LibraryUtils{

    fun createLibrary(library: LibraryData, projectDir:File){
        val libraryDir = File(projectDir, ".idea/libraries")
        if(!libraryDir.exists()){
            libraryDir.mkdirs()
        }
        val content = xml("component", "name" to "libraryTable") {
            "library" ("name" to library.name) {
                "CLASSES" {
                    library.classes.forEach {
                        emptyTag("root", "url" to it)
                    }
                }
                "JAVADOC"{}
                "SOURCES"{
                    library.sources.forEach {
                        emptyTag("root", "url" to it)
                    }
                }
            }
        }
        File(libraryDir, "${library.name}.xml").writeIfDiffers(content)
    }

    data class LibraryData(val name: String) {
        constructor(name: String, init:LibraryData.() -> Unit):this(name){
            this.init()
        }
        val classes = arrayListOf<String>()
        val sources = arrayListOf<String>()
    }
}



