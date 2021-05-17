/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.easyui.components

import com.gridnine.jasmine.common.core.model.XeptionJS
import com.gridnine.jasmine.web.core.ui.components.WebDivsContainer
import com.gridnine.jasmine.web.core.ui.components.WebDivsContainerConfiguration
import com.gridnine.jasmine.web.core.ui.components.WebNode
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS

class EasyUiWebDivsContainer(configure:WebDivsContainerConfiguration.()->Unit) :WebDivsContainer,EasyUiComponent{
    private val divsMap = hashMapOf<String,WebDivData>()
    private var initialized = false
    private val uid = MiscUtilsJS.createUUID()
    private var activeComponentId:String? = null
    private var jq:dynamic = null
    private val conf = WebDivsContainerConfiguration()
    init {
        conf.configure()
    }
    override fun addDiv(id: String, content: WebNode) {
        divsMap[id] = WebDivData("div${id}${MiscUtilsJS.createUUID()}", content, null)
    }

    override fun show(id: String) {
        if(activeComponentId != id){
            if(initialized){
                showInternal(id)
            }
            activeComponentId = id
        }
    }

    private fun showInternal(id: String) {
        if(activeComponentId != null) {
            val compData = divsMap[activeComponentId!!]!!
            if (compData.jq != null) {
                compData.jq.hide()
            }
        }
        val divData = divsMap[id]?:throw XeptionJS.forDeveloper("div with id $id does not exist")
        if(divData.jq == null){
            val easyUiComp = findEasyUiComponent(divData.content)
            jq.append("""
                <div id = "${divData.id}" style="width:100%;height:100%">
                    ${easyUiComp.getHtml()}
                </div>
            """.trimIndent())
            divData.jq = jQuery("#${divData.id}")
            easyUiComp.decorate()
        } else {
            divData.jq.show()
        }
    }

    override fun removeDiv(id: String) {
        divsMap[id]?.let {
            if(it.jq != null){
                findEasyUiComponent(it.content).destroy()
                it.jq.remove()
            }
            divsMap.remove(id)
            activeComponentId = null
        }
    }

    override fun getDiv(id: String): WebNode? {
        return divsMap[id]?.content
    }

    override fun clear() {
        ArrayList(divsMap.keys).forEach { removeDiv(it) }
    }


    override fun getHtml(): String {
        return "<div id=\"divsContainer${uid}\" style=\"${if(conf.width != null) "width:${conf.width}" else ""};${if(conf.height != null) "height:${conf.height}" else ""}\"/>"
    }

    override fun decorate() {
        jq = jQuery("#${getId()}")
        initialized = true
        activeComponentId?.let {
            showInternal(it)
        }

    }

    override fun destroy() {
        divsMap.values.forEach { findEasyUiComponent(it.content).destroy() }
    }

    override fun getId(): String {
        return "divsContainer${uid}"
    }

}

internal class WebDivData(val id:String, val content:WebNode, var jq:dynamic)