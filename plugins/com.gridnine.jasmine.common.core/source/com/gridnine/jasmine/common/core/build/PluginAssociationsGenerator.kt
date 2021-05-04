/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.common.core.build

import com.gridnine.jasmine.common.core.meta.WebPluginsAssociationsRegistry
import java.io.File
import java.util.HashMap

@Suppress("UNCHECKED_CAST")
internal object PluginAssociationsGenerator {

    const val WEB_MAP_KEY = "web-associations"
    const val COMMON_MAP_KEY = "common-associations"


    fun generate(pluginsMap: Map<String, File>,  genContext: HashMap<String, Any>, generatedFiles: HashMap<String, MutableList<File>>) {
        val result = hashMapOf<String, HashMap<String,String>>()
        val webMap =   genContext[WEB_MAP_KEY] as Map<String,String>
        val commonMap =   genContext[COMMON_MAP_KEY] as Map<String,String>
        webMap.entries.forEach { we ->
            val commonName = we.key.substringBeforeLast("JS")
            val commonPlugin = commonMap[commonName]!!
            result.getOrPut(commonPlugin){ hashMapOf()}[we.key] = we.value
        }
        result.entries.forEach {entry ->
            val commonPluginId = entry.key
            val sb = StringBuilder()
            GenUtils.generateHeader(sb, "${commonPluginId}.WebPluginsAssociations", commonPluginId)
            GenUtils.classBuilder(sb, "object WebPluginsAssociations") {
                blankLine()
                "fun registerAssociations()"{
                    "val registry =${WebPluginsAssociationsRegistry::class.qualifiedName}.get()"()
                   entry.value.entries.forEach {e2->
                       """registry.associations["${e2.key.substringBeforeLast("JS")}"]="${e2.value}""""()
                   }
                }
            }

            val file = File(pluginsMap[commonPluginId]!!, "source-gen/${commonPluginId.replace(".", "/")}/WebPluginsAssociations.kt")
            if (!file.parentFile.exists()) {
                file.parentFile.mkdirs()
            }
            val content = sb.toString().toByteArray()
            if (!file.exists() || !content.contentEquals(file.readBytes())) {
                file.writeBytes(content)
            }
            generatedFiles[commonPluginId]!!.add(file)
        }
    }
}