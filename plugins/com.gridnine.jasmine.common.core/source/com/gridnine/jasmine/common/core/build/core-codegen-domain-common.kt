/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("UNCHECKED_CAST")

package com.gridnine.jasmine.common.core.build

import com.gridnine.jasmine.common.core.app.Environment
import com.gridnine.jasmine.common.core.meta.*
import com.gridnine.jasmine.common.core.model.*
import com.gridnine.jasmine.common.core.parser.DomainMetadataParser
import java.io.File


@Suppress("unused")
class CommonDomainGenerator : CodeGenerator {

    private fun <T : BaseDocumentDescription> toCachedGenData(descr: T, registry: DomainMetaRegistry): GenClassData {
        val result = GenClassData("${descr.id.substringBeforeLast(".")}._Cached${descr.id.substringAfterLast(".")}", descr.id, false, noEnumProperties = true, implementCachedObject = true)
        result.properties.add(GenPropertyDescription(CachedObject.allowChanges, GenPropertyType.BOOLEAN, null, nonNullable = true, lateinit = false, openSetter = false, override = true))
        result.properties.add(GenPropertyDescription(BaseIdentity.uid, GenPropertyType.STRING, null, nonNullable = true, lateinit = false, openSetter = false, override = true, disallowedSetter = true))
        var extendsId = descr.extendsId
        while (extendsId != null) {
            val nestedDoc = registry.nestedDocuments[extendsId]!!
            nestedDoc.properties.values.forEach { prop ->
                result.properties.add(GenPropertyDescription(prop.id, getPropertyType(prop.type), prop.className, nonNullable = (prop.nonNullable && isSimpleType(prop.type)), lateinit = (prop.nonNullable && !isSimpleType(prop.type)), openSetter = false, override = true, disallowedSetter = true))
            }
            nestedDoc.collections.values.forEach { coll ->
                result.collections.add(GenCollectionDescription(coll.id, getPropertyType(coll.elementType), coll.elementClassName, openGetter = false, readonlyImpl = true))
            }
            result.codeInjections.addAll(nestedDoc.codeInjections)
            extendsId = nestedDoc.extendsId
        }
        descr.properties.values.forEach { prop ->
            result.properties.add(GenPropertyDescription(prop.id, getPropertyType(prop.type), prop.className, nonNullable = prop.nonNullable, lateinit = false, openSetter = false, override = true, disallowedSetter = true))
        }
        descr.collections.values.forEach { coll ->
            result.collections.add(GenCollectionDescription(coll.id, getPropertyType(coll.elementType), coll.elementClassName, openGetter = false, readonlyImpl = true))
        }
        result.codeInjections.addAll(descr.codeInjections)
        return result
    }

    private fun toCachedGenData(descr: AssetDescription): GenClassData {
        val result = GenClassData("${descr.id.substringBeforeLast(".")}._Cached${descr.id.substringAfterLast(".")}", descr.id, false, noEnumProperties = true, implementCachedObject = true)
        result.properties.add(GenPropertyDescription(BaseIdentity.uid, GenPropertyType.STRING, null, nonNullable = true, lateinit = false, openSetter = false, override = true, disallowedSetter = true))
        result.properties.add(GenPropertyDescription(CachedObject.allowChanges, GenPropertyType.BOOLEAN, null, nonNullable = true, lateinit = false, openSetter = false, override = true))
        descr.properties.values.forEach { prop ->
            result.properties.add(GenPropertyDescription(prop.id, getPropertyType(prop.type), prop.className, nonNullable = prop.nonNullable, lateinit = false, openSetter = false, override = true, disallowedSetter = true))
        }
        descr.collections.values.forEach { coll ->
            result.collections.add(GenCollectionDescription(coll.id, getPropertyType(coll.elementType), coll.elementClassName, openGetter = false, readonlyImpl = true))
        }
        result.codeInjections.addAll(descr.codeInjections)
        return result
    }

    private fun <T : BaseDocumentDescription> toGenData(descr: T, cached: Boolean): GenClassData {

        val extendsId = when {
            descr.extendsId != null -> descr.extendsId
            descr is DocumentDescription -> BaseDocument::class.java.name
            else -> BaseNestedDocument::class.java.name
        }
        val result = GenClassData(descr.id, extendsId, descr.isAbstract, noEnumProperties = true, open = cached)
        descr.properties.values.forEach { prop ->
            result.properties.add(GenPropertyDescription(prop.id, getPropertyType(prop.type) , prop.className, nonNullable = (prop.nonNullable && isSimpleType(prop.type)), lateinit = (prop.nonNullable && !isSimpleType(prop.type)), openSetter = cached))
        }
        descr.collections.values.forEach { coll ->
            result.collections.add(GenCollectionDescription(coll.id, getPropertyType(coll.elementType), coll.elementClassName, openGetter = cached))
        }
        result.codeInjections.addAll(descr.codeInjections)

        return result
    }

    private fun <T : BaseIndexDescription> fillGenData(descr: T, data: GenClassData, cached: Boolean) {
        descr.properties.values.forEach { prop ->
            data.properties.add(GenPropertyDescription(prop.id, getPropertyType(prop.type), prop.className, openSetter = cached))
        }
        descr.collections.values.forEach { coll ->
            data.collections.add(GenCollectionDescription(coll.id, getPropertyType(coll.elementType), coll.elementClassName, openGetter = cached))
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

    private fun isSimpleType(type: DocumentPropertyType): Boolean {
        return when (type) {
            DocumentPropertyType.STRING -> true
            DocumentPropertyType.BYTE_ARRAY -> false
            DocumentPropertyType.BIG_DECIMAL -> false
            DocumentPropertyType.BOOLEAN -> true
            DocumentPropertyType.NESTED_DOCUMENT -> false
            DocumentPropertyType.ENTITY_REFERENCE -> false
            DocumentPropertyType.ENUM -> false
            DocumentPropertyType.INT -> true
            DocumentPropertyType.LOCAL_DATE -> false
            DocumentPropertyType.LOCAL_DATE_TIME -> false
            DocumentPropertyType.LONG -> true
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


    private fun updateCached(it: BaseDocumentDescription, registry: DomainMetaRegistry, cachedEntities: HashSet<String>) {
        it.properties.values.forEach {
            if (it.type == DocumentPropertyType.NESTED_DOCUMENT) {
                cachedEntities.add(it.className!!)
                updateCached(registry.nestedDocuments[it.className!!]!!, registry, cachedEntities)
            }
        }
        it.collections.values.forEach {
            if (it.elementType == DocumentPropertyType.NESTED_DOCUMENT) {
                cachedEntities.add(it.elementClassName!!)
                updateCached(registry.nestedDocuments[it.elementClassName!!]!!, registry, cachedEntities)
            }
        }
        if (it.isAbstract) {
            registry.nestedDocuments.values.forEach { ndd ->
                if (ndd.extendsId == it.id) {
                    cachedEntities.add(ndd.id)
                    updateCached(ndd, registry, cachedEntities)
                }
            }
        }
    }

    override fun generate(destPlugin: File, sources: List<Pair<File, String?>>, projectName: String, generatedFiles: MutableList<File>, context: MutableMap<String, Any>) {
        val totalRegistry = context.computeIfAbsent(DOMAIN_REGISTRY_KEY) { DomainMetaRegistry() } as DomainMetaRegistry
        val registry = DomainMetaRegistry()
        sources.forEach { metaFile -> DomainMetadataParser.updateDomainMetaRegistry(registry, metaFile.first) }
        sources.forEach { metaFile -> DomainMetadataParser.updateDomainMetaRegistry(totalRegistry, metaFile.first) }
        val classesData = arrayListOf<BaseGenData>()
        val mapping = context[PluginAssociationsGenerator.COMMON_MAP_KEY] as HashMap<String,String>
        val pluginId = destPlugin.name
        registry.enums.values.forEach {
            val enumClassData = GenEnumData(it.id)
            it.items.values.forEach { ei ->
                enumClassData.enumItems.add(ei.id)
            }
            enumClassData.codeInjections.add("""
                override fun toString():String{
                    return ${DomainMetaRegistry::class.qualifiedName}.get().enums["${it.id}"]?.items?.get(name)?.getDisplayName()?:name
                }
            """.trimIndent())
            classesData.add(enumClassData)
            mapping[it.id] = pluginId
        }
        val cachedEntities = hashSetOf<String>()
        registry.documents.values.forEach {
            if (it.parameters["x-cache-resolve"] == "true") {
                cachedEntities.add(it.id)
                updateCached(it, registry, cachedEntities)
            }
        }

        registry.documents.values.forEach {
            val docClassData = toGenData(it, cachedEntities.contains(it.id))
            classesData.add(docClassData)
            if (cachedEntities.contains(it.id) && !it.isAbstract) {
                val docClassData2 = toCachedGenData(it, registry)
                classesData.add(docClassData2)
            }
        }
        registry.nestedDocuments.values.forEach {
            val docClassData = toGenData(it, cachedEntities.contains(it.id))
            classesData.add(docClassData)
            if (cachedEntities.contains(it.id) && !it.isAbstract) {
                val docClassData2 = toCachedGenData(it, registry)
                classesData.add(docClassData2)
            }
        }
        registry.indexes.values.forEach {
            val data = GenClassData(it.id, "${BaseIndex::class.qualifiedName}<${it.document}>", abstract = false, noEnumProperties = false)
            fillGenData(it, data, false)
            classesData.add(data)
            mapping[it.id] = pluginId
        }
        registry.assets.values.forEach {

            val cached = run {
                if (it.parameters["x-cache-resolve"] == "true") {
                    true
                } else {
                    it.properties.values.find { param -> param.parameters["x-cache-find"] != null } != null
                }
            }
            if (cached) {
                val data2 = toCachedGenData(it)
                classesData.add(data2)
            }
            val data = GenClassData(it.id, BaseAsset::class.qualifiedName, abstract = false, noEnumProperties = false, open = cached)
            fillGenData(it, data, cached)
            classesData.add(data)
            mapping[it.id] = pluginId
        }
        GenUtils.generateClasses(classesData, destPlugin, projectName, generatedFiles)
        if(!Environment.isPublished(DomainMetaRegistry::class)){
            Environment.publish(totalRegistry)
        }
    }

    companion object {
        const val DOMAIN_REGISTRY_KEY = "DOMAIN_REGISTRY_KEY"
    }

}