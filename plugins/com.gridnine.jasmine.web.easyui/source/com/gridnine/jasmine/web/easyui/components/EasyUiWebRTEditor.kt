/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.easyui.components

import com.gridnine.jasmine.web.core.ui.components.WebRichTextEditor
import com.gridnine.jasmine.web.core.ui.components.WebRichTextEditorConfiguration
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS

@Suppress("unused")
class EasyUiWebRTEditor(configure: WebRichTextEditorConfiguration.()->Unit) :WebRichTextEditor,EasyUiComponent{

    private var initialized = false

    private val uid = MiscUtilsJS.createUUID()

    private var jq:dynamic = null

    private var value:String? = null

    private var disabled = false

    private val config = WebRichTextEditorConfiguration()
    init {
        config.configure()
    }

    override fun getId(): String {
        return "rtEditor${uid}"
    }
    override fun getHtml(): String {
        return "<div id=\"rtEditor${uid}\" style=\"${getSizeAttributes(config)}\"/>"
    }

    override fun getContent(): String? {
        if(!initialized){
            return value
        }
        return jq.texteditor("getValue")
    }

    override fun setContent(content: String?){
        if(!initialized){
            this.value = content
            return
        }
        updateContentInternal()

    }

    private fun updateContentInternal() {
        jq.texteditor("setValue", value)
    }

    override fun setDisabled(value: Boolean) {
        disabled = value
        if(initialized) {
            updateDisabledInternal()
        }
    }

    private fun updateDisabledInternal() {
        if (disabled) {
            jq.texteditor("disable")
        } else {
            jq.texteditor("enable")
        }
    }

    override fun decorate() {
        jq = jQuery("#rtEditor$uid")
        jq.texteditor(object{})
        initialized = true
        updateDisabledInternal()
        updateContentInternal()
    }

    override fun destroy() {
        if(initialized){
            jq.texteditor("destroy")
        }
    }

}