/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jtasks
 *****************************************************************/
package com.gridnine.jasmine.web.antd.components


import com.gridnine.jasmine.web.core.remote.launch
import com.gridnine.jasmine.web.core.ui.components.*

class AntdWebTag(private val tagName: String, private val id: String?) : WebTag, BaseAntdWebUiComponent() {

    private val children = AntdTagChildren(this)

    private var text: String? = null

    private var handlers = hashMapOf<String, (suspend (dynamic) -> Unit)?>()

    private var postRenderAction: (() -> Unit)? = null

    private val style = AntdTagStyle(this)

    private val attributes = AntdTagAttributes(this)

    private val classes = AntdTagClass(this)

    private var visible = true

    override fun createReactElementWrapper(): ReactElementWrapper {
        return ReactFacade.createProxyAdvanced({
            if (text != null) {
                ReactFacade.createElementWithChildren(tagName, createProps(), text!!.asDynamic())
            } else {
                val ch = children.map {
                    children.elementCache.getOrPut(it) {
                        findAntdComponent(it).getReactElement()
                    }
                }
                ReactFacade.createElementWithChildren(tagName, createProps(), ch.toTypedArray())
            }
        }, if(postRenderAction == null) null else object {
            val componentDidMount = {
                postRenderAction!!.invoke()
            }
        })
    }

    private fun createProps(): dynamic {
        val result = js("{}")
        if (id != null) {
            result.id = id
        }
        attributes.entries.forEach {
            result[it.key] = it.value
        }
        handlers.forEach {
            if (it.value != null) {
                result["on${it.key.capitalize()}"] = { event: dynamic ->
                    launch {
                        it.value!!.invoke(event)
                    }
                }
            }
        }
        if(classes.isNotEmpty()){
            result.className = classes.joinToString(" ") { it }
        }
        if (style.isNotEmpty()) {
            val styleParam = js("{}")
            style.entries.forEach {
                styleParam[it.key] = it.value
            }
            result.style = styleParam
        }
        return result
    }

    override fun getName(): String {
        return tagName
    }

    override fun setText(value: String?) {
        if (text != value) {
            text = value
            maybeRedraw()
        }
    }

    override fun getChildren(): TagChildren {
        return children
    }

    override fun getStyle(): TagStyle {
        return style
    }

    override fun setVisible(value: Boolean) {
        if (visible != value) {
            visible = value
            maybeRedraw()
        }
    }

    override fun getClass(): TagClass {
        return classes
    }

    override fun getAttributes(): TagAttributes {
        return attributes
    }

    override fun setEventHandler(event: String, handler: (suspend (dynamic) -> Unit)?) {
        handlers[event] = handler
        maybeRedraw()
    }

    override fun setPostRenderAction(action: (() -> Unit)?) {
        postRenderAction = action
        maybeRedraw()
    }

    override fun getId(): String? {
        return id
    }

    class AntdTagChildren(private val parent: AntdWebTag) : ArrayList<WebNode>(), TagChildren {

        var elementCache = hashMapOf<WebNode, ReactElement>()

        override fun addChild(child: WebNode) {
            super.add(child)
            parent.maybeRedraw()
        }

        override fun addChild(position: Int, child: WebNode) {
            super.add(position, child)
            parent.maybeRedraw()
        }

        override fun removeChild(child: WebNode) {
            super.remove(child)
            elementCache.remove(child)
            parent.maybeRedraw()
        }

        override fun clear() {
            super.clear()
            elementCache.clear()
            parent.maybeRedraw()
        }

        override fun moveChild(fromPosition: Int, toPosition: Int) {
            val elm = super.removeAt(fromPosition)
            super.add(toPosition, elm)
            parent.maybeRedraw()
        }
    }

    class AntdTagStyle(private val parent: AntdWebTag) : LinkedHashMap<String, String>(), TagStyle {
        override fun setParameters(vararg params: Pair<String, String>) {
            params.forEach {
                super.put(it.first, it.second)
            }
            parent.maybeRedraw()
        }

        override fun removeParameters(vararg params: String) {
            params.forEach { super.remove(it) }
            parent.maybeRedraw()
        }

    }


    class AntdTagAttributes(private val parent: AntdWebTag) : LinkedHashMap<String, String>(), TagAttributes {
        override fun setAttributes(vararg attrs: Pair<String, String>) {
            attrs.forEach { super.put(it.first, it.second) }
            parent.maybeRedraw()
        }

        override fun removeAttributes(vararg attrs: String) {
            attrs.forEach { super.remove(it) }
            parent.maybeRedraw()
        }

    }

    class AntdTagClass(private val parent: AntdWebTag) : LinkedHashSet<String>(), TagClass {

        override fun addClasses(vararg classes: String) {
            classes.forEach { super.add(it) }
            parent.maybeRedraw()
        }

        override fun removeClasses(vararg classes: String) {
            classes.forEach { super.remove(it) }
            parent.maybeRedraw()
        }
    }
}