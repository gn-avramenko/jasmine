/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("unused")

package com.gridnine.jasmine.web.easyui.components

import com.gridnine.jasmine.web.core.remote.launch
import com.gridnine.jasmine.web.core.ui.components.WebSearchBox
import com.gridnine.jasmine.web.core.ui.components.WebSearchBoxConfiguration
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS

class EasyUiWebSearchBox(configure: WebSearchBoxConfiguration.()->Unit) :WebSearchBox,EasyUiComponent{

    private var initialized = false

    private val uid = MiscUtilsJS.createUUID()

    private var jq:dynamic = null

    private var searcher: (suspend (String?) ->Unit)? = null

    private var enabled = true

    private val config = WebSearchBoxConfiguration()
    init {
        config.configure()
    }

    override fun getHtml(): String {
        return "<input id=\"searchBox${uid}\" style=\"${if(config.width != null) "width:${config.width}" else ""};${if(config.height != null) "height:${config.height}" else ""}\"/>"
    }

    override fun setSearcher(value: suspend (String?) -> Unit) {
        searcher = value
    }

    override fun getValue(): String? {
        return jq.searchbox("getValue")
    }

    override fun decorate() {
        jq = jQuery("#searchBox$uid")
        jq.searchbox(object{
            val prompt = config.prompt
            val searcher = {value:String?,_:String? ->
                this@EasyUiWebSearchBox.searcher?.let {
                    launch {
                        it.invoke(value)
                    }
                }
            }
            val disabled = !enabled
        })
        initialized = true
    }

    override fun setEnabled(value: Boolean) {
        if(enabled != value){
            enabled = value
            if(initialized){
                jq.searchbox(if (enabled) "enable" else "disable")
            }
        }
    }

    override fun destroy() {
        //noops
    }

}