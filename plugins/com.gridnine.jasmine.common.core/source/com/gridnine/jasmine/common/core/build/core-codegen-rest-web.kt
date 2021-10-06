/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST")

package com.gridnine.jasmine.common.core.build

import com.gridnine.jasmine.common.core.meta.*
import com.gridnine.jasmine.common.core.model.*
import com.gridnine.jasmine.common.core.parser.RestMetadataParser
import java.io.File


class WebRestGenerator : CodeGenerator {

    private fun toGenData(descr: RestEntityDescription): GenClassData {

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

    private fun getClassName(type: RestPropertyType, className: String?): String? {
        return when (type) {
            RestPropertyType.STRING -> null
            RestPropertyType.BIG_DECIMAL -> "Double"
            RestPropertyType.BOOLEAN -> null
            RestPropertyType.ENTITY_REFERENCE -> "${ObjectReference::class.qualifiedName}JS"
            RestPropertyType.ENUM -> className + "JS"
            RestPropertyType.INT -> null
            RestPropertyType.LONG -> null
            RestPropertyType.LOCAL_DATE -> "kotlin.js.Date"
            RestPropertyType.LOCAL_DATE_TIME -> "kotlin.js.Date"
            RestPropertyType.ENTITY -> "${className}JS"
            RestPropertyType.BYTE_ARRAY -> "ByteArray"
        }
    }


    private fun getPropertyType(type: RestPropertyType): GenPropertyType {
        return when (type) {
            RestPropertyType.STRING -> GenPropertyType.STRING
            RestPropertyType.BIG_DECIMAL -> GenPropertyType.ENTITY
            RestPropertyType.BOOLEAN -> GenPropertyType.BOOLEAN
            RestPropertyType.ENTITY_REFERENCE -> GenPropertyType.ENTITY
            RestPropertyType.ENUM -> GenPropertyType.ENUM
            RestPropertyType.INT -> GenPropertyType.INT
            RestPropertyType.LOCAL_DATE -> GenPropertyType.ENTITY
            RestPropertyType.LOCAL_DATE_TIME -> GenPropertyType.ENTITY
            RestPropertyType.LONG -> GenPropertyType.LONG
            RestPropertyType.ENTITY -> GenPropertyType.ENTITY
            RestPropertyType.BYTE_ARRAY -> GenPropertyType.ENTITY
        }
    }

    override fun generate(destPlugin: File, sources: List<Pair<File, String?>>, projectName: String, generatedFiles: MutableList<File>, context: MutableMap<String, Any>) {
        val registry = RestMetaRegistry()
        sources.forEach { source ->
            RestMetadataParser.updateRestMetaRegistry(registry, source.first)
        }
        val pluginId = destPlugin.name
        val mapping = context[PluginAssociationsGenerator.WEB_MAP_KEY] as HashMap<String, String>
        val classesData = arrayListOf<BaseGenData>()
        val enums = arrayListOf<String>()
        val entities = arrayListOf<String>()
        registry.enums.values.forEach {
            val enumClassData = GenEnumData(it.id + "JS")
            it.items.values.forEach { ei ->
                enumClassData.enumItems.add(ei.id)
            }
            classesData.add(enumClassData)
            mapping[it.id + "JS"] = pluginId
            enums.add(enumClassData.id)
        }
        registry.entities.values.forEach {
            val docClassData = toGenData(it)
            classesData.add(docClassData)
            mapping[it.id + "JS"] = pluginId
            if(!it.isAbstract) {
                entities.add(docClassData.id)
            }
        }
        GenUtils.generateClasses(classesData, destPlugin, projectName, generatedFiles)
        run {
            val sb = StringBuilder()
            GenUtils.generateHeader(sb, "${pluginId}.Reflection", projectName)

            GenUtils.classBuilder(sb, "object RestReflectionUtilsJS") {
                blankLine()
                "fun registerWebRestClasses()"{
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

            val file = File(destPlugin, "source-gen/${pluginId.replace(".", "/")}/RestReflectionUtils.kt")
            if (!file.parentFile.exists()) {
                file.parentFile.mkdirs()
            }
            val content = sb.toString().toByteArray()
            if (!file.exists() || !content.contentEquals(file.readBytes())) {
                file.writeBytes(content)
            }
            generatedFiles.add(file)
        }
        run {
            val operationsMap = linkedMapOf<String, MutableList<RestOperationDescription>>()
            registry.operations.values.forEach {
                val restId = registry.rests[registry.groups[it.groupId]!!.restId]!!.id
                operationsMap.getOrPut(restId) { arrayListOf() }.add(it)
            }
            operationsMap.entries.forEach { entry ->
                val sb = StringBuilder()
                GenUtils.generateHeader(sb, "${pluginId}.RestClient", projectName)
                GenUtils.classBuilder(sb, "object ${entry.key.capitalize()}RestClient") {
                    entry.value.forEach {
                        "suspend fun ${it.id}(request:${it.requestEntity}JS): ${it.responseEntity}JS"{
                            "return com.gridnine.jasmine.web.core.remote.RpcManager.get().post(\"${it.id}\",request)"()
                        }
                    }
                }
                val file = File(destPlugin, "source-gen/${pluginId.replace(".", "/")}/${entry.key.capitalize()}RestClient.kt")
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
}
