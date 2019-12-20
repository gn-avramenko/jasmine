/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
package com.gridnine.jasmine.gradle.plugin

import com.gridnine.spf.meta.SpfPluginsRegistry
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File


open class CreateModulesTask: DefaultTask() {

    lateinit var registry:SpfPluginsRegistry
    @TaskAction
    fun createArtifacts() {
        val projectName = project.name
        val rootDir = File(project.projectDir, ".idea/modules")
        if(!rootDir.exists()){
            rootDir.mkdirs()
        }
        createMainModule(projectName)
        createModules(projectName)
        createPlugins()
    }

    private fun createPlugins() {
        registry.plugins.forEach{pluginDescr ->
            val pluginType = KotlinUtils.getType(pluginDescr)
            val content = xml("module", "version" to "4"){
                "component"("name" to "NewModuleRootManager"){
                    emptyTag("output", "url" to "file://\$MODULE_DIR\$/../../plugins/${pluginDescr.id}/classes")
                    emptyTag("output-test", "url" to "file://\$MODULE_DIR\$/../..plugins/${pluginDescr.id}/classes")
                    "content"("url" to "file://\$MODULE_DIR\$/../../plugins/${pluginDescr.id}"){
                        if(File(project.projectDir,"plugins/${pluginDescr.id}/source").exists()){
                            emptyTag("sourceFolder", "url" to "file://\$MODULE_DIR\$/../../plugins/${pluginDescr.id}/source",
                                    "isTestSource" to if(pluginType == SpfPluginType.SERVER_TEST) "true" else "false")
                        }
                        if(File(project.projectDir,"plugins/${pluginDescr.id}/source-gen").exists()){
                            emptyTag("sourceFolder", "url" to "file://\$MODULE_DIR\$/../../plugins/${pluginDescr.id}/source-gen",
                                    "isTestSource" to "false", "generated" to "true")
                        }
                        if(File(project.projectDir,"plugins/${pluginDescr.id}/classes").exists()){
                            emptyTag("excludeFolder", "url" to "file://\$MODULE_DIR\$/../../plugins/${pluginDescr.id}/classes")
                        }
                    }

                    when(pluginType){
                        SpfPluginType.SERVER,SpfPluginType.CORE ->{
                            emptyTag("orderEntry", "type" to "sourceFolder", "forTests" to "false")
                            emptyTag("orderEntry", "type" to "inheritedJdk")
                            emptyTag("orderEntry", "type" to "library", "name" to "server", "level" to "project")
                        }
                        SpfPluginType.SERVER_TEST ->{
                            emptyTag("orderEntry", "type" to "sourceFolder", "forTests" to "true")
                            emptyTag("orderEntry", "type" to "inheritedJdk")
                            emptyTag("orderEntry", "type" to "library", "name" to "server", "level" to "project")
                            emptyTag("orderEntry", "type" to "library", "name" to "server_test", "level" to "project")
                        }
                        SpfPluginType.SPF ->{
                            emptyTag("orderEntry", "type" to "sourceFolder", "forTests" to "false")
                            emptyTag("orderEntry", "type" to "inheritedJdk")
                            emptyTag("orderEntry", "type" to "library", "name" to "server", "level" to "project")
                            emptyTag("orderEntry", "type" to "library", "name" to "spf", "level" to "project")
                        }
                    }
                    pluginDescr.pluginsDependencies.forEach {
                        emptyTag("orderEntry", "type" to "module", "module-name" to it.pluginId)
                    }
                }
            }
            File(project.projectDir, ".idea/modules/${pluginDescr.id}.iml").writeText(content, charset("utf-8"))
        }
    }

    private fun createModules(projectName: String) {

        val content = xml("component","name" to "ProjectModuleManager"){
            "modules"{
                emptyTag("module",  "fileurl" to "file://\$PROJECT_DIR\$/.idea/modules/$projectName.iml",
                        "filepath" to "\$PROJECT_DIR\$/.idea/modules/$projectName.iml",
                        "group" to projectName)
                registry.plugins.forEach {
                    emptyTag("module",  "fileurl" to "file://\$PROJECT_DIR\$/.idea/modules/${it.id}.iml",
                            "filepath" to "\$PROJECT_DIR\$/.idea/modules/${it.id}.iml",
                            "group" to projectName)
                }

            }
        }
        File(project.projectDir, ".idea/modules.xml").writeText(content, charset("utf-8"))
    }

    private fun createMainModule(projectName: String) {
        val content = xml("module",
                "external.linked.project.id" to projectName,
                "external.linked.project.path" to "\$MODULE_DIR\$/../..",
                "external.root.project.path" to "\$MODULE_DIR\$/../..",
                "external.system.id" to "GRADLE",
                "external.system.module.group" to "",
                "external.system.module.version" to "unspecified",
                "type" to "JAVA_MODULE",
                "version" to "4"
                ){
            "component"("name" to "NewModuleRootManager", "inherit-compiler-output" to "true"){
                emptyTag("exclude-output")
                "content"("url" to "file://\$MODULE_DIR\$/../.."){
                    emptyTag("excludeFolder", "url" to "file://\$MODULE_DIR\$/../../.gradle")
                    emptyTag("excludeFolder", "url" to "file://\$MODULE_DIR\$/../../build")
                }
                emptyTag("orderEntry", "type" to "inheritedJdk")
                emptyTag("orderEntry", "type" to "sourceFolder", "forTests" to "false")
                emptyTag("orderEntry", "type" to "library", "name" to "spf", "level" to "project")
            }
        }
        File(project.projectDir, ".idea/modules/$projectName.iml").writeText(content, charset("utf-8"))
    }

}