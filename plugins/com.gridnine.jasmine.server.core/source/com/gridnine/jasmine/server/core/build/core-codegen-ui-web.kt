/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
package com.gridnine.jasmine.server.core.build

import com.gridnine.jasmine.server.core.app.IApplicationMetadataProvider
import com.gridnine.jasmine.server.core.model.common.Xeption
import com.gridnine.jasmine.server.core.model.domain.*
import com.gridnine.jasmine.server.core.model.ui.*
import java.io.File


object UiWebGenerator {


    private fun toGenData(descr: VVEntityDescription): BaseGenData {
        val result = GenClassData(descr.id+"JS", (descr.extendsId?:BaseVV::class.qualifiedName)+"JS", false, true)
        descr.properties.values.forEach { prop ->
            result.properties.add(GenPropertyDescription(prop.id, getPropertyType(prop.type), getClassName(prop.type, prop.className),lateinit = false, nonNullable = false))
        }
        descr.collections.values.forEach { coll ->
            result.collections.add(GenCollectionDescription(coll.id, getPropertyType(coll.elementType), getClassName(coll.elementType, coll.elementClassName)))
        }
        return result
    }

    private fun getClassName(type: VVCollectionType, className: String?): String? {
        return when(type){
            VVCollectionType.ENTITY-> className+"JS"
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
            VVPropertyType.ENTITY -> className+"JS"
        }
    }

    private fun getPropertyType(type: VVPropertyType): GenPropertyType {
        return when(type){
            VVPropertyType.STRING -> GenPropertyType.STRING
            VVPropertyType.ENTITY -> GenPropertyType.ENTITY
        }
    }

    private fun toGenData(descr: VSEntityDescription): BaseGenData {
        val result = GenClassData(descr.id+"JS", (descr.extendsId?:BaseVS::class.qualifiedName)+"JS", false, true)
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
            VSCollectionType.ENTITY-> className+"JS"
        }
    }

    private fun getPropertyType(elementType: VSCollectionType): GenPropertyType {
        return when(elementType){
            VSCollectionType.ENTITY-> GenPropertyType.ENTITY
        }
    }

    private fun getClassName(type: VSPropertyType, className: String?): String? {
        return when(type){
            VSPropertyType.TEXT_BOX_SETTINGS-> "${TextBoxConfiguration::class.qualifiedName}JS"
            VSPropertyType.PASSWORD_BOX_SETTINGS->"${PasswordBoxConfiguration::class.qualifiedName}JS"
            VSPropertyType.ENTITY -> "${className}JS"
            VSPropertyType.FLOAT_NUMBER_BOX_SETTINGS -> "${FloatNumberBoxConfiguration::class.qualifiedName}JS"
            VSPropertyType.INTEGER_NUMBER_BOX_SETTINGS ->"${IntegerNumberBoxConfiguration::class.qualifiedName}JS"
            VSPropertyType.BOOLEAN_BOX_SETTINGS -> "${BooleanBoxConfiguration::class.qualifiedName}JS"
            VSPropertyType.ENTITY_SELECT_BOX_SETTINGS ->"${EntitySelectBoxConfiguration::class.qualifiedName}JS"
            VSPropertyType.ENUM_SELECT_BOX_SETTINGS -> "${EnumSelectBoxConfiguration::class.qualifiedName}JS"
            VSPropertyType.DATE_BOX_SETTINGS -> "${DateBoxConfiguration::class.qualifiedName}JS"
            VSPropertyType.DATE_TIME_BOX_SETTINGS -> "${DateTimeBoxConfiguration::class.qualifiedName}JS"
            VSPropertyType.STRING -> null
        }
    }

    private fun getPropertyType(type: VSPropertyType): GenPropertyType {
        return when(type){
            VSPropertyType.TEXT_BOX_SETTINGS-> GenPropertyType.ENTITY
            VSPropertyType.PASSWORD_BOX_SETTINGS-> GenPropertyType.ENTITY
            VSPropertyType.ENTITY -> GenPropertyType.ENTITY
            VSPropertyType.FLOAT_NUMBER_BOX_SETTINGS -> GenPropertyType.ENTITY
            VSPropertyType.INTEGER_NUMBER_BOX_SETTINGS-> GenPropertyType.ENTITY
            VSPropertyType.BOOLEAN_BOX_SETTINGS -> GenPropertyType.ENTITY
            VSPropertyType.ENTITY_SELECT_BOX_SETTINGS -> GenPropertyType.ENTITY
            VSPropertyType.ENUM_SELECT_BOX_SETTINGS -> GenPropertyType.ENTITY
            VSPropertyType.DATE_BOX_SETTINGS -> GenPropertyType.ENTITY
            VSPropertyType.DATE_TIME_BOX_SETTINGS -> GenPropertyType.ENTITY
            VSPropertyType.STRING -> GenPropertyType.STRING
        }
    }

    private fun toGenData(descr: VMEntityDescription): BaseGenData {
        val result = GenClassData(descr.id+"JS", (descr.extendsId?:BaseVM::class.qualifiedName)+"JS", false, true)
        descr.properties.values.forEach { prop ->
            result.properties.add(GenPropertyDescription(prop.id, getPropertyType(prop.type), getClassName(prop.type, prop.className),lateinit = prop.lateInit, nonNullable = prop.nonNullable))
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

    private fun getClassName(type:VMPropertyType, className: String?): String? {
        return when(type){
            VMPropertyType.STRING -> null
            VMPropertyType.ENUM -> getClassName(className)
            VMPropertyType.SELECT -> getClassName(className)
            VMPropertyType.LONG -> null
            VMPropertyType.INT -> null
            VMPropertyType.BIG_DECIMAL -> null
            VMPropertyType.ENTITY_REFERENCE -> getClassName(ObjectReference::class.qualifiedName)
            VMPropertyType.LOCAL_DATE_TIME -> "kotlin.js.Date"
            VMPropertyType.LOCAL_DATE ->  "kotlin.js.Date"
            VMPropertyType.ENTITY -> getClassName(className)
            VMPropertyType.BOOLEAN -> null
        }
    }

    private fun getClassName(className: String?): String? {
        return className+"JS"
    }

    private fun getPropertyType(type: VMPropertyType): GenPropertyType {
        return when(type){
            VMPropertyType.STRING -> GenPropertyType.STRING
            VMPropertyType.ENUM   -> GenPropertyType.ENUM
            VMPropertyType.SELECT  -> GenPropertyType.ENTITY
            VMPropertyType.LONG  -> GenPropertyType.LONG
            VMPropertyType.INT  -> GenPropertyType.INT
            VMPropertyType.BIG_DECIMAL  -> GenPropertyType.DOUBLE
            VMPropertyType.ENTITY_REFERENCE   -> GenPropertyType.ENTITY
            VMPropertyType.LOCAL_DATE_TIME  -> GenPropertyType.ENTITY
            VMPropertyType.LOCAL_DATE   -> GenPropertyType.ENTITY
            VMPropertyType.ENTITY  -> GenPropertyType.ENTITY
            VMPropertyType.BOOLEAN  -> GenPropertyType.BOOLEAN
        }
    }

    fun generateUiClasses(metadataProvider: IApplicationMetadataProvider, generatedFiles: MutableMap<String, MutableList<File>>,  pluginsLocation:Map<String,File>, projectName: String) {
        val uiData = linkedMapOf<String, MutableList<File>>()
        metadataProvider.getExtensions("ui-metadata").forEach {
            val destClientPlugin = it.getParameters("dest-client-plugin-id").firstOrNull()
            if (destClientPlugin != null) {
                uiData.getOrPut(destClientPlugin, { arrayListOf() })
                        .add(File(pluginsLocation[it.plugin.pluginId], it.getParameters("relative-path").first()))
            }
            Unit
        }
        uiData.entries.forEach { (key, value) ->
            val registry = UiMetaRegistry()
            value.forEach { metaFile -> UiMetadataParser.updateUiMetaRegistry(registry, metaFile) }
            val classes = arrayListOf<String>()
            val classesData = arrayListOf<BaseGenData>()
            val enums = arrayListOf<String>()
            registry.enums.values.forEach {
                val enumClassData = GenEnumData(it.id + "JS")
                it.items.values.forEach { ei ->
                    enumClassData.enumItems.add(ei.id)
                }
                classesData.add(enumClassData)
                enums.add(it.id + "JS")
            }
            registry.viewModels.values.forEach {vmd ->
                classesData.add(toGenData(vmd))
                classes.add(vmd.id + "JS")
            }
            registry.viewSettings.values.forEach {vsd ->
                classesData.add(toGenData(vsd))
                classes.add(vsd.id + "JS")
            }
            registry.viewValidations.values.forEach {vvd ->
                classesData.add(toGenData(vvd))
                classes.add(vvd.id + "JS")
            }
            GenUtils.generateClasses(classesData, pluginsLocation[key]?: throw Xeption.forDeveloper("unable to find basedir of plugin $key"), projectName, generatedFiles.getOrPut(key, { arrayListOf() }))
            val sb = StringBuilder()
            GenUtils.generateHeader(sb, "${key}.Reflection", projectName)

            GenUtils.classBuilder(sb, "object UiReflectionUtilsJS") {
                blankLine()
                "fun registerWebUiClasses()"{
                    enums.forEach {
                        "com.gridnine.jasmine.web.core.reflection.ReflectionFactoryJS.get().registerEnum(\"$it\", {$it.valueOf(it)})"()
                    }
                    registry.enums.values.forEach {
                        val className = it.id + "JS"
                        "com.gridnine.jasmine.web.core.reflection.ReflectionFactoryJS.get().registerQualifiedName($className::class, \"$className\")"()
                    }
                    classes.forEach {
                        "com.gridnine.jasmine.web.core.reflection.ReflectionFactoryJS.get().registerClass(\"$it\", {$it()})"()
                    }
                    classes.forEach {
                        "com.gridnine.jasmine.web.core.reflection.ReflectionFactoryJS.get().registerQualifiedName($it::class, \"$it\")"()
                    }
                }
            }
            val file = File(pluginsLocation[key]?: throw Xeption.forDeveloper("unable to find basedir of plugin $key"), "source-gen/${key.replace(".", "/")}/UiReflectionUtils.kt")
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