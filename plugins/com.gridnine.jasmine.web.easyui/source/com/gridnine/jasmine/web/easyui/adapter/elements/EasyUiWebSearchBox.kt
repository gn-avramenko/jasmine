/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.easyui.adapter.elements

import com.gridnine.jasmine.web.core.ui.WebComponent
import com.gridnine.jasmine.web.core.ui.components.WebSearchBox
import com.gridnine.jasmine.web.core.ui.components.WebSearchBoxConfiguration
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import com.gridnine.jasmine.web.easyui.adapter.jQuery

class EasyUiWebSearchBox(private val parent:WebComponent?, configure: WebSearchBoxConfiguration.()->Unit) :WebSearchBox{

    private var initialized = false

    private val uid = MiscUtilsJS.createUUID()

    private var width:String? = null
    private var height:String? = null
    private var prompt:String? = null
    private var jq:dynamic = null
    private lateinit var searcher:(String?) ->Unit

    init {
        (parent?.getChildren() as MutableList<WebComponent>?)?.add(this)
        val configuration = WebSearchBoxConfiguration()
        configuration.configure()
        width = configuration.width
        height = configuration.height
        prompt = configuration.prompt
    }

    override fun getHtml(): String {
        return "<input id=\"searchBox${uid}\" style=\"${if(width != null) "width:$width" else ""};${if(height != null) "height:$height" else ""}\"/>"
    }

    override fun setSearcher(value: (String?) -> Unit) {
        searcher = value
    }

    override fun getValue(): String? {
        return jq.searchbox("getValue")
    }


    override fun getParent(): WebComponent? {
        return parent
    }

    override fun getChildren(): List<WebComponent> {
        return emptyList()
    }


    override fun decorate() {
        jq = jQuery("#searchBox$uid")
        jq.searchbox(object{
            val prompt = this@EasyUiWebSearchBox.prompt
            val searcher = {value:String?,_:String? ->
                this@EasyUiWebSearchBox.searcher.invoke(value)
            }
        })
        initialized = true
    }

    override fun destroy() {
        //noops
    }

}