/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
package com.gridnine.jasmine.server.core.build

import com.gridnine.jasmine.server.core.app.IApplicationMetadataProvider
import com.gridnine.jasmine.server.core.model.ui.*
import java.io.File


object UiServerGenerator {

    private fun toGenData(descr: VMEntityDescription): GenClassData {

        val result = GenClassData(descr.id, BaseVMEntity::class.qualifiedName, abstract = false, enum = false, noEnumProperties = true)
        descr.properties.values.forEach { prop ->
            result.properties.add(GenPropertyDescription(prop.id, getPropertyType(prop.type), prop.className))
        }
        descr.collections.values.forEach { coll ->
            result.collections.add(GenCollectionDescription(coll.id, getPropertyType(coll.elementType), coll.elementClassName))
        }
        return result
    }


    private fun getPropertyType(type: VMCollectionType): GenPropertyType {
        return when (type) {
            VMCollectionType.ENTITY -> GenPropertyType.ENTITY
        }
    }




    private fun getPropertyType(type: VMPropertyType): GenPropertyType {
        return when (type) {
            VMPropertyType.STRING -> GenPropertyType.STRING
            VMPropertyType.BIG_DECIMAL -> GenPropertyType.BIG_DECIMAL
            VMPropertyType.BOOLEAN -> GenPropertyType.BOOLEAN
            VMPropertyType.ENTITY -> GenPropertyType.ENTITY
            VMPropertyType.SELECT -> GenPropertyType.ENTITY
            VMPropertyType.ENTITY_REFERENCE -> GenPropertyType.ENTITY_REFERENCE
            VMPropertyType.ENUM -> GenPropertyType.ENUM
            VMPropertyType.INT -> GenPropertyType.INT
            VMPropertyType.LOCAL_DATE -> GenPropertyType.LOCAL_DATE
            VMPropertyType.LOCAL_DATE_TIME -> GenPropertyType.LOCAL_DATE_TIME
            VMPropertyType.LONG -> GenPropertyType.LONG
        }
    }

    private fun getClassName(type: VSPropertyType, elementClassName: String?): String? {
        return when (type) {
            VSPropertyType.ENUM_SELECT -> "${EnumSelectConfiguration::class.java.name}<${elementClassName}>"
            VSPropertyType.ENTITY_AUTOCOMPLETE -> "${EntityAutocompleteConfiguration::class.java.name}<${elementClassName}>"
            VSPropertyType.ENTITY -> elementClassName
            VSPropertyType.SELECT -> SelectItem::javaClass.name
            VSPropertyType.COLUMN_ENTITY -> "${EntityColumnConfiguration::class.java.name}<${elementClassName}>"
            VSPropertyType.COLUMN_ENUM_SELECT -> "${EnumColumnConfiguration::class.java.name}<${elementClassName}>"
            VSPropertyType.COLUMN_TEXT -> TextColumnConfiguration::class.java.name
            VSPropertyType.COLUMN_FLOAT -> FloatColumnConfiguration::class.java.name
            VSPropertyType.COLUMN_INT -> IntegerColumnConfiguration::class.java.name
            VSPropertyType.COLUMN_DATE -> DateColumnConfiguration::class.java.name
        }
    }

    private fun toGenData(descr: VSEntityDescription): GenClassData {

        val result = GenClassData(descr.id, BaseVSEntity::class.qualifiedName, abstract = false, enum = false, noEnumProperties = true)
        result.generateBuilder = true
        descr.properties.values.forEach { prop ->
            result.properties.add(GenPropertyDescription(prop.id, GenPropertyType.ENTITY, getClassName(prop.type, prop.className)))
        }
        descr.collections.values.forEach { coll ->
            result.collections.add(GenCollectionDescription(coll.id, GenPropertyType.ENTITY, getClassName(coll.elementType, coll.elementClassName)))
        }
        return result
    }

    private fun getClassName(elementType: VSCollectionType, elementClassName: String?): String? {
        return when (elementType) {
            VSCollectionType.ENTITY -> elementClassName
        }

    }

    private fun getPropertyType(type: VVPropertyType): GenPropertyType {
        return when (type) {
            VVPropertyType.STRING -> GenPropertyType.STRING
        }
    }

    private fun toGenData(descr: VVEntityDescription): GenClassData {

        val result = GenClassData(descr.id, BaseVVEntity::class.qualifiedName, abstract = false, enum = false, noEnumProperties = true)
        descr.properties.values.forEach { prop ->
            result.properties.add(GenPropertyDescription(prop.id, getPropertyType(prop.type), prop.className))
        }
        descr.collections.values.forEach { coll ->
            result.collections.add(GenCollectionDescription(coll.id, GenPropertyType.ENTITY, coll.elementClassName))
        }
        return result
    }

    fun generateUiClasses(projectDir: File, metadataProvider: IApplicationMetadataProvider, generatedFiles: MutableMap<String, MutableList<File>>, projectName: String) {
        val domainData = linkedMapOf<String, MutableList<File>>()
        metadataProvider.getExtensions("ui-metadata").forEach {
            domainData.getOrPut(it.getParameters("dest-server-plugin-id").first(), { arrayListOf() })
                    .add(File(projectDir, it.getParameters("file").first()))
            Unit
        }
        domainData.entries.forEach { (key, value) ->
            val registry = UiMetaRegistry()
            value.forEach { metaFile -> UiMetadataParser.updateUiMetaRegistry(registry, metaFile) }
            registry.validationMessages.forEach { entry ->
                val sb = StringBuilder()
                GenUtils.generateHeader(sb, entry.key, projectName, false)
                sb.append("\n\n")
                GenUtils.classBuilder(sb, "object " + GenUtils.getSimpleClassName(entry.key)) {
                    entry.value.items.values.forEach { mi ->
                        "fun ${mi.id}(vararg params:Any) = ${ValidationMessagesFactory::class.qualifiedName}.getMessage(\"${entry.key}\",\"${mi.id}\",params)"()
                    }
                }
                val file = File(projectDir, "plugins/$key/source-gen//${GenUtils.getPackageName(entry.key).replace(".", "/")}/${GenUtils.getSimpleClassName(entry.key)}.kt")
                if (!file.parentFile.exists()) {
                    file.parentFile.mkdirs()
                }
                file.writeText(sb.toString(),Charsets.UTF_8)
                generatedFiles.getOrPut(key, { arrayListOf()}).add(file)
            }

            val classesData = arrayListOf<GenClassData>()
            registry.viewModels.values.forEach {
                classesData.add(toGenData(it))
            }
            registry.viewSettings.values.forEach {
                classesData.add(toGenData(it))
            }
            registry.viewValidations.values.forEach {
                classesData.add(toGenData(it))
            }
            GenUtils.generateClasses(classesData, File(projectDir, "plugins/$key"), projectName, generatedFiles.getOrPut(key, { arrayListOf() }))
        }
    }

}