/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.easyui.components

import com.gridnine.jasmine.web.core.ui.components.WebLabel
import com.gridnine.jasmine.web.core.ui.components.WebLabelConfiguration
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS

class EasyUiWebLabel(configure: WebLabelConfiguration.()->Unit) :WebLabel,EasyUiComponent{

    private var initialized = false

    private val uid = MiscUtilsJS.createUUID()

    private var text:String? = null

    private val config = WebLabelConfiguration()
    init {
        config.configure()
    }

    override fun getHtml(): String {
        return """<div style="display:inline-block; ${getSizeAttributes(config)}" id = "label$uid"></div>"""
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
        config.className?.let{ jq.addClass(it) }
        initialized = true
    }

    override fun destroy() {
        //noops
    }

}