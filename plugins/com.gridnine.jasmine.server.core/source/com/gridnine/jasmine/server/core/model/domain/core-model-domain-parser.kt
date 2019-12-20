/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")
package com.gridnine.jasmine.server.core.model.domain


import com.gridnine.jasmine.server.core.model.common.ParserUtils
import com.gridnine.jasmine.server.core.utils.XmlNode
import java.io.File
import java.util.*


object DomainMetadataParser {
    private fun getDocumentPropertyType(typeStr: String): DocumentPropertyType {
        return DocumentPropertyType.valueOf(typeStr)
    }

    private fun getDatabasePropertyType(typeStr: String): DatabasePropertyType {
        return DatabasePropertyType.valueOf(typeStr)
    }

    private fun getDatabaseCollectionType(typeStr: String): DatabaseCollectionType {
        return DatabaseCollectionType.valueOf(typeStr)
    }

    private fun <T : BaseDocumentDescription> fillBaseDocument(elm: XmlNode, description: T, localizations: Map<String, Map<Locale, String>>) {
        description.extendsId = elm.attributes["extends"]
        description.isAbstract = "true" == elm.attributes["abstract"]
        ParserUtils.updateLocalizations(description, localizations)
        elm.children("codeInjection").forEach {
            description.codeInjections.add(it.value?:throw IllegalArgumentException("injection is empty in $it"))
        }
        elm.children("property").forEach {
            val id = it.attributes["id"]?:throw IllegalArgumentException("id attribute is absent in property $it")
            val propDescr = description.properties.getOrPut(id) { DocumentPropertyDescription(description.id, id) }
            ParserUtils.updateLocalizations(propDescr, localizations)
            propDescr.type = getDocumentPropertyType(it.attributes["type"]?:throw IllegalArgumentException("type attribute is absent in property $it"))
            propDescr.className = it.attributes["className"]
            propDescr.notNullable = "true" == it.attributes["notNullable"]
        }
        elm.children("collection").forEach {
            val id = it.attributes["id"]?:throw IllegalArgumentException("id attribute is absent in collection $it")
            val propDescr = description.collections.getOrPut(id) { DocumentCollectionDescription(description.id, id) }
            ParserUtils.updateLocalizations(propDescr, localizations)
            propDescr.elementType = getDocumentPropertyType(it.attributes["element-type"]?:throw IllegalArgumentException("id attribute is absent in collection $it"))
            propDescr.elementClassName = it.attributes["elementClassName"]
        }
    }

    private fun <T : BaseIndexDescription> fillBaseIndex(elm: XmlNode, description: T, localizations: Map<String, Map<Locale, String>>) {
        ParserUtils.updateLocalizations(description, localizations)
        ParserUtils.updateParameters(elm, description)
        elm.children("property").forEach { child ->
            val id = child.attributes["id"]?:throw IllegalArgumentException("id attribute is absent in property $child")
            val property = description.properties.getOrPut(id) { IndexPropertyDescription(description.id, id) }
            ParserUtils.updateParameters(child, property)
            ParserUtils.updateLocalizations(property, localizations)
            property.className = child.attributes["className"]
            property.type = getDatabasePropertyType(child.attributes["type"]?:throw IllegalArgumentException("type attribute is absent in property $child"))
        }
        elm.children("collection").forEach { child ->
            val id = child.attributes["id"]?:throw IllegalArgumentException("id attribute is absent in collection $child")
            val collection = description.collections.getOrPut(id) { IndexCollectionDescription(description.id, id) }
            ParserUtils.updateParameters(child, collection)
            ParserUtils.updateLocalizations(collection, localizations)
            collection.elementClassName = child.attributes["elementClassName"]
            collection.elementType = getDatabaseCollectionType(child.attributes["elementType"]?:throw IllegalArgumentException("type attribute is absent in collection $child"))
        }
    }


    fun updateDomainMetaRegistry(registry: DomainMetaRegistry, meta:File) {
        val (node, localizations) = ParserUtils.parseMeta(meta)
        updateRegistry(registry, node, localizations)
    }


    fun updateDomainMetaRegistry(registry: DomainMetaRegistry, metaQualifiedName: String, classLoader: ClassLoader) {
        val (node, localizations) = ParserUtils.parseMeta(metaQualifiedName, classLoader)
        updateRegistry(registry, node, localizations)

    }

    private fun updateRegistry(registry: DomainMetaRegistry, node: XmlNode, localizations: Map<String, Map<Locale, String>>) {
        node.children("enum").forEach { child ->
            val enumId = child.attributes["id"]?:throw IllegalArgumentException("id attribute is absent in enum $child")
            val enumDescription = registry.enums.getOrPut(enumId) { DomainEnumDescription(enumId) }
            child.children("enum-item").forEach { enumItemElm ->
                val enumItemId = enumItemElm.attributes["id"]?:throw IllegalArgumentException("id attribute is absent in enum item $enumItemElm")
                val enumItem = enumDescription.items.getOrPut(enumItemId) { DomainEnumItemDescription(enumId, enumItemId) }
                ParserUtils.updateLocalizations(enumItem, localizations)
            }
        }
        node.children("nested-document").forEach { child ->
            val id = child.attributes["id"]?:throw IllegalArgumentException("id attribute is absent in document $child")
            val nestedDoc = registry.nestedDocuments.getOrPut(id) { NestedDocumentDescription(id) }
            fillBaseDocument(child, nestedDoc, localizations)
        }
        node.children("document").forEach { child ->
            val id = child.attributes["id"]?:throw IllegalArgumentException("id attribute is absent in document $child")
            val nestedDoc = registry.documents.getOrPut(id) { DocumentDescription(id) }
            fillBaseDocument(child, nestedDoc, localizations)
        }
        node.children("index").forEach { child ->
            val id = child.attributes["id"]?:throw IllegalArgumentException("id attribute is absent in index $child")
            val index = registry.indexes.getOrPut(id) { IndexDescription(id) }
            index.document = child.attributes["document"]!!
            fillBaseIndex(child, index, localizations)
        }
        node.children("asset").forEach { child ->
            val id = child.attributes["id"]?:throw IllegalArgumentException("id attribute is absent in asset $child")
            val asset = registry.assets.getOrPut(id) { AssetDescription(id) }
            fillBaseIndex(child, asset, localizations)
        }
    }




}