/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
package com.gridnine.jasmine.server.core.build

import com.gridnine.jasmine.server.core.app.IApplicationMetadataProvider
import com.gridnine.jasmine.server.core.model.common.Xeption
import com.gridnine.jasmine.server.core.model.domain.*
import com.gridnine.jasmine.server.core.model.ui.UiMetaRegistry
import com.gridnine.jasmine.server.core.model.ui.UiMetadataParser
import java.io.File


object UiWebGenerator {


    fun generateUiClasses(metadataProvider: IApplicationMetadataProvider, generatedFiles: MutableMap<String, MutableList<File>>,  pluginsLocation:Map<String,File>, projectName: String) {
        val uiData = linkedMapOf<String, MutableList<File>>()
        metadataProvider.getExtensions("ui-metadata").forEach {
            val destClientPlugin = it.getParameters("dest-client-plugin-id").firstOrNull()
            if (destClientPlugin != null) {
                uiData.getOrPut(destClientPlugin, { arrayListOf() })
                        .add(File(pluginsLocation[it.plugin.pluginId], it.getParameters("relative-path").first()))
            }
            Unit
        }
        uiData.entries.forEach { (key, value) ->
            val registry = UiMetaRegistry()
            value.forEach { metaFile -> UiMetadataParser.updateUiMetaRegistry(registry, metaFile) }
            val classesData = arrayListOf<BaseGenData>()
            val enums = arrayListOf<String>()
            registry.enums.values.forEach {
                val enumClassData = GenEnumData(it.id + "JS")
                it.items.values.forEach { ei ->
                    enumClassData.enumItems.add(ei.id)
                }
                classesData.add(enumClassData)
                enums.add(it.id + "JS")
            }
            GenUtils.generateClasses(classesData, pluginsLocation[key]?: throw Xeption.forDeveloper("unable to find basedir of plugin $key"), projectName, generatedFiles.getOrPut(key, { arrayListOf() }))
            val sb = StringBuilder()
            GenUtils.generateHeader(sb, "${key}.Reflection", projectName)

            GenUtils.classBuilder(sb, "object UiReflectionUtilsJS") {
                blankLine()
                "fun registerWebUiClasses()"{
                    enums.forEach {
                        "com.gridnine.jasmine.web.core.reflection.ReflectionFactoryJS.get().registerEnum(\"$it\", {$it.valueOf(it)})"()
                    }
                    registry.enums.values.forEach {
                        val className = it.id + "JS"
                        "com.gridnine.jasmine.web.core.reflection.ReflectionFactoryJS.get().registerQualifiedName($className::class, \"$className\")"()
                    }
                }
            }
            val file = File(pluginsLocation[key]?: throw Xeption.forDeveloper("unable to find basedir of plugin $key"), "source-gen/${key.replace(".", "/")}/UiReflectionUtils.kt")
            if (!file.parentFile.exists()) {
                file.parentFile.mkdirs()
            }
            val content = sb.toString().toByteArray()
            if (!file.exists() || !content.contentEquals(file.readBytes())) {
                file.writeBytes(content)
            }
            generatedFiles.getOrPut(key) { arrayListOf() }.add(file)
        }
    }


}