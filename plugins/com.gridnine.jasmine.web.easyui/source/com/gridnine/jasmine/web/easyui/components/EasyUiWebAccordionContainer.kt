/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("unused")

package com.gridnine.jasmine.web.easyui.components

import com.gridnine.jasmine.web.core.ui.components.WebAccordionContainer
import com.gridnine.jasmine.web.core.ui.components.WebAccordionContainerConfiguration
import com.gridnine.jasmine.web.core.ui.components.WebAccordionPanel
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS

class EasyUiWebAccordionContainer(configure: WebAccordionContainerConfiguration.()->Unit) :WebAccordionContainer,EasyUiComponent{
    private var initialized = false
    private val panels = arrayListOf<WebAccordionPanel>()
    private var selected:Int = 0
    private var jq:dynamic = null
    private val uid = MiscUtilsJS.createUUID()
    private val config = WebAccordionContainerConfiguration()
    init {
        config.configure()
    }

    override fun addPanel(configure: WebAccordionPanel.() -> Unit) {
        val panel = WebAccordionPanel()
        panel.configure()
        panels.add(panel)
        selected = panels.size-1
        if(initialized){
            addPanelInternal(panel)
        }
    }

    override fun select(id: String) {
        val idx = panels.indexOfFirst { it.id == id}
        if(idx !=-1) {
            selected = idx
            if (initialized) {
                jq!!.accordion("select", idx)
            }
        }
    }

    override fun getPanels(): List<WebAccordionPanel> {
        return panels
    }

    private fun addPanelInternal(panel: WebAccordionPanel) {
        jq!!.accordion("add", object{
            val id = panel.id
            val title = panel.title
            val content = findEasyUiComponent(panel.content).getHtml()
        })
        findEasyUiComponent(panel.content).decorate()
    }

    override fun removePanel(id: String) {
        val panel = panels.find { it.id == id}
        if(panel != null){
            val idx = panels.indexOf(panel)
            panels.removeAt(idx)
            if(initialized){
                jq!!.accordion("remove", idx)
            }
        }
    }

    override fun getId(): String {
        return "accordion${uid}"
    }

    override fun getHtml(): String {
        return "<div id=\"accordion${uid}\" style=\"${getSizeAttributes(config)}\"></div>"
    }

    override fun decorate() {
        jq = jQuery("#accordion${uid}")
        jq.accordion(object{
            val fit = config.fit
        })
        panels.forEach {
            addPanelInternal(it)
        }
        jq!!.accordion("select", selected)
        initialized = true
    }

    override fun destroy() {
        getPanels().forEach { findEasyUiComponent(it.content).destroy() }
    }

}