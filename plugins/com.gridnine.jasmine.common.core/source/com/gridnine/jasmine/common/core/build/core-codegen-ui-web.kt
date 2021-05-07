/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("UNCHECKED_CAST")

package com.gridnine.jasmine.common.core.build

import com.gridnine.jasmine.common.core.meta.*
import com.gridnine.jasmine.common.core.parser.UiMetadataParser
import java.io.File


@Suppress("unused")
class UiWebGenerator: CodeGenerator {


    override fun generate(destPlugin: File, sources: List<Pair<File, String?>>, projectName: String, generatedFiles: MutableList<File>, context: MutableMap<String, Any>) {
        val registry = UiMetaRegistry()
        sources.forEach { metaFile -> UiMetadataParser.updateUiMetaRegistry(registry, metaFile.first) }
        val webMapping = context[PluginAssociationsGenerator.WEB_MAP_KEY] as HashMap<String, String>
        val classesData = arrayListOf<BaseGenData>()
        val pluginId = destPlugin.name
        registry.enums.values.forEach {
            val enumClassData = GenEnumData(it.id+"JS")
            it.items.values.forEach { ei ->
                enumClassData.enumItems.add(ei.id)
            }
            classesData.add(enumClassData)
            webMapping[enumClassData.id] = pluginId
        }
        GenUtils.generateClasses(classesData, destPlugin, projectName, generatedFiles)
        generateActionsIds(registry,destPlugin, generatedFiles)
        generateUiReflectionUtils(registry, destPlugin, projectName, generatedFiles, context)
    }

    private fun generateUiReflectionUtils(registry: UiMetaRegistry, destPlugin: File, projectName: String, generatedFiles: MutableList<File>, context: MutableMap<String, Any>) {
        val sb = StringBuilder()
        val pluginId = destPlugin.name
        GenUtils.generateHeader(sb, "${pluginId}.Reflection", projectName)
        GenUtils.classBuilder(sb, "object UiReflectionUtilsJS") {
            blankLine()
            "fun registerWebUiClasses()"{
                registry.actions.values.forEach {
                    if(it is ActionDescription){
                        "com.gridnine.jasmine.web.core.reflection.ReflectionFactoryJS.get().registerClass(\"${it.actionHandler}\", {${it.actionHandler}()})"()
                        "com.gridnine.jasmine.web.core.reflection.ReflectionFactoryJS.get().registerQualifiedName(${it.actionHandler}::class, \"${it.actionHandler}\")"()
                    }
                }
            }
        }
        GenUtils.saveFile(destPlugin, "UiReflectionUtilsJS", sb.toString(), generatedFiles)
        val map =context.getOrPut(PluginAssociationsGenerator.WEB_MAP_KEY){
            hashMapOf<String,String>()
        } as MutableMap<String,String>
        registry.actions.values.forEach {
            if (it is ActionDescription) {
                map[it.actionHandler] = pluginId
            }
        }
    }

    private fun generateActionsIds(registry: UiMetaRegistry, destPlugin: File, generatedFiles: MutableList<File>) {
        val actionGroupsIds = registry.actions.values.filterIsInstance<ActionsGroupDescription>().filter { it.root }.map { it.id }
        if(actionGroupsIds.isEmpty()){
            return
        }
        val pluginId = destPlugin.name
        val sb = StringBuilder()
        GenUtils.generateHeader(sb, "${pluginId}.ActionsIds", pluginId)
        GenUtils.classBuilder(sb, "object ActionsIds") {
            actionGroupsIds.forEach {groupId->
                blankLine()
                """val ${groupId.replace(".","_").replace("-","_")} = "$groupId" """()
            }
        }
        GenUtils.saveFile(destPlugin, "ActionsIds", sb.toString(), generatedFiles)
    }
}