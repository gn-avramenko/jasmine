/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.mainframe

import com.gridnine.jasmine.server.core.model.domain.ObjectReferenceJS
import com.gridnine.jasmine.server.standard.model.domain.BaseWorkspaceItemJS
import com.gridnine.jasmine.server.standard.model.domain.ListWorkspaceItemJS
import com.gridnine.jasmine.server.standard.model.domain.WorkspaceJS
import com.gridnine.jasmine.web.core.application.EnvironmentJS
import com.gridnine.jasmine.web.core.ui.WebComponent
import com.gridnine.jasmine.web.core.ui.UiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.components.*
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import kotlin.js.Promise
import kotlin.reflect.KClass

class MainFrame(private val delegate:WebBorderContainer = UiLibraryAdapter.get().createBorderLayout(null){
    fit=true
}):WebBorderContainer by delegate{

    private val configuration = MainFrameConfiguration()

    private val uid = MiscUtilsJS.createUUID()

    private lateinit var  tabsContainer:WebTabsContainer

    init {
        configuration.elementsHandlers[ListWorkspaceItemJS::class] = ListWorkspaceItemHandler()
    }

    fun configure(configurator:MainFrameConfiguration.() ->Unit){
        configuration.configurator()
    }

    fun build(workspace: WorkspaceJS) {
        val westRegion = UiLibraryAdapter.get().createBorderLayout(this){fit=true}
        val logoLabel = UiLibraryAdapter.get().createLabel(westRegion)
        logoLabel.setWidth("100%")
        logoLabel.addClass("jasmine-logo")
        logoLabel.setText(configuration.logoText)
        westRegion.setNorthRegion(WebBorderContainer.region {
            content = logoLabel
        })
        val westAccordion = UiLibraryAdapter.get().createAccordionContainer(westRegion){
            fit=false
            width="100%"
        }
        val centerRegion =  UiLibraryAdapter.get().createBorderLayout(this){fit=true}
        tabsContainer = UiLibraryAdapter.get().createTabsContainer(centerRegion){
            fit = true
            width = "100%"
            height = "100%"
            tools.addAll(configuration.tools)
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
                    result.setClickListener {we ->
                        val handler = configuration.elementsHandlers[we::class]!! as MainFrameTabHandler<BaseWorkspaceItemJS, Any>
                        openTab(handler, we)
                    }
                    result.setData(group.items)
                    result
                }
            })
        }
        westAccordion.select(westAccordion.getPanels()[0].id)
        westRegion.setCenterRegion(WebBorderContainer.region {
            content = westAccordion
        })
        setWestRegion(WebBorderContainer.region {
            collapsible = true
            showBorder = false
            showSplitLine = true
            content = westRegion
            width = configuration.westRegionWidth
        })


        centerRegion.setCenterRegion(WebBorderContainer.region {
            collapsible = false
            content = tabsContainer
        })
        setCenterRegion(WebBorderContainer.region {
            collapsible = true
            showBorder = false
            showSplitLine = true
            content = centerRegion
        })
    }

    fun<T:Any> openTab(handler: MainFrameTabHandler<T, Any>, we: T) {
        val tabId = "$uid|${handler.getTabId(we)}"
        val existingTab = tabsContainer.getTabs().find { it.id == tabId }
        if(existingTab != null){
            tabsContainer.select(existingTab.id)
            return
        }
        handler.loadData(we).then {

            val tabData  = handler.createTabData(we, it, tabsContainer, object:MainFrameTabCallback {
                override fun setTitle(title: String) {
                    TODO("Not yet implemented")
                }

                override fun close() {
                    TODO("Not yet implemented")
                }

            })
            tabsContainer.addTab(WebTabsContainer.tab{
                id = tabId
                title = tabData.title
                content = tabData.content
            })
        }
    }

    fun openTab(ref:ObjectReferenceJS){
        openTab(ObjectEditorTabHandler() as MainFrameTabHandler<ObjectReferenceJS, Any>, ref)
    }
    companion object {
        fun get() = EnvironmentJS.getPublished(MainFrame::class)
    }
}

interface MainFrameTabCallback{
    fun setTitle(title:String)
    fun close()
}

data class MainFrameTabData(var title:String, var content:WebComponent)

interface MainFrameTabHandler<T:Any,D>{
    fun getTabId(obj:T):String
    fun loadData(obj:T):Promise<D>
    fun createTabData(obj:T, data:D, parent:WebComponent, callback: MainFrameTabCallback):MainFrameTabData
}

class MainFrameConfiguration{

    val tools = arrayListOf<BaseButtonConfiguration>()

    var westRegionWidth = 200

    var logoText = "Jasmine"

    val elementsHandlers = hashMapOf<KClass<*>, MainFrameTabHandler<*,*>>()


}

