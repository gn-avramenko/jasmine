/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")
package com.gridnine.jasmine.server.core.build

import com.gridnine.jasmine.server.core.app.IApplicationMetadataProvider
import com.gridnine.jasmine.server.core.model.common.BaseEntity
import com.gridnine.jasmine.server.core.model.domain.*
import com.gridnine.jasmine.server.core.model.rest.*
import com.gridnine.jasmine.server.core.model.ui.BaseVMEntity
import com.gridnine.jasmine.server.core.model.ui.BaseVSEntity
import com.gridnine.jasmine.server.core.model.ui.BaseVVEntity
import java.io.File


object RestWebGenerator {

    private fun  toGenData(descr: RestEntityDescription): GenClassData {

        val result = GenClassData("${descr.id}JS", if(descr.extends != null) "${descr.extends}JS" else "com.gridnine.jasmine.web.core.model.rest.BaseRestEntityJS", descr.abstract, enum = false, noEnumProperties = true)
        descr.properties.values.forEach { prop ->
            result.properties.add(GenPropertyDescription(prop.id, getPropertyType(prop.type), getClassName(prop.type, prop.className),lateinit = prop.lateinit, nonNullable = false))
        }
        descr.collections.values.forEach { coll ->
            result.collections.add(GenCollectionDescription(coll.id, getPropertyType(coll.elementType), getClassName(coll.elementType, coll.elementClassName)))
        }
        return result
    }

    private fun getClassName(propertyType: RestPropertyType, className: String?): String? {
        if(propertyType == RestPropertyType.ENTITY_REFERENCE ){
            return "com.gridnine.jasmine.web.core.model.domain.EntityReferenceJS"
        }
        if(propertyType == RestPropertyType.BIG_DECIMAL ){
            return "Double"
        }
        if(className != null){
            if(BaseIndex::class.qualifiedName == className){
                return "com.gridnine.jasmine.web.core.model.domain.BaseIndexJS"
            }
            if(BaseEntity::class.qualifiedName == className){
                return "com.gridnine.jasmine.web.core.model.common.BaseEntityJS"
            }
            if(BaseVMEntity::class.qualifiedName == className){
                return "com.gridnine.jasmine.web.core.model.ui.BaseVMEntityJS"
            }
            if(BaseVSEntity::class.qualifiedName == className){
                return "com.gridnine.jasmine.web.core.model.ui.BaseVSEntityJS"
            }
            if(BaseVVEntity::class.qualifiedName == className){
                return "com.gridnine.jasmine.web.core.model.ui.BaseVVEntityJS"
            }
            if(className.startsWith(EntityReference::class.qualifiedName!!)){
                return "com.gridnine.jasmine.web.core.model.domain.EntityReferenceJS"
            }

            return className+"JS"
        }
        return null
    }


    private fun getPropertyType(type: RestPropertyType): GenPropertyType {
        return when (type) {
            RestPropertyType.STRING->  GenPropertyType.STRING
            RestPropertyType.BYTE_ARRAY ->GenPropertyType.BYTE_ARRAY
            RestPropertyType.BIG_DECIMAL ->GenPropertyType.ENTITY
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

    fun generateWebRest(projectDir: File, metadataProvider: IApplicationMetadataProvider, generatedFiles: MutableMap<String, MutableList<File>>, projectName: String) {
        val domainData = linkedMapOf<String, MutableList<File>>()
        metadataProvider.getExtensions("rest-metadata").forEach {
            val pluginId = it.getParameters("dest-client-plugin-id").firstOrNull()
            if(pluginId != null) {
                domainData.getOrPut(pluginId, { arrayListOf() })
                        .add(File(projectDir, it.getParameters("file").first()))
            }
            Unit
        }
        domainData.entries.forEach { (key, value) ->
            val registry = RestMetaRegistry()
            value.forEach { metaFile -> RestMetadataParser.updateRestMetaRegistry(registry, metaFile) }
            val classesData = arrayListOf<GenClassData>()
            val enums = arrayListOf<String>()
            val classes = arrayListOf<String>()
            registry.enums.values.forEach {
                val enumClassData = GenClassData("${it.id}JS", null, abstract = false, enum = true, noEnumProperties = true)
                enums.add(enumClassData.id)
                it.items.values.forEach { ei ->
                    enumClassData.enumItems.add(ei.id)
                }
                classesData.add(enumClassData)
            }
            registry.entities.values.forEach {
                val docClassData = toGenData(it)
                classesData.add(docClassData)
                if(!it.abstract) {
                    classes.add(docClassData.id)
                }
            }
            GenUtils.generateClasses(classesData, File(projectDir, "plugins/$key"), projectName,  generatedFiles.getOrPut(key, { arrayListOf()}))

            kotlin.run {
                val sb = StringBuilder()
                GenUtils.generateHeader(sb, "${key}.RestReflectionUtilsJS", projectName, false)

                GenUtils.classBuilder(sb, "object RestReflectionUtilsJS") {
                    blankLine()
                    "fun registerRestWebClasses()"{
                        enums.forEach {
                            "com.gridnine.jasmine.web.core.utils.ReflectionFactoryJS.get().registerEnum(\"${it}\"){${it}.valueOf(it)}"()
                        }
                        classes.forEach {
                            "com.gridnine.jasmine.web.core.utils.ReflectionFactoryJS.get().registerClass(\"${it}\"){${it}()}"()
                        }

                        classes.forEach {
                            "com.gridnine.jasmine.web.core.utils.ReflectionFactoryJS.get().registerQualifiedName(${it}::class, \"${it}\")"()
                        }
                    }
                }
                val file = File(projectDir, "plugins/$key/source-gen/${key.replace(".", "/")}/RestReflectionUtilsJS.kt")
                if (!file.parentFile.exists()) {
                    file.parentFile.mkdirs()
                }
                val content = sb.toString().toByteArray()
                if (!file.exists() || !content.contentEquals(file.readBytes())) {
                    file.writeBytes(content)
                }
                generatedFiles.getOrPut(key) { arrayListOf() }.add(file)
            }

            kotlin.run{
                val operationsMap = linkedMapOf<String, MutableList<RestOperationDescription>>()
                registry.operations.values.forEach {
                    val restId = registry.rests[registry.groups[it.groupId]!!.restId]!!.id
                    operationsMap.getOrPut(restId){arrayListOf()}.add(it)
                }
                operationsMap.entries.forEach { entry  ->
                    val sb = StringBuilder()
                    GenUtils.generateHeader(sb, "${key}.RestClientJS", projectName, false)
                    GenUtils.classBuilder(sb, "object ${entry.key.capitalize()}RestClient"){
                        entry.value.forEach{
                            "fun ${it.id}(request:${it.requestEntity}JS): kotlin.js.Promise<${it.responseEntity}JS>"{
                                "return com.gridnine.jasmine.web.core.remote.RpcManager.get().post(\"${it.id}\",request)"()
                            }
                        }
                    }
                    val file = File(projectDir, "plugins/$key/source-gen/${key.replace(".", "/")}/${entry.key.capitalize()}RestClient.kt")
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