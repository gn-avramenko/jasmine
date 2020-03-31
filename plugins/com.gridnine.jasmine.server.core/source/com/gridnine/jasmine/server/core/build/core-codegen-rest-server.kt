/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")
package com.gridnine.jasmine.server.core.build

import com.gridnine.jasmine.server.core.app.IApplicationMetadataProvider
import com.gridnine.jasmine.server.core.model.domain.*
import com.gridnine.jasmine.server.core.model.rest.*
import java.io.File


object RestServerGenerator {

    private fun  toGenData(descr: RestEntityDescription): GenClassData {

        val result = GenClassData(descr.id, if(descr.extends != null) descr.extends else BaseRestEntity::class.qualifiedName, descr.abstract, false, true)
        descr.properties.values.forEach { prop ->
            result.properties.add(GenPropertyDescription(prop.id, getPropertyType(prop.type), getClassName(prop.className),lateinit = prop.lateinit, nonNullable = prop.nonNullable))
        }
        descr.collections.values.forEach { coll ->
            result.collections.add(GenCollectionDescription(coll.id, getPropertyType(coll.elementType), getClassName(coll.elementClassName)))
        }
        return result
    }

    private fun getClassName(elementClassName: String?): String? {
        if(BaseIndex::class.qualifiedName == elementClassName){
            return "${BaseIndex::class.qualifiedName}<${BaseDocument::class.qualifiedName}>"
        }

        return elementClassName
    }


    private fun getPropertyType(type: RestPropertyType): GenPropertyType {
        return when (type) {
            RestPropertyType.STRING->  GenPropertyType.STRING
            RestPropertyType.BYTE_ARRAY ->GenPropertyType.BYTE_ARRAY
            RestPropertyType.BIG_DECIMAL ->GenPropertyType.BIG_DECIMAL
            RestPropertyType.BOOLEAN ->GenPropertyType.BOOLEAN
            RestPropertyType.ENTITY ->GenPropertyType.ENTITY
            RestPropertyType.ENTITY_REFERENCE ->GenPropertyType.ENTITY_REFERENCE
            RestPropertyType.ENUM ->GenPropertyType.ENUM
            RestPropertyType.INT ->GenPropertyType.INT
            RestPropertyType.LOCAL_DATE ->GenPropertyType.LOCAL_DATE
            RestPropertyType.LOCAL_DATE_TIME ->GenPropertyType.LOCAL_DATE_TIME
            RestPropertyType.LONG ->GenPropertyType.LONG
        }
    }

    fun generateServerRest(projectDir: File, metadataProvider: IApplicationMetadataProvider, generatedFiles: MutableMap<String, MutableList<File>>, projectName: String) {
        val domainData = linkedMapOf<String, MutableList<File>>()
        metadataProvider.getExtensions("rest-metadata").forEach {
            domainData.getOrPut(it.getParameters("dest-server-plugin-id").first(), { arrayListOf() })
                    .add(File(projectDir, it.getParameters("file").first()))
            Unit
        }
        domainData.entries.forEach { (key, value) ->
            val registry = RestMetaRegistry()
            value.forEach { metaFile -> RestMetadataParser.updateRestMetaRegistry(registry, metaFile) }
            val classesData = arrayListOf<GenClassData>()
            registry.enums.values.forEach {
                val enumClassData = GenClassData(it.id, null, abstract = false, enum = true, noEnumProperties = true)
                it.items.values.forEach { ei ->
                    enumClassData.enumItems.add(ei.id)
                }
                classesData.add(enumClassData)
            }
            registry.entities.values.forEach {
                val docClassData = toGenData(it)
                classesData.add(docClassData)
            }
            GenUtils.generateClasses(classesData, File(projectDir, "plugins/$key"), projectName,  generatedFiles.getOrPut(key, { arrayListOf()}))
        }
    }

}