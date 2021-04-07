/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")
package com.gridnine.jasmine.server.core.model.l10n

import com.gridnine.jasmine.server.core.model.common.ParserUtils
import com.gridnine.jasmine.server.core.model.ui.*
import com.gridnine.jasmine.server.core.utils.XmlNode
import java.io.File
import java.util.*


object L10nMetadataParser {


    fun updateServerMessages(registry: L10nMetaRegistry, meta: File) {
        val (node, localizations) = ParserUtils.parseMeta(meta)
        updateServerMessages(registry, node, localizations)
    }
    fun updateServerMessages(registry: L10nMetaRegistry, metaQualifiedName: String, classLoader: ClassLoader) {
        val (node, localizations) = ParserUtils.parseMeta(metaQualifiedName,classLoader)
        updateServerMessages(registry, node, localizations)
    }

    fun updateWebMessages(registry: L10nMetaRegistry, meta: File) {
        val (node, localizations) = ParserUtils.parseMeta(meta)
        updateWebMessages(registry, node, localizations)
    }
    fun updateWebMessages(registry: L10nMetaRegistry, metaQualifiedName: String, classLoader: ClassLoader) {
        val (node, localizations) = ParserUtils.parseMeta(metaQualifiedName,classLoader)
        updateWebMessages(registry, node, localizations)
    }

    fun updateWebMessages(registry: L10nMetaRegistry, uiRegistry:UiMetaRegistry){
        uiRegistry.views.values.forEach {vd->
            val bundle = WebMessagesBundleDescription(vd.id,"")
            registry.webMessages[bundle.id] = bundle
            when(vd.viewType){
                ViewType.GRID_CONTAINER ->{
                    vd as GridContainerDescription
                    vd.rows.forEach {row ->
                        row.cells.forEach{cell ->
                            val message = WebMessageDescription(cell.id)
                            message.displayNames.putAll(cell.displayNames)
                            bundle.messages[cell.id] = message
                            val widget = cell.widget
                            if(widget is TableBoxWidgetDescription){
                                val bundle2 = WebMessagesBundleDescription(widget.id,"")
                                registry.webMessages[widget.id] = bundle2
                                widget.columns.forEach{column ->
                                    val message2 = WebMessageDescription(column.id)
                                    message2.displayNames.putAll(column.displayNames)
                                    bundle2.messages[column.id] = message2
                                }
                            }
                        }
                    }
                }
                ViewType.TILE_SPACE ->{
                    vd as TileSpaceDescription
                    vd.overviewDescription?.let {
                        val message = WebMessageDescription("overview")
                        message.displayNames.putAll(it.displayNames)
                        bundle.messages[it.id] = message
                    }
                    vd.tiles.forEach {
                        val message = WebMessageDescription(it.id)
                        message.displayNames.putAll(it.displayNames)
                        bundle.messages[it.id] = message
                    }
                }
            }
        }
    }

    private fun updateServerMessages(registry: L10nMetaRegistry, node: XmlNode, localizations:Map<String, Map<Locale, String>>){
        val bundleDescription = ServerMessagesBundleDescription(ParserUtils.getIdAttribute(node), node.attributes["factory-class"]!!)
        registry.serverMessages[bundleDescription.id] = bundleDescription
        node.children("message").forEach { messageNode ->
            val messageId = ParserUtils.getIdAttribute(messageNode)
            val descr = ServerMessageDescription(messageId)
            bundleDescription.messages[messageId] = descr
            messageNode.children("parameter").forEach { paramNode ->
               val param = ServerMessageParameterDescription(ParserUtils.getIdAttribute(paramNode), ServerMessageParameterType.valueOf(paramNode.attributes["type"]!!))
                descr.params[param.id] = param
                param.className = paramNode.attributes["class-name"]
                param.collection = "true" == paramNode.attributes["collection"]
            }
            ParserUtils.updateLocalizationsForId(descr, descr.id, localizations)
        }
    }

    private fun updateWebMessages(registry: L10nMetaRegistry, node: XmlNode, localizations:Map<String, Map<Locale, String>>){
        val bundleDescription = WebMessagesBundleDescription(ParserUtils.getIdAttribute(node), node.attributes["messages-class"]!!)
        registry.webMessages[bundleDescription.id] = bundleDescription
        node.children("message").forEach { messageNode ->
            val descr = WebMessageDescription(ParserUtils.getIdAttribute(messageNode))
            bundleDescription.messages[descr.id] = descr
            ParserUtils.updateLocalizations(descr, localizations, descr.id)
        }
    }


}