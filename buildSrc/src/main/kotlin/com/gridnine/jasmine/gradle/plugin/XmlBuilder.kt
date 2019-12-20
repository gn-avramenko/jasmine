/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")

package com.gridnine.jasmine.gradle.plugin


interface Element {
    fun render(builder: StringBuilder, indent: String)
}

class TextElement(private val text: String) : Element {
    override fun render(builder: StringBuilder, indent: String) {
        builder.append("$indent$text\n")
    }
}

open class Tag(private val name: String, vararg attributes:Pair<String,String>):Element  {
    private val children = arrayListOf<Element>()

    private val localAttributes = attributes.toMutableList()


    private fun <T : Tag> initTag(tag: T, init: T.() -> Unit): T {
        tag.init()
        children.add(tag)
        return tag
    }

    fun attribute(attr:Pair<String,String>){
        localAttributes.add(attr)
    }

    override fun render(builder: StringBuilder, indent: String) {
        if(children.isEmpty()){
            builder.append("$indent<$name${renderAttributes()}/>\n")
            return
        }
        builder.append("$indent<$name${renderAttributes()}>\n")
        for (c in children) {
            c.render(builder, "$indent   ")
        }
        builder.append("$indent</$name>\n")
    }

    private fun renderAttributes(): String {
        val builder = StringBuilder()
        for ((attr, value) in localAttributes) {
            builder.append(" $attr=\"$value\"")
        }
        return builder.toString()
    }

    override fun toString(): String {
        val builder = StringBuilder()
        render(builder, "")
        return builder.toString()
    }

    operator fun String.invoke(){
        children.add(TextElement(this))
    }

    operator fun String.invoke(vararg attributes:Pair<String,String>,init: Tag.() -> Unit){
        initTag(Tag(this,*attributes), init)
    }

    fun emptyTag(name:String, vararg attributes:Pair<String,String>){
        children.add(Tag(name,*attributes))
    }
}



fun xml(rootTag:String, vararg attributes:Pair<String,String>, init: Tag.() -> Unit): String {
    val xml = Tag(rootTag, *attributes)
    xml.init()
    return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n$xml"
}
