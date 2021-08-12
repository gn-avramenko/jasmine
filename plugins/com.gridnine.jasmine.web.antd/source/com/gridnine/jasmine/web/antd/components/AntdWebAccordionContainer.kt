/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jtasks
 *****************************************************************/
package com.gridnine.jasmine.web.antd.components

import com.gridnine.jasmine.web.core.ui.components.WebAccordionContainer
import com.gridnine.jasmine.web.core.ui.components.WebAccordionContainerConfiguration
import com.gridnine.jasmine.web.core.ui.components.WebAccordionPanel

class AntdWebAccordionContainer(configure:WebAccordionContainerConfiguration.()->Unit) :WebAccordionContainer,BaseAntdWebUiComponent(){

    private val panels = arrayListOf<WebAccordionPanel>()

    private var selectedKey:String? = null

    private val config= WebAccordionContainerConfiguration()

    init {
        config.configure()
    }

    override fun createReactElementWrapper(): ReactElementWrapper {
        return ReactFacade.createProxy{callbackIndex:Int ->
            val props = js("{}")
            props.style = js("{}")
            if(config.fit){
                props.style.width = "100%"
                props.style.height = "100%"
            } else {
                config.width?.let { props.style.width = it }
                config.height?.let { props.style.height = it }
            }
            if(selectedKey != null){
                props.defaultActiveKey = arrayOf(selectedKey)
            } else if(panels.isNotEmpty()){
                props.defaultActiveKey = arrayOf(panels[0].id)
            }
            ReactFacade.callbackRegistry.get(callbackIndex).onChange = { keys:Array<String> ->
                if(keys.isNotEmpty()){
                    selectedKey = keys[0]
                }
            }
            props.onChange = { keys:Array<String> ->
                ReactFacade.callbackRegistry.get(callbackIndex).onChange(keys)
            }
            ReactFacade.createElementWithChildren(ReactFacade.Collapse, props, panels.map {
                val panelProps = js("{}")
                panelProps.header = it.title
                panelProps.key = it.id
                ReactFacade.createElementWithChildren(ReactFacade.Panel, panelProps, findAntdComponent(it.content).getReactElement())
            }.toTypedArray())
        }
    }

    override fun addPanel(configure: WebAccordionPanel.() -> Unit) {
        val panel = WebAccordionPanel()
        panel.configure()
        panels.add(panel)
        maybeRedraw()
    }

    override fun removePanel(id: String) {
        if(panels.removeAll { it.id == id }){
            maybeRedraw()
        }
    }

    override fun select(id: String) {
        if(id != selectedKey){
            selectedKey = id
            maybeRedraw()
        }
    }

    override fun getPanels(): List<WebAccordionPanel> {
        return panels
    }

}