/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.richlet

import com.gridnine.jasmine.web.server.mainframe.ServerUiMainFrame
import com.gridnine.jasmine.web.server.mainframe.ServerUiMainFrameConfiguration
import com.gridnine.jasmine.web.server.zk.components.findZkComponent
import com.gridnine.jasmine.web.server.zk.richlet.pg.PgMainFrame
import org.zkoss.zk.ui.*
import org.zkoss.zul.Div


class JasmineRichlet : GenericRichlet() {

    override fun service(page: Page) {
        page.title = "Jasmine"
        val mainFrame = ServerUiMainFrame(ServerUiMainFrameConfiguration{
            title = "Jasmine"
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