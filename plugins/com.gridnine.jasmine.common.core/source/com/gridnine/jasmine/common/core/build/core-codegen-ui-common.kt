/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("UNCHECKED_CAST")

package com.gridnine.jasmine.common.core.build

import com.gridnine.jasmine.common.core.meta.*
import com.gridnine.jasmine.common.core.model.*
import com.gridnine.jasmine.common.core.parser.UiMetadataParser
import java.io.File


@Suppress("unused")
class UiCommonGenerator: CodeGenerator {

    private fun toGenData(descr: VVEntityDescription): BaseGenData {
        val result = GenClassData(descr.id, descr.extendsId?:BaseVV::class.qualifiedName, abstract = false, noEnumProperties = true)
        descr.properties.values.forEach { prop ->
            result.properties.add(GenPropertyDescription(prop.id, getPropertyType(prop.type), getClassName(prop.type, prop.className),lateinit = prop.lateInit, nonNullable = false))
        }
        descr.collections.values.forEach { coll ->
            result.collections.add(GenCollectionDescription(coll.id, getPropertyType(coll.elementType), getClassName(coll.elementType, coll.elementClassName)))
        }
        return result
    }

    private fun getClassName(type: VVCollectionType, className: String?): String? {
        return when(type){
            VVCollectionType.ENTITY-> className
        }
    }

    private fun getPropertyType(elementType: VVCollectionType): GenPropertyType {
        return when(elementType){
            VVCollectionType.ENTITY-> GenPropertyType.ENTITY
        }
    }

    private fun getClassName(type: VVPropertyType, className: String?): String? {
        return when(type){
            VVPropertyType.STRING-> "String"
            VVPropertyType.ENTITY -> className
        }
    }

    private fun getPropertyType(type: VVPropertyType): GenPropertyType {
        return when(type){
            VVPropertyType.STRING -> GenPropertyType.STRING
            VVPropertyType.ENTITY -> GenPropertyType.ENTITY
        }
    }

    private fun toGenData(descr: VSEntityDescription): BaseGenData {
        val result = GenClassData(descr.id, descr.extendsId?:BaseVS::class.qualifiedName, abstract = false, noEnumProperties = true)
        descr.properties.values.forEach { prop ->
            result.properties.add(GenPropertyDescription(prop.id, getPropertyType(prop.type), getClassName(prop.type, prop.className),lateinit = prop.lateInit, nonNullable = false, useBuilder = true))
        }
        descr.collections.values.forEach { coll ->
            result.collections.add(GenCollectionDescription(coll.id, getPropertyType(coll.elementType), getClassName(coll.elementType, coll.elementClassName)))
        }
        return result
    }

    private fun getClassName(type: VSCollectionType, className: String?): String? {
        return when(type){
            VSCollectionType.ENTITY-> className
        }
    }

    private fun getPropertyType(elementType: VSCollectionType): GenPropertyType {
        return when(elementType){
            VSCollectionType.ENTITY-> GenPropertyType.ENTITY
        }
    }

    private fun getClassName(type: VSPropertyType, className: String?): String? {
        return when(type){
            VSPropertyType.TEXT_BOX_SETTINGS-> TextBoxConfiguration::class.qualifiedName
            VSPropertyType.PASSWORD_BOX_SETTINGS-> PasswordBoxConfiguration::class.qualifiedName
            VSPropertyType.ENTITY-> className
            VSPropertyType.FLOAT_NUMBER_BOX_SETTINGS -> BigDecimalBoxConfiguration::class.qualifiedName
            VSPropertyType.INTEGER_NUMBER_BOX_SETTINGS -> IntegerNumberBoxConfiguration::class.qualifiedName
            VSPropertyType.BOOLEAN_BOX_SETTINGS -> BooleanBoxConfiguration::class.qualifiedName
            VSPropertyType.ENTITY_SELECT_BOX_SETTINGS -> EntitySelectBoxConfiguration::class.qualifiedName
            VSPropertyType.ENUM_SELECT_BOX_SETTINGS -> EnumSelectBoxConfiguration::class.qualifiedName
            VSPropertyType.DATE_BOX_SETTINGS -> DateBoxConfiguration::class.qualifiedName
            VSPropertyType.DATE_TIME_BOX_SETTINGS -> DateTimeBoxConfiguration::class.qualifiedName
            VSPropertyType.STRING -> null
            VSPropertyType.GENERAL_SELECT_BOX_SETTINGS -> GeneralSelectBoxConfiguration::class.qualifiedName
        }
    }

    private fun getPropertyType(type: VSPropertyType): GenPropertyType {
        return when(type){
            VSPropertyType.TEXT_BOX_SETTINGS-> GenPropertyType.ENTITY
            VSPropertyType.PASSWORD_BOX_SETTINGS-> GenPropertyType.ENTITY
            VSPropertyType.ENTITY -> GenPropertyType.ENTITY
            VSPropertyType.FLOAT_NUMBER_BOX_SETTINGS ->  GenPropertyType.ENTITY
            VSPropertyType.INTEGER_NUMBER_BOX_SETTINGS ->  GenPropertyType.ENTITY
            VSPropertyType.BOOLEAN_BOX_SETTINGS ->  GenPropertyType.ENTITY
            VSPropertyType.ENTITY_SELECT_BOX_SETTINGS ->  GenPropertyType.ENTITY
            VSPropertyType.ENUM_SELECT_BOX_SETTINGS ->  GenPropertyType.ENTITY
            VSPropertyType.DATE_BOX_SETTINGS ->  GenPropertyType.ENTITY
            VSPropertyType.DATE_TIME_BOX_SETTINGS ->  GenPropertyType.ENTITY
            VSPropertyType.STRING -> GenPropertyType.STRING
            VSPropertyType.GENERAL_SELECT_BOX_SETTINGS -> GenPropertyType.ENTITY
        }
    }

    private fun toGenData(descr: VMEntityDescription): BaseGenData {
        val result = GenClassData(descr.id, descr.extendsId?:BaseVM::class.qualifiedName, abstract = false, noEnumProperties = true)
        descr.properties.values.forEach { prop ->
            result.properties.add(GenPropertyDescription(prop.id, getPropertyType(prop.type), getClassName(prop.className),lateinit = prop.lateInit, nonNullable = prop.nonNullable))
        }
        descr.collections.values.forEach { coll ->
            result.collections.add(GenCollectionDescription(coll.id, getPropertyType(coll.elementType), getClassName(coll.elementClassName)))
        }
        return result
    }

    private fun getPropertyType(type: VMCollectionType): GenPropertyType {
        return when(type){
            VMCollectionType.ENTITY -> GenPropertyType.ENTITY
        }
    }

    private fun getClassName(className: String?): String? {
        return className
    }

    private fun getPropertyType(type: VMPropertyType): GenPropertyType {
        return when(type){
            VMPropertyType.STRING -> GenPropertyType.STRING
            VMPropertyType.ENUM   -> GenPropertyType.ENUM
            VMPropertyType.SELECT  -> GenPropertyType.ENTITY
            VMPropertyType.LONG  -> GenPropertyType.LONG
            VMPropertyType.INT  -> GenPropertyType.INT
            VMPropertyType.BIG_DECIMAL  -> GenPropertyType.BIG_DECIMAL
            VMPropertyType.ENTITY_REFERENCE  -> GenPropertyType.ENTITY_REFERENCE
            VMPropertyType.LOCAL_DATE_TIME  -> GenPropertyType.LOCAL_DATE_TIME
            VMPropertyType.LOCAL_DATE  -> GenPropertyType.LOCAL_DATE
            VMPropertyType.ENTITY  -> GenPropertyType.ENTITY
            VMPropertyType.BOOLEAN  -> GenPropertyType.BOOLEAN
        }
    }

    override fun generate(destPlugin: File, sources: List<Pair<File, String?>>, projectName: String, generatedFiles: MutableList<File>, context: MutableMap<String, Any>) {
        val registry = UiMetaRegistry()
        sources.forEach { metaFile -> UiMetadataParser.updateUiMetaRegistry(registry, metaFile.first) }
        val commonMapping = context[PluginAssociationsGenerator.COMMON_MAP_KEY] as HashMap<String,String>
        val classesData = arrayListOf<BaseGenData>()
        val pluginId = destPlugin.name
        registry.enums.values.forEach {
            val enumClassData = GenEnumData(it.id)
            it.items.values.forEach { ei ->
                enumClassData.enumItems.add(ei.id)
            }
            classesData.add(enumClassData)
            commonMapping[enumClassData.id] = pluginId
        }
        registry.viewModels.values.forEach {vmd ->
            classesData.add(toGenData(vmd))
        }
        registry.viewSettings.values.forEach {vmd ->
            classesData.add(toGenData(vmd))
        }
        registry.viewValidations.values.forEach {vmd ->
            classesData.add(toGenData(vmd))
        }
        GenUtils.generateClasses(classesData, destPlugin, projectName,  generatedFiles)
    }
}