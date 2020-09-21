/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.easyui.adapter.elements

import com.gridnine.jasmine.web.core.ui.WebComponent
import com.gridnine.jasmine.web.core.ui.components.WebAccordionContainer
import com.gridnine.jasmine.web.core.ui.components.WebAccordionPanel
import com.gridnine.jasmine.web.core.ui.components.WebAccordionPanelConfiguration
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import com.gridnine.jasmine.web.easyui.adapter.jQuery

class EasyUiWebAccordionContainer(private val parent:WebComponent?, configure: WebAccordionPanelConfiguration.()->Unit) :WebAccordionContainer{
    private var initialized = false
    private val fit:Boolean
    private val panels = arrayListOf<WebAccordionPanel>()
    private var selected:Int = 0
    private var jq:dynamic = null
    private val children = arrayListOf<WebComponent>()
    private val width:String?
    private val height:String?
    private val uid = MiscUtilsJS.createUUID()
    init {
        parent?.getChildren()?.add(this)
        val configuration = WebAccordionPanelConfiguration()
        configuration.configure()
        fit = configuration.fit
        width = configuration.width
        height = configuration.height
    }

    override fun addPanel(panel: WebAccordionPanel) {
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
            val content = panel.content.getHtml()
        })
        panel.content.decorate()
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


    override fun getParent(): WebComponent? {
        return parent
    }

    override fun getChildren(): MutableList<WebComponent> {
        return children
    }

    override fun getHtml(): String {
        return "<div id=\"accordion${uid}\" style=\"${if(width != null) "width:$width" else ""};${if(height != null) "height:$height" else ""}\"></div>"

    }

    override fun decorate() {
        jq = jQuery("#accordion${uid}")
        jq.accordion(object{
            val fit = this@EasyUiWebAccordionContainer.fit
        })
        panels.forEach {
            addPanelInternal(it)
        }
        jq!!.accordion("select", selected)
        initialized = true
    }



}