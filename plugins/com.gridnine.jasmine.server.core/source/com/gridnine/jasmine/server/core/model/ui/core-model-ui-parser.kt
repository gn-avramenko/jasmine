/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")
package com.gridnine.jasmine.server.core.model.ui


import com.gridnine.jasmine.server.core.model.common.ParserUtils
import com.gridnine.jasmine.server.core.model.common.Xeption
import com.gridnine.jasmine.server.core.utils.XmlNode
import java.io.File
import java.util.*


object UiMetadataParser {



    fun updateUiMetaRegistry(registry: UiMetaRegistry, meta:File) {
        val (node, localizations) = ParserUtils.parseMeta(meta)
        updateRegistry(registry, node, localizations)
    }


    fun updateUiMetaRegistry(registry: UiMetaRegistry, metaQualifiedName: String, classLoader: ClassLoader) {
        val (node, localizations) = ParserUtils.parseMeta(metaQualifiedName, classLoader)
        updateRegistry(registry, node, localizations)

    }

    private fun updateRegistry(registry: UiMetaRegistry, node: XmlNode, localizations: Map<String, Map<Locale, String>>) {
        node.children("enum").forEach { child ->
            val enumId = child.attributes["id"]?:throw Xeption.forDeveloper("id attribute is absent in enum $child")
            val enumDescription = registry.enums.getOrPut(enumId) { UiEnumDescription(enumId) }
            child.children("enum-item").forEach { enumItemElm ->
                val enumItemId = enumItemElm.attributes["id"]?:throw Xeption.forDeveloper("id attribute is absent in enum item $enumItemElm")
                val enumItem = enumDescription.items.getOrPut(enumItemId) { UiEnumItemDescription(enumItemId) }
                ParserUtils.updateLocalizations(enumItem, enumId, localizations)
            }
        }
    }

}