/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.common.core.build

import com.gridnine.jasmine.common.core.app.IApplicationMetadataProvider
import com.gridnine.jasmine.common.core.reflection.ReflectionFactory
import com.gridnine.jasmine.common.core.utils.XmlUtils
import java.io.File


internal class CodeGenRegistry(pluginsMap:Map<String,File>,pluginsRegistry:IApplicationMetadataProvider){
    private val codeGen = arrayListOf<CodeGenMetadata>()
    private val generators= hashMapOf<String, CodeGeneratorMetadata>()
    private val sources = hashMapOf<String, CodeSourceMetadata>()
    private val pluginsIndex = hashMapOf<String, Int>()
    init {
        val plugins = pluginsRegistry.plugins
        plugins.withIndex().forEach { (idx, plugin) ->pluginsIndex[plugin.pluginId] = idx }
        plugins.forEach {plugin ->
            val codeGenFile = File(pluginsMap[plugin.pluginId], "code-gen.xml")
            if(codeGenFile.exists()){
                val root = XmlUtils.parseXml(codeGenFile.readBytes())
                root.children("source").forEach {source ->
                    sources[source.attributes["id"]!!] = CodeSourceMetadata(source.attributes["id"]!!,plugin.pluginId, source.attributes["factoryClassName"], File(pluginsMap[plugin.pluginId], source.attributes["location"]!!) )
                }
                root.children("generator").forEach {generator ->
                    generators[generator.attributes["id"]!!] = CodeGeneratorMetadata(generator.attributes["id"]!!,generator.attributes["className"]!!, generator.attributes["priority"]!!.toInt())
                }
                root.children("generate").forEach {generate ->
                    codeGen.add(CodeGenMetadata(generate.attributes["generator"]!!,generate.attributes["targetPlugin"]!!, generate.attributes["source"]!!))
                }
            }
        }
    }

    internal fun getGeneratorsClassNames():List<String>{
        return codeGen.map { generators[it.generatorId]!! }.distinct().sortedBy { it.priority }.map { it.className }
    }

    internal fun getSources(pluginId: String, generatorClassName:String):List<Pair<File, String?>>{
        return codeGen.filter { generators[it.generatorId]!!.className == generatorClassName && it.targetPluginId == pluginId }
                .map { sources[it.sourceId]!! }.sortedBy { pluginsIndex[it.sourcePluginId]!! }.map { Pair(it.location, it.factoryClassName) }
    }

    internal fun getPluginsIds(generatorClassName:String):List<String>{
        return codeGen.filter{ generators[it.generatorId]!!.className == generatorClassName }.map { it.targetPluginId }.distinct()
    }

    private data class CodeGenMetadata(val generatorId:String, val targetPluginId:String, val sourceId:String)

    private data class CodeGeneratorMetadata(val id:String, val className:String, val priority:Int)

    private data class CodeSourceMetadata(val id:String,  val sourcePluginId:String, val factoryClassName:String?, val location:File)

}

interface CodeGenerator{
    fun generate(destPlugin:File, sources:List<Pair<File,String?>>, projectName:String, generatedFiles:MutableList<File>, context:MutableMap<String,Any>)
}

object CodeGeneratorTask{
    fun generate(pluginsMap:Map<String,File>, projectName: String, pluginsRegistry:IApplicationMetadataProvider){
        val registry = CodeGenRegistry(pluginsMap, pluginsRegistry)
        val generatedFiles = hashMapOf<String, MutableList<File>>()
        val genContext = hashMapOf<String,Any>()
        registry.getGeneratorsClassNames().forEach {generatorClassName ->
            val generator = ReflectionFactory.get().newInstance<CodeGenerator>(generatorClassName)
            registry.getPluginsIds(generatorClassName).forEach { pluginId ->
                generator.generate(pluginsMap[pluginId]!!, registry.getSources(pluginId, generatorClassName),  projectName, generatedFiles.computeIfAbsent(pluginId){arrayListOf()},  genContext)
            }
        }
        generatedFiles.entries.forEach {
            GenUtils.deleteFiles(File(pluginsMap[it.key], "source-gen"), it.value)
        }
    }
}