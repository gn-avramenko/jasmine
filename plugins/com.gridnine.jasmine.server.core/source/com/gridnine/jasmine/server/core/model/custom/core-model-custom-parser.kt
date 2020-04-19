/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")
package com.gridnine.jasmine.server.core.model.custom

import com.gridnine.jasmine.server.core.model.common.ParserUtils
import com.gridnine.jasmine.server.core.model.common.Xeption
import com.gridnine.jasmine.server.core.utils.XmlNode
import java.io.File


object CustomMetadataParser {
    private fun getPropertyType(typeStr: String): CustomType {
        return CustomType.valueOf(typeStr)
    }

    private fun  fillCustomEntity(elm: XmlNode, description: CustomEntityDescription) {
        description.abstract = "true" == elm.attributes["abstract"]
        description.extends = elm.attributes["extends"]
        elm.children("property").forEach {
            val id = ParserUtils.getIdAttribute(it)
            val propDescr = description.properties.getOrPut(id){ CustomPropertyDescription(id,
                    getPropertyType(it.attributes["type"]?:throw Xeption.forDeveloper("$it has no type attribute")), "true" == it.attributes["lateinit"],"true" == it.attributes["nonNullable"])}
            propDescr.className = it.attributes["class-name"]
        }
        elm.children("collection").forEach {
            val id = ParserUtils.getIdAttribute(it)
            val collDescr = description.collections.getOrPut(id) { CustomCollectionDescription(id,
                    getPropertyType(it.attributes["element-type"]?:throw Xeption.forDeveloper("$it has no element-type attribute"))) }
            collDescr.elementClassName = it.attributes["element-class-name"]
        }
    }

    fun updateCustomMetaRegistry(registry: CustomMetaRegistry, meta: File) {
        val (node, _) = ParserUtils.parseMeta(meta)
        updateRegistry(registry, node)
    }


    fun updateCustomMetaRegistry(registry: CustomMetaRegistry, metaQualifiedName: String, classLoader: ClassLoader) {
        val (node, _) = ParserUtils.parseMeta(metaQualifiedName, classLoader)
        updateRegistry(registry, node)

    }

    private fun updateRegistry(registry: CustomMetaRegistry, node:XmlNode) {

        node.children("enum").forEach { child ->
            val enumId = ParserUtils.getIdAttribute(child)
            val enumDescription = registry.enums.getOrPut(enumId) { CustomEnumDescription(enumId) }
            child.children("enum-item").forEach { enumItemElm ->
                val enumItemId = ParserUtils.getIdAttribute(enumItemElm)
                 enumDescription.items.getOrPut(enumItemId){ CustomEnumItemDescription(enumItemId) }
            }
        }
        node.children("entity").forEach { child ->
            val id = ParserUtils.getIdAttribute(child)
            val entityDescr = registry.entities.getOrPut(id){CustomEntityDescription(id)}
            fillCustomEntity(child, entityDescr)
        }

    }
}