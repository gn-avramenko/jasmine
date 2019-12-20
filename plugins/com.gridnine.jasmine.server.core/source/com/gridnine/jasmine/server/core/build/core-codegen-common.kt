/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")

package com.gridnine.jasmine.server.core.build

import com.gridnine.jasmine.server.core.model.domain.EntityReference
import com.gridnine.jasmine.server.core.storage.search.*
import java.io.File
import java.lang.IllegalArgumentException
import java.math.BigDecimal
import java.time.LocalDateTime


internal class GenClassData(val id: String, val extends: String?, val abstract: Boolean, val enum: Boolean, val noEnumProperties: Boolean) {
    var generateBuilder = false
    val properties = arrayListOf<GenPropertyDescription>()
    val collections = arrayListOf<GenCollectionDescription>()
    val codeInjections = arrayListOf<String>()
    val enumItems = arrayListOf<String>()
}

internal class GenPropertyDescription(val id: String, val type: GenPropertyType, val className: String?, var lateinit: Boolean = false, var nonNullable: Boolean = false)
internal class GenCollectionDescription(val id: String, val elementType: GenPropertyType, var elementClassName: String?)

internal enum class GenPropertyType {

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
    BYTE_ARRAY

}

internal object GenUtils {

    fun generateHeader(sb:StringBuilder, className: String, projectName: String, supressUnchecked:Boolean){
        sb.append("""
                /*****************************************************************
                 * Gridnine AB http://www.gridnine.com
                 * Project: $projectName
                 * This file is auto generated, don't modify it manually
                 *****************************************************************/
            """.trimIndent())
        val packageName = getPackageName(className)
        if(supressUnchecked) {
            sb.append("\n\n@file:Suppress(\"unused\",\"UNCHECKED_CAST\")")
        } else {
            sb.append("\n\n@file:Suppress(\"unused\")")
        }
        sb.append("\n\npackage $packageName\n")
    }

    fun generateClasses(classes: List<GenClassData>, baseDir: File, projectName: String,generatedFiles:MutableList<File>) {
        classes.forEach { it ->

            val sb = StringBuilder()
            generateHeader(sb, it.id, projectName, true)
            if (it.enum) {
                classBuilder(sb, "enum class ${getSimpleClassName(it.id)}") {
                    blankLine()
                    it.enumItems.withIndex().forEach { (idx, value) ->
                        if (idx < it.enumItems.size - 1) "$value,"() else value()
                    }
                    blankLine()
                }
            } else {
                classBuilder(sb, "${if (it.abstract) "abstract " else ""}class ${getSimpleClassName(it.id)}():${it.extends!!}()") {
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
                            prop.lateinit -> "lateinit var ${prop.id}:${getPropertyType(prop.type, prop.className)}"()
                            prop.nonNullable -> "var ${prop.id}:${getPropertyType(prop.type, prop.className)}=${if(getPropertyType(prop.type, prop.className) == "Boolean") "false" else "0"}"()
                            else -> "var ${prop.id}:${getPropertyType(prop.type, prop.className)}?=null"()
                        }
                    }
                    it.collections.forEach { coll ->
                        blankLine()
                        "val ${coll.id} = arrayListOf<${getPropertyType(coll.elementType, coll.elementClassName)}>()"()
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
                        "override fun getCollection(collectionName: String): MutableCollection<Any>" {
                            it.collections.forEach { prop ->
                                blankLine()
                                "if(\"${prop.id}\" == collectionName)"{
                                    "return this.${prop.id} as MutableCollection<Any>"()
                                }
                            }
                            blankLine()
                            "return super.getCollection(collectionName)"()
                        }
                    }
                    it.codeInjections.forEach {
                        blankLine()
                        it()
                    }

                    if (!it.noEnumProperties) {

                        var idx = 0
                        it.properties.forEach { propDescr ->
                            blankLine()
                            val sb2 = StringBuilder()
                            sb2.append("class _TestDomainDocumentIndexProperty${idx}(name:String):${PropertyNameSupport::class.qualifiedName}(name)")
                            when (propDescr.type) {
                                GenPropertyType.LONG, GenPropertyType.INT -> sb2.append(",${EqualitySupport::class.qualifiedName}," +
                                        "${ComparisonSupport::class.qualifiedName},${NumberOperationsSupport::class.qualifiedName},${SortableProperty::class.qualifiedName}")
                                GenPropertyType.LOCAL_DATE_TIME -> sb2.append(",${ComparisonSupport::class.qualifiedName},${SortableProperty::class.qualifiedName}")
                                GenPropertyType.LOCAL_DATE -> sb2.append(",${EqualitySupport::class.qualifiedName},${ComparisonSupport::class.qualifiedName},${SortableProperty::class.qualifiedName}")
                                GenPropertyType.ENTITY_REFERENCE -> sb2.append(",${EqualitySupport::class.qualifiedName},${StringOperationsSupport::class.qualifiedName}")
                                GenPropertyType.BIG_DECIMAL -> sb2.append(",${ComparisonSupport::class.qualifiedName},${NumberOperationsSupport::class.qualifiedName},${SortableProperty::class.qualifiedName}")
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
                                "val ${propDescr.id} = _TestDomainDocumentIndexProperty${idx}(\"${propDescr.id}\")"()
                                idx++
                            }
                            it.collections.forEach { collDescr ->
                                "val ${collDescr.id} = _TestDomainDocumentIndexProperty${idx}(\"${collDescr.id}\")"()
                                idx++
                            }
                        }
                    }
                }
            }
            val file = File(baseDir, "source-gen/${getPackageName(it.id).replace(".", "/")}/${getSimpleClassName(it.id)}.kt")
            if (!file.parentFile.exists()) {
                file.parentFile.mkdirs()
            }
            val content = sb.toString().toByteArray()
            if (!file.exists() || !content.contentEquals(file.readBytes())) {
                file.writeBytes(content)
            }
            generatedFiles.add(file)
        }
    }

    internal fun deleteFiles(baseDir: File, generatedFiles: List<File>) {
        baseDir.listFiles()?.forEach {
            if(it.isFile && !generatedFiles.contains(it)){
                it.delete()
            }
            if(it.isDirectory){
                deleteFiles(it, generatedFiles)
                if(it.listFiles()?.size?:0 >0){
                    it.delete()
                }
            }

        }
    }

    private fun getPropertyType(type: GenPropertyType, className: String?): String {
        return when (type) {
            GenPropertyType.LONG -> "Long"
            GenPropertyType.LOCAL_DATE_TIME -> LocalDateTime::class.qualifiedName!!
            GenPropertyType.LOCAL_DATE -> LocalDateTime::class.qualifiedName!!
            GenPropertyType.INT -> "Int"
            GenPropertyType.ENTITY_REFERENCE -> "${EntityReference::class.qualifiedName}<${className}>"
            GenPropertyType.BIG_DECIMAL -> BigDecimal::class.qualifiedName!!
            GenPropertyType.DOUBLE -> "Double"
            GenPropertyType.BYTE_ARRAY -> "ByteArray"
            GenPropertyType.STRING -> "String"
            GenPropertyType.BOOLEAN -> "Boolean"
            else -> className ?: throw IllegalArgumentException("classname is null")
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


    internal open class UNIT(val sb: StringBuilder, private val indent: Int) {


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

        fun blankLine() {
            sb.append("\n")
        }


    }

    internal class CLASS(sb: StringBuilder) : UNIT(sb, 1) {

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
}


