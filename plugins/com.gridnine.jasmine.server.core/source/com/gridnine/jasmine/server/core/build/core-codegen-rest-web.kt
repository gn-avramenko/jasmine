/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
package com.gridnine.jasmine.server.core.build

import com.gridnine.jasmine.server.core.app.IApplicationMetadataProvider
import com.gridnine.jasmine.server.core.model.common.BaseIntrospectableObject
import com.gridnine.jasmine.server.core.model.common.Xeption
import com.gridnine.jasmine.server.core.model.domain.ObjectReference
import com.gridnine.jasmine.server.core.model.rest.*
import java.io.File


object RestWebGenerator {

    private fun <T : RestEntityDescription> toGenData(descr: T): GenClassData {

        val extendsId = when {
            descr.extendsId != null -> descr.extendsId+"JS"
            else -> "${BaseIntrospectableObject::class.qualifiedName}JS"
        }
        val result = GenClassData(descr.id+"JS", extendsId, descr.isAbstract,  noEnumProperties = true, open = false)
        descr.properties.values.forEach { prop ->
            result.properties.add(GenPropertyDescription(prop.id, getPropertyType(prop.type), getClassName(prop.type, prop.className), nonNullable = prop.nonNullable, lateinit = prop.lateinit, openSetter = false))
        }
        descr.collections.values.forEach { coll ->
            result.collections.add(GenCollectionDescription(coll.id, getPropertyType(coll.elementType), getClassName(coll.elementType, coll.elementClassName), openGetter = false))
        }
        return result
    }

    private fun getClassName(type: RestPropertyType, className: String?): String? {
        return when (type) {
            RestPropertyType.STRING -> null
            RestPropertyType.BIG_DECIMAL -> "Double"
            RestPropertyType.BOOLEAN -> null
            RestPropertyType.ENTITY_REFERENCE -> "${ObjectReference::class.qualifiedName}JS"
            RestPropertyType.ENUM -> className + "JS"
            RestPropertyType.INT -> null
            RestPropertyType.LONG -> null
            RestPropertyType.LOCAL_DATE -> "kotlin.js.Date"
            RestPropertyType.LOCAL_DATE_TIME -> "kotlin.js.Date"
            RestPropertyType.ENTITY -> "${className}JS"
            RestPropertyType.BYTE_ARRAY -> "kotlin.js.ByteArray"
        }
    }


    private fun getPropertyType(type: RestPropertyType): GenPropertyType {
        return when (type) {
            RestPropertyType.STRING -> GenPropertyType.STRING
            RestPropertyType.BIG_DECIMAL -> GenPropertyType.ENTITY
            RestPropertyType.BOOLEAN -> GenPropertyType.BOOLEAN
            RestPropertyType.ENTITY_REFERENCE -> GenPropertyType.ENTITY
            RestPropertyType.ENUM -> GenPropertyType.ENUM
            RestPropertyType.INT -> GenPropertyType.INT
            RestPropertyType.LOCAL_DATE -> GenPropertyType.ENTITY
            RestPropertyType.LOCAL_DATE_TIME -> GenPropertyType.ENTITY
            RestPropertyType.LONG -> GenPropertyType.LONG
            RestPropertyType.ENTITY -> GenPropertyType.ENTITY
            RestPropertyType.BYTE_ARRAY -> GenPropertyType.ENTITY
        }
    }


    fun generateRestClasses(metadataProvider: IApplicationMetadataProvider, generatedFiles: MutableMap<String, MutableList<File>>,  pluginsLocation:Map<String,File>, projectName:String) {
        val restData = linkedMapOf<String, MutableList<File>>()
        metadataProvider.getExtensions("rest-metadata").forEach {
            val destClientPlugin = it.getParameters("dest-client-plugin-id").firstOrNull()
            if (destClientPlugin != null) {
                restData.getOrPut(destClientPlugin, { arrayListOf() })
                        .add(File(pluginsLocation[it.plugin.pluginId], it.getParameters("relative-path").first()))
            }
            Unit
        }
        restData.entries.forEach { (key, value) ->
            val registry = RestMetaRegistry()
            value.forEach { metaFile -> RestMetadataParser.updateRestMetaRegistry(registry, metaFile) }
            val classesData = arrayListOf<BaseGenData>()
            val classes = arrayListOf<String>()
            val enums = arrayListOf<String>()
            registry.enums.values.forEach {
                val enumClassData = GenEnumData(it.id + "JS")
                it.items.values.forEach { ei ->
                    enumClassData.enumItems.add(ei.id)
                }
                classesData.add(enumClassData)
                enums.add(it.id + "JS")
            }
            registry.entities.values.forEach {
                val data = toGenData(it)
                classesData.add(data)
                classes.add(it.id + "JS")
            }
            GenUtils.generateClasses(classesData, pluginsLocation[key]?: throw Xeption.forDeveloper("unable to find basedir of plugin $key"), projectName, generatedFiles.getOrPut(key, { arrayListOf() }))
            val sb = StringBuilder()
            GenUtils.generateHeader(sb, "${key}.Reflection", projectName)

            GenUtils.classBuilder(sb, "object RestReflectionUtilsJS") {
                blankLine()
                "fun registerWebRestClasses()"{
                    enums.forEach {
                        "com.gridnine.jasmine.web.core.reflection.ReflectionFactoryJS.get().registerEnum(\"$it\", {$it.valueOf(it)})"()
                    }
                    classes.forEach {
                        "com.gridnine.jasmine.web.core.reflection.ReflectionFactoryJS.get().registerClass(\"$it\", {$it()})"()
                    }
                    classes.forEach {
                        "com.gridnine.jasmine.web.core.reflection.ReflectionFactoryJS.get().registerQualifiedName($it::class, \"$it\")"()
                    }
                    registry.enums.values.forEach {
                        val className = it.id + "JS"
                        "com.gridnine.jasmine.web.core.reflection.ReflectionFactoryJS.get().registerQualifiedName($className::class, \"$className\")"()
                    }
                }
            }
            run {
                val file = File(pluginsLocation[key]
                        ?: throw Xeption.forDeveloper("unable to find basedir of plugin $key"), "source-gen/${key.replace(".", "/")}/RestReflectionUtils.kt")
                if (!file.parentFile.exists()) {
                    file.parentFile.mkdirs()
                }
                val content = sb.toString().toByteArray()
                if (!file.exists() || !content.contentEquals(file.readBytes())) {
                    file.writeBytes(content)
                }
                generatedFiles.getOrPut(key) { arrayListOf() }.add(file)
            }
            run{
                val operationsMap = linkedMapOf<String, MutableList<RestOperationDescription>>()
                registry.operations.values.forEach {
                    val restId = registry.rests[registry.groups[it.groupId]!!.restId]!!.id
                    operationsMap.getOrPut(restId){arrayListOf()}.add(it)
                }
                operationsMap.entries.forEach { entry  ->
                    val sb = StringBuilder()
                    GenUtils.generateHeader(sb, "${key}.RestClientJS", projectName)
                    GenUtils.classBuilder(sb, "object ${entry.key.capitalize()}RestClient"){
                        entry.value.forEach{
                            "fun ${it.id}(request:${it.requestEntity}JS): kotlin.js.Promise<${it.responseEntity}JS>"{
                                "return com.gridnine.jasmine.web.core.remote.RpcManager.get().post(\"${it.id}\",request)"()
                            }
                        }
                    }
                    val file = File(pluginsLocation[key]?: throw Xeption.forDeveloper("unable to find basedir of plugin $key"), "source-gen/${key.replace(".", "/")}/${entry.key.capitalize()}RestClient.kt")
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
    }


}