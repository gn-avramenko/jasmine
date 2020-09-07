/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.mainframe

import com.gridnine.jasmine.server.standard.model.domain.BaseWorkspaceItemJS
import com.gridnine.jasmine.server.standard.model.domain.WorkspaceJS
import com.gridnine.jasmine.web.core.ui.WebComponent
import com.gridnine.jasmine.web.core.ui.UiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.components.BaseButtonConfiguration
import com.gridnine.jasmine.web.core.ui.components.WebAccordionContainer
import com.gridnine.jasmine.web.core.ui.components.WebBorderContainer
import com.gridnine.jasmine.web.core.ui.components.WebBorderLayoutRegion
import kotlin.browser.window

class MainFrame(val delegate:WebBorderContainer = UiLibraryAdapter.get().createBorderLayout(null){
    fit=true
}):WebBorderContainer by delegate{

    var westRegionWidth = 200

    var logoText = "Jasmine"

    lateinit var westRegion:WebBorderContainer

    lateinit var centerRegion:WebBorderContainer

    val tools = arrayListOf<BaseButtonConfiguration>()

    fun initialize(workspace: WorkspaceJS) {
        westRegion = UiLibraryAdapter.get().createBorderLayout(this){fit=true}
        val logoLabel = UiLibraryAdapter.get().createLabel(westRegion)
        logoLabel.setWidth("100%")
        logoLabel.addClass("jasmine-logo")
        logoLabel.setText(logoText)
        westRegion.setNorthRegion(WebBorderContainer.region {
            content = logoLabel
        })
        val westAccordion = createWestAccordion(westRegion, workspace)

        westRegion.setCenterRegion(WebBorderContainer.region {
            content = westAccordion
        })
        delegate.setWestRegion(WebBorderContainer.region {
            collapsible = true
            showBorder = false
            showSplitLine = true
            content = westRegion
            width = westRegionWidth
        })

        centerRegion =  UiLibraryAdapter.get().createBorderLayout(this){fit=true}
        centerRegion.setCenterRegion(createTabs())


        delegate.setCenterRegion(WebBorderContainer.region {
            collapsible = true
            showBorder = false
            showSplitLine = true
            content = centerRegion
        })
    }

    private fun createTabs(): WebBorderLayoutRegion {
        return WebBorderContainer.region {
            collapsible = false
            content = UiLibraryAdapter.get().createTabsContainer(centerRegion){
                fit = true
                width = "100%"
                height = "100%"
                tools.addAll(this@MainFrame.tools)
            }
        }
    }

    private fun createWestAccordion(westRegion: WebBorderContainer, workspace: WorkspaceJS): WebComponent {
        val westAccordion = UiLibraryAdapter.get().createAccordionContainer(westRegion){
            fit=false
            width="100%"
        }
        workspace.groups.forEach {group ->
            westAccordion.addPanel(WebAccordionContainer.panel {
                title = group.displayName
                content = run{
                    val result = UiLibraryAdapter.get().createDataList<BaseWorkspaceItemJS>(westAccordion){
                        width = "100%"
                        showLines = true
                        fit = false
                    }
                    result.setValueGetter {
                        it.displayName
                    }
                    result.setSelectionAllowed(false)
                    result.setClickListener {
                        window.alert("you select ${it.displayName}")
                    }
                    result.setData(group.items)
                    result
                }
            })
        }
        westAccordion.select(0)
        return westAccordion
    }
}