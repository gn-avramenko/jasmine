/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")
package com.gridnine.jasmine.common.core.build

import com.gridnine.jasmine.common.core.meta.*
import com.gridnine.jasmine.common.core.model.*
import com.gridnine.jasmine.common.core.parser.MiscMetadataParser
import java.io.File


class CommonMiscGenerator:CodeGenerator {

    private fun  toGenData(descr: MiscEntityDescription): GenClassData {

        val result = GenClassData(descr.id, if(descr.extendsId != null) descr.extendsId else BaseIntrospectableObject::class.qualifiedName, descr.isAbstract, true)
        descr.properties.values.forEach { prop ->
            result.properties.add(GenPropertyDescription(prop.id, getPropertyType(prop.type), getClassName(prop.className),lateinit = prop.lateinit, nonNullable = prop.nonNullable))
        }
        descr.collections.values.forEach { coll ->
            result.collections.add(GenCollectionDescription(coll.id, getPropertyType(coll.elementType), getClassName(coll.elementClassName)))
        }
        descr.maps.values.forEach {map ->
            result.maps.add(GenMapDescription(map.id, getPropertyType(map.keyClassType), getClassName(map.keyClassName), getPropertyType(map.valueClassType), getClassName(map.valueClassName)))
        }
        return result
    }

    private fun getClassName(elementClassName: String?): String? {
        if(BaseIndex::class.qualifiedName == elementClassName){
            return "${BaseIndex::class.qualifiedName}<${BaseDocument::class.qualifiedName}>"
        }

        return elementClassName
    }


    private fun getPropertyType(type: MiscFieldType): GenPropertyType {
        return when (type) {
            MiscFieldType.STRING->  GenPropertyType.STRING
            MiscFieldType.BYTE_ARRAY ->GenPropertyType.BYTE_ARRAY
            MiscFieldType.BIG_DECIMAL ->GenPropertyType.BIG_DECIMAL
            MiscFieldType.BOOLEAN ->GenPropertyType.BOOLEAN
            MiscFieldType.ENTITY ->GenPropertyType.ENTITY
            MiscFieldType.ENTITY_REFERENCE ->GenPropertyType.ENTITY_REFERENCE
            MiscFieldType.ENUM ->GenPropertyType.ENUM
            MiscFieldType.INT ->GenPropertyType.INT
            MiscFieldType.LOCAL_DATE ->GenPropertyType.LOCAL_DATE
            MiscFieldType.LOCAL_DATE_TIME ->GenPropertyType.LOCAL_DATE_TIME
            MiscFieldType.LONG ->GenPropertyType.LONG
            MiscFieldType.CLASS -> GenPropertyType.CLASS
        }
    }

    override fun generate(destPlugin: File, sources: List<Pair<File, String?>>, projectName: String, generatedFiles: MutableList<File>, context: MutableMap<String, Any>) {
        val registry = MiscMetaRegistry()
        sources.forEach { source ->
            MiscMetadataParser.updateMiscMetaRegistry(registry, source.first)
        }
        val classesData = arrayListOf<BaseGenData>()
        registry.enums.values.forEach {
            val enumClassData = GenEnumData(it.id)
            it.items.values.forEach { ei ->
                enumClassData.enumItems.add(ei.id)
            }
            enumClassData.codeInjections.add("""
                override fun toString():String{
                    return ${MiscMetaRegistry::class.qualifiedName}.get().enums["${it.id}"]?.items?.get(name)?.getDisplayName()?:name
                }
            """.trimIndent())
            classesData.add(enumClassData)
        }
        registry.entities.values.forEach {
            val docClassData = toGenData(it)
            classesData.add(docClassData)
        }
        GenUtils.generateClasses(classesData, destPlugin, projectName,  generatedFiles)
    }

}