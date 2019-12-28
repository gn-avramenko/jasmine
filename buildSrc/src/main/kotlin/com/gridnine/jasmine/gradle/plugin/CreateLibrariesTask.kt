/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.gradle.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.component.ComponentIdentifier
import org.gradle.api.internal.artifacts.result.DefaultResolvedArtifactResult
import org.gradle.api.tasks.TaskAction
import org.gradle.jvm.JvmLibrary
import org.gradle.language.base.artifact.SourcesArtifact
import java.io.File

open class CreateLibrariesTask: DefaultTask(){

    @TaskAction
    fun createLibraries(){
        createLibrary("server")
        createLibrary("server_test")
        createSpfLibrary()
        createKotlinLibrary()
    }

    private fun createKotlinLibrary() {
        LibraryUtils.createLibrary(LibraryUtils.LibraryData("web_js"){
            classes.add("jar://\$KOTLIN_BUNDLED\$/lib/kotlin-stdlib-js.jar!/")
            sources.add("jar://\$KOTLIN_BUNDLED$\$/lib/kotlin-stdlib-js-sources.jar!/")
        }, project.projectDir)
    }

    private fun createSpfLibrary() {
        LibraryUtils.createLibrary(LibraryUtils.LibraryData("spf"){
            classes.add("jar://\$PROJECT_DIR\$/lib/spf-1.0.jar!/")
            sources.add("jar://\$PROJECT_DIR\$/lib/src/spf-1.0-sources.jar!/")
        }, project.projectDir)
    }

    @Suppress("UnstableApiUsage")
    private fun createLibrary(configurationName: String) {
        val config = project.configurations.getByName(configurationName)
        val libraryData = LibraryUtils.LibraryData(configurationName)
        config.forEach{
            libraryData.classes.add("jar://${it.absolutePath}!/")
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
                    libraryData.sources.add("jar://${it.file.absolutePath}!/")
                }
            }
        }
        libraryData.sources.sort()
        libraryData.classes.sort()
        LibraryUtils.createLibrary(libraryData, project.projectDir)
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



