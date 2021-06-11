/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.easyui.components

import com.gridnine.jasmine.common.core.model.XeptionJS
import com.gridnine.jasmine.web.core.remote.launch
import com.gridnine.jasmine.web.core.ui.components.*
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS


class EasyUiWebTag(private val tagName:String, private val id:String?) :EasyUiComponent,WebTag{

    private val children = EasyUiTagChildren(this)

    private var text:String?  = null

    internal var initialized = false

    private var jq:dynamic = null

    private var handlers = hashMapOf<String, (suspend (dynamic) -> Unit)?>()

    private var postRenderAction : (() -> Unit)? = null

    private val style = EasyUiTagStyle(this)

    private val attributes = EasyUiTagAttributes(this)

    private val classes = EasyUiTagClass(this)

    private var visible = true

    override fun getId(): String? {
        return id
    }

    override fun getHtml(): String {
        return """
            <${tagName} ${if(MiscUtilsJS.isNotBlank(id)) "id=\"$id\"" else ""} ${if(style.isNotEmpty()) "style =\"${style.getStyleAttribute()}\""  else ""}  ${if(attributes.isNotEmpty()) attributes.entries.joinToString(" ") { "${it.key} = \"${it.value}\"" } else ""} ${if(classes.isNotEmpty()) "class=\"${classes.getClassValue()}\"" else ""}>
                ${if(MiscUtilsJS.isNotBlank(text)) text else children.joinToString("\r\n") { findEasyUiComponent(it).getHtml() }}
            </${tagName}>
        """.trimIndent()

    }

    override fun decorate() {
        children.forEach { findEasyUiComponent(it).decorate() }
        handlers.forEach {
            if(it.value != null) {
                getJQ().on(it.key) { arg:dynamic ->
                    launch {
                        it.value!!.invoke(arg)
                    }
                }
            }
        }

        initialized = true
        setVisible(visible)
        if(postRenderAction != null){
            postRenderAction!!.invoke()
        }
    }

    override fun destroy() {
        if(initialized) {
            children.forEach { findEasyUiComponent(it) }
        }
    }

    override fun getName(): String {
        return tagName
    }

    override fun setText(value: String?) {
        text = value
        if(initialized){
            destroy()
            getJQ().html(value)
        }
    }

    internal fun getJQ():dynamic{
        if(jq != null){
            return jq
        }
        if(MiscUtilsJS.isBlank(id)){
            throw  XeptionJS.forDeveloper("unable to perform operation without specified id")
        }
        jq = jQuery("#$id")
        return jq
    }
    override fun getChildren(): TagChildren {
        return children
    }

    override fun getStyle(): TagStyle {
        return style
    }

    override fun setVisible(value: Boolean) {
        visible = value
        if(initialized){
            if(getId() != null) {
                if (visible) {
                    getJQ().show()
                } else {
                    getJQ().hide()
                }
            }
        }
    }

    override fun getClass(): TagClass {
        return classes
    }

    override fun getAttributes(): TagAttributes {
        return attributes
    }

    override fun setEventHandler(event: String, handler: (suspend (dynamic) -> Unit)?) {
        val oldVal = handlers[event]
        handlers[event] = handler
        if(initialized) {
            if (oldVal != null) {
                getJQ().off(event, "**")
            }
            if(handler != null) {
                getJQ().on(event) { arg: dynamic ->
                    launch {
                        handler.invoke(arg)
                    }
                }
            }
        }
    }

    override fun setPostRenderAction(action: (() -> Unit)?) {
        postRenderAction = action
        if(initialized && postRenderAction != null){
            postRenderAction!!.invoke()
        }
    }


}

class EasyUiTagChildren(private val parent:EasyUiWebTag) : ArrayList<WebNode>(), TagChildren{
    override fun addChild(child: WebNode) {
        super.add(child)
        if(parent.initialized) {
            parent.getJQ().append(findEasyUiComponent(child).getHtml())
            findEasyUiComponent(child).decorate()
        }
    }

    override fun addChild(position: Int, child: WebNode) {
        super.add(position, child)
        if(parent.initialized){
            if (position == 0) {
                parent.getJQ().prepend(findEasyUiComponent(child).getHtml())
            } else {
                parent.getJQ().children("tr").eq(position - 1).after(findEasyUiComponent(child).getHtml())
            }
            findEasyUiComponent(child).decorate()
        }
    }

    override fun removeChild(child: WebNode) {
        val idx = indexOf(child)
        super.remove(child)
        if(parent.initialized){
            val comp = findEasyUiComponent(child)
            comp.destroy()
            parent.getJQ().children().eq(idx).remove()
        }
    }

    override fun clear() {
        forEach {
            findEasyUiComponent(it).destroy()
        }
        super.clear()
        if(parent.initialized) {
            parent.getJQ().html("")
        }
    }

    override fun moveChild(fromPosition: Int, toPosition: Int) {
        val elm = super.removeAt(fromPosition)
        super.add(toPosition, elm)
        if(parent.initialized){
            val jq = parent.getJQ()
            if (toPosition > fromPosition) {
                jq.children().eq(fromPosition).insertAfter(jq.children().eq(toPosition))
            } else {
                jq.children().eq(fromPosition).insertBefore(jq.children().eq(toPosition))
            }
        }
    }
}

class EasyUiTagStyle(private val parent:EasyUiWebTag) : LinkedHashMap<String, String>(),TagStyle{
    override fun setParameters(vararg params: Pair<String, String>) {
        params.forEach {
            super.put(it.first,it.second)
        }
        if(parent.initialized){
            updateStyle(parent.getJQ())
        }
    }

    private fun updateStyle(jq: dynamic) {
        jq.attr("style", getStyleAttribute())
    }

    internal fun getStyleAttribute():String{
        return this.entries.joinToString(";") { "${it.key}:${it.value}" }
    }

    override fun removeParameters(vararg params: String) {
        params.forEach { super.remove(it) }
        if(parent.initialized){
            updateStyle(parent.getJQ())
        }
    }

}


class EasyUiTagAttributes(private val parent:EasyUiWebTag) : LinkedHashMap<String, String>(),TagAttributes{
    override fun setAttributes(vararg attrs: Pair<String, String>) {
        attrs.forEach { super.put(it.first, it.second) }
        if(parent.initialized){
            super.entries.forEach {
                parent.getJQ().attr(it.key, it.value)
            }
        }
    }

    override fun removeAttributes(vararg attrs: String){
        attrs.forEach { super.remove(it) }
        if(parent.initialized){
            attrs.forEach {
                parent.getJQ().removeAttr(it)
            }
        }
    }

}

class EasyUiTagClass(private val parent:EasyUiWebTag) : LinkedHashSet<String>(),TagClass{

    override fun addClasses(vararg classes: String) {
        classes.forEach { super.add(it) }
        if(parent.initialized){
            updateClassAttribute()
        }
    }

    private fun updateClassAttribute() {
        parent.getJQ().attr("class", getClassValue())
    }

    internal fun getClassValue(): String {
        return joinToString (" "){ it }
    }

    override fun removeClasses(vararg classes: String) {
        classes.forEach { super.remove(it) }
        if(parent.initialized){
            updateClassAttribute()
        }
    }


}