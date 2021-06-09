/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST")

package com.gridnine.jasmine.common.core.build

import com.gridnine.jasmine.common.core.meta.*
import com.gridnine.jasmine.common.core.model.*
import com.gridnine.jasmine.common.core.parser.MiscMetadataParser
import com.gridnine.jasmine.common.core.parser.RestMetadataParser
import java.io.File


class WebMiscGenerator : CodeGenerator {

    private fun toGenData(descr: MiscEntityDescription): GenClassData {

        val result = GenClassData(descr.id + "JS", (if (descr.extendsId != null) descr.extendsId else BaseRestEntity::class.qualifiedName) + "JS", descr.isAbstract, true)
        descr.properties.values.forEach { prop ->
            result.properties.add(GenPropertyDescription(prop.id, getPropertyType(prop.type), getClassName(prop.type, prop.className), lateinit = prop.lateinit, nonNullable = prop.nonNullable))
        }
        descr.collections.values.forEach { coll ->
            result.collections.add(GenCollectionDescription(coll.id, getPropertyType(coll.elementType), getClassName(coll.elementType, coll.elementClassName)))
        }
        descr.maps.values.forEach { map ->
            result.maps.add(GenMapDescription(map.id, getPropertyType(map.keyClassType), getClassName(map.keyClassType, map.keyClassName),getPropertyType(map.valueClassType), getClassName(map.valueClassType, map.valueClassName) ))
        }
        return result
    }

    private fun getClassName(type: MiscFieldType, className: String?): String? {
        return when (type) {
            MiscFieldType.STRING -> null
            MiscFieldType.BIG_DECIMAL -> "Double"
            MiscFieldType.BOOLEAN -> null
            MiscFieldType.ENTITY_REFERENCE -> "${ObjectReference::class.qualifiedName}JS"
            MiscFieldType.ENUM -> className + "JS"
            MiscFieldType.INT -> null
            MiscFieldType.LONG -> null
            MiscFieldType.LOCAL_DATE -> "kotlin.js.Date"
            MiscFieldType.LOCAL_DATE_TIME -> "kotlin.js.Date"
            MiscFieldType.ENTITY -> "${className}JS"
            MiscFieldType.BYTE_ARRAY -> "kotlin.js.ByteArray"
            MiscFieldType.CLASS -> null
        }
    }


    private fun getPropertyType(type: MiscFieldType): GenPropertyType {
        return when (type) {
            MiscFieldType.STRING -> GenPropertyType.STRING
            MiscFieldType.BIG_DECIMAL -> GenPropertyType.ENTITY
            MiscFieldType.BOOLEAN -> GenPropertyType.BOOLEAN
            MiscFieldType.ENTITY_REFERENCE -> GenPropertyType.ENTITY
            MiscFieldType.ENUM -> GenPropertyType.ENUM
            MiscFieldType.INT -> GenPropertyType.INT
            MiscFieldType.LOCAL_DATE -> GenPropertyType.ENTITY
            MiscFieldType.LOCAL_DATE_TIME -> GenPropertyType.ENTITY
            MiscFieldType.LONG -> GenPropertyType.LONG
            MiscFieldType.ENTITY -> GenPropertyType.ENTITY
            MiscFieldType.BYTE_ARRAY -> GenPropertyType.ENTITY
            MiscFieldType.CLASS -> GenPropertyType.STRING
        }
    }

    override fun generate(destPlugin: File, sources: List<Pair<File, String?>>, projectName: String, generatedFiles: MutableList<File>, context: MutableMap<String, Any>) {
        val registry = MiscMetaRegistry()
        sources.forEach { source ->
            MiscMetadataParser.updateMiscMetaRegistry(registry, source.first)
        }
        val pluginId = destPlugin.name
        val mapping = context[PluginAssociationsGenerator.WEB_MAP_KEY] as HashMap<String, String>
        val classesData = arrayListOf<BaseGenData>()
        val enums = arrayListOf<String>()
        val entities = arrayListOf<String>()
        registry.enums.values.forEach {
            if(it.exposedAtRest) {
                val enumClassData = GenEnumData(it.id + "JS")
                it.items.values.forEach { ei ->
                    enumClassData.enumItems.add(ei.id)
                }
                classesData.add(enumClassData)
                mapping[it.id + "JS"] = pluginId
                enums.add(enumClassData.id)
            }
        }
        registry.entities.values.forEach {
            if(it.exposedAtRest) {
                val docClassData = toGenData(it)
                classesData.add(docClassData)
                mapping[it.id + "JS"] = pluginId
                if (!it.isAbstract) {
                    entities.add(docClassData.id)
                }
            }
        }
        GenUtils.generateClasses(classesData, destPlugin, projectName, generatedFiles)
        run {
            val sb = StringBuilder()
            GenUtils.generateHeader(sb, "${pluginId}.Reflection", projectName)

            GenUtils.classBuilder(sb, "object MiscReflectionUtilsJS") {
                blankLine()
                "fun registerWebMiscClasses()"{
                    enums.forEach {
                        "com.gridnine.jasmine.web.core.reflection.ReflectionFactoryJS.get().registerEnum(\"$it\", {$it.valueOf(it)})"()
                        "com.gridnine.jasmine.web.core.reflection.ReflectionFactoryJS.get().registerQualifiedName($it::class, \"$it\")"()
                    }
                    entities.forEach {
                        "com.gridnine.jasmine.web.core.reflection.ReflectionFactoryJS.get().registerClass(\"$it\", {$it()})"()
                        "com.gridnine.jasmine.web.core.reflection.ReflectionFactoryJS.get().registerQualifiedName($it::class, \"$it\")"()
                    }
                }
            }

            val file = File(destPlugin, "source-gen/${pluginId.replace(".", "/")}/MiscReflectionUtilsJS.kt")
            if (!file.parentFile.exists()) {
                file.parentFile.mkdirs()
            }
            val content = sb.toString().toByteArray()
            if (!file.exists() || !content.contentEquals(file.readBytes())) {
                file.writeBytes(content)
            }
            generatedFiles.add(file)
        }
    }
}
