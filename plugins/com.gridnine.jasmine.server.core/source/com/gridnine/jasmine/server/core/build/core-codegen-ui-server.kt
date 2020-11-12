/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
package com.gridnine.jasmine.server.core.build

import com.gridnine.jasmine.server.core.app.IApplicationMetadataProvider
import com.gridnine.jasmine.server.core.model.common.BaseIdentity
import com.gridnine.jasmine.server.core.model.common.Xeption
import com.gridnine.jasmine.server.core.model.domain.*
import com.gridnine.jasmine.server.core.model.rest.BaseRestEntity
import com.gridnine.jasmine.server.core.model.ui.*
import java.io.File


internal object UiServerGenerator {

    fun generateUiClasses(metadataProvider: IApplicationMetadataProvider, generatedFiles: MutableMap<String, MutableList<File>>, pluginsLocation:Map<String,File>, projectName: String){
        val uiData = linkedMapOf<String, MutableList<File>>()
        metadataProvider.getExtensions("ui-metadata").forEach {
            uiData.getOrPut(it.getParameters("dest-server-plugin-id").first(), { arrayListOf() })
                    .add(File(pluginsLocation[it.plugin.pluginId], it.getParameters("relative-path").first()))
            Unit
        }
        uiData.entries.forEach { (key, value) ->
            val registry = UiMetaRegistry()
            value.forEach { metaFile -> UiMetadataParser.updateUiMetaRegistry(registry, metaFile) }
            val classesData = arrayListOf<BaseGenData>()
            registry.enums.values.forEach {
                val enumClassData = GenEnumData(it.id)
                it.items.values.forEach { ei ->
                    enumClassData.enumItems.add(ei.id)
                }
                classesData.add(enumClassData)
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
            GenUtils.generateClasses(classesData, pluginsLocation[key]?: throw Xeption.forDeveloper("unable to find basedir of plugin $key"), projectName,  generatedFiles.getOrPut(key, { arrayListOf()}))
        }
    }
    private fun toGenData(descr: VVEntityDescription): BaseGenData {
        val result = GenClassData(descr.id, BaseVV::class.qualifiedName, false, true)
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
        val result = GenClassData(descr.id, BaseVS::class.qualifiedName, false, true)
        descr.properties.values.forEach { prop ->
            result.properties.add(GenPropertyDescription(prop.id, getPropertyType(prop.type), getClassName(prop.type, prop.className),lateinit = prop.lateInit, nonNullable = false))
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
        }
    }

    private fun getPropertyType(type: VSPropertyType): GenPropertyType {
        return when(type){
            VSPropertyType.TEXT_BOX_SETTINGS-> GenPropertyType.ENTITY
            VSPropertyType.PASSWORD_BOX_SETTINGS-> GenPropertyType.ENTITY
            VSPropertyType.ENTITY -> GenPropertyType.ENTITY
        }
    }

    private fun toGenData(descr: VMEntityDescription): BaseGenData {
        val result = GenClassData(descr.id, BaseVM::class.qualifiedName, false, true)
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


}