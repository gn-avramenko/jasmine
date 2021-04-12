/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
package com.gridnine.jasmine.common.core.parser

import com.gridnine.jasmine.common.core.meta.*
import com.gridnine.jasmine.common.core.model.Xeption
import com.gridnine.jasmine.common.core.utils.XmlNode
import java.io.File


object RestMetadataParser {
    private fun getPropertyType(typeStr: String): RestPropertyType {
        return RestPropertyType.valueOf(typeStr)
    }

    private fun  fillRestEntity(elm: XmlNode, description: RestEntityDescription) {
        description.isAbstract = "true" == elm.attributes["abstract"]
        description.extendsId = elm.attributes["extends"]
        elm.children("property").forEach {
            val id = ParserUtils.getIdAttribute(it)
            val propDescr = description.properties.getOrPut(id){ RestPropertyDescription(id,
                    getPropertyType(it.attributes["type"]?:throw Xeption.forDeveloper("$it has no type attribute"))
                    , "true" == it.attributes["lateinit"],"true" == it.attributes["non-nullable"])}
            propDescr.className = it.attributes["class-name"]
        }
        elm.children("collection").forEach {
            val id = ParserUtils.getIdAttribute(it)
            val collDescr = description.collections.getOrPut(id) { RestCollectionDescription(id,
                    getPropertyType(it.attributes["element-type"]?:throw Xeption.forDeveloper("$it has no element-type attribute"))) }
            collDescr.elementClassName = it.attributes["element-class-name"]
        }
    }

    fun updateRestMetaRegistry(registry: RestMetaRegistry, meta: File) {
        val (node, _) = ParserUtils.parseMeta(meta)
        updateRegistry(registry, node)
    }


    fun updateRestMetaRegistry(registry: RestMetaRegistry, metaQualifiedName: String, classLoader: ClassLoader) {
        val (node, _) = ParserUtils.parseMeta(metaQualifiedName, classLoader)
        updateRegistry(registry, node)

    }

    private fun updateRegistry(registry: RestMetaRegistry, node:XmlNode) {
        val restId = ParserUtils.getIdAttribute(node)
        registry.rests.getOrPut(restId){RestDescription(restId)}

        node.children("enum").forEach { child ->
            val enumId = ParserUtils.getIdAttribute(child)
            val enumDescription = registry.enums.getOrPut(enumId) { RestEnumDescription(enumId) }
            child.children("enum-item").forEach { enumItemElm ->
                val enumItemId = ParserUtils.getIdAttribute(enumItemElm)
                 enumDescription.items.getOrPut(enumItemId){ RestEnumItemDescription(enumItemId) }
            }
        }
        node.children("entity").forEach { child ->
            val id = ParserUtils.getIdAttribute(child)
            val entityDescr = registry.entities.getOrPut(id){RestEntityDescription(id)}
            fillRestEntity(child, entityDescr)
        }
        node.children("group").forEach { child ->
            val groupId = "${restId}_${child.attributes["id"]}"
            val group = registry.groups.getOrPut(groupId) { RestGroupDescription(groupId, restId) }
            child.children("operation").forEach {operation ->
                val operationId = "${group.id}_${operation.attributes["id"]}"
                val handlerId = operation.attributes["handler"]?: throw Xeption.forDeveloper("handler is not defined in operation ${operation.name}")
                val requestItem = operation.children("request")[0]
                val requestEntityId = ParserUtils.getIdAttribute(requestItem)
                val requestDescr = registry.entities.getOrPut(requestEntityId) { RestEntityDescription(requestEntityId) }
                fillRestEntity(requestItem, requestDescr)
                val responseItem = operation.children("response")[0]
                val responseEntityId = ParserUtils.getIdAttribute(responseItem)
                val responseDescr = registry.entities.getOrPut(responseEntityId) { RestEntityDescription(responseEntityId) }
                fillRestEntity(responseItem, responseDescr)
                registry.operations[operationId] = RestOperationDescription(id = operationId, groupId = groupId, handler = handlerId, requestEntity = requestEntityId, responseEntity = responseEntityId)
            }
        }
        val hierarchy = hashMapOf<String,MutableSet<String>>()
        DomainMetaRegistry.get().documents.values.forEach{updateHierarchy(hierarchy, it)}
        DomainMetaRegistry.get().nestedDocuments.values.forEach{updateHierarchy(hierarchy, it)}
        registry.entities.values.forEach {ett -> ett.extendsId?.let { hierarchy.getOrPut(it, { hashSetOf()}) }?.add(ett.id) }
        val seen = hashSetOf<String>()
        registry.entities.values.forEach { updateUsedInRestParameter(it, registry, hierarchy, seen)}
    }

    private fun updateHierarchy(hierarchy: HashMap<String, MutableSet<String>>, dd: BaseDocumentDescription) {
        dd.extendsId?.let { hierarchy.getOrPut(it, { hashSetOf()}) }?.add(dd.id)
    }


    private fun updateUsedInRestParameter(red: RestEntityDescription,registry: RestMetaRegistry,hierarchy: HashMap<String, MutableSet<String>>, seen:HashSet<String>) {
        if(!seen.add(red.id)){
            return
        }
        red.properties.values.forEach {prop -> prop.className?.let {updateUsedInRestParameter(it, registry, hierarchy, seen) }}
        red.collections.values.forEach {prop -> prop.elementClassName?.let {updateUsedInRestParameter(it, registry, hierarchy, seen) }}
    }

    private fun updateUsedInRestParameter(clsName: String, registry: RestMetaRegistry, hierarchy: HashMap<String, MutableSet<String>>, seen: HashSet<String>) {
        DomainMetaRegistry.get().documents[clsName]?.let { updateUsedInRestParameter(it, hierarchy, seen)}
        DomainMetaRegistry.get().nestedDocuments[clsName]?.let { updateUsedInRestParameter(it, hierarchy,seen)}
        registry.entities[clsName]?.let {
            updateUsedInRestParameter(it, registry, hierarchy, seen)
        }
    }

    private fun updateUsedInRestParameter(dd: BaseDocumentDescription, hierarchy: HashMap<String, MutableSet<String>>, seen: HashSet<String>) {
        if(seen.contains(dd.id)){
            return
        }
        if(dd.parameters[DomainMetaRegistry.EXPOSED_IN_REST_KEY] == "true"){
            return
        }
        val entities = hashSetOf<BaseDocumentDescription>()
        collectEntities(entities, dd, hierarchy, seen)
        entities.forEach{dd2 ->
            dd2.parameters[DomainMetaRegistry.EXPOSED_IN_REST_KEY] = "true"
            dd2.properties.values.forEach {prop -> prop.className?.let {updateUsedInRestParameter(it,  hierarchy, seen) }}
            dd2.collections.values.forEach {prop -> prop.elementClassName?.let {updateUsedInRestParameter(it,  hierarchy, seen) }}
        }
    }

    private fun updateUsedInRestParameter(clsName: String, hierarchy: HashMap<String, MutableSet<String>>, seen: HashSet<String>) {
        DomainMetaRegistry.get().documents[clsName]?.let { updateUsedInRestParameter(it, hierarchy, seen) }
        DomainMetaRegistry.get().nestedDocuments[clsName]?.let { updateUsedInRestParameter(it, hierarchy, seen) }

    }

    private fun collectEntities(entities: HashSet<BaseDocumentDescription>, dd: BaseDocumentDescription, hierarchy: HashMap<String, MutableSet<String>>, seen: HashSet<String>) {
        if(!seen.add(dd.id)){
            return
        }
        entities.add(dd)
        if(dd.isAbstract){
            hierarchy[dd.id]?.forEach {clsName ->
                DomainMetaRegistry.get().nestedDocuments[clsName]?.let { collectEntities(entities, it, hierarchy, seen) }
                DomainMetaRegistry.get().documents[clsName]?.let { collectEntities(entities, it, hierarchy, seen) }
            }
        }
    }


}