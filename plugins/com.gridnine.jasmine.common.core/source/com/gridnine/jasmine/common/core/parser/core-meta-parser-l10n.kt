/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
package com.gridnine.jasmine.common.core.parser

import com.gridnine.jasmine.common.core.meta.*
import com.gridnine.jasmine.common.core.utils.XmlNode
import java.io.File
import java.util.*


object L10nMetadataParser {


    fun updateL10nMessages(registry: L10nMetaRegistry, meta: File) {
        val (node, localizations) = ParserUtils.parseMeta(meta)
        updateL10nMessages(registry, node, localizations)
    }
    fun updateL10nMessages(registry: L10nMetaRegistry, metaQualifiedName: String, classLoader: ClassLoader) {
        val (node, localizations) = ParserUtils.parseMeta(metaQualifiedName,classLoader)
        updateL10nMessages(registry, node, localizations)
    }

    private fun updateL10nMessages(registry: L10nMetaRegistry, node: XmlNode, localizations:Map<String, Map<Locale, String>>){
        val bundleDescription = L10nMessagesBundleDescription(ParserUtils.getIdAttribute(node))
        registry.bundles[bundleDescription.id] = bundleDescription
        node.children("message").forEach { messageNode ->
            val messageId = ParserUtils.getIdAttribute(messageNode)
            val descr = L10nMessageDescription(messageId)
            bundleDescription.messages[messageId] = descr
            messageNode.children("parameter").forEach { paramNode ->
               val param = L10nMessageParameterDescription(ParserUtils.getIdAttribute(paramNode), L10nMessageParameterType.valueOf(paramNode.attributes["type"]!!))
                descr.params[param.id] = param
                param.className = paramNode.attributes["class-name"]
                param.collection = "true" == paramNode.attributes["collection"]
            }
            ParserUtils.updateLocalizationsForId(descr, descr.id, localizations)
        }
    }



}