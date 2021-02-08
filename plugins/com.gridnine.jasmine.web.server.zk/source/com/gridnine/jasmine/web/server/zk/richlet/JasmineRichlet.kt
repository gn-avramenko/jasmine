/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.richlet

import com.gridnine.jasmine.web.server.zk.richlet.pg.MainFrame
import org.zkoss.zk.ui.*
import org.zkoss.zk.ui.event.Event
import org.zkoss.zk.ui.event.EventListener
import org.zkoss.zk.ui.event.Events
import org.zkoss.zk.ui.impl.PollingServerPush
import org.zkoss.zk.ui.sys.DesktopCtrl
import org.zkoss.zk.ui.sys.WebAppCtrl
import org.zkoss.zk.ui.util.Clients
import org.zkoss.zul.Button
import org.zkoss.zul.Label
import org.zkoss.zul.Window


class JasmineRichlet : GenericRichlet() {

    override fun service(page: Page) {
        page.title = "Jasmine"
        val mainFrame = MainFrame()
        mainFrame.page = page
    }

}