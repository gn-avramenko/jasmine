/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
package com.gridnine.jasmine.common.core.build

import com.gridnine.jasmine.common.core.meta.L10nMessageParameterDescription
import com.gridnine.jasmine.common.core.meta.L10nMessageParameterType
import com.gridnine.jasmine.common.core.meta.L10nMetaRegistry
import com.gridnine.jasmine.common.core.model.L10nMessage
import com.gridnine.jasmine.common.core.model.ObjectReference
import com.gridnine.jasmine.common.core.parser.L10nMetadataParser
import java.io.File
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime


class L10nCommonGenerator:CodeGenerator {

    private fun getParameterWithType(param: L10nMessageParameterDescription):String{
        return "${param.id}:${getType(param)}?"
    }
    private fun getType(param: L10nMessageParameterDescription):String?{
        return when (param.type){
            L10nMessageParameterType.STRING -> "String"
            L10nMessageParameterType.LOCAL_DATE -> LocalDate::class.qualifiedName
            L10nMessageParameterType.LOCAL_DATE_TIME -> LocalDateTime::class.qualifiedName
            L10nMessageParameterType.ENUM -> param.className
            L10nMessageParameterType.BOOLEAN -> "Boolean"
            L10nMessageParameterType.ENTITY_REFERENCE -> "${ObjectReference::class.qualifiedName}<${param.className}>"
            L10nMessageParameterType.LONG -> "Long"
            L10nMessageParameterType.INT -> "Int"
            L10nMessageParameterType.BIG_DECIMAL -> BigDecimal::class.qualifiedName
        }
    }

    override fun generate(destPlugin: File, sources: List<Pair<File, String?>>, projectName: String, generatedFiles: MutableList<File>, context: MutableMap<String, Any>) {

        sources.forEach { source ->
            val registry = L10nMetaRegistry()
            L10nMetadataParser.updateL10nMessages(registry, source.first)
            registry.bundles.values.forEach { bundle ->
                run {
                    val sb = StringBuilder()
                    GenUtils.generateHeader(sb, source.second!!, projectName)

                    GenUtils.classBuilder(sb, "object ${GenUtils.getSimpleClassName(source.second!!)}") {
                        blankLine()
                        "const val bundle = \"${bundle.id}\""()
                        blankLine()
                        bundle.messages.values.forEach { message ->
                            "fun ${message.id}Message(${message.params.map { getParameterWithType(it.value) }.joinToString() }) = ${L10nMessage::class.qualifiedName}(bundle, \"${message.id}\" ${if(message.params.isEmpty())"" else ","} ${message.params.map { "${it.key}?:\"???\"" }.joinToString()})"()
                            blankLine()
                            "fun ${message.id}(${message.params.map { getParameterWithType(it.value) }.joinToString() }) = ${message.id}Message(${message.params.map { it.value.id }.joinToString() }).toString()"()
                            blankLine()
                        }
                    }
                    val file = File(destPlugin, "source-gen/${GenUtils.getPackageName(source.second!!).replace(".", "/")}/${GenUtils.getSimpleClassName(source.second!!)}.kt")
                    GenUtils.writeContent(file, sb)
                    generatedFiles.add(file)
                }
            }
        }
    }
}