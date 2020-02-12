/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")
package com.gridnine.jasmine.server.core.model.rest

import com.gridnine.jasmine.server.core.model.common.ParserUtils
import com.gridnine.jasmine.server.core.utils.XmlNode
import java.io.File


object RestMetadataParser {
    private fun getPropertyType(typeStr: String?): RestPropertyType {
        return RestPropertyType.valueOf(typeStr!!)
    }

    private fun  fillRestEntity(elm: XmlNode, description: RestEntityDescription) {
        description.abstract = "true" == elm.attributes["abstract"]
        description.extends = elm.attributes["extends"]
        elm.children("property").forEach {
            val id = ParserUtils.getIdAttribute(it)
            val propDescr = description.properties.getOrPut(id){ RestPropertyDescription(id, getPropertyType(it.attributes["type"]), "true" == it.attributes["lateinit"],"true" == it.attributes["nonNullable"])}
            propDescr.className = it.attributes["className"]
        }
        elm.children("collection").forEach {
            val id = ParserUtils.getIdAttribute(it)
            val collDescr = description.collections.getOrPut(id) { RestCollectionDescription(id, getPropertyType(it.attributes["element-type"])) }
            collDescr.elementClassName = it.attributes["elementClassName"]
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
                val handlerId = operation.attributes["handler"]?: throw IllegalArgumentException("handler is not definde in opertatio ${operation.name}")
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
    }




}