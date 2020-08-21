/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
package com.gridnine.jasmine.server.core.build

import com.gridnine.jasmine.server.core.app.IApplicationMetadataProvider
import com.gridnine.jasmine.server.core.model.common.BaseIdentity
import com.gridnine.jasmine.server.core.model.common.Xeption
import com.gridnine.jasmine.server.core.model.domain.*
import java.io.File


object DomainWebGenerator {

    private fun <T : BaseDocumentDescription> toGenData(descr: T): GenClassData {

        val extendsId = when {
            descr.extendsId != null -> descr.extendsId+"JS"
            descr is DocumentDescription -> "${BaseDocument::class.qualifiedName}JS"
            else -> "${BaseIdentity::class.qualifiedName}JS"
        }
        val result = GenClassData(descr.id+"JS", extendsId, descr.isAbstract,  noEnumProperties = true, open = false)
        descr.properties.values.forEach { prop ->
            result.properties.add(GenPropertyDescription(prop.id, getPropertyType(prop.type), getClassName(prop.type, prop.className), nonNullable = prop.nonNullable, lateinit = false, openSetter = false))
        }
        descr.collections.values.forEach { coll ->
            result.collections.add(GenCollectionDescription(coll.id, getPropertyType(coll.elementType), getClassName(coll.elementType, coll.elementClassName), openGetter = false))
        }

        return result
    }

    private fun <T : BaseIndexDescription> toGenData(descr: T): GenClassData {
        val extendsId = if (descr is IndexDescription) "${BaseIndex::class.qualifiedName}JS" else "${BaseAsset::class.qualifiedName}JS"
        val result = GenClassData(descr.id + "JS", extendsId, abstract = false, noEnumProperties = false, open = false)
        descr.properties.values.forEach { prop ->
            result.properties.add(GenPropertyDescription(prop.id, getPropertyType(prop.type), getClassName(prop.type, prop.className)))
        }
        descr.collections.values.forEach { coll ->
            result.collections.add(GenCollectionDescription(coll.id, getPropertyType(coll.elementType), coll.elementClassName))
        }
        return result
    }

    private fun getClassName(type: DocumentPropertyType, className: String?): String? {
        return when (type) {
            DocumentPropertyType.STRING -> null
            DocumentPropertyType.BIG_DECIMAL -> "Double"
            DocumentPropertyType.BOOLEAN -> null
            DocumentPropertyType.ENTITY_REFERENCE -> "${ObjectReference::class.qualifiedName}JS"
            DocumentPropertyType.ENUM -> className + "JS"
            DocumentPropertyType.INT -> null
            DocumentPropertyType.LONG -> null
            DocumentPropertyType.LOCAL_DATE -> "kotlin.js.Date"
            DocumentPropertyType.LOCAL_DATE_TIME -> "kotlin.js.Date"
            DocumentPropertyType.BYTE_ARRAY -> "kotlin.js.ByteArray"
            DocumentPropertyType.NESTED_DOCUMENT -> "${className}JS"
        }
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

    private fun getPropertyType(type: DocumentPropertyType): GenPropertyType {
        return when (type) {
            DocumentPropertyType.STRING -> GenPropertyType.STRING
            DocumentPropertyType.BIG_DECIMAL -> GenPropertyType.ENTITY
            DocumentPropertyType.BOOLEAN -> GenPropertyType.BOOLEAN
            DocumentPropertyType.ENTITY_REFERENCE -> GenPropertyType.ENTITY
            DocumentPropertyType.ENUM -> GenPropertyType.ENUM
            DocumentPropertyType.INT -> GenPropertyType.INT
            DocumentPropertyType.LOCAL_DATE -> GenPropertyType.ENTITY
            DocumentPropertyType.LOCAL_DATE_TIME -> GenPropertyType.ENTITY
            DocumentPropertyType.LONG -> GenPropertyType.LONG
            DocumentPropertyType.BYTE_ARRAY -> GenPropertyType.BYTE_ARRAY
            DocumentPropertyType.NESTED_DOCUMENT -> GenPropertyType.ENTITY
        }
    }

    fun generateDomainClasses(metadataProvider: IApplicationMetadataProvider, generatedFiles: MutableMap<String, MutableList<File>>,  pluginsLocation:Map<String,File>, projectName: String, totalRegistry:DomainMetaRegistry) {
        val domainData = linkedMapOf<String, MutableList<File>>()
        metadataProvider.getExtensions("domain-metadata").forEach {
            val destClientPlugin = it.getParameters("dest-client-plugin-id").firstOrNull()
            if (destClientPlugin != null) {
                domainData.getOrPut(destClientPlugin, { arrayListOf() })
                        .add(File(pluginsLocation[it.plugin.pluginId], it.getParameters("relative-path").first()))
            }
            Unit
        }
        domainData.entries.forEach { (key, value) ->
            val registry = DomainMetaRegistry()
            value.forEach { metaFile -> DomainMetadataParser.updateDomainMetaRegistry(registry, metaFile) }
            val classesData = arrayListOf<BaseGenData>()
            val classes = arrayListOf<String>()
            val enums = arrayListOf<String>()
            registry.enums.values.forEach {
                val enumClassData = GenEnumData(it.id + "JS")
                it.items.values.forEach { ei ->
                    enumClassData.enumItems.add(ei.id)
                }
                classesData.add(enumClassData)
                enums.add(it.id + "JS")
            }
            registry.indexes.values.forEach {
                val data = toGenData(it)
                classesData.add(data)
                classes.add(it.id + "JS")
            }
            registry.assets.values.forEach {
                val data = toGenData(it)
                classesData.add(data)
                classes.add(it.id + "JS")
            }
            registry.documents.values.forEach {
                if(totalRegistry.documents[it.id]?.parameters?.get(DomainMetaRegistry.EXPOSED_IN_REST_KEY) == "true"){
                    val docClassData = toGenData(it)
                    classesData.add(docClassData)
                    if(!it.isAbstract) {
                        classes.add(it.id + "JS")
                    }
                }
            }
            registry.nestedDocuments.values.forEach {
                if(totalRegistry.nestedDocuments[it.id]?.parameters?.get(DomainMetaRegistry.EXPOSED_IN_REST_KEY) == "true"){
                    val docClassData = toGenData(it)
                    classesData.add(docClassData)
                    if(!it.isAbstract) {
                        classes.add(it.id + "JS")
                    }
                }
            }
            GenUtils.generateClasses(classesData, pluginsLocation[key]?: throw Xeption.forDeveloper("unable to find basedir of plugin $key"), projectName, generatedFiles.getOrPut(key, { arrayListOf() }))
            val sb = StringBuilder()
            GenUtils.generateHeader(sb, "${key}.Reflection", projectName)

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
            val file = File(pluginsLocation[key]?: throw Xeption.forDeveloper("unable to find basedir of plugin $key"), "source-gen/${key.replace(".", "/")}/DomainReflectionUtils.kt")
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