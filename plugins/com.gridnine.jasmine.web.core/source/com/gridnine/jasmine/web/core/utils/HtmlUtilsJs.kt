/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST")
package com.gridnine.jasmine.web.core.utils


object HtmlUtilsJS {
    interface Element {
        fun render(builder: StringBuilder, indent: String)
    }

    class TextElement(val text: String) : Element {
        override fun render(builder: StringBuilder, indent: String) {
            builder.append("$indent$text\n")
        }
    }

    abstract class Tag(val name: String) : Element {
        val children = arrayListOf<Element>()
        val attributes = hashMapOf<String, String>()

        fun text(content:String){
            children.add(TextElement(content))
        }

        protected fun <T : Element> initTag(tag: T, init: T.() -> Unit): T {
            tag.init()
            children.add(tag)
            return tag
        }

        override fun render(builder: StringBuilder, indent: String) {
            builder.append("$indent<$name${renderAttributes()}>\n")
            for (c in children) {
                c.render(builder, "$indent    ")
            }
            builder.append("$indent</$name>\n")
        }

        protected fun renderAttributes(): String {
            val builder = StringBuilder()
            for ((attr, value) in attributes) {
                builder.append(" $attr=\"$value\"")
            }
            return builder.toString()
        }

        override fun toString(): String {
            val builder = StringBuilder()
            render(builder, "")
            return builder.toString()
        }
    }

    abstract class TagWithText(name: String) : Tag(name) {
        operator fun String.invoke() {
            children.add(TextElement(this))
        }
    }
    abstract class BaseDiv(tagName:String) : TagWithText(tagName) {
        fun div(`class`: String? = null, id: String? = null, region: String? = null, border: Boolean? = null, style: String? = null, data_options: String? = null, init: Div.() -> Unit) {
            val result = initTag(Div(), init)
            result.`class` = `class`
            result.id = id
            result.region = region
            result.border = border
            result.style = style
            result.dataOptions = data_options
        }

        fun nobr(style: String? = null, init: Nobr.() -> Unit) {
            val result = initTag(Nobr(), init)
            result.style = style
        }

        fun ul(`class`: String? = null, id: String? = null, lines: Boolean? = null, style: String? = null, init: UL.() -> Unit) {
            val result = initTag(UL(), init)
            result.`class` = `class`
            result.id = id
            result.lines = lines
            result.style = style
        }

        fun img(src:String) {
            val result = IMG()
            result.src = src
            children.add(result)
        }

        fun input(`class`: String? = null, id: String? = null, style: String? = null) {
            val result = INPUT()
            children.add(result)
            result.`class` = `class`
            result.id = id
            result.style = style
        }

        fun a(href: String? = "javascript(void);;", id: String?, init: A.() -> Unit) {
            val a = initTag(A(), init)
            a.href = href
            a.id = id
        }

        fun table(`class`: String? = null, id: String? = null, style: String? = null, data_options: String? = null, init: TABLE.() -> Unit) {
            val result = initTag(TABLE(), init)
            result.`class` = `class`
            result.id = id
            result.style = style
            result.dataOptions = data_options
        }



        var `class`: String?
            get() = attributes["class"]
            set(value) {
                if (value != null) attributes["class"] = value else attributes.remove("class")
            }
        var id: String?
            get() = attributes["id"]
            set(value) {
                if (value != null) attributes["id"] = value else attributes.remove("id")
            }
        var region: String?
            get() = attributes["region"]
            set(value) {
                if (value != null) attributes["region"] = value else attributes.remove("region")
            }
        var border: Boolean?
            get() = if (attributes["border"] != null) "true" == attributes["border"] else false
            set(value) {
                if (value == true) attributes["border"] = "true" else attributes["border"] = "false"
            }
        var style: String?
            get() = attributes["style"]
            set(value) {
                if (value != null) attributes["style"] = value else attributes.remove("style")
            }
        var dataOptions: String?
            get() = attributes["data-options"]
            set(value) {
                if (value != null) attributes["data-options"] = value else attributes.remove("data-options")
            }
        var split: Boolean?
            get() = attributes["split"]?.toBoolean()?:false
            set(value) {
                attributes["split"] = value?.toString()?:"false"
            }
    }
    class Div : BaseDiv("div")


    class Nobr : TagWithText("nobr") {

        fun input(`class`: String? = null, id: String? = null, style: String? = null) {
            val result = INPUT()
            children.add(result)
            result.`class` = `class`
            result.id = id
            result.style = style
        }

        fun a(href: String? = "#", id: String?, init: A.() -> Unit) {
            val a = initTag(A(), init)
            a.href = href
            a.id = id
        }


        var style: String?
            get() = attributes["style"]
            set(value) {
                if (value != null) attributes["style"] = value else attributes.remove("style")
            }

    }

    class TD : TagWithText("td") {
        fun div(`class`: String? = null, id: String? = null, style: String? = null, init: Div.() -> Unit) {
            val result = initTag(Div(), init)
            result.`class` = `class`
            result.id = id
            result.style = style
        }

        fun input(`class`: String? = null, id: String? = null, style: String? = null) {
            val result = INPUT()
            children.add(result)
            result.`class` = `class`
            result.id = id
            result.style = style
        }

        fun a(href: String?= null, id: String?, init: A.() -> Unit) {
            val a = initTag(A(), init)
            a.href = href
            a.id = id
        }

        var `class`: String?
            get() = attributes["class"]
            set(value) {
                if (value != null) attributes["class"] = value else attributes.remove("class")
            }
        var id: String?
            get() = attributes["id"]
            set(value) {
                if (value != null) attributes["id"] = value else attributes.remove("id")
            }
        var style: String?
            get() = attributes["style"]
            set(value) {
                if (value != null) attributes["style"] = value else attributes.remove("style")
            }
        var hSpan: Int?
            get() = attributes["colspan"]?.toInt()
            set(value) {
                if (value != null) attributes["colspan"] = value.toString() else attributes.remove("colspan")
            }
    }

    class TR : TagWithText("tr") {
        var style: String?
            get() = attributes["style"]
            set(value) {
                if (value != null) attributes["style"] = value else attributes.remove("style")
            }
        fun td(`class`: String? = null, id: String? = null, style: String? = null, hSpan:Int =1, init: TD.() -> Unit) {
            val result = initTag(TD(), init)
            result.`class` = `class`
            result.id = id
            result.style = style
            result.hSpan= hSpan
        }

    }

    class UL : TagWithText("ul") {

        fun li(init: LI.() -> Unit) {
            initTag(LI(), init)
        }

        var `class`: String?
            get() = attributes["class"]
            set(value) {
                if (value != null) attributes["class"] = value else attributes.remove("class")
            }
        var id: String?
            get() = attributes["id"]
            set(value) {
                if (value != null) attributes["id"] = value else attributes.remove("id")
            }
        var lines: Boolean?
            get() = if (attributes["lines"] != null) "true" == attributes["lines"] else false
            set(value) {
                if (value == true) attributes["lines"] = "true" else attributes["lines"] = "false"
            }
        var style: String?
            get() = attributes["style"]
            set(value) {
                if (value != null) attributes["style"] = value else attributes.remove("style")
            }


    }

    class TABLE : Tag("table") {

        var `class`: String?
            get() = attributes["class"]
            set(value) {
                if (value != null) attributes["class"] = value else attributes.remove("class")
            }
        var id: String?
            get() = attributes["id"]
            set(value) {
                if (value != null) attributes["id"] = value else attributes.remove("id")
            }
        var style: String?
            get() = attributes["style"]
            set(value) {
                if (value != null) attributes["style"] = value else attributes.remove("style")
            }
        var dataOptions: String?
            get() = attributes["data-options"]
            set(value) {
                if (value != null) attributes["data-options"] = value else attributes.remove("data-options")
            }

        fun tr(style:String?=null, init: TR.() -> Unit) {
            val result = initTag(TR(), init)
            result.style = style
        }
    }

    class LI : Tag("li") {
        fun a(href: String?, id: String?, init: A.() -> Unit) {
            val a = initTag(A(), init)
            a.href = href
            a.id = id
        }
        operator fun String.invoke() {
            children.add(TextElement(this))
        }
    }

    class A : TagWithText("a") {
        var href: String?
            get() = attributes["href"]
            set(value) {
                if (value != null) attributes["href"] = value else attributes.remove("href")
            }
        var id: String?
            get() = attributes["id"]
            set(value) {
                if (value != null) attributes["id"] = value else attributes.remove("id")
            }
    }

    class IMG : TagWithText("img") {
        var src: String?
            get() = attributes["src"]
            set(value) {
                if (value != null) attributes["src"] = value else attributes.remove("src")
            }
    }

    class INPUT : Tag("input") {
        var `class`: String?
            get() = attributes["class"]
            set(value) {
                if (value != null) attributes["class"] = value else attributes.remove("class")
            }
        var id: String?
            get() = attributes["id"]
            set(value) {
                if (value != null) attributes["id"] = value else attributes.remove("id")
            }
        var style: String?
            get() = attributes["style"]
            set(value) {
                if (value != null) attributes["style"] = value else attributes.remove("style")
            }

        override fun render(builder: StringBuilder, indent: String) {
            builder.append("$indent<$name${renderAttributes()}>\n")
        }
    }

    class HTML : BaseDiv("html") {

        override fun render(builder: StringBuilder, indent: String) {
            for (c in children) {
                c.render(builder, indent + "")
            }
        }

        override fun toString(): String {
            val sb = StringBuilder()
            render(sb, " ")
            return sb.toString()
        }
    }

    fun html(init: HTML.() -> Unit): HTML {
        val html = HTML()
        html.init()
        return html
    }

    fun div(`class`: String? = null, id: String? = null, region: String? = null, border: Boolean? = null, style: String? = null, data_options: String? = null, init: Div.() -> Unit):Div {
        val div = Div()
        div.`class` = `class`
        div.id = id
        div.region = region
        div.border = border
        div.style = style
        div.dataOptions = data_options
        div.init()
        return div
    }

    fun table(`class`: String? = null, id: String? = null, style: String? = null, data_options: String? = null, init: TABLE.() -> Unit):TABLE {
        val result = TABLE()
        result.`class` = `class`
        result.id = id
        result.style = style
        result.dataOptions = data_options
        result.init()
        return  result
    }

}