/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
package com.gridnine.jasmine.server.core.build

import com.gridnine.jasmine.server.core.app.IApplicationMetadataProvider
import com.gridnine.jasmine.server.core.model.domain.*
import java.io.File


object DomainWebGenerator {

    private fun <T : BaseIndexDescription> toGenData(descr: T): GenClassData {
        val extendsId = if (descr is IndexDescription) "com.gridnine.jasmine.web.core.model.domain.BaseIndexJS" else "com.gridnine.jasmine.web.core.model.domaon.BaseAssetJS"
        val result = GenClassData(descr.id + "JS", extendsId, false, false, true)
        descr.properties.values.forEach { prop ->
            result.properties.add(GenPropertyDescription(prop.id, getPropertyType(prop.type), getClassName(prop.type, prop.className)))
        }
        descr.collections.values.forEach { coll ->
            result.collections.add(GenCollectionDescription(coll.id, getPropertyType(coll.elementType), coll.elementClassName))
        }
        return result
    }

    private fun getClassName(type: DatabasePropertyType, className: String?): String? {
        return when (type) {
            DatabasePropertyType.STRING, DatabasePropertyType.TEXT -> null
            DatabasePropertyType.BIG_DECIMAL -> "Double"
            DatabasePropertyType.BOOLEAN -> null
            DatabasePropertyType.ENTITY_REFERENCE -> "com.gridnine.jasmine.web.core.model.domain.EntityReferenceJS"
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

    fun generateDomainClasses(projectDir: File, metadataProvider: IApplicationMetadataProvider, generatedFiles: MutableMap<String, MutableList<File>>, projectName: String) {
        val domainData = linkedMapOf<String, MutableList<File>>()
        metadataProvider.getExtensions("domain-metadata").forEach {
            val destClientPlugin = it.getParameters("dest-client-plugin-id").firstOrNull()
            if (destClientPlugin != null) {
                domainData.getOrPut(destClientPlugin, { arrayListOf() })
                        .add(File(projectDir, it.getParameters("file").first()))
            }
            Unit
        }
        domainData.entries.forEach { (key, value) ->
            val registry = DomainMetaRegistry()
            value.forEach { metaFile -> DomainMetadataParser.updateDomainMetaRegistry(registry, metaFile) }
            val classesData = arrayListOf<GenClassData>()
            val classes = arrayListOf<String>()
            val enums = arrayListOf<String>()
            registry.enums.values.forEach {
                val enumClassData = GenClassData(it.id + "JS", extends = null, abstract = false, enum = true, noEnumProperties = true)
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
            GenUtils.generateClasses(classesData, File(projectDir, "plugins/$key"), projectName, generatedFiles.getOrPut(key, { arrayListOf() }))
            val sb = StringBuilder()
            GenUtils.generateHeader(sb, "${key}.Reflection", projectName, false)

            GenUtils.classBuilder(sb, "object DomainReflectionUtils") {
                blankLine()
                "fun registerWebDomainClasses()"{
                    enums.forEach {
                        "com.gridnine.jasmine.web.core.utils.ReflectionFactoryJS.get().registerEnum(\"$it\", {$it.valueOf(it)})"()
                    }
                    classes.forEach {
                        "com.gridnine.jasmine.web.core.utils.ReflectionFactoryJS.get().registerClass(\"$it\", {$it()})"()
                    }
                    classes.forEach {
                        "com.gridnine.jasmine.web.core.utils.ReflectionFactoryJS.get().registerQualifiedName($it::class, \"$it\")"()
                    }
                    registry.enums.values.forEach {
                        val className = it.id + "JS"
                        "com.gridnine.jasmine.web.core.utils.ReflectionFactoryJS.get().registerQualifiedName($className::class, \"$className\")"()
                    }
                }
            }
            val file = File(projectDir, "plugins/$key/source-gen/${key.replace(".", "/")}/DomainReflectionUtils.kt")
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