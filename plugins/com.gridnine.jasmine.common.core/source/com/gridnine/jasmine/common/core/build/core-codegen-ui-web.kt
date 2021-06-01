/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("UNCHECKED_CAST")

package com.gridnine.jasmine.common.core.build

import com.gridnine.jasmine.common.core.meta.*
import com.gridnine.jasmine.common.core.model.*
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
        registry.displayHandlers.values.forEach {
            webMapping[it.className] = pluginId
        }
        val classes = arrayListOf<String>()
        registry.viewModels.values.forEach {vmd ->
            classesData.add(toGenData(vmd))
            classes.add(vmd.id + "JS")
            webMapping[vmd.id + "JS"] = pluginId
        }
        registry.viewSettings.values.forEach {vsd ->
            classesData.add(toGenData(vsd))
            classes.add(vsd.id + "JS")
            webMapping[vsd.id + "JS"] = pluginId
        }
        registry.viewValidations.values.forEach {vvd ->
            classesData.add(toGenData(vvd))
            classes.add(vvd.id + "JS")
            webMapping[vvd.id + "JS"] = pluginId
        }
        registry.views.values.forEach {
            webMapping[it.id] = pluginId
            webMapping["web-messages-${it.id}"] = pluginId
            if(it is GridContainerDescription){
                GridWebEditorGenerator.generateEditor(it,destPlugin, projectName, generatedFiles, context)
            }
            if(it is NavigatorDescription){
                WebNavigatorGenerator.generateEditor(it,destPlugin, projectName, generatedFiles)
            }
            if(it is TileSpaceDescription){
                WebTileSpaceGenerator.generateEditor(it,destPlugin, projectName, generatedFiles,context)
            }

        }
        GenUtils.generateClasses(classesData, destPlugin, projectName, generatedFiles)
        generateActionsIds(registry,destPlugin, generatedFiles)
        generateOptionsGroupsIds(registry,destPlugin, generatedFiles, context)
        generateUiReflectionUtils(registry, classes, destPlugin, projectName, generatedFiles, context)
    }

    private fun toGenData(descr: VVEntityDescription): BaseGenData {
        val result = GenClassData(descr.id+"JS", (descr.extendsId?:BaseVV::class.qualifiedName)+"JS", false, true)
        descr.properties.values.forEach { prop ->
            result.properties.add(GenPropertyDescription(prop.id, getPropertyType(prop.type), getClassName(prop.type, prop.className),lateinit = false, nonNullable = false))
        }
        descr.collections.values.forEach { coll ->
            result.collections.add(GenCollectionDescription(coll.id, getPropertyType(coll.elementType), getClassName(coll.elementType, coll.elementClassName)))
        }
        return result
    }

    private fun getClassName(type: VVCollectionType, className: String?): String? {
        return when(type){
            VVCollectionType.ENTITY-> className+"JS"
        }
    }

    private fun getPropertyType(elementType: VVCollectionType): GenPropertyType {
        return when(elementType){
            VVCollectionType.ENTITY-> GenPropertyType.ENTITY
        }
    }

    private fun getClassName(type: VVPropertyType, className: String?): String? {
        return when(type){
            VVPropertyType.STRING-> "String"
            VVPropertyType.ENTITY -> className+"JS"
        }
    }

    private fun getPropertyType(type: VVPropertyType): GenPropertyType {
        return when(type){
            VVPropertyType.STRING -> GenPropertyType.STRING
            VVPropertyType.ENTITY -> GenPropertyType.ENTITY
        }
    }

    private fun toGenData(descr: VSEntityDescription): BaseGenData {
        val result = GenClassData(descr.id+"JS", (descr.extendsId?:BaseVS::class.qualifiedName)+"JS", false, true)
        descr.properties.values.forEach { prop ->
            result.properties.add(GenPropertyDescription(prop.id, getPropertyType(prop.type), getClassName(prop.type, prop.className),lateinit = prop.lateInit, nonNullable = false, useBuilder = true))
        }
        descr.collections.values.forEach { coll ->
            result.collections.add(GenCollectionDescription(coll.id, getPropertyType(coll.elementType), getClassName(coll.elementType, coll.elementClassName)))
        }
        return result
    }

    private fun getClassName(type: VSCollectionType, className: String?): String? {
        return when(type){
            VSCollectionType.ENTITY-> className+"JS"
        }
    }

    private fun getPropertyType(elementType: VSCollectionType): GenPropertyType {
        return when(elementType){
            VSCollectionType.ENTITY-> GenPropertyType.ENTITY
        }
    }

    private fun getClassName(type: VSPropertyType, className: String?): String? {
        return when(type){
            VSPropertyType.TEXT_BOX_SETTINGS-> "${TextBoxConfiguration::class.qualifiedName}JS"
            VSPropertyType.PASSWORD_BOX_SETTINGS->"${PasswordBoxConfiguration::class.qualifiedName}JS"
            VSPropertyType.ENTITY -> "${className}JS"
            VSPropertyType.FLOAT_NUMBER_BOX_SETTINGS -> "${BigDecimalBoxConfiguration::class.qualifiedName}JS"
            VSPropertyType.INTEGER_NUMBER_BOX_SETTINGS ->"${IntegerNumberBoxConfiguration::class.qualifiedName}JS"
            VSPropertyType.BOOLEAN_BOX_SETTINGS -> "${BooleanBoxConfiguration::class.qualifiedName}JS"
            VSPropertyType.ENTITY_SELECT_BOX_SETTINGS ->"${EntitySelectBoxConfiguration::class.qualifiedName}JS"
            VSPropertyType.ENUM_SELECT_BOX_SETTINGS -> "${EnumSelectBoxConfiguration::class.qualifiedName}JS"
            VSPropertyType.DATE_BOX_SETTINGS -> "${DateBoxConfiguration::class.qualifiedName}JS"
            VSPropertyType.DATE_TIME_BOX_SETTINGS -> "${DateTimeBoxConfiguration::class.qualifiedName}JS"
            VSPropertyType.STRING -> null
            VSPropertyType.GENERAL_SELECT_BOX_SETTINGS -> "${GeneralSelectBoxConfiguration::class.qualifiedName}JS"
        }
    }

    private fun getPropertyType(type: VSPropertyType): GenPropertyType {
        return when(type){
            VSPropertyType.TEXT_BOX_SETTINGS-> GenPropertyType.ENTITY
            VSPropertyType.PASSWORD_BOX_SETTINGS-> GenPropertyType.ENTITY
            VSPropertyType.ENTITY -> GenPropertyType.ENTITY
            VSPropertyType.FLOAT_NUMBER_BOX_SETTINGS -> GenPropertyType.ENTITY
            VSPropertyType.INTEGER_NUMBER_BOX_SETTINGS-> GenPropertyType.ENTITY
            VSPropertyType.BOOLEAN_BOX_SETTINGS -> GenPropertyType.ENTITY
            VSPropertyType.ENTITY_SELECT_BOX_SETTINGS -> GenPropertyType.ENTITY
            VSPropertyType.ENUM_SELECT_BOX_SETTINGS -> GenPropertyType.ENTITY
            VSPropertyType.DATE_BOX_SETTINGS -> GenPropertyType.ENTITY
            VSPropertyType.DATE_TIME_BOX_SETTINGS -> GenPropertyType.ENTITY
            VSPropertyType.STRING -> GenPropertyType.STRING
            VSPropertyType.GENERAL_SELECT_BOX_SETTINGS -> GenPropertyType.ENTITY
        }
    }

    private fun toGenData(descr: VMEntityDescription): BaseGenData {
        val result = GenClassData(descr.id+"JS", (descr.extendsId?:BaseVM::class.qualifiedName)+"JS", false, true)
        descr.properties.values.forEach { prop ->
            result.properties.add(GenPropertyDescription(prop.id, getPropertyType(prop.type), getClassName(prop.type, prop.className),lateinit = prop.lateInit, nonNullable = prop.nonNullable))
        }
        descr.collections.values.forEach { coll ->
            result.collections.add(GenCollectionDescription(coll.id, getPropertyType(coll.elementType), getClassName(coll.elementClassName)))
        }
        return result
    }

    private fun getPropertyType(type: VMCollectionType): GenPropertyType {
        return when(type){
            VMCollectionType.ENTITY -> GenPropertyType.ENTITY
        }
    }

    private fun getClassName(type:VMPropertyType, className: String?): String? {
        return when(type){
            VMPropertyType.STRING -> null
            VMPropertyType.ENUM -> getClassName(className)
            VMPropertyType.SELECT -> getClassName(className)
            VMPropertyType.LONG -> null
            VMPropertyType.INT -> null
            VMPropertyType.BIG_DECIMAL -> null
            VMPropertyType.ENTITY_REFERENCE -> getClassName(ObjectReference::class.qualifiedName)
            VMPropertyType.LOCAL_DATE_TIME -> "kotlin.js.Date"
            VMPropertyType.LOCAL_DATE ->  "kotlin.js.Date"
            VMPropertyType.ENTITY -> getClassName(className)
            VMPropertyType.BOOLEAN -> null
        }
    }

    private fun getClassName(className: String?): String? {
        return className+"JS"
    }

    private fun getPropertyType(type: VMPropertyType): GenPropertyType {
        return when(type){
            VMPropertyType.STRING -> GenPropertyType.STRING
            VMPropertyType.ENUM   -> GenPropertyType.ENUM
            VMPropertyType.SELECT  -> GenPropertyType.ENTITY
            VMPropertyType.LONG  -> GenPropertyType.LONG
            VMPropertyType.INT  -> GenPropertyType.INT
            VMPropertyType.BIG_DECIMAL  -> GenPropertyType.DOUBLE
            VMPropertyType.ENTITY_REFERENCE   -> GenPropertyType.ENTITY
            VMPropertyType.LOCAL_DATE_TIME  -> GenPropertyType.ENTITY
            VMPropertyType.LOCAL_DATE   -> GenPropertyType.ENTITY
            VMPropertyType.ENTITY  -> GenPropertyType.ENTITY
            VMPropertyType.BOOLEAN  -> GenPropertyType.BOOLEAN
        }
    }

    private fun generateUiReflectionUtils(registry: UiMetaRegistry, classes:List<String>, destPlugin: File, projectName: String, generatedFiles: MutableList<File>, context: MutableMap<String, Any>) {
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
                registry.displayHandlers.values.forEach {
                        "com.gridnine.jasmine.web.core.reflection.ReflectionFactoryJS.get().registerClass(\"${it.className}\", {${it.className}()})"()
                        "com.gridnine.jasmine.web.core.reflection.ReflectionFactoryJS.get().registerQualifiedName(${it.className}::class, \"${it.className}\")"()
                }
                classes.forEach {
                    "com.gridnine.jasmine.web.core.reflection.ReflectionFactoryJS.get().registerClass(\"$it\", {$it()})"()
                    "com.gridnine.jasmine.web.core.reflection.ReflectionFactoryJS.get().registerQualifiedName($it::class, \"$it\")"()
                }
                registry.enums.values.forEach {
                    "com.gridnine.jasmine.web.core.reflection.ReflectionFactoryJS.get().registerEnum(\"${it.id}JS\", {${it.id}JS.valueOf(it)})"()
                    "com.gridnine.jasmine.web.core.reflection.ReflectionFactoryJS.get().registerQualifiedName(${it.id}JS::class, \"${it.id}JS\")"()
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
    private fun generateOptionsGroupsIds(registry: UiMetaRegistry, destPlugin: File, generatedFiles: MutableList<File>, context: MutableMap<String, Any>) {
        if(registry.optionsGroups.isEmpty()){
            return
        }
        val pluginId = destPlugin.name
        val map =context.get(PluginAssociationsGenerator.WEB_MAP_KEY) as MutableMap<String,String>
        registry.optionsGroups.values.forEach {group ->
            group.options.forEach { option ->
                map["options-${group.id}-${option.id}"] = pluginId
            }
        }
        val sb = StringBuilder()
        GenUtils.generateHeader(sb, "${pluginId}.OptionsGroupsIds", pluginId)
        GenUtils.classBuilder(sb, "object OptionsIds") {
            registry.optionsGroups.keys.forEach {groupId->
                blankLine()
                """val ${groupId.replace(".","_").replace("-","_")} = "$groupId" """()
            }
        }
        GenUtils.saveFile(destPlugin, "OptionsGroupsIds", sb.toString(), generatedFiles)
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