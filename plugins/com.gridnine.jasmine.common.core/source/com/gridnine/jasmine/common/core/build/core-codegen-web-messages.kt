/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("UNCHECKED_CAST")

package com.gridnine.jasmine.common.core.build

import com.gridnine.jasmine.common.core.meta.WebMessagesMetaRegistry
import com.gridnine.jasmine.common.core.parser.WebMessagesMetadataParser
import java.io.File


class WebMessagesGenerator :CodeGenerator{

    override fun generate(destPlugin: File, sources: List<Pair<File, String?>>, projectName: String, generatedFiles: MutableList<File>, context: MutableMap<String, Any>) {
        val registry = WebMessagesMetaRegistry()
        sources.forEach { metaFile -> WebMessagesMetadataParser.updateWebMessages(registry, metaFile.first) }
        val pluginId = destPlugin.name
        val webMapping = context[PluginAssociationsGenerator.WEB_MAP_KEY] as HashMap<String, String>
        registry.bundles.values.forEach { bundle ->
            webMapping["web-messages-${bundle.id}"] = pluginId
            run {
                val sb = StringBuilder()
                GenUtils.generateHeader(sb, "${pluginId}.WebMessages", projectName)

                GenUtils.classBuilder(sb, "object WebMessages") {
                    blankLine()
                    bundle.messages.values.forEach { message ->
                        "lateinit var ${message.id}:String"()
                    }
                }
                val file = File(destPlugin, "source-gen/${pluginId.replace(".", "/")}/WebMessages.kt")
                GenUtils.writeContent(file, sb)
                generatedFiles.add(file)
            }
            run {
                val sb = StringBuilder()
                GenUtils.generateHeader(sb, "${pluginId}.WebMessages", projectName)
                GenUtils.classBuilder(sb, "object WebMessagesInitializerJS") {
                    blankLine()
                    "fun initialize()"{
                        "val messages = com.gridnine.jasmine.common.core.meta.L10nMetaRegistryJS.get().messages"()
                        bundle.messages.values.forEach { message ->
                            "WebMessages.${message.id} = messages[\"${bundle.id}\"]!![\"${message.id}\"]!!"()
                        }

                    }
                }
                val file = File(destPlugin, "source-gen/${pluginId.replace(".", "/")}/WebMessagesInitializerJS.kt")
                GenUtils.writeContent(file, sb)
                generatedFiles.add(file)
            }
        }
    }
}