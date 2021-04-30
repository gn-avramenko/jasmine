/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
package com.gridnine.jasmine.common.core.parser

import com.gridnine.jasmine.common.core.meta.*
import com.gridnine.jasmine.common.core.utils.XmlNode
import java.io.File
import java.util.*


object WebMessagesMetadataParser {


    fun updateWebMessages(registry: WebMessagesMetaRegistry, meta: File) {
        val (node, localizations) = ParserUtils.parseMeta(meta)
        updateWebMessages(registry, node, localizations)
    }
    fun updateWebMessages(registry: WebMessagesMetaRegistry, metaQualifiedName: String, classLoader: ClassLoader) {
        val (node, localizations) = ParserUtils.parseMeta(metaQualifiedName,classLoader)
        updateWebMessages(registry, node, localizations)
    }

    private fun updateWebMessages(registry: WebMessagesMetaRegistry, node: XmlNode, localizations:Map<String, Map<Locale, String>>){
        val bundleDescription = WebMessagesBundleDescription(ParserUtils.getIdAttribute(node))
        registry.bundles[bundleDescription.id] = bundleDescription
        node.children("message").forEach { messageNode ->
            val messageId = ParserUtils.getIdAttribute(messageNode)
            val descr = WebMessageDescription(messageId)
            bundleDescription.messages[messageId] = descr
            ParserUtils.updateLocalizationsForId(descr, descr.id, localizations)
        }
    }


}