/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
package com.gridnine.jasmine.server.core.build

import com.gridnine.jasmine.server.core.app.IApplicationMetadataProvider
import com.gridnine.jasmine.server.core.model.common.ServerMessage
import com.gridnine.jasmine.server.core.model.common.Xeption
import com.gridnine.jasmine.server.core.model.domain.ObjectReference
import com.gridnine.jasmine.server.core.model.l10n.L10nMetaRegistry
import com.gridnine.jasmine.server.core.model.l10n.L10nMetadataParser
import com.gridnine.jasmine.server.core.model.l10n.ServerMessageParameterDescription
import com.gridnine.jasmine.server.core.model.l10n.ServerMessageParameterType
import java.io.File
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime


internal object L10nServerGenerator {

    fun generateServerClasses(metadataProvider: IApplicationMetadataProvider, generatedFiles: MutableMap<String, MutableList<File>>,  pluginsLocation:Map<String,File>, projectName: String) {
        val l10nData = linkedMapOf<String, MutableList<File>>()
        metadataProvider.getExtensions("server-messages").forEach {
            val destServerPlugin = it.getParameters("dest-server-plugin-id").firstOrNull()
            if (destServerPlugin != null) {
                l10nData.getOrPut(destServerPlugin, { arrayListOf() })
                        .add(File(pluginsLocation[it.plugin.pluginId], it.getParameters("relative-path").first()))
            }
            Unit
        }
        l10nData.entries.forEach { (key, value) ->
            val registry = L10nMetaRegistry()
            value.forEach { metaFile -> L10nMetadataParser.updateServerMessages(registry, metaFile) }
            registry.serverMessages.values.forEach { bundle ->
                run {
                    val sb = StringBuilder()
                    GenUtils.generateHeader(sb, "${key}.ServerMessagesFactory", projectName)

                    GenUtils.classBuilder(sb, "object ${GenUtils.getSimpleClassName(bundle.factoryClassName)}") {
                        blankLine()
                        "const val bundle = \"${bundle.id}\""()
                        blankLine()
                        bundle.messages.values.forEach { message ->
                            "fun ${message.id}(${message.params.map { getParameterWithType(it.value) }.joinToString() }) = ${ServerMessage::class.qualifiedName}(bundle, \"${message.id}\" ${if(message.params.isEmpty())"" else ","} ${message.params.map { "${it.key}?:\"???\"" }.joinToString()})"()
                        }
                    }
                    val file = File(pluginsLocation[key]
                            ?: throw Xeption.forDeveloper("unable to find basedir of plugin $key"), "source-gen/${GenUtils.getPackageName(bundle.factoryClassName).replace(".", "/")}/${GenUtils.getSimpleClassName(bundle.factoryClassName)}.kt")
                    GenUtils.writeContent(file, sb)
                    generatedFiles.getOrPut(key) { arrayListOf() }.add(file)
                }
            }
        }
    }

    private fun getParameterWithType(param: ServerMessageParameterDescription):String{
        return "${param.id}:${getType(param)}?"
    }
    private fun getType(param: ServerMessageParameterDescription):String?{
        return when (param.type){
            ServerMessageParameterType.STRING -> "String"
            ServerMessageParameterType.LOCAL_DATE -> LocalDate::class.qualifiedName
            ServerMessageParameterType.LOCAL_DATE_TIME -> LocalDateTime::class.qualifiedName
            ServerMessageParameterType.ENUM -> param.className
            ServerMessageParameterType.BOOLEAN -> "Boolean"
            ServerMessageParameterType.ENTITY_REFERENCE -> "${ObjectReference::class.qualifiedName}<${param.className}>"
            ServerMessageParameterType.LONG -> "Long"
            ServerMessageParameterType.INT -> "Int"
            ServerMessageParameterType.BIG_DECIMAL -> BigDecimal::class.qualifiedName
        }
    }

}