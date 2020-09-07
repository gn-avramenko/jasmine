/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
package com.gridnine.jasmine.server.core.build

import com.gridnine.jasmine.server.core.app.IApplicationMetadataProvider
import com.gridnine.jasmine.server.core.model.common.Xeption
import com.gridnine.jasmine.server.core.model.domain.*
import com.gridnine.jasmine.server.core.model.l10n.L10nMetaRegistry
import com.gridnine.jasmine.server.core.model.l10n.L10nMetadataParser
import java.io.File


internal object L10nWebGenerator {

    fun generateWebClasses(metadataProvider: IApplicationMetadataProvider, generatedFiles: MutableMap<String, MutableList<File>>,  pluginsLocation:Map<String,File>, projectName: String) {
        val l10nData = linkedMapOf<String, MutableList<File>>()
        metadataProvider.getExtensions("web-messages").forEach {
            val destClientPlugin = it.getParameters("dest-client-plugin-id").firstOrNull()
            if (destClientPlugin != null) {
                l10nData.getOrPut(destClientPlugin, { arrayListOf() })
                        .add(File(pluginsLocation[it.plugin.pluginId], it.getParameters("relative-path").first()))
            }
            Unit
        }
        l10nData.entries.forEach { (key, value) ->
            val registry = L10nMetaRegistry()
            value.forEach { metaFile -> L10nMetadataParser.updateWebMessages(registry, metaFile) }
            registry.webMessages.values.forEach { bundle ->
                run {
                    val sb = StringBuilder()
                    GenUtils.generateHeader(sb, "${key}.WebMessages", projectName)

                    GenUtils.classBuilder(sb, "object ${GenUtils.getSimpleClassName(bundle.messagesClassName)}") {
                        blankLine()
                        bundle.messages.values.forEach { message ->
                            "lateinit var ${message.id}:String"()
                        }
                    }
                    val file = File(pluginsLocation[key]
                            ?: throw Xeption.forDeveloper("unable to find basedir of plugin $key"), "source-gen/${GenUtils.getPackageName(bundle.messagesClassName).replace(".", "/")}/${GenUtils.getSimpleClassName(bundle.messagesClassName)}.kt")
                    GenUtils.writeContent(file, sb)
                    generatedFiles.getOrPut(key) { arrayListOf() }.add(file)
                }
                run {
                    val sb = StringBuilder()
                    GenUtils.generateHeader(sb, "${key}.WebMessages", projectName)
                    GenUtils.classBuilder(sb, "object ${GenUtils.getSimpleClassName(bundle.messagesClassName.substringBeforeLast("JS"))}InitializerJS") {
                        blankLine()
                        "fun initialize()"{
                            "val messages = com.gridnine.jasmine.server.core.model.l10n.L10nMetaRegistryJS.get().messages"()
                            bundle.messages.values.forEach { message ->
                                "${GenUtils.getSimpleClassName(bundle.messagesClassName)}.${message.id} = messages[\"${bundle.id}\"]!![\"${message.id}\"]!!"()
                            }

                        }
                    }
                    val file = File(pluginsLocation[key]
                            ?: throw Xeption.forDeveloper("unable to find basedir of plugin $key"), "source-gen/${GenUtils.getPackageName(bundle.messagesClassName).replace(".", "/")}/${GenUtils.getSimpleClassName(bundle.messagesClassName.substringBeforeLast("JS"))}InitializerJS.kt")
                    GenUtils.writeContent(file, sb)
                    generatedFiles.getOrPut(key) { arrayListOf() }.add(file)
                }
            }
        }

    }

}