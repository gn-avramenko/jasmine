/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
package com.gridnine.jasmine.server.core.build

import com.gridnine.jasmine.server.core.app.IApplicationMetadataProvider
import com.gridnine.jasmine.server.core.model.domain.*
import java.io.File


internal object DomainServerGenerator {

    private fun <T : BaseDocumentDescription> toGenData(descr: T): GenClassData {
        val extendsId = when {
            descr.extendsId != null -> descr.extendsId
            descr is DocumentDescription -> BaseDocument::class.java.name
            else -> BaseNestedDocument::class.java.name
        }
        val result = GenClassData(descr.id, extendsId, descr.isAbstract, enum = false, noEnumProperties = true)
        descr.properties.values.forEach { prop ->
            result.properties.add(GenPropertyDescription(prop.id, getPropertyType(prop.type), prop.className, nonNullable = prop.notNullable, lateinit = false))
        }
        descr.collections.values.forEach { coll ->
            result.collections.add(GenCollectionDescription(coll.id, getPropertyType(coll.elementType), coll.elementClassName))
        }
        result.codeInjections.addAll(descr.codeInjections)

        return result
    }

    private fun <T : BaseIndexDescription> fillGenData(descr: T, data: GenClassData) {
        descr.properties.values.forEach { prop ->
            data.properties.add(GenPropertyDescription(prop.id, getPropertyType(prop.type), prop.className))
        }
        descr.collections.values.forEach { coll ->
            data.collections.add(GenCollectionDescription(coll.id, getPropertyType(coll.elementType), coll.elementClassName))
        }
    }

    private fun getPropertyType(type: DatabaseCollectionType): GenPropertyType {
        return when (type) {
            DatabaseCollectionType.STRING -> GenPropertyType.STRING
            DatabaseCollectionType.ENTITY_REFERENCE -> GenPropertyType.ENTITY_REFERENCE
            DatabaseCollectionType.ENUM -> GenPropertyType.ENUM
        }
    }

    private fun getPropertyType(type: DatabasePropertyType): GenPropertyType {
        return when (type) {
            DatabasePropertyType.STRING -> GenPropertyType.STRING
            DatabasePropertyType.TEXT -> GenPropertyType.STRING
            DatabasePropertyType.BIG_DECIMAL -> GenPropertyType.BIG_DECIMAL
            DatabasePropertyType.BOOLEAN -> GenPropertyType.BOOLEAN
            DatabasePropertyType.ENTITY_REFERENCE -> GenPropertyType.ENTITY_REFERENCE
            DatabasePropertyType.ENUM -> GenPropertyType.ENUM
            DatabasePropertyType.INT -> GenPropertyType.INT
            DatabasePropertyType.LOCAL_DATE -> GenPropertyType.LOCAL_DATE
            DatabasePropertyType.LOCAL_DATE_TIME -> GenPropertyType.LOCAL_DATE_TIME
            DatabasePropertyType.LONG -> GenPropertyType.LONG
        }
    }

    private fun getPropertyType(type: DocumentPropertyType): GenPropertyType {
        return when (type) {
            DocumentPropertyType.STRING -> GenPropertyType.STRING
            DocumentPropertyType.BYTE_ARRAY -> GenPropertyType.BYTE_ARRAY
            DocumentPropertyType.BIG_DECIMAL -> GenPropertyType.BIG_DECIMAL
            DocumentPropertyType.BOOLEAN -> GenPropertyType.BOOLEAN
            DocumentPropertyType.NESTED_DOCUMENT -> GenPropertyType.ENTITY
            DocumentPropertyType.ENTITY_REFERENCE -> GenPropertyType.ENTITY_REFERENCE
            DocumentPropertyType.ENUM -> GenPropertyType.ENUM
            DocumentPropertyType.INT -> GenPropertyType.INT
            DocumentPropertyType.LOCAL_DATE -> GenPropertyType.LOCAL_DATE
            DocumentPropertyType.LOCAL_DATE_TIME -> GenPropertyType.LOCAL_DATE_TIME
            DocumentPropertyType.LONG -> GenPropertyType.LONG
        }
    }

    fun generateDomainClasses(projectDir: File, metadataProvider: IApplicationMetadataProvider, generatedFiles: MutableMap<String, MutableList<File>>, projectName: String) {
        val domainData = linkedMapOf<String, MutableList<File>>()
        metadataProvider.getExtensions("domain-metadata").forEach {
            domainData.getOrPut(it.getParameters("dest-server-plugin-id").first(), { arrayListOf() })
                    .add(File(projectDir, it.getParameters("file").first()))
            Unit
        }
        domainData.entries.forEach { (key, value) ->
            val registry = DomainMetaRegistry()
            value.forEach { metaFile -> DomainMetadataParser.updateDomainMetaRegistry(registry, metaFile) }
            val classesData = arrayListOf<GenClassData>()
            registry.enums.values.forEach {
                val enumClassData = GenClassData(it.id, null, abstract = false, enum = true, noEnumProperties = true)
                it.items.values.forEach { ei ->
                    enumClassData.enumItems.add(ei.id)
                }
                classesData.add(enumClassData)
            }
            registry.documents.values.forEach {
                val docClassData = toGenData(it)
                classesData.add(docClassData)
            }
            registry.nestedDocuments.values.forEach {
                val docClassData = toGenData(it)
                classesData.add(docClassData)
            }
            registry.indexes.values.forEach {
                val data = GenClassData(it.id, "${BaseIndex::class.qualifiedName}<${it.document}>", abstract = false, enum = false, noEnumProperties = false)
                fillGenData(it, data)
                classesData.add(data)
            }
            registry.assets.values.forEach {
                val data = GenClassData(it.id, BaseAsset::class.qualifiedName, abstract = false, enum = false, noEnumProperties = false)
                fillGenData(it, data)
                classesData.add(data)
            }
            GenUtils.generateClasses(classesData, File(projectDir, "plugins/$key"), projectName,  generatedFiles.getOrPut(key, { arrayListOf()}))
        }
    }


}