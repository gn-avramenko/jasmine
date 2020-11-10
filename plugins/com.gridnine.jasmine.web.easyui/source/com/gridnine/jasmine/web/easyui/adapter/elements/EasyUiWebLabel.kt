/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.easyui.adapter.elements

import com.gridnine.jasmine.web.core.ui.WebComponent
import com.gridnine.jasmine.web.core.ui.components.WebLabel
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import com.gridnine.jasmine.web.easyui.adapter.jQuery

class EasyUiWebLabel(private val parent:WebComponent?) :WebLabel{

    private var initialized = false

    private val uid = MiscUtilsJS.createUUID()

    private var text:String? = null

    private val children = arrayListOf<WebComponent>()

    private val classes = hashSetOf<String>()

    private var width:String? = null
    private var height:String? = null

    init {
        (parent?.getChildren() as MutableList<WebComponent>?)?.add(this)
    }

    override fun getHtml(): String {
        return "<div style=\"display:inline-block\" id = \"label$uid\"></div>"
    }

    private fun getSelector() = "#label$uid"

    override fun setText(value: String?) {
        text = value
        if(initialized){
            jQuery(getSelector()).html(text)
        }
    }
    override fun decorate() {
        val jq = jQuery(getSelector()).html(text)
        classes.forEach { jq.addClass(it) }
        if(width != null){
            jq.width(width)
        }
        if(height != null){
            jq.height(height)
        }
        initialized = true
    }

    override fun destroy() {
        //noops
    }

    override fun addClass(className: String) {
        classes.add(className)
        if(initialized){
            val jq = jQuery(getSelector())
            if(!jq.hasClass(className)){
                jq.addClass(className)
            }

        }
    }

    override fun removeClass(className: String) {
        classes.remove(className)
        if(initialized){
            jQuery(getSelector()).removeClass(className)
        }
    }

    override fun getId(): String {
        return "label$uid"
    }

    override fun setWidth(value: String) {
        width = value
        if(initialized){
            jQuery(getSelector()).width(width)
        }
    }

    override fun setHeight(value: String) {
        height = value
        if(initialized){
            jQuery(getSelector()).height(height)
        }
    }

    override fun getParent(): WebComponent? {
        return parent
    }

    override fun getChildren(): MutableList<WebComponent> {
        return children
    }

}