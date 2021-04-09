/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.richlet

import com.gridnine.jasmine.web.server.components.ServerUiTabTool
import com.gridnine.jasmine.web.server.mainframe.ServerUiMainFrame
import com.gridnine.jasmine.web.server.mainframe.ServerUiMainFrameConfiguration
import com.gridnine.jasmine.web.server.mainframe.ServerUiMainFrameTabHandler
import com.gridnine.jasmine.web.server.mainframe.workspaceEditor.ServerUiWorkspaceEditorTabHandler
import com.gridnine.jasmine.web.server.zk.components.findZkComponent
import org.zkoss.zk.ui.*
import org.zkoss.zul.Div


class JasmineRichlet : GenericRichlet() {

    override fun service(page: Page) {
        page.title = "Jasmine"
        val mainFrame = ServerUiMainFrame(ServerUiMainFrameConfiguration{
            title = "Jasmine"
            tools.add(ServerUiTabTool("Редактор рабочей области"){
                ServerUiMainFrame.get().openTab(ServerUiWorkspaceEditorTabHandler(), Unit)
            })
        })
        val comp = findZkComponent(mainFrame).getZkComponent()
        val div = Div()
        div.hflex = "1"
        div.vflex = "1"
        div.appendChild(comp)
        div.attributes["rootComponent"] = mainFrame
        div.page = page
    }

}