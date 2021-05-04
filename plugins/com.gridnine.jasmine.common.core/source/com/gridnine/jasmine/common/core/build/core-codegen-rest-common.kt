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


class CommonRestGenerator:CodeGenerator {

    private fun  toGenData(descr: RestEntityDescription): GenClassData {

        val result = GenClassData(descr.id, if(descr.extendsId != null) descr.extendsId else BaseRestEntity::class.qualifiedName, descr.isAbstract, true)
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

    override fun generate(destPlugin: File, sources: List<Pair<File, String?>>, projectName: String, generatedFiles: MutableList<File>, context: MutableMap<String, Any>) {
        val registry = RestMetaRegistry()
        sources.forEach { source ->
            RestMetadataParser.updateRestMetaRegistry(registry, source.first)
        }
        val classesData = arrayListOf<BaseGenData>()
        val mapping = context[PluginAssociationsGenerator.COMMON_MAP_KEY] as HashMap<String,String>
        val pluginId = destPlugin.name
        registry.enums.values.forEach {
            val enumClassData = GenEnumData(it.id)
            it.items.values.forEach { ei ->
                enumClassData.enumItems.add(ei.id)
            }
            classesData.add(enumClassData)
            mapping[it.id] = pluginId
        }
        registry.entities.values.forEach {
            val docClassData = toGenData(it)
            classesData.add(docClassData)
            mapping[it.id] = pluginId
        }
        GenUtils.generateClasses(classesData, destPlugin, projectName,  generatedFiles)
    }

}