/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST")

package com.gridnine.jasmine.common.core.build

import com.gridnine.jasmine.common.core.meta.*
import com.gridnine.jasmine.common.core.model.BaseAsset
import com.gridnine.jasmine.common.core.model.BaseIndex
import com.gridnine.jasmine.common.core.model.ObjectReference
import com.gridnine.jasmine.common.core.parser.DomainMetadataParser
import java.io.File


class WebDomainGenerator : CodeGenerator {


    private fun <T : BaseIndexDescription> toGenData(descr: T): GenClassData {
        val extendsId = if (descr is IndexDescription) "${BaseIndex::class.qualifiedName}JS" else "${BaseAsset::class.qualifiedName}JS"
        val result = GenClassData(descr.id + "JS", extendsId, abstract = false, noEnumProperties = true, open = false)
        descr.properties.values.forEach { prop ->
            result.properties.add(GenPropertyDescription(prop.id, getPropertyType(prop.type), getClassName(prop.type, prop.className)))
        }
        descr.collections.values.forEach { coll ->
            result.collections.add(GenCollectionDescription(coll.id, getPropertyType(coll.elementType), coll.elementClassName))
        }
        if (descr is IndexDescription) {
            result.codeInjections.add("""
            companion object{
                val objectId = "${descr.document}"
                val indexId = "${descr.id}JS"
            }
        """.trimIndent())
        }
        return result
    }


    private fun getClassName(type: DatabasePropertyType, className: String?): String? {
        return when (type) {
            DatabasePropertyType.STRING, DatabasePropertyType.TEXT -> null
            DatabasePropertyType.BIG_DECIMAL -> "Double"
            DatabasePropertyType.BOOLEAN -> null
            DatabasePropertyType.ENTITY_REFERENCE -> "${ObjectReference::class.qualifiedName}JS"
            DatabasePropertyType.ENUM -> className + "JS"
            DatabasePropertyType.INT -> null
            DatabasePropertyType.LONG -> null
            DatabasePropertyType.LOCAL_DATE -> "kotlin.js.Date"
            DatabasePropertyType.LOCAL_DATE_TIME -> "kotlin.js.Date"
        }
    }

    private fun getPropertyType(type: DatabaseCollectionType): GenPropertyType {
        return when (type) {
            DatabaseCollectionType.STRING -> return GenPropertyType.STRING
            DatabaseCollectionType.ENTITY_REFERENCE -> GenPropertyType.ENTITY
            DatabaseCollectionType.ENUM -> GenPropertyType.ENTITY
        }
    }

    private fun getPropertyType(type: DatabasePropertyType): GenPropertyType {
        return when (type) {
            DatabasePropertyType.STRING, DatabasePropertyType.TEXT -> GenPropertyType.STRING
            DatabasePropertyType.BIG_DECIMAL -> GenPropertyType.ENTITY
            DatabasePropertyType.BOOLEAN -> GenPropertyType.BOOLEAN
            DatabasePropertyType.ENTITY_REFERENCE -> GenPropertyType.ENTITY
            DatabasePropertyType.ENUM -> GenPropertyType.ENUM
            DatabasePropertyType.INT -> GenPropertyType.INT
            DatabasePropertyType.LOCAL_DATE -> GenPropertyType.ENTITY
            DatabasePropertyType.LOCAL_DATE_TIME -> GenPropertyType.ENTITY
            DatabasePropertyType.LONG -> GenPropertyType.LONG
        }
    }



    override fun generate(destPlugin: File, sources: List<Pair<File, String?>>, projectName: String, generatedFiles: MutableList<File>, context: MutableMap<String, Any>) {
        val registry = DomainMetaRegistry()
        sources.forEach { source ->
            DomainMetadataParser.updateDomainMetaRegistry(registry, source.first)
        }
        val classesData = arrayListOf<BaseGenData>()
        val classes = arrayListOf<String>()
        val enums = arrayListOf<String>()
        val pluginId = destPlugin.name
        val mapping = context[PluginAssociationsGenerator.WEB_MAP_KEY] as HashMap<String,String>
        registry.enums.values.forEach {
            val enumClassData = GenEnumData(it.id + "JS")
            it.items.values.forEach { ei ->
                enumClassData.enumItems.add(ei.id)
            }
            classesData.add(enumClassData)
            enums.add(it.id + "JS")
            mapping[it.id+"JS"] = pluginId
        }
        registry.indexes.values.forEach {
            val data = toGenData(it)
            classesData.add(data)
            classes.add(it.id + "JS")
            mapping[it.id+"JS"] = pluginId
        }
        registry.assets.values.forEach {
            val data = toGenData(it)
            classesData.add(data)
            classes.add(it.id + "JS")
            mapping[it.id+"JS"] = pluginId
        }
        GenUtils.generateClasses(classesData, destPlugin, projectName,  generatedFiles)

        val sb = StringBuilder()
        GenUtils.generateHeader(sb, "${pluginId}.Reflection", projectName)
        GenUtils.classBuilder(sb, "object DomainReflectionUtilsJS") {
            blankLine()
            "fun registerWebDomainClasses()"{
                enums.forEach {
                    "com.gridnine.jasmine.web.core.reflection.ReflectionFactoryJS.get().registerEnum(\"$it\", {$it.valueOf(it)})"()
                }
                classes.forEach {
                    "com.gridnine.jasmine.web.core.reflection.ReflectionFactoryJS.get().registerClass(\"$it\", {$it()})"()
                }
                classes.forEach {
                    "com.gridnine.jasmine.web.core.reflection.ReflectionFactoryJS.get().registerQualifiedName($it::class, \"$it\")"()
                }
                registry.enums.values.forEach {
                    val className = it.id + "JS"
                    "com.gridnine.jasmine.web.core.reflection.ReflectionFactoryJS.get().registerQualifiedName($className::class, \"$className\")"()
                }
            }
        }
        val file = File(destPlugin, "source-gen/${pluginId.replace(".", "/")}/DomainReflectionUtils.kt")
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