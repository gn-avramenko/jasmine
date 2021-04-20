/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")

package com.gridnine.jasmine.common.core.parser

import com.gridnine.jasmine.common.core.meta.*
import com.gridnine.jasmine.common.core.model.Xeption
import com.gridnine.jasmine.common.core.utils.XmlNode
import java.io.File
import java.util.*


object MiscMetadataParser {
    private fun getPropertyType(typeStr: String): MiscType {
        return MiscType.valueOf(typeStr)
    }

    private fun  fillMiscEntity(elm: XmlNode, description: MiscEntityDescription, localizations: Map<String, Map<Locale, String>>) {
        description.isAbstract = "true" == elm.attributes["abstract"]
        description.extendsId = elm.attributes["extends"]
        elm.children("property").forEach {
            val id = ParserUtils.getIdAttribute(it)
            val propDescr = description.properties.getOrPut(id){ MiscPropertyDescription(id,
                    getPropertyType(it.attributes["type"]?:throw Xeption.forDeveloper("$it has no type attribute")), "true" == it.attributes["lateinit"],"true" == it.attributes["nonNullable"])
            }
            propDescr.className = it.attributes["class-name"]
            ParserUtils.updateLocalizations(propDescr, description.id, localizations)
        }
        elm.children("collection").forEach {
            val id = ParserUtils.getIdAttribute(it)
            val collDescr = description.collections.getOrPut(id) { MiscCollectionDescription(id,
                    getPropertyType(it.attributes["element-type"]?:throw Xeption.forDeveloper("$it has no element-type attribute"))) }
            collDescr.elementClassName = it.attributes["element-class-name"]
            ParserUtils.updateLocalizations(collDescr, description.id, localizations)
        }
        elm.children("map").forEach {
            val id = ParserUtils.getIdAttribute(it)
            val mapDescr = description.maps.getOrPut(id){ MiscMapDescription(id, getPropertyType(it.attributes["key-type"]?:throw Xeption.forDeveloper("$it has no element-type attribute")), getPropertyType(it.attributes["value-type"]?:throw Xeption.forDeveloper("$it has no element-type attribute")))}
            mapDescr.keyClassName = it.attributes["key-class-name"]
            mapDescr.valueClassName = it.attributes["value-class-name"]
            ParserUtils.updateLocalizations(mapDescr, description.id, localizations)
        }
    }

    fun updateMiscMetaRegistry(registry: MiscMetaRegistry, meta: File) {
        val (node, localizations) = ParserUtils.parseMeta(meta)
        updateRegistry(registry, node, localizations)
    }


    fun updateMiscMetaRegistry(registry: MiscMetaRegistry, metaQualifiedName: String, classLoader: ClassLoader) {
        val (node, localizations) = ParserUtils.parseMeta(metaQualifiedName, classLoader)
        updateRegistry(registry, node, localizations)

    }

    private fun updateRegistry(registry: MiscMetaRegistry, node:XmlNode, localizations: Map<String, Map<Locale, String>>) {

        node.children("enum").forEach { child ->
            val enumId = ParserUtils.getIdAttribute(child)
            val enumDescription = registry.enums.getOrPut(enumId) { MiscEnumDescription(enumId) }
            child.children("enum-item").forEach { enumItemElm ->
                val enumItemId = ParserUtils.getIdAttribute(enumItemElm)
                val item = enumDescription.items.getOrPut(enumItemId){ MiscEnumItemDescription(enumItemId) }
                ParserUtils.updateLocalizations(item, enumId, localizations)
            }
        }
        node.children("entity").forEach { child ->
            val id = ParserUtils.getIdAttribute(child)
            val entityDescr = registry.entities.getOrPut(id){ MiscEntityDescription(id) }
            ParserUtils.updateLocalizations(entityDescr, null, localizations)
            fillMiscEntity(child, entityDescr, localizations)
        }

    }
}