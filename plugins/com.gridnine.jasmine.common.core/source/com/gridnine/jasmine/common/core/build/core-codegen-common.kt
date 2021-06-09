/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")

package com.gridnine.jasmine.common.core.build

import com.gridnine.jasmine.common.core.model.*
import java.io.File
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

open class BaseGenData(val id: String)

class GenClassData(id: String, val extends: String?, val abstract: Boolean, val noEnumProperties: Boolean, val open:Boolean = false, val implementCachedObject:Boolean=false):BaseGenData(id) {
    var generateBuilder = false
    val properties = arrayListOf<GenPropertyDescription>()
    val collections = arrayListOf<GenCollectionDescription>()
    val maps = arrayListOf<GenMapDescription>()
    val codeInjections = arrayListOf<String>()
}

class GenEnumData(id:String):BaseGenData(id) {
    val enumItems = arrayListOf<String>()
    val codeInjections = arrayListOf<String>()
}


class GenPropertyDescription(val id: String, val type: GenPropertyType, val className: String?, var lateinit: Boolean = false, var nonNullable: Boolean = false, var openSetter:Boolean = false, var disallowedSetter:Boolean = false, var override:Boolean = false, val useBuilder:Boolean = false)
class GenCollectionDescription(val id: String, val elementType: GenPropertyType, var elementClassName: String?, var openGetter:Boolean = false, var readonlyImpl:Boolean = false)
class GenMapDescription(val id: String, val keyType: GenPropertyType, var keyClassName: String?, val valueType: GenPropertyType, var valuesClassName: String?)

enum class GenPropertyType {

    STRING,
    ENUM,
    ENTITY,
    LONG,
    INT,
    BIG_DECIMAL,
    DOUBLE,
    ENTITY_REFERENCE,
    LOCAL_DATE_TIME,
    LOCAL_DATE,
    BOOLEAN,
    BYTE_ARRAY,
    DATE,
    CLASS

}

object GenUtils {

    fun generateHeader(sb:StringBuilder, className: String, projectName: String){
        sb.append("""
                /*****************************************************************
                 * Gridnine AB http://www.gridnine.com
                 * Project: $projectName
                 * This file is auto generated, don't modify it manually
                 *****************************************************************/
            """.trimIndent())
        val packageName = getPackageName(className)
        sb.append("\n\n@file:Suppress(\"unused\",\"RemoveRedundantQualifierName\",\"UNCHECKED_CAST\",\"MemberVisibilityCanBePrivate\",\"RemoveEmptyPrimaryConstructor\", \"FunctionName\")")
        sb.append("\n\npackage $packageName\n")
    }

    fun generateClasses(classes: List<BaseGenData>, baseDir: File, projectName: String,generatedFiles:MutableList<File>) {
        classes.forEach {
            val sb = StringBuilder()
            generateHeader(sb, it.id, projectName)
            when(it){
                is GenClassData ->{
                    classBuilder(sb, "${if(it.open && !it.abstract) "open " else ""}${if (it.abstract) "abstract " else ""}class ${getSimpleClassName(it.id)}():${it.extends!!}()${if(it.implementCachedObject) ", "+CachedObject::class.java.name else ""}") {
                        if (!(it.abstract) && it.generateBuilder) {
                            blankLine()
                            """
                               constructor(init: ${getSimpleClassName(it.id)}.() ->Unit):this(){
                                        this.init()
                                   }
                            """.trimIndent()()
                        }
                        it.properties.forEach { prop ->
                            blankLine()
                            when {
                                prop.lateinit -> "${if(prop.override) "override " else ""}lateinit var ${prop.id}:${getPropertyType(prop.type, prop.className)}"()
                                prop.nonNullable -> "${if(prop.override) "override " else ""} var ${prop.id}:${getPropertyType(prop.type, prop.className)}=${
                                when(getPropertyType(prop.type, prop.className)){
                                    "Boolean" ->"false"
                                    "String" ->"\"\""
                                    else ->"0"
                                }}"()
                                else -> "${if(prop.override) "override " else ""}${if(prop.openSetter) "open " else ""}var ${prop.id}:${getPropertyType(prop.type, prop.className)}?=null"()
                            }
                            if(prop.disallowedSetter) {
                                "set(value) = if(allowChanges) field = value else throw ${Xeption::class.java.name}.forDeveloper(\"illegal setter call in ${it.id}\")"()
                            }
                            if(prop.useBuilder){
                                blankLine()
                                "fun set${prop.id.capitalize()}(configure: ${prop.className}.()->Unit)"{
                                    "${prop.id} = ${prop.className}()"()
                                    "${prop.id}!!.configure()"()
                                }
                            }
                        }
                        it.collections.forEach { coll ->
                            blankLine()
                            "${if(coll.readonlyImpl) "override " else ""}${if(coll.openGetter) "open " else ""}val ${coll.id} = ${if(coll.readonlyImpl) ReadOnlyArrayList::class.java.name else "arrayListOf"}<${getPropertyType(coll.elementType, coll.elementClassName)}>()"()
                        }
                        it.maps.forEach { map ->
                            blankLine()
                            "val ${map.id} = hashMapOf<${getPropertyType(map.keyType, map.keyClassName)}?,${getPropertyType(map.valueType, map.valuesClassName)}?>()"()
                        }


                        if (it.properties.isNotEmpty()) {
                            blankLine()
                            "override fun getValue(propertyName: String): Any?" {
                                it.properties.forEach { prop ->
                                    blankLine()
                                    "if(\"${prop.id}\" == propertyName)"{
                                        "return this.${prop.id}"()
                                    }
                                }
                                blankLine()
                                "return super.getValue(propertyName)"()
                            }

                            blankLine()
                            "override fun setValue(propertyName:String, value:Any?)"{
                                it.properties.forEach { prop ->
                                    blankLine()
                                    "if(\"${prop.id}\" == propertyName)"{
                                        if (prop.lateinit || prop.nonNullable) {
                                            "this.${prop.id}=value as ${getPropertyType(prop.type, prop.className)}"()
                                        } else {
                                            "this.${prop.id}=value as ${getPropertyType(prop.type, prop.className)}?"()
                                        }
                                        "return"()
                                    }
                                }
                                blankLine()
                                "super.setValue(propertyName, value)"()
                            }
                        }
                        if (it.collections.isNotEmpty()) {

                            blankLine()
                            "@Suppress(\"UNCHECKED_CAST\")"()
                            "override fun getCollection(collectionName: String): MutableList<Any>" {
                                it.collections.forEach { prop ->
                                    blankLine()
                                    "if(\"${prop.id}\" == collectionName)"{
                                        "return this.${prop.id} as MutableList<Any>"()
                                    }
                                }
                                blankLine()
                                "return super.getCollection(collectionName)"()
                            }
                        }
                        if (it.maps.isNotEmpty()) {

                            blankLine()
                            "@Suppress(\"UNCHECKED_CAST\")"()
                            "override fun getMap(mapName: String): MutableMap<Any?,Any?>" {
                                it.maps.forEach { prop ->
                                    blankLine()
                                    "if(\"${prop.id}\" == mapName)"{
                                        "return this.${prop.id} as MutableMap<Any?,Any?>"()
                                    }
                                }
                                blankLine()
                                "return super.getMap(mapName)"()
                            }
                        }
                        it.codeInjections.forEach { str ->
                            blankLine()
                            str()
                        }

                        if (!it.noEnumProperties) {

                            var idx = 0
                            it.properties.forEach { propDescr ->
                                blankLine()
                                val sb2 = StringBuilder()
                                sb2.append("class _TestDomainDocumentIndexProperty${idx}(name:String):${PropertyNameSupport::class.qualifiedName}(name)")
                                when (propDescr.type) {
                                    GenPropertyType.LONG, GenPropertyType.INT -> sb2.append(",${EqualitySupport::class.qualifiedName}," +
                                            "${ComparisonSupport::class.qualifiedName},${NumberOperationsSupport::class.qualifiedName},${SortSupport::class.qualifiedName}")
                                    GenPropertyType.LOCAL_DATE_TIME -> sb2.append(",${ComparisonSupport::class.qualifiedName},${SortSupport::class.qualifiedName}")
                                    GenPropertyType.LOCAL_DATE -> sb2.append(",${EqualitySupport::class.qualifiedName},${ComparisonSupport::class.qualifiedName},${SortSupport::class.qualifiedName}")
                                    GenPropertyType.ENTITY_REFERENCE -> sb2.append(",${EqualitySupport::class.qualifiedName},${StringOperationsSupport::class.qualifiedName}")
                                    GenPropertyType.BIG_DECIMAL -> sb2.append(",${ComparisonSupport::class.qualifiedName},${NumberOperationsSupport::class.qualifiedName},${SortSupport::class.qualifiedName}")
                                    GenPropertyType.STRING -> sb2.append(",${EqualitySupport::class.qualifiedName},${StringOperationsSupport::class.qualifiedName}")
                                    GenPropertyType.ENUM -> sb2.append(",${EqualitySupport::class.qualifiedName}")
                                    else -> {
                                    }
                                }
                                sb2.toString()()
                                idx++
                            }
                            it.collections.forEach { collDescr ->
                                blankLine()
                                val sb2 = StringBuilder()
                                sb2.append("class _TestDomainDocumentIndexProperty${idx}(name:String):${PropertyNameSupport::class.qualifiedName}(name)")
                                when (collDescr.elementType) {
                                    GenPropertyType.ENTITY_REFERENCE -> sb2.append(",${CollectionSupport::class.qualifiedName}")
                                    GenPropertyType.STRING -> sb2.append(",${CollectionSupport::class.qualifiedName}")
                                    else -> {
                                    }
                                }
                                sb2.toString()()
                                idx++
                            }

                            blankLine()
                            "companion object" {
                                idx = 0
                                it.properties.forEach { propDescr ->
                                    "val ${propDescr.id}Property = _TestDomainDocumentIndexProperty${idx}(\"${propDescr.id}\")"()
                                    idx++
                                }
                                it.collections.forEach { collDescr ->
                                    "val ${collDescr.id}Collection = _TestDomainDocumentIndexProperty${idx}(\"${collDescr.id}\")"()
                                    idx++
                                }
                            }
                        }
                    }
                }
                is GenEnumData ->{
                    classBuilder(sb, "enum class ${getSimpleClassName(it.id)}") {
                        blankLine()
                        it.enumItems.withIndex().forEach { (idx, value) ->
                            if (idx < it.enumItems.size - 1) "$value,"() else "${value};"()
                        }
                        blankLine()
                        it.codeInjections.forEach { str ->
                            blankLine()
                            str()
                        }
                    }
                }
                else -> throw Xeption.forDeveloper("unsupported type $it")
            }
            val file = File(baseDir, "source-gen/${getPackageName(it.id).replace(".", File.separator)}/${getSimpleClassName(it.id)}.kt")
            writeContent(file, sb)
            generatedFiles.add(file)
        }
    }

    fun writeContent(file:File, sb: StringBuilder){
        if (!file.parentFile.exists()) {
            file.parentFile.mkdirs()
        }
        val content = sb.toString().toByteArray()
        if (!file.exists() || !content.contentEquals(file.readBytes())) {
            file.writeBytes(content)
        }
    }

    fun deleteFiles(baseDir: File, generatedFiles: List<File>) {
        baseDir.listFiles()?.forEach {
            if(it.isFile && !generatedFiles.contains(it)){
                it.delete()
            }
            if(it.isDirectory){
                deleteFiles(it, generatedFiles)
                if(it.listFiles()?.size?:0 == 0){
                    it.delete()
                }
            }
        }
    }

    private fun getPropertyType(type: GenPropertyType, className: String?): String {
        return when (type) {
            GenPropertyType.LONG -> "Long"
            GenPropertyType.LOCAL_DATE_TIME -> LocalDateTime::class.qualifiedName!!
            GenPropertyType.LOCAL_DATE -> LocalDate::class.qualifiedName!!
            GenPropertyType.INT -> "Int"
            GenPropertyType.ENTITY_REFERENCE -> "${ObjectReference::class.qualifiedName}<${className}>"
            GenPropertyType.BIG_DECIMAL -> BigDecimal::class.qualifiedName!!
            GenPropertyType.DOUBLE -> "Double"
            GenPropertyType.BYTE_ARRAY -> "ByteArray"
            GenPropertyType.STRING -> "String"
            GenPropertyType.BOOLEAN -> "Boolean"
            GenPropertyType.DATE -> "Date"
            else -> className ?: throw Xeption.forDeveloper("classname is null")
        }

    }

    fun getPackageName(className: String): String {
        return className.substring(0, className.lastIndexOf("."))
    }

    fun getSimpleClassName(className: String): String {
        return className.substring(className.lastIndexOf(".") + 1)
    }



    private fun indent(times: Int, sb: StringBuilder) {
        for (n in 0 until times) {
            sb.append("    ")
        }
    }


    open class UNIT(val sb: StringBuilder, private val indent: Int) {


        operator fun String.invoke() {
            sb.append("\n")
            indent(indent, sb)
            sb.append(this)
        }

        operator fun String.invoke(init: (UNIT.() -> Unit)? = null) {

            val unit = UNIT(sb, indent + 1)
            sb.append("\n")
            indent(indent, sb)
            sb.append("$this{")
            if (init != null) {
                unit.apply(init)
            }
            sb.append("\n")
            indent(indent, sb)
            sb.append("}")
        }

        operator fun String.invoke(lambdaParamName:String, init: (UNIT.() -> Unit)? = null) {

            val unit = UNIT(sb, indent + 1)
            sb.append("\n")
            indent(indent, sb)
            sb.append("$this{ $lambdaParamName ->")
            if (init != null) {
                unit.apply(init)
            }
            sb.append("\n")
            indent(indent, sb)
            sb.append("}")
        }

        fun blankLine() {
            sb.append("\n")
        }


    }

    class CLASS(sb: StringBuilder) : UNIT(sb, 1) {

        fun injection(value: String) {
            sb.append("\n")
            indent(1, sb)
            sb.append(value)
            sb.append("\n")
        }

    }

    fun classBuilder(sb: StringBuilder, className: String, init: CLASS.() -> Unit) {
        sb.append("\n${className}{")
        val cls = CLASS(sb)
        cls.init()
        sb.append("\n}")
    }

    private fun methodBuilder(sb: StringBuilder, method: String, init: UNIT.() -> Unit) {
        sb.append("\n\n\n\n${method}{")
        val unit = UNIT(sb, 0)
        unit.init()
        sb.append("\n}")
    }

    fun saveFile(destPlugin:File, className:String, strContent:String, generatedFiles:MutableList<File>){
        val pluginId = destPlugin.name
        val file = File(destPlugin, "source-gen/${pluginId.replace(".", "/")}/${className}.kt")
        if (!file.parentFile.exists()) {
            file.parentFile.mkdirs()
        }
        val content = strContent.toByteArray()
        if (!file.exists() || !content.contentEquals(file.readBytes())) {
            file.writeBytes(content)
        }
        generatedFiles.add(file)
    }
}


