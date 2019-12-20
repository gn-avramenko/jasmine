/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 *****************************************************************/
@file:Suppress("unused")
package com.gridnine.jasmine.server.core.utils


import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.ByteArrayInputStream
import javax.xml.parsers.DocumentBuilderFactory


class XmlNode{
    lateinit var name:String
    var value:String? = null
    val children = arrayListOf<XmlNode>()
    val attributes = linkedMapOf<String, String>()

    fun children(name:String):List<XmlNode>{
        return children.filter { it.name == name }
    }

    override fun toString(): String {
        return name
    }
}


object XmlUtils {
    fun parseXml(content: ByteArray): XmlNode {
        val result = XmlNode()
        val db = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        lateinit var doc: Document
        try {
            doc = db.parse(ByteArrayInputStream(content))
        } finally {
            db.reset()
        }
        updateElm(result, doc.documentElement)
        return result
    }

    private fun updateElm(result: XmlNode, elm: Element) {
        result.name = elm.tagName
        val attributes = elm.attributes
        val attributeLength = attributes.length
        for (i in 0 until attributeLength) {
            val item = attributes.item(i)
            result.attributes[item.nodeName] = item.nodeValue
        }
        val children = elm.childNodes
        val childrenLength = children.length
        loop@ for (i in 0 until childrenLength) {
            var child: Node? = children.item(i)
            if (child == null) {
                child = children.item(i)
            }
            if (child != null) {
                when (child.nodeType) {
                    Node.TEXT_NODE -> {
                        result.value = child.nodeValue
                    }
                    Node.CDATA_SECTION_NODE -> {
                        result.value = child.nodeValue
                        break@loop
                    }
                    Node.ELEMENT_NODE -> {
                        val childNode = XmlNode()
                        updateElm(childNode, child as Element)
                        result.children.add(childNode)
                    }
                }
            }
        }
    }

}